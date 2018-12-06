package org.embroideryio.embroideryio;

import java.io.IOException;

public class TbfReader extends EmbReader {

    private final static int COMMANDSIZE = 3;

    public void read_tbf_stitches() throws IOException {
        byte[] b = new byte[COMMANDSIZE];
        int count = 0;
        while (true) {
            count += 1;
            if (readFully(b) != b.length) {
                break;
            }
            int x = b[0];
            int y = -b[1];
            int ctrl = b[2] & 0xFF;
            switch (ctrl) {
                case 0x80:
                    pattern.stitch(x, y);
                    continue;
                case 0x81:
                    if (count > 1) { //this may be needle change.
                        pattern.color_change();
                    }
                    continue;
                case 0x90:
                    if ((x == 0) && (y == 0)) {
                        pattern.trim();
                    } else {
                        pattern.move(x, y);
                    }
                    continue;
                case 0x40:
                    pattern.stop();
                    continue;
                case 0x86:
                    pattern.trim();
                    continue;
                case 0x8F:
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
        seek(0x20E);
        while (true) {
            if (readInt8() == 0x45) {
                EmbThread thread = new EmbThread();
                thread.color = readInt24BE();
                readInt8();
                pattern.add(thread);
            } else {
                break;
            }
        }
        seek(0x600);
        read_tbf_stitches();
    }

}
