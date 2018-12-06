package org.embroideryio.embroideryio;

import java.util.ArrayList;

import static org.embroideryio.embroideryio.EmbConstant.*;

public class EmbEncoder {

    public static final String PROP_ENCODE = "encode";
    public static final String PROP_MAX_STITCH = "max_stitch";
    public static final String PROP_MAX_JUMP = "max_jump";
    public static final String PROP_FULL_JUMP = "full_jump";
    public static final String PROP_ROUND = "round";
    public static final String PROP_NEEDLE_COUNT = "needle_count";
    public static final String PROP_THREAD_CHANGE_COMMAND = "thread_change_command";
    public static final String PROP_SEQUIN_CONTINGENCY = "sequin_contingency";
    public static final String PROP_WRITES_SPEED = "writes_speeds";
    public static final String PROP_EXPLICIT_TRIM = "explicit_trim";
    public static final String PROP_TIE_ON = "tie_on";
    public static final String PROP_TIE_OFF = "tie_off";
    public static final String PROP_LONG_STITCH = "long_stitch";

    public boolean encode = true;
    public float max_stitch = Float.POSITIVE_INFINITY;
    public float max_jump = Float.POSITIVE_INFINITY;
    public boolean full_jump = false;
    public boolean round = false;
    public int needle_count = 5;
    public int thread_change_command = COLOR_CHANGE;
    public int sequin_contingency = CONTINGENCY_SEQUIN_JUMP;
    public boolean writes_speeds = true;
    public boolean explicit_trim = false;
    public int tie_on_contingency = CONTINGENCY_TIE_ON_NONE;
    public int tie_off_contingency = CONTINGENCY_TIE_OFF_NONE;
    public int long_stitch_contingency = CONTINGENCY_LONG_STITCH_JUMP_NEEDLE;
    public EmbMatrix matrix = new EmbMatrix();

    private EmbPattern source_pattern;
    private EmbPattern destination_pattern;

    private ArrayList<ThreadSequence> change_sequence = new ArrayList<>();
    private boolean state_trimmed = true;
    private boolean state_sequin_mode = false;
    private boolean state_jumping = false;
    private int position = 0;
    private int order_index = -1;
    private float needle_x = 0;
    private float needle_y = 0;
    private float stitch_x = 0;
    private float stitch_y = 0;

    public EmbEncoder() {
    }

    public void setDefaultIO(EmbroideryIO.BaseIO io) {
        this.encode = (boolean) io.get(PROP_ENCODE, encode);
        this.max_stitch = (float) io.get(PROP_MAX_STITCH, max_stitch);
        this.max_jump = (float) io.get(PROP_MAX_JUMP, max_jump);
        this.full_jump = (boolean) io.get(PROP_FULL_JUMP, full_jump);
        this.round = (boolean) io.get(PROP_ROUND, round);
        this.needle_count = (int) io.get(PROP_NEEDLE_COUNT, needle_count);
        this.thread_change_command = (int) io.get(PROP_THREAD_CHANGE_COMMAND, thread_change_command);
        this.sequin_contingency = (int) io.get(PROP_SEQUIN_CONTINGENCY, sequin_contingency);
        this.writes_speeds = (boolean) io.get(PROP_WRITES_SPEED, writes_speeds);
        this.explicit_trim = (boolean) io.get(PROP_EXPLICIT_TRIM, explicit_trim);
        this.tie_on_contingency = (int) io.get(PROP_TIE_ON, tie_on_contingency);
        this.tie_off_contingency = (int) io.get(PROP_TIE_OFF, tie_off_contingency);
        this.long_stitch_contingency = (int) io.get(PROP_LONG_STITCH, long_stitch_contingency);
    }

    public EmbPattern transcode(EmbPattern source_pattern) {
        if (!encode) {
            return new EmbPattern(source_pattern);
        }
        EmbPattern dest = new EmbPattern();
        dest = transcode(source_pattern, dest);
        return dest;
    }

    public EmbPattern transcode(EmbPattern source_pattern, EmbPattern destination_pattern) {
        if (!encode) {
            destination_pattern.setPattern(source_pattern);
            return destination_pattern;
        }
        if (source_pattern == destination_pattern) {
            source_pattern = new EmbPattern(destination_pattern);
            destination_pattern.clear();
        }
        this.source_pattern = source_pattern;
        this.destination_pattern = destination_pattern;
        transcode_main();
        return this.destination_pattern;
    }

    ArrayList<Integer[]> get_as_thread_change_sequence_events() {
        ArrayList<Integer[]> results = new ArrayList<>();
        Points source = source_pattern.getStitches();
        int current_index = 0;
        for (int i = 0, ie = source.size(); i < ie; i++) {
            Integer[] change = EmbFunctions.decode_embroidery_command(source.getData(i));
            Integer command = change[0];
            int flags = command & COMMAND_MASK;
            if (current_index == 0) {
                if ((flags == STITCH) || (flags == SEW_TO) || (flags == NEEDLE_AT) || (flags == SEQUIN_EJECT)) {
                    current_index = 1;
                }
            }
            if (flags == SET_CHANGE_SEQUENCE) {
                Integer thread = change[1];
                Integer needle = change[2];
                Integer order = change[3];
                results.add(new Integer[]{flags, thread, needle, order, null});
            } else if ((flags == NEEDLE_SET) || (flags == COLOR_CHANGE) || (flags == COLOR_BREAK)) {
                change = EmbFunctions.decode_embroidery_command(command);
                Integer thread = change[1];
                Integer needle = change[2];
                Integer order = change[3];
                results.add(new Integer[]{flags, thread, needle, order, current_index});
                current_index += 1;
            }
        }
        return results;
    }

    private class ThreadSequence {

        Integer command = null;
        Integer thread_index = null;
        Integer needle_index = null;
        EmbThread embThread = null;
    }

    private ThreadSequence getSequence(ArrayList<ThreadSequence> thread_sequence, int index) {
        while (thread_sequence.size() <= index) {
            thread_sequence.add(null);
        }
        ThreadSequence current = thread_sequence.get(index);
        if (current == null) {
            current = new ThreadSequence();
            thread_sequence.set(index, current);
        }
        return current;
    }

    ArrayList<ThreadSequence> build_thread_change_sequence() {
        int lookahead_index = 0;
        change_sequence = new ArrayList<>();
        getSequence(change_sequence, 0);
        ArrayList<Integer[]> events = get_as_thread_change_sequence_events();
        ThreadSequence current;
        for (Integer[] v : events) {
            Integer flags = v[0];
            Integer thread = v[1];
            Integer needle = v[2];
            Integer order = v[3];
            Integer current_index = v[4];
            if (flags == SET_CHANGE_SEQUENCE) {
                if (order == null) {
                    current = getSequence(change_sequence, lookahead_index);
                    lookahead_index += 1;
                } else {
                    current = getSequence(change_sequence, lookahead_index);
                }
            } else {
                current = getSequence(change_sequence, current_index);
                if (current_index > lookahead_index) {
                    lookahead_index = current_index + 1;
                }
            }
            if ((flags == COLOR_CHANGE) || (flags == NEEDLE_SET)) {
                current.command = flags;
            }
            if (thread != null) {
                current.thread_index = thread;
                current.embThread = source_pattern.getThreadOrFiller(thread);
            }
            if (needle != null) {
                current.needle_index = needle;
            }
        }

        int needle_limit = needle_count;
        int thread_index = 0;
        int needle_index = 1;
        for (int i = 0, ie = change_sequence.size(); i < ie; i++) {
            int order = i;
            ThreadSequence s = change_sequence.get(order);
            if (s == null) {
                continue;
            }
            if (s.command == null) {
                s.command = this.thread_change_command;
            }
            if (s.thread_index == null) {
                s.thread_index = thread_index;
                thread_index += 1;
            }
            if (s.needle_index == null) {
                s.needle_index = needle_index;
                if (s.needle_index > needle_limit) {
                    s.needle_index = (s.needle_index - 1) % needle_limit;
                    s.needle_index += 1;
                }
                needle_index += 1;
            }
            if (s.embThread == null) {
                s.embThread = source_pattern.getThreadOrFiller(s.thread_index);
            }
        }
        return change_sequence;
    }

    void transcode_main() {
        Points source = source_pattern.getStitches();
        state_trimmed = true;
        needle_x = 0;
        needle_y = 0;
        position = 0;
        order_index = -1;
        this.destination_pattern.setMetadata(this.source_pattern);
        change_sequence = build_thread_change_sequence();
        
        float[] p = new float[2];
        float x, y;
        int data, flags = NO_COMMAND;
        for (int i = 0, ie = source.size(); i < ie; i++) {
            position = i;
            stitch_x = source.getX(i);
            stitch_y = source.getY(i);
            p[0] = stitch_x;
            p[1] = stitch_y;
            matrix.mapPoints(p);
            x = p[0];
            y = p[1];
            if (this.round) {
                x = (float) Math.rint(x);
                y = (float) Math.rint(y);
            }
            data = source.getData(i);
            flags = data & COMMAND_MASK;
            switch (flags) {
                case NO_COMMAND:
                    continue;
                case STITCH:
                    if (state_trimmed) {
                        declare_not_trimmed();
                        jump_to_within_stitchrange(x, y);
                        stitch_at(x, y);
                        tie_on();
                    } else if (state_jumping) {
                        needle_to(x, y);
                        state_jumping = false;
                    } else {
                        stitch_with_contingency(x, y);
                    }
                    break;
                case NEEDLE_AT:
                    if (state_trimmed) {
                        declare_not_trimmed();
                    }
                    break;
                case SEW_TO:
                    if (state_trimmed) {
                        declare_not_trimmed();
                        jump_to_within_stitchrange(x, y);
                        stitch_at(x, y);
                        tie_on();
                    } else if (state_jumping) {
                        needle_to(x, y);
                        state_jumping = false;
                    } else {
                        sew_to(x, y);
                    }
                    break;
                case STITCH_BREAK:
                    state_jumping = false;
                    break;
                case FRAME_EJECT:
                    tie_off_and_trim_if_needed();
                    jump_to(x, y);
                    stop_here();
                    break;
                case SEQUENCE_BREAK:
                    tie_off_and_trim_if_needed();
                    break;
                case COLOR_BREAK:
                    color_break();
                    break;
                case TIE_OFF:
                    tie_off();
                    break;
                case TIE_ON:
                    tie_on();
                    break;
                case TRIM:
                    tie_off_and_trim_if_needed();
                    break;
                case JUMP:
                    if (!state_jumping) {
                        jump_to(x, y);
                    }
                    break;
                case SEQUIN_MODE:
                    toggle_sequins();
                    break;
                case SEQUIN_EJECT:
                    if (state_trimmed) {
                        declare_not_trimmed();
                        jump_to_within_stitchrange(x, y);
                        stitch_at(x, y);
                        tie_on();
                    }
                    if (!state_sequin_mode) {
                        toggle_sequins();
                    }
                    sequin_at(x, y);
                    break;
                case COLOR_CHANGE:
                    tie_off_trim_color_change();
                    break;
                case NEEDLE_SET:
                    tie_off_trim_color_change();
                    break;
                case STOP:
                    stop_here();
                    break;
                case END:
                    end_here();
                    return;
                case CONTINGENCY_TIE_ON_THREE_SMALL:
                    tie_on_contingency = CONTINGENCY_TIE_ON_THREE_SMALL;
                    break;
                case CONTINGENCY_TIE_OFF_THREE_SMALL:
                    tie_off_contingency = CONTINGENCY_TIE_OFF_THREE_SMALL;
                    break;
                case CONTINGENCY_TIE_ON_NONE:
                    tie_on_contingency = CONTINGENCY_TIE_ON_NONE;
                    break;
                case CONTINGENCY_TIE_OFF_NONE:
                    tie_off_contingency = CONTINGENCY_TIE_OFF_NONE;
                    break;
                case OPTION_MAX_JUMP_LENGTH:
                    x = stitch_x;
                    max_jump = x;
                    break;
                case OPTION_MAX_STITCH_LENGTH:
                    x = stitch_x;
                    max_stitch = x;
                    break;
                case OPTION_EXPLICIT_TRIM:
                    explicit_trim = true;
                    break;
                case OPTION_IMPLICIT_TRIM:
                    explicit_trim = false;
                    break;
                case CONTINGENCY_LONG_STITCH_NONE:
                    long_stitch_contingency = CONTINGENCY_LONG_STITCH_NONE;
                    break;
                case CONTINGENCY_LONG_STITCH_JUMP_NEEDLE:
                    long_stitch_contingency = CONTINGENCY_LONG_STITCH_JUMP_NEEDLE;
                    break;
                case CONTINGENCY_LONG_STITCH_SEW_TO:
                    long_stitch_contingency = CONTINGENCY_LONG_STITCH_SEW_TO;
                    break;
                case CONTINGENCY_SEQUIN_REMOVE:
                    if (state_sequin_mode) {
                        toggle_sequins();
                    }
                    sequin_contingency = CONTINGENCY_SEQUIN_REMOVE;
                    break;
                case CONTINGENCY_SEQUIN_STITCH:
                    if (state_sequin_mode) {
                        toggle_sequins();
                    }
                    sequin_contingency = CONTINGENCY_SEQUIN_STITCH;
                    break;
                case CONTINGENCY_SEQUIN_JUMP:
                    if (state_sequin_mode) {
                        toggle_sequins();
                    }
                    sequin_contingency = CONTINGENCY_SEQUIN_JUMP;
                    break;
                case CONTINGENCY_SEQUIN_UTILIZE:
                    sequin_contingency = CONTINGENCY_SEQUIN_UTILIZE;
                    break;
                case MATRIX_TRANSLATE:
                    matrix.postTranslate(stitch_x, stitch_y);
                    break;
                case MATRIX_SCALE_ORIGIN:
                    matrix.postScale(stitch_x, stitch_y);
                    break;
                case MATRIX_ROTATE_ORIGIN:
                    matrix.postRotate(stitch_x);
                    break;
                case MATRIX_SCALE: {
                    float[] q = new float[2];
                    q[0] = needle_x;
                    q[1] = needle_y;
                    matrix.invert(matrix);
                    matrix.mapPoints(q);
                    matrix.invert(matrix);
                    matrix.postScale(stitch_x, stitch_y, q[0], q[1]);
                    break;
                }
                case MATRIX_ROTATE: {
                    float[] q = new float[2];
                    q[0] = needle_x;
                    q[1] = needle_y;
                    matrix.invert(matrix);
                    matrix.mapPoints(q);
                    matrix.invert(matrix);
                    matrix.postRotate(stitch_x, q[0], q[1]);
                    break;
                }
                case MATRIX_RESET:
                    break;
            }
        }
        if (flags != END) {
            this.end_here();
        }
    }

    void update_needle_position(float x, float y) {
        needle_x = x;
        needle_y = y;
    }

    void declare_not_trimmed() {
        if (order_index == -1) {
            next_change_sequence();
        }
        state_trimmed = false;
    }

    void add_thread_change(Integer command, Integer thread, Integer needle) {
        add_thread_change(command, thread, needle, null);
    }

    void add_thread_change(Integer command, Integer thread, Integer needle, Integer order) {
        add(EmbFunctions.encode_thread_change(command, thread, needle, order));
    }

    void add(int command) {
        add(command, null, null);
    }

    void add(int command, Float x, Float y) {
        if (x == null) {
            x = needle_x;
        }
        if (y == null) {
            y = needle_y;
        }
        destination_pattern.add(x, y, command);
    }

    boolean lookahead_stitch() {
        Points source = source_pattern.getStitches();
        for (int i = position, ie = source.size(); i < ie; i++) {
            int flags = source.getData(i) & COMMAND_MASK;
            switch (flags) {
                case STITCH:
                case NEEDLE_AT:
                case SEW_TO:
                case TIE_ON:
                case SEQUIN_EJECT:
                    return true;
                case END:
                    return false;
            }
        }
        return false;
    }

    void color_break() {
        if (order_index < 0) {
            return; //never stitched yet, ignore.
        }
        if (!state_trimmed) {
            tie_off();
            if (explicit_trim) {
                trim_here();
            }
        }
        if (!lookahead_stitch()) {
            return; //no more stitching, unneeded command.
        }
        next_change_sequence();
        state_trimmed = true;
    }

    void tie_off_trim_color_change() {
        if (!state_trimmed) {
            tie_off();
            if (explicit_trim) {
                trim_here();
            }
        }
        next_change_sequence();
        state_trimmed = true;
    }

    void tie_off_and_trim_if_needed() {
        if (!state_trimmed) {
            tie_off_and_trim();
        }
    }

    void tie_off_and_trim() {
        tie_off();
        trim_here();
    }

    void tie_off() {
        switch (tie_off_contingency) {
            case CONTINGENCY_TIE_OFF_THREE_SMALL:
                Points points = source_pattern.getStitches();
                int pos = position - 1;
                try {
                    float[] b = new float[2];
                    b[0] = points.getX(pos);
                    b[1] = points.getY(pos);
                    matrix.mapPoints(b);
                    int flags = points.getData(pos) & COMMAND_MASK;
                    switch (flags) {
                        case STITCH:
                        case NEEDLE_AT:
                        case SEW_TO:
                        case SEQUIN_EJECT:
                            lock_stitch(needle_x, needle_y, b[0], b[1], max_stitch);
                    }
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }
                break;
            case CONTINGENCY_TIE_OFF_NONE:
                break;
        }
    }

    public void tie_on() {
        switch (tie_on_contingency) {
            case CONTINGENCY_TIE_ON_THREE_SMALL:
                Points points = source_pattern.getStitches();
                int pos = position + 1;
                try {
                    float[] b = new float[2];
                    b[0] = points.getX(pos);
                    b[1] = points.getY(pos);
                    matrix.mapPoints(b);
                    int flags = points.getData(pos) & COMMAND_MASK;
                    switch (flags) {
                        case STITCH:
                        case NEEDLE_AT:
                        case SEW_TO:
                        case SEQUIN_EJECT:
                            lock_stitch(needle_x, needle_y, b[0], b[1], max_stitch);
                    }
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }
                break;
            case CONTINGENCY_TIE_ON_NONE:
                break;
        }
    }

    public void trim_here() {
        if (state_sequin_mode) {
            toggle_sequins();
        }
        add(TRIM);
        state_trimmed = true;
    }

    public void toggle_sequins() {
        if (sequin_contingency == CONTINGENCY_SEQUIN_UTILIZE) {
            add(SEQUIN_MODE);
            state_sequin_mode = !state_sequin_mode;
        }
    }

    void jump_to_within_stitchrange(float new_x, float new_y) {
        float x0 = needle_x;
        float y0 = needle_y;
        float max_length = max_jump;
        interpolate_gap_stitches(x0, y0, new_x, new_y, max_length, JUMP);
        if (full_jump) {
            if ((needle_x != new_x) || (needle_y != new_y)) {
                jump_at(new_x, new_y);
            }
        }
    }

    void jump_to(float new_x, float new_y) {
        float x0 = needle_x;
        float y0 = needle_y;
        float max_length = max_jump;
        interpolate_gap_stitches(x0, y0, new_x, new_y, max_length, JUMP);
        jump_at(new_x, new_y);
    }

    void jump_at(float new_x, float new_y) {
        if (state_sequin_mode) {
            toggle_sequins();
        }
        add(JUMP, new_x, new_y);
        update_needle_position(new_x, new_y);
    }

    void stitch_with_contingency(float new_x, float new_y) {
        switch (long_stitch_contingency) {
            case CONTINGENCY_LONG_STITCH_SEW_TO:
                sew_to(new_x, new_y);
                break;
            case CONTINGENCY_LONG_STITCH_JUMP_NEEDLE:
                needle_to(new_x, new_y);
                break;
            default:
                stitch_at(new_x, new_y);
                break;
        }
    }

    void sew_to(float new_x, float new_y) {
        float x0 = needle_x;
        float y0 = needle_y;
        float max_length = max_stitch;
        interpolate_gap_stitches(x0, y0, new_x, new_y, max_length, STITCH);
        stitch_at(new_x, new_y);
    }

    void needle_to(float new_x, float new_y) {
        float x0 = needle_x;
        float y0 = needle_y;
        float max_length = max_stitch;
        interpolate_gap_stitches(x0, y0, new_x, new_y, max_length, JUMP);
        stitch_at(new_x, new_y);
    }

    void stitch_at(float new_x, float new_y) {
        add(STITCH, new_x, new_y);
        update_needle_position(new_x, new_y);
    }

    void sequin_at(float new_x, float new_y) {
        int contingency = sequin_contingency;
        if (contingency == CONTINGENCY_SEQUIN_REMOVE) //do not update needle position or untrim.
        {
            return;
        }
        float x0 = needle_x;
        float y0 = needle_y;
        float max_length = max_stitch;
        interpolate_gap_stitches(x0, y0, new_x, new_y, max_length, STITCH);
        switch (contingency) {
            case CONTINGENCY_SEQUIN_UTILIZE:
                add(SEQUIN_EJECT, new_x, new_y);
                break;
            case CONTINGENCY_SEQUIN_JUMP:
                add(JUMP, new_x, new_y);
                break;
            case CONTINGENCY_SEQUIN_STITCH:
                add(STITCH, new_x, new_y);
                break;
        }
        update_needle_position(new_x, new_y);
    }

    void slow_command_here() {
        if (writes_speeds) {
            add(SLOW);
        }
    }

    void fast_command_here() {
        if (writes_speeds) {
            add(FAST);
        }
    }

    void stop_here() {
        add(STOP);
        state_trimmed = true;
    }

    void end_here() {
        add(END);
        state_trimmed = true;
    }

    void next_change_sequence() {
        order_index += 1;
        ThreadSequence change = change_sequence.get(order_index);
        ArrayList<EmbThread> threadlist = destination_pattern.getThreadlist();
        threadlist.add(change.embThread);
        switch (thread_change_command) {
            case COLOR_CHANGE:
                if (order_index != 0) {
                    add_thread_change(COLOR_CHANGE, change.thread_index, change.needle_index);
                }
                break;
            case NEEDLE_SET:
                add_thread_change(NEEDLE_SET, change.thread_index, change.needle_index);
                break;
            case STOP:
                add_thread_change(STOP, change.thread_index, change.needle_index);
                break;
        }
        state_trimmed = true;
    }

    boolean position_will_exceed_constraint(Float length) {
        return position_will_exceed_constraint(length, null, null);
    }

    boolean position_will_exceed_constraint(Float length, Float new_x, Float new_y) {
        if (length == null) {
            length = max_stitch;
        }
        if ((new_x == null) || (new_y == null)) {
            float[] p = new float[2];
            p[0] = stitch_x;
            p[1] = stitch_y;
            matrix.mapPoints(p);
            new_x = p[0];
            new_y = p[1];
        }
        double distance_x = new_x - needle_x;
        double distance_y = new_y - needle_y;
        return Math.abs(distance_x) > length || Math.abs(distance_y) > length;
    }

    void interpolate_gap_stitches(float x0, float y0, float x1, float y1, float max_length, int data) {
        Points transcode = destination_pattern.getStitches();
        double distance_x = x1 - x0;
        double distance_y = y1 - y0;
        if ((Math.abs(distance_x) > max_length) || (Math.abs(distance_y) > max_length)) {
            if ((data == JUMP) && (state_sequin_mode)) {
                toggle_sequins(); //can't jump with sequin mode on.
            }
            double steps_x = Math.ceil(Math.abs(distance_x / max_length));
            double steps_y = Math.ceil(Math.abs(distance_y / max_length));
            double steps;
            if (steps_x > steps_y) {
                steps = steps_x;
            } else {
                steps = steps_y;
            }
            double step_size_x = distance_x / steps;
            double step_size_y = distance_y / steps;
            double qx = x0;
            double qy = y0;
            for (int q = 1, qe = (int) steps; q < qe; q++) {
                //we need the gap stitches only, not start or end stitch
                qx += step_size_x;
                qy += step_size_y;
                float x = Math.round(qx);
                float y = Math.round(qy);
                add(data, x, y);
                update_needle_position(x, y);
            }
        }
    }

    void lock_stitch(float x, float y, float anchor_x, float anchor_y) {
        lock_stitch(x, y, anchor_x, anchor_y, max_stitch);
    }

    void lock_stitch(float x, float y, float anchor_x, float anchor_y, double max_length) {
        double length = distance(x, y, anchor_x, anchor_y);
        if (length > max_length) {
            double radians = Math.atan2(anchor_y - y, anchor_x - x);
            anchor_x = (float) (x + max_length * Math.cos(radians));
            anchor_y = (float) (y + max_length * Math.sin(radians));
        }
        float[] amounts = new float[]{0.33f, 0.66f, 0.33f, 0};
        for (float amount : amounts) {
            add(STITCH, (float) towards(x, anchor_x, amount), (float) towards(y, anchor_y, amount));
        }
    }

    public static double towards(double a, double b, double amount) {
        return (amount * (b - a)) + a;
    }

    public static double distance(double x0, double y0, double x1, double y1) {
        double dx = x1 - x0;
        double dy = y1 - y0;
        dx *= dx;
        dy *= dy;
        return Math.sqrt(dx + dy);
    }

}
