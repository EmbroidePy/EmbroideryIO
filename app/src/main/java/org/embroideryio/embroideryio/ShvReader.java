package org.embroideryio.embroideryio;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.embroideryio.embroideryio.EmbConstant.*;

public class ShvReader extends EmbReader {

    @Override
    protected void read() throws IOException {
        int color_count;
        boolean in_jump = false;
        skip(0x56);
        int fileNameLength = readInt8();
        skip(fileNameLength);
        int designWidth = readInt8();
        int designHeight = readInt8();
        int skip_image = (int) Math.ceil(designHeight / 2.0) * designWidth;
        skip(4 + skip_image);
        color_count = readInt8();
        skip(18);
        Map<Integer, Integer> stitchesPerColor = new HashMap<>();
        EmbThreadShv[] threadSet = EmbThreadShv.getThreadSet();
        for (int i = 0, ie = color_count - 1; i <= ie; i++) {
            int colorNumber;
            int stitchCount;
            stitchCount = readInt32BE();
            colorNumber = readInt8();
            pattern.addThread(threadSet[colorNumber % threadSet.length]);
            stitchesPerColor.put(i, stitchCount);
            //skip(9);
            if (i == ie) {
                skip(7);
            } else {
                skip(9);
            }
        }
        int stitches_since_stop = 0;
        int current_color_index = 0;
        Integer max_stitches = stitchesPerColor.get(current_color_index);
        if (max_stitches == null) {
            max_stitches = 0;
        }
        byte[] b = new byte[2];
        while (true) {
            int b0, b1;
            int flags = STITCH;
            if (in_jump) {
                flags = JUMP;
            }
            if (readFully(b) != b.length) break;
            b0 = b[0] & 0xFF;
            b1 = b[1] & 0xFF;
            if (stitches_since_stop >= max_stitches) {
                pattern.color_change();
                stitches_since_stop = 0;
                current_color_index += 1;
                max_stitches = stitchesPerColor.get(current_color_index);
                if (max_stitches == null) {
                    max_stitches = Integer.MAX_VALUE;
                }
            }
            if (b0 == 0x80) {
                stitches_since_stop += 1;
                switch (b1) {
                    case 0x03:
                        continue;
                    case 0x02:
                        in_jump = false;
                        continue;
                    case 0x01:
                        int sx,
                         sy;
                        stitches_since_stop += 2;
                        sx = readInt16BE();
                        sy = readInt16BE();
                        in_jump = true;
                        pattern.move(signed16(sx), signed16(sy));
                        continue;
                    default:
                        break;
                }
            }
            int dx = signed8(b0);
            int dy = signed8(b1);
            stitches_since_stop++;
            pattern.addStitchRel(dx, dy, flags);
        }
        pattern.end();
    }

}
