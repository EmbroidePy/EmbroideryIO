/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embroideryio.embroideryio;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *
 * @author Tat
 */
public class HusReader  extends EmbReader {

    @Override
    public void read() throws IOException {
        int magic_code = readInt32LE();
        int number_of_stitches = readInt32LE();
        int number_of_colors = readInt32LE();

        int extend_pos_x = signed16(readInt16LE());
        int extend_pos_y = signed16(readInt16LE());
        int extend_neg_x = signed16(readInt16LE());
        int extend_neg_y = signed16(readInt16LE());

        int command_offset = readInt32LE();
        int x_offset = readInt32LE();
        int y_offset = readInt32LE();

        byte[] string = new byte[8];
        readFully(string);
        
        int unknown_16_bit = readInt16LE();

        EmbThreadHus[] hus_thread_set = EmbThreadHus.getThreadSet();
        for (int i = 0; i < number_of_colors; i++) {
            int index = readInt16LE();
            pattern.add(hus_thread_set[index % hus_thread_set.length]);
        }
        ByteBuffer b;
        
        seek(command_offset);
        byte[] command_bytes = new byte[x_offset - command_offset];
        readFully(command_bytes);
        ByteBuffer command_decompressed = EmbCompress.expand(ByteBuffer.wrap(command_bytes), number_of_stitches);
                
        seek(x_offset);
        byte[] x_bytes = new byte[y_offset - x_offset];
        readFully(x_bytes);
        ByteBuffer x_decompressed = EmbCompress.expand(ByteBuffer.wrap(x_bytes), number_of_stitches);
        
        seek(y_offset);
        byte[] y_bytes = new byte[100000]; //readfully
        readFully(y_bytes);
        ByteBuffer y_decompressed = EmbCompress.expand(ByteBuffer.wrap(y_bytes), number_of_stitches);
        
        int stitch_count = Math.min(
                Math.min(
                        command_decompressed.capacity(), x_decompressed.capacity()
                ), y_decompressed.capacity());

        for (int i = 0; i < stitch_count; i++) {
            int cmd = command_decompressed.get(i);
            int x = signed8(x_decompressed.get(i));
            int y = -signed8(y_decompressed.get(i));
            if (cmd == 0x80) {  //# STITCH
                pattern.stitch(x, y);
            }
            else if (cmd == 0x81) { //  # JUMP
                pattern.move(x, y);
            }
            else if (cmd == 0x84) { //  # COLOR_CHANGE
                pattern.color_change(x, y);
            }
            else if (cmd == 0x88) { //# TRIM
                if ((x != 0) || (y != 0)) {
                    pattern.move(x, y);
                }
                pattern.trim();
            }
            else if (cmd == 0x90) { // # END
                break;
            }
            else {  //# UNMAPPED COMMAND
                break;
            }
        }
        pattern.end();
    }
}
