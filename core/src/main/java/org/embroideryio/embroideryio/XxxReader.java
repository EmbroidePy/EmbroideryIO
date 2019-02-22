package org.embroideryio.embroideryio;

import java.io.IOException;

public class XxxReader extends EmbReader {

    @Override
    protected void read() throws IOException {
        skip(0x27);
        int num_of_colors = readInt16LE();
        seek(0x100);

        while (true) {
            int b1 = readInt8();
            if ((b1 == 0x7D) || (b1 == 0x7E)) {
                int x = signed16(readInt16LE());
                int y = signed16(readInt16LE());
                pattern.move(x, -y);
                continue;
            }
            int b2 = readInt8();
            if (b1 != 0x7F) {
                pattern.stitch(signed8(b1), -signed8(b2));
                continue;
            }
            int b3 = readInt8();
            int b4 = readInt8();
            switch (b2) {
                case 0x01:
                    pattern.move(signed8(b3), -signed8(b4));
                    continue;
                case 0x03:
                    int x = signed8(b3);
                    int y = -signed8(b4);
                    if ((x != 0) || (y != 0)) pattern.move(x, y);
                    pattern.trim();
                    continue;
                case 0x08:
                    pattern.color_change();
                    continue;
                case 0x7F:
                    break;
            }
            break;
        }
        pattern.end();
        skip(2);
        for (int i = 0, ie = num_of_colors; i < ie; i++) {
            EmbThread thread = new EmbThread();
            thread.color = readInt32BE();
            pattern.add(thread);
        }
    }
}
