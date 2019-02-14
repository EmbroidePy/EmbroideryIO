package org.embroideryio.embroideryio;

import java.io.IOException;

public class JpxReader extends EmbReader {

    private final static int COMMANDSIZE = 2;

    public static void read_jpx_stitches(EmbReader reader) throws IOException {
        EmbPattern pattern = reader.pattern;
        byte[] b = new byte[COMMANDSIZE];
        while (true) {
            if (reader.readFully(b) != b.length) {
                break;
            }
            if ((b[0] & 0xFF) != 0x80) {
                float x = (float) b[0];
                float y = -(float) b[1];
                pattern.stitch(x, y);
                continue;
            }
            int control = b[1] & 0xFF;
            if (reader.readFully(b) != b.length) {
                break;
            }
            int x = b[0];
            int y = -b[1];
            switch (control) {
                case 0x01:
                    pattern.color_change();
                    if ((x != 0) && (y != 0)) {
                        pattern.move(x, y);
                    }
                    continue;
                case 0x02:
                    pattern.move(x, y);
                    continue;
                case 0x10:
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
        int stitch_start_position = readInt32LE();
        skip(0x1C);
        int colors = readInt32LE();
        skip(0x18);
        for (int i = 0, ie = colors; i < ie; i++) {
            int color_index = readInt32LE();
            EmbThread thread = new EmbThread();
            thread.color = (int) (Math.random() * 0xFFFFFF);
            thread.description = "JPX index " + color_index;
        }
        seek(stitch_start_position);
        read_jpx_stitches(this);
    }
}
