//package com.example.studentutilitysoftware.RemainingControllers;
//
//import com.example.studentutilitysoftware.ExpenseFeature.ExpenseController;
//import com.example.studentutilitysoftware.Home;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Scene;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Button;
//import javafx.scene.control.ButtonType;
//import javafx.scene.layout.BorderPane;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//
//public class SideBarController {
//    private BorderPane layout;
//    private String User;
//
//    @FXML
//    private Button Logoutbtn;
//    @FXML
//    private Button Compressbtn;
//    @FXML
//    private Button Decompressbtn;
//    @FXML
//    private Button Notesbtn;
//    @FXML
//    private Button Expensesbtn;
//    @FXML
//    private Button DashBoardbtn;
//
//
//    public void setLayout(BorderPane layout) {
//        this.layout = layout;
//    }
//
//    @FXML
//    protected void GotoDashBoard() {
//        loadView("/com/example/studentutilitysoftware/DashBoard.fxml",600, "DashBoard");
//    }
//
//    @FXML
//    protected void GotoCompressor() {
//        loadView("/com/example/studentutilitysoftware/FileCompressionUI.fxml",600, "Compressor");
//    }
//
//    @FXML
//    protected void GotoDecompressor() {
//        loadView("/com/example/studentutilitysoftware/FileDecompressionUI.fxml",600, "Decompressor");
//    }
//
//    @FXML
//    protected void GotoNotes() {
//        loadView("/com/example/studentutilitysoftware/Notes.fxml",875, "Notes");
//    }
//
//    @FXML
//    protected void GotoExpenses() {
//        loadView("/com/example/studentutilitysoftware/Expenses.fxml",754, "Expenses");
//    }
//
//    @FXML
//    protected void Logout() throws IOException {
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("Logout Confirmation");
//        alert.setHeaderText(null);
//        alert.setContentText("Are you sure you want to logout?");
//
//        alert.showAndWait().ifPresent(response -> {
//            if (response == ButtonType.OK) {
//                try {
//                    FXMLLoader fxmlLoader = new FXMLLoader(Home.class.getResource("Home.fxml"));
//                    Scene scene = new Scene(fxmlLoader.load(), 1105, 700);
//                    Stage stage = (Stage) Logoutbtn.getScene().getWindow();
//                    stage.setMaxWidth(1105);
//                    stage.setMinWidth(1105);
//                    stage.setTitle("WELCOME!");
//                    stage.setScene(scene);
//                    stage.show();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//    private void loadView(String fxmlPath , int View , String s) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
//            layout.setRight(loader.load());
//            layout.setMinWidth(View);
//            layout.setMaxWidth(View);
//            if(s.equals("DashBoard")){
//                Stage stage = (Stage) DashBoardbtn.getScene().getWindow();
//                stage.setMinWidth(250 + View);
//                stage.setMaxWidth(250 + View);
//            } else if (s.equals("Compressor")) {
//                Stage stage = (Stage) Compressbtn.getScene().getWindow();
//                stage.setMinWidth(250 + View);
//                stage.setMaxWidth(250 + View);
//            }else if (s.equals("Decompressor")) {
//                Stage stage = (Stage) Decompressbtn.getScene().getWindow();
//                stage.setMinWidth(250 + View);
//                stage.setMaxWidth(250 + View);
//            }else if (s.equals("Notes")) {
//                Stage stage = (Stage) Notesbtn.getScene().getWindow();
//                stage.setMinWidth(250 + View);
//                stage.setMaxWidth(250 + View);
//            }else if (s.equals("Expenses")) {
//                ExpenseController expenseController = loader.getController();
//                expenseController.setUser(this.User);
//                loader.setController(expenseController);
//                Stage stage = (Stage) Expensesbtn.getScene().getWindow();
//                stage.setMinWidth(250 + View);
//                stage.setMaxWidth(250 + View);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.err.println("Error loading view: " + fxmlPath);
//        }
//    }
//
//    public void setUser(String username) {
//        this.User = username;
//    }
//}
package com.example.studentutilitysoftware.RemainingControllers;

import animatefx.animation.SlideInLeft;
import animatefx.animation.SlideOutRight;
import com.example.studentutilitysoftware.DashBoard.DashBoardController;
import com.example.studentutilitysoftware.DataBase.DatabaseConnection;
import com.example.studentutilitysoftware.ExpenseFeature.ExpenseController;
import com.example.studentutilitysoftware.Home;
import com.example.studentutilitysoftware.NotesFeature.NotesController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class SideBarController {
    private BorderPane layout;
    private String User;
    private ExpenseController currentExpenseController;

    @FXML
    private StackPane somePane;
    @FXML
    private Button Logoutbtn;
    @FXML
    private AnchorPane SideBarPane;
    @FXML
    private Label UserNameLabel;


    public void setLayout(BorderPane layout) {
        this.layout = layout;
    }

    private void Intialize(String user){
        UserNameLabel.setText(user);
    }

    @FXML
    protected void GotoDashBoard() {
        if (canSwitchFrames()) {
            loadView("/com/example/studentutilitysoftware/DashBoard.fxml", 883, "DashBoard");
        }
    }

    @FXML
    protected void GotoCompressor() {
        if (canSwitchFrames()) {
            loadView("/com/example/studentutilitysoftware/FileCompressionUI.fxml", 881, "Compressor");
        }
    }

    @FXML
    protected void GotoDecompressor() {
        if (canSwitchFrames()) {
            loadView("/com/example/studentutilitysoftware/FileDecompressionUI.fxml", 600, "Decompressor");
        }
    }

    @FXML
    protected void GotoNotes() {
        if (canSwitchFrames()) {
            loadView("/com/example/studentutilitysoftware/Notes.fxml", 875, "Notes");
        }
    }

    @FXML
    protected void GotoExpenses() {
        if (canSwitchFrames()) {
            loadView("/com/example/studentutilitysoftware/Expenses.fxml", 755, "Expenses");
        }
    }

    @FXML
    protected void Logout() throws IOException {
        if (canSwitchFrames()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Logout Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to logout?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader(Home.class.getResource("Home.fxml"));
                        Scene scene = new Scene(fxmlLoader.load(), 1105, 700);
                        Stage stage = (Stage) Logoutbtn.getScene().getWindow();
                        stage.setMaxWidth(1105);
                        stage.setMinWidth(1105);
                        stage.setTitle("WELCOME!");
                        stage.setScene(scene);
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

//    private void loadView(String fxmlPath, int viewWidth, String viewName) {
//
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
//            layout.setRight(loader.load());
//            layout.setMinWidth(viewWidth);
//            layout.setMaxWidth(viewWidth);
//
//            if (viewName.equals("Expenses")) {
//                ExpenseController expenseController = loader.getController();
//                expenseController.setUser(this.User);
//                currentExpenseController = expenseController;
//            }else if(viewName.equals("DashBoard")){
//                DashBoardController dashBoardController = loader.getController();
//                dashBoardController.setUser(this.User);
//            }else if(viewName.equals("Notes")){
//                NotesController notesController = loader.getController();
//                notesController.setUser(this.User);
//            }
//
//            Stage stage = (Stage) layout.getScene().getWindow();
//            if(viewName.equals("Expenses")){
//                stage.setMinHeight(737);
//                SideBarPane.setPrefHeight(737);
//            }else if(viewName.equals("DashBoard")){
//                stage.setMinHeight(788);
//                SideBarPane.setPrefHeight(788);
//            }
//            stage.setMinWidth(252 + viewWidth);
//            stage.setMaxWidth(252 + viewWidth);
//            SlideInLeft S = new SlideInLeft(layout.getRight().getScene().getRoot());
//            S.setSpeed(0.1);
//            S.play();
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.err.println("Error loading view: " + fxmlPath);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

    private void loadView(String fxmlPath, int viewWidth, String viewName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node rightPane = loader.load();

            // Set the right pane in the BorderPane but make it initially invisible
            layout.setRight(rightPane);
            layout.setMinWidth(viewWidth);
            layout.setMaxWidth(viewWidth);

            // Ensure the RightPane starts off-screen
            rightPane.setTranslateX(layout.getWidth() + viewWidth);

            // Set controller-specific logic
            if (viewName.equals("Expenses")) {
                ExpenseController expenseController = loader.getController();
                expenseController.setUser(this.User);
                currentExpenseController = expenseController;
            } else if (viewName.equals("DashBoard")) {
                DashBoardController dashBoardController = loader.getController();
                dashBoardController.setUser(this.User);
            } else if (viewName.equals("Notes")) {
                NotesController notesController = loader.getController();
                notesController.setUser(this.User);
            }

            // Adjust stage dimensions
            Stage stage = (Stage) layout.getScene().getWindow();
            if (viewName.equals("Expenses")) {
                stage.setMinHeight(737);
                SideBarPane.setPrefHeight(737);
            } else if (viewName.equals("DashBoard")) {
                stage.setMinHeight(788);
                SideBarPane.setPrefHeight(788);
            }
            stage.setMinWidth(252 + viewWidth);
            stage.setMaxWidth(252 + viewWidth);

            // Apply SlideInLeft animation, ensuring it moves from off-screen
            SlideInLeft animation = new SlideInLeft(rightPane);
            animation.setSpeed(0.5); // Adjust speed if necessary
            animation.play();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading view: " + fxmlPath);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    private boolean canSwitchFrames() {
        if (currentExpenseController != null && currentExpenseController.hasUnsavedExpenses()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Unsaved Expenses");
            alert.setHeaderText("You have unsaved expenses.");
            alert.setContentText("Do you want to save them before switching?");

            ButtonType saveButton = new ButtonType("Save");
            ButtonType discardButton = new ButtonType("Discard");
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(saveButton, discardButton, cancelButton);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == saveButton) {
                    currentExpenseController.saveNewExpenses();
                    return true;
                } else if (result.get() == discardButton) {
                    currentExpenseController.clearExpenses();
                    return true;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    public void setUser(String username) {
        this.User = username;
        Intialize(username);
        loadProfileImage(username);
    }

    private void loadProfileImage(String username) {
        try {
            Connection connection = DatabaseConnection.getConnection();
            String query = "SELECT profile_picture FROM users WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String imagePath = resultSet.getString("profile_picture");

                if (imagePath != null && !imagePath.isEmpty()) {
                    FileInputStream fileInputStream = new FileInputStream(imagePath);
                    Image profileImage = new Image(fileInputStream);
                    applyCircularImage(profileImage);
                } else {
                    System.out.println("No profile picture found for user: " + username);
                }
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load profile picture: " + e.getMessage());
        }
    }

    private void applyCircularImage(Image profileImage) {
        ImageView imageView = new ImageView(profileImage);

        imageView.setFitWidth(200);
        imageView.setFitHeight(150);
        imageView.setLayoutX(50);
        imageView.setLayoutY(40);

        Circle circleMask = new Circle(125, 100, 76);
        circleMask.setFill(Color.WHITE);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(imageView);

        stackPane.setClip(circleMask);

        somePane.getChildren().add(stackPane);
    }



    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

}
