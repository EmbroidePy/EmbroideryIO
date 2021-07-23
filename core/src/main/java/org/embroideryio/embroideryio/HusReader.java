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
        byte[] y_bytes = this.stream.readAllBytes(); //readfully
        readFully(y_bytes);
        ByteBuffer y_decompressed = EmbCompress.expand(ByteBuffer.wrap(y_bytes), number_of_stitches);
        
        int stitch_count = Math.min(
                Math.min(
                        command_decompressed.capacity(), x_decompressed.capacity()
                ), y_decompressed.capacity());

        OUTER:
        for (int i = 0; i < stitch_count; i++) {
            int cmd = (int)(command_decompressed.get(i) & 0xFF);
            int x = signed8((int)(x_decompressed.get(i) & 0xFF));
            int y = -signed8((int)(y_decompressed.get(i) & 0xFF));
            switch (cmd) {
                case 0x80:
                    //# STITCH
                    pattern.stitch(x, y);
                    break;
                case 0x81:
                    //  # JUMP
                    pattern.move(x, y);
                    break;
                case 0x84:
                    //  # COLOR_CHANGE
                    pattern.color_change(x, y);
                    break;
                case 0x88:
                    //# TRIM
                    if ((x != 0) || (y != 0)) {
                        pattern.move(x, y);
                    }   pattern.trim();
                    break;
                case 0x90:
                    // # END
                    break OUTER;
                default:
                    //# UNMAPPED COMMAND
                    break OUTER;
            }
        }
        pattern.end();
    }
}
