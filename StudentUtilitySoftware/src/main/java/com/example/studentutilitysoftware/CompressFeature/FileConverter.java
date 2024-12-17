package com.example.studentutilitysoftware.CompressFeature;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class FileConverter {

    // Convert file to String (for text files) or to Base64 (for images) or to byte[] (for binary files like PDFs)
    public Object convertFile(File file) throws Exception {
        String fileName = file.getName().toLowerCase();
        Path filePath = file.toPath();

        if (fileName.endsWith(".txt")) {
            System.out.println("Converting text file...");
            return convertTextFileToString(filePath); // Return as string
        } else if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            System.out.println("Converting image file to Base64...");
            return convertImageToBase64(filePath); // Return as Base64 string
        } else if (fileName.endsWith(".pdf")) {
            System.out.println("Converting PDF file to byte array...");
            return convertPdfToBytes(file); // Return as byte array
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + fileName);
        }
    }

    // Converts a text file to a string
    private String convertTextFileToString(Path filePath) throws IOException {
        return Files.readString(filePath);
    }

    // Converts an image file to a Base64 string
    private String convertImageToBase64(Path filePath) throws IOException {
        byte[] fileBytes = Files.readAllBytes(filePath);
        return Base64.getEncoder().encodeToString(fileBytes);
    }

    // Converts a PDF file to a byte array
    private byte[] convertPdfToBytes(File file) throws IOException {
        return Files.readAllBytes(file.toPath()); // Read PDF as raw binary data
    }

    // Convert file to byte array for compression (handles all file types except text)
    public byte[] convertFileToBytes(File file) throws IOException {
        String fileName = file.getName().toLowerCase();

        if (fileName.endsWith(".txt")) {
            System.out.println("Converting text file to byte array...");
            return Files.readAllBytes(file.toPath()); // Text files can be read as byte arrays too
        } else if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            System.out.println("Converting image file to byte array...");
            return Files.readAllBytes(file.toPath()); // Images read as byte arrays
        } else if (fileName.endsWith(".pdf")) {
            System.out.println("Converting PDF file to byte array...");
            return Files.readAllBytes(file.toPath()); // PDFs read as byte arrays
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + fileName);
        }
    }

    // Write compressed binary data to a file
    public void writeCompressedFile(File outputFile, byte[] compressedData) throws IOException {
        try {
            Files.write(outputFile.toPath(), compressedData);
            System.out.println("Compressed file written successfully to: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error writing compressed file: " + e.getMessage());
            throw e;
        }
    }

    // Write encoded string (Huffman encoded text) to a text file
    public void writeEncodedFile(File outputFile, String encodedText) throws IOException {
        try {
            Files.writeString(outputFile.toPath(), encodedText);
            System.out.println("Encoded text written successfully to: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error writing encoded text: " + e.getMessage());
            throw e;
        }
    }
}
