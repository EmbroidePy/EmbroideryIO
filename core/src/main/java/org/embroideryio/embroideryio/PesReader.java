package org.embroideryio.embroideryio;

import java.io.IOException;

public class PesReader extends PecReader {

    static final String PROP_VERSION = "version";


    @Override
    public void read() throws IOException {
        readPESHeader();
        readPec();
    }

    public void readPESHeader() throws IOException {
        String signature = readString(8);
        switch (signature) {
            case "#PES0100":
                pattern.setMetadata(PROP_VERSION, Float.toString(10));
                break;
            case "#PES0090":
                pattern.setMetadata(PROP_VERSION, Float.toString(9));
                break;
            case "#PES0080":
                pattern.setMetadata(PROP_VERSION, Float.toString(8));
                break;
            case "#PES0070":
                pattern.setMetadata(PROP_VERSION, Float.toString(7));
                break;
            case "#PES0060":
                pattern.setMetadata(PROP_VERSION, Float.toString(6));
                readPESHeaderV6();
                break;
            case "#PES0056":
                pattern.setMetadata(PROP_VERSION, Float.toString(5.6f));
                readPESHeaderV5();
                break;
            case "#PES0055":
                pattern.setMetadata(PROP_VERSION, Float.toString(5.5f));
                readPESHeaderV5();
                break;
            case "#PES0050":
                pattern.setMetadata(PROP_VERSION, Float.toString(5));
                readPESHeaderV5();
                break;
            case "#PES0040":
                pattern.setMetadata(PROP_VERSION, Float.toString(4));
                readPESHeaderV4();
                break;
            case "#PES0030":
                pattern.setMetadata(PROP_VERSION, Float.toString(3));
                readPESHeaderDefault();
                break;
            case "#PES0022":
                pattern.setMetadata(PROP_VERSION, Float.toString(2.2f));
                readPESHeaderDefault();
                break;
            case "#PES0020":
                pattern.setMetadata(PROP_VERSION, Float.toString(2));
                readPESHeaderDefault();
                break;
            case "#PES0001":
                pattern.setMetadata(PROP_VERSION, Float.toString(1));
                readPESHeaderDefault();
                break;
            case "#PEC0001":
                //PEC needs to go straight to reading, no default.
                return;
            default:
                readPESHeaderDefault();
                break;
        }
    }

    public void readPESHeaderDefault() throws IOException {
        int pecStart = readInt32LE();
        skip(pecStart - readPosition);
    }

    public void readDescriptions() throws IOException {
        int DesignStringLength = readInt8();
        String DesignName = readString(DesignStringLength);
        pattern.setName(DesignName);
        int categoryStringLength = readInt8();
        String Category = readString(categoryStringLength);
        pattern.setCategory(Category);
        int authorStringLength = readInt8();
        String Author = readString(authorStringLength);
        pattern.setAuthor(Author);
        int keywordsStringLength = readInt8();
        String keywords = readString(keywordsStringLength);
        pattern.setAuthor(keywords);
        int commentsStringLength = readInt8();
        String Comments = readString(commentsStringLength);
        pattern.setComments(Comments);
    }

    public void readPESHeaderV4() throws IOException {
        int pecStart = readInt32LE();
        skip(4);
        readDescriptions();
        skip(pecStart - readPosition);
    }

    public void readPESHeaderV5() throws IOException {
        int pecStart = readInt32LE();
        skip(4);
        readDescriptions();
        skip(24);//36 v6
        int fromImageStringLength = readInt8();
        skip(fromImageStringLength);
        skip(24);
        int numberOfProgrammableFillPatterns = readInt16LE();
        if (numberOfProgrammableFillPatterns != 0) {
            seek(pecStart);
            return;
        }
        int numberOfMotifPatterns = readInt16LE();
        if (numberOfMotifPatterns != 0) {
            seek(pecStart);
            return;
        }
        int featherPatternCount = readInt16LE();
        if (featherPatternCount != 0) {
            seek(pecStart);
            return;
        }
        int numberOfColors = readInt16LE();
        for (int i = 0; i < numberOfColors; i++) {
            readThread();
        }
        seek(pecStart);
    }

    public void readPESHeaderV6() throws IOException {
        int pecStart = readInt32LE();
        skip(4);
        readDescriptions();
        skip(36);
        int fromImageStringLength = readInt8();
        String fromImageString = readString(fromImageStringLength);
        if (fromImageString.length() != 0) {
            pattern.setMetadata("image_file", fromImageString);
        }
        skip(24);
        int numberOfProgrammableFillPatterns = readInt16LE();
        if (numberOfProgrammableFillPatterns != 0) {
            seek(pecStart);
            return;
        }
        int numberOfMotifPatterns = readInt16LE();
        if (numberOfMotifPatterns != 0) {
            seek(pecStart);
            return;
        }
        int featherPatternCount = readInt16LE();
        if (featherPatternCount != 0) {
            seek(pecStart);
            return;
        }
        int numberOfColors = readInt16LE();
        for (int i = 0; i < numberOfColors; i++) {
            readThread();
        }
        seek(pecStart);
    }

    public void readThread() throws IOException {
        int color_code_length = readInt8();
        String color_code = readString(color_code_length);
        int red = readInt8();
        int green = readInt8();
        int blue = readInt8();
        skip(5);
        int descriptionStringLength = readInt8();
        String description = readString(descriptionStringLength);

        int brandStringLength = readInt8();
        String brand = readString(brandStringLength);

        int threadChartStringLength = readInt8();
        String threadChart = readString(threadChartStringLength);

        int color = (red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF);
        pattern.add(new EmbThread(color, description, color_code, brand, threadChart));
    }
}
