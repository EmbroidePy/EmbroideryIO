package org.embroideryio.embroideryio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.embroideryio.embroideryio.EmbConstant.*;

public class PesWriter extends PecWriter {

    public static final String PROP_PES_VERSION = "pes version";
    public static final String PROP_TRUNCATED = "truncated";

    static final int VERSION_1 = 1;
    static final int VERSION_6 = 6;

    static final String PES_VERSION_1_SIGNATURE = "#PES0001";
    static final String PES_VERSION_6_SIGNATURE = "#PES0060";

    static final String EMB_ONE = "CEmbOne";
    static final String EMB_SEG = "CSewSeg";

    @Override
    public void write() throws IOException {
        int version = getInt(PROP_PES_VERSION, VERSION_1);
        boolean truncated = getBoolean(PROP_TRUNCATED, false);

        if (truncated) {
            switch (version) {
                case VERSION_1:
                    write_truncated_version_1();
                    break;
                case VERSION_6:
                    write_truncated_version_6();
                    break;
            }
        } else {
            switch (version) {
                case VERSION_1:
                    write_version_1();
                    break;
                case VERSION_6:
                    write_version_6();
                    break;
            }
        }
    }

    void write_truncated_version_1() throws IOException {
        write(PES_VERSION_1_SIGNATURE);
        writeInt8(0x16);
        for (int i = 0; i < 13; i++) {
            writeInt8(0x00);
        }
        write_pec(null);
    }

    void write_truncated_version_6() throws IOException {
        ArrayList<EmbThread> chart = new ArrayList<>();
        chart.addAll(Arrays.asList(EmbThreadPec.getThreadSet()));
        write(PES_VERSION_6_SIGNATURE);
        space_holder(4);
        write_pes_header_v6(chart, 0);
        for (int i = 0; i < 5; i++) {
            writeInt8(0x00);
        }
        writeInt16LE(0x0000);
        writeInt16LE(0x0000);
        int current_position = tell();
        writeSpaceHolder32LE(current_position);
        Object[] color_info = write_pec(null);
        write_pes_addendum(color_info);
        writeInt16LE(0x0000); //found v6, not 5,4
    }

    void write_version_1() throws IOException {
        ArrayList<EmbThread> chart = new ArrayList<>();
        chart.addAll(Arrays.asList(EmbThreadPec.getThreadSet()));
        write(PES_VERSION_1_SIGNATURE);

        float pattern_left = pattern.getMinX();
        float pattern_top = pattern.getMinY();
        float pattern_right = pattern.getMaxX();
        float pattern_bottom = pattern.getMaxY();

        float cx = ((pattern_left + pattern_right) / 2);
        float cy = ((pattern_top + pattern_bottom) / 2);

        float left = pattern_left - cx;
        float top = pattern_top - cy;
        float right = pattern_right - cx;
        float bottom = pattern_bottom - cy;

        int placeholder_pec_block = tell();
        space_holder(4);

        if (pattern.getStitches().size() == 0) {
            write_pes_header_v1(0);
            writeInt16LE(0x0000);
            writeInt16LE(0x0000);
        } else {
            write_pes_header_v1(1);
            writeInt16LE(0xFFFF);
            writeInt16LE(0x0000);
            write_pes_blocks(chart, left, top, right, bottom, cx, cy);
        }
        writeSpaceHolder32LE(tell());
        write_pec(null);
    }

    void write_version_6() throws IOException {
        pattern.fixColorCount();
        ArrayList<EmbThread> chart = pattern.threadlist;
        write(PES_VERSION_6_SIGNATURE);

        float pattern_left = pattern.getMinX();
        float pattern_top = pattern.getMinY();
        float pattern_right = pattern.getMaxX();
        float pattern_bottom = pattern.getMaxY();

        float cx = ((pattern_left + pattern_right) / 2);
        float cy = ((pattern_top + pattern_bottom) / 2);

        float left = pattern_left - cx;
        float top = pattern_top - cy;
        float right = pattern_right - cx;
        float bottom = pattern_bottom - cy;

        int placeholder_pec_block = tell();
        space_holder(4);

        if (pattern.getStitches().size() == 0) {
            write_pes_header_v6(chart, 0);
            writeInt16LE(0x0000);
            writeInt16LE(0x0000);
        } else {
            write_pes_header_v6(chart, 1);
            writeInt16LE(0xFFFF);
            writeInt16LE(0x0000);
            ArrayList<Integer> log = write_pes_blocks(chart, left, top, right, bottom, cx, cy);
            //In version 6 there is some node, tree, order thing.
            writeInt32LE(0);
            writeInt32LE(0);
            for (int i = 0, ie = log.size(); i < ie; i++) {
                writeInt32LE(i);
                writeInt32LE(0);
            }
        }
        writeSpaceHolder32LE(tell());
        Object[] color_info = write_pec(null);
        write_pes_addendum(color_info);
        writeInt16LE(0x0000); //found v6, not 5,4

    }

    public void write_pes_header_v1(int distinctBlockObjects) throws IOException {
        writeInt16LE(0x01); //1 is scale to fit.
        writeInt16LE(0x01); // 0 = 100x100 else 130x180 or above
        writeInt16LE(distinctBlockObjects);//number of distinct blocks
    }

    public void write_pes_header_v6(ArrayList<EmbThread> chart, int distinctBlockObjects) throws IOException {
        writeInt16LE(0x01); // 0 = 100x100 else 130x180 or above
        writeInt8(0x30);
        writeInt8(0x32);
        String name = "untitled";
        String pattern_name = pattern.getName();
        if ((pattern_name != null) && (pattern_name.length() > 0)) {
            name = pattern_name;
        }
        writePesString8(name);
        writePesString8(pattern.getCategory());
        writePesString8(pattern.getAuthor());
        writePesString8(pattern.getKeywords());
        writePesString8(pattern.getComments());

        writeInt16LE(0);//boolean optimizeHoopChange = (readInt16LE() == 1);

        writeInt16LE(0);//boolean designPageIsCustom = (readInt16LE() == 1);

        writeInt16LE(0x64); //hoopwidth
        writeInt16LE(0x64); //hoopheight
        writeInt16LE(0);// 1 means "UseExistingDesignArea" 0 means "Design Page Area"        

        writeInt16LE(0xC8);//int designWidth = readInt16LE();
        writeInt16LE(0xC8);//int designHeight = readInt16LE();
        writeInt16LE(0x64);//int designPageSectionWidth = readInt16LE();
        writeInt16LE(0x64);//int designPageSectionHeight = readInt16LE();
        writeInt16LE(0x64);//int p6 = readInt16LE(); // 100

        writeInt16LE(0x07);//int designPageBackgroundColor = readInt16LE();
        writeInt16LE(0x13);//int designPageForegroundColor = readInt16LE();
        writeInt16LE(0x01); //boolean ShowGrid = (readInt16LE() == 1);
        writeInt16LE(0x01);//boolean WithAxes = (readInt16LE() == 1);
        writeInt16LE(0x00);//boolean SnapToGrid = (readInt16LE() == 1);
        writeInt16LE(100);//int GridInterval = readInt16LE();

        writeInt16LE(0x01);//int p9 = readInt16LE(); // curves?
        writeInt16LE(0x00);//boolean OptimizeEntryExitPoints = (readInt16LE() == 1);

        writeInt8(0);//int fromImageStringLength = readInt8();
        //String FromImageFilename = readString(fromImageStringLength);

        writeInt32LE(Float.floatToIntBits(1f));
        writeInt32LE(Float.floatToIntBits(0f));
        writeInt32LE(Float.floatToIntBits(0f));
        writeInt32LE(Float.floatToIntBits(1f));
        writeInt32LE(Float.floatToIntBits(0f));
        writeInt32LE(Float.floatToIntBits(0f));
        writeInt16LE(0);//int numberOfProgrammableFillPatterns = readInt16LE();
        writeInt16LE(0);//int numberOfMotifPatterns = readInt16LE();
        writeInt16LE(0);//int featherPatternCount = readInt16LE();

        writeInt16LE(chart.size());//int numberOfColors = readInt16LE();
        for (EmbThread t : chart) {
            write_pes_thread(t);
        }
        writeInt16LE(distinctBlockObjects);//number of distinct blocks
    }

    void write_pes_addendum(Object[] color_info) throws IOException {
        ArrayList<Integer> color_index_list = (ArrayList<Integer>) color_info[0];
        ArrayList<Integer> rgb_list = (ArrayList<Integer>) color_info[1];
        int count = color_index_list.size();
        for (int i = 0, ie = count; i < ie; i++) {
            writeInt8(color_index_list.get(i));
        }
        for (int i = count, ie = 128 - count; i < ie; i++) {
            writeInt8(0x20);
        }

        for (int s = 0, se = rgb_list.size(); s < se; s++) {
            for (int i = 0, ie = 0x90; i < ie; i++) {
                writeInt8(0x00);
            }
        }
        for (int s = 0, se = rgb_list.size(); s < se; s++) {
            writeInt24LE(rgb_list.get(s));
        }
    }

    public void writePesString8(String string) throws IOException {
        if (string == null) {
            writeInt8(0);
            return;
        }
        if (string.length() > 255) {
            string = string.substring(0, 255);
        }
        writeInt8(string.length());
        write(string.getBytes());
    }

    public void writePesString16(String string) throws IOException {
        writeInt16LE(string.length());
        write(string.getBytes());
    }

    public void write_pes_thread(EmbThread thread) throws IOException {
        writePesString8(thread.getCatalogNumber());
        writeInt8(thread.getRed());
        writeInt8(thread.getGreen());
        writeInt8(thread.getBlue());
        writeInt8(0); //unknown
        writeInt32LE(0xA);
        writePesString8(thread.getDescription());
        writePesString8(thread.getBrand());
        writePesString8(thread.getChart());
    }

    public ArrayList<Integer> write_pes_blocks(ArrayList<EmbThread> chart, float left, float top, float right, float bottom, float cx, float cy) throws IOException {
        if (pattern.getStitches().size() == 0) {
            return null;
        }
        writePesString16(EMB_ONE);
        write_pes_sewsegheader(left, top, right, bottom);
        space_holder(2);
        writeInt16LE(0xFFFF);
        writeInt16LE(0x0000); //FFFF0000 means more blocks exist.
        writePesString16(EMB_SEG);
        Object[] data = write_pes_embsewseg_segments(chart, left, bottom, cx, cy);
        Integer sections = (Integer) data[0];
        ArrayList<Integer> colorlog = (ArrayList<Integer>) data[1];
        writeSpaceHolder16LE(sections);
        return colorlog;
    }

    public Object[] write_pes_embsewseg_segments(ArrayList<EmbThread> chart, float left, float bottom, float cx, float cy) throws IOException {
        ArrayList<Integer> segment = new ArrayList<>();
        ArrayList<Integer> colorlog = new ArrayList<>();
        int section = 0;
        int flag = -1;

        int mode;
        int adjust_x = (int) (left + cx);
        int adjust_y = (int) (bottom + cy);
        Points points = pattern.getStitches();
        int colorIndex = 0;
        int colorCode = 0;
        EmbThread currentThread;
        if (pattern.getThreadCount() != 0) {
            currentThread = pattern.getThread(colorIndex++);
            colorCode = EmbThread.findNearestColorIndex(currentThread.color, chart);
            colorlog.add(section);
            colorlog.add(colorCode);
        }
        float lastx = 0, lasty = 0;
        float x, y;
        for (int i = 0, ie = points.size(); i < ie; i++) {
            mode = points.getData(i) & COMMAND_MASK;
            if ((mode != END) && (flag != -1)) {
                writeSectionEnd();
            }
            switch (mode) {
                case JUMP:
                    x = lastx;
                    y = lasty;
                    segment.add((int) (x - adjust_x));
                    segment.add((int) (y - adjust_y));
                    while (i < ie && mode == (points.getData(i) & COMMAND_MASK)) {
                        i++;
                    }
                    i--;
                    x = points.getX(i);
                    y = points.getY(i);
                    segment.add((int) (x - adjust_x));
                    segment.add((int) (y - adjust_y));
                    flag = 1;
                    break;
                case COLOR_CHANGE:
                    currentThread = pattern.getThread(colorIndex++);
                    colorCode = EmbThread.findNearestColorIndex(currentThread.color, chart);
                    colorlog.add(section);
                    colorlog.add(colorCode);
                    flag = 1;
                    break;
                case STITCH:
                    while (i < ie && mode == (points.getData(i) & COMMAND_MASK)) {
                        lastx = points.getX(i);
                        lasty = points.getY(i);
                        x = lastx;
                        y = lasty;
                        segment.add((int) (x - adjust_x));
                        segment.add((int) (y - adjust_y));
                        i++;
                    }
                    i--;
                    flag = 0;
                    break;
            }
            if (segment.size() != 0) {
                writeInt16LE(flag);
                writeInt16LE((short) colorCode);
                writeInt16LE((short) segment.size() / 2);
                for (Integer v : segment) {
                    writeInt16LE(v);
                }
                section++;
            } else {
                flag = -1;
            }
            segment.clear();
        }
        int count = colorlog.size() / 2;
        writeInt16LE(count);
        for (Integer v : colorlog) {
            writeInt16LE(v);
        }
        writeInt16LE(0x0000);
        writeInt16LE(0x0000);
        return new Object[]{section, colorlog};
    }

    public int write_pes_sewsegheader(float left, float top, float right, float bottom) throws IOException {
        float height = bottom - top;
        float width = right - left;
        int hoopHeight = 1800, hoopWidth = 1300;
        writeInt16LE(0);  //writeInt16LE((int) bounds.left);
        writeInt16LE(0);  //writeInt16LE((int) bounds.top);
        writeInt16LE(0);  //writeInt16LE((int) bounds.right);
        writeInt16LE(0);  //writeInt16LE((int) bounds.bottom);
        writeInt16LE(0);  //writeInt16LE((int) bounds.left);
        writeInt16LE(0);  //writeInt16LE((int) bounds.top);
        writeInt16LE(0);  //writeInt16LE((int) bounds.right);
        writeInt16LE(0);  //writeInt16LE((int) bounds.bottom);
        float transX = 0;
        float transY = 0;
        transX += 350f;
        transY += 100f + height;
        transX += hoopWidth / 2;
        transY += hoopHeight / 2;
        transX += -width / 2;
        transY += -height / 2;
        writeInt32LE(Float.floatToIntBits(1f));
        writeInt32LE(Float.floatToIntBits(0f));
        writeInt32LE(Float.floatToIntBits(0f));
        writeInt32LE(Float.floatToIntBits(1f));
        writeInt32LE(Float.floatToIntBits(transX));
        writeInt32LE(Float.floatToIntBits(transY));
        writeInt16LE(1);
        writeInt16LE(0);
        writeInt16LE(0);
        writeInt16LE((short) width);
        writeInt16LE((short) height);
        writeInt32LE(0);
        writeInt32LE(0);
        return tell();
    }

    private void writePosition(float left, float top, float right, float bottom, float x, float y) throws IOException {
        writeInt16LE((short) (x - left));
        writeInt16LE((short) (y - bottom));
    }

    private void writeSectionEnd() throws IOException {
        writeInt16LE(0x8003);
    }

}
