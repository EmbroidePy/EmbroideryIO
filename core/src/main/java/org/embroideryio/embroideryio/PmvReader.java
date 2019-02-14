package org.embroideryio.embroideryio;

import java.io.IOException;

public class PmvReader extends EmbReader {

    public void read_pmv_stitches(EmbReader reader) throws IOException {
        int px = 0;
        while (true) {
            int stitch_count = readInt16LE();
            int block_length = readInt16LE();
            if (block_length >= 256) {
                break;
            }
            if (stitch_count == 0) {
                continue;
            }
            for (int i = 0; i < stitch_count; i++) {
                int x = readInt8();
                int y = readInt8();
                if (y > 16) {
                    y = -(32 - y); //5 bit signed number.
                }
                if (x > 32) {
                    x = -(64 - x); // 6 bit signed number.
                }
                x *= 2.5;
                y *= 2.5;
                int dx = x;
                pattern.stitchAbs(px + x, y);
                px += dx;
            }
        }
        pattern.end();
    }

    @Override
    public void read() throws IOException {
        skip(0x64);
        read_pmv_stitches(this);
    }
}
