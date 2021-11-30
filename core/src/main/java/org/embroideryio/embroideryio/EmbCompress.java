/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embroideryio.embroideryio;

import java.nio.ByteBuffer;
import java.util.Arrays;


/**
 *
 * @author Tat
 */
public class EmbCompress {
    public static ByteBuffer expand(ByteBuffer data, int uncompressed_size) {
        EmbCompress compress = new EmbCompress();
        return compress.decompress(data, uncompressed_size);
    }

    int bit_position = 0;
    ByteBuffer input_data = null;
    int block_elements = -1;
    Huffman character_huffman = null;
    Huffman distance_huffman = null;

    public EmbCompress() {
    }

    public int get_bits(int start_pos_in_bits, int length) {
        int end_pos_in_bits = start_pos_in_bits + length - 1;
        int start_pos_in_bytes = start_pos_in_bits / 8;
        int end_pos_in_bytes = end_pos_in_bits / 8;
        int value = 0;
        for (int i = start_pos_in_bytes, s = end_pos_in_bytes + 1; i < s; i++) {
            value <<= 8;
            try {
                value |= input_data.get(i) & 0xFF;
            }
            catch (IndexOutOfBoundsException e) {
                //pass
            }
        }
        int unused_bits_right_of_sample = (8 - (end_pos_in_bits + 1) % 8) % 8;
        int mask_sample_bits = (1 << length) - 1;
        int original = (value >> unused_bits_right_of_sample) & mask_sample_bits;
        return original;
    }
                
    public int pop(int bit_count) {
        int value = peek(bit_count);
        slide(bit_count);
        return value;
    }

    public int peek(int bit_count) {
        return get_bits(bit_position, bit_count);
    }

    public void slide(int bit_count) {
        bit_position += bit_count;
    }

    public int read_variable_length() {
        int m = pop(3);
        if (m != 7) {
            return m;
        }
        for (int q = 0, r = 13; q < r; q++) {
            // max read is 16 bit, 3 bits already used. It can't exceed 16-3
            int s = pop(1);
            if (s == 1) {
                m += 1;
            }
            else {
                break;
            }
        }
        return m;
    }

    public Huffman load_character_length_huffman() {
        int count = pop(5);
        if (count == 0) {
            int v = pop(5);
            return new Huffman(v); // value=v
        }
        else {
            int[] huffman_code_lengths = new int[count];
            int index = 0;
            while (index < count) {
                if (index == 3) { // Special index 3, skip up to 3 elements.
                    index += pop(2);
                }
                huffman_code_lengths[index] = read_variable_length();
                index += 1;
            }
            Huffman huffman = new Huffman(huffman_code_lengths, 8);
            huffman.build_table();
            return huffman;
        }
        
    }

    public Huffman load_character_huffman(Huffman length_huffman) {
        int count = pop(9);
        if (count == 0) {
            int v = pop(9);
            return new Huffman(v); // value=v
        }
        else {
            int[] huffman_code_lengths = new int[count];
            int index = 0;
            while (index < count) {
                int[] h = length_huffman.lookup(peek(16));
                int c = h[0];
                slide(h[1]);
                switch (c) {
                    case 0:
                        //# C == 0, skip 1.
                        c = 1;
                        index += c;
                        break;
                    case 1:
                        //# C == 1, skip 3 + read(4)
                        c = 3 + pop(4);
                        index += c;
                        break;
                    case 2:
                        // # C == 2, skip 20 + read(9)
                        c = 20 + pop(9);
                        index += c;
                        break;
                    default:
                        c -= 2;
                        huffman_code_lengths[index] = c;
                        index += 1;
                        break;
                }
            }
            Huffman huffman = new Huffman(huffman_code_lengths);
            huffman.build_table();
            return huffman;
        }
    }

    public Huffman load_distance_huffman() {
        int count = pop(5);
        if (count == 0) {
            int v = pop(5);
            return new Huffman(v); // value=v
        }
        else {
            int index = 0;
            int[] lengths = new int[count];
            for (int i = 0; i < count; i++) {
                lengths[index] = read_variable_length();
                index += 1;
            }
            Huffman huffman = new Huffman(lengths);
            huffman.build_table();
            return huffman;
        }
    }

    public void load_block() {
        block_elements = pop(16);
        Huffman character_length_huffman = load_character_length_huffman();
        character_huffman = load_character_huffman(character_length_huffman);
        distance_huffman = load_distance_huffman();
    }

    public int get_token() {
        if (block_elements <= 0) {
            load_block();
        }
        block_elements -= 1;
        int[] h = character_huffman.lookup(peek(16));
        slide(h[1]);
        return h[0];
    }

    public int get_position() {
        int[] h = distance_huffman.lookup(peek(16));
        slide(h[1]);
        if (h[0] == 0) {
            return 0;
        }
        int v = h[0] - 1;
        v = (1 << v) + pop(v);
        return v;
    }

    public ByteBuffer decompress(ByteBuffer input_data_set, int uncompressed_size) {
        input_data = input_data_set;
        ByteBuffer output_data = ByteBuffer.allocate(uncompressed_size);
        block_elements = -1;
        int bits_total = input_data.array().length * 8;
        while ((bits_total > bit_position) && ((uncompressed_size == -1) || (output_data.array().length <= uncompressed_size))) {
            int character = get_token();
            if (character <= 255) {  //# literal.
                output_data.put((byte) character);
            }
            else if (character == 510) {
                break; //# END
            }
            else {
                int length = character - 253;  //# Min length is 3. 256-253=3.
                int back = get_position() + 1;
                int position = output_data.array().length - back;
                if (back > length){
                    //# Entire lookback is already within output data.
                    output_data.put(Arrays.copyOfRange(output_data.array(), position, position + length));
                } else {
                    //# Will read & write the same data at some point.
                    for (int i = position, s = position + length; i < s; i++) {
                        try {
                            System.out.print(i);
                            output_data.put(output_data.get(i));
                        } catch (IndexOutOfBoundsException e) {
                            System.out.print(e);
                        }
                    }
                }
            }
        }
        return output_data;
    }

    public class Huffman {
        int default_value;
        int[] lengths;
        int[] table = new int[0];
        int table_width = 0;
        
        public Huffman(int value) {
            this.default_value = value;
        }
        
        public Huffman(int[] lengths) {
            this.lengths = lengths;
        }
        
        public Huffman(int[] lengths, int value) {
            this.default_value = value;
            this.lengths = lengths;
            this.table_width = 0;
        }

        /*
        Build an index huffman table based on the lengths. lowest index value wins in a tie.
        */
        public void build_table() {
            int m = Integer.MIN_VALUE;
            for (int q : lengths) { // m = max(lengths)
                if (q > m) {
                    m = q;
                }
            }
            table_width = m;
            int size = (1 << table_width);
            for (int bit_length = 1, s = table_width + 1; bit_length < s; bit_length++) {
                size /= 2;
                for (int len_index = 0, ss = lengths.length; len_index < ss; len_index++) {
                    int length = lengths[len_index];
                    if (length == bit_length) {
                        int[] old_table = table;
                        int[] new_table = Arrays.copyOf(old_table, old_table.length + size);
                        Arrays.fill(new_table, old_table.length, new_table.length, len_index);
                        table = new_table;
                    }
                }
            }
        }

        /*
        lookup into the index, returns value and length
        must be requested with 2 bytes.
        */
        public int[] lookup(int byte_lookup) {
            if (table.length == 0) {
                return new int[] { default_value, 0 };
            }
            int v = table[byte_lookup >> (16 - table_width)];
            return new int[]  { v, lengths[v] };
        }
    }
}
