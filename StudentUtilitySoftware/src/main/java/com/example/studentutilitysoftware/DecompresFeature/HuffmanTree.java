package com.example.studentutilitysoftware.DecompresFeature;

import java.util.*;

public class HuffmanTree {

    private Node root;
    private final Map<Character, String> huffmanCodes;

    // Constructor builds the tree and generates Huffman codes
    public HuffmanTree(Map<Character, Integer> frequencyMap) {
        root = buildTree(frequencyMap);
        huffmanCodes = new HashMap<>();
        generateCodes(root, "");
    }

    // Get the generated Huffman codes
    public Map<Character, String> getHuffmanCodes() {
        return huffmanCodes;
    }

    // Decode a binary string using the Huffman tree
    public String decode(String encodedString) {
        StringBuilder decodedString = new StringBuilder();
        Node current = root;

        for (char bit : encodedString.toCharArray()) {
            current = (bit == '0') ? current.left : current.right;

            // If we reach a leaf node, append the character
            if (current.isLeaf()) {
                decodedString.append(current.character);
                current = root; // Reset to root for the next character
            }
        }

        return decodedString.toString();
    }

    // Build the Huffman tree from a frequency map
    private Node buildTree(Map<Character, Integer> frequencyMap) {
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(node -> node.frequency));

        // Create a node for each character and add it to the priority queue
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            priorityQueue.add(new Node(entry.getKey(), entry.getValue()));
        }

        // Merge nodes until there is only one tree left
        while (priorityQueue.size() > 1) {
            Node left = priorityQueue.poll();
            Node right = priorityQueue.poll();

            // Create a parent node with combined frequency
            Node parent = new Node(null, left.frequency + right.frequency, left, right);
            priorityQueue.add(parent);
        }

        // The root of the tree
        return priorityQueue.poll();
    }

    // Recursively generate Huffman codes
    private void generateCodes(Node node, String code) {
        if (node == null) {
            return;
        }

        // If leaf node, assign the code
        if (node.isLeaf()) {
            huffmanCodes.put(node.character, code);
        } else {
            generateCodes(node.left, code + "0");
            generateCodes(node.right, code + "1");
        }
    }

    public Node getRoot() {
        return this.root;
    }

    // Node class represents each node in the Huffman tree
    static class Node {
        Character character; // Character stored in the node (null for internal nodes)
        int frequency; // Frequency of the character
        Node left; // Left child
        Node right; // Right child

        // Constructor for leaf nodes
        Node(Character character, int frequency) {
            this.character = character;
            this.frequency = frequency;
        }

        // Constructor for internal nodes
        Node(Character character, int frequency, Node left, Node right) {
            this.character = character;
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }

        // Check if the node is a leaf
        boolean isLeaf() {
            return left == null && right == null;
        }


        public Node getLeft() {
            return this.left;
        }

        public Node getRight() {
            return  this.right;
        }

        public int getCharacter() {
            return this.character;
        }
    }
}

