package org.embroideryio.embroideryio;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class CsvReader extends EmbReader {

    public static Iterable<String[]> parseCSV(final InputStream stream) throws IOException {
        return new Iterable<String[]>() {
            @Override
            public Iterator<String[]> iterator() {
                return new Iterator<String[]>() {
                    static final int UNCALCULATED = 0;
                    static final int READY = 1;
                    static final int FINISHED = 2;
                    int state = UNCALCULATED;
                    ArrayList<String> value_list = new ArrayList<>();
                    StringBuilder sb = new StringBuilder();
                    String[] return_value;

                    public void end() {
                        end_part();
                        return_value = new String[value_list.size()];
                        value_list.toArray(return_value);
                        value_list.clear();
                    }

                    public void end_part() {
                        value_list.add(sb.toString());
                        sb.setLength(0);
                    }

                    public void append(int ch) {
                        sb.append((char) ch);
                    }

                    public void calculate() throws IOException {
                        boolean inquote = false;
                        while (true) {
                            int ch = stream.read();
                            switch (ch) {
                                default: //regular character.
                                    append(ch);
                                    break;
                                case -1: //read has reached the end.
                                    if ((sb.length() == 0) && (value_list.isEmpty())) {
                                        state = FINISHED;
                                    } else {
                                        end();
                                        state = READY;
                                    }
                                    return;
                                case '\r':
                                case '\n': //end of line.
                                    if (inquote) {
                                        append(ch);
                                    } else {
                                        end();
                                        state = READY;
                                        return;
                                    }
                                    break;
                                case ',': //comma
                                    if (inquote) {
                                        append(ch);
                                    } else {
                                        end_part();
                                        break;
                                    }
                                    break;
                                case '"': //quote.
                                    inquote = !inquote;
                                    break;
                            }
                        }
                    }

                    @Override
                    public boolean hasNext() {
                        if (state == UNCALCULATED) {
                            try {
                                calculate();
                            } catch (IOException ex) {
                            }
                        }
                        return state == READY;
                    }

                    @Override
                    public String[] next() {
                        if (state == UNCALCULATED) {
                            try {
                                calculate();
                            } catch (IOException ex) {
                            }
                        }
                        state = UNCALCULATED;
                        return return_value;
                    }
                };
            }
        };
    }

    @Override

    protected void read() throws IOException {
        Map<String, Integer> cmd_dict = EmbFunctions.get_command_dictionary();

        for (String[] row : parseCSV(stream)) {
            switch (row[0]) {
                case ">":
                    break;
                case "#":
                    break;
                case "@":
                    if (row.length != 3) {
                        continue;
                    }
                    String metadata_name = row[1];
                    String metadata = row[2];
                    pattern.setMetadata(metadata_name, metadata);
                    break;
                case "*":
                    String[] split = row[2].split(" ");
                    int cmd = cmd_dict.get(split[0]);
                    for (String sp : split) {
                        switch (sp.substring(0, 1)) {
                            case "n":
                                String needle_string = sp.substring(1);
                                int needle = Integer.parseInt(needle_string);
                                cmd |= (needle + 1) << 16;
                                break;
                            case "o":
                                String order_string = sp.substring(1);
                                int order = Integer.parseInt(order_string);
                                cmd |= (order + 1) << 24;
                                break;
                            case "t":
                                String thread_string = sp.substring(1);
                                int thread = Integer.parseInt(thread_string);
                                cmd |= (thread + 1) << 8;
                                break;
                        }
                    }
                    if (row.length == 3) {
                        pattern.addStitchRel(0, 0, cmd);
                    } else {
                        String string_x = row[3];
                        String string_y = row[4];
                        float x = Float.valueOf(string_x);
                        float y = Float.valueOf(string_y);
                        pattern.addStitchAbs(x, y, cmd);
                    }
                    break;
                case "$":
                    EmbThread thread = new EmbThread();
                    if ((row.length == 7)
                            && (row[2].length() <= 3)
                            && (row[3].length() <= 3)
                            && (row[4].length() <= 3)) {
                        //This is an embroidermodder csv file, I changed the colors and added more details.
                        //[THREAD_NUMBER], [RED], [GREEN], [BLUE], [DESCRIPTION], [CATALOG_NUMBER]\"\n");
                        thread.setColor(
                                Integer.parseInt(row[2]),
                                Integer.parseInt(row[2]),
                                Integer.parseInt(row[2]));
                        thread.setDescription(row[5]);
                        thread.setDescription(row[6]);
                    } else {
                        try {
                            String hex_color = row[2];
                            thread.setStringColor(hex_color);
                            String desc = row[3];
                            thread.setDescription(desc);
                            String brand = row[4];
                            thread.setBrand(brand);
                            String cat_num = row[5];
                            thread.setCatalogNumber(cat_num);
                            String details = row[6];
                            thread.setDetails(details);
                            String weight = row[7];
                            thread.setWeight(weight);
                        } catch (Exception ignore) {
                        }
                    }
                    pattern.add(thread);
                    break;
                case "":
                    break;
                default:
                    break;
            }
        }
    }
}
