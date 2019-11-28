package com.ufabc.live;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("Select an option:");
        System.out.println("1 - Huffman");
        System.out.println("2 - RLE");
        System.out.println("3 - Huffman-RLE");
        System.out.println("4 - RLE-Huffman");

        Scanner scanner = new Scanner(System.in);

        int option = scanner.nextInt();

        switch (option){
            case 1:

                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            default:
                System.out.println("Invalid option!");
        }



    }

}
