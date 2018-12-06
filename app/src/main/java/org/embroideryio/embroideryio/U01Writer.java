package org.embroideryio.embroideryio;

import java.io.IOException;

import static org.embroideryio.embroideryio.EmbConstant.*;

public class U01Writer extends EmbWriter {

    public static final String MIME = "application/x-u01";
    public static final String EXT = "u01";

    private final static int COMMANDSIZE = 3;

    public U01Writer() {
        super();
        settings.put(EmbEncoder.PROP_MAX_JUMP, (float) 127);
        settings.put(EmbEncoder.PROP_MAX_STITCH, (float) 127);
        settings.put(EmbEncoder.PROP_SEQUIN_CONTINGENCY, CONTINGENCY_SEQUIN_JUMP);
        settings.put(EmbEncoder.PROP_WRITES_SPEED, true);
        settings.put(EmbEncoder.PROP_FULL_JUMP, false);
        settings.put(EmbEncoder.PROP_ROUND, true);
        settings.put(EmbEncoder.PROP_THREAD_CHANGE_COMMAND, NEEDLE_SET);
    }

    @Override
    public void write() throws IOException {
        Points stitches = pattern.getStitches();
        int stitch_count = stitches.size();
        for (int i = 0, ie = 0x80; i < ie; i++) {
            writeInt8(0);
        }
        if (stitch_count == 0) {
            return;
        }
        writeInt16LE((int) pattern.getMinX());
        writeInt16LE(-(int) pattern.getMaxY());
        writeInt16LE((int) pattern.getMaxX());
        writeInt16LE(-(int) pattern.getMinY());
        writeInt32LE(0);

        writeInt32LE(stitch_count + 1);
        writeInt16LE((int) stitches.getX(stitch_count - 1));
        writeInt16LE(-(int) stitches.getY(stitch_count - 1));

        for (int i = tell(), ie = 0x100; i < ie; i++) {
            writeInt8(0);
        }
        boolean trigger_fast = false;
        boolean trigger_slow = false;

        byte[] command = new byte[COMMANDSIZE];
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
                case SLOW:
                    trigger_slow = true;
                    continue;
                case FAST:
                    trigger_fast = true;
                    continue;
            }
            int cmd = 0x80;
            if (dy >= 0) {
                cmd |= 0x40;
            }
            if (dx <= 0) {
                cmd |= 0x20;
            }
            int delta_x = Math.abs(dx);
            int delta_y = Math.abs(dy);
            switch (data) {
                case STITCH:
                    if (trigger_fast) {
                        cmd |= 0x02;
                    }
                    if (trigger_slow) {
                        cmd |= 0x04;
                    }
                    command[0] = (byte) cmd;
                    command[1] = (byte) delta_y;
                    command[2] = (byte) delta_x;
                    write(command);
                    continue;
                case JUMP:
                    if (trigger_fast) {
                        cmd |= 0x02;
                    }
                    if (trigger_slow) {
                        cmd |= 0x04;
                    }
                    cmd |= 0x01;
                    command[0] = (byte) cmd;
                    command[1] = (byte) delta_y;
                    command[2] = (byte) delta_x;
                    write(command);
                    continue;
                case STOP:
                    cmd |= 0x08;
                    command[0] = (byte) cmd;
                    command[1] = (byte) delta_y;
                    command[2] = (byte) delta_x;
                    write(command);
                    continue;
                case TRIM:
                    cmd |= 0x07;
                    command[0] = (byte) cmd;
                    command[1] = (byte) delta_y;
                    command[2] = (byte) delta_x;
                    write(command);
                    continue;
                case NEEDLE_SET:
                    int needle = stitches.getData(i) & NEEDLE_MASK;
                    needle <<= 16;
                    if (needle >= 15) {
                        needle = (needle % 15) + 1;
                    }
                    cmd |= 0x08;
                    cmd += needle;
                    command[0] = (byte) cmd;
                    command[1] = (byte) delta_y;
                    command[2] = (byte) delta_x;
                    write(command);
                    continue;
                case END:
                    command[0] = (byte) 0xF8;
                    command[1] = (byte) 0;
                    command[2] = (byte) 0;
                    write(command);
                    break;
            }
            break;
        }
    }
}
