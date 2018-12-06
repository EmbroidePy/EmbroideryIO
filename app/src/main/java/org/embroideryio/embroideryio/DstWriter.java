package org.embroideryio.embroideryio;

import java.io.IOException;
import java.util.Locale;

import static org.embroideryio.embroideryio.EmbConstant.*;

public class DstWriter extends EmbWriter {

    public static final String PROP_EXTENDED_HEADER = "extended_header";
    public static final String MIME = "application/x-dst";
    public static final String EXT = "dst";

    private static final int PPMM = 10;
    private static final int MAXPJ = 121; //121 tenth millimeters is max move for a single move command, positive

    private final static int DSTHEADERSIZE = 512;
    private final static int COMMANDSIZE = 3;

    public DstWriter() {
        super();
        settings.put(EmbEncoder.PROP_MAX_JUMP, (float) MAXPJ);
        settings.put(EmbEncoder.PROP_MAX_STITCH, (float) MAXPJ);
        settings.put(EmbEncoder.PROP_ROUND, true);
        settings.put(EmbEncoder.PROP_SEQUIN_CONTINGENCY, CONTINGENCY_SEQUIN_UTILIZE);
    }

    private int bit(int b) {
        return 1 << b;
    }

    private void encodeRecord(byte[] command, int x, int y, int flags) {
        y = -y;
        byte b0 = 0;
        byte b1 = 0;
        byte b2 = 0;
        switch (flags) {
            case JUMP:
            case SEQUIN_EJECT:
                b2 += bit(7); //jumpstitch 10xxxx11
            //bit7 is the difference between move and the stitch encode.
            //fallthrough.
            case STITCH:
                b2 += bit(0);
                b2 += bit(1);
                if (x > 40) {
                    b2 += bit(2);
                    x -= 81;
                }
                if (x < -40) {
                    b2 += bit(3);
                    x += 81;
                }
                if (x > 13) {
                    b1 += bit(2);
                    x -= 27;
                }
                if (x < -13) {
                    b1 += bit(3);
                    x += 27;
                }
                if (x > 4) {
                    b0 += bit(2);
                    x -= 9;
                }
                if (x < -4) {
                    b0 += bit(3);
                    x += 9;
                }
                if (x > 1) {
                    b1 += bit(0);
                    x -= 3;
                }
                if (x < -1) {
                    b1 += bit(1);
                    x += 3;
                }
                if (x > 0) {
                    b0 += bit(0);
                    x -= 1;
                }
                if (x < 0) {
                    b0 += bit(1);
                    x += 1;
                }
                if (x != 0) {
                    //ANDROID: Log.e("Error", "Write exceeded possible distance.");
                }
                if (y > 40) {
                    b2 += bit(5);
                    y -= 81;
                }
                if (y < -40) {
                    b2 += bit(4);
                    y += 81;
                }
                if (y > 13) {
                    b1 += bit(5);
                    y -= 27;
                }
                if (y < -13) {
                    b1 += bit(4);
                    y += 27;
                }
                if (y > 4) {
                    b0 += bit(5);
                    y -= 9;
                }
                if (y < -4) {
                    b0 += bit(4);
                    y += 9;
                }
                if (y > 1) {
                    b1 += bit(7);
                    y -= 3;
                }
                if (y < -1) {
                    b1 += bit(6);
                    y += 3;
                }
                if (y > 0) {
                    b0 += bit(7);
                    y -= 1;
                }
                if (y < 0) {
                    b0 += bit(6);
                    y += 1;
                }
                if (y != 0) {
                    //ANDROID: Log.e("Error", "Write exceeded possible distance.");
                }
                break;
            case COLOR_CHANGE:
                b2 = (byte) 0b11000011;
                break;
            case STOP:
                b2 = (byte) 0b11110011;
                break;
            case END:
                b2 = (byte) 0b11110011;
                break;
            case SEQUIN_MODE:
                b2 = 0b01000011;
                break;
        }
        command[0] = b0;
        command[1] = b1;
        command[2] = b2;
    }

    @Override
    public void write() throws IOException {
        boolean extended_header = getBoolean(PROP_EXTENDED_HEADER, false);
        float[] bounds = pattern.getBounds();

        String name = getName();
        if (name == null) {
            name = "Untitled";
        }
        if (name.length() > 8) {
            name = name.substring(0, 8);
        }

        int colorchanges = getColorChanges();
        int pointsize = pattern.getStitches().size();
        stream.write(String.format("LA:%-16s\r", name).getBytes());
        stream.write(String.format(Locale.ENGLISH, "ST:%7d\r", pointsize).getBytes());
        stream.write(String.format(Locale.ENGLISH, "CO:%3d\r", colorchanges).getBytes());
        /* number of color changes, not number of colors! */
        stream.write(String.format(Locale.ENGLISH, "+X:%5d\r", (int) Math.abs(bounds[2])).getBytes());
        stream.write(String.format(Locale.ENGLISH, "-X:%5d\r", (int) Math.abs(bounds[0])).getBytes());
        stream.write(String.format(Locale.ENGLISH, "+Y:%5d\r", (int) Math.abs(bounds[3])).getBytes());
        stream.write(String.format(Locale.ENGLISH, "-Y:%5d\r", (int) Math.abs(bounds[1])).getBytes());
        int ax = 0;
        int ay = 0;
        if (pattern.size() > 0) {
            int last = pattern.size() - 1;
            ax = (int) (pattern.getX(last));
            ay = -(int) (pattern.getY(last));
        }
        if (ax >= 0) {
            stream.write(String.format(Locale.ENGLISH, "AX:+%5d\r", ax).getBytes());
        } else {
            stream.write(String.format(Locale.ENGLISH, "AX:-%5d\r", Math.abs(ax)).getBytes());
        }
        if (ay >= 0) {
            stream.write(String.format(Locale.ENGLISH, "AY:+%5d\r", ay).getBytes());
        } else {
            stream.write(String.format(Locale.ENGLISH, "AY:-%5d\r", Math.abs(ay)).getBytes());
        }
        stream.write(String.format(Locale.ENGLISH, "MX:+%5d\r", 0).getBytes());
        stream.write(String.format(Locale.ENGLISH, "MY:+%5d\r", 0).getBytes());
        stream.write(String.format(Locale.ENGLISH, "PD:%6s\r", "******").getBytes());
        if (extended_header) {
            String author = pattern.getAuthor();
            if (author != null) {
                stream.write(String.format(Locale.ENGLISH, "AU:%s\r", author).getBytes());
            }
            String copyright = pattern.getMetadata("copyright");
            if (copyright != null) {
                stream.write(String.format(Locale.ENGLISH, "CP:%s\r", copyright).getBytes());
            }
            if (!pattern.threadlist.isEmpty()) {
                for (EmbThread thread : pattern.threadlist) {
                    stream.write(String.format(Locale.ENGLISH, "TC:%s,%s,%s\r", thread.getHexColor(), thread.description, thread.catalogNumber).getBytes());
                }
            }
        }
        stream.write(0x1A);
        for (int i = 125; i < DSTHEADERSIZE; i++) {
            stream.write(' ');
        }
        byte[] command = new byte[COMMANDSIZE];

        Points stitches = pattern.getStitches();
        double xx = 0, yy = 0;
        for (int i = 0, ie = stitches.size(); i < ie; i++) {
            int data = stitches.getData(i) & COMMAND_MASK;
            float x = stitches.getX(i);
            float y = stitches.getY(i);
            int dx = (int) Math.rint(x - xx);
            int dy = (int) Math.rint(y - yy);
            xx += dx;
            yy += dy;
            switch (data) {
                case TRIM:
                    encodeRecord(command, 2, 2, JUMP);
                    stream.write(command);
                    encodeRecord(command, -4, -4, JUMP);
                    stream.write(command);
                    encodeRecord(command, 2, 2, JUMP);
                    stream.write(command);
                    break;
                default:
                    encodeRecord(command, dx, dy, data);
                    stream.write(command);
                    break;
            }
        }
    }
}
