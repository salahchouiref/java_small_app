module com.example.learn_javafxcrud {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.dotenv;


    opens com.example.controller to javafx.fxml;
    exports com.example.controller;
    exports com.example.data;
    exports com.example.model;
}
