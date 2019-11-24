package com.ufabc.live.huffman;

import java.util.HashMap;
import java.util.Map;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.PriorityQueue;

abstract class HuffmanTree implements Comparable<HuffmanTree> {
    public int frequency; // the frequency of this tree
    public HuffmanTree(int freq) { frequency = freq; }

    // compares on the frequency
    @Override
    public int compareTo(HuffmanTree tree) {
        return frequency - tree.frequency;
    }
}

class HuffmanLeaf extends HuffmanTree {
    public char value; // the character this leaf represents
   
    public HuffmanLeaf(int freq, char val) {
        super(freq);
        value = val;
    }
}

class HuffmanNode extends HuffmanTree {
    public HuffmanTree left, right; // subtrees
   
    public HuffmanNode(HuffmanTree l, HuffmanTree r) {
        super(l.frequency + r.frequency);
        left = l;
        right = r;
    }
}

public class Huffman {
    // input is an array of frequencies, indexed by character code
    public static HuffmanTree buildTree(int[] charFreqs) {
        PriorityQueue<HuffmanTree> trees = new PriorityQueue<>();
        // initially, we have a forest of leaves
        // one for each non-empty character
        for (int i = 0; i < charFreqs.length; i++)
            if (charFreqs[i] > 0)
                trees.offer(new HuffmanLeaf(charFreqs[i], (char)i));

        assert trees.size() > 0;
        // loop until there is only one tree left
        while (trees.size() > 1) {
            // two trees with least frequency
            HuffmanTree a = trees.poll();
            HuffmanTree b = trees.poll();

            // put into new node and re-insert into queue
            trees.offer(new HuffmanNode(a, b));
        }
        return trees.poll();
    }

    public static void printCodes(HuffmanTree tree, StringBuffer prefix, Map<Character, String> hashMapOfCode) {
        assert tree != null;
        if (tree instanceof HuffmanLeaf) {
            HuffmanLeaf leaf = (HuffmanLeaf)tree;
 
            // print out character, frequency, and code for this leaf (which is just the prefix)
            //System.out.println(leaf.value + "\t" + leaf.frequency + "\t" + prefix);
            hashMapOfCode.put(leaf.value, prefix.toString());
 
        } else if (tree instanceof HuffmanNode) {
            HuffmanNode node = (HuffmanNode)tree;
 
            // traverse left
            prefix.append('0');
            printCodes(node.left, prefix, hashMapOfCode);
            prefix.deleteCharAt(prefix.length()-1);
 
            // traverse right
            prefix.append('1');
            printCodes(node.right, prefix, hashMapOfCode);
            prefix.deleteCharAt(prefix.length()-1);
        }
    }

    public static void huffman(byte[] b) throws FileNotFoundException, IOException {   

        int[] charFreqs = new int[65536];
        
        int originalSize = b.length * 16;
        System.out.println("Original size: " + originalSize);

        for (char c : new String(b).toCharArray())
        {	
            charFreqs[c]++;
        }
        

        // build tree
        HuffmanTree tree = buildTree(charFreqs);
        
        Map<Character, String> hashMapOfCodes = new HashMap<>();

        // print out results
        System.out.println("SYMBOL\tWEIGHT\tHUFFMAN CODE");
        printCodes(tree, new StringBuffer(), hashMapOfCodes);
        
        int compressedSize = 0;
        for (char c : new String(b).toCharArray())
        {	
            compressedSize += hashMapOfCodes.get(c).length();
        }
        System.out.println("Compressed size: " + compressedSize);
    }
}