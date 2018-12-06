package org.embroideryio.embroideryio;

import java.io.IOException;

public class A10oReader extends EmbReader {

    private final static int COMMANDSIZE = 3;

    public void read_10o_stitches() throws IOException {
        byte[] b = new byte[COMMANDSIZE];
        while (true) {
            if (readFully(b) != b.length) {
                break;
            }
            int ctrl = b[0] & 0xFF;
            int y = -(b[1] & 0xFF);
            int x = b[2] & 0xFF;
            if ((ctrl & 0x20) != 0) {
                x = -x;
            }
            if ((ctrl & 0x40) != 0) {
                y = -y;
            }
            if ((ctrl & 0b11111) == 0) {
                pattern.stitch(x, y);
                continue;
            }
            if ((ctrl & 0xb11111) == 0x10) {
                pattern.move(x, y);
                continue;
            }
            switch (ctrl) {
                case 0x8A:
                    //Start
                    continue;
                case 0x85:
                    pattern.color_change();
                    continue;
                case 0x82:
                    pattern.stop();
                    continue;
                case 0x81:
                    pattern.trim();
                    continue;
                case 0x87:
                    break;
            }
            break;
        }
        pattern.end();
    }

    @Override
    public void read() throws IOException {
        read_10o_stitches();
    }
}
