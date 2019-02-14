package org.embroideryio.embroideryio;

import java.io.IOException;

public class NewReader extends EmbReader {

    public static final String MIME = "application/x-new";
    public static final String EXT = "new";

    private final static int COMMANDSIZE = 3;

    public static void read_new_stitches(EmbReader reader) throws IOException {
        EmbPattern pattern = reader.pattern;
        byte[] b = new byte[COMMANDSIZE];
        while (true) {
            if (reader.readFully(b) != b.length) {
                break;
            }
            int x = b[0] & 0xFF;
            int y = -(b[1] & 0xFF);
            int ctrl = b[2] & 0xFF;

            if ((ctrl & 0x40) != 0) {
                x = -x;
            }
            if ((ctrl & 0x20) != 0) {
                y = -y;
            }
            int control = ctrl & 0b11111;
            switch (control) {
                case 0x00:
                    pattern.stitch(x, y);
                    continue;
                case 0x01:
                    pattern.move(x, y);
                    continue;
                case 0x03:
                case 0x02:
                    pattern.color_change(x, y);
                    continue;
                case 0x11:
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
        skip(0x02); //stitchcount.
        read_new_stitches(this);
    }
}
