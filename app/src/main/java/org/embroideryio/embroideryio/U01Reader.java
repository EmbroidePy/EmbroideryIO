package org.embroideryio.embroideryio;

import static org.embroideryio.embroideryio.EmbConstant.*;

import java.io.IOException;

public class U01Reader extends EmbReader {

    public void read_u01_stitches() throws IOException {
        byte[] b = new byte[3];
        while (true) {
            if (readFully(b) != b.length) break;
            int ctrl = b[0] & 0xFF;
            int dy = -(b[1] & 0xFF);
            int dx = b[2] & 0xFF;
            if ((ctrl & 0x20) != 0) {
                dx = -dx;
            }
            if ((ctrl & 0x40) != 0) {
                dy = -dy;
            }
            int command = ctrl & 0b11111;
            switch (command) {
                case 0x00:
                    //stitch
                    pattern.stitch(dx, dy);
                    continue;
                case 0x01:
                    //jump
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
                    //C00 STOP
                    pattern.stop();
                    if ((dx != 0) || (dy != 0)) {
                        pattern.move(dx, dy);
                    }
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
                    // C01 - C14
                    int needle = command - 0x08;
                    pattern.needle_change(needle);
                    if ((dx != 0) || (dy != 0)) {
                        pattern.move(dx, dy);
                    }
                    continue;
                case 0x18:
                    break;
            }
            if (ctrl == 0x2B) {
                //Postfix teach information from machine. Do not read.
                break;
            }
            break; // Uncaught.
        }
        pattern.end();
    }


    @Override
    public void read() throws IOException {
        skip(0x80);
        skip(0x80);
        read_u01_stitches();
    }
}
