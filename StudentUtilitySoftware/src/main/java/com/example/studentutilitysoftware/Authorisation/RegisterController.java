package com.example.studentutilitysoftware.Authorisation;

import com.example.studentutilitysoftware.DataBase.DatabaseConnection;
import com.example.studentutilitysoftware.Home;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class RegisterController {
    @FXML
    private Button Registerbtn;

    @FXML
    private TextField FullUserNametf;

    @FXML
    private PasswordField Passwordpf;

    @FXML
    private TextField UserNametf;

    @FXML
    protected void RegisterUser() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("ADD A PROFILE PICTURE");
        alert.setHeaderText("OPTIONAL");
        alert.setContentText("DO YOU WISH TO ADD A PROFILE PICTURE ?");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        final String[] profilePicturePath = {null};

        alert.showAndWait().ifPresent(response -> {
            System.out.println("Alert response: " + response);
            if (response == ButtonType.YES) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choose Profile Picture");
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

                System.out.println("Opening FileChooser...");

                File selectedFile = fileChooser.showOpenDialog(Registerbtn.getScene().getWindow());

                if (selectedFile == null) {
                    System.out.println("No file selected.");
                } else {
                    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                    profilePicturePath[0] = selectedFile.getAbsolutePath();

                    try {
                        FileInputStream inputStream = new FileInputStream(selectedFile);
                        Image fxImage = new Image(inputStream);
                        ImageView imageView = createCircularImageView(fxImage);

                        StackPane previewPane = new StackPane(imageView);
                        Alert previewAlert = new Alert(Alert.AlertType.INFORMATION);
                        previewAlert.setHeaderText("Profile Picture Preview");
                        previewAlert.getDialogPane().setContent(previewPane);
                        previewAlert.showAndWait();

                    } catch (FileNotFoundException e) {
                        showAlert("Error", "File not found: " + e.getMessage());
                    }
                }
            }
        });

        String fullName = FullUserNametf.getText().trim();
        String userName = UserNametf.getText().trim();
        String password = Passwordpf.getText().trim();
        password = BCrypt.hashpw(password,BCrypt.gensalt());

        if (fullName.isEmpty() || userName.isEmpty() || password.isEmpty()) {
            showAlert("Error", "All fields must be filled out.");
            return;
        }

        try {
            Connection connection = DatabaseConnection.getConnection();
            String insertQuery = "INSERT INTO users (full_name, username, password, profile_picture) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, fullName);
            preparedStatement.setString(2, userName);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, profilePicturePath[0]);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Success", "User registered successfully!");
                clearFields();
            } else {
                showAlert("Error", "Failed to register user. Please try again.");
            }

            preparedStatement.close();
            connection.close();

            FXMLLoader fxmlLoader = new FXMLLoader(Home.class.getResource("Home.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1105, 700);
            Stage stage = (Stage) Registerbtn.getScene().getWindow();
            stage.setTitle("LOGIN FORM!");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Database connection failed: " + e.getMessage());
        }
    }


    private ImageView createCircularImageView(Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(200);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        imageView.setClip(new Circle(100, 75, Math.min(100, 75)));
        return imageView;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        FullUserNametf.clear();
        UserNametf.clear();
        Passwordpf.clear();
    }
}
