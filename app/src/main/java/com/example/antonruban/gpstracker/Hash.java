package com.example.antonruban.gpstracker;

import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;


public class Hash{
    private static final String HASH_CONSTANT = "B194BAC80A08F53B366D008E584A5DE48504FA9D1BB6C7AC252E72C202FDCE0D";
    private static final int REQUIRED_BYTES_AMOUNT = 32;
    private static byte MASK = (byte)Integer.parseInt("11111111", 2);
    private static long TWO_IN_8_DEGREE = 256;
    private static long TWO_IN_16_DEGREE = 65536;
    private static long TWO_IN_24_DEGREE = 16777216;
    private static long TWO_IN_32_DEGREE = 4294967296L;
    private static String[] H_TABLE = {
            "10110001", "10010100", "10111010", "11001000", "00001010",
            "00001000", "11110101", "00111011", "00110110", "01101101",
            "00000000", "10001110", "01011000", "01001010", "01011101",
            "11100100", "10000101", "00000100", "11111010", "10011101",
            "00011011", "10110110", "11000111", "10101100", "00100101",
            "00101110", "01110010", "11000010", "00000010", "11111101",
            "11001110", "00001101", "01011011", "11100011", "11010110",
            "00010010", "00010111", "10111001", "01100001", "10000001",
            "11111110", "01100111", "10000110", "10101101", "01110001",
            "01101011", "10001001", "00001011", "01011100", "10110000",
            "11000000", "11111111", "00110011", "11000011", "01010110",
            "10111000", "00110101", "11000100", "00000101", "10101110",
            "11011000", "11100000", "01111111", "10011001", "11100001",
            "00101011", "11011100", "00011010", "11100010", "10000010",
            "01010111", "11101100", "01110000", "00111111", "11001100",
            "11110000", "10010101", "11101110", "10001101", "11110001",
            "11000001", "10101011", "01110110", "00111000", "10011111",
            "11100110", "01111000", "11001010", "11110111", "11000110",
            "11111000", "01100000", "11010101", "10111011", "10011100",
            "01001111", "11110011", "00111100", "01100101", "01111011",
            "01100011", "01111100", "00110000", "01101010", "11011101",
            "01001110", "10100111", "01111001", "10011110", "10110010",
            "00111101", "00110001", "00111110", "10011000", "10110101",
            "01101110", "00100111", "11010011", "10111100", "11001111",
            "01011001", "00011110", "00011000", "00011111", "01001100",
            "01011010", "10110111", "10010011", "11101001", "11011110",
            "11100111", "00101100", "10001111", "00001100", "00001111",
            "10100110", "00101101", "11011011", "01001001", "11110100",
            "01101111", "01110011", "10010110", "01000111", "00000110",
            "00000111", "01010011", "00010110", "11101101", "00100100",
            "01111010", "00110111", "00111001", "11001011", "10100011",
            "10000011", "00000011", "10101001", "10001011", "11110110",
            "10010010", "10111101", "10011011", "00011100", "11100101",
            "11010001", "01000001", "00000001", "01010100", "01000101",
            "11111011", "11001001", "01011110", "01001101", "00001110",
            "11110010", "01101000", "00100000", "10000000", "10101010",
            "00100010", "01111101", "01100100", "00101111", "00100110",
            "10000111", "11111001", "00110100", "10010000", "01000000",
            "01010101", "00010001", "10111110", "00110010", "10010111",
            "00010011", "01000011", "11111100", "10011010", "01001000",
            "10100000", "00101010", "10001000", "01011111", "00011001",
            "01001011", "00001001", "10100001", "01111110", "11001101",
            "10100100", "11010000", "00010101", "01000100", "10101111",
            "10001100", "10100101", "10000100", "01010000", "10111111",
            "01100110", "11010010", "11101000", "10001010", "10100010",
            "11010111", "01000110", "01010010", "01000010", "10101000",
            "11011111", "10110011", "01101001", "01110100", "11000101",
            "01010001", "11101011", "00100011", "00101001", "00100001",
            "11010100", "11101111", "11011001", "10110100", "00111010",
            "01100010", "00101000", "01110101", "10010001", "00010100",
            "00010000", "11101010", "01110111", "01101100", "11011010",
            "00011101"
    };
    private static byte[] EMPTY_BYTE_ARRAY = {0, 0, 0, 0};
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    private static byte[] hexStringToByteArray(String str){
        int len = str.length();
        byte[] data = new byte[len/2];
        for(int i = 0; i < len; i+=2){
            data[i/2] = (byte)((Character.digit(str.charAt(i), 16)<<4) + Character.digit(str.charAt(i+1),16));
        }
        return data;
    }

    public static byte[] getHashFromFile(File file) {
        Log.d("Hash", "GetHashFromFile: start");
        byte[] s_byte = new byte[REQUIRED_BYTES_AMOUNT / 2];
        byte[] h_byte = hexStringToByteArray(HASH_CONSTANT);

        long fileLengthInBits = 0;
        byte[] x_byte = new byte[REQUIRED_BYTES_AMOUNT];
        int bytesRead = -1;

        try {
            FileInputStream os = new FileInputStream(file);

            while ((bytesRead = os.read(x_byte)) > 0) {

                Log.d("Hash", "GetHashFromFile: read: " + bytesRead + "; byte: " + x_byte.toString());

                if (bytesRead < REQUIRED_BYTES_AMOUNT) {
                    if (bytesRead == -1) {
                        break;
                    } else {
                        for (int i = bytesRead; i < REQUIRED_BYTES_AMOUNT; i++) {
                            x_byte[i] = 0;
                        }
                    }
                }
                s_byte = XOR(s_byte, getDisplay1(x_byte, h_byte));
                h_byte = getDisplay2(x_byte, h_byte);
            }
            fileLengthInBits = file.length() * 8;
            Log.d("Hash", "GetHashFromFile: file lenth " + fileLengthInBits + " bit");

            //Log.d("HashFunc", "GetHashFromFile: end " + y_byte.length);


        } catch (Exception e) {
            Log.d("Hash", "GetHashFromFile: read: " + bytesRead + "; byte: " + x_byte.toString());
        }
        return getDisplay2(getWordFromAccordance(fileLengthInBits, 16), s_byte, h_byte);
    }

    private static byte[] getDisplay1(byte[] u1u2, byte[] u3, byte[] u4){
        byte[] buffer = encodeX(u1u2, XOR(u3, u4));
        buffer = XOR(buffer, u3);
        buffer = XOR(buffer, u4);
        return buffer;
    }

    private static byte[] getDisplay1(byte[] u1u2, byte[] u3u4){
        byte[] u3 = Arrays.copyOfRange(u3u4, 0, u3u4.length/2);
        byte[] u4 = Arrays.copyOfRange(u3u4, u3u4.length/2, u3u4.length);
        return getDisplay1(u1u2, u3, u4);
    }

    private static byte[] getDisplay2(byte[] u1, byte[] u2, byte[] u3, byte[] u4){
        byte[] buffer1 = XOR(encodeX(getO1(concatArrays(u1, u2), u3, u4), u1), u1);
        byte[] buffer2 = XOR(encodeX(getO2(concatArrays(u1, u2), u3, u4), u2), u2);
        return concatArrays(buffer1, buffer2);
    }

    private static byte[] getDisplay2(byte[] u1u2, byte[] u3u4){
        byte[] u1 = Arrays.copyOfRange(u1u2, 0, u1u2.length/2);
        byte[] u2 = Arrays.copyOfRange(u1u2, u1u2.length/2, u1u2.length);
        byte[] u3 = Arrays.copyOfRange(u3u4, 0, u3u4.length/2);
        byte[] u4 = Arrays.copyOfRange(u3u4, u3u4.length/2, u3u4.length);
        return getDisplay2(u1, u2, u3, u4);
    }

    private static byte[] getDisplay2(byte[] u1, byte[] u2, byte[] u3u4){
        byte[] u3 = Arrays.copyOfRange(u3u4, 0, u3u4.length/2);
        byte[] u4 = Arrays.copyOfRange(u3u4, u3u4.length/2, u3u4.length);
        return getDisplay2(u1, u2, u3, u4);
    }

    private static byte[] getO1(byte[] u1u2, byte[] u3, byte[] u4){
        return concatArrays(getDisplay1(u1u2, u3, u4), u4);
    }

    private static byte[] getO2(byte[] u1u2, byte[] u3, byte[] u4){
        return concatArrays(XORWithOnes(getDisplay1(u1u2, u3, u4)), u3);
    }

    private static byte[] getWordFromAccordance(long accordance, int n){
        if (n == 4) {
            accordance = accordance % TWO_IN_32_DEGREE;
        }
        byte[] buff = ByteBuffer.allocate(8).putLong(accordance).array();
        if (n == 4){
            buff = Arrays.copyOfRange(buff, 4, 8);
        }
        return getWordFromAccordance(buff, n);
    }

    private static byte[] getWordFromAccordance(byte[] word, int n){
        byte[] result = new byte[n];
        for (int i = 0; i < word.length; i++){
            result[i] = word[word.length - i - 1];
        }
        return result;
    }

    private static long getAccordanceFromWord(byte[] word){
        if (word.length != 4) throw new RuntimeException("RETARD ALERT");
        long accordance = signedByteToInteger(word[0]);
        accordance += signedByteToInteger(word[1]) * TWO_IN_8_DEGREE;
        accordance += signedByteToInteger(word[2]) * TWO_IN_16_DEGREE;
        accordance += signedByteToInteger(word[3]) * TWO_IN_24_DEGREE;
        return accordance;
    }

    private static byte[] XOR(byte[] array1, byte[] array2){
        if (array1.length != array2.length){
            throw new RuntimeException("RETARD ALERT");
        }
        byte [] resultArray = new byte[array1.length];
        for (int i = 0; i< array1.length; i++){
            resultArray[i] = (byte)(array1[i] ^ array2[i]);
        }
        return resultArray;
    }

    private static byte[] XORWithOnes(byte[] array){
        byte[] result = new byte[array.length];
        int currentIndex;
        for (currentIndex = 0; currentIndex < array.length; currentIndex++){
            result[currentIndex] = (byte)(array[currentIndex] ^ MASK);
        }
        return result;
    }

    private static byte[] concatArrays(byte[] ... arrays){
        int length = 0;
        for (byte[] array: arrays) {
            length += array.length;
        }
        byte [] resultArray = new byte[length];
        int currentIndex = 0;
        for (byte[] array: arrays) {
            for (byte element: array) {
                resultArray[currentIndex] = element;
                currentIndex++;
            }
        }
        return resultArray;
    }

    public static byte[] encodeX(byte[] key, byte[] x){
        Log.d("Hash","encodeX: start");
        int additionalArraysLength = x.length/4;
        int startIndex = 0;
        byte[] a = Arrays.copyOfRange(x, startIndex, additionalArraysLength);
        startIndex += additionalArraysLength;
        byte[] b = Arrays.copyOfRange(x, startIndex, startIndex + additionalArraysLength);
        startIndex += additionalArraysLength;
        byte[] c = Arrays.copyOfRange(x, startIndex, startIndex + additionalArraysLength);
        startIndex += additionalArraysLength;
        byte[] d = Arrays.copyOfRange(x, startIndex, startIndex + additionalArraysLength);
        byte[] e;
        byte[] buffer;
        for (int i = 1; i <= 8; i ++) {
            b = XOR(b, getG(5, squarePlus(a, getKeyByIndex(key, 7*i - 6))));
            c = XOR(c, getG(21, squarePlus(d, getKeyByIndex(key, 7*i - 5))));
            a = squareMinus(a, getG(13, squarePlus(b, getKeyByIndex(key, 7*i - 4))));
            e = XOR(getG(21, squarePlus(squarePlus(b, c), getKeyByIndex(key, 7*i - 3))), getWordFromAccordance(i, 4));
            b = squarePlus(b, e);
            c = squareMinus(c, e);
            d = squarePlus(d, getG(13, squarePlus(c, getKeyByIndex(key, 7*i - 2))));
            b = XOR(b, getG(21, squarePlus(a, getKeyByIndex(key, 7*i - 1))));
            c = XOR(c, getG(5, squarePlus(d, getKeyByIndex(key, 7*i))));

            buffer = a;
            a = b;
            b = buffer;

            buffer = c;
            c = d;
            d = buffer;

            buffer = b;
            b = c;
            c = buffer;

        }
        Log.d("Hash","encodeX: end");
        return concatArrays(b, d, a, c);
    }

    private static byte[] getG(int index, byte[] u){
        return rotHi(getH(u[3]) + getH(u[2]) + getH(u[1]) + getH(u[0]), index);
    }

    private static String getH(byte u){
        return H_TABLE[signedByteToInteger(u)];
    }

    private static byte[] rotHi(String bytes, int index){
        String movedBytes = bytes.substring(index) + bytes.substring(0, index);
        Long buffer = Long.parseLong(movedBytes, 2);
        return getWordFromAccordance(Arrays.copyOfRange(ByteBuffer.allocate(8).putLong(buffer).array(), 4, 8),4);
    }

    private static byte[] squarePlus(byte[] array1, byte[] array2){
        if (array1.length != 4) throw new RuntimeException("RETARD ALERT");
        return getWordFromAccordance(getAccordanceFromWord(array1) + getAccordanceFromWord(array2), 4);
    }

    private static byte[] squareMinus(byte[] array1, byte[] array2){
        if (array1.length != 4) throw new RuntimeException("RETARD ALERT");
        return getWordFromAccordance(ByteBuffer.wrap(concatArrays(EMPTY_BYTE_ARRAY, getWordFromAccordance(array1, 4))).getLong() - getAccordanceFromWord(array2), 4);
    }

    private static byte[] getKeyByIndex(byte[] key, int startIndex){
        startIndex = startIndex - 1;
        startIndex = startIndex % 8;
        int currentIndex = 0;
        byte[] result = new byte[4];
        while (currentIndex < 4){
            result[currentIndex] = key[startIndex * 4 + currentIndex];
            currentIndex++;
        }
        return result;
    }

    private static int signedByteToInteger(byte b) {
        return b & 0xFF;
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}

