package com.example.controller;

import com.example.data.AppQuery;
import com.example.model.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;


import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class StudentController implements Initializable {

    @FXML
    private Student student;

    @FXML
    public TextField fieldFirstName;
    @FXML
    public TextField fieldMiddleName;
    @FXML
    public TextField fieldLastName;
    @FXML
    public TextField searchField;

    @FXML
    public Button btnNew;
    @FXML
    public Button btnSave;
    @FXML
    public Button btnUpdate;
    @FXML
    public Button btnDelete;

    @FXML
    private TableView<Student> studentsTable;
    @FXML
    private TableColumn <Student, Integer> colId;
    @FXML
    private TableColumn<Student, String> colFirstName;
    @FXML
    private TableColumn<Student, String> colMiddleName;
    @FXML
    private TableColumn<Student, String> colLastName;

    @FXML
    private Label errorLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Load all students into the TableView
        loadStudents();
    }

    @FXML
    private void addStudent() {
        String firstName = fieldFirstName.getText().trim();
        String middleName = fieldMiddleName.getText().trim();
        String lastName = fieldLastName.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            // Display an error message for required fields
            showError("First Name and Last Name are required fields.");
            return;
        }

        Student student = new Student(
                firstName,
                middleName,
                lastName
        );
        AppQuery.create(student);
        loadStudents();
    }

    private void showError(String errorMessage) {
        // Set the error message and make the Label visible
        errorLabel.setText(errorMessage);
        errorLabel.setVisible(true);
    }

    private void clearError() {
        // Clear the error message and make the Label invisible
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }


    @FXML
    private void loadStudents() {
        // Get all students from the database
        List<Student> allStudents = AppQuery.getAll();

        // Create an ObservableList from the list of students
        ObservableList<Student> observableStudents = FXCollections.observableArrayList(allStudents);

        // Initialize TableView columns
        colId.setCellValueFactory(new PropertyValueFactory<Student,Integer>("id"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<Student,String>("first_name"));
        colMiddleName.setCellValueFactory(new PropertyValueFactory<Student,String>("middle_name"));
        colLastName.setCellValueFactory(new PropertyValueFactory<Student,String>("last_name"));

        // Set the ObservableList as the data source for the TableView
        studentsTable.setItems(observableStudents);
        setNew();
    }

    @FXML
    public void mouseClicked(MouseEvent e){
        try{
            clearError();
            Student s = studentsTable.getSelectionModel().getSelectedItem();
            this.student = new Student(s.getId(),s.getFirst_name(),s.getMiddle_name(),s.getLast_name());
            fieldFirstName.setText(this.student.getFirst_name());
            fieldMiddleName.setText(this.student.getMiddle_name());
            fieldLastName.setText(this.student.getLast_name());
            btnUpdate.setDisable(false);
            btnDelete.setDisable(false);
            btnSave.setDisable(true);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @FXML
    public void updateStudent(){
        String firstName = fieldFirstName.getText().trim();
        String middleName = fieldMiddleName.getText().trim();
        String lastName = fieldLastName.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            // Display an error message for required fields
            showError("First Name and Last Name are required fields.");
            return;
        }

        this.student.setFirst_name(firstName);
        this.student.setMiddle_name(middleName);
        this.student.setLast_name(lastName);

        AppQuery.update(this.student);
        loadStudents();
    }

    public void deleteStudent(){
        clearError();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation delete");
        alert.setHeaderText("Delete Student");
        alert.setContentText("Are you sure you want to delete the student ?");

        // Show the dialog and wait for the user's response
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // User clicked OK, proceed with deletion
                AppQuery.delete(this.student.getId());
                loadStudents(); // Refresh the TableView
            }

        });
    }

    @FXML
    private void searchStudents() {
        clearError();
        String searchText = searchField.getText().trim().toLowerCase();

        if (searchText.isEmpty()) {
            // If the search text is empty, reload all students
            loadStudents();
        } else {
            // Filter the students based on the search text
            List<Student> filteredStudents = AppQuery.search(searchText);

            // Create an ObservableList from the filtered list
            ObservableList<Student> observableStudents = FXCollections.observableArrayList(filteredStudents);

            // Set the ObservableList as the data source for the TableView
            studentsTable.setItems(observableStudents);

            // Clear the selection
            studentsTable.getSelectionModel().clearSelection();
            clearFields();
            setNew();
        }
    }

    @FXML
    public void setNew(){
        btnSave.setDisable(false);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        clearFields();
        clearError();
    }

    @FXML
    private void clearFields() {
        fieldFirstName.clear();
        fieldMiddleName.clear();
        fieldLastName.clear();
    }
}
