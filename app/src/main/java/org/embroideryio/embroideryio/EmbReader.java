package org.embroideryio.embroideryio;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public abstract class EmbReader extends ReadHelper implements EmbroideryIO.Reader {

    EmbPattern pattern;
    HashMap<String, Object> settings = new HashMap<>();

    @Override
    public void set(String key, Object value) {
        if (settings == null) {
            settings = new HashMap<>();
        }
        settings.put(key, value);
    }

    @Override
    public Object get(String key) {
        if (settings == null) {
            return null;
        }
        return settings.get(key);
    }

    @Override
    public Object get(String key, Object default_value) {
        if (settings == null) {
            return default_value;
        }
        Object value = settings.get(key);
        if (value == null) {
            return default_value;
        }
        return value;
    }

    @Override
    public void read(EmbPattern pattern, InputStream stream) throws IOException {
        readPosition = 0;
        this.stream = stream;
        this.pattern = pattern;
        read();
    }

    @Override
    protected abstract void read() throws IOException;

}
