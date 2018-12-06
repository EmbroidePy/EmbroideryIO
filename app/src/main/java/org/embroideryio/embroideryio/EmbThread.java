package org.embroideryio.embroideryio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmbThread {
    private static final String VALUE_NONE = "none";
    public static final String PROP_COLOR = "color";
    public static final String PROP_DESCRIPTION = "description";
    public static final String PROP_CATALOGNUMBER = "catalognumber";
    public static final String PROP_DETAILS = "details";
    public static final String PROP_BRAND = "brand";
    public static final String PROP_CHART = "chart";
    public static final String PROP_WEIGHT = "weight";

    protected int color;
    protected String description; //also colorname
    protected String catalogNumber;
    protected String details;
    protected String brand; //also manufacturer,
    protected String chart; //also source.
    protected String weight;

    public EmbThread() {
    }

    public EmbThread(int color) {
        this.color = 0xFF000000 | color;
    }

    public EmbThread(int color, String description) {
        this.color = 0xFF000000 | color;
        this.description = description;
    }

    public EmbThread(int color, String description, String catalogNumber, String brand, String chart) {
        this.color = 0xFF000000 | color;
        this.description = description;
        this.catalogNumber = catalogNumber;
        this.brand = brand;
        this.chart = chart;
    }

    public EmbThread(int color, String description, String catalogNumber) {
        this.color = 0xFF000000 | color;
        this.description = description;
        this.catalogNumber = catalogNumber;
    }

    public EmbThread(int red, int green, int blue, String description, String catalogNumber) {
        this.color = newColor(red,green,blue);
        this.description = description;
        this.catalogNumber = catalogNumber;
    }

    public EmbThread(EmbThread embthread) {
        this.color = embthread.color;
        this.description = embthread.description;
        this.catalogNumber = embthread.catalogNumber;
        this.details = embthread.details;
        this.brand = embthread.brand;
        this.chart = embthread.chart;
        this.weight = embthread.weight;
    }

    public EmbThread(int red, int green, int blue, String description, String catalogNumber, String brand, String chart) {
        this.color = newColor(red,green,blue);
        this.description = description;
        this.catalogNumber = catalogNumber;
        this.brand = brand;
        this.chart = chart;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValidatedCatalogNumber() {
        return catalogNumber.replaceAll("[^\\d]", "");
    }

    public String getCatalogNumber() {
        return catalogNumber;
    }

    public void setCatalogNumber(String catalogNumber) {
        this.catalogNumber = catalogNumber;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getChart() {
        return chart;
    }

    public void setChart(String chart) {
        this.chart = chart;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHexColor() {
        return getHexColor(color);
    }

    public static String getHexColor(int color) {
        return String.format("#%02x%02x%02x", getRed(color), getGreen(color), getBlue(color));
    }

    public String getRGBColor() {
        return getRed() + "," + getGreen() + "," + getBlue();
    }

    public String getRGBColorFormat() {
        return " ( " + getRed() + ", " + getGreen() + ", " + getBlue() + " ) ";
    }

    public void setStringColor(String v) {
        setColor(parseColor(v));
    }

    public int getOpaqueColor() {
        return 0xFF000000 | color;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setColor(int r, int g, int b) {
        this.color = newColor(r, g, b);
    }

    public int getRed() {
        return (color >> 16) & 0xFF;
    }

    public int getGreen() {
        return (color >> 8) & 0xFF;
    }

    public static int getBlue(int color) {
        return (color) & 0xFF;
    }

    public static int getRed(int color) {
        return (color >> 16) & 0xFF;
    }

    public static int getGreen(int color) {
        return (color >> 8) & 0xFF;
    }

    public int getBlue() {
        return (color) & 0xFF;
    }
    @Override
    public int hashCode() {
        int result = color;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (catalogNumber != null ? catalogNumber.hashCode() : 0);
        result = 31 * result + (details != null ? details.hashCode() : 0);
        result = 31 * result + (brand != null ? brand.hashCode() : 0);
        result = 31 * result + (chart != null ? chart.hashCode() : 0);
        result = 31 * result + (weight != null ? weight.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmbThread embThread = (EmbThread) o;

        if (color != embThread.color) return false;
        if (description != null ? !description.equals(embThread.description) : embThread.description != null)
            return false;
        if (catalogNumber != null ? !catalogNumber.equals(embThread.catalogNumber) : embThread.catalogNumber != null)
            return false;
        if (details != null ? !details.equals(embThread.details) : embThread.details != null)
            return false;
        if (brand != null ? !brand.equals(embThread.brand) : embThread.brand != null) return false;
        if (chart != null ? !chart.equals(embThread.chart) : embThread.chart != null) return false;
        return weight != null ? weight.equals(embThread.weight) : embThread.weight == null;
    }

    public static <T extends EmbThread> int findNearestColor(int findColor, T[] values) {
        return findNearestThread(findColor, values).getColor();
    }

    public static <T extends EmbThread> int findNearestColor(int findColor, List<T> values) {
        return findNearestThread(findColor, values).getColor();
    }

    public static <T extends EmbThread> T findNearestThread(int findColor, T[] values) {
        int index = EmbThread.findNearestIndex(findColor, values);
        return values[index];
    }

    public static <T extends EmbThread> T findNearestThread(int findColor, List<T> values) {
        int index = findNearestColorIndex(findColor, values);
        return values.get(index);
    }

    public static <T extends EmbThread> int findNearestIndex(int findColor, T[] values) {
        double currentClosestValue = Double.POSITIVE_INFINITY;
        int red = (findColor >> 16) & 0xff;
        int green = (findColor >> 8) & 0xff;
        int blue = (findColor) & 0xff;

        int closestIndex = -1;
        int currentIndex = -1;
        for (EmbThread thread : values) {
            currentIndex++;
            if (thread == null) {
                continue;
            }
            double dist = distanceRedMean(red, green, blue, thread.getRed(), thread.getGreen(), thread.getBlue());
            if (dist <= currentClosestValue) {
                currentClosestValue = dist;
                closestIndex = currentIndex;
            }
        }
        return closestIndex;
    }

    public static <T extends EmbThread> int findNearestColorIndex(int findColor, Iterable<T> values) {
        double currentClosestValue = Double.POSITIVE_INFINITY;
        int red = (findColor >> 16) & 0xff;
        int green = (findColor >> 8) & 0xff;
        int blue = (findColor) & 0xff;

        int closestIndex = -1;
        int currentIndex = -1;
        for (EmbThread thread : values) {
            currentIndex++;
            if (thread == null) {
                continue;
            }
            double dist = distanceRedMean(red, green, blue, thread.getRed(), thread.getGreen(), thread.getBlue());
            if (dist <= currentClosestValue) {
                currentClosestValue = dist;
                closestIndex = currentIndex;
            }
        }
        return closestIndex;
    }

    public static double distanceRedMean(int r1, int g1, int b1, int r2, int g2, int b2) {
        long rmean = ((long) r1 + (long) r2) / 2;
        long r = (long) r1 - (long) r2;
        long g = (long) g1 - (long) g2;
        long b = (long) b1 - (long) b2;
        return (((512 + rmean) * r * r) >> 8) + 4 * g * g + (((767 - rmean) * b * b) >> 8);
    }

    public static int parseColor(String color) {
        if (color == null) {
            return 0;
        }
        if (color.equalsIgnoreCase(VALUE_NONE)) {
            return 0;
        }
        if (color.matches("\\d+,\\s*\\d+,\\s*\\d+")) {
            String delims = "[,]";
            String[] RGB = color.split(delims);
            try {
                int r = Integer.parseInt(RGB[0]);
                int g = Integer.parseInt(RGB[1]);
                int b = Integer.parseInt(RGB[2]);
                int or = (r | g | b);
                if ((or & 0xFF) == or) {
                    return newColor(r, g, b);
                }
            } catch (NumberFormatException ex) {
            }

        }
        return parseHex(color);
    }

    public static int parseHex(String color) {
        if (color == null) {
            return 0;
        }
        Pattern PATTERN_HEX = Pattern.compile("#?([0-9A-Fa-f]+)");
        Matcher m;
        m = PATTERN_HEX.matcher(color);
        if (m.find()) {
            int a = 255, r = 0, g = 0, b = 0;
            String rgb = m.group(1);
            try {
                switch (rgb.length()) {
                    case 3:
                        r = Integer.parseInt(rgb.substring(0, 1) + rgb.substring(0, 1), 16);
                        g = Integer.parseInt(rgb.substring(1, 2) + rgb.substring(1, 2), 16);
                        b = Integer.parseInt(rgb.substring(2, 3) + rgb.substring(2, 3), 16);
                        break;
                    case 5: //error color;
                    case 4:
                        a = Integer.parseInt(rgb.substring(0, 1) + rgb.substring(0, 1), 16);
                        r = Integer.parseInt(rgb.substring(1, 2) + rgb.substring(1, 2), 16);
                        g = Integer.parseInt(rgb.substring(2, 3) + rgb.substring(2, 3), 16);
                        b = Integer.parseInt(rgb.substring(3, 4) + rgb.substring(3, 4), 16);
                        break;
                    case 7: //error color;
                    case 6:
                        r = Integer.parseInt(rgb.substring(0, 2), 16);
                        g = Integer.parseInt(rgb.substring(2, 4), 16);
                        b = Integer.parseInt(rgb.substring(4, 6), 16);
                        break;
                    case 8:
                        a = Integer.parseInt(rgb.substring(0, 2), 16);
                        r = Integer.parseInt(rgb.substring(2, 4), 16);
                        g = Integer.parseInt(rgb.substring(4, 6), 16);
                        b = Integer.parseInt(rgb.substring(6, 8), 16);
                        break;

                    default:
                }
            } catch (NumberFormatException ignored) {
            }
            int c = newColor(a, r, g, b);
            return c;
        }
        Pattern PATTERN_RGB = Pattern.compile("rgb\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\)");
        m = PATTERN_RGB.matcher(color);
        if (m.find()) {
            String r = m.group(1);
            String g = m.group(2);
            String b = m.group(3);
            int rp = Integer.decode(r);
            int gp = Integer.decode(g);
            int bp = Integer.decode(b);
            return newColor(rp, gp, bp);
        }
        Pattern PATTERN_PERCENT_RGB = Pattern.compile("rgb\\(\\s*(\\d+)%\\s*,\\s*(\\d+)%\\s*,\\s*(\\d+)%\\s*\\)");
        m = PATTERN_PERCENT_RGB.matcher(color);
        if (m.find()) {
            String r = m.group(1);
            String g = m.group(2);
            String b = m.group(3);
            int rp = Integer.decode(r);
            int gp = Integer.decode(g);
            int bp = Integer.decode(b);
            rp = (rp * 255) / 100;
            gp = (gp * 255) / 100;
            bp = (bp * 255) / 100;
            return newColor(rp, gp, bp);
        }

        return svgColor(color);
    }

    public static int newColor(int r, int g, int b) {
        return 0xFF000000 | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
    }

    public static int newColor(int a, int r, int g, int b) {
        return ((a & 255) << 24) | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
    }

    public static int svgColor(String color) {
        if (color == null) {
            return 0;
        }
        switch (color.toLowerCase()) {
            case VALUE_NONE:
                return -1;
            case "aliceblue":
                return newColor(240, 248, 255);
            case "antiquewhite":
                return newColor(250, 235, 215);
            case "aqua":
                return newColor(0, 255, 255);
            case "aquamarine":
                return newColor(127, 255, 212);
            case "azure":
                return newColor(240, 255, 255);
            case "beige":
                return newColor(245, 245, 220);
            case "bisque":
                return newColor(255, 228, 196);
            case "black":
                return newColor(0, 0, 0);
            case "blanchedalmond":
                return newColor(255, 235, 205);
            case "blue":
                return newColor(0, 0, 255);
            case "blueviolet":
                return newColor(138, 43, 226);
            case "brown":
                return newColor(165, 42, 42);
            case "burlywood":
                return newColor(222, 184, 135);
            case "cadetblue":
                return newColor(95, 158, 160);
            case "chartreuse":
                return newColor(127, 255, 0);
            case "chocolate":
                return newColor(210, 105, 30);
            case "coral":
                return newColor(255, 127, 80);
            case "cornflowerblue":
                return newColor(100, 149, 237);
            case "cornsilk":
                return newColor(255, 248, 220);
            case "crimson":
                return newColor(220, 20, 60);
            case "cyan":
                return newColor(0, 255, 255);
            case "darkblue":
                return newColor(0, 0, 139);
            case "darkcyan":
                return newColor(0, 139, 139);
            case "darkgoldenrod":
                return newColor(184, 134, 11);
            case "darkgray":
                return newColor(169, 169, 169);
            case "darkgreen":
                return newColor(0, 100, 0);
            case "darkgrey":
                return newColor(169, 169, 169);
            case "darkkhaki":
                return newColor(189, 183, 107);
            case "darkmagenta":
                return newColor(139, 0, 139);
            case "darkolivegreen":
                return newColor(85, 107, 47);
            case "darkorange":
                return newColor(255, 140, 0);
            case "darkorchid":
                return newColor(153, 50, 204);
            case "darkred":
                return newColor(139, 0, 0);
            case "darksalmon":
                return newColor(233, 150, 122);
            case "darkseagreen":
                return newColor(143, 188, 143);
            case "darkslateblue":
                return newColor(72, 61, 139);
            case "darkslategray":
                return newColor(47, 79, 79);
            case "darkslategrey":
                return newColor(47, 79, 79);
            case "darkturquoise":
                return newColor(0, 206, 209);
            case "darkviolet":
                return newColor(148, 0, 211);
            case "deeppink":
                return newColor(255, 20, 147);
            case "deepskyblue":
                return newColor(0, 191, 255);
            case "dimgray":
                return newColor(105, 105, 105);
            case "dimgrey":
                return newColor(105, 105, 105);
            case "dodgerblue":
                return newColor(30, 144, 255);
            case "firebrick":
                return newColor(178, 34, 34);
            case "floralwhite":
                return newColor(255, 250, 240);
            case "forestgreen":
                return newColor(34, 139, 34);
            case "fuchsia":
                return newColor(255, 0, 255);
            case "gainsboro":
                return newColor(220, 220, 220);
            case "ghostwhite":
                return newColor(248, 248, 255);
            case "gold":
                return newColor(255, 215, 0);
            case "goldenrod":
                return newColor(218, 165, 32);
            case "gray":
                return newColor(128, 128, 128);
            case "grey":
                return newColor(128, 128, 128);
            case "green":
                return newColor(0, 128, 0);
            case "greenyellow":
                return newColor(173, 255, 47);
            case "honeydew":
                return newColor(240, 255, 240);
            case "hotpink":
                return newColor(255, 105, 180);
            case "indianred":
                return newColor(205, 92, 92);
            case "indigo":
                return newColor(75, 0, 130);
            case "ivory":
                return newColor(255, 255, 240);
            case "khaki":
                return newColor(240, 230, 140);
            case "lavender":
                return newColor(230, 230, 250);
            case "lavenderblush":
                return newColor(255, 240, 245);
            case "lawngreen":
                return newColor(124, 252, 0);
            case "lemonchiffon":
                return newColor(255, 250, 205);
            case "lightblue":
                return newColor(173, 216, 230);
            case "lightcoral":
                return newColor(240, 128, 128);
            case "lightcyan":
                return newColor(224, 255, 255);
            case "lightgoldenrodyellow":
                return newColor(250, 250, 210);
            case "lightgray":
                return newColor(211, 211, 211);
            case "lightgreen":
                return newColor(144, 238, 144);
            case "lightgrey":
                return newColor(211, 211, 211);
            case "lightpink":
                return newColor(255, 182, 193);
            case "lightsalmon":
                return newColor(255, 160, 122);
            case "lightseagreen":
                return newColor(32, 178, 170);
            case "lightskyblue":
                return newColor(135, 206, 250);
            case "lightslategray":
                return newColor(119, 136, 153);
            case "lightslategrey":
                return newColor(119, 136, 153);
            case "lightsteelblue":
                return newColor(176, 196, 222);
            case "lightyellow":
                return newColor(255, 255, 224);
            case "lime":
                return newColor(0, 255, 0);
            case "limegreen":
                return newColor(50, 205, 50);
            case "linen":
                return newColor(250, 240, 230);
            case "magenta":
                return newColor(255, 0, 255);
            case "maroon":
                return newColor(128, 0, 0);
            case "mediumaquamarine":
                return newColor(102, 205, 170);
            case "mediumblue":
                return newColor(0, 0, 205);
            case "mediumorchid":
                return newColor(186, 85, 211);
            case "mediumpurple":
                return newColor(147, 112, 219);
            case "mediumseagreen":
                return newColor(60, 179, 113);
            case "mediumslateblue":
                return newColor(123, 104, 238);
            case "mediumspringgreen":
                return newColor(0, 250, 154);
            case "mediumturquoise":
                return newColor(72, 209, 204);
            case "mediumvioletred":
                return newColor(199, 21, 133);
            case "midnightblue":
                return newColor(25, 25, 112);
            case "mintcream":
                return newColor(245, 255, 250);
            case "mistyrose":
                return newColor(255, 228, 225);
            case "moccasin":
                return newColor(255, 228, 181);
            case "navajowhite":
                return newColor(255, 222, 173);
            case "navy":
                return newColor(0, 0, 128);
            case "oldlace":
                return newColor(253, 245, 230);
            case "olive":
                return newColor(128, 128, 0);
            case "olivedrab":
                return newColor(107, 142, 35);
            case "orange":
                return newColor(255, 165, 0);
            case "orangered":
                return newColor(255, 69, 0);
            case "orchid":
                return newColor(218, 112, 214);
            case "palegoldenrod":
                return newColor(238, 232, 170);
            case "palegreen":
                return newColor(152, 251, 152);
            case "paleturquoise":
                return newColor(175, 238, 238);
            case "palevioletred":
                return newColor(219, 112, 147);
            case "papayawhip":
                return newColor(255, 239, 213);
            case "peachpuff":
                return newColor(255, 218, 185);
            case "peru":
                return newColor(205, 133, 63);
            case "pink":
                return newColor(255, 192, 203);
            case "plum":
                return newColor(221, 160, 221);
            case "powderblue":
                return newColor(176, 224, 230);
            case "purple":
                return newColor(128, 0, 128);
            case "red":
                return newColor(255, 0, 0);
            case "rosybrown":
                return newColor(188, 143, 143);
            case "royalblue":
                return newColor(65, 105, 225);
            case "saddlebrown":
                return newColor(139, 69, 19);
            case "salmon":
                return newColor(250, 128, 114);
            case "sandybrown":
                return newColor(244, 164, 96);
            case "seagreen":
                return newColor(46, 139, 87);
            case "seashell":
                return newColor(255, 245, 238);
            case "sienna":
                return newColor(160, 82, 45);
            case "silver":
                return newColor(192, 192, 192);
            case "skyblue":
                return newColor(135, 206, 235);
            case "slateblue":
                return newColor(106, 90, 205);
            case "slategray":
                return newColor(112, 128, 144);
            case "slategrey":
                return newColor(112, 128, 144);
            case "snow":
                return newColor(255, 250, 250);
            case "springgreen":
                return newColor(0, 255, 127);
            case "steelblue":
                return newColor(70, 130, 180);
            case "tan":
                return newColor(210, 180, 140);
            case "teal":
                return newColor(0, 128, 128);
            case "thistle":
                return newColor(216, 191, 216);
            case "tomato":
                return newColor(255, 99, 71);
            case "turquoise":
                return newColor(64, 224, 208);
            case "violet":
                return newColor(238, 130, 238);
            case "wheat":
                return newColor(245, 222, 179);
            case "white":
                return newColor(255, 255, 255);
            case "whitesmoke":
                return newColor(245, 245, 245);
            case "yellow":
                return newColor(255, 255, 0);
            case "yellowgreen":
                return newColor(154, 205, 50);
        }
        return 0;
    }


    public HashMap<String,String> getMetadata() {
        HashMap<String,String> metadata = new HashMap<>();
        if (description != null) metadata.put(PROP_DESCRIPTION, description);
        if (catalogNumber != null) metadata.put(PROP_CATALOGNUMBER, catalogNumber);
        if (details != null) metadata.put(PROP_DETAILS, details);
        if (brand != null) metadata.put(PROP_BRAND, brand);
        if (chart != null) metadata.put(PROP_CHART, chart);
        if (weight != null) metadata.put(PROP_WEIGHT, weight);
        return metadata;
    }

    public void setMetadata(String key, String value) {
        switch (key) {
            case PROP_DESCRIPTION:
                description = value;
                break;
            case PROP_CATALOGNUMBER:
                catalogNumber = value;
                break;
            case PROP_DETAILS:
                details = value;
                break;
            case PROP_BRAND:
                brand = value;
                break;
            case PROP_CHART:
                chart = value;
                break;
            case PROP_WEIGHT:
                weight = value;
                break;
        }
    }

    public void setMetadata(Map<String,String> map) {
        description = map.get(PROP_DESCRIPTION);
        catalogNumber = map.get(PROP_CATALOGNUMBER);
        details = map.get(PROP_DETAILS);
        brand = map.get(PROP_BRAND);
        chart = map.get(PROP_CHART);
        weight = map.get(PROP_WEIGHT);
    }

    @Override
    public String toString() {
        return "EmbThread{" + "description=" + description + ", catalogNumber=" + catalogNumber + ", brand=" + brand + ", " + this.getHexColor() + '}';
    }

}
