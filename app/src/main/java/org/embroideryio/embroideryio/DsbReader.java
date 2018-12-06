package org.embroideryio.embroideryio;

import java.io.IOException;
import static org.embroideryio.embroideryio.EmbConstant.FAST;
import static org.embroideryio.embroideryio.EmbConstant.SLOW;

public class DsbReader extends EmbReader {

    public static final String MIME = "application/x-dsb";
    public static final String EXT = "dsb";

    private final static int COMMANDSIZE = 3;

    public static void b_stitch_encoding_read(EmbReader reader) throws IOException {
        EmbPattern pattern = reader.pattern;
        byte[] b = new byte[COMMANDSIZE];
        while (true) {
            if (reader.readFully(b) != b.length) {
                break;
            }
            int ctrl = b[0];
            int dy = -(b[1] & 0xFF);
            int dx = b[2] & 0xFF;

            if ((ctrl & 0x40) != 0) {
                dy = -dy;
            }
            if ((ctrl & 0x20) != 0) {
                dx = -dx;
            }
            int control = ctrl & 0b11111;
            switch (control) {
                case 0x00:
                    pattern.stitch(dx, dy);
                    continue;
                case 0x01:
                    pattern.move(dx, dy);
                    continue;
                case 0x02:
                    //fast
                    pattern.addStitchRel(0, 0, FAST);
                    if ((dx != 0) || (dy != 0)) {
                        pattern.stitch(dx, dy);
                    }
                    continue;
                case 0x03:
                    //fast, jump
                    pattern.addStitchRel(0, 0, FAST);
                    if ((dx != 0) || (dy != 0)) {
                        pattern.move(dx, dy);
                    }
                    continue;
                case 0x04:
                    //slow
                    pattern.addStitchRel(0, 0, SLOW);
                    if ((dx != 0) || (dy != 0)) {
                        pattern.stitch(dx, dy);
                    }
                    continue;
                case 0x05:
                    //slow, jump
                    pattern.addStitchRel(0, 0, SLOW);
                    if ((dx != 0) || (dy != 0)) {
                        pattern.move(dx, dy);
                    }
                    continue;
                case 0x06:
                    //T1 Top Thread Trimming, TTrim.
                    pattern.trim();
                    if ((dx != 0) || (dy != 0)) {
                        pattern.move(dx, dy);
                    }
                    continue;
                case 0x07:
                    //T2 Bobbin Threading
                    pattern.trim();
                    if ((dx != 0) || (dy != 0)) {
                        pattern.move(dx, dy);
                    }
                    continue;
                case 0x08:
                    if ((dx != 0) || (dy != 0)) {
                        pattern.stitch(dx, dy);
                    }
                    pattern.stop();
                    continue;
                case 0x09:
                case 0x0A:
                case 0x0B:
                case 0x0C:
                case 0x0D:
                case 0x0E:
                case 0x0F:
                case 0x10:
                case 0x11:
                case 0x12:
                case 0x13:
                case 0x14:
                case 0x15:
                case 0x16:
                case 0x17:
                    int needle = control - 8;
                    pattern.needle_change(needle);
                    continue;
                case 0x18:
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
        DstReader.dst_read_header(this);
        b_stitch_encoding_read(this);
    }
}
