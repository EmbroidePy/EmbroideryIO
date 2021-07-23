package org.embroideryio.embroideryio;

import static org.embroideryio.embroideryio.EmbConstant.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

public class JefWriter extends EmbWriter {

    public JefWriter() {
        super();
        settings.put(EmbEncoder.PROP_MAX_JUMP, 127f);
        settings.put(EmbEncoder.PROP_MAX_STITCH, 127f);
        settings.put(EmbEncoder.PROP_FULL_JUMP, true);
        settings.put(EmbEncoder.PROP_ROUND, true);
    }

    public static final int HOOP_110X110 = 0;
    public static final int HOOP_50X50 = 1;
    public static final int HOOP_140X200 = 2;
    public static final int HOOP_126X110 = 3;
    public static final int HOOP_200X200 = 4;

    @Override
    public void write() throws IOException {
        boolean trims = getBoolean("trims", true);

        Date date = new Date();
        String date_string = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
        date_string = (String) get("date", date_string);

        Points stitches = pattern.getStitches();

        pattern.fixColorCount();
        
        EmbThreadJef[] jef_threads = EmbThreadJef.getThreadSet();
        int last_index = -1;
        EmbThread last_thread = null;
        ArrayList<Integer> palette = new ArrayList<>();
        
        boolean color_toggled = false;
        int color_count = 0;  // Color and Stop count.
        int index_in_threadlist = 0;
        stitches = pattern.getStitches();
        for (int i = 0, s = stitches.size(); i < s; i++) {
            //Iterate all stitches.
            int flags = stitches.getData(i) & COMMAND_MASK;
            if ((flags == COLOR_CHANGE) || (index_in_threadlist == 0)) {
                // If color change *or* initial color unset.
                EmbThread thread = pattern.threadlist.get(index_in_threadlist);
                index_in_threadlist += 1;
                color_count += 1;
                int index_of_jefthread = EmbThread.findNearestColorIndex(thread.getColor(), Arrays.asList(jef_threads));
                if ((last_index == index_of_jefthread) && (last_thread != thread)) {
                    //Last thread and current thread pigeonhole to same jefcolor.
                    //We set that thread to None. And get the second closest color.
                    EmbThreadJef repeated_thread = jef_threads[index_of_jefthread];
                    int repeated_index = index_of_jefthread;
                    jef_threads[index_of_jefthread] = null;
                    index_of_jefthread = EmbThread.findNearestColorIndex(thread.getColor(), Arrays.asList(jef_threads));
                    jef_threads[repeated_index] = repeated_thread;
                }
                palette.add(index_of_jefthread);
                last_index = index_of_jefthread;
                last_thread = thread;
                color_toggled = false;
            }
            if (flags == STOP) {
                color_count += 1;
                color_toggled = !color_toggled;
                if (color_toggled) {
                    palette.add(0);
                }
                else {
                    palette.add(last_index);
                }
            }
        }
        
        int offsets = 0x74 + (color_count * 8);
        writeInt32LE(offsets);
        writeInt32LE(0x14);
        write(date_string.getBytes());
        writeInt8(0x00);
        writeInt8(0x00);
        writeInt32LE(color_count);

        int command_count = 1; // 1 command for END;
        for (int i = 0, ie = stitches.size(); i < ie; i++) {
            int data = stitches.getData(i) & COMMAND_MASK;
            switch (data) {
                case STITCH:
                    command_count += 1;
                    continue;
                case STOP:
                case COLOR_CHANGE:
                    command_count += 2;
                    continue;
                case JUMP:
                    command_count += 2;
                    continue;
                case TRIM:
                    if (trims) {
                        command_count += 2;
                    }
                    continue;
                case END:
                    break;
            }
            break;
        }
        writeInt32LE(command_count);
        
        float[] bounds = pattern.getBounds();
        int design_width = (int) (bounds[2] - bounds[0]);
        int design_height = (int) (bounds[3] - bounds[1]);

        writeInt32LE(get_jef_hoop_size(design_width, design_height));

        int half_width = (int) Math.rint(design_width / 2.0);
        int half_height = (int) Math.rint(design_height / 2.0);
        /* Distance from center of Hoop */
        writeInt32LE(half_width); // left
        writeInt32LE(half_height); // top
        writeInt32LE(half_width); // right
        writeInt32LE(half_height); // bottom

        /* Distance from default 110 x 110 Hoop */
        int x_hoop_edge = 550 - half_width;
        int y_hoop_edge = 550 - half_height;
        write_hoop_edge_distance(x_hoop_edge, y_hoop_edge);

        /* Distance from default 50 x 50 Hoop */
        x_hoop_edge = 250 - half_width;
        y_hoop_edge = 250 - half_height;
        write_hoop_edge_distance(x_hoop_edge, y_hoop_edge);

        /* Distance from default 140 x 200 Hoop */
        x_hoop_edge = 700 - half_width;
        y_hoop_edge = 1000 - half_height;
        write_hoop_edge_distance(x_hoop_edge, y_hoop_edge);

        /* Distance from custom hoop, but this should be accepted.*/
        x_hoop_edge = 700 - half_width;
        y_hoop_edge = 1000 - half_height;
        write_hoop_edge_distance(x_hoop_edge, y_hoop_edge);

        for (Integer t : palette) {
            writeInt32LE(t);
        }
        
        for (int i = 0; i < color_count; i++) {
            writeInt32LE(0x0D);
        }

        double xx = 0, yy = 0;
        for (int i = 0, ie = stitches.size(); i < ie; i++) {
            int data = stitches.getData(i) & COMMAND_MASK;
            float x = stitches.getX(i);
            float y = stitches.getY(i);
            int dx = (int) Math.rint(x - xx);
            int dy = (int) Math.rint(y - yy);
            xx += dx;
            yy += dy;
            switch (data) {
                case STITCH:
                    writeInt8(dx);
                    writeInt8(-dy);
                    continue;
                case STOP:
                case COLOR_CHANGE:
                    writeInt8(0x80);
                    writeInt8(0x01);
                    writeInt8(dx);
                    writeInt8(-dy);
                    continue;
                case JUMP:
                    writeInt8(0x80);
                    writeInt8(0x02);
                    writeInt8(dx);
                    writeInt8(-dy);
                    continue;
                case TRIM:
                    if (trims) {
                        writeInt8(0x80);
                        writeInt8(0x02);
                        writeInt8(0);
                        writeInt8(0);
                    }
                    continue;
                case END:
                    break;
            }
            break;
        }
        writeInt8(0x80);
        writeInt8(0x10);
    }

    private void write_hoop_edge_distance(int x_hoop_edge, int y_hoop_edge) throws IOException {
        if (Math.min(x_hoop_edge, y_hoop_edge) >= 0) {
            writeInt32LE(x_hoop_edge);
            writeInt32LE(y_hoop_edge);
            writeInt32LE(x_hoop_edge);
            writeInt32LE(y_hoop_edge);
        } else {
            writeInt32LE(-1);
            writeInt32LE(-1);
            writeInt32LE(-1);
            writeInt32LE(-1);
        }
    }

    private static int get_jef_hoop_size(int width, int height) {
        if (width < 500 && height < 500) {
            return HOOP_50X50;
        }
        if (width < 1260 && height < 1100) {
            return HOOP_126X110;
        }
        if (width < 1400 && height < 2000) {
            return HOOP_140X200;
        }
        if (width < 2000 && height < 2000) {
            return HOOP_200X200;
        }
        return HOOP_110X110;
    }
}
