package com.example.studentutilitysoftware.Models.Trees;

import java.util.HashMap;
import java.util.PriorityQueue;

public class HuffmanTree {

    public static class HuffmanNode implements Comparable<HuffmanNode> {
        char character;
        int freq;
        HuffmanNode left, right;

        public HuffmanNode(char character, int freq) {
            this.character = character;
            this.freq = freq;
        }

        public HuffmanNode(char character, int freq, HuffmanNode left, HuffmanNode right) {
            this.character = character;
            this.freq = freq;
            this.left = left;
            this.right = right;
        }

        @Override
        public int compareTo(HuffmanNode o) {
            return this.freq - o.freq;
        }
    }

    private HuffmanNode root;
    private HashMap<Character, String> huffmanCode;

    public HuffmanTree(HashMap<Character, Integer> frequency) {
        this.root = buildHuffmanTree(frequency);
        this.huffmanCode = new HashMap<>();
        generateHuffmanCodes(root, "");
    }

    private HuffmanNode buildHuffmanTree(HashMap<Character, Integer> frequency) {
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
        frequency.forEach((character, freq) -> pq.add(new HuffmanNode(character, freq)));

        while (pq.size() > 1) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();
            HuffmanNode parent = new HuffmanNode('\0', left.freq + right.freq, left, right);
            pq.add(parent);
        }
        return pq.poll();
    }

    private void generateHuffmanCodes(HuffmanNode node, String code) {
        if (node == null) return;

        if (node.left == null && node.right == null) {
            huffmanCode.put(node.character, code);
            return;
        }

        generateHuffmanCodes(node.left, code + "0");
        generateHuffmanCodes(node.right, code + "1");
    }

    public HashMap<Character, String> getHuffmanCode() {
        return huffmanCode;
    }

    public HuffmanNode getRoot() {
        return root;
    }
}
