package com.example.studentutilitysoftware.DashBoard;

import animatefx.animation.BounceIn;
import animatefx.animation.FadeIn;
import animatefx.animation.FadeInRight;
import animatefx.animation.SlideInRight;
import com.example.studentutilitysoftware.DataBase.DatabaseConnection;
import com.example.studentutilitysoftware.Models.Expense;
import com.example.studentutilitysoftware.Models.LinkedList.LinkedList;
import com.example.studentutilitysoftware.Models.Note;
import com.example.studentutilitysoftware.RemainingControllers.SideBarController;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.PriorityQueue;

public class DashBoardController {

    public TextField UserNametf;
    @FXML
    private XYChart<String, Number> expensesChart;

    @FXML
    private Label TotalLabel;

    @FXML
    private Label WelcomeLabel;

    @FXML
    private TextField FNametf;

    @FXML
    private ImageView ProfileImage;

    @FXML
    private Label LastNoteLabel;

    @FXML
    private Label SecondLastNoteLabel;

    @FXML
    private Label ThirdLastNoteLabel;

    @FXML
    private TextArea LastTA;

    @FXML
    private TextArea SecondTA;

    @FXML
    private TextArea ThirdTA;

    private float total;
    private LinkedList<Expense> expenses;
    private String user;

    private PriorityQueue<Note> notesQueue;

    protected void setProfileImage() throws SQLException {
        String profilePicturePath = null;
        String query = "SELECT profile_picture FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    profilePicturePath = rs.getString("profile_picture");
                }
            }
        }

        if (profilePicturePath != null && !profilePicturePath.isEmpty()) {
            File imageFile = new File(profilePicturePath);
            if (imageFile.exists()) {
                Image profileImage = new Image(imageFile.toURI().toString());
                ProfileImage.setImage(profileImage);
                applyCircularImage(ProfileImage.getImage());
            } else {
                System.out.println("Profile picture file not found at the specified path.");
            }
        } else {
            System.out.println("No profile picture set for the user.");
        }
    }

    @FXML
    protected void UpdateProfilePicture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a Profile Picture");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile == null) {
            System.out.println("No file selected.");
            return;
        }

        String newProfilePicturePath = selectedFile.getAbsolutePath();

        String updateQuery = "UPDATE users SET profile_picture = ? WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setString(1, newProfilePicturePath);
            stmt.setString(2, user);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Profile picture updated successfully.");
                File imageFile = new File(newProfilePicturePath);
                if (imageFile.exists()) {
                    Image newImage = new Image(imageFile.toURI().toString());
                    ProfileImage.setImage(newImage);
                    ShowAlert(Alert.AlertType.INFORMATION,"Logout!", null ,"Please Logout to Save Your Changes!");
                }
            } else {
                System.out.println("User not found or no changes were made.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error updating profile picture in the database.");
        }
    }

    @FXML
    protected void UpdateUserInfo() {

        String username = UserNametf.getText();
        String fullname = FNametf.getText();

        if (username == null || username.trim().isEmpty() || fullname == null || fullname.trim().isEmpty()) {
            System.out.println("Username or Full Name cannot be empty.");
            return;
        }

        String updateQuery = "UPDATE users SET Full_name = ?, Username = ? WHERE Username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            // Set parameters in the query
            stmt.setString(1, fullname);
            stmt.setString(2, username);
            stmt.setString(3, this.user);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("User information updated successfully.");
            } else {
                System.out.println("User not found or no changes were made.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error updating user information.");
        }
    }


    public void initialize() throws SQLException {
        expenses = loadExpenses();
        populateExpensesChart();
        setTotal(total);
        notesQueue = new PriorityQueue<>(Comparator
                .comparingInt(Note::getPriority)
                .thenComparing(Note::getDate).reversed());
        loadNotesFromDatabase();
        setUpNotes(notesQueue);
        setProfileImage();
    }

    private void setUpNotes(PriorityQueue<Note> notesQueue) {
        Comparator<Note> comparator = (note1, note2) -> {

            int priorityComparison = Integer.compare(note2.getPriority(), note1.getPriority());
            if (priorityComparison != 0) {
                return priorityComparison;
            }

            LocalDate currentDate = LocalDate.now();
            long daysDiff1 = ChronoUnit.DAYS.between(note1.getDate(), currentDate);
            long daysDiff2 = ChronoUnit.DAYS.between(note2.getDate(), currentDate);
            return Long.compare(Math.abs(daysDiff1), Math.abs(daysDiff2));
        };


        PriorityQueue<Note> sortedQueue = new PriorityQueue<>(comparator);
        sortedQueue.addAll(notesQueue);

        notesQueue.clear();
        notesQueue.addAll(sortedQueue);

        for (int i = 0; i < 3 && !notesQueue.isEmpty(); i++) {
            Note newNote = notesQueue.poll();
            if(i == 0){
                LastNoteLabel.setText(newNote.getTitle());
                LastTA.setText(newNote.getContent());
            }else if(i == 1){
                SecondLastNoteLabel.setText(newNote.getTitle());
                SecondTA.setText(newNote.getContent());
            }else if(i == 2){
                ThirdLastNoteLabel.setText(newNote.getTitle());
                ThirdTA.setText(newNote.getContent());
            }
        }
    }


    private void setTotal(float total) {
        TotalLabel.setText(STR."Total Expense: \{String.valueOf(total)}");
    }
    private void setWelcome(String UserName){
        WelcomeLabel.setText(STR."WELCOME BACK, \{UserName.toUpperCase()}" + "!");
    }

    private void populateExpensesChart() {

        if (expenses == null || expenses.isEmpty()) {
            System.out.println("No expenses found.");
            return;
        }
        expenses.mergeSort("date");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Sorted Monthly Expenses");

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM");

        for (Expense expense : expenses.getExpenses()) {
            String formattedDate = expense.getDate().toLocalDate().format(dateFormatter);
            series.getData().add(new XYChart.Data<>(formattedDate, expense.getAmount()));
        }

        expensesChart.getData().clear();
        expensesChart.getData().add(series);
        FadeInRight animation = new FadeInRight(expensesChart);
        animation.setTimeline(
                new Timeline(
                        new KeyFrame(Duration.millis(0),
                                new KeyValue(expensesChart.opacityProperty(), 0),
                                new KeyValue(expensesChart.translateXProperty(), expensesChart.getBoundsInParent().getWidth())
                        ),
                        new KeyFrame(Duration.millis(2000), // Set to 2 seconds
                                new KeyValue(expensesChart.opacityProperty(), 1),
                                new KeyValue(expensesChart.translateXProperty(), 0)
                        )
                )
        );
        animation.play();

    }

    private LinkedList<Expense> loadExpenses() {
        total = 0;
        LinkedList<Expense> expenses = new LinkedList<>();

        try {
            LocalDate currentDate = LocalDate.now();
            LocalDate startOfMonth = currentDate.withDayOfMonth(1);
            Date sqlStartOfMonth = Date.valueOf(startOfMonth);
            Date sqlCurrentDate = Date.valueOf(currentDate);

            Connection connection = DatabaseConnection.getConnection();
            String query = "SELECT title, amount, date FROM expenses WHERE username = ? AND date BETWEEN ? AND ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, user);
            preparedStatement.setDate(2, sqlStartOfMonth);
            preparedStatement.setDate(3, sqlCurrentDate);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String title = resultSet.getString("title");
                float amount = resultSet.getFloat("amount");
                Date date = resultSet.getDate("date");

                Expense newExpense = new Expense();
                newExpense.setTitle(title);
                newExpense.setAmount(amount);
                newExpense.setDate(date);

                expenses.insert(newExpense);
                total += newExpense.getAmount();
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return expenses;
    }

    public void setUser(String username) throws SQLException {
        this.user = username;
        setWelcome(username);
        initialize();
    }

    private void loadNotesFromDatabase() {
        String query = "SELECT NoteID, Title, Content, Priority, Date FROM Notes WHERE Username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user);

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

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void ShowAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void applyCircularImage(Image profileImage) {
        ProfileImage.setImage(profileImage);

        Circle circleMask = new Circle(75);
        circleMask.setCenterX(75); // Center X of the circle
        circleMask.setCenterY(75); // Center Y of the circle
        ProfileImage.setClip(circleMask);

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        Image clippedImage = ProfileImage.snapshot(params, null);

        ProfileImage.setClip(null);
        ProfileImage.setImage(clippedImage);
    }


}
