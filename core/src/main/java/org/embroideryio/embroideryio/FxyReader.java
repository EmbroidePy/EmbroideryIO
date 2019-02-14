package org.embroideryio.embroideryio;

import java.io.IOException;

public class FxyReader extends EmbReader {

    public static final String MIME = "application/x-exy";
    public static final String EXT = "exy";

    @Override
    public void read() throws IOException {
        skip(0x100);
        DszReader.z_stitch_encoding_read(this);
    }
}
