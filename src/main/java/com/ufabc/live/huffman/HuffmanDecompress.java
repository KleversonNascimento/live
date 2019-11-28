package com.ufabc.live.huffman;
/* 
 * Reference Huffman coding
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/reference-huffman-coding
 * https://github.com/nayuki/Reference-Huffman-coding
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;


/**
 * Decompression application using static Huffman coding.
 * <p>Usage: java HuffmanDecompress InputFile OutputFile</p>
 * <p>This decompresses files generated by the "HuffmanCompress" application.</p>
 */
public final class HuffmanDecompress {
	
	// Command line main application function.
	public static void main(String[] args) throws IOException {

		Scanner scanner = new Scanner(System.in);

		System.out.println("Huffman Decompression");
		System.out.println("Digite o nome do arquivo (com a a extensão):");
		String fileName = scanner.nextLine();
		String fileNameNoExtension = fileName.split("\\.")[0];
		
		File inputFile  = new File("res/huff/" + fileName);
		File outputFile = new File("res/huff/" + fileNameNoExtension + ".mov");
		
		// Perform file decompression
		try (BitInputStream in = new BitInputStream(new BufferedInputStream(new FileInputStream(inputFile)))) {
			try (OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile))) {
				CanonicalCode canonCode = readCodeLengthTable(in);
				CodeTree code = canonCode.toCodeTree();
				decompress(code, in, out);
			}
		}
	}
	
	
	// To allow unit testing, this method is package-private instead of private.
	static CanonicalCode readCodeLengthTable(BitInputStream in) throws IOException {
		int[] codeLengths = new int[257];
		for (int i = 0; i < codeLengths.length; i++) {
			// For this file format, we read 8 bits in big endian
			int val = 0;
			for (int j = 0; j < 8; j++)
				val = (val << 1) | in.readNoEof();
			codeLengths[i] = val;
		}
		return new CanonicalCode(codeLengths);
	}
	
	
	// To allow unit testing, this method is package-private instead of private.
	static void decompress(CodeTree code, BitInputStream in, OutputStream out) throws IOException {
		HuffmanDecoder dec = new HuffmanDecoder(in);
		dec.codeTree = code;
		while (true) {
			int symbol = dec.read();
			if (symbol == 256)  // EOF symbol
				break;
			out.write(symbol);
		}
	}
	
}