package org.embroideryio.embroideryio;

/**
 * Created by Tat on 12/8/2016.
 */


public class EmbThreadSew extends EmbThread {

    public static EmbThreadSew[] getThreadSet() {
        return new EmbThreadSew[]{
                new EmbThreadSew(0, 0, 0, "Unknown", "0"),
                new EmbThreadSew(0, 0, 0, "Black", "1"),
                new EmbThreadSew(255, 255, 255, "White", "2"),
                new EmbThreadSew(255, 255, 23, "Sunflower", "3"),
                new EmbThreadSew(250, 160, 96, "Hazel", "4"),
                new EmbThreadSew(92, 118, 73, "Green Dust", "5"),
                new EmbThreadSew(64, 192, 48, "Green", "6"),
                new EmbThreadSew(101, 194, 200, "Sky", "7"),
                new EmbThreadSew(172, 128, 190, "Purple", "8"),
                new EmbThreadSew(245, 188, 203, "Pink", "9"),
                new EmbThreadSew(255, 0, 0, "Red", "10"),
                new EmbThreadSew(192, 128, 0, "Brown", "11"),
                new EmbThreadSew(0, 0, 240, "Blue", "12"),
                new EmbThreadSew(228, 195, 93, "Gold", "13"),
                new EmbThreadSew(165, 42, 42, "Dark Brown", "14"),
                new EmbThreadSew(213, 176, 212, "Pale Violet", "15"),
                new EmbThreadSew(252, 242, 148, "Pale Yellow", "16"),
                new EmbThreadSew(240, 208, 192, "Pale Pink", "17"),
                new EmbThreadSew(255, 192, 0, "Peach", "18"),
                new EmbThreadSew(201, 164, 128, "Beige", "19"),
                new EmbThreadSew(155, 61, 75, "Wine Red", "20"),
                new EmbThreadSew(160, 184, 204, "Pale Sky", "21"),
                new EmbThreadSew(127, 194, 28, "Yellow Green", "22"),
                new EmbThreadSew(185, 185, 185, "Silver Grey", "23"),
                new EmbThreadSew(160, 160, 160, "Grey", "24"),
                new EmbThreadSew(152, 214, 189, "Pale Aqua", "25"),
                new EmbThreadSew(184, 240, 240, "Baby Blue", "26"),
                new EmbThreadSew(54, 139, 160, "Powder Blue", "27"),
                new EmbThreadSew(79, 131, 171, "Bright Blue", "28"),
                new EmbThreadSew(56, 106, 145, "Slate Blue", "29"),
                new EmbThreadSew(0, 32, 107, "Nave Blue", "30"),
                new EmbThreadSew(229, 197, 202, "Salmon Pink", "31"),
                new EmbThreadSew(249, 103, 107, "Coral", "32"),
                new EmbThreadSew(227, 49, 31, "Burnt Orange", "33"),
                new EmbThreadSew(226, 161, 136, "Cinnamon", "34"),
                new EmbThreadSew(181, 148, 116, "Umber", "35"),
                new EmbThreadSew(228, 207, 153, "Blonde", "36"),
                new EmbThreadSew(225, 203, 0, "Sunflower", "37"),
                new EmbThreadSew(225, 173, 212, "Orchid Pink", "38"),
                new EmbThreadSew(195, 0, 126, "Peony Purple", "39"),
                new EmbThreadSew(128, 0, 75, "Burgundy", "40"),
                new EmbThreadSew(160, 96, 176, "Royal Purple", "41"),
                new EmbThreadSew(192, 64, 32, "Cardinal Red", "42"),
                new EmbThreadSew(202, 224, 192, "Opal Green", "43"),
                new EmbThreadSew(137, 152, 86, "Moss Green", "44"),
                new EmbThreadSew(0, 170, 0, "Meadow Green", "45"),
                new EmbThreadSew(33, 138, 33, "Dark Green", "46"),
                new EmbThreadSew(93, 174, 148, "Aquamarine", "47"),
                new EmbThreadSew(76, 191, 143, "Emerald Green", "48"),
                new EmbThreadSew(0, 119, 114, "Peacock Green", "49"),
                new EmbThreadSew(112, 112, 112, "Dark Grey", "50"),
                new EmbThreadSew(242, 255, 255, "Ivory White", "51"),
                new EmbThreadSew(177, 88, 24, "Hazel", "52"),
                new EmbThreadSew(203, 138, 7, "Toast", "53"),
                new EmbThreadSew(247, 146, 123, "Salmon", "54"),
                new EmbThreadSew(152, 105, 45, "Cocoa Brown", "55"),
                new EmbThreadSew(162, 113, 72, "Sienna", "56"),
                new EmbThreadSew(123, 85, 74, "Sepia", "57"),
                new EmbThreadSew(79, 57, 70, "Dark Sepia", "58"),
                new EmbThreadSew(82, 58, 151, "Violet Blue", "59"),
                new EmbThreadSew(0, 0, 160, "Blue Ink", "60"),
                new EmbThreadSew(0, 150, 222, "Solar Blue", "61"),
                new EmbThreadSew(178, 221, 83, "Green Dust", "62"),
                new EmbThreadSew(250, 143, 187, "Crimson", "63"),
                new EmbThreadSew(222, 100, 158, "Floral Pink", "64"),
                new EmbThreadSew(181, 80, 102, "Wine", "65"),
                new EmbThreadSew(94, 87, 71, "Olive Drab", "66"),
                new EmbThreadSew(76, 136, 31, "Meadow", "67"),
                new EmbThreadSew(228, 220, 121, "Canary Yellow", "68"),
                new EmbThreadSew(203, 138, 26, "Toast", "69"),
                new EmbThreadSew(198, 170, 66, "Beige", "70"),
                new EmbThreadSew(236, 176, 44, "Honey Dew", "71"),
                new EmbThreadSew(248, 128, 64, "Tangerine", "72"),
                new EmbThreadSew(255, 229, 5, "Ocean Blue", "73"),
                new EmbThreadSew(250, 122, 122, "Sepia", "74"),
                new EmbThreadSew(107, 224, 0, "Royal Purple", "75"),
                new EmbThreadSew(56, 108, 174, "Yellow Ocher", "76"),
                new EmbThreadSew(208, 186, 176, "Beige Grey", "77"),
                new EmbThreadSew(227, 190, 129, "Bamboo", "78"),
        };
    }


    public EmbThreadSew(int red, int green, int blue, String description, String catalogNumber) {
        super(red, green, blue, description, catalogNumber, "Sew", "Sew");
    }
}
