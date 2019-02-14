package org.embroideryio.embroideryio;

import java.io.IOException;

import static org.embroideryio.embroideryio.EmbConstant.*;

public class Vp3Writer extends EmbWriter {

    public Vp3Writer() {
        super();
        settings.put(EmbEncoder.PROP_MAX_JUMP, 3200f);
        settings.put(EmbEncoder.PROP_MAX_STITCH, 255f);
        settings.put(EmbEncoder.PROP_FULL_JUMP, false);
        settings.put(EmbEncoder.PROP_ROUND, true);
    }

    private void vp3_write_string_8(String string) throws IOException {
        writeInt16BE(string.length());
        write(string.getBytes("UTF-8"));
    }

    private void vp3_write_string_16(String string) throws IOException {
        writeInt16BE(string.length() * 2);
        write(string.getBytes("UTF-16BE"));
    }

    private void vp3_write_bytes(byte[] bytes) throws IOException {
        writeInt16BE(bytes.length);
        write(bytes);
    }

    @Override
    public void write() throws IOException {
        pattern.fixColorCount();
        pattern.getStitches().translate(
                -pattern.getStitches().getCenterX(),
                -pattern.getStitches().getCenterX());
        write("%vsm%");
        writeInt8(0);
        vp3_write_string_16("Produced by     Software Ltd");
        write_file();
    }

    private void write_file() throws IOException {
        writeInt8(0x00);
        writeInt8(0x02);
        writeInt8(0x00);
        int placeholder_distance_end_of_file_block_020 = tell();
        space_holder(4);
        //this refers to the end of the final block, not entire bytes.
        vp3_write_string_16(""); //this is global notes and settings string.
        int count_stitches = pattern.size();

        int count_color_blocks_total = 0;
        for (StitchBlock block : pattern.asColorBlock()) {
            count_color_blocks_total += 1;
        }

        float[] bounds = pattern.getBounds();
        writeInt32BE((int) (bounds[2] * 100)); //right
        writeInt32BE((int) (bounds[1] * -100));//-top
        writeInt32BE((int) (bounds[0] * 100)); //left
        writeInt32BE((int) (bounds[3] * -100)); //-bottom
        int count_just_stitches = 0;
        for (int i = 0, ie = pattern.size(); i < ie; i++) {
            int data = pattern.getData(i) & COMMAND_MASK;
            if (data == END) {
                continue;
            }
            count_just_stitches += 1;
        }
        writeInt32BE(count_just_stitches);
        writeInt8(0x00);
        writeInt8(count_color_blocks_total);
        writeInt8(12);
        writeInt8(0x00);
        int count_designs = 1;
        writeInt8(count_designs);
        for (int i = 0; i < count_designs; i++) {
            write_design_block(bounds, count_color_blocks_total);
        }
        int current_pos = tell();
        writeSpaceHolder32BE(
                current_pos
                - placeholder_distance_end_of_file_block_020
                - 4
        );
    }

    private void write_design_block(float[] bounds, int count_color_blocks_total) throws IOException {
        writeInt8(0x00);
        writeInt8(0x03);
        writeInt8(0x00);
        int placeholder_distance_end_of_design_block_030 = tell();
        space_holder(4);

        double width = bounds[2] - bounds[0];
        double height = bounds[3] - bounds[1];
        double half_width = width / 2;
        double half_height = height / 2;
        double center_x = bounds[2] - half_width;
        double center_y = bounds[3] - half_height;

        writeInt32BE(((int) center_x) * 100); //initial x;
        writeInt32BE(((int) center_y) * 100); //initial y;
        writeInt8(0x00);
        writeInt8(0x00);
        writeInt8(0x00);

        //bounds 2
        writeInt32BE(((int) half_width) * -100);
        writeInt32BE(((int) half_width) * 100);
        writeInt32BE(((int) half_height) * -100);
        writeInt32BE(((int) half_height) * 100);

        writeInt32BE(((int) width) * 100);
        writeInt32BE(((int) height) * 100);
        vp3_write_string_16(""); //this is notes and settings string.
        writeInt8(100);
        writeInt8(100);

        writeInt32BE(4096);
        writeInt32BE(0);
        writeInt32BE(0);
        writeInt32BE(4096);

        write("xxPP");
        writeInt8(0x01);
        writeInt8(0x00);

        vp3_write_string_16("Produced by     Software Ltd");

        writeInt16BE(count_color_blocks_total);
        boolean first = true;
        for (StitchBlock sb : pattern.asColorBlock()) {
            Points stitches = sb.getPoints();
            EmbThread thread = sb.getThread();
            write_vp3_colorblock(first, center_x, center_y, stitches, thread);
            first = false;
        }
        int current_pos = tell();
        writeSpaceHolder32BE(
                current_pos
                - placeholder_distance_end_of_design_block_030
                - 4
        );
    }

    private void write_vp3_colorblock(boolean first, double center_x, double center_y, Points stitches, EmbThread thread) throws IOException {
        writeInt8(0x00);
        writeInt8(0x05);
        writeInt8(0x00);
        int placeholder_distance_end_of_color_block_050 = tell();
        space_holder(4);
        double first_pos_x = 0;
        double first_pos_y = 0;
        double last_pos_x = 0;
        double last_pos_y = 0;
        if (stitches.size() > 0) {
            first_pos_x = stitches.getX(0);
            first_pos_y = stitches.getY(0);
            if (first) {
                first_pos_x = 0;
                first_pos_y = 0;
            }
            last_pos_x = stitches.getX(stitches.size() - 1);
            last_pos_y = stitches.getY(stitches.size() - 1);
        }
        double start_position_from_center_x = first_pos_x - center_x;
        double start_position_from_center_y = -(first_pos_y - center_y);
        writeInt32BE((int) (start_position_from_center_x) * 100);
        writeInt32BE((int) (start_position_from_center_y) * 100);

        vp3_write_thread(thread);

        double block_shift_x = last_pos_x - first_pos_x;
        double block_shift_y = -(last_pos_y - first_pos_y);

        writeInt32BE(((int) block_shift_x) * 100);
        writeInt32BE(((int) block_shift_y) * 100);

        write_stitches_block(stitches, first_pos_x, first_pos_y);

        writeInt8(0);
        int current_pos = tell();
        writeSpaceHolder32BE(
                current_pos
                - placeholder_distance_end_of_color_block_050
                - 4
        );
    }

    private void vp3_write_thread(EmbThread thread) throws IOException {
        writeInt8(1); //1 color.
        writeInt8(0); //0 transition.
        writeInt24BE(thread.getColor());
        writeInt8(0); //0 parts
        writeInt8(0); //0 length
        writeInt8(0);
        writeInt8(5); //Rayon
        writeInt8(40); //40 weight;
        if (thread.getCatalogNumber() != null) {
            vp3_write_string_8(thread.getCatalogNumber());
        } else {
            vp3_write_string_8("");
        }
        if (thread.getDescription() != null) {
            vp3_write_string_8(thread.getDescription());
        } else {
            vp3_write_string_8(thread.getHexColor());
        }
        if (thread.getBrand() != null) {
            vp3_write_string_8(thread.getBrand());
        } else {
            vp3_write_string_8("");
        }
    }

    private void write_stitches_block(Points stitches, double first_pos_x, double first_pos_y) throws IOException {
        writeInt8(0x00);
        writeInt8(0x01);
        writeInt8(0x00);
        int placeholder_distance_to_end_of_stitches_block_010 = tell();
        space_holder(4);

        writeInt8(0x0A);
        writeInt8(0xF6);
        writeInt8(0x00);
        double last_x = first_pos_x;
        double last_y = first_pos_y;
        for (int i = 0, ie = stitches.size(); i < ie; i++) {
            float x = stitches.getX(i);
            float y = stitches.getY(i);
            int flags = stitches.getData(i) & COMMAND_MASK;
            if (flags == END) {
                writeInt8(0x80);
                writeInt8(0x03);
                break;
            }
            switch (flags) {
                case COLOR_CHANGE:
                case TRIM:
                case STOP:
                case JUMP: //vp3.jump == vp3.stitch, combine.
                    continue;
            }
            int dx = (int) (x - last_x);
            int dy = (int) (y - last_y);
            last_x += dx;
            last_y += dy;
            if (flags != STITCH) {
                continue;
            }
            if ((-127 < dx) && (dx < 127)
                    && (-127 < dy) && (dy < 127)) {
                writeInt8(dx);
                writeInt8(dy);
            } else {
                writeInt8(0x80);
                writeInt8(0x01);
                writeInt16BE(dx);
                writeInt16BE(dy);
                writeInt8(0x80);
                writeInt8(0x02);
            }
            //VSM gave ending stitches as 80 03 35 A5, so, 80 03 isn't strictly end.
        }
        int current_pos = tell();
        writeSpaceHolder32BE(
                current_pos
                - placeholder_distance_to_end_of_stitches_block_010
                - 4
        );
    }

}
