package org.embroideryio.embroideryio;

public class EmbConstant {
    public static final int COMMAND_MASK = 0xFF;
    public static final int THREAD_MASK = 0xFF00;
    public static final int NEEDLE_MASK = 0xFF0000;
    public static final int ORDER_MASK = 0xFF000000;

    public static final int NO_COMMAND = -1;
    public static final int STITCH = 0;
    public static final int JUMP = 1;
    public static final int TRIM = 2;
    public static final int STOP = 3;
    public static final int END = 4;
    public static final int COLOR_CHANGE = 5;
    public static final int NEEDLE_SET = 9;
    public static final int SEQUIN_MODE = 6;
    public static final int SEQUIN_EJECT = 7;
    public static final int SLOW = 0xB;
    public static final int FAST = 0xC;

    public static final int SET_CHANGE_SEQUENCE = 0x10;
    public static final int SEW_TO = 0xB0;
    public static final int NEEDLE_AT = 0xB1;
    public static final int STITCH_BREAK = 0xE0;

    public static final int SEQUENCE_BREAK = 0xE1;
    public static final int COLOR_BREAK = 0xE2;

    public static final int TIE_ON = 0xE4;
    public static final int TIE_OFF = 0xE5;
    public static final int FRAME_EJECT = 0xE9;

    public static final int MATRIX_TRANSLATE = 0xC0;
    public static final int MATRIX_SCALE_ORIGIN = 0xC1;
    public static final int MATRIX_ROTATE_ORIGIN = 0xC2;
    public static final int MATRIX_SCALE = 0xC4;
    public static final int MATRIX_ROTATE = 0xC5;
    public static final int MATRIX_RESET = 0xC3;

    public static final int OPTION_MAX_STITCH_LENGTH = 0xD5;
    public static final int OPTION_MAX_JUMP_LENGTH = 0xD6;
    public static final int OPTION_EXPLICIT_TRIM = 0xD7;
    public static final int OPTION_IMPLICIT_TRIM = 0xD8;

    public static final int CONTINGENCY_TIE_ON_NONE = 0xD3;
    public static final int CONTINGENCY_TIE_ON_THREE_SMALL = 0xD1;

    public static final int CONTINGENCY_TIE_OFF_NONE = 0xD4;
    public static final int CONTINGENCY_TIE_OFF_THREE_SMALL = 0xD2;

    public static final int CONTINGENCY_LONG_STITCH_NONE = 0xF0;
    public static final int CONTINGENCY_LONG_STITCH_JUMP_NEEDLE = 0xF1;
    public static final int CONTINGENCY_LONG_STITCH_SEW_TO = 0xF2;

    public static final int CONTINGENCY_SEQUIN_UTILIZE = 0xF5;
    public static final int CONTINGENCY_SEQUIN_JUMP = 0xF6;
    public static final int CONTINGENCY_SEQUIN_STITCH = 0xF7;
    public static final int CONTINGENCY_SEQUIN_REMOVE = 0xF8;
}
