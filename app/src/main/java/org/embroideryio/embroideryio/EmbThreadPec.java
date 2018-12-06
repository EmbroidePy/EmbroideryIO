package org.embroideryio.embroideryio;

/**
 * Created by Tat on 12/8/2016.
 */


public class EmbThreadPec extends EmbThread {

    public static EmbThreadPec[] getThreadSet() {
        return new EmbThreadPec[]{
                new EmbThreadPec(0, 0, 0, "Unknown", "0"),
                new EmbThreadPec(14, 31, 124, "Prussian Blue", "1"),
                new EmbThreadPec(10, 85, 163, "Blue", "2"),
                new EmbThreadPec(0, 135, 119, "Teal Green", "3"),
                new EmbThreadPec(75, 107, 175, "Cornflower Blue", "4"),
                new EmbThreadPec(237, 23, 31, "Red", "5"),
                new EmbThreadPec(209, 92, 0, "Reddish Brown", "6"),
                new EmbThreadPec(145, 54, 151, "Magenta", "7"),
                new EmbThreadPec(228, 154, 203, "Light Lilac", "8"),
                new EmbThreadPec(145, 95, 172, "Lilac", "9"),
                new EmbThreadPec(158, 214, 125, "Mint Green", "10"),
                new EmbThreadPec(232, 169, 0, "Deep Gold", "11"),
                new EmbThreadPec(254, 186, 53, "Orange", "12"),
                new EmbThreadPec(255, 255, 0, "Yellow", "13"),
                new EmbThreadPec(112, 188, 31, "Lime Green", "14"),
                new EmbThreadPec(186, 152, 0, "Brass", "15"),
                new EmbThreadPec(168, 168, 168, "Silver", "16"),
                new EmbThreadPec(125, 111, 0, "Russet Brown", "17"),
                new EmbThreadPec(255, 255, 179, "Cream Brown", "18"),
                new EmbThreadPec(79, 85, 86, "Pewter", "19"),
                new EmbThreadPec(0, 0, 0, "Black", "20"),
                new EmbThreadPec(11, 61, 145, "Ultramarine", "21"),
                new EmbThreadPec(119, 1, 118, "Royal Purple", "22"),
                new EmbThreadPec(41, 49, 51, "Dark Gray", "23"),
                new EmbThreadPec(42, 19, 1, "Dark Brown", "24"),
                new EmbThreadPec(246, 74, 138, "Deep Rose", "25"),
                new EmbThreadPec(178, 118, 36, "Light Brown", "26"),
                new EmbThreadPec(252, 187, 197, "Salmon Pink", "27"),
                new EmbThreadPec(254, 55, 15, "Vermillion", "28"),
                new EmbThreadPec(240, 240, 240, "White", "29"),
                new EmbThreadPec(106, 28, 138, "Violet", "30"),
                new EmbThreadPec(168, 221, 196, "Seacrest", "31"),
                new EmbThreadPec(37, 132, 187, "Sky Blue", "32"),
                new EmbThreadPec(254, 179, 67, "Pumpkin", "33"),
                new EmbThreadPec(255, 243, 107, "Cream Yellow", "34"),
                new EmbThreadPec(208, 166, 96, "Khaki", "35"),
                new EmbThreadPec(209, 84, 0, "Clay Brown", "36"),
                new EmbThreadPec(102, 186, 73, "Leaf Green", "37"),
                new EmbThreadPec(19, 74, 70, "Peacock Blue", "38"),
                new EmbThreadPec(135, 135, 135, "Gray", "39"),
                new EmbThreadPec(216, 204, 198, "Warm Gray", "40"),
                new EmbThreadPec(67, 86, 7, "Dark Olive", "41"),
                new EmbThreadPec(253, 217, 222, "Flesh Pink", "42"),
                new EmbThreadPec(249, 147, 188, "Pink", "43"),
                new EmbThreadPec(0, 56, 34, "Deep Green", "44"),
                new EmbThreadPec(178, 175, 212, "Lavender", "45"),
                new EmbThreadPec(104, 106, 176, "Wisteria Violet", "46"),
                new EmbThreadPec(239, 227, 185, "Beige", "47"),
                new EmbThreadPec(247, 56, 102, "Carmine", "48"),
                new EmbThreadPec(181, 75, 100, "Amber Red", "49"),
                new EmbThreadPec(19, 43, 26, "Olive Green", "50"),
                new EmbThreadPec(199, 1, 86, "Dark Fuschia", "51"),
                new EmbThreadPec(254, 158, 50, "Tangerine", "52"),
                new EmbThreadPec(168, 222, 235, "Light Blue", "53"),
                new EmbThreadPec(0, 103, 62, "Emerald Green", "54"),
                new EmbThreadPec(78, 41, 144, "Purple", "55"),
                new EmbThreadPec(47, 126, 32, "Moss Green", "56"),
                new EmbThreadPec(255, 204, 204, "Flesh Pink", "57"),
                new EmbThreadPec(255, 217, 17, "Harvest Gold", "58"),
                new EmbThreadPec(9, 91, 166, "Electric Blue", "59"),
                new EmbThreadPec(240, 249, 112, "Lemon Yellow", "60"),
                new EmbThreadPec(227, 243, 91, "Fresh Green", "61"),
                new EmbThreadPec(255, 153, 0, "Orange", "62"),
                new EmbThreadPec(255, 240, 141, "Cream Yellow", "63"),
                new EmbThreadPec(255, 200, 200, "Applique", "64")
        };
    }


    public EmbThreadPec(int red, int green, int blue, String description, String catalogNumber) {
        super(red, green, blue, description, catalogNumber, "Brother", "Brother");
    }
}
