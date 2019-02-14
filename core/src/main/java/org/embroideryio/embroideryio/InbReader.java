package org.embroideryio.embroideryio;

import java.io.IOException;

public class InbReader extends EmbReader {

    public static final String MIME = "application/x-inb";
    public static final String EXT = "inb";

    private final static int COMMANDSIZE = 3;

    public static void read_inb_stitches(EmbReader reader) throws IOException {
        EmbPattern pattern = reader.pattern;
        byte[] b = new byte[COMMANDSIZE];
        while (true) {
            if (reader.readFully(b) != b.length) {
                break;
            }
            int dx = b[0] & 0xFF;
            int dy = -(b[1] & 0xFF);
            int ctrl = b[2] & 0xFF;

            if ((ctrl & 0x20) != 0) {
                dy = -dy;
            }
            if ((ctrl & 0x40) != 0) {
                dx = -dx;
            }
            int control = ctrl & 0b1111;
            switch (control) {
                case 0x00:
                    pattern.stitch(dx, dy);
                    continue;
                case 0x01:
                    pattern.color_change(dx, dy);
                    continue;
                case 0x02:
                    pattern.move(dx, dy);
                    continue;
                case 0x04:
                    break;
                default:
                    break;
            }
            break;
        }
        pattern.end();
    }

    @Override
    public void read() throws IOException {
        skip(0x2000);
        read_inb_stitches(this);
    }
}
