package org.embroideryio.embroideryio;

import static org.embroideryio.embroideryio.EmbConstant.*;

import java.io.IOException;

public class XxxWriter extends EmbWriter {

    public XxxWriter() {
        super();
        settings.put(EmbEncoder.PROP_MAX_JUMP, (float) 124.0);
        settings.put(EmbEncoder.PROP_MAX_STITCH, (float) 124.0);
        settings.put(EmbEncoder.PROP_FULL_JUMP, true);
        settings.put(EmbEncoder.PROP_ROUND, true);
    }

    public void write_xxx_header() throws IOException {
        Points stitches = pattern.getStitches();
        for (int i = 0, ie = 0x17; i < ie; i++) {
            writeInt8(0x00);
        }
        int size = pattern.count_commands(STITCH, JUMP, TRIM, COLOR_CHANGE, STOP);
        writeInt32LE(size); //end is not a command.
        for (int i = 0, ie = 0x0C; i < ie; i++) {
            writeInt8(0x00);
        }
        writeInt32LE(pattern.threadlist.size());
        writeInt16LE(0x0000);

        float[] bounds = pattern.getBounds();
        double width = (bounds[2] - bounds[0]);
        double height = (bounds[3] - bounds[1]);

        writeInt16LE((int) width);
        writeInt16LE((int) height);

        int last = stitches.size() - 1;

        writeInt16LE((int) stitches.getX(last)); // correct
        writeInt16LE((int) stitches.getY(last)); // correct
        
        writeInt16LE((int) -bounds[0]);
        writeInt16LE((int) bounds[3]); 

        for (int i = 0, ie = 0x42; i < ie; i++) {
            writeInt8(0x00);
        }
        writeInt16LE(0x0000); //unknown
        writeInt16LE(0x0000); //unknown
        for (int i = 0, ie = 0x73; i < ie; i++) {
            writeInt8(0x00);
        }
        writeInt16LE(0x20); //unknown
        for (int i = 0, ie = 0x08; i < ie; i++) {
            writeInt8(0x00);
        }
    }
    
    public void write_xxx_stitches() throws IOException {
        double xx = 0, yy = 0;
        Points stitches = pattern.getStitches();
        for (int i = 0, ie = stitches.size(); i < ie; i++) {
            int data = stitches.getData(i) & COMMAND_MASK;
            float x = stitches.getX(i);
            float y = stitches.getY(i);
            int dx = (int) Math.rint(x - xx);
            int dy = (int) Math.rint(y - yy);
            xx += dx;
            yy += dy;
            switch (data) {
                case STOP:
                case COLOR_CHANGE:
                    writeInt8(0x7F);
                    writeInt8(0x08);
                    writeInt8(dx);
                    writeInt8(-dy);
                    continue;
                case END:
                    break;
                case TRIM:
                    writeInt8(0x7F);
                    writeInt8(0x03);
                    writeInt8(dx);
                    writeInt8(-dy);
                    continue;
                case JUMP: {
                    writeInt8(0x7F);
                    writeInt8(0x01);
                    writeInt8(dx);
                    writeInt8(-dy);
                    continue;
                }
                case STITCH:
                    if ((-124 < dx) && (dx < 124)
                            && (-124 < dy) && (dy < 124)) {
                        writeInt8(dx);
                        writeInt8(-dy);
                    } else {
                        writeInt8(0x7D);
                        writeInt16LE(dx);
                        writeInt16LE(-dy);
                    }
                    continue;
            }
            break;
        }
    }
    
    public void write_xxx_colors() throws IOException {
        writeInt8(0x00);
        writeInt8(0x00);
        int current_color = 0;
        for (EmbThread thread : pattern.threadlist) {
            writeInt8(0x00);
            writeInt8(thread.getRed());
            writeInt8(thread.getGreen());
            writeInt8(thread.getBlue());
            current_color += 1;
        }
        for (int i = 0, ie = 21 - current_color; i < ie; i++) {
            writeInt32LE(0x00000000);
        }
        writeInt32LE(0xffffff00);
        writeInt8(0x00);
        writeInt8(0x01);
    }
    
    @Override
    public void write() throws IOException {
        write_xxx_header();
        
        space_holder(4); //place_holder_for_end_of_stitches

        write_xxx_stitches();
        
        writeSpaceHolder32LE(tell());
        writeInt8(0x7F);
        writeInt8(0x7F);
        writeInt8(0x02);
        writeInt8(0x14);
        write_xxx_colors();
    }

}
