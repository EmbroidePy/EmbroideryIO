package org.embroideryio.embroideryio;

import java.io.IOException;

public class ZxyReader extends EmbReader {

    private final static int COMMANDSIZE = 3;

    public static void read_zxy_stitches(EmbReader reader) throws IOException {
        EmbPattern pattern = reader.pattern;
        byte[] b = new byte[COMMANDSIZE];
        while (true) {
            if (reader.readFully(b) != b.length) {
                break;
            }
            int ctrl = b[0] & 0xFF;
            int x = b[1];
            int y = -b[2];
            if ((ctrl & 0x08) != 0) {
                x = -x;
            }
            if ((ctrl & 0x04) != 0) {
                y = -y;
            }
            ctrl &= ~0x0C;
            if (ctrl == 0) {
                pattern.stitch(x, y);
                continue;
            }
            if ((ctrl & 0x02) != 0) {
                pattern.move(x, y);
                continue;
            }
            if ((ctrl & 0x20) != 0) {
                if (b[1] == 0xFF) {
                    break;
                }
                int needle = b[2];
                pattern.needle_change(needle);
                continue;
            }
            break;
        }
        pattern.end();
    }

    @Override
    public void read() throws IOException {
        seek(0x01);
        int stitch_start_distance = readInt16BE();
        skip(stitch_start_distance);
        read_zxy_stitches(this);
    }
}
