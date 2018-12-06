package org.embroideryio.embroideryio;

import java.io.IOException;

public class EmdReader extends EmbReader {

    private final static int COMMANDSIZE = 2;

    public void read_emd_stitches() throws IOException {
        byte[] b = new byte[COMMANDSIZE];
        while (true) {
            if (readFully(b) != b.length) {
                break;
            }
            if ((b[0] & 0xFF) != 0x80) {
                float x = (float) b[0];
                float y = -(float) b[1];
                pattern.stitch(x, y);
                continue;
            }
            int control = b[1] & 0xFF;
            switch (control) {
                case 0x80:
                    if (readFully(b) != b.length) {
                        break;
                    }
                    int x = b[0];
                    int y = -b[1];
                    pattern.move(x, y);
                    continue;
                case 0x2A:
                    pattern.color_change();
                    continue;
                case 0x7D:
                    //Dunno, occurs at position 0.
                    continue;
                case 0xAD:
                    pattern.trim(); //Final command before returning to start.
                    continue;
                case 0xFD:
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
        skip(0x30);
        read_emd_stitches();
    }
}
