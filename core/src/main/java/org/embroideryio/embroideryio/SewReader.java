package org.embroideryio.embroideryio;

import java.io.IOException;

public class SewReader extends EmbReader {

    void read_sew_stitches() throws IOException {
        byte[] b = new byte[2];
        while (true) {
            if (readFully(b) != b.length) break;
            if (((b[0] & 0xFF) != 0x80)) {
                pattern.stitch(b[0], -b[1]);
                continue;
            }
            int control = b[1];
            if (readFully(b) != b.length) break;
            if ((control & 0x01) != 0) {
                pattern.color_change();
                continue;
            }
            if ((control == 0x04) || (control == 0x02)) {
                pattern.move(signed8(b[0]), -signed8(b[1]));
                continue;
            }
            if (control == 0x10) {
                pattern.stitch(signed8(b[0]), -signed8(b[1]));
                continue;
            }
            break;
        }
        pattern.end();
    }

    @Override
    protected void read() throws IOException {
        EmbThreadSew[] threads = EmbThreadSew.getThreadSet();
        int numberOfColors = readInt16LE();
        for (int i = 0; i < numberOfColors; i++) {
            int index = readInt16LE();
            pattern.addThread(threads[index % 79]);
        }
        seek(0x1D78);
        read_sew_stitches();
    }
}
