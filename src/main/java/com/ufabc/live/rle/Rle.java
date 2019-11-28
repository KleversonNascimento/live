package com.ufabc.live.rle;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;


// Frequency notation used:
// DFS
// D: Separator - byte 27 -> ESC on ASCII table
// F: Frequency
// S: Symbol
// If frequency is 1, then only the symbol is copied to the compressed array
// Example:
// Original sequence:     AAABBC
// Compressed sequence:   @3A@2BC

public class Rle {

    public static void executeRleCompression(String readPath, String writePath, String fileName, String fileNameNoExtension) throws IOException {
        System.out.println("Compressão Run-Length Encoding");

        // Get bytes from video
        byte[] original = getFileInByteArray(readPath + fileName);

        if (original == null) {
            return;
        }

        // Print Original Bytes
        // ####################################################################
//        System.out.println("ORIGINAL:");
//        for (int i = 0; i < original.length; i++) {
//            System.out.print(original[i]);
//        }
//
//        System.out.println("");

        // Compress file
        byte[] compressed = new byte[2];

        int insertCount = 0;
        Integer count = 0;
        byte temp = 0;

        // Save compressed bytes
        for (int i = 0; i < original.length; i++) {
            byte current = original[i];

            if (current != temp || count >= 255 ) { // Max frequency
                // Save previous
                byte freq;

                if(count <= 127){
                    freq = count.byteValue();
                } else {
                    freq = (byte) ((count - 127) * -1);
                }

                if (count != 1) {
                    compressed = insertIntoArray(compressed, insertCount, (byte) 27); // 27 -> ESC on ASCII table
                    compressed = insertIntoArray(compressed, insertCount + 1, freq);
                    compressed = insertIntoArray(compressed, insertCount + 2, temp);

                    insertCount = insertCount + 3;
                } else {

                    // If byte 27 is found on the original array, insert it on the compressed array with the frequency notation
                    // This way the decompression algorithm won't get confused and mess up the video
                    if(temp == (byte) 27){
                        compressed = insertIntoArray(compressed, insertCount, (byte) 27);
                        compressed = insertIntoArray(compressed, insertCount + 1, freq);
                        compressed = insertIntoArray(compressed, insertCount + 2, temp);

                        insertCount = insertCount + 3;
                    }else{
                        compressed = insertIntoArray(compressed, insertCount, temp);
                        insertCount++;
                    }

                }

                count = 1;
                temp = current;

            } else {
                count++;
            }
        }

        // Save last byte
        byte freq;

        if(count <= 127){
            freq = count.byteValue();
        } else {
            freq = (byte) ((count - 127) * -1);
        }

        compressed = insertIntoArray(compressed, insertCount, (byte) 27);
        compressed = insertIntoArray(compressed, insertCount + 1, freq);
        compressed = insertIntoArray(compressed, insertCount + 2, temp);

        insertCount = insertCount + 3;

        // Remove unused space
        compressed = cleanArray(compressed, insertCount);

        // Save compressed file
        writeBytesToFileNio(compressed, writePath + fileNameNoExtension + ".raedii");
    }

    public static void executeRleDecompression(String readPath, String writePath, String fileName, String fileNameNoExtension) throws IOException {

        // Read compressed file
        byte[] bytes = getFileInByteArray(readPath + fileNameNoExtension + ".raedii");

        // Print Compressed File
        // ####################################################################
//        System.out.println("COMPRESSED:");
//
//        for (int i = 0; i < bytes.length; i++) {
//            System.out.print(Integer.toBinaryString((char) bytes[i]) + "|");
//        }
//
//        System.out.println("");

        // Decompress file
        byte[] decompressed = new byte[2];
        int indexCount = 0;
        int c = 0;
        while (c < bytes.length) {
            byte s = bytes[c];
            if (s == (byte) 27) {
                byte repeat = bytes[c + 1];
                int frequency;

                if(repeat < 0) {
                    frequency = (repeat * (-1)) + 127;
                } else {
                    frequency = repeat;
                }

                for (int j = 0; j < frequency; j++) {
                    decompressed = insertIntoArray(decompressed, indexCount, bytes[c + 2]);
                    indexCount++;
                }
                c = c + 3;
            } else {
                decompressed = insertIntoArray(decompressed, indexCount, s);
                indexCount++;
                c++;
            }
        }

        // Remove unused space
        decompressed = cleanArray(decompressed, indexCount);

        // Print decompressed file
        // ####################################################################
//        System.out.println("DECOMPRESSED:");
//        // Print decompressed byte array
//        for (int i = 0; i < decompressed.length; i++) {
//            System.out.print(decompressed[i]);
//        }
//
//        System.out.println("");

        // Save decompressed file
        writeBytesToFileNio(decompressed, writePath + fileName);

        System.out.println("Pronto!");
    }

    private static void writeBytesToFileNio(byte[] bFile, String fileDest) {

        try {
            Path path = Paths.get(fileDest);
            Files.write(path, bFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] getFileInByteArray(String path) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            FileInputStream fis = new FileInputStream(new File(path));
            byte[] buf = new byte[1024];
            int n;

            while (-1 != (n = fis.read(buf))) {
                baos.write(buf, 0, n);
            }

            return baos.toByteArray();
        } catch (FileNotFoundException e) {
            System.out.println(path);
            System.out.println("Arquivo não encontrado!");
        }

        return null;
    }

    private static byte[] insertIntoArray(byte[] array, int index, byte value) {

        if (index >= array.length) {
            // Create a new array with double the space
            byte[] newArray = new byte[array.length * 2];

            // Copy array into new one
            System.arraycopy(array, 0, newArray, 0, array.length);

            // Put value into newArray
            newArray[index] = value;

            return newArray;
        }

        array[index] = value;
        return array;
    }

    private static byte[] cleanArray(byte[] array, int size) {
        // Remove unused space
        byte[] finalFile = new byte[size + 1];
        System.arraycopy(array, 0, finalFile, 0, size + 1);
        return finalFile;
    }

}