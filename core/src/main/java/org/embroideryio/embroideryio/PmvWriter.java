package org.embroideryio.embroideryio;

import java.io.IOException;

import static org.embroideryio.embroideryio.EmbConstant.*;

public class PmvWriter extends EmbWriter {

    static final int MAX_STITCH_DISTANCE = 70;
    static final int MAX_PERMITTED_STITCHES = 100;

    @Override
    public void write() throws IOException {
        double max_x = Double.NEGATIVE_INFINITY;
        double min_x = Double.POSITIVE_INFINITY;
        double max_y = Double.NEGATIVE_INFINITY;
        double min_y = Double.POSITIVE_INFINITY;
        int point_count = 0;
        for (int i = 0, ie = pattern.size(); i < ie; i++) {
            int data = pattern.getData(i) & COMMAND_MASK;
            float x = pattern.getX(i);
            float y = pattern.getY(i);
            if ((data == STITCH) || (data == JUMP)) {
                point_count += 1;
                if (x > max_x) {
                    max_x = x;
                }
                if (x < min_x) {
                    min_x = x;
                }
                if (y > max_y) {
                    max_y = y;
                }
                if (y < min_y) {
                    min_y = y;
                }
            }
            if (point_count >= MAX_PERMITTED_STITCHES) {
                break;
            }
        }
        double center_y = (min_y + max_y) / 2.0;
        double normal_max_y = max_y - center_y;
        double scale_y;
        if (normal_max_y > 35.0) { //14 * 2.5 = 35.0
            scale_y = 14.0 / normal_max_y;
        } else {
            scale_y = 1.0 / 2.5; //pure unit conversion.
        }
        double scale_x = 1.0 / 2.5;
        write("#PMV0001");
        String header = "....................................";
        write(header.substring(0, 36));
        write(new byte[]{
            0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00
        });
        writeInt16LE(point_count);
        writeInt16LE(point_count * 2);
        int point_index = -1;
        int int_max_x = Integer.MIN_VALUE;
        int int_min_x = Integer.MAX_VALUE;
        int int_max_y = Integer.MIN_VALUE;
        int int_min_y = Integer.MAX_VALUE;
        double xx = 0;
        for (int i = 0, ie = pattern.size(); i < ie; i++) {
            point_index += 1;
            if (point_index >= point_count) {
                break;
            }
            int data = pattern.getData(i) & COMMAND_MASK;
            float x = pattern.getX(i);
            float y = pattern.getY(i);
            x *= scale_x;
            y -= center_y;
            y *= scale_y;
            int int_y = (int) Math.rint(y);
            int int_x = (int) Math.rint(x - xx);
            xx += int_x;
            if (data != STITCH && data != JUMP) {
                continue;
            }
            if (int_x > int_max_x) {
                int_max_x = int_x;
            }
            if (int_x < int_min_x) {
                int_min_x = int_x;
            }
            if (int_y > int_max_y) {
                int_max_y = int_y;
            }
            if (int_y < int_min_y) {
                int_min_y = int_y;
            }

            if (int_x < 0) { //6 bit signed.
                int_x += 64;
            }
            if (int_y < 0) { //5 bit signed.
                int_y += 32;
            }
            writeInt8(int_x);
            writeInt8(int_y);
        }

        writeInt16LE(0);
        writeInt16LE(256);
        write(new byte[]{
            0x00, 0x00, 0x00, 0x00, 0x05, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0x00
        });
        writeInt16LE(256);
        writeInt8(0);
        writeInt8(0);

        int length_range = int_max_x - int_min_x;
        write_length_lookup_table(length_range);
        int width_range = int_max_y - int_min_y;
        write_width_lookup_table(width_range);
        writeInt16LE(0x12);
        write(new byte[]{
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
        });
    }

    private void write_length_lookup_table(int length_range) throws IOException {
        //I've not solved this for how they are actually made, writing a something that should work.
        int[] write_values = new int[]{
            0, 0, 10, 71, 20, 143, 40, 214, 60, 286, 80, 357,
            100, 429, 120, 500, 140, 571, 160, 714, 180, 786, 200, 857,
            250, 1000, 300, 1286, 350, 1429, 400, 1571, 450, 1786, 500, 2000
        };
        writeInt8(12);
        int steps = write_values.length / 2;
        writeInt8(steps);
        for (int i = 0, ie = write_values.length; i < ie; i += 2) {
            int length_at_step = write_values[i];
            int other_at_step = write_values[i + 1];
            writeInt16LE(length_at_step);
            writeInt16LE(other_at_step);
        }
    }

    private void write_width_lookup_table(int width_range) throws IOException {
        int pos = width_range / 2;
        writeInt8(pos);
        if (width_range == 0) {
            writeInt8(1);
            writeInt16LE(8192);
            writeInt16LE(1000);
            return;
        }
        int steps = 15;
        writeInt8(steps);
        double second_max = 28000.0 / ((float) width_range);
        double second_step = second_max / ((float) steps - 1);
        for (int i = 0; i < steps; i++) {
            int width_at_step = 50 * i;
            int other_at_step = (int) Math.rint(second_step * i);
            
            writeInt16LE(width_at_step);
            writeInt16LE(other_at_step);
        }
    }

}
