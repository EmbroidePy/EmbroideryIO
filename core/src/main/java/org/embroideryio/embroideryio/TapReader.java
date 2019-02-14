package org.embroideryio.embroideryio;

import java.io.IOException;

public class TapReader extends EmbReader {

    public static final String MIME = "application/x-tap";
    public static final String EXT = "tap";

    @Override
    public void read() throws IOException {
        DstReader.dst_read_stitches(this);
    }
}
