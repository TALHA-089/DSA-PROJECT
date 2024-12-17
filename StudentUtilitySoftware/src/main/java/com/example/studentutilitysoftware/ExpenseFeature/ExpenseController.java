package com.example.studentutilitysoftware.ExpenseFeature;

import com.example.studentutilitysoftware.DataBase.DatabaseConnection;
import com.example.studentutilitysoftware.Models.Expense;
import com.example.studentutilitysoftware.Models.LinkedList.LinkedList;
import com.example.studentutilitysoftware.Models.PriorityQueue.PriorityQueue;
import com.example.studentutilitysoftware.Models.Stack.NewStack;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.application.Platform;


import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseController {

    private String User;
    @FXML
    private Button Searchbtn;

    @FXML
    private DatePicker DateFrom;

    @FXML
    private DatePicker DateTo;

    @FXML
    private Button AddExpensebtn;

    @FXML
    private TextField Searchtf;

    @FXML
    private TextField newTitletf;

    @FXML
    private TextField newAmounttf;

    @FXML
    private DatePicker newDate;

    @FXML
    private TableView<Expense> ExpenseTable;

    @FXML
    private TableColumn<Expense, Integer> SrNoCol;

    @FXML
    private TableColumn<Expense, String> TitleCol;

    @FXML
    private TableColumn<Expense, Date> DateCol;

    @FXML
    private TableColumn<Expense, Float> AmountCol;

    @FXML
    private TableView<Expense> RecentExpenseTable;

    @FXML
    private TableColumn<Expense, String> RETitleCol;

    @FXML
    private TableColumn<Expense, Date> REDateCol;

    @FXML
    private TableColumn<Expense, Float> REAmountCol;

    @FXML
    private Label TotalLabel;

    private PriorityQueue<Expense> expensesQueue;
    private LinkedList<Expense> newExpensesList;
    private NewStack<Expense> recentExpensesStack;
    private float totalRecentExpenses = 0;

    private void Initialize() {
        expensesQueue = new PriorityQueue<>();
        newExpensesList = new LinkedList<>();
        recentExpensesStack = new NewStack<>();

        if (DateFrom.getValue() == null) DateFrom.setValue(LocalDate.now());
        if (DateTo.getValue() == null) DateTo.setValue(LocalDate.now());

        SrNoCol.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        TitleCol.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        DateCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDate()));
        AmountCol.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());

        RETitleCol.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        REDateCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDate()));
        REAmountCol.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());

        loadExpenses();
        updateTotalLabel();
        updateRecentExpenseTable();
    }

    private void loadExpenses() {
        List<Expense> allExpenses = loadAllExpensesFromDatabase();
        for (Expense expense : allExpenses) {
            expensesQueue.addExpense(expense);
        }
        updateExpenseTable();
    }

    private List<Expense> loadAllExpensesFromDatabase() {
        List<Expense> expenses = new ArrayList<>();
        if (User == null || User.isEmpty()) {
            showAlert("Error", "User is not set. Cannot load expenses.");
            return expenses;
        }

        String createTableQuery = "CREATE TABLE IF NOT EXISTS expenses (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(255) NOT NULL, " +
                "title VARCHAR(255) NOT NULL, " +
                "date DATE NOT NULL, " +
                "amount FLOAT NOT NULL)";
        String selectQuery = "SELECT id, title, date, amount FROM expenses WHERE username = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            try (PreparedStatement createTableStmt = connection.prepareStatement(createTableQuery)) {
                createTableStmt.execute();
            }

            try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
                selectStmt.setString(1, User);

                try (ResultSet resultSet = selectStmt.executeQuery()) {
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String title = resultSet.getString("title");
                        Date date = resultSet.getDate("date");
                        float amount = resultSet.getFloat("amount");

                        Expense expense = new Expense(id, title, date, amount);
                        expenses.add(expense);
                    }
                }
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load expenses: " + e.getMessage());
            e.printStackTrace();
        }

        return expenses;
    }

    @FXML
    protected void AddExpense() {
        String title = newTitletf.getText().trim();
        String amountText = newAmounttf.getText().trim();
        LocalDate date = newDate.getValue();

        if (date == null) {
            date = LocalDate.now();
        }

        if (title.isEmpty() || amountText.isEmpty()) {
            showAlert("Error", "Title and Amount cannot be empty.");
            return;
        }

        float amount;
        try {
            amount = Float.parseFloat(amountText);
        } catch (NumberFormatException e) {
            showAlert("Error", "Amount must be a valid number.");
            return;
        }

        Date sqlDate = Date.valueOf(date);
        Expense newExpense = new Expense(0, title, sqlDate, amount);

        newExpensesList.insert(newExpense);
        recentExpensesStack.pushItem(newExpense);

        updateRecentExpenseTable();

        newTitletf.clear();
        newAmounttf.clear();
        newDate.setValue(LocalDate.now());
    }

    private void updateRecentExpenseTable() {
        java.util.LinkedList<Expense> recentExpensesStackData = recentExpensesStack.getStack();

        ObservableList<Expense> recentExpensesList = FXCollections.observableArrayList(recentExpensesStackData);

        RecentExpenseTable.setItems(recentExpensesList);
        totalRecentExpenses = 0;
        for(Expense e : recentExpensesList){
            totalRecentExpenses+= e.getAmount();
        }
        updateTotalLabel();
    }

    @FXML
    protected void UndoRecentlyAddedExpense() {

        if (recentExpensesStack.isEmpty()) {
            showAlert("Warning", "No recent expenses to undo.");
            return;
        }

        Expense lastExpense = recentExpensesStack.popItem();

        newExpensesList.delete(lastExpense);
        totalRecentExpenses -= lastExpense.getAmount();
        if (totalRecentExpenses < 0) totalRecentExpenses = 0;

        java.util.LinkedList<Expense> recentExpensesStackData = recentExpensesStack.getStack();
        ObservableList<Expense> updatedRecentExpenses = FXCollections.observableArrayList(recentExpensesStackData);
        RecentExpenseTable.setItems(updatedRecentExpenses);

        updateTotalLabel();
    }


    private void updateExpenseTable() {
        List<Expense> expensesList = expensesQueue.getAllExpenses();
        ObservableList<Expense> observableList = FXCollections.observableArrayList(expensesList);
        ExpenseTable.setItems(observableList);
    }

    private void updateTotalLabel() {
        TotalLabel.setText(String.valueOf(totalRecentExpenses));
    }

    public void saveNewExpenses() {
        if (User == null || User.isEmpty()) {
            showAlert("Error", "User is not set. Cannot save expenses.");
            return;
        }

        if(totalRecentExpenses == 0){
            showAlert("Error", "No Recent Expenses to Save");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String createTableQuery = "CREATE TABLE IF NOT EXISTS expenses (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(255) NOT NULL, " +
                    "title VARCHAR(255) NOT NULL, " +
                    "date DATE NOT NULL, " +
                    "amount FLOAT NOT NULL)";
            String insertExpenseQuery = "INSERT INTO expenses (username, title, date, amount) VALUES (?, ?, ?, ?)";

            try (PreparedStatement createTableStmt = connection.prepareStatement(createTableQuery)) {
                createTableStmt.execute();
            }

            try (PreparedStatement insertStmt = connection.prepareStatement(insertExpenseQuery)) {
                for (Expense expense : newExpensesList.getExpenses()) {
                    insertStmt.setString(1, User);
                    insertStmt.setString(2, expense.getTitle());
                    insertStmt.setDate(3, expense.getDate());
                    insertStmt.setFloat(4, expense.getAmount());
                    insertStmt.executeUpdate();
                }
            }

            newExpensesList.clear();
            showAlert("Success", "Expenses saved successfully!");
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to save expenses: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setUser(String user) {
        this.User = user;
        Initialize();
    }

    public void clearExpenses() {
        newExpensesList.clear();
    }

    public boolean hasUnsavedExpenses() {
        return !newExpensesList.isEmpty();
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    @FXML
    protected void FilterByTitle() {
        if (User == null || User.isEmpty()) {
            showAlert("Error", "User is not set. Cannot filter expenses.");
            return;
        }

        String searchTitle = Searchtf.getText().trim().toLowerCase();
        LocalDate fromDate = DateFrom.getValue();
        LocalDate toDate = DateTo.getValue();

        if (fromDate == null) {
            fromDate = LocalDate.now();
        }
        if (toDate == null) {
            toDate = LocalDate.now();
        }

        Date sqlFromDate = Date.valueOf(fromDate);
        Date sqlToDate = Date.valueOf(toDate);

        String query = "SELECT id, title, date, amount FROM expenses WHERE username = ? AND LOWER(title) LIKE ? AND date BETWEEN ? AND ?";

        List<Expense> filteredExpenses = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, User);
            statement.setString(2, "%" + searchTitle + "%");
            statement.setDate(3, sqlFromDate);
            statement.setDate(4, sqlToDate);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String title = resultSet.getString("title");
                    Date date = resultSet.getDate("date");
                    float amount = resultSet.getFloat("amount");

                    Expense expense = new Expense(id, title, date, amount);
                    filteredExpenses.add(expense);
                }
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to filter expenses: " + e.getMessage());
            e.printStackTrace();
        }

        ObservableList<Expense> filteredList = FXCollections.observableArrayList(filteredExpenses);
        ExpenseTable.setItems(filteredList);
    }

}
