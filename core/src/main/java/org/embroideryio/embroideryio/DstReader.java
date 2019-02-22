package org.embroideryio.embroideryio;

import java.io.IOException;

public class DstReader extends EmbReader {

    public static final String MIME = "application/x-dst";
    public static final String EXT = "dst";

    private static final int PPMM = 10;
    private static final int MAXPJ = 121; //121 tenth millimeters is max move for a single move command, positive

    private final static int DSTHEADERSIZE = 512;
    private final static int COMMANDSIZE = 3;

    public static void process_header_info(EmbPattern pattern, String prefix, String value) {
        switch (prefix) {
            case "LA":
                pattern.setMetadata("name", value);
                break;
            case "AU":
                pattern.setMetadata("author", value);
                break;
            case "CP":
                pattern.setMetadata("copyright", value);
                break;
            case "TC":
                String[] tc_split = value.split(",");
                EmbThread thread = new EmbThread();
                try {
                    thread.setStringColor(tc_split[0]);
                    thread.setDescription(tc_split[1]);
                    thread.setDescription(tc_split[2]);
                    pattern.addThread(thread);
                } catch (IndexOutOfBoundsException ignored) {
                }
        }
    }

    public static void parse_datastitch_header(EmbPattern pattern, byte[] b) {
        String bytestring = new String(b);
        String[] split = bytestring.split("[\n\r]");
        for (String s : split) {
            if (s == null) {
                continue;
            }
            if (s.length() <= 3) {
                continue;
            }
            process_header_info(pattern, s.substring(0, 2), s.substring(3).trim());
        }
    }

    public static void dst_read_header(EmbReader reader) throws IOException {
        byte[] b = new byte[DSTHEADERSIZE];
        reader.readFully(b);
        parse_datastitch_header(reader.pattern, b);
    }

    private static int getbit(byte b, int pos) {
        int bit;
        bit = (b >> pos) & 1;
        return (bit);
    }

    private static int decodedx(byte b0, byte b1, byte b2) {
        int x = 0;
        x += getbit(b2, 2) * (+81);
        x += getbit(b2, 3) * (-81);
        x += getbit(b1, 2) * (+27);
        x += getbit(b1, 3) * (-27);
        x += getbit(b0, 2) * (+9);
        x += getbit(b0, 3) * (-9);
        x += getbit(b1, 0) * (+3);
        x += getbit(b1, 1) * (-3);
        x += getbit(b0, 0) * (+1);
        x += getbit(b0, 1) * (-1);
        return x;
    }

    private static int decodedy(byte b0, byte b1, byte b2) {
        int y = 0;
        y += getbit(b2, 5) * (+81);
        y += getbit(b2, 4) * (-81);
        y += getbit(b1, 5) * (+27);
        y += getbit(b1, 4) * (-27);
        y += getbit(b0, 5) * (+9);
        y += getbit(b0, 4) * (-9);
        y += getbit(b1, 7) * (+3);
        y += getbit(b1, 6) * (-3);
        y += getbit(b0, 7) * (+1);
        y += getbit(b0, 6) * (-1);
        return -y;
    }

    public static void dst_read_stitches(EmbReader reader) throws IOException {
        EmbPattern pattern = reader.pattern;
        boolean sequin_mode = false;
        byte[] command = new byte[COMMANDSIZE];
        while (true) {
            if (Thread.interrupted()) {
                return;
            }
            if (reader.readFully(command) != command.length) {
                break;
            }
            int dx = decodedx(command[0], command[1], command[2]);
            int dy = decodedy(command[0], command[1], command[2]);
            if ((command[2] & 0b11110011) == 0b11110011) {
                pattern.end(dx, dy);
            } else if ((command[2] & 0b11000011) == 0b11000011) {
                pattern.color_change(dx, dy);
            } else if ((command[2] & 0b01000011) == 0b01000011) {
                pattern.sequin_mode(dx, dy);
                sequin_mode = !sequin_mode;
            } else if ((command[2] & 0b10000011) == 0b10000011) {
                if (sequin_mode) {
                    pattern.sequin_eject(dx, dy);
                } else {
                    pattern.move(dx, dy);
                }
            } else {
                pattern.stitch(dx, dy);
            }
        }

    }

    @Override
    public void read() throws IOException {
        dst_read_header(this);
        dst_read_stitches(this);
    }
}
