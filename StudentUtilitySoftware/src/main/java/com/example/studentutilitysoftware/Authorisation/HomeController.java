package com.example.studentutilitysoftware.Authorisation;


import com.example.studentutilitysoftware.DashBoard.DashBoardController;
import com.example.studentutilitysoftware.DataBase.DatabaseConnection;
import com.example.studentutilitysoftware.Home;
import com.example.studentutilitysoftware.RemainingControllers.SideBarController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HomeController {

    @FXML
    private Button Loginbtn;
    @FXML
    private Hyperlink RegisterLink;
    @FXML
    private TextField Usernametf;
    @FXML
    private TextField Passwordpf;

    @FXML
    protected void login() {
        String username = Usernametf.getText().trim();
        String password = Passwordpf.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Username and Password must not be empty!", "Empty Fields");
            return;
        }

        try {
            Connection connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM users WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String storedHash = resultSet.getString("password");
                if (BCrypt.checkpw(password, storedHash)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Login Successful", "Welcome back!");
                    navigateToDashboard(username);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid username or password!","Try Again");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid username or password!", "Try Again");
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Database connection failed: " + e.getMessage(), "Try Again!");
        }
    }


    @FXML
    protected void GotoRegisterPage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Home.class.getResource("Register.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1105, 700);
        Stage stage = (Stage) RegisterLink.getScene().getWindow();
        stage.setTitle("REGISTER FORM!");
        stage.setScene(scene);
        stage.show();
    }

    private void navigateToDashboard(String Username) throws IOException, SQLException {
        BorderPane borderPane = new BorderPane();
        FXMLLoader sidebarLoader = new FXMLLoader(Home.class.getResource("DashboardSideBar.fxml"));
        borderPane.setLeft(sidebarLoader.load());

        SideBarController sidebarController = sidebarLoader.getController();
        sidebarController.setLayout(borderPane);
        sidebarController.setUser(Username);
        sidebarLoader.setController(sidebarController);
        FXMLLoader dashboardLoader = new FXMLLoader(Home.class.getResource("Dashboard.fxml"));
        borderPane.setRight(dashboardLoader.load());
        DashBoardController dashBoardController = dashboardLoader.getController();
        dashBoardController.setUser(this.Usernametf.getText());

        Scene scene = new Scene(borderPane, 1135, 788);


        Stage stage = (Stage) Loginbtn.getScene().getWindow();
        stage.setTitle("Dashboard");
        stage.setScene(scene);
        stage.show();
    }



    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

}