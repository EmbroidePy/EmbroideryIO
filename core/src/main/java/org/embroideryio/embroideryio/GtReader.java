package org.embroideryio.embroideryio;

import java.io.IOException;

public class GtReader extends EmbReader {

    public static final String MIME = "application/x-gt";
    public static final String EXT = "gt";

    @Override
    public void read() throws IOException {
        skip(0x200);
        DszReader.z_stitch_encoding_read(this);
    }
}
