package org.embroideryio.embroideryio;


import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import static org.embroideryio.embroideryio.EmbConstant.*;

public abstract class EmbWriter extends WriteHelper implements EmbroideryIO.Writer {

    protected EmbPattern pattern;
    HashMap<String, Object> settings = new HashMap<>();

    public EmbWriter() {
        settings.put("encode", true);
        settings.put("max_jump", Float.POSITIVE_INFINITY);
        settings.put("max_stitch", Float.POSITIVE_INFINITY);
        settings.put("full_jump", false);
        settings.put("writes_speeds", false);
        settings.put("sequin_contingency", CONTINGENCY_SEQUIN_JUMP);
        settings.put("thread_change_command", COLOR_CHANGE);
    }

    @Override
    public void write(EmbPattern pattern, OutputStream stream) throws IOException {
        setStream(stream);
        this.pattern = pattern;
        write();
    }

    public boolean getBoolean(String key, boolean default_value) {
        if (settings == null) {
            return default_value;
        }
        return (boolean) get(key, default_value);
    }

    public int getInt(String key, int default_value) {
        if (settings == null) {
            return default_value;
        }
        return (int) get(key, default_value);
    }

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

    public abstract void write() throws IOException;

    public String getName() {
        return pattern.getName();
    }

    public ArrayList<EmbThread> getUniqueThreads() {
        ArrayList<EmbThread> threads = new ArrayList<>();
        for (StitchBlock object : pattern.asStitchBlock()) {
            EmbThread thread = object.getThread();
            threads.remove(threads);
            threads.add(thread);
        }
        return threads;
    }

    public int getColorChanges() {
        int count = 0;
        Points stitches = pattern.getStitches();
        for (int i = 0, ie = stitches.size(); i < ie; i++) {
            int flags = stitches.getData(i);
            switch (flags & COMMAND_MASK) {
                case COLOR_CHANGE:
                    count++;
            }
        }
        return count;
    }

    public int getStitchJumpCount() {
        int count = 0;
        Points stitches = pattern.getStitches();
        for (int i = 0, ie = stitches.size(); i < ie; i++) {
            int flags = stitches.getData(i);
            switch (flags) {
                case STITCH:
                case JUMP:
                    count++;
            }
        }
        return count;
    }

    public int[] getThreadUseOrder() {
        ArrayList<EmbThread> colors = getThreads();
        ArrayList<EmbThread> uniquelist = getUniqueThreads();

        int[] useorder = new int[colors.size()];
        for (int i = 0, s = colors.size(); i < s; i++) {
            useorder[i] = uniquelist.indexOf(colors.get(i));
        }
        return useorder;
    }

    public ArrayList<EmbThread> getThreads() {
        ArrayList<EmbThread> threads = new ArrayList<>();
        for (StitchBlock object : pattern.asStitchBlock()) {
            threads.add(object.getThread());
        }
        return threads;
    }

}
