package org.embroideryio.embroideryio;

import java.io.IOException;

public class CsdReader extends EmbReader {

    int CsdSubMaskSize = 479;
    int CsdXorMaskSize = 501;

    int[] _subMask = new int[CsdSubMaskSize];
    int[] _xorMask = new int[CsdXorMaskSize];

    int[] _decryptArray = new int[]{
        0x43, 0x6E, 0x72, 0x7A, 0x76, 0x6C, 0x61, 0x6F, 0x7C, 0x29, 0x5D, 0x62, 0x60, 0x6E, 0x61, 0x62, 0x20,
        0x41, 0x66, 0x6A, 0x3A, 0x35, 0x5A, 0x63, 0x7C, 0x37, 0x3A, 0x2A, 0x25, 0x24, 0x2A, 0x33, 0x00, 0x10,
        0x14, 0x03, 0x72, 0x4C, 0x48, 0x42, 0x08, 0x7A, 0x5E, 0x0B, 0x6F, 0x45, 0x47, 0x5F, 0x40, 0x54, 0x5C,
        0x57, 0x55, 0x59, 0x53, 0x3A, 0x32, 0x6F, 0x53, 0x54, 0x50, 0x5C, 0x4A, 0x56, 0x2F, 0x2F, 0x62, 0x2C,
        0x22, 0x65, 0x25, 0x28, 0x38, 0x30, 0x38, 0x22, 0x2B, 0x25, 0x3A, 0x6F, 0x27, 0x38, 0x3E, 0x3F, 0x74,
        0x37, 0x33, 0x77, 0x2E, 0x30, 0x3D, 0x34, 0x2E, 0x32, 0x2B, 0x2C, 0x0C, 0x18, 0x42, 0x13, 0x16, 0x0A,
        0x15, 0x02, 0x0B, 0x1C, 0x1E, 0x0E, 0x08, 0x60, 0x64, 0x0D, 0x09, 0x51, 0x25, 0x1A, 0x18, 0x16, 0x19,
        0x1A, 0x58, 0x10, 0x14, 0x5B, 0x08, 0x15, 0x1B, 0x5F, 0xD5, 0xD2, 0xAE, 0xA3, 0xC1, 0xF0, 0xF4, 0xE8,
        0xF8, 0xEC, 0xA6, 0xAB, 0xCD, 0xF8, 0xFD, 0xFB, 0xE2, 0xF0, 0xFE, 0xFA, 0xF5, 0xB5, 0xF7, 0xF9, 0xFC,
        0xB9, 0xF5, 0xEF, 0xF4, 0xF8, 0xEC, 0xBF, 0xC3, 0xCE, 0xD7, 0xCD, 0xD0, 0xD7, 0xCF, 0xC2, 0xDB, 0xA4,
        0xA0, 0xB0, 0xAF, 0xBE, 0x98, 0xE2, 0xC2, 0x91, 0xE5, 0xDC, 0xDA, 0xD2, 0x96, 0xC4, 0x98, 0xF8, 0xC9,
        0xD2, 0xDD, 0xD3, 0x9E, 0xDE, 0xAE, 0xA5, 0xE2, 0x8C, 0xB6, 0xAC, 0xA3, 0xA9, 0xBC, 0xA8, 0xA6, 0xEB,
        0x8B, 0xBF, 0xA1, 0xAC, 0xB5, 0xA3, 0xBB, 0xB6, 0xA7, 0xD8, 0xDC, 0x9A, 0xAA, 0xF9, 0x82, 0xFB, 0x9D,
        0xB9, 0xAB, 0xB3, 0x94, 0xC1, 0xA0, 0x8C, 0x8B, 0x8E, 0x95, 0x8F, 0x87, 0x99, 0xE7, 0xE1, 0xA3, 0x83,
        0x8B, 0xCF, 0xA3, 0x85, 0x9D, 0x83, 0xD4, 0xB7, 0x83, 0x84, 0x91, 0x97, 0x9F, 0x88, 0x8F, 0xDD, 0xAD,
        0x90};

    void BuildDecryptionTable(int seed) {
        int mul1 = 0x41C64E6D;
        int add1 = 0x3039;
        for (int i = 0; i < CsdSubMaskSize; i++) {
            seed *= mul1;
            seed += add1;
            seed &= 0xFFFFFFFF;
            _subMask[i] = (seed >> 16) & 0xFF;
        }
        for (int i = 0; i < CsdXorMaskSize; i++) {
            seed *= mul1;
            seed += add1;
            seed &= 0xFFFFFFFF;
            _xorMask[i] = (seed >> 16) & 0xFF;
        }
    }

    int DecodeCsdByte(int fileOffset, int val, int type) {
        int newOffset;
        if (type != 0) {
            int fileOffsetHigh = fileOffset & 0xFFFFFF00;
            int fileOffsetLow = fileOffset & 0xFF;
            newOffset = fileOffsetLow;
            fileOffsetLow = fileOffsetHigh;
            int ffinal = fileOffsetLow % 0x300;
            if ((ffinal != 0x100) && (ffinal != 0x200)) {
                newOffset = _decryptArray[newOffset] | fileOffsetHigh;
            } else if ((ffinal != 0x100) && (ffinal == 0x200)) {
                if (newOffset == 0) {
                    fileOffsetHigh = fileOffsetHigh - 0x100;
                }
                newOffset = _decryptArray[newOffset] | fileOffsetHigh;
            } else if ((newOffset != 1) && (newOffset != 0)) {
                newOffset = _decryptArray[newOffset] | fileOffsetHigh;
            } else {
                fileOffsetHigh = fileOffsetHigh - 0x100;
                newOffset = _decryptArray[newOffset] | fileOffsetHigh;
            }
        } else {
            newOffset = fileOffset;
        }
        return ((val ^ _xorMask[newOffset % CsdXorMaskSize]) - (_subMask[newOffset % CsdSubMaskSize])) & 0xFF;
    }

    public void read_csd_stitches() throws IOException {
        int type = 0;
        int[] identifier = new int[8];
        int colorChange = -1;
        int[] colorOrder = new int[14]; //I don't actually use this, only thread order.
        for (int i = 0; i < 8; i++) {
            identifier[i] = readInt8();
        }

        if ((identifier[0] != 0x7C) && (identifier[2] != 0xC3)) {
            type = 1;
        }
        if (type == 0) {
            BuildDecryptionTable(0xC);
        } else {
            BuildDecryptionTable(identifier[0]);
        }
        for (int i = 0; i < 16; i++) {
            EmbThread thread = new EmbThread();
            int r = DecodeCsdByte(tell(), readInt8(), type);
            int g = DecodeCsdByte(tell(), readInt8(), type);
            int b = DecodeCsdByte(tell(), readInt8(), type);
            thread.setColor(r, g, b);
            thread.catalogNumber = "";
            thread.description = "";
            this.pattern.add(thread);
        }
        int unknown1 = DecodeCsdByte(tell(), readInt8(), type);
        int unknown2 = DecodeCsdByte(tell(), readInt8(), type);

        for (int i = 0; i < 14; i++) {
            colorOrder[i] = DecodeCsdByte(tell(), readInt8(), type);
        }

        while (true) {
            int b0 = DecodeCsdByte(tell(), readInt8(), type);
            int b1 = DecodeCsdByte(tell(), readInt8(), type);
            int b2 = DecodeCsdByte(tell(), readInt8(), type);

            int dx = b2;
            int dy = b1;

            if ((b0 == 0xF8) || (b0 == 0x87) || (b0 == 0x91)) {
                break;
            }
            boolean negativeX = ((b0 & 0x20) > 0);
            boolean negativeY = ((b0 & 0x40) > 0);

            if (negativeX) {
                dx = -dx;
            }
            if (!negativeY) { //This is ! because we're converting to a flipped y system.
                dy = -dy;
            }

            b0 &= 0xFF ^ 0xE0;

            if ((b0 & 0x1F) == 0) {
                pattern.stitch(dx, dy);
            } else if ((b0 & 0x0C) > 0) {
                pattern.color_change();
                if (colorChange >= 14) {
                    break;  //Invalid color change.
                }
                colorChange += 1;
            } else if ((b0 & 0x1F) > 0) {
                pattern.trim();
                pattern.move(dx, dy);
            } else {
                pattern.stitch(dx, dy);
            }
        }
    }

    @Override
    public void read() throws IOException {
        read_csd_stitches();
    }
}
