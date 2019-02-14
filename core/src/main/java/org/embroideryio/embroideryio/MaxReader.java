package org.embroideryio.embroideryio;

import java.io.IOException;

public class MaxReader extends EmbReader {

    public static float MAX_SIZE_CONVERSION_RATIO = 1.235f;
    
    @Override
    public void read() throws IOException {
        skip(0xD5);
        int stitch_count = readInt32LE();
        for (int i = 0; i < stitch_count; i++) {
            int x = readInt24LE();
            int c0 = readInt8();
            int y = readInt24LE();
            int c1 = readInt8();
            x = signed24(x);
            y = signed24(y);
            x *= MAX_SIZE_CONVERSION_RATIO;
            y *= MAX_SIZE_CONVERSION_RATIO;
            pattern.stitchAbs(x, y);
        }
        pattern.end();
    }
}
