package org.embroideryio.embroideryio;

import java.io.IOException;

public class MitReader extends EmbReader {

    private final static int COMMANDSIZE = 2;
    private final static double MIT_SIZE_CONVERSION_RATIO = 5.0 / 2.0;

    @Override
    public void read() throws IOException {
        int previous_ctrl = -1;
        byte[] b = new byte[COMMANDSIZE];
        while (true) {
            if (readFully(b) != b.length) {
                break;
            }
            float x = (b[0] & 0x1F);
            float y = (b[1] & 0x1F);
            x *= MIT_SIZE_CONVERSION_RATIO;
            y *= -MIT_SIZE_CONVERSION_RATIO;
            if ((b[0] & 0b10000000) != 0) {
                x = -x;
            }
            if ((b[1] & 0b10000000) != 0) {
                y = -y;
            }
            int ctrl = ((b[0] & 0x60) >> 3) | ((b[1] & 0x60) >> 5);
            switch (ctrl) {
                case 0b1100:
                    pattern.move(x, y);
                    previous_ctrl = ctrl;
                    continue;
                case 0b1000:
                    if (previous_ctrl == 0b0111) {
                        pattern.color_change();
                    }
                    previous_ctrl = ctrl;
                    continue;
                case 0b0111:
                case 0b0100:
                case 0b0101:
                    pattern.stitch(x, y);
                    previous_ctrl = ctrl;
                    continue;
                case 0b0000:
                    break; //end;
                default:
                    break;
            }
            break;
        }
        pattern.end();
    }
}
