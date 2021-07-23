package org.embroideryio.embroideryio;

/**
 * Created by Tat on 12/8/2016.
 */


public class EmbThreadHus extends EmbThread {

    public static EmbThreadHus[] getThreadSet() {
        return new EmbThreadHus[]{
            new EmbThreadHus("#000000", "Black", "026"),
            new EmbThreadHus("#0000e7", "Blue", "005"),
            new EmbThreadHus("#00c600", "Green", "002"),
            new EmbThreadHus("#ff0000", "Red", "014"),
            new EmbThreadHus("#840084", "Purple", "008"),
            new EmbThreadHus("#ffff00", "Yellow", "020"),
            new EmbThreadHus("#848484", "Grey", "024"),
            new EmbThreadHus("#8484e7", "Light Blue", "006"),
            new EmbThreadHus("#00ff84", "Light Green", "003"),
            new EmbThreadHus("#ff7b31", "Orange", "017"),
            new EmbThreadHus("#ff8ca5", "Pink", "011"),
            new EmbThreadHus("#845200", "Brown", "028"),
            new EmbThreadHus("#ffffff", "White", "022"),
            new EmbThreadHus("#000084", "Dark Blue", "004"),
            new EmbThreadHus("#008400", "Dark Green", "001"),
            new EmbThreadHus("#7b0000", "Dark Red", "013"),
            new EmbThreadHus("#ff6384", "Light Red", "015"),
            new EmbThreadHus("#522952", "Dark Purple", "007"),
            new EmbThreadHus("#ff00ff", "Light Purple", "009"),
            new EmbThreadHus("#ffde00", "Dark Yellow", "019"),
            new EmbThreadHus("#ffff9c", "Light Yellow", "021"),
            new EmbThreadHus("#525252", "Dark Grey", "025"),
            new EmbThreadHus("#d6d6d6", "Light Grey", "023"),
            new EmbThreadHus("#ff5208", "Dark Orange", "016"),
            new EmbThreadHus("#ff9c5a", "Light Orange", "018"),
            new EmbThreadHus("#ff52b5", "Dark Pink", "010"),
            new EmbThreadHus("#ffc6de", "Light Pink", "012"),
            new EmbThreadHus("#523100", "Dark Brown", "027"),
            new EmbThreadHus("#b5a584", "Light Brown", "029"),
        };
    }


    public EmbThreadHus(String color, String description, String catalogNumber) {
        super();
        this.setColor(EmbThread.parseColor(color));
        this.description = description;
        this.catalogNumber = catalogNumber;
        this.brand = "Hus";
        this.chart = "Hus";
    }
}
