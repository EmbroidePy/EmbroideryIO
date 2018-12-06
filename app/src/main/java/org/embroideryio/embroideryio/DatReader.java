package org.embroideryio.embroideryio;

import java.io.IOException;

import static org.embroideryio.embroideryio.EmbConstant.*;

public class DatReader extends EmbReader {

    public boolean read_barudan_dat() throws IOException {
        byte[] b = new byte[3];
        while (true) {
            if (readFully(b) != b.length) break;
            int ctrl = b[0] & 0xFF;
            int dy = -(b[1] & 0xFF);
            int dx = b[2] & 0xFF;
            if ((ctrl & 0x80) == 0) {
                //This bit is always set, must be sunstar.
                return false;
            }
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
        return true;
    }

    public void read_sunstar_dat_stitches() throws IOException {
        byte[] b = new byte[3];
        while (true) {
            if (readFully(b) != b.length) break;
            int x = b[0] & 0x7F;
            int y = b[1] & 0x7F;
            if ((b[0] & 0x80) != 0) {
                x = -x;
            }
            if ((b[1] & 0x80) != 0) {
                y = -y;
            }
            y = -y;
            int ctrl = b[2] & 0xFF;
            switch (ctrl) {
                case 0x07:
                    pattern.stitch(x,y);
                    continue;
                case 0x04:
                    pattern.move(x,y);
                    continue;
                case 0x80:
                    pattern.trim();
                    if ((x != 0) || (y != 0)) {
                        pattern.move(x,y);
                    }
                    continue;
                case 0x87:
                    pattern.color_change();
                    if ((x != 0) || (y != 0)) {
                        pattern.move(x,y);
                    }
                    continue;
                case 0x84: //Initialized info.
                    pattern.stitch(x,y);
                    continue;
                case 0:
                    break;
            }
            break;
        }
    }

    public void read_sunstar_dat() throws IOException {
        seek(0x100);
        read_sunstar_dat_stitches();
    }

    @Override
    public void read() throws IOException {
        if (!read_barudan_dat()) {
            read_sunstar_dat();
        }
    }
}
