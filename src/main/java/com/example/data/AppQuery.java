package com.example.data;

import com.example.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AppQuery {

    // Inserts a new student record into the database
    public static void create(Student student) {
        try (Connection connection = DbConnection.connect()) {
            String sql = "INSERT INTO students(first_name, middle_name, last_name) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, student.getFirst_name());
                preparedStatement.setString(2, student.getMiddle_name());
                preparedStatement.setString(3, student.getLast_name());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Retrieves a student's name from the database by ID
    public static Student read(int id) {
        try (Connection connection = DbConnection.connect()) {
            String sql = "SELECT first_name, middle_name, last_name FROM students WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, id);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String firstName = resultSet.getString("first_name");
                        String middleName = resultSet.getString("middle_name");
                        String lastName = resultSet.getString("last_name");
                        return new Student(id, firstName, middleName, lastName);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Updates a student's record in the database
    public static void update(Student student) {
        try (Connection connection = DbConnection.connect()) {
            String sql = "UPDATE students SET first_name = ?, middle_name = ?, last_name = ? WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, student.getFirst_name());
                preparedStatement.setString(2, student.getMiddle_name()!=null ? student.getMiddle_name() : null);
                preparedStatement.setString(3, student.getLast_name());
                preparedStatement.setInt(4, student.getId());
                preparedStatement.executeUpdate();
                preparedStatement.close();
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Deletes a student's record from the database
    public static void delete(int id) {
        try (Connection connection = DbConnection.connect()) {
            String sql = "DELETE FROM students WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Student> search(String query) {
        List<Student> result = new ArrayList<>();

        try (Connection connection = DbConnection.connect()) {
            String[] nameParts = query.split("\\s+");

            // Build the SQL query dynamically based on the number of name parts
            StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM students WHERE ");
            for (int i = 0; i < nameParts.length; i++) {
                if (i > 0) {
                    sqlBuilder.append("AND ");
                }
                sqlBuilder.append("(first_name LIKE ? OR middle_name LIKE ? OR last_name LIKE ?) ");
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlBuilder.toString())) {
                for (int i = 0; i < nameParts.length; i++) {
                    String likePattern = "%" + nameParts[i] + "%";
                    preparedStatement.setString(i * 3 + 1, likePattern);
                    preparedStatement.setString(i * 3 + 2, likePattern);
                    preparedStatement.setString(i * 3 + 3, likePattern);
                }

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String firstName = resultSet.getString("first_name");
                        String middleName = resultSet.getString("middle_name");
                        String lastName = resultSet.getString("last_name");

                        Student student = new Student(id, firstName, middleName, lastName);
                        result.add(student);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception according to your application's needs
        }

        return result;
    }

    // Retrieves all students from the database
    public static List<Student> getAll() {
        List<Student> allStudents = new ArrayList<>();
        try (Connection connection = DbConnection.connect()) {
            String sql = "SELECT id, first_name, middle_name, last_name FROM students";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int studentId = resultSet.getInt("id");
                        String firstName = resultSet.getString("first_name");
                        String middleName = resultSet.getString("middle_name");
                        String lastName = resultSet.getString("last_name");
                        allStudents.add(new Student(studentId, firstName, middleName, lastName));
                    }
                }
                    preparedStatement.close();
                    connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allStudents;
    }
}
