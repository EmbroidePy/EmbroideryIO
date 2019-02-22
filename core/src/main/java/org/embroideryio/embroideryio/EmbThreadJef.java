package org.embroideryio.embroideryio;

/**
 * Created by Tat on 12/10/2016.
 */

public class EmbThreadJef extends EmbThread {

    public static EmbThreadJef[] getThreadSet() {
        return new EmbThreadJef[]{
                new EmbThreadJef(0, 0, 0, "Unknown", "0"),
                new EmbThreadJef(0, 0, 0, "Black", "1"),
                new EmbThreadJef(255, 255, 255, "White", "2"),
                new EmbThreadJef(255, 255, 23, "Sunflower", "3"),
                new EmbThreadJef(250, 160, 96, "Hazel", "4"),
                new EmbThreadJef(92, 118, 73, "Olive Green", "5"), //was green dust
                new EmbThreadJef(64, 192, 48, "Green", "6"),
                new EmbThreadJef(101, 194, 200, "Sky", "7"),
                new EmbThreadJef(172, 128, 190, "Purple", "8"),
                new EmbThreadJef(245, 188, 203, "Pink", "9"),
                new EmbThreadJef(255, 0, 0, "Red", "10"),
                new EmbThreadJef(192, 128, 0, "Brown", "11"),
                new EmbThreadJef(0, 0, 240, "Blue", "12"),
                new EmbThreadJef(228, 195, 93, "Gold", "13"),
                new EmbThreadJef(165, 42, 42, "Dark Brown", "14"),
                new EmbThreadJef(213, 176, 212, "Pale Violet", "15"),
                new EmbThreadJef(252, 242, 148, "Pale Yellow", "16"),
                new EmbThreadJef(240, 208, 192, "Pale Pink", "17"),
                new EmbThreadJef(255, 192, 0, "Peach", "18"),
                new EmbThreadJef(201, 164, 128, "Beige", "19"),
                new EmbThreadJef(155, 61, 75, "Wine Red", "20"),
                new EmbThreadJef(160, 184, 204, "Pale Sky", "21"),
                new EmbThreadJef(127, 194, 28, "Yellow Green", "22"),
                new EmbThreadJef(185, 185, 185, "Silver Grey", "23"),
                new EmbThreadJef(160, 160, 160, "Grey", "24"),
                new EmbThreadJef(152, 214, 189, "Pale Aqua", "25"),
                new EmbThreadJef(184, 240, 240, "Baby Blue", "26"),
                new EmbThreadJef(54, 139, 160, "Powder Blue", "27"),
                new EmbThreadJef(79, 131, 171, "Bright Blue", "28"),
                new EmbThreadJef(56, 106, 145, "Slate Blue", "29"),
                new EmbThreadJef(0, 32, 107, "Nave Blue", "30"),
                new EmbThreadJef(229, 197, 202, "Salmon Pink", "31"),
                new EmbThreadJef(249, 103, 107, "Coral", "32"),
                new EmbThreadJef(227, 49, 31, "Burnt Orange", "33"),
                new EmbThreadJef(226, 161, 136, "Cinnamon", "34"),
                new EmbThreadJef(181, 148, 116, "Umber", "35"),
                new EmbThreadJef(228, 207, 153, "Blonde", "36"),
                new EmbThreadJef(225, 203, 0, "Sunflower", "37"),
                new EmbThreadJef(225, 173, 212, "Orchid Pink", "38"),
                new EmbThreadJef(195, 0, 126, "Peony Purple", "39"),
                new EmbThreadJef(128, 0, 75, "Burgundy", "40"),
                new EmbThreadJef(160, 96, 176, "Royal Purple", "41"),
                new EmbThreadJef(192, 64, 32, "Cardinal Red", "42"),
                new EmbThreadJef(202, 224, 192, "Opal Green", "43"),
                new EmbThreadJef(137, 152, 86, "Moss Green", "44"),
                new EmbThreadJef(0, 170, 0, "Meadow Green", "45"),
                new EmbThreadJef(33, 138, 33, "Dark Green", "46"),
                new EmbThreadJef(93, 174, 148, "Aquamarine", "47"),
                new EmbThreadJef(76, 191, 143, "Emerald Green", "48"),
                new EmbThreadJef(0, 119, 114, "Peacock Green", "49"),
                new EmbThreadJef(112, 112, 112, "Dark Grey", "50"),
                new EmbThreadJef(242, 255, 255, "Ivory White", "51"),
                new EmbThreadJef(177, 88, 24, "Hazel", "52"),
                new EmbThreadJef(203, 138, 7, "Toast", "53"),
                new EmbThreadJef(247, 146, 123, "Salmon", "54"),
                new EmbThreadJef(152, 105, 45, "Cocoa Brown", "55"),
                new EmbThreadJef(162, 113, 72, "Sienna", "56"),
                new EmbThreadJef(123, 85, 74, "Sepia", "57"),
                new EmbThreadJef(79, 57, 70, "Dark Sepia", "58"),
                new EmbThreadJef(82, 58, 151, "Violet Blue", "59"),
                new EmbThreadJef(0, 0, 160, "Blue Ink", "60"),
                new EmbThreadJef(0, 150, 222, "Solar Blue", "61"),
                new EmbThreadJef(178, 221, 83, "Green Dust", "62"),
                new EmbThreadJef(250, 143, 187, "Crimson", "63"),
                new EmbThreadJef(222, 100, 158, "Floral Pink", "64"),
                new EmbThreadJef(181, 80, 102, "Wine", "65"),
                new EmbThreadJef(94, 87, 71, "Olive Drab", "66"),
                new EmbThreadJef(76, 136, 31, "Meadow", "67"),
                new EmbThreadJef(228, 220, 121, "Canary Yellow", "68"),
                new EmbThreadJef(203, 138, 26, "Toast", "69"),
                new EmbThreadJef(198, 170, 66, "Beige", "70"),
                new EmbThreadJef(236, 176, 44, "Honey Dew", "71"),
                new EmbThreadJef(248, 128, 64, "Tangerine", "72"),
                new EmbThreadJef(255, 229, 5, "Ocean Blue", "73"),
                new EmbThreadJef(250, 122, 122, "Sepia", "74"),
                new EmbThreadJef(107, 224, 0, "Royal Purple", "75"),
                new EmbThreadJef(56, 108, 174, "Yellow Ocher", "76"),
                new EmbThreadJef(208, 186, 176, "Beige Grey", "77"),
                new EmbThreadJef(227, 190, 129, "Bamboo", "78")
        };
    }

    public EmbThreadJef(int red, int green, int blue, String description, String catalogNumber) {
        super(red, green, blue, description, catalogNumber, "Jef","Jef");
    }
}
