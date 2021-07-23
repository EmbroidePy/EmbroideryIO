package org.embroideryio.embroideryio;

import java.io.IOException;


public class JefReader extends EmbReader {

    public void read_jef_stitches() throws IOException {
        int color_index = 1;
        byte[] b = new byte[2];
        while (true) {
            if (readFully(b) != b.length) break;
            if (((b[0] & 0xFF) != 0x80)) {
                float x = b[0];
                float y = -b[1];
                pattern.stitch(x, y);
                continue;
            }
            int ctrl = b[1];
            if (readFully(b) != b.length) break;
            float x = b[0];
            float y = -b[1];
            switch (ctrl) {
                case 0x02:
                    if ((x == 0) && (y == 0)) {
                        pattern.trim();
                    }
                    else {
                        pattern.move(x, y);
                    }
                    continue;
                case 0x01:
                    
                    if (pattern.threadlist.get(color_index) == null) {
                        pattern.stop();
                        pattern.threadlist.remove(color_index);
                    }
                    else {
                        pattern.color_change();
                        color_index += 1;
                    }
                    continue;
                case 0x10:
                    break;
            }
            break;
        }
        pattern.end();
    }

    @Override
    public void read() throws IOException {
        EmbThreadJef[] jefThread = EmbThreadJef.getThreadSet();
        int stitch_offset = readInt32LE();
        skip(20);
        int count_colors = readInt32LE();
        skip(88);
        for (int i = 0; i < count_colors; i++) {
            int index = Math.abs(readInt32LE());
            if (index == 0) {
                // If we have color 0. Go ahead and set that to None.
                pattern.threadlist.add(null);
            }
            else {
                pattern.add(jefThread[index % jefThread.length]);
            }
        }
        seek(stitch_offset);
        read_jef_stitches();
    }
}
