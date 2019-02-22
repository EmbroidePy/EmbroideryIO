package org.embroideryio.embroideryio;

import java.io.IOException;
import java.util.ArrayList;

public class PecReader extends EmbReader {

    static final int MASK_07_BIT = 0b01111111;
    static final int JUMP_CODE = 0x10;
    static final int TRIM_CODE = 0x20;
    static final int FLAG_LONG = 0x80;

    @Override
    public void read() throws IOException {
        skip(0x8);
        readPec();
    }

    public void readPec() throws IOException {
        skip(3);
        String label = readString(16).trim();
        if (label.length() != 0) {
            pattern.setMetadata(EmbPattern.PROP_NAME, label);
        }
        skip(0x0F);
        int pec_graphic_byte_stride = readInt8();
        int pec_graphic_icon_height = readInt8();
        skip(0x0C);
        int colorChanges = readInt8();
        if (colorChanges == Integer.MIN_VALUE) return;
        byte[] color_bytes = new byte[colorChanges + 1];
        readFully(color_bytes);
        map_pec_colors(color_bytes);
        skip(0x1D0 - colorChanges);
        int stitch_block_end = readInt24LE() - 5 + tell();
        //The end of this value is 5 into the sttichblock.
        // 3 bytes, '0x31, 0xff, 0xf0, 6 2-byte shorts. 15 total.
        skip(0x0F);
        readPecStitches();
        seek(stitch_block_end);
        int byte_size = pec_graphic_byte_stride * pec_graphic_icon_height;
        readPecGraphics(byte_size, pec_graphic_byte_stride, colorChanges);
    }

    public void readPecGraphics(int size, int stride, int count) throws IOException {
        byte[] graphic = new byte[size];
        for (int i = 0; i < count; i++) {
            if (readFully(graphic) != graphic.length) {
                pattern.setMetadata("pec_graphic_" + i, get_graphic_as_string(graphic, '#', ' '));
                break;
            }
        }
    }

    public String get_graphic_as_string(byte[] graphic, char one, char zero) {
        StringBuilder sb = new StringBuilder();
        int stride = 6;
        for (int i = 0, ie = graphic.length; i < ie; i++) {
            if ((i + 1) % stride == 0) {
                sb.append('\n');
            }
            int b = graphic[i];
            for (int k = 0; k < 8; k++) {
                if ((b & 1) == 0) {
                    sb.append(zero);
                } else {
                    sb.append(one);
                }
                b >>= 1;
            }
        }
        return sb.toString();
    }

    public void process_pec_colors(byte[] color_bytes) {
        int color_count = color_bytes.length;
        int color_index = 0;
        EmbThreadPec[] threadSet = EmbThreadPec.getThreadSet();
        for (int i = 0; i < color_count; i++) {
            int c = color_bytes[color_index++] & 0xFF;
            int index = (c % threadSet.length);
            pattern.threadlist.add(threadSet[index]);
        }
    }

    public void process_pec_table(byte[] color_bytes) {
        int color_count = color_bytes.length;
        int color_index = 0;
        EmbThreadPec[] threadSet = EmbThreadPec.getThreadSet();
        EmbThread[] threadMap = new EmbThread[threadSet.length];
        ArrayList<EmbThread> queue = new ArrayList<>();
        for (int i = 0; i < color_count; i++) {
            int c = color_bytes[color_index++] & 0xFF;
            int index = (c % threadSet.length);
            EmbThread value = threadMap[index];
            if (value == null) {
                if (!pattern.threadlist.isEmpty()) {
                    value = pattern.threadlist.remove(0);
                } else {
                    value = threadSet[index];
                }
                threadMap[index] = value;
            }
            queue.add(value);
        }
        pattern.threadlist.clear();
        pattern.threadlist.addAll(queue);
    }

    public void map_pec_colors(byte[] color_bytes) {
        int color_count = color_bytes.length;
        int color_index = 0;
        if (pattern.threadlist.isEmpty()) {
            //if the threadlist is empty, we are reading a file without header threads.
            process_pec_colors(color_bytes);
            return;
        }
        if (pattern.threadlist.size() == color_count) {
            //threadList is equal to the colors, use the default 1 header thread, to 1 color produced by some flawed PES writers.
            //since we're 1 to 1, the listed colors are irrelevant.
            return;
        }
        //if the threadlist is not empty but also not equal to the number of colorchanges;
        //convert unique list threadList to 1 to 1 list thread.
        process_pec_table(color_bytes);
    }

    public void readPecStitches() throws IOException {
        int val1, val2;
        int x, y;
        while (true) {
            val1 = readInt8();
            if (val1 == Integer.MIN_VALUE) {
                break;
            }
            val2 = readInt8();
            if (val2 == Integer.MIN_VALUE) {
                break;
            }

            int code = (val1 << 8) | val2;
            if (val1 == 0xFF) {// && val2 == 0x00) {
                break; //End command.
            }
            if (val1 == 0xFE && val2 == 0xB0) {
                skip(1);
                pattern.color_change();
                continue;
            }
            boolean jump = false;
            boolean trim = false;
            /* High bit set means 12-bit offset, otherwise 7-bit signed delta */
            if ((val1 & FLAG_LONG) != 0) {
                if ((val1 & TRIM_CODE) != 0) {
                    trim = true;
                }
                if ((val1 & JUMP_CODE) != 0) {
                    jump = true;
                }

                x = (code << 20) >> 20;

                val2 = readInt8();
                if (val2 == Integer.MIN_VALUE) {
                    break;
                }
            } else {
                x = (val1 << 25) >> 25;
            }
            if ((val2 & FLAG_LONG) != 0) {
                if ((val2 & TRIM_CODE) != 0) {
                    trim = true;
                }
                if ((val2 & JUMP_CODE) != 0) {
                    jump = true;
                }

                int val3 = readInt8();
                if (val3 == Integer.MIN_VALUE) {
                    break;
                }
                code = (val2 << 8) | val3;

                y = (code << 20) >> 20;
            } else {
                y = (val2 << 25) >> 25;
            }
            if (jump) {
                pattern.move(x, y);
            } else if (trim) {
                pattern.trim();
                pattern.move(x, y);
            } else {
                pattern.stitch(x, y);
            }
        }
        pattern.end();
    }
}
