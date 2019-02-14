package org.embroideryio.embroideryio;

import java.io.IOException;

public class PhcReader extends PecReader {

    @Override
    public void read() throws IOException {
        seek(0x4A);
        int pec_graphic_icon_height = readInt8();
        skip(1);
        int pec_graphic_byte_stride = readInt8();
        int color_count = readInt16LE();
        EmbThreadPec[] threadset = EmbThreadPec.getThreadSet();
        for (int i = 0; i < color_count; i++) {
            int color_index = readInt8();
            pattern.add(threadset[color_index % threadset.length]);
        }
        int byte_size = pec_graphic_byte_stride * pec_graphic_icon_height;
        readPecGraphics(
                byte_size,
                pec_graphic_byte_stride,
                color_count
        );
        seek(0x2b); //TODO: Read this in correct order, not using -skip.
        int pec_add = readInt8();
        skip(4);
        int pec_offset = readInt16LE();
        seek(pec_offset + pec_add);
        int bytes_in_section = readInt16LE();
        skip(bytes_in_section);
        int bytes_in_section2 = readInt32LE();
        skip(bytes_in_section2 + 10);
        int color_count2 = readInt8();
        skip(color_count2 + 0x1D);
        this.readPecStitches();
    }

}
