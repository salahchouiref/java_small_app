package com.example.data;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    private static String JDBC_URL = "jdbc:mysql://localhost:3306/students_db";
    private static String USERNAME = "root";
    private static String PASSWORD = "";

    static {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.load();

        // Update JDBC_URL, USERNAME, and PASSWORD with values from .env or use default values
        JDBC_URL = dotenv.get("DB_URL", JDBC_URL);
        USERNAME = dotenv.get("DB_USER", USERNAME);
        PASSWORD = dotenv.get("DB_PASSWORD", PASSWORD);
    }

    // Establishes a database connection
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }

    public static void main(String[] args) {
        // You can perform some testing or initialization here if needed
        try (Connection connection = connect()) {
            System.out.println("Connected to the database!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
