package org.embroideryio.embroideryio;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import static org.embroideryio.embroideryio.EmbConstant.*;

public class CsvWriter extends EmbWriter {

    Map<Integer, String> names = EmbFunctions.get_common_name_dictionary();
    StringBuilder sb = new StringBuilder();

    public CsvWriter() {
        super();
        settings.put(EmbEncoder.PROP_ENCODE, false);
    }

    private void wc(String... values) throws IOException {
        write(csv(values));
    }

    private String csv(String... values) {
        sb.setLength(0);
        for (String s : values) {
            if (s == null) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(String.format(Locale.ENGLISH, "\"%s\"", s));
        }
        sb.append('\n');
        return sb.toString();
    }

    private double distance(double dx, double dy) {
        dx *= dx;
        dy *= dy;
        return Math.sqrt(dx + dy);
    }

    private double angle(double dx, double dy) {
        double tau = Math.PI * 2;
        double angle = Math.atan2(dy, dx);
        angle += (tau / 2.0f);
        angle /= tau;
        return angle;
    }

    public static final String PROP_DELTAS = "deltas";
    public static final String PROP_DISPLACEMENT = "displacement";

    public void write_data() throws IOException {
        Map<Integer, String> names = EmbFunctions.get_common_name_dictionary();
        float[] bounds = pattern.getBounds();
        double width = bounds[2] - bounds[0];
        double height = bounds[3] - bounds[1];
        wc("#", "[VAR_NAME]", "[VAR_VALUE]");
        int count_stitches = pattern.size();
        wc(">", "STITCH_COUNT:", Integer.toString(count_stitches));
        int[] counts = new int[COMMAND_MASK];
        for (int i = 0, ie = pattern.size(); i < ie; i++) {
            int data = pattern.getData(i) & COMMAND_MASK;
            counts[data] += 1;
        }

        wc(">", "THREAD_COUNT:", Integer.toString(counts[COLOR_CHANGE]));
        wc(">", "NEEDLE_COUNT:", Integer.toString(counts[NEEDLE_SET]));
        wc(">", "EXTENTS_LEFT:", Float.toString(bounds[0]));
        wc(">", "EXTENTS_TOP:", Float.toString(bounds[1]));
        wc(">", "EXTENTS_RIGHT:", Float.toString(bounds[2]));
        wc(">", "EXTENTS_BOTTOM:", Float.toString(bounds[3]));
        wc(">", "EXTENTS_LEFT:", Double.toString(width));
        wc(">", "EXTENTS_LEFT:", Double.toString(height));
        for (int i = 0; i < COMMAND_MASK; i++) {
            if (counts[i] == 0) {
                continue;
            }
            String name = names.get(i);
            if (name == null) {
                name = "COMMAND_UNKNOWN_" + i;
            } else {
                name = "COMMAND_" + name;
            }
            wc(">", name, Integer.toString(i));
        }
        write("\n");

    }

    public void write_metadata() throws IOException {
        HashMap<String, String> metadata = pattern.getMetadata();
        if (!metadata.isEmpty()) {
            wc("#", "[METADATA_NAME]", "[METADATA]");
            for (Entry<String, String> entry : metadata.entrySet()) {
                wc("@", entry.getKey(), entry.getValue());
            }
            write("\n");
        }

    }

    public void write_threads() throws IOException {
        if (pattern.getThreadCount() != 0) {
            wc("#",
                    "[THREAD_NUMBER]",
                    "[HEX_COLOR]",
                    "[DESCRIPTION]",
                    "[BRAND]",
                    "[CATALOG_NUMBER]",
                    "[DETAILS]",
                    "[WEIGHT]");
            int itr = 0;
            for (EmbThread thread : pattern.threadlist) {
                wc("$",
                        Integer.toString(itr++),
                        thread.getHexColor(),
                        thread.getDescription(),
                        thread.getBrand(),
                        thread.getCatalogNumber(),
                        thread.getDetails(),
                        thread.getWeight()
                );
            }
            write("\n");
        }
    }

    public String decoded_name(int data) {
        Integer[] decode = EmbFunctions.decode_embroidery_command(data);
        String name = names.get(decode[0]);
        if (name == null) {
            name = "UNKNOWN_" + decode[0];
        }
        if (decode[1] != null) {
            name = name + " t" + decode[1];
        }
        if (decode[2] != null) {
            name = name + " n" + decode[2];
        }
        if (decode[3] != null) {
            name = name + " o" + decode[3];
        }
        return name;

    }

    public void write_stitches_displacement() throws IOException {
        wc("#",
                "[STITCH_INDEX]",
                "[STITCH_TYPE]",
                "[X]",
                "[Y]",
                "[DX]",
                "[DY]",
                "[R]",
                "[ANGLE]"
        );
        float current_x = 0;
        float current_y = 0;
        for (int i = 0, ie = pattern.size(); i < ie; i++) {
            int data = pattern.getData(i);
            String name = decoded_name(data);
            float dx = pattern.getX(i) - current_x;
            float dy = pattern.getY(i) - current_y;
            wc("*",
                    Integer.toString(i),
                    name,
                    Float.toString(pattern.getX(i)),
                    Float.toString(pattern.getY(i)),
                    Float.toString(dx),
                    Float.toString(dy),
                    Double.toString(distance(dx, dy)),
                    Double.toString(angle(dx, dy))
            );
        }
    }

    public void write_stitches_deltas() throws IOException {
        wc("#",
                "[STITCH_INDEX]",
                "[STITCH_TYPE]",
                "[X]",
                "[Y]",
                "[DX]",
                "[DY]"
        );
        float current_x = 0;
        float current_y = 0;
        for (int i = 0, ie = pattern.size(); i < ie; i++) {
            int data = pattern.getData(i);
            String name = decoded_name(data);
            float dx = pattern.getX(i) - current_x;
            float dy = pattern.getY(i) - current_y;
            wc("*",
                    Integer.toString(i),
                    name,
                    Float.toString(pattern.getX(i)),
                    Float.toString(pattern.getY(i)),
                    Float.toString(dx),
                    Float.toString(dy)
            );
        }
    }

    public void write_stitches() throws IOException {
        wc("#",
                "[STITCH_INDEX]",
                "[STITCH_TYPE]",
                "[X]",
                "[Y]"
        );
        for (int i = 0, ie = pattern.size(); i < ie; i++) {
            int data = pattern.getData(i);
            String name = decoded_name(data);
            wc("*",
                    Integer.toString(i),
                    name,
                    Float.toString(pattern.getX(i)),
                    Float.toString(pattern.getY(i))
            );
        }
    }

    @Override
    public void write() throws IOException {
        boolean deltas = getBoolean(PROP_DELTAS, false);
        boolean displacement = getBoolean(PROP_DISPLACEMENT, false);

        write_data();
        write_metadata();
        write_threads();

        if (pattern.size() > 0) {
            if (displacement) {
                write_stitches_displacement();
            } else if (deltas) {
                write_stitches_deltas();
            } else {
                write_stitches();
            }
        }

    }
}
