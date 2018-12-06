package org.embroideryio.embroideryio;

import static org.embroideryio.embroideryio.EmbConstant.*;

import java.io.IOException;

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
        Points stitches = pattern.getStitches();

        pattern.fixColorCount();
        int color_count = pattern.getThreadlist().size();
        byte b[] = new byte[4];

        int offsets = 0x74 + (color_count * 8);
        writeInt32(offsets);
        writeInt32(0x14);
        //TODO: replace this with valid time.
        //'%Y%m%d%H%M%S'
        write(String.format("20122017218088").getBytes());
        writeInt8(0x00);
        writeInt8(0x00);
        writeInt32(color_count);
        int point_count = 1; // 1 command for END;
        for (int i = 0, ie = stitches.size(); i < ie; i++) {
            int data = stitches.getData(i) % COMMAND_MASK;
            switch (data) {
                case STITCH:
                    point_count += 1;
                    continue;
                case JUMP:
                    point_count += 2;
                    break;
                case COLOR_CHANGE:
                    point_count += 2;
                    break;
                case END:
                    break;
            }
        }
        writeInt32(point_count);
        float[] bounds = pattern.getBounds();
        int design_width = (int) (bounds[2] - bounds[0]);
        int design_height = (int) (bounds[3] - bounds[1]);

        writeInt32(get_jef_hoop_size(design_width, design_height));

        int half_width = (int) Math.rint(design_width / 2.0);
        int half_height = (int) Math.rint(design_height / 2.0);
        /* Distance from center of Hoop */
        writeInt32(half_width); // left
        writeInt32(half_height); // top
        writeInt32(half_width); // right
        writeInt32(half_height); // bottom

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

        EmbThread[] threadSet = EmbThreadJef.getThreadSet();
        for (EmbThread thread : pattern.threadlist) {
            int thread_index = EmbThread.findNearestIndex(thread.color, threadSet);
            writeInt32(thread_index);
        }
        for (int i = 0; i < color_count; i++) {
            writeInt32(0x0D);
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
