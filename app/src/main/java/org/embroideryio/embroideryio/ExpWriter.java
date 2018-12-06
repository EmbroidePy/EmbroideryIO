package org.embroideryio.embroideryio;

import static org.embroideryio.embroideryio.EmbConstant.*;

import java.io.IOException;

public class ExpWriter extends EmbWriter {

    public ExpWriter() {
        super();
        settings.put(EmbEncoder.PROP_MAX_JUMP, 127f);
        settings.put(EmbEncoder.PROP_MAX_STITCH, 127f);
        settings.put(EmbEncoder.PROP_FULL_JUMP, true);
        settings.put(EmbEncoder.PROP_ROUND, true);
    }

    @Override
    public void write() throws IOException {
        Points stitches = pattern.getStitches();
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
                case STITCH: {
                    int deltaX = dx & 0xFF;
                    int deltaY = (-dy) & 0xFF;
                    stream.write(deltaX);
                    stream.write(deltaY);
                    break;
                }
                case JUMP: {
                    int deltaX = dx & 0xFF;
                    int deltaY = (-dy) & 0xFF;
                    stream.write((byte) 0x80);
                    stream.write((byte) 0x04);
                    stream.write((byte) deltaX);
                    stream.write((byte) deltaY);
                    break;
                }
                case TRIM:
                    stream.write(0x80);
                    stream.write(0x80);
                    stream.write(0x07);
                    stream.write(0x00);
                    break;
                case COLOR_CHANGE:
                    stream.write((byte) 0x80);
                    stream.write((byte) 0x01);
                    stream.write((byte) 0x00);
                    stream.write((byte) 0x00);
                    break;
                case STOP:
                    stream.write((byte) 0x80);
                    stream.write((byte) 0x01);
                    stream.write((byte) 0x00);
                    stream.write((byte) 0x00);
                    break;
                case END:
                    break;
            }
        }
    }

}
