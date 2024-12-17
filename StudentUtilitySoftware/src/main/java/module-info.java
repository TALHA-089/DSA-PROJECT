module com.example.studentutilitysoftware {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.pdfbox;
    requires com.google.gson;
    requires java.sql;
    requires java.desktop;
    requires jBCrypt;
    requires animatefx;


    opens com.example.studentutilitysoftware to javafx.fxml;
    exports com.example.studentutilitysoftware;
    opens com.example.studentutilitysoftware.Models;
    exports com.example.studentutilitysoftware.Models;
    exports com.example.studentutilitysoftware.CompressFeature;
    opens com.example.studentutilitysoftware.CompressFeature to javafx.fxml;
    exports com.example.studentutilitysoftware.Authorisation;
    opens com.example.studentutilitysoftware.Authorisation to javafx.fxml;
    exports com.example.studentutilitysoftware.DataBase;
    opens com.example.studentutilitysoftware.DataBase to javafx.fxml;
    exports com.example.studentutilitysoftware.RemainingControllers;
    opens com.example.studentutilitysoftware.RemainingControllers to javafx.fxml;
    opens com.example.studentutilitysoftware.DecompresFeature;
    exports com.example.studentutilitysoftware.DecompresFeature;
    opens com.example.studentutilitysoftware.NotesFeature;
    exports com.example.studentutilitysoftware.NotesFeature;
    opens com.example.studentutilitysoftware.ExpenseFeature;
    exports com.example.studentutilitysoftware.ExpenseFeature;
    opens com.example.studentutilitysoftware.Models.LinkedList;
    exports com.example.studentutilitysoftware.Models.LinkedList;
    exports com.example.studentutilitysoftware.Models.PriorityQueue;
    opens com.example.studentutilitysoftware.Models.PriorityQueue;
    exports com.example.studentutilitysoftware.Models.Trees;
    opens com.example.studentutilitysoftware.Models.Trees;
    exports com.example.studentutilitysoftware.Models.Stack;
    opens com.example.studentutilitysoftware.Models.Stack;
    exports com.example.studentutilitysoftware.DashBoard;
    opens com.example.studentutilitysoftware.DashBoard to javafx.fxml;
}