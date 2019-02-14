package org.embroideryio.embroideryio;

/**
 * Created by Tat on 12/10/2016.
 */

public class EmbThreadShv extends EmbThread {

    public static EmbThreadShv[] getThreadSet() {
        return new EmbThreadShv[]{
                new EmbThreadShv(0, 0, 0, "Black", "0"),
                new EmbThreadShv(0, 0, 255, "Blue", "1"),
                new EmbThreadShv(51, 204, 102, "Green", "2"),
                new EmbThreadShv(255, 0, 0, "Red", "3"),
                new EmbThreadShv(255, 0, 255, "Purple", "4"),
                new EmbThreadShv(255, 255, 0, "Yellow", "5"),
                new EmbThreadShv(127, 127, 127, "Gray", "6"),
                new EmbThreadShv(51, 154, 255, "Light Blue", "7"),
                new EmbThreadShv(0, 255, 0, "Light Green", "8"),
                new EmbThreadShv(255, 127, 0, "Orange", "9"),
                new EmbThreadShv(255, 160, 180, "Pink", "10"),
                new EmbThreadShv(153, 75, 0, "Brown", "11"),
                new EmbThreadShv(255, 255, 255, "White", "12"),
                new EmbThreadShv(0, 0, 0, "Black", "13"),
                new EmbThreadShv(0, 0, 0, "Black", "14"),
                new EmbThreadShv(0, 0, 0, "Black", "15"),
                new EmbThreadShv(0, 0, 0, "Black", "16"),
                new EmbThreadShv(0, 0, 0, "Black", "17"),
                new EmbThreadShv(0, 0, 0, "Black", "18"),
                new EmbThreadShv(255, 127, 127, "Light Red", "19"),
                new EmbThreadShv(255, 127, 255, "Light Purple", "20"),
                new EmbThreadShv(255, 255, 153, "Light Yellow", "21"),
                new EmbThreadShv(192, 192, 192, "Light Gray", "22"),
                new EmbThreadShv(0, 0, 0, "Black", "23"),
                new EmbThreadShv(0, 0, 0, "Black", "24"),
                new EmbThreadShv(255, 165, 65, "Light Orange", "25"),
                new EmbThreadShv(255, 204, 204, "Light Pink", "26"),
                new EmbThreadShv(175, 90, 10, "Light Brown", "27"),
                new EmbThreadShv(0, 0, 0, "Black", "28"),
                new EmbThreadShv(0, 0, 0, "Black", "29"),
                new EmbThreadShv(0, 0, 0, "Black", "30"),
                new EmbThreadShv(0, 0, 0, "Black", "31"),
                new EmbThreadShv(0, 0, 0, "Black", "32"),
                new EmbThreadShv(0, 0, 127, "Dark Blue", "33"),
                new EmbThreadShv(0, 127, 0, "Dark Green", "34"),
                new EmbThreadShv(127, 0, 0, "Dark Red", "35"),
                new EmbThreadShv(127, 0, 127, "Dark Purple", "36"),
                new EmbThreadShv(200, 200, 0, "Dark Yellow", "37"),
                new EmbThreadShv(60, 60, 60, "Dark Gray", "38"),
                new EmbThreadShv(0, 0, 0, "Black", "39"),
                new EmbThreadShv(0, 0, 0, "Black", "40"),
                new EmbThreadShv(232, 63, 0, "Dark Orange", "41"),
                new EmbThreadShv(255, 102, 122, "Dark Pink", "42")
        };
    }

    public EmbThreadShv(int red, int green, int blue, String description, String catalogNumber) {
        super(red, green, blue, description, catalogNumber, "Shv", "Shv");
    }
}
