package org.embroideryio.embroideryio;

import java.io.IOException;

public class DszReader extends EmbReader {

    public static final String MIME = "application/x-dsz";
    public static final String EXT = "dsz";

    private final static int COMMANDSIZE = 3;

    public static void z_stitch_encoding_read(EmbReader reader) throws IOException {
        EmbPattern pattern = reader.pattern;
        byte[] b = new byte[COMMANDSIZE];
        while (true) {
            if (reader.readFully(b) != b.length) {
                break;
            }
            int dy = -(b[0] & 0xFF);
            int dx = b[1] & 0xFF;
            int ctrl = b[2] & 0xFF;

            if ((ctrl & 0x40) != 0) {
                dx = -dx;
            }
            if ((ctrl & 0x20) != 0) {
                dy = -dy;
            }
            int control = ctrl & 0b11111;
            switch (control) {
                case 0x00:
                    pattern.stitch(dx, dy);
                    continue;
                case 0x01:
                    pattern.move(dx, dy);
                    continue;
            }
            switch (ctrl) {
                case 0x82:
                    pattern.stop();
                    continue;
                case 0x9B:
                    pattern.trim();
                    continue;
                case 0x83:
                case 0x84:
                case 0x85:
                case 0x86:
                case 0x87:
                case 0x88:
                case 0x89:
                case 0x8A:
                case 0x8B:
                case 0x8C:
                case 0x8D:
                case 0x8E:
                case 0x8F:
                case 0x90:
                case 0x91:
                case 0x92:
                case 0x93:
                case 0x94:
                case 0x95:
                case 0x96:
                case 0x97:
                case 0x98:
                case 0x99:
                case 0x9A:
                    int needle = (ctrl - 0x83) >> 1;
                    pattern.needle_change(needle);
                    continue;
                default:
                    break;
            }
            break;
        }
        pattern.end();
    }

    @Override
    public void read() throws IOException {
        DstReader.dst_read_header(this);
        z_stitch_encoding_read(this);
    }
}
