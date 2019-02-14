package org.embroideryio.embroideryio;

import java.io.IOException;
import java.util.HashMap;

import static org.embroideryio.embroideryio.EmmWriter.MAGIC_NUMBER;

public class EmmReader extends EmbReader {
    HashMap<String,String> map = new HashMap<>();

    @Override
    protected void read() throws IOException {
        int magic_number = readInt32LE();
        if (magic_number != MAGIC_NUMBER) return;
        int version = readInt32LE();
        if (version != EmmWriter.VERSION) return;
        readVersion1();
        this.pattern.getStitches().read(stream);
    }

    public void readVersion1() throws IOException {
        int thread_count = readInt32LE();
        for (int i = 0; i < thread_count; i++) {
            EmbThread thread = readThread();
            pattern.addThread(thread);
        }
        pattern.setMetadata(readMap());
    }

    public EmbThread readThread() throws IOException {
        EmbThread thread = new EmbThread();
        thread.setColor(readInt32LE());
        thread.setMetadata(readMap());
        return thread;
    }

    public HashMap<String, String> readMap() throws IOException {
        map.clear();
        int size = readInt32LE();
        for (int i = 0; i < size; i++) {
            int ks = readInt32LE();
            String key = readString(ks);
            int vs = readInt32LE();
            String value = readString(vs);
            map.put(key,value);
        }
        return map;
    }
}