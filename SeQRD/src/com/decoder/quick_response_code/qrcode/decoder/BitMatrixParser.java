
package com.decoder.quick_response_code.qrcode.decoder;

import com.google.zxing.FormatException;
import com.google.zxing.common.BitMatrix;

final class BitMatrixParser {

    private final BitMatrix bitMatrix;
    private Version parsedVersion;
    private FormatInformation parsedFormatInfo;


    BitMatrixParser(BitMatrix bitMatrix) throws FormatException {
        int dimension = bitMatrix.getHeight();
        if (dimension < 21 || (dimension & 0x03) != 1) {
            throw FormatException.getFormatInstance();
        }
        this.bitMatrix = bitMatrix;
    }

    FormatInformation readFormatInformation() throws FormatException {

        if (parsedFormatInfo != null) {
            return parsedFormatInfo;
        }

        int formatInfoBits1 = 0;
        for (int i = 0; i < 6; i++) {
            formatInfoBits1 = copyBit(i, 8, formatInfoBits1);
        }
        
        formatInfoBits1 = copyBit(7, 8, formatInfoBits1);
        formatInfoBits1 = copyBit(8, 8, formatInfoBits1);
        formatInfoBits1 = copyBit(8, 7, formatInfoBits1);

        for (int j = 5; j >= 0; j--) {
            formatInfoBits1 = copyBit(8, j, formatInfoBits1);
        }

        int dimension = bitMatrix.getHeight();
        int formatInfoBits2 = 0;
        int jMin = dimension - 7;
        for (int j = dimension - 1; j >= jMin; j--) {
            formatInfoBits2 = copyBit(8, j, formatInfoBits2);
        }
        for (int i = dimension - 8; i < dimension; i++) {
            formatInfoBits2 = copyBit(i, 8, formatInfoBits2);
        }

        parsedFormatInfo = FormatInformation.decodeFormatInformation(formatInfoBits1, formatInfoBits2);
        if (parsedFormatInfo != null) {
            return parsedFormatInfo;
        }
        throw FormatException.getFormatInstance();
    }

    Version readVersion() throws FormatException {

        if (parsedVersion != null) {
            return parsedVersion;
        }

        int dimension = bitMatrix.getHeight();

        int provisionalVersion = (dimension - 17) >> 2;
        if (provisionalVersion <= 6) {
            return Version.getVersionForNumber(provisionalVersion);
        }

        int versionBits = 0;
        int ijMin = dimension - 11;
        for (int j = 5; j >= 0; j--) {
            for (int i = dimension - 9; i >= ijMin; i--) {
                versionBits = copyBit(i, j, versionBits);
            }
        }

        Version theParsedVersion = Version.decodeVersionInformation(versionBits);
        if (theParsedVersion != null && theParsedVersion.getDimensionForVersion() == dimension) {
            parsedVersion = theParsedVersion;
            return theParsedVersion;
        }

        versionBits = 0;
        for (int i = 5; i >= 0; i--) {
            for (int j = dimension - 9; j >= ijMin; j--) {
                versionBits = copyBit(i, j, versionBits);
            }
        }

        theParsedVersion = Version.decodeVersionInformation(versionBits);
        if (theParsedVersion != null && theParsedVersion.getDimensionForVersion() == dimension) {
            parsedVersion = theParsedVersion;
            return theParsedVersion;
        }
        throw FormatException.getFormatInstance();
    }

    private int copyBit(int i, int j, int versionBits) {
        return bitMatrix.get(i, j) ? (versionBits << 1) | 0x1 : versionBits << 1;
    }

    byte[] readCodewords() throws FormatException {

        FormatInformation formatInfo = readFormatInformation();
        Version version = readVersion();

        DataMask dataMask = DataMask.forReference((int) formatInfo.getDataMask());
        int dimension = bitMatrix.getHeight();
        dataMask.unmaskBitMatrix(bitMatrix, dimension);

        BitMatrix functionPattern = version.buildFunctionPattern();

        boolean readingUp = true;
        byte[] result = new byte[version.getTotalCodewords()];
        int resultOffset = 0;
        int currentByte = 0;
        int bitsRead = 0;

        for (int j = dimension - 1; j > 0; j -= 2) {
            if (j == 6) {
            	
                j--;
            }

            for (int count = 0; count < dimension; count++) {
                int i = readingUp ? dimension - 1 - count : count;
                for (int col = 0; col < 2; col++) {

                    if (!functionPattern.get(j - col, i)) {

                        bitsRead++;
                        currentByte <<= 1;
                        if (bitMatrix.get(j - col, i)) {
                            currentByte |= 1;
                        }

                        if (bitsRead == 8) {
                            result[resultOffset++] = (byte) currentByte;
                            bitsRead = 0;
                            currentByte = 0;
                        }
                    }
                }
            }
            readingUp ^= true; 
        }
        if (resultOffset != version.getTotalCodewords()) {
            throw FormatException.getFormatInstance();
        }
        return result;
    }

}
