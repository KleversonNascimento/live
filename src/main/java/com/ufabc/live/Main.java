package com.ufabc.live;

import com.ufabc.live.huffman.HuffmanCompress;
import com.ufabc.live.huffman.HuffmanDecompress;
import com.ufabc.live.rle.Rle;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Digite o nome do arquivo (com a extensão):");
        String fileName = scanner.nextLine();
        String fileNameNoExtension = fileName.split("\\.")[0];

        System.out.println("Selecione uma opção de compressão:");
        System.out.println("1 - Huffman");
        System.out.println("2 - RLE");
        System.out.println("3 - Huffman-RLE");
        System.out.println("4 - RLE-Huffman");

        int option = scanner.nextInt();

        switch (option){
            case 1:
                HuffmanCompress.executeHuffmanCompression("res/original/", "res/huff/", fileName, fileNameNoExtension);
                HuffmanDecompress.executeHuffmanDecompression("res/huff/", "res/huff/", fileName, fileNameNoExtension);
                break;
            case 2:
                Rle.executeRleCompression("res/original/", "res/rle/", fileName, fileNameNoExtension);
                Rle.executeRleDecompression("res/rle/", "res/rle/", fileName, fileNameNoExtension);
                break;
            case 3:
                // Compress with Huffman
                HuffmanCompress.executeHuffmanCompression("res/original/", "res/huff-rle/", fileName, fileNameNoExtension);

                String compressedHuffmanFileName = fileNameNoExtension + ".haedii";
                String compressedHuffmanFileNameNoExtension = compressedHuffmanFileName.split("\\.")[0];

                // Compress Huffman file with RLE
                Rle.executeRleCompression("res/huff-rle/", "res/huff-rle/", compressedHuffmanFileName, compressedHuffmanFileNameNoExtension);

                // Delete huffman file
                File compressedHuffmanFile = new File("res/huff-rle/" + compressedHuffmanFileName);
                compressedHuffmanFile.delete();

                String compressedRleFileName = fileNameNoExtension + ".raedii";
                String compressedRleFileNameNoExtension = compressedRleFileName.split("\\.")[0];

                // Decompress RLE file
                Rle.executeRleDecompression("res/huff-rle/", "res/huff-rle/r", compressedRleFileNameNoExtension + ".haedii", compressedRleFileNameNoExtension);

                // Decompress Huffman file
                HuffmanDecompress.executeHuffmanDecompression("res/huff-rle/r", "res/huff-rle/", fileName , compressedRleFileNameNoExtension);

                // Delete rebuilt huffman file
                File decompressedHuffmanFile = new File("res/huff-rle/r" + fileNameNoExtension + ".haedii");
                decompressedHuffmanFile.delete();
                break;
            case 4:
//                Rle.executeRle("res/original/", "res/rle-huff/", fileName, fileNameNoExtension);

                break;
            default:
                System.out.println("Invalid option!");
        }



    }

}
