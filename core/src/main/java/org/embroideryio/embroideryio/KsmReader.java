package org.embroideryio.embroideryio;

import java.io.IOException;

public class KsmReader extends EmbReader {

    private final static int COMMANDSIZE = 3;

    public void read_ksm_stitches() throws IOException {
        boolean trimmed = false;
        boolean stitched_yet = false;

        byte[] b = new byte[COMMANDSIZE];
        while (true) {
            if (readFully(b) != b.length) {
                break;
            }
            int y = -(b[0] & 0xFF);
            int x = (b[1] & 0xFF);
            int ctrl = (b[2] & 0xFF);
            if ((ctrl & 0x40) != 0) {
                x = -x;
            }
            if ((ctrl & 0x20) != 0) {
                y = -y;
            }
            int control = ctrl & 0b11111;
            if ((x != 0) || (y != 0)) {
                if (trimmed) {
                    pattern.move(x, y);
                } else {
                    pattern.stitch(x, y);
                    stitched_yet = true;
                }
            }
            switch (control) {
                case 0x00:
                    continue;
                case 0x07:
                case 0x13:
                case 0x1D:
                    if (stitched_yet) {
                        pattern.trim();
                    }
                    trimmed = true;
                    continue;
                case 0x17:
                case 0x18:
                case 0x19:
                    trimmed = false;
                    continue;
                case 0x0B:
                case 0x0C:
                case 0x0D:
                case 0x0E:
                case 0x0F:
                case 0x10:
                case 0x11:
                case 0x12:
                    int needle = control - 0x0A;
                    pattern.needle_change(needle);
                    trimmed = true;
                    continue;
                case 0x05:
                    pattern.stop();
                    continue;
                case 0x1B: // called before end command.
                    trimmed = false;
                    continue;
                case 0x08: //0x88 end zero direction.
                    break;
                default:
                    break;
            }
            break;
        }
        pattern.end();
    }

    @Override
    public void read() throws IOException {
        skip(0x200);
        read_ksm_stitches();
    }
}
