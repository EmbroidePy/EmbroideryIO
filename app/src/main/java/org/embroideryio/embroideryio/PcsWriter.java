package org.embroideryio.embroideryio;

import java.io.IOException;

import static org.embroideryio.embroideryio.EmbConstant.*;

public class PcsWriter extends EmbWriter {

    private static final double R = 5d / 3d;

    void pcsEncode(int dx, int dy, int flags) throws IOException {
        if ((flags & COMMAND_MASK) == COLOR_CHANGE) {
            writeInt8(0);
            writeInt24LE(0);
            writeInt8(0);
            writeInt24LE(0);
            writeInt8(0x01);
            if ((dx == 0) && (dy == 0)) return;
            flags = STITCH;
        }
        int flagsToWrite = 0;
        writeInt8(0);
        writeInt24LE(dx);
        writeInt8(0);
        writeInt24LE(-dy);
        switch ((flags & COMMAND_MASK)) {
            case COLOR_CHANGE:
                flagsToWrite |= 0x01;
                break;
            case TRIM:
                flagsToWrite |= 0x04;
                break;
        }
        writeInt8(flagsToWrite);
    }

    @Override
    public void write() throws IOException {
        pattern.fixColorCount();
        writeInt8(0x32);
        writeInt8(3);/* hoop size defaulting to Large PCS hoop */
        int colorCount = pattern.getThreadCount();
        writeInt16LE((colorCount < 16) ? 16 : colorCount); //I do not know if the format is limited at 16 explictly.
        for (EmbThread thread : pattern.getThreadlist()) {
            writeInt8(thread.getRed());
            writeInt8(thread.getGreen());
            writeInt8(thread.getBlue());
            writeInt8(0);
        }
        for (; colorCount < 16; colorCount++) {
            writeInt32(0);/* write remaining colors to reach 16 */
        }
        /* write stitches */
        Points stitches = pattern.getStitches();
        double xx = 0.0, yy = 0.0;
        writeInt16LE(stitches.size());
        for (int i = 0, ie = stitches.size(); i < ie; i++) {
            pcsEncode((int) (R * stitches.getX(i)), (int) (R * stitches.getY(i)), stitches.getData(i));
        }
    }

}
