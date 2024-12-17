package com.example.studentutilitysoftware.CompressFeature;

import com.example.studentutilitysoftware.Models.Trees.HuffmanTree;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileDragDropController {

    @FXML
    private Label dropZone;
    @FXML
    private Label statusLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button compressButton;
    @FXML
    private StackPane SomePane;
    @FXML
    private Label CompressionRatio;
    @FXML
    private Label OriginalSize;
    @FXML
    private Label CompressedSize;
    @FXML
    private TableView<HashMap.Entry<Character, Integer>> FrequencyTable;
    @FXML
    private TableView<HashMap.Entry<Character, String>> HuffmanCodesTable;
    @FXML
    private TextArea EncodedStringTA;
    @FXML
    private CategoryAxis Characters;
    @FXML
    private NumberAxis Frequencies;

    private File selectedFile;
    private HashMap<Character, String> HCMap = new HashMap<>();
    private String FileType = "";


    @FXML
    public void initialize() {
        dropZone.setOnDragOver(event -> {
            if (event.getGestureSource() != dropZone && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        dropZone.setOnDragDropped(this::handleFileDrop);
    }

    private void handleFileDrop(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasFiles()) {
            selectedFile = dragboard.getFiles().getFirst();
            if (validateFile(selectedFile)) {
                dropZone.setText("File Selected: " + selectedFile.getName());
                statusLabel.setText("Ready for Compression");
            } else {
                dropZone.setText("Invalid File");
                statusLabel.setText("Supported formats: .txt, .png, .jpg, .pdf");
            }
        }
        event.setDropCompleted(true);
        event.consume();
    }

    private boolean validateFile(File file) {
        String fileName = file.getName().toLowerCase();
        if(fileName.endsWith(".txt")){
            FileType = ".txt";
            return true;
        }else if(fileName.endsWith(".png")){
            FileType = ".png";
            return true;
        } else if (fileName.endsWith(".jpg")) {
            FileType = ".jpg";
            return true;
        }else if(fileName.endsWith(".pdf")){
            FileType = ".pdf";
            return true;
        }else{
            return false;
        }
    }

    @FXML
    private void compressFile() {
        if (selectedFile == null) {
            statusLabel.setText("No file selected!");
            return;
        }

        Task<String> compressionTask = createCompressionTask(selectedFile);
        progressBar.progressProperty().bind(compressionTask.progressProperty());

        compressionTask.setOnSucceeded(event -> {
            statusLabel.setText("Compression Successful!");
            progressBar.progressProperty().unbind();
            progressBar.setProgress(1.0);

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Compressed File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Compressed Files (*.zip)", "*.zip"));


            File compressedFile = fileChooser.showSaveDialog(null);
            if (compressedFile != null) {
                try {
                    saveCompressedFile(compressionTask.getValue(), compressedFile);
                } catch (IOException e) {
                    statusLabel.setText("Error saving compressed file!");
                }
            }

            try {
                saveMetaFile(HCMap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });

        compressionTask.setOnFailed(event -> {
            statusLabel.setText("Compression Failed!");
            progressBar.progressProperty().unbind();
            progressBar.setProgress(0);
        });

        new Thread(compressionTask).start();
    }

    private void saveMetaFile(Map<Character, String> huffmanCodesMap) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Metadata File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("FileType: " + FileType + "\n");
                writer.write("Character\tHuffmanCode\n");
                writer.write("========================\n");

                for (var entry : huffmanCodesMap.entrySet()) {
                    char character = entry.getKey();
                    String huffmanCode = entry.getValue();

                    // Format special characters for readability in the file
                    String displayCharacter = switch (character) {
                        case '\n' -> "\\n";
                        case '\t' -> "\\t";
                        case '\r' -> "\\r";
                        default -> String.valueOf(character);
                    };

                    writer.write(displayCharacter + "\t\t" + huffmanCode + "\n");
                }

                System.out.println("Metadata file saved successfully at: " + file.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("An error occurred while saving the metadata file: " + e.getMessage());
                throw e;
            }
        } else {
            System.out.println("File save operation was canceled by the user.");
        }
    }


    private Task<String> createCompressionTask(File inputFile) {
        return new Task<String>() {
            @Override
            protected String call() throws Exception {
                updateProgress(0, 100);

                byte[] fileBytes = Files.readAllBytes(inputFile.toPath());
                boolean isTextFile = isTextFile(inputFile);

                HashMap<Character, Integer> frequencyMap = calculateFrequency(fileBytes);
                updateProgress(30, 100);

                HuffmanTree huffmanTree = new HuffmanTree(frequencyMap);
                HashMap<Character, String> huffmanCodes = huffmanTree.getHuffmanCode();
                HCMap = huffmanCodes;

                StringBuilder encodedData = encodeData(fileBytes, huffmanCodes);
                updateProgress(70, 100);

                Platform.runLater(() -> {
                    PopulateFrequencyTable(frequencyMap);
                    PopulateHuffmanCodesTable(huffmanCodes);
                    EncodedStringTA.setText(encodedData.toString());
                    updateCompressionChart(frequencyMap);
                });

                long originalSize = fileBytes.length;
                long compressedSize = encodedData.length() / 8; // Estimate the compressed size
                double compressionRatio = ((double) (originalSize - compressedSize) / originalSize) * 100;

                Platform.runLater(() -> {
                    OriginalSize.setText("Original Size: " + originalSize + " bytes");
                    CompressedSize.setText("Compressed Size: " + compressedSize + " bytes");
                    CompressionRatio.setText("Compression Ratio: " + String.format("%.2f", compressionRatio) + "%");
                });

                updateProgress(100, 100);
                return encodedData.toString();  // Return the encoded data for compression
            }
        };
    }

    private boolean isTextFile(File file) throws IOException {
        String mimeType = Files.probeContentType(file.toPath());
        return mimeType != null && mimeType.startsWith("text");
    }

    private HashMap<Character, Integer> calculateFrequency(byte[] fileBytes) {
        HashMap<Character, Integer> frequencyMap = new HashMap<>();
        for (byte b : fileBytes) {
            char ch = (char) b;
            frequencyMap.put(ch, frequencyMap.getOrDefault(ch, 0) + 1);
        }
        return frequencyMap;
    }

    private StringBuilder encodeData(byte[] fileBytes, HashMap<Character, String> huffmanCodes) {
        StringBuilder encodedData = new StringBuilder();
        for (byte b : fileBytes) {
            String code = huffmanCodes.get((char) b);
            if (code == null) {
                throw new IllegalArgumentException("Byte not found in Huffman Code Map: " + b);
            }
            encodedData.append(code);
        }
        return encodedData;
    }

    private void saveCompressedFile(String encodedData, File compressedFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(compressedFile);
             ZipOutputStream zipOutputStream = new ZipOutputStream(fos)) {

            ZipEntry zipEntry = new ZipEntry("compressed_data");
            zipOutputStream.putNextEntry(zipEntry);

            byte[] dataBytes = encodedData.getBytes();
            zipOutputStream.write(dataBytes);

            zipOutputStream.closeEntry();
        }
    }

    private void PopulateFrequencyTable(Map<Character, Integer> frequencyMap) {

        TableColumn<Map.Entry<Character, Integer>, String> characterColumn = new TableColumn<>("Character");
        characterColumn.setPrefWidth(125);
        characterColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Character, Integer>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<Character, Integer>, String> param) {
                return new SimpleStringProperty(param.getValue().getKey().toString());  // Convert character to string
            }
        });

        TableColumn<Map.Entry<Character, Integer>, String> frequencyColumn = new TableColumn<>("Frequency");
        frequencyColumn.setPrefWidth(125);
        frequencyColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Character, Integer>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<Character, Integer>, String> param) {
                return new SimpleStringProperty(param.getValue().getValue().toString());  // Convert frequency to string
            }
        });

        // Populate the frequency table with data
        ObservableList<Map.Entry<Character, Integer>> data = FXCollections.observableArrayList(frequencyMap.entrySet());
        FrequencyTable.getColumns().setAll(characterColumn, frequencyColumn);  // Set columns
        FrequencyTable.setItems(data);  // Set items to the table
    }


    private void PopulateHuffmanCodesTable(Map<Character, String> huffmanCodesMap) {
        TableColumn<Map.Entry<Character, String>, String> characterColumn = new TableColumn<>("Character");
        characterColumn.setPrefWidth(125);
        characterColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Character, String>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<Character, String>, String> param) {
                return new SimpleStringProperty(param.getValue().getKey().toString());  // Convert character to string
            }
        });

        TableColumn<Map.Entry<Character, String>, String> huffmanCodeColumn = new TableColumn<>("Huffman Code");
        huffmanCodeColumn.setPrefWidth(125);
        huffmanCodeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Character, String>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<Character, String>, String> param) {
                return new SimpleStringProperty(param.getValue().getValue());  // Get the Huffman code
            }
        });

        // Populate the Huffman codes table with data
        ObservableList<Map.Entry<Character, String>> data = FXCollections.observableArrayList(huffmanCodesMap.entrySet());
        HuffmanCodesTable.getColumns().setAll(characterColumn, huffmanCodeColumn);  // Set columns
        HuffmanCodesTable.setItems(data);  // Set items to the table
    }

    private void updateCompressionChart(HashMap<Character, Integer> frequencyMap) {
        Characters.setLabel("Characters");
        Frequencies.setLabel("Frequencies");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Characters");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Frequencies");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Character Frequencies");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Character Frequencies");

        // Add data points to the series
        for (var entry : frequencyMap.entrySet()) {
            String character = String.valueOf(entry.getKey());
            Number frequency = entry.getValue();
            series.getData().add(new XYChart.Data<>(character, frequency));
        }

        chart.setPrefWidth(500);
        chart.setPrefHeight(235);

        Platform.runLater(() -> {
            chart.getData().clear();
            chart.getData().add(series);

            SomePane.getChildren().clear();
            SomePane.getChildren().add(chart);
        });
    }


}
