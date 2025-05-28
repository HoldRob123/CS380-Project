package org.example;

import java.sql.*;

public class UserCreate {

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://10.10.10.64:3306/vin_decoder_db";
    private static final String DB_USERNAME = "GEN_USE";
    private static final String DB_PASSWORD = "pass1";

    /**
     * Adds a new user to the 'users' table if the username is unique.
     *
     * @param username The username of the user.
     * @param password The password of the user (store securely in production).
     */
    public static void createUser(String username, String password) {
        String checkSql = "SELECT COUNT(*) FROM users WHERE userName = ?";
        String insertSql = "INSERT INTO users (userName, userPassword) VALUES (?, ?)";

        try (Connection connect = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {

            // Check for existing username
            try (PreparedStatement checkStmt = connect.prepareStatement(checkSql)) {
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();
                rs.next();

                if (rs.getInt(1) > 0) {
                    System.out.println("Username already exists. Choose a different one.");
                    return;
                }
            }

            // Insert new user
            try (PreparedStatement insertStmt = connect.prepareStatement(insertSql)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, password); // You should hash passwords in real systems
                int rows = insertStmt.executeUpdate();

                if (rows > 0) {
                    System.out.println("User added successfully!");
                } else {
                    System.out.println("Something went wrong. No user was added.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Database error occurred:");
            e.printStackTrace();
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        createUser("VodkaNoodle", "litPass123");
    }
}

