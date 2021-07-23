package org.embroideryio.embroideryio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    
    /*
    * Gets pattern with jumps within sewn command blocks removed,
    * only counting the needle hits as valid points. Optional trim added if
    * beyond acceptable limit.
    */
    public EmbPattern get_pattern_needle_hits() {
        return get_pattern_needle_hits(Integer.MAX_VALUE);
    }
    
    public EmbPattern get_pattern_needle_hits(int jumps_to_require_trim){
        EmbPattern new_pattern = new EmbPattern();
        int i = -1;
        int ie = stitches.size() - 1;
        int count = 0;
        boolean trimmed = true;
        while (i < ie) {
            i += 1;
            int command = stitches.getData(i) & COMMAND_MASK;
            if ((command == STITCH) || (command == SEQUIN_EJECT))
                trimmed = false;
            else if ((command == COLOR_CHANGE) || (command == NEEDLE_SET) || (command == TRIM))
                trimmed = true;
            
            if ((trimmed) || (command != JUMP)) {
                new_pattern.add(stitches.getX(i),stitches.getY(i),  stitches.getData(i));
                continue;
            }
            
            while ((i < ie) && (command == JUMP)) { //skip all jumps.
                i += 1;
                command = stitches.getData(i) & COMMAND_MASK;
                count += 1;
            }
            if (command != JUMP)
                i -= 1; //overshot, go back a step.
            
            if (count >= jumps_to_require_trim) {
                new_pattern.trim(); //jumped beyond limit.
                trimmed = true;
            }
            count = 0;
            //Stitched jumps are simply omitted.
        }
        new_pattern.threadlist.addAll(threadlist);
        new_pattern.setMetadata(this);
        return new_pattern;
    }
    
    /*
    * Gets pattern with jumps combined into a single jump, and trim added if beyond
    * jump limit. For DST this tends to be required.
    */
    public EmbPattern get_pattern_interpolate_trim(int jumps_to_require_trim){
        EmbPattern new_pattern = new EmbPattern();
        int i = -1;
        int ie = stitches.size() - 1;
        int count = 0;
        boolean trimmed = true;
        while (i < ie) {
            i += 1;
            int command = stitches.getData(i) & COMMAND_MASK;
            if ((command == STITCH) || (command == SEQUIN_EJECT))
                trimmed = false;
            else if ((command == COLOR_CHANGE) || (command == NEEDLE_SET) || (command == TRIM))
                trimmed = true;
            
            if ((trimmed) || (command != JUMP)) {
                new_pattern.add(stitches.getX(i),stitches.getY(i),  stitches.getData(i));
                continue;
            }
            
            while ((i < ie) && (command == JUMP)) { //skip all jumps.
                i += 1;
                command = stitches.getData(i) & COMMAND_MASK;
                count += 1;
            }
            if (command != JUMP)
                i -= 1; //overshot, go back a step.
            
            if (count >= jumps_to_require_trim) {
                new_pattern.trim(); //jumped beyond limit.
            }
            count = 0;
            new_pattern.add(stitches.getX(i),stitches.getY(i),  stitches.getData(i));
        }
        new_pattern.threadlist.addAll(threadlist);
        new_pattern.setMetadata(this);
        return new_pattern;
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

    
    public static void setSettings(BaseIO obj, Object... settings) {
        for (int i = 0, ie = settings.length; i < ie; i += 2) {
            if (settings[i] instanceof String) {
                obj.set((String) settings[i], settings[i + 1]);
            }
        }
    }

    public static void writeEmbroidery(EmbPattern pattern, Writer writer, OutputStream out) throws IOException {
        EmbEncoder coder = new EmbEncoder();
        coder.setDefaultIO(writer);
        EmbPattern out_pattern = new EmbPattern();
        coder.transcode(pattern, out_pattern);
        writer.write(out_pattern, out);
    }

    public static void writeStream(EmbPattern pattern, String path, OutputStream out, Object... settings) throws IOException {
        EmbPattern.Writer writer = EmbPattern.getWriterByFilename(path);
        if (writer != null) {
            setSettings(writer, settings);
            writeEmbroidery(pattern, writer, out);
        }
    }

    public static void write(EmbPattern pattern, String path, Object... settings) throws IOException {
        FileOutputStream out = null;
        EmbPattern.Writer writer = EmbPattern.getWriterByFilename(path);
        try {
            if (writer != null) {
                setSettings(writer, settings);
                File file = new File(path);
                file.createNewFile();
                out = new FileOutputStream(file);
                writeEmbroidery(pattern, writer, out);
                out.flush();
                out.close();
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public static EmbPattern readEmbroidery(Reader reader, InputStream in) throws IOException {
        EmbPattern pattern = new EmbPattern();
        reader.read(pattern, in);
        return pattern;
    }

    public static EmbPattern readStream(String path, InputStream out, Object... settings) throws IOException {
        EmbPattern.Reader reader = EmbPattern.getReaderByFilename(path);
        if (reader != null) {
            setSettings(reader, settings);
            return readEmbroidery(reader, out);
        }
        return null;
    }

    public static EmbPattern read(String path, Object... settings) throws IOException {
        File input = new File(path);
        if (input.exists()) {
            FileInputStream instream = null;
            try {
                instream = new FileInputStream(input);
                EmbPattern.Reader reader = EmbPattern.getReaderByFilename(path);
                if (reader == null) {
                    return null;
                }
                setSettings(reader, settings);
                return readEmbroidery(reader, instream);
            } catch (IOException ex) {
                throw ex;
            } finally {
                if (instream != null) {
                    instream.close();
                }
            }
        }
        return null;
    }

    public static String getExtensionByFileName(String name) {
        if (name == null) {
            return null;
        }
        String[] split = name.split("\\.");
        if (split.length <= 1) {
            return null;
        }
        return split[split.length - 1].toLowerCase();
    }

    public static EmbPattern.Reader getReaderByFilename(String filename) {
        String ext = getExtensionByFileName(filename);
        if (ext == null) {
            return null;
        }
        switch (ext) {
            case "100":
                return new A100Reader();
            case "10o":
                return new A10oReader();
            case "bro":
                return new BroReader();
            case "col":
                return new ColReader();
            case "csd":
                return new CsdReader();
            case "csv":
                return new CsvReader();
            case "dat":
                return new DatReader();
            case "dsb":
                return new DsbReader();
            case "dst":
                return new DstReader();
            case "dsz":
                return new DszReader();
            case "emd":
                return new EmdReader();
            case "emm":
                return new EmmReader();
            case "exp":
                return new ExpReader();
            case "gt":
                return new GtReader();
            case "edr":
                return new EdrReader();
            case "exy":
            case "e00":
            case "e01":
                return new ExyReader();
            case "fxy":
            case "f00":
            case "f01":
                return new FxyReader();
            case "inb":
                return new InbReader();
            case "inf":
                return new InfReader();
            
            case "jef":
                return new JefReader();
            case "jpx":
                return new JpxReader();
            case "ksm":
                return new KsmReader();
            case "max":
                return new MaxReader();
            case "mit":
                return new MitReader();
            case "new":
                return new NewReader();
            case "pcd":
                return new PcdReader();
            case "pcm":
                return new PcmReader();
            case "pcq":
                return new PcqReader();
            case "pcs":
                return new PcsReader();
            case "pec":
                return new PecReader();
            case "pes":
                return new PesReader();
            case "phb":
                return new PhbReader();
            case "phc":
                return new PhcReader();
            case "pmv":
                return new PmvReader();
            case "sew":
                return new SewReader();
            case "shv":
                return new ShvReader();
            case "stc":
                return new StcReader();
            case "stx":
                return new StxReader();
            case "tap":
                return new TapReader();
            case "tbf":
                return new TbfReader();
            case "u01":
                return new U01Reader();
            case "vp3":
                return new Vp3Reader();
            case "xxx":
                return new XxxReader();
            case "zhs":
                //return new ZhsReader();
                break;
            case "zxy":
            case "z00":
            case "z01":
                return new ZxyReader();
            default:
                return null;
        }
        return null;
    }

    public static Reader getReaderByMime(String mime) {
        //TODO: Implement the mime type checker.
        return null;
    }

    public static EmbPattern.Writer getWriterByFilename(String filename) {
        String ext = getExtensionByFileName(filename);
        if (ext == null) {
            return null;
        }
        switch (ext) {
            case "100":
                //return new A100Writer();
                break;
            case "10o":
                //return new A10oWriter();
                break;
            case "bro":
                //return new BroWriter();
                break;
            case "col":
                //return new ColWriter();
                break;
            case "csv":
                return new CsvWriter();
            case "dat":
                //return new DatWriter();
                break;
            case "edr":
                return new EdrWriter();
            case "dsb":
                //return new DsbWriter();
                break;
            case "dst":
                return new DstWriter();
            case "dsz":
                //return new DszWriter();
                break;
            case "emd":
                //return new EmdWriter();
                break;
            case "emm":
                return new EmmWriter();
            case "exp":
                return new ExpWriter();
            case "gt":
                break;
            //return new GtWriter();
            case "exy":
            case "e00":
            case "e01":
                //return new ExyWriter();
                break;
            case "fxy":
            case "f00":
            case "f01":
                //return new FxyWriter();
                break;
            case "inb":
                //return InbWriter();
                break;
            case "inf":
                //return new InfWriter();
                break;
            case "jef":
                return new JefWriter();
            case "jpx":
                //return new JpxWriter();
                break;
            case "ksm":
                //return new KsmWriter();
                break;
            case "max":
                //return new MaxWriter();
                break;
            case "mit":
                //return new MitWriter();
                break;
            case "new":
                //return new NewWriter();
                break;
            case "pcd":
                //return new PcdWriter();
                break;
            case "pcm":
                //return new PcmWriter();
                break;
            case "pcq":
                //return new PcqWriter();
                break;
            case "pcs":
                return new PcsWriter();
            case "pec":
                return new PecWriter();
            case "pes":
                return new PesWriter();
            case "phb":
                //return new PhbWriter();
                break;
            case "phc":
                //return new PhcWriter();
                break;
            case "pmv":
                return new PmvWriter();
            case "sew":
                //return new SewWriter();
                break;
            case "shv":
                //return new ShvWriter();
                break;
            case "stc":
                //return new StcWriter();
                break;
            case "stx":
                //return new StxWriter();
                break;
            case "tap":
                //return new TapWriter();
                break;
            case "tbf":
                //return new TbfWriter();
                break;
            case "u01":
                return new U01Writer();
            case "vp3":
                return new Vp3Writer();
            case "xxx":
                return new XxxWriter();
            case "zhs":
                //return new ZhsWriter();
                break;
            case "zxy":
            case "z00":
            case "z01":
                //return new ZxyWriter();
                break;
            default:
                return null;
        }
        return null;
    }

    public static Writer getWriterByMime(String mime) {
        //TODO: Implement the meme type writer.
        return null;
    }

    public interface BaseIO {

        void set(String key, Object value);

        Object get(String key);

        Object get(String key, Object default_value);
    }

    public interface Reader extends BaseIO {

        void read(EmbPattern pattern, InputStream stream) throws IOException;
    }

    public interface Writer extends BaseIO {

        void write(EmbPattern pattern, OutputStream stream) throws IOException;
    }
    
}
