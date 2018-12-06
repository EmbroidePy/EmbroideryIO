package org.embroideryio.embroideryio;

import java.util.HashMap;
import java.util.Map;

import static org.embroideryio.embroideryio.EmbConstant.*;

public class EmbFunctions {

    static int encode_thread_change(int command, Integer thread) {
        return encode_thread_change(command, thread, null, null);
    }

    static int encode_thread_change(int command, Integer thread, Integer needle) {
        return encode_thread_change(command, thread, needle, null);
    }

    static int encode_thread_change(int command, Integer thread, Integer needle, Integer order) {
        if (thread == null) {
            thread = 0;
        } else {
            thread &= 0xFF;
            thread += 1;
        }
        if (needle == null) {
            needle = 0;
        } else {
            needle &= 0xFF;
            needle += 1;
        }
        if (order == null) {
            order = 0;
        } else {
            order &= 0xFF;
            order += 1;
        }
        command &= COMMAND_MASK;
        return command | (order << 24) | (needle << 16) | (thread << 8);
    }

    static final Integer[] decode_embroidery_command(int command) {
        Integer flag = command & COMMAND_MASK;
        Integer thread = command & THREAD_MASK;
        thread >>= 8;
        thread -= 1;
        if (thread == -1) {
            thread = null;
        }
        Integer needle = command & NEEDLE_MASK;
        needle >>= 16;
        needle -= 1;
        if (needle == -1) {
            needle = null;
        }
        Integer order = command & ORDER_MASK;
        order >>= 24;
        order -= 1;
        if (order == -1) {
            order = null;
        }
        return new Integer[]{flag, thread, needle, order};
    }

    public static Map<String, Integer> get_command_dictionary() {
        HashMap<String, Integer> dict = new HashMap<>();
        dict.put("NO_COMMAND", NO_COMMAND);
        dict.put("STITCH", STITCH);
        dict.put("JUMP", JUMP);
        dict.put("TRIM", TRIM);
        dict.put("STOP", STOP);
        dict.put("END", END);
        dict.put("COLOR_CHANGE", COLOR_CHANGE);
        dict.put("NEEDLE_SET", NEEDLE_SET);
        dict.put("SEQUIN_MODE", SEQUIN_MODE);
        dict.put("SEQUIN_EJECT", SEQUIN_EJECT);
        dict.put("SLOW", SLOW);
        dict.put("FAST", FAST);

        dict.put("SET_CHANGE_SEQUENCE", SET_CHANGE_SEQUENCE);
        dict.put("SEW_TO", SEW_TO);
        dict.put("NEEDLE_AT", NEEDLE_AT);
        dict.put("STITCH_BREAK", STITCH_BREAK);

        dict.put("SEQUENCE_BREAK", SEQUENCE_BREAK);
        dict.put("COLOR_BREAK", COLOR_BREAK);

        dict.put("TIE_ON", TIE_ON);
        dict.put("TIE_OFF", TIE_OFF);
        dict.put("FRAME_EJECT", FRAME_EJECT);

        dict.put("MATRIX_TRANSLATE", MATRIX_TRANSLATE);
        dict.put("MATRIX_SCALE_ORIGIN", MATRIX_SCALE_ORIGIN);
        dict.put("MATRIX_ROTATE_ORIGIN", MATRIX_ROTATE_ORIGIN);
        dict.put("MATRIX_SCALE", MATRIX_SCALE);
        dict.put("MATRIX_RESET", MATRIX_RESET);
        dict.put("MATRIX_ROTATE", MATRIX_ROTATE);

        dict.put("OPTION_MAX_STITCH_LENGTH", OPTION_MAX_STITCH_LENGTH);
        dict.put("OPTION_MAX_JUMP_LENGTH", OPTION_MAX_JUMP_LENGTH);
        dict.put("OPTION_EXPLICIT_TRIM", OPTION_EXPLICIT_TRIM);
        dict.put("OPTION_IMPLICIT_TRIM", OPTION_IMPLICIT_TRIM);

        dict.put("CONTINGENCY_TIE_ON_NONE", CONTINGENCY_TIE_ON_NONE);
        dict.put("CONTINGENCY_TIE_ON_THREE_SMALL", CONTINGENCY_TIE_ON_THREE_SMALL);

        dict.put("CONTINGENCY_TIE_OFF_NONE", CONTINGENCY_TIE_OFF_NONE);
        dict.put("CONTINGENCY_TIE_OFF_THREE_SMALL", CONTINGENCY_TIE_OFF_THREE_SMALL);

        dict.put("CONTINGENCY_LONG_STITCH_NONE", CONTINGENCY_LONG_STITCH_NONE);
        dict.put("CONTINGENCY_LONG_STITCH_JUMP_NEEDLE", CONTINGENCY_LONG_STITCH_JUMP_NEEDLE);
        dict.put("CONTINGENCY_LONG_STITCH_SEW_TO", CONTINGENCY_LONG_STITCH_SEW_TO);

        dict.put("CONTINGENCY_SEQUIN_UTILIZE", CONTINGENCY_SEQUIN_UTILIZE);
        dict.put("CONTINGENCY_SEQUIN_JUMP", CONTINGENCY_SEQUIN_JUMP);
        dict.put("CONTINGENCY_SEQUIN_STITCH", CONTINGENCY_SEQUIN_STITCH);
        dict.put("CONTINGENCY_SEQUIN_REMOVE", CONTINGENCY_SEQUIN_REMOVE);
        return dict;
    }

    public static Map<Integer, String> get_common_name_dictionary() {
        HashMap<Integer, String> dict = new HashMap<>();
        dict.put(NO_COMMAND, "NO_COMMAND");
        dict.put(STITCH, "STITCH");
        dict.put(JUMP, "JUMP");
        dict.put(TRIM, "TRIM");
        dict.put(STOP, "STOP");
        dict.put(END, "END");
        dict.put(COLOR_CHANGE, "COLOR_CHANGE");
        dict.put(NEEDLE_SET, "NEEDLE_SET");
        dict.put(SEQUIN_MODE, "SEQUIN_MODE");
        dict.put(SEQUIN_EJECT, "SEQUIN_EJECT");
        dict.put(SLOW, "SLOW");
        dict.put(FAST, "FAST");

        dict.put(SET_CHANGE_SEQUENCE, "SET_CHANGE_SEQUENCE");
        dict.put(SEW_TO, "SEW_TO");
        dict.put(NEEDLE_AT, "NEEDLE_AT");
        dict.put(STITCH_BREAK, "STITCH_BREAK");

        dict.put(SEQUENCE_BREAK, "SEQUENCE_BREAK");
        dict.put(COLOR_BREAK, "COLOR_BREAK");

        dict.put(TIE_ON, "TIE_ON");
        dict.put(TIE_OFF, "TIE_OFF");
        dict.put(FRAME_EJECT, "FRAME_EJECT");

        dict.put(MATRIX_TRANSLATE, "MATRIX_TRANSLATE");
        dict.put(MATRIX_SCALE_ORIGIN, "MATRIX_SCALE_ORIGIN");
        dict.put(MATRIX_ROTATE_ORIGIN, "MATRIX_ROTATE_ORIGIN");
        dict.put(MATRIX_SCALE, "MATRIX_SCALE");
        dict.put(MATRIX_ROTATE, "MATRIX_ROTATE");
        dict.put(MATRIX_RESET, "MATRIX_RESET");

        dict.put(OPTION_MAX_STITCH_LENGTH, "OPTION_MAX_STITCH_LENGTH");
        dict.put(OPTION_MAX_JUMP_LENGTH, "OPTION_MAX_JUMP_LENGTH");
        dict.put(OPTION_EXPLICIT_TRIM, "OPTION_EXPLICIT_TRIM");
        dict.put(OPTION_IMPLICIT_TRIM, "OPTION_IMPLICIT_TRIM");

        dict.put(CONTINGENCY_TIE_ON_NONE, "CONTINGENCY_TIE_ON_NONE");
        dict.put(CONTINGENCY_TIE_ON_THREE_SMALL, "CONTINGENCY_TIE_ON_THREE_SMALL");

        dict.put(CONTINGENCY_TIE_OFF_NONE, "CONTINGENCY_TIE_OFF_NONE");
        dict.put(CONTINGENCY_TIE_OFF_THREE_SMALL, "CONTINGENCY_TIE_OFF_THREE_SMALL");

        dict.put(CONTINGENCY_LONG_STITCH_NONE, "CONTINGENCY_LONG_STITCH_NONE");
        dict.put(CONTINGENCY_LONG_STITCH_JUMP_NEEDLE, "CONTINGENCY_LONG_STITCH_JUMP_NEEDLE");
        dict.put(CONTINGENCY_LONG_STITCH_SEW_TO, "CONTINGENCY_LONG_STITCH_SEW_TO");

        dict.put(CONTINGENCY_SEQUIN_UTILIZE, "CONTINGENCY_SEQUIN_UTILIZE");
        dict.put(CONTINGENCY_SEQUIN_JUMP, "CONTINGENCY_SEQUIN_JUMP");
        dict.put(CONTINGENCY_SEQUIN_STITCH, "CONTINGENCY_SEQUIN_STITCH");
        dict.put(CONTINGENCY_SEQUIN_REMOVE, "CONTINGENCY_SEQUIN_REMOVE");
        return dict;
    }
}
