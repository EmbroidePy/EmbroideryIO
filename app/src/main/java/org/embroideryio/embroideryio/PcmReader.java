package org.embroideryio.embroideryio;

import java.io.IOException;

public class PcmReader extends EmbReader {

    private static final float PC_SIZE_CONVERSION_RATIO = 5f / 3f;

    void read_pc_file() throws IOException {
        EmbThread[] pcm_thread = new EmbThread[]{
            new EmbThread(0x000000, "PCM Color 1"),
            new EmbThread(0x000080, "PCM Color 2"),
            new EmbThread(0x0000FF, "PCM Color 3"),
            new EmbThread(0x008080, "PCM Color 4"),
            new EmbThread(0x00FFFF, "PCM Color 5"),
            new EmbThread(0x800080, "PCM Color 6"),
            new EmbThread(0xFF00FF, "PCM Color 7"),
            new EmbThread(0x800000, "PCM Color 8"),
            new EmbThread(0xFF0000, "PCM Color 9"),
            new EmbThread(0x008000, "PCM Color 10"),
            new EmbThread(0x00FF00, "PCM Color 11"),
            new EmbThread(0x808000, "PCM Color 12"),
            new EmbThread(0xFFFF00, "PCM Color 13"),
            new EmbThread(0x808080, "PCM Color 14"),
            new EmbThread(0xC0C0C0, "PCM Color 15"),
            new EmbThread(0xFFFFFF, "PCM Color 16"),};
        skip(2);
        int colors = readInt16BE();
        for (int i = 0; i < colors; i++) {
            int color_index = readInt16BE();
            EmbThread thread = pcm_thread[color_index];
            pattern.add(thread);
        }

        int stitch_count = readInt16BE();
        while (true) {
            int x = signed24(readInt24BE());
            int c0 = readInt8();
            int y = signed24(readInt24BE());
            int c1 = readInt8();
            int ctrl = readInt8();
            if (ctrl == Integer.MIN_VALUE) {
                break;
            }
            x *= PC_SIZE_CONVERSION_RATIO;
            y *= -PC_SIZE_CONVERSION_RATIO;
            if (ctrl == 0x00) {
                pattern.stitchAbs(x, y);
                continue;
            }
            if ((ctrl & 0x01) != 0) {
                pattern.color_change();
                continue;
            }
            if ((ctrl & 0x04) != 0) {
                pattern.moveAbs(x, y);
                continue;
            }
            break;
        }
        pattern.end();
    }

    @Override
    protected void read() throws IOException {
        read_pc_file();
    }

}
