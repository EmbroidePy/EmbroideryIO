package org.embroideryio.embroideryio;

import java.io.IOException;

public class ExyReader extends EmbReader {

    public static final String MIME = "application/x-exy";
    public static final String EXT = "exy";

    @Override
    public void read() throws IOException {
        skip(0x100);
        DstReader.dst_read_stitches(this);
    }
}
