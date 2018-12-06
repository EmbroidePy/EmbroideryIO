package org.embroideryio.embroideryio;

import java.io.IOException;

public class PcqReader extends EmbReader {

    private static final float PC_SIZE_CONVERSION_RATIO = 5f / 3f;

    void read_pc_file() throws IOException {
        int version = readInt8();
        int hoop_size = readInt8();
        //# 0 for PCD,
        //# 1 for PCQ (MAXI),
        //# 2 for PCS small hoop(80x80),
        //# 3 for PCS with large hoop.
        int color_count = readInt16LE();
        for (int i = 0; i < color_count; i++) {
            int color = readInt24BE();
            EmbThread t = new EmbThread(color, EmbThread.getHexColor(color), "" + i);
            pattern.addThread(t);
            skip(1);
        }

        int stitch_count = readInt16LE();
        while (true) {
            int c0 = readInt8();
            int x = signed24(readInt24LE());
            int c1 = readInt8();
            int y = signed24(readInt24LE());
            int ctrl = readInt8();
            if (ctrl == Integer.MIN_VALUE) {
                break;
            }
            x *= PC_SIZE_CONVERSION_RATIO;
            y *= -PC_SIZE_CONVERSION_RATIO;

            if (ctrl == 0x00) {
                pattern.stitchAbs(x, y);
                continue;
            }
            if ((ctrl & 0x01) != 0) {
                pattern.color_change();
                continue;
            }
            if ((ctrl & 0x04) != 0) {
                pattern.moveAbs(x, y);
                continue;
            }
            if (ctrl == 0xFF) {
                //This isn't part of the format, it's a consequence of
                // how read will fail when out of file.
                break;
            }
            break;
        }
        pattern.end();
    }

    @Override
    protected void read() throws IOException {
        read_pc_file();
    }

}
