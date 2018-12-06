package org.embroideryio.embroideryio;

import java.io.IOException;

public class StcReader extends EmbReader {

    private final static int COMMANDSIZE = 3;

    public static void read_stc_stitches(EmbReader reader) throws IOException {
        EmbPattern pattern = reader.pattern;
        byte[] b = new byte[COMMANDSIZE];
        while (true) {
            if (reader.readFully(b) != b.length) {
                break;
            }
            int x = b[0];
            int y = -b[1];
            int ctrl = b[2] & 0xFF;
            switch (ctrl) {
                case 0x01:
                    pattern.stitch(x, y);
                    continue;
                case 0x00:
                    pattern.move(x, y);
                    continue;
                case 25:
                    break;
                default:
                    int needle = ctrl - 2;
                    pattern.needle_change(needle);
                    continue;
            }
            break;
        }
        pattern.end();
    }

    @Override
    public void read() throws IOException {
        skip(0x28); //DESIGN: xxxxxx STITCH: xxx.
        read_stc_stitches(this);
    }
}
