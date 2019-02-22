package org.embroideryio.embroideryio;

import java.io.IOException;

public class A100Reader extends EmbReader {

    private final static int COMMANDSIZE = 4;

    public void read_100_stitches() throws IOException {
        byte[] b = new byte[COMMANDSIZE];
        while (true) {
            if (readFully(b) != b.length) {
                break;
            }
            int x = b[2] & 0xFF;
            int y = b[3] & 0xFF;
            if (x > 0x80) {
                x -= 0x80;
                x = -x;
            }
            if (y > 0x80) {
                y -= 0x80;
                y = -y;
            }
            if (b[0] == 0x61) {
                pattern.stitch(x, -y);
                continue;
            } else if ((b[0] & 0x01) != 0) {
                pattern.move(x, -y);
                continue;
            } else {
                pattern.color_change();
                continue;
            } //this catch is too broad.
            //break;
        }
        pattern.end();
    }

    @Override
    public void read() throws IOException {
        read_100_stitches();
    }
}
