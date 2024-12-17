package com.example.studentutilitysoftware.NotesFeature;

import com.example.studentutilitysoftware.DataBase.DatabaseConnection;
import com.example.studentutilitysoftware.Models.Note;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Stack;

public class NotesController {

    @FXML
    private TextField Titletf;

    @FXML
    private TextArea ContentTA;

    @FXML
    private RadioButton HighRB, NormalRB, LowRB;

    @FXML
    private DatePicker NoteDatePicker;

    @FXML
    private TableView<Note> NotesTable, RecentNotesTable;

    @FXML
    private TableColumn<Note, String> NoteIDCol, TitleCol, DateCol, RENotesIDCol, RETitleCol, REDateCol;

    @FXML
    private Label LastNoteLabel;

    @FXML
    private TextArea LastTA;

    @FXML
    private Label TextFileDropZone;

    @FXML
    private Button UploadBtn;

    private PriorityQueue<Note> notesQueue;
    private Stack<Note> recentNotesStack;
    private ObservableList<Note> recentNotesList;
    private ObservableList<Note> notesList;

    private String currentUser;
    private boolean fileDropped = false;
    private File droppedFile;

    @FXML
    public void initialize() {
        UploadBtn.setOnAction(event -> {
            if (fileDropped && droppedFile != null) {
                uploadFile(droppedFile);
            } else {
                System.out.println("No file dropped to upload.");
            }
        });
    }
    public void Initialize() {
        notesQueue = new PriorityQueue<>(Comparator
                .comparingInt(Note::getPriority)
                .thenComparing(Note::getDate).reversed());

        recentNotesStack = new Stack<>();
        recentNotesList = FXCollections.observableArrayList();
        notesList = FXCollections.observableArrayList();

        NotesTable.setItems(notesList);
        RecentNotesTable.setItems(recentNotesList);

        NoteIDCol.setCellValueFactory(cellData -> cellData.getValue().noteIDProperty());
        TitleCol.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        DateCol.setCellValueFactory(cellData -> cellData.getValue().dateProperty().asString());

        RENotesIDCol.setCellValueFactory(cellData -> cellData.getValue().noteIDProperty());
        RETitleCol.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        REDateCol.setCellValueFactory(cellData -> cellData.getValue().dateProperty().asString());

        NoteDatePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(LocalDate.now()));
            }
        });

        TextFileDropZone.setOnDragOver(event -> {
            if (event.getGestureSource() != TextFileDropZone && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        TextFileDropZone.setOnDragDropped(this::handleFileDrop);

        loadNotesFromDatabase();
    }

    @FXML
    public void addNote() {
        String title = Titletf.getText();
        String content = ContentTA.getText();
        LocalDate date = NoteDatePicker.getValue();

        int priority = HighRB.isSelected() ? 1 : NormalRB.isSelected() ? 2 : 3;

        String noteID = generateNoteID();

        Note note = new Note(noteID, title, content, priority, date);

        notesQueue.add(note);
        recentNotesStack.push(note);
        recentNotesList.addFirst(note);
        notesList.setAll(notesQueue);

        Titletf.clear();
        ContentTA.clear();
        NoteDatePicker.setValue(null);
    }

    @FXML
    public void saveRecentNotes() {
        for (Note note : recentNotesStack) {
            saveNoteToDatabase(note);
        }
        recentNotesStack.clear();
        recentNotesList.clear();
    }

    @FXML
    public void undoRecentNote() {
        if (!recentNotesStack.isEmpty()) {
            Note lastNote = recentNotesStack.pop();
            recentNotesList.remove(lastNote);
            notesQueue.remove(lastNote);
            notesList.setAll(notesQueue);
        }
    }

    @FXML
    public void extractNote() {
        Note selectedNote = NotesTable.getSelectionModel().getSelectedItem();
        if (selectedNote == null) {
            selectedNote = RecentNotesTable.getSelectionModel().getSelectedItem();
        }
        if (selectedNote != null) {
            LastNoteLabel.setText(selectedNote.getTitle());
            LastTA.setText(selectedNote.getContent());
        }
    }

    @FXML
    public void downloadNote() {
        Note selectedNote = NotesTable.getSelectionModel().getSelectedItem();
        if (selectedNote == null) {
            selectedNote = RecentNotesTable.getSelectionModel().getSelectedItem();
        }
        if (selectedNote != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName(selectedNote.getTitle() + ".txt");
            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write("Title: " + selectedNote.getTitle());
                    writer.newLine();
                    writer.write("Content: ");
                    writer.newLine();
                    writer.write(selectedNote.getContent());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    public void uploadFile(File file) {
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String title = reader.readLine();
                StringBuilder content = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    content.append(line).append(System.lineSeparator());
                }

                Note note = new Note(generateNoteID(), title, content.toString(), 1, LocalDate.now());
                notesQueue.add(note);
                recentNotesStack.push(note);
                recentNotesList.addFirst(note);
                notesList.setAll(notesQueue);

                saveNoteToDatabase(note);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleFileDrop(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles() && db.getFiles().getFirst().getName().endsWith(".txt")) {
            droppedFile = db.getFiles().getFirst();
            fileDropped = true; // File has been dropped, but we won't upload until button click
            TextFileDropZone.setText("Selected File: " + droppedFile.getName());
        }
        event.setDropCompleted(true);
        event.consume();
    }

    private String generateNoteID() {
        return String.valueOf((int) (Math.random() * 90000) + 10000);
    }

    private void saveNoteToDatabase(Note note) {
        String query = "INSERT INTO Notes (NoteID, Title, Content, Priority, Date, Username) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, note.getNoteID()); // Note ID
            stmt.setString(2, note.getTitle()); // Title
            stmt.setString(3, note.getContent()); // Content
            stmt.setInt(4, note.getPriority()); // Priority
            stmt.setDate(5, java.sql.Date.valueOf(note.getDate())); // Date
            stmt.setString(6, currentUser); // Username

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void loadNotesFromDatabase() {
        String query = "SELECT NoteID, Title, Content, Priority, Date FROM Notes WHERE Username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, currentUser);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int noteID = rs.getInt("NoteID");
                    String title = rs.getString("Title");
                    String content = rs.getString("Content");
                    int priority = rs.getInt("Priority");
                    LocalDate date = rs.getDate("Date").toLocalDate();

                    Note note = new Note(String.valueOf(noteID), title, content, priority, date);
                    notesQueue.add(note);
                }
            }

            notesList.setAll(notesQueue);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setUser(String username){
        this.currentUser = username;
        Initialize();
    }
}
