package org.embroideryio.embroideryio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.embroideryio.embroideryio.EmbConstant.*;

public class EmbPattern implements Points {

    public static final String PROP_FILENAME = "filename";
    public static final String PROP_NAME = "name";
    public static final String PROP_CATEGORY = "category";
    public static final String PROP_AUTHOR = "author";
    public static final String PROP_KEYWORDS = "keywords";
    public static final String PROP_COMMENTS = "comments";

    public ArrayList<EmbThread> threadlist;
    private String filename;
    private String name;
    private String category;
    private String author;
    private String keywords;
    private String comments;

    private float _previousX = 0;
    private float _previousY = 0;

    private DataPoints stitches = new DataPoints();

    public EmbPattern() {
        threadlist = new ArrayList<>();
    }

    public EmbPattern(EmbPattern p) {
        this.filename = p.filename;
        this.name = p.name;
        this.category = p.category;
        this.author = p.author;
        this.keywords = p.keywords;
        this.comments = p.comments;
        this.threadlist = new ArrayList<>(p.threadlist.size());
        for (EmbThread thread : p.getThreadlist()) {
            addThread(new EmbThread(thread));
        }
        this.stitches = new DataPoints(p.stitches);
    }

    public void setMetadata(EmbPattern p) {
        this.filename = p.filename;
        this.name = p.name;
        this.category = p.category;
        this.author = p.author;
        this.keywords = p.keywords;
        this.comments = p.comments;
    }

    public void setPattern(EmbPattern p) {
        this.filename = p.filename;
        this.name = p.name;
        this.category = p.category;
        this.author = p.author;
        this.keywords = p.keywords;
        this.comments = p.comments;
        this.threadlist.clear();
        for (EmbThread thread : p.getThreadlist()) {
            addThread(new EmbThread(thread));
        }
        this.stitches = new DataPoints(p.stitches);
    }

    @Override
    public float getX(int index) {
        return stitches.getX(index);
    }

    @Override
    public float getY(int index) {
        return stitches.getY(index);
    }

    @Override
    public void setLocation(int index, float x, float y) {
        stitches.setLocation(index, x, y);
    }

    @Override
    public int getData(int index) {
        return stitches.getData(index);
    }

    @Override
    public int size() {
        return stitches.size();
    }

    public float[] getPointlist() {
        return stitches.getPointlist();
    }

    public int[] getData() {
        return stitches.getData();
    }

    public Points getPoints() {
        return stitches;
    }

    DataPoints getStitches() {
        return stitches;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String value) {
        filename = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    void add(EmbThread embroideryThread) {
        threadlist.add(embroideryThread);
    }

    public ArrayList<EmbThread> getThreadlist() {
        return threadlist;
    }

    public void addThread(EmbThread thread) {
        threadlist.add(thread);
    }

    public EmbThread getThread(int index) {
        return threadlist.get(index);
    }

    public EmbThread getRandomThread() {
        return new EmbThread(0xFF000000 | (int) (Math.random() * 0xFFFFFF), "Random");
    }

    public EmbThread getThreadOrFiller(int index) {
        if (threadlist.size() <= index) {
            return getRandomThread();
        }
        return threadlist.get(index);
    }

    public EmbThread getLastThread() {
        if (threadlist == null) {
            return null;
        }
        if (threadlist.isEmpty()) {
            return null;
        }
        return threadlist.get(threadlist.size() - 1);
    }

    public int getThreadCount() {
        if (threadlist == null) {
            return 0;
        }
        return threadlist.size();
    }

    public boolean isEmpty() {
        if (stitches == null) {
            return true;
        }
        if (stitches.size() == 0) {
            return threadlist.isEmpty();
        }
        return false;
    }

    public HashMap<String, String> getMetadata() {
        HashMap<String, String> metadata = new HashMap<>();
        if (filename != null) {
            metadata.put(PROP_FILENAME, filename);
        }
        if (name != null) {
            metadata.put(PROP_NAME, name);
        }
        if (category != null) {
            metadata.put(PROP_CATEGORY, category);
        }
        if (author != null) {
            metadata.put(PROP_AUTHOR, author);
        }
        if (keywords != null) {
            metadata.put(PROP_KEYWORDS, keywords);
        }
        if (comments != null) {
            metadata.put(PROP_COMMENTS, comments);
        }
        return metadata;
    }

    public void setMetadata(String key, String value) {
    }

    public String getMetadata(String data) {
        return null;
    }

    public String getMetadata(String value, String default_value) {
        return default_value; //TODO: This stuff should be hooked up.
    }

    public void setMetadata(Map<String, String> map) {
        filename = map.get(PROP_FILENAME);
        name = map.get(PROP_NAME);
        category = map.get(PROP_CATEGORY);
        author = map.get(PROP_AUTHOR);
        keywords = map.get(PROP_KEYWORDS);
        comments = map.get(PROP_COMMENTS);
    }

    public Iterable<StitchBlock> asStitchBlock() {
        return new Iterable<StitchBlock>() {
            @Override
            public Iterator<StitchBlock> iterator() {
                return new Iterator<StitchBlock>() {
                    int threadIndex = -1;
                    EmbThread thread = null;

                    final PointsIndexRange<DataPoints> points = new PointsIndexRange<>(stitches, 0, 0);

                    final StitchBlock object = new StitchBlock() {
                        @Override
                        public EmbThread getThread() {
                            if (thread != null) {
                                return thread;
                            }
                            if (threadlist.size() <= threadIndex) {
                                thread = getRandomThread();
                            } else {
                                thread = threadlist.get(threadIndex);
                            }
                            return thread;
                        }

                        @Override
                        public Points getPoints() {
                            return points;
                        }

                        @Override
                        public int getType() {
                            return 0;
                        }
                    };

                    final int NOT_CALCULATED = 0;
                    final int HAS_NEXT = 1;
                    final int ENDED = 2;

                    int mode = NOT_CALCULATED;

                    private boolean iterateStart() {
                        int start = points.getStart();
                        int end = stitches.size();
                        while (start < end) {
                            int data = stitches.getData(start) & COMMAND_MASK;
                            if ((data == COLOR_CHANGE) || (data == NEEDLE_SET) || (data == COLOR_BREAK)) {
                                threadIndex++;
                                thread = null;
                            }
                            if ((data & COMMAND_MASK) == STITCH) {
                                if (threadIndex == -1) {
                                    threadIndex = 0;
                                }
                                points.setStart(start);
                                return false;
                            }
                            start++;
                        }
                        points.setStart(start);
                        return true;
                    }

                    private boolean iterateLength() {
                        int length = 0;
                        for (int i = points.getStart(), ie = stitches.size(); i < ie; i++, length++) {
                            int data = stitches.getData(i);
                            if ((data & COMMAND_MASK) != STITCH) {
                                points.setLength(length);
                                return false;
                            }
                        }
                        points.setLength(length);
                        return true;
                    }

                    private void calculate() {
                        points.setStart(points.getStart() + points.getLength());
                        points.setLength(0);
                        if (points.getStart() >= stitches.size()) {
                            mode = ENDED;
                            return;
                        }
                        if (iterateStart()) {
                            mode = ENDED;
                            return;
                        }
                        iterateLength();
                        mode = HAS_NEXT;
                    }

                    @Override
                    public boolean hasNext() {
                        if (mode == NOT_CALCULATED) {
                            calculate();
                        }
                        return mode == HAS_NEXT;
                    }

                    @Override
                    public StitchBlock next() {
                        mode = NOT_CALCULATED;
                        return object;
                    }
                };
            }
        };
    }

    public Iterable<StitchBlock> asColorBlock() {
        return new Iterable<StitchBlock>() {
            @Override
            public Iterator<StitchBlock> iterator() {
                return new Iterator<StitchBlock>() {
                    int threadIndex = -1;
                    EmbThread thread = null;
                    final PointsIndexRange<DataPoints> points = new PointsIndexRange<>(stitches, 0, 0);
                    final StitchBlock object = new StitchBlock() {
                        @Override
                        public EmbThread getThread() {
                            if (thread != null) {
                                return thread;
                            }
                            if (threadlist.size() <= threadIndex) {
                                thread = getRandomThread();
                            } else {
                                thread = threadlist.get(threadIndex);
                            }
                            return thread;
                        }

                        @Override
                        public Points getPoints() {
                            return points;
                        }

                        @Override
                        public int getType() {
                            return 0;
                        }
                    };

                    final int NOT_CALCULATED = 0;
                    final int HAS_NEXT = 1;
                    final int ENDED = 2;

                    int mode = NOT_CALCULATED;

                    private boolean iterateLength() {
                        int length = 0;
                        for (int i = points.getStart(), ie = stitches.size(); i < ie; i++, length++) {
                            int data = stitches.getData(i);
                            if ((data & COMMAND_MASK) == COLOR_CHANGE) {
                                points.setLength(length);
                                return false;
                            }
                        }
                        points.setLength(length);
                        return true;
                    }

                    private void calculate() {
                        if (stitches.size() == 0) {
                            mode = ENDED;
                            return;
                        }
                        threadIndex++;
                        thread = null;
                        points.setStart(points.getStart() + points.getLength());
                        points.setLength(0);
                        if (points.getStart() < stitches.size()) {
                            int data = stitches.getData(points.getStart());
                            if ((data & COMMAND_MASK) == COLOR_CHANGE) {
                                points.setStart(points.getStart() + 1);
                            }
                        } else {
                            mode = ENDED;
                            return;
                        }
                        iterateLength();
                        mode = HAS_NEXT;
                    }

                    @Override
                    public boolean hasNext() {
                        if (mode == NOT_CALCULATED) {
                            calculate();
                        }
                        return mode == HAS_NEXT;
                    }

                    @Override
                    public StitchBlock next() {
                        mode = NOT_CALCULATED;
                        return object;
                    }
                };
            }
        };
    }

    public List<EmbThread> getUniqueThreadList() {
        ArrayList<EmbThread> threads = new ArrayList<>();
        for (EmbThread thread : threadlist) {
            if (!threads.contains(thread)) {
                threads.add(thread);
            }
        }
        return threads;
    }

    public List<EmbThread> getSingletonThreadList() {
        ArrayList<EmbThread> threads = new ArrayList<>();
        EmbThread previous = null;
        for (EmbThread thread : threadlist) {
            if (!thread.equals(previous)) {
                threads.add(thread);
            }
            previous = thread;
        }
        return threads;
    }

    public void translate(float dx, float dy) {
        stitches.translate(dx, dy);
    }

    public void clear() {
        threadlist.clear();
        filename = null;
        name = null;
        category = null;
        author = null;
        keywords = null;
        comments = null;
        _previousX = 0;
        _previousY = 0;
        if (stitches != null) {
            stitches.clear();
        }
    }

    public float[] getBounds() {
        float minX = stitches.getMinX();
        float minY = stitches.getMinY();
        float maxX = stitches.getMaxX();
        float maxY = stitches.getMaxY();
        return new float[]{minX, minY, maxX, maxY};
    }

    public float getWidth() {
        return stitches.getWidth();
    }

    public float getHeight() {
        return stitches.getHeight();
    }

    public float getMinX() {
        return stitches.getMinX();
    }

    public float getMaxX() {
        return stitches.getMaxX();
    }

    public float getMinY() {
        return stitches.getMinY();
    }

    public float getMaxY() {
        return stitches.getMaxY();
    }

    public void fixColorCount() {
        int threadIndex = 0;
        boolean starting = true;
        for (int i = 0, ie = stitches.size(); i < ie; i++) {
            int data = stitches.getData(i) & COMMAND_MASK;
            if (data == STITCH) {
                if (starting) {
                    threadIndex++;
                }
                starting = false;
            } else if ((data == COLOR_CHANGE) || (data == NEEDLE_SET)) {
                if (starting) {
                    continue;
                }
                threadIndex++;
            }
        }
        while (threadlist.size() < threadIndex) {
            addThread(getThreadOrFiller(threadlist.size()));
        }
    }

    public void stitchAbs(float x, float y) {
        addStitchAbs(x, y, STITCH);
    }

    public void stitch(float dx, float dy) {
        addStitchRel(dx, dy, STITCH);
    }

    public void moveAbs(float x, float y) {
        addStitchAbs(x, y, JUMP);
    }

    public void move(float dx, float dy) {
        addStitchRel(dx, dy, JUMP);
    }

    public void color_change(float dx, float dy) {
        addStitchRel(dx, dy, COLOR_CHANGE);
    }

    public void color_change() {
        addStitchRel(0, 0, COLOR_CHANGE);
    }

    public void needle_change(Integer needle, float dx, float dy) {
        int cmd = EmbFunctions.encode_thread_change(NEEDLE_SET, null, needle);
        addStitchRel(dx, dy, cmd);
    }

    public void needle_change(Integer needle) {
        int cmd = EmbFunctions.encode_thread_change(NEEDLE_SET, null, needle);
        addStitchRel(0, 0, cmd);
    }

    public void trim(float dx, float dy) {
        addStitchRel(dx, dy, TRIM);
    }

    public void trim() {
        addStitchRel(0, 0, TRIM);
    }

    public void sequin_mode(float dx, float dy) {
        addStitchRel(dx, dy, SEQUIN_MODE);
    }

    public void sequin_mode() {
        addStitchRel(0, 0, SEQUIN_MODE);
    }

    public void sequin_eject() {
        addStitchRel(0, 0, SEQUIN_EJECT);
    }

    public void sequin_eject(float dx, float dy) {
        addStitchRel(dx, dy, SEQUIN_EJECT);
    }

    public void stop(float dx, float dy) {
        addStitchRel(dx, dy, STOP);
    }

    public void stop() {
        addStitchRel(0, 0, STOP);
    }

    public void end(float dx, float dy) {
        addStitchRel(dx, dy, END);
    }

    public void end() {
        addStitchRel(0, 0, END);
    }

    public void add(double x, double y, int flag) {
        stitches.add((float) x, (float) y, flag);
    }

    public void addStitchAbs(float x, float y, int command) {
        stitches.add(x, y, command);
        _previousX = x;
        _previousY = y;
    }

    /**
     * AddStitchRel adds a stitch to the pattern at the relative position (dx,
     * dy) to the previous stitch. Units are in 1/10 millimeters.
     *
     * @param dx The change in X position.
     * @param dy The change in Y position. Positive value move upward.
     * @param flags JUMP, TRIM, NORMAL or STOP
     */
    public void addStitchRel(float dx, float dy, int flags) {
        float x = _previousX + dx;
        float y = _previousY + dy;
        this.addStitchAbs(x, y, flags);
    }

    public int count_commands(int... commands) {
        int count = 0;
        for (int i = 0, ie = stitches.size(); i < ie; i++) {
            int data = stitches.getData(i) & COMMAND_MASK;
            for (int cmd : commands) {
                if (cmd == data) {
                    count += 1;
                    break;
                }
            }
        }
        return count;
    }

    public void addBlock(String textColor, float... values) {

        stitches.add(values);
        if (textColor != null) {
            addStitchRel(0, 0, SEQUENCE_BREAK);
        }
        if (textColor != null) {
            EmbThread thread = new EmbThread();
            thread.setStringColor(textColor);
            threadlist.add(thread);
            addStitchRel(0, 0, COLOR_BREAK);
        }
    }

}
