package org.embroideryio.embroideryio;

import java.io.IOException;

public class PhbReader extends PecReader {

    @Override
    public void read() throws IOException {
        int file_offset = 0x52;
        seek(0x54);
        file_offset += readInt32LE();

        seek(0x71);
        int color_count = readInt16LE();
        EmbThread[] threadset = EmbThreadPec.getThreadSet();
        for (int i = 0; i < color_count; i++) {
            pattern.add(threadset[readInt8() % threadset.length]);
        }
        seek(file_offset);
        file_offset += readInt32LE() + 2;
        seek(file_offset);
        file_offset += readInt32LE();
        seek(file_offset + 14);
        int color_count2 = readInt8();
        skip(color_count2 + 0x15);
        readPecStitches();
    }

}
