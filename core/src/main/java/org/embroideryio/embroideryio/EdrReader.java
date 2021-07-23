package org.embroideryio.embroideryio;

import java.io.IOException;

public class EdrReader extends EmbReader {

    public static final String MIME = "application/x-edr";
    public static final String EXT = "edr";

    
    @Override
    protected void read() throws IOException {
        while (true) {
            int red = readInt8();
            int green = readInt8();
            int blue = readInt8();
            if (blue < 0) {
                return;
            }
            skip(1);
            EmbThread t = new EmbThread();
            t.setColor(red, green, blue);
            pattern.addThread(t);
        }
    }
}
