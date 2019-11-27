package com.ufabc.live.rle;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Rle {

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Digite o nome do arquivo (com a a extensão):");
        String fileName = scanner.nextLine();
        String fileNameNoExtension = fileName.split("\\.")[0];

        // Get bytes from video
        byte[] original = getFileInByteArray("res/" + fileName);

        if(original == null){
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

            if (current != temp || count > 110) {
                // Save previous
                byte freq = count.byteValue();
                    if (count != 1) {
                    compressed = insertIntoArray(compressed, insertCount, (byte) 27);
                    compressed = insertIntoArray(compressed, insertCount + 1, (byte) 64);
                    compressed = insertIntoArray(compressed, insertCount + 2, (byte) 12);
                    compressed = insertIntoArray(compressed, insertCount + 3, (byte) 95);
                    compressed = insertIntoArray(compressed, insertCount + 4, freq);
                    compressed = insertIntoArray(compressed, insertCount + 5, temp);

                    insertCount = insertCount + 6;
                } else {
                    compressed = insertIntoArray(compressed, insertCount, temp);
                    insertCount++;
                }

                count = 1;
                temp = current;

            } else {
                count++;
            }
        }

        // Save last byte
        byte freq = count.byteValue();

        compressed = insertIntoArray(compressed, insertCount, (byte) 27);
        compressed = insertIntoArray(compressed, insertCount + 1, (byte) 64);
        compressed = insertIntoArray(compressed, insertCount + 2, (byte) 12);
        compressed = insertIntoArray(compressed, insertCount + 3, (byte) 95);
        compressed = insertIntoArray(compressed, insertCount + 4, freq);
        compressed = insertIntoArray(compressed, insertCount + 5, temp);
        insertCount = insertCount + 6;

        // Remove unused space
        compressed = cleanArray(compressed, insertCount);

        // Save compressed file
        writeBytesToFileNio(compressed, "res/compressed_"+ fileNameNoExtension + ".aedii");

        // Read compressed file
        byte[] bytes = getFileInByteArray("res/compressed_"+ fileNameNoExtension + ".aedii");

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
            if ((s == (byte) 27) && (bytes[c + 1] == (byte) 64) && (bytes[c + 2] == (byte) 12) && (bytes[c + 3] == (byte) 95)) {
                byte repeat = bytes[c + 4];
                for (int j = 0; j < repeat; j++) {
                    decompressed = insertIntoArray(decompressed, indexCount, bytes[c + 5]);
                    indexCount++;
                }
                c = c + 6;
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
        writeBytesToFileNio(decompressed, "res/result_" + fileName);

        System.out.println("Done!");
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
        try{
            FileInputStream fis = new FileInputStream(new File(path));
            byte[] buf = new byte[1024];
            int n;

            while (-1 != (n = fis.read(buf))) {
                baos.write(buf, 0, n);
            }

            return baos.toByteArray();
        } catch (FileNotFoundException e){
            System.out.println("File not found!");
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