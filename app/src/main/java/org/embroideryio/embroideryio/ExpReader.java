package org.embroideryio.embroideryio;

import java.io.IOException;

public class ExpReader extends EmbReader {

    public static void read_exp_stitches(EmbReader reader) throws IOException {
        EmbPattern pattern = reader.pattern;
        byte[] b = new byte[2];
        while (true) {
            if (reader.readFully(b) != b.length) break;

            if ((b[0] & 0xFF) != 0x80) {
                float x = (float) b[0];
                float y = -(float) b[1];
                pattern.stitch(x, y);
                continue;
            }
            int control = b[1] & 0xFF;
            if (reader.readFully(b) != b.length) break;
            float x = (float) b[0];
            float y = -(float) b[1];

            switch (control) {
                case 0x80: //trim
                    pattern.trim();
                    continue;
                case 0x02:
                    pattern.stitch(x, y);
                    continue;
                case 0x04:
                    pattern.move(x, y);
                    continue;
                case 0x01:
                    pattern.color_change();
                    if ((x != 0) || (y != 0)) {
                        pattern.move(x, y);
                    }
                    continue;
            }
            break;
        }
        pattern.end();
    }


    @Override
    public void read() throws IOException {
        read_exp_stitches(this);
    }
}
