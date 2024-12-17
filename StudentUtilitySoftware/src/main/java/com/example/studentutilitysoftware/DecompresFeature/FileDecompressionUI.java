package com.example.studentutilitysoftware.DecompresFeature;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;

import java.io.File;

public class FileDecompressionUI {

    @FXML private Label compressedFileDropZone;
    @FXML private Label metaDataFileDropZone;
    @FXML private Label statusLabel;
    @FXML private ProgressBar progressBar;
    @FXML private Button DecompressButton;

    private File compressedFile;
    private File metadataFile;

    public void initialize() {
        compressedFileDropZone.setOnDragOver(this::handleDragOver);
        compressedFileDropZone.setOnDragDropped(this::handleDragDroppedCompressedFile);

        metaDataFileDropZone.setOnDragOver(this::handleDragOver);
        metaDataFileDropZone.setOnDragDropped(this::handleDragDroppedMetadataFile);

        DecompressButton.setOnAction(event -> {
            if (compressedFile != null && metadataFile != null) {
                try {
                    HuffmanDecompressor decompressor = new HuffmanDecompressor();

                    statusLabel.setText("Decompressing files...");
                    progressBar.setProgress(-1.0);

                    decompressor.decompressAndReconstruct(
                            compressedFile.getPath(),
                            metadataFile.getPath()
                    );

                    statusLabel.setText("Decompression completed successfully!");
                    progressBar.setProgress(1.0);
                } catch (Exception e) {
                    showAlert("Decompression Failed", "Error during decompression: " + e.getMessage(), AlertType.ERROR);
                    statusLabel.setText("Decompression failed!");
                    progressBar.setProgress(0.0);
                }
            } else {
                showAlert("Missing Files", "Please provide both the compressed file and metadata file.", AlertType.WARNING);
                statusLabel.setText("Waiting for files...");
            }
        });
    }

    private void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    private void handleDragDroppedCompressedFile(DragEvent event) {
        File file = event.getDragboard().getFiles().getFirst();
        if (validateCompressedFile(file)) {
            compressedFile = file;
            compressedFileDropZone.setText("Compressed File: " + file.getName());
            statusLabel.setText("Compressed file selected.");
        } else {
            showAlert("Invalid File", "The selected file is not a valid compressed file.", AlertType.WARNING);
        }
        event.setDropCompleted(true);
        event.consume();
    }

    private void handleDragDroppedMetadataFile(DragEvent event) {
        File file = event.getDragboard().getFiles().getFirst();
        if (validateMetadataFile(file)) {
            metadataFile = file;
            metaDataFileDropZone.setText("Metadata File: " + file.getName());
            statusLabel.setText("Metadata file selected.");
        } else {
            showAlert("Invalid File", "The selected file is not a valid metadata file.", AlertType.WARNING);
        }
        event.setDropCompleted(true);
        event.consume();
    }

    private boolean validateCompressedFile(File file) {
        return file != null && file.getName().toLowerCase().endsWith(".zip");
    }

    private boolean validateMetadataFile(File file) {
        return file != null && file.getName().toLowerCase().endsWith(".txt");
    }

    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
