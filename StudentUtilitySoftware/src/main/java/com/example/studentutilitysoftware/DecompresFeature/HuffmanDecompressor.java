package com.example.studentutilitysoftware.DecompresFeature;

import javafx.stage.FileChooser;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HuffmanDecompressor {

    /**
     * Decompresses a file using Huffman encoding and reconstructs the original data.
     *
     * @param compressedFilePath Path to the compressed file containing encoded data.
     * @param metadataFilePath   Path to the metadata file with Huffman codes and file type.
     * @throws IOException If an I/O error occurs.
     */
    public void decompressAndReconstruct(String compressedFilePath, String metadataFilePath) throws IOException {
        // Step 1: Read the compressed file to get the encoded string
        String encodedString = readCompressedFile(compressedFilePath);

        // Step 2: Read the metadata file to get the file type and Huffman codes map
        Metadata metadata = readMetadata(metadataFilePath);
        String fileType = metadata.getFileType();
        Map<String, Character> huffmanCodeMap = metadata.getHuffmanCodeMap();

        // Step 3: Decode the encoded string using the Huffman codes map
        String decodedData = decodeUsingHuffman(encodedString, huffmanCodeMap);

        // Step 4: Save the decoded data to a file
        saveDecodedFile(decodedData, fileType);
    }

    /**
     * Reads the compressed file containing the encoded data.
     *
     * @param compressedFilePath Path to the compressed file.
     * @return The encoded data as a string.
     * @throws IOException If an I/O error occurs.
     */
    private String readCompressedFile(String compressedFilePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(compressedFilePath)) {
            StringBuilder encodedData = new StringBuilder();
            int byteRead;
            while ((byteRead = fis.read()) != -1) {
                // Convert each byte to an 8-bit binary string
                encodedData.append(String.format("%8s", Integer.toBinaryString(byteRead & 0xFF)).replace(' ', '0'));
            }
            return encodedData.toString();
        }
    }

    /**
     * Reads the metadata file to extract the file type and Huffman codes map.
     *
     * @param metadataFilePath Path to the metadata file.
     * @return A Metadata object containing the file type and Huffman codes map.
     * @throws IOException If an I/O error occurs.
     */
    /**
     * Reads the metadata file to extract the file type and Huffman codes map.
     *
     * @param metadataFilePath Path to the metadata file.
     * @return A Metadata object containing the file type and Huffman codes map.
     * @throws IOException If an I/O error occurs.
     */
    private Metadata readMetadata(String metadataFilePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(metadataFilePath))) {
            // Read the file type from the first line
            String fileType = reader.readLine().split(": ")[1];

            // Skip the header and separator line
            reader.readLine(); // Skip "Character\tHuffmanCode"
            reader.readLine(); // Skip "========================"

            // Read the Huffman codes map
            Map<String, Character> huffmanCodeMap = new HashMap<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t\t"); // Split by tab separator
                char character = parseCharacter(parts[0]);
                String huffmanCode = parts[1];
                huffmanCodeMap.put(huffmanCode, character);
            }

            return new Metadata(huffmanCodeMap, fileType);
        }
    }


    /**
     * Parses the string representation of a character from the metadata file.
     *
     * @param displayCharacter The string representation of the character.
     * @return The corresponding character.
     */
    private char parseCharacter(String displayCharacter) {
        return switch (displayCharacter) {
            case "\\n" -> '\n';
            case "\\t" -> '\t';
            case "\\r" -> '\r';
            default -> displayCharacter.charAt(0);
        };
    }

    /**
     * Decodes the encoded string using the Huffman codes map.
     *
     * @param encodedString   The encoded data as a string of bits.
     * @param huffmanCodeMap  The map of Huffman codes to their corresponding characters.
     * @return The decoded data as a string.
     */
    private String decodeUsingHuffman(String encodedString, Map<String, Character> huffmanCodeMap) {
        StringBuilder decodedData = new StringBuilder();
        StringBuilder currentCode = new StringBuilder();

        for (char bit : encodedString.toCharArray()) {
            currentCode.append(bit);

            // If the current code matches a Huffman code in the map, decode it
            if (huffmanCodeMap.containsKey(currentCode.toString())) {
                decodedData.append(huffmanCodeMap.get(currentCode.toString()));
                currentCode.setLength(0); // Reset the current code
            }
        }

        return decodedData.toString();
    }

    /**
     * Saves the decompressed data to a file chosen by the user.
     *
     * @param decodedData The decoded data as a string.
     * @param fileType    The file extension for the saved file.
     * @throws IOException If an I/O error occurs.
     */
    private void saveDecodedFile(String decodedData, String fileType) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Decompressed File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(fileType.toUpperCase() + " Files", "*" + fileType)
        );

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(decodedData);
                System.out.println("File decompressed and saved to: " + file.getAbsolutePath());
            }
        }
    }

    /**
     * Inner class to encapsulate metadata information (Huffman codes map and file type).
     */
    private static class Metadata {
        private final Map<String, Character> huffmanCodeMap;
        private final String fileType;

        public Metadata(Map<String, Character> huffmanCodeMap, String fileType) {
            this.huffmanCodeMap = huffmanCodeMap;
            this.fileType = fileType;
        }

        public Map<String, Character> getHuffmanCodeMap() {
            return huffmanCodeMap;
        }

        public String getFileType() {
            return fileType;
        }
    }
}
