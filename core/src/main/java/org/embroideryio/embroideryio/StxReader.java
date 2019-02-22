package org.embroideryio.embroideryio;

import java.io.IOException;

public class StxReader extends EmbReader {

    public static final String MIME = "application/x-stx";
    public static final String EXT = "stx";

    @Override
    public void read() throws IOException {
        //file starts with STX
        skip(0x0C);
        int color_start_position = readInt32LE();
        int dunno_block_start_position = readInt32LE();
        int stitch_start_position = readInt32LE();
        seek(stitch_start_position);
        ExpReader.read_exp_stitches(this);
    }
}
