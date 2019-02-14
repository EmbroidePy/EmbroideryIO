package org.embroideryio.embroideryio;

import java.io.IOException;

public class Vp3Reader extends EmbReader {

    private String read_vp3_string_8() throws IOException {
        int stringLength = readInt16BE();
        byte content[] = new byte[stringLength];
        readFully(content);
        return new String(content, "UTF-8");
    }

    private String read_vp3_string_16() throws IOException {
        int stringLength = readInt16BE();
        byte content[] = new byte[stringLength];
        readFully(content);
        return new String(content, "UTF-16");
    }

    private void skip_vp3_string() throws IOException {
        int stringLength = readInt16BE();
        skip(stringLength);
    }

    @Override
    protected void read() throws IOException {
        byte magicString[] = new byte[6];
        readFully(magicString);
        //"%vsm%\0"
        skip_vp3_string();
        skip(7);
        skip_vp3_string();
        skip(32);
        float center_x = (readInt32BE() / 100f);
        float center_y = -(readInt32BE() / 100f);
        skip(27);
        skip_vp3_string();
        skip(24);
        skip_vp3_string();
        int count_colors = readInt16BE();
        for (int i = 0; i < count_colors; i++) {
            vp3_read_colorblock(center_x, center_y);
        }
    }

    void vp3_read_colorblock(float center_x, float center_y) throws IOException {
        byte[] bytecheck = new byte[3];
        readFully(bytecheck); //0x00,0x05,0x00
        int distance_to_next_block_050 = readInt32BE();
        int block_end_position = distance_to_next_block_050 + tell();
        float start_position_x = (readInt32BE() / 100f);
        float start_position_y = -(readInt32BE() / 100f);
        float abs_x = start_position_x + center_x;
        float abs_y = start_position_y + center_y;
        if ((abs_x != 0) && (abs_y != 0)) {
            pattern.moveAbs(abs_x, abs_y);
        }
        EmbThread thread = vp3_read_thread();
        pattern.add(thread);
        skip(15);
        readFully(bytecheck);  // \x0A\xF6\x00
        int stitch_byte_length = block_end_position - tell();
        byte[] stitch_bytes = new byte[stitch_byte_length];
        if (readFully(stitch_bytes) != stitch_bytes.length) return;
        int i = 0;
        while (i < stitch_byte_length - 1) {
            int x = stitch_bytes[i];
            int y = stitch_bytes[i + 1];
            i += 2;
            if ((x & 0xFF) != 0x80) {
                pattern.stitch(x, y);
                continue;
            }
            if (y == 0x01) {
                x = signed16(stitch_bytes[i], stitch_bytes[i + 1]);
                i += 2;
                y = signed16(stitch_bytes[i], stitch_bytes[i + 1]);
                i += 2;
                if ((Math.abs(x) > 255) || (Math.abs(y) > 255)) {
                    pattern.trim();
                    pattern.move(x, y);
                } else {
                    pattern.stitch(x, y);
                }
            } else if (y == 0x02) {
                //end of long stitch mode.
            } else if (y == 0x03) {
                pattern.end();
                return;
            }
        }
        pattern.trim();
        pattern.color_change();
    }

    EmbThread vp3_read_thread() throws IOException {
        EmbThread thread = new EmbThread();
        int colors = readInt8();
        int transition = readInt8();
        for (int m = 0; m < colors; m++) {
            thread.color = readInt24BE();
            int parts = readInt8();
            int color_length = readInt16BE();
        }
        int thread_type = readInt8();
        int weight = readInt8();
        thread.catalogNumber = read_vp3_string_8();
        thread.description = read_vp3_string_8();
        thread.brand = read_vp3_string_8();
        return thread;
    }
}
