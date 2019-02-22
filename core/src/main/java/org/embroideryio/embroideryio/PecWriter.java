package org.embroideryio.embroideryio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.embroideryio.embroideryio.EmbConstant.*;

public class PecWriter extends EmbWriter {

    static final int MASK_07_BIT = 0b01111111;
    static final int JUMP_CODE = 0b00010000;
    static final int TRIM_CODE = 0b00100000;
    static final int FLAG_LONG = 0b10000000;

    static final int PEC_ICON_WIDTH = 48;
    static final int PEC_ICON_HEIGHT = 38;

    public PecWriter() {
        super();
        settings.put(EmbEncoder.PROP_MAX_JUMP, 2047f);
        settings.put(EmbEncoder.PROP_MAX_STITCH, 2047f);
        settings.put(EmbEncoder.PROP_FULL_JUMP, true);
        settings.put(EmbEncoder.PROP_ROUND, true);
    }

    @Override
    public void write() throws IOException {
        write("#PEC0001");
        write_pec(null);
    }

    public Object[] write_pec(ArrayList<EmbThread> threadlist) throws IOException {
        Object[] color_info;
        if (threadlist == null) {
            pattern.fixColorCount();
            threadlist = pattern.threadlist;
            color_info = write_pec_header(threadlist);
        } else {
            color_info = write_pec_header(threadlist);
        }
        write_pec_block();
        write_pec_graphics();
        return color_info;
    }

    public Object[] write_pec_header(ArrayList<EmbThread> threadlist) throws IOException {
        String name = pattern.getMetadata(EmbPattern.PROP_NAME, "Untitled");
        name = name.substring(0, 8);
        write(String.format(Locale.ENGLISH, "LA:%-16s\r", name).getBytes());
        for (int i = 0; i < 12; i++) {
            writeInt8(0x20);
        }
        writeInt8(0xFF);
        writeInt8(0x00);

        writeInt8(PEC_ICON_WIDTH / 8);
        writeInt8(PEC_ICON_HEIGHT);

        EmbThreadPec[] threadSet = EmbThreadPec.getThreadSet();
        if (threadSet.length > threadlist.size()) {
            threadlist = new ArrayList<>();
            threadlist.addAll(Arrays.asList(threadSet));
            // Data is likely corrupt. Cheat so it won't crash.
        }

        final EmbThread[] chart = new EmbThread[threadSet.length];

        List<EmbThread> threads = pattern.getUniqueThreadList();
        for (EmbThread thread : threads) {
            int index = EmbThreadPec.findNearestIndex(thread.getColor(), threadSet);
            threadSet[index] = null;
            chart[index] = thread;
        }

        ArrayList<Integer> color_index_list = new ArrayList<>();
        ArrayList<Integer> rgb_list = new ArrayList<>();
        for (EmbThread thread : pattern.threadlist) {
            color_index_list.add(EmbThread.findNearestIndex(thread.color, chart));
            rgb_list.add(thread.color);
        }
        int current_thread_count = color_index_list.size();
        if (current_thread_count != 0) {
            for (int i = 0; i < 12; i++) {
                writeInt8(0x20);
            }
            int add_value = current_thread_count - 1;
            color_index_list.add(0, add_value);
            for (int i = 0, ie = color_index_list.size(); i < ie; i++) {
                writeInt8(color_index_list.get(i));
            }
        } else {
            writeInt8(0x20);
            writeInt8(0x20);
            writeInt8(0x20);
            writeInt8(0x20);
            writeInt8(0x64);
            writeInt8(0x20);
            writeInt8(0x00);
            writeInt8(0x20);
            writeInt8(0x00);
            writeInt8(0x20);
            writeInt8(0x20);
            writeInt8(0x20);
            writeInt8(0xFF);
        }
        for (int i = 0; i < (463 - current_thread_count); i++) {
            writeInt8(0x20);
        } //520
        return new Object[]{color_index_list, rgb_list};
    }

    void write_pec_block() throws IOException {
        int width = (int) Math.rint(pattern.getWidth());
        int height = (int) Math.rint(pattern.getHeight());
        int stitch_block_start_position = tell();
        writeInt8(0x00);
        writeInt8(0x00);
        space_holder(3);

        writeInt8(0x31);
        writeInt8(0xFF);
        writeInt8(0xF0);
        /* write 2 byte x size */
        writeInt16LE((short) Math.round(width));
        /* write 2 byte y size */
        writeInt16LE((short) Math.round(height));

        /* Write 4 miscellaneous int16's */
        writeInt16LE((short) 0x1E0);
        writeInt16LE((short) 0x1B0);

        writeInt16BE((0x9000 | -Math.round(pattern.getMinX())));
        writeInt16BE((0x9000 | -Math.round(pattern.getMinY())));

        pec_encode();

        int stitch_block_length = tell() - stitch_block_start_position;
        writeSpaceHolder24LE(stitch_block_length);
    }

    void write_pec_graphics() throws IOException {
        float minX = pattern.getMinX();
        float minY = pattern.getMinY();
        float maxX = pattern.getMaxX();
        float maxY = pattern.getMaxY();
        PecGraphics graphics = new PecGraphics(minX, minY, maxX, maxY, PEC_ICON_WIDTH, PEC_ICON_HEIGHT);

        for (StitchBlock object : pattern.asStitchBlock()) {
            graphics.draw(object.getPoints());
        }
        write(graphics.getGraphics());
        graphics.clear();

        int lastcolor = 0;
        for (StitchBlock layer : pattern.asStitchBlock()) {
            int currentcolor = layer.getThread().getColor();
            if ((lastcolor != 0) && (currentcolor != lastcolor)) {
                write(graphics.getGraphics());
                graphics.clear();
            }
            graphics.draw(layer.getPoints());
            lastcolor = currentcolor;
        }
        write(graphics.getGraphics());
    }

    public static int encode_long_form(int value) {
        value &= 0b00001111_11111111;
        value |= 0b10000000_00000000;
        return value;
    }

    public static int flagJump(int longForm) {
        return longForm | (JUMP_CODE << 8);
    }

    public static int flagTrim(int longForm) {
        return longForm | (TRIM_CODE << 8);
    }

    private void pec_encode() throws IOException {
        boolean color_two = true;
        Points stitches = pattern.getStitches();
        int dx, dy;
        boolean jumping = false;
        double xx = 0, yy = 0;
        for (int i = 0, ie = stitches.size(); i < ie; i++) {
            int data = stitches.getData(i) & COMMAND_MASK;
            float x = stitches.getX(i);
            float y = stitches.getY(i);
            dx = (int) Math.rint(x - xx);
            dy = (int) Math.rint(y - yy);
            xx += dx;
            yy += dy;
            switch (data) {
                case STITCH:
                    if ((jumping) && (dx != 0) && (dy != 0)) {
                        writeInt8((byte) 0x00);
                        writeInt8((byte) 0x00);
                        jumping = false;
                    }
                    if (dx < 63 && dx > -64 && dy < 63 && dy > -64) {
                        writeInt8(dx & MASK_07_BIT);
                        writeInt8(dy & MASK_07_BIT);
                    } else {
                        dx = encode_long_form(dx);
                        dy = encode_long_form(dy);
                        writeInt16BE(dx);
                        writeInt16BE(dy);
                    }
                    continue;
                case JUMP:
                    jumping = true;
                    dx = encode_long_form(dx);
                    dx = flagTrim(dx);
                    dy = encode_long_form(dy);
                    dy = flagTrim(dy);
                    writeInt16BE(dx);
                    writeInt16BE(dy);
                    continue;
                case COLOR_CHANGE:
                    if (jumping) {
                        writeInt8((byte) 0x00);
                        writeInt8((byte) 0x00);
                        jumping = false;
                    }
                    writeInt8(0xfe);
                    writeInt8(0xb0);
                    writeInt8((color_two) ? 2 : 1);
                    color_two = !color_two;
                    continue;
                case TRIM:
                    continue;
                case STOP:
                    continue;
                case END:
                    writeInt8(0xff);
                    break;
            }
            break;
        }
    }
}
