package org.example;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;
import java.util.Scanner;

public class VehicleInserter {

    public static void insertVehicleFromVin(String vin, String userId) {
        String url = "https://vpic.nhtsa.dot.gov/api/vehicles/DecodeVinValues/" + vin + "?format=json";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            // Create temporary vehicle object
            Vehicle tempVehicle = new Vehicle(
                    vin,
                    extractDetail(body, "Make"),
                    extractDetail(body, "Model"),
                    extractDetail(body, "ModelYear"),
                    null
            );

            // Display preview
            System.out.println("\nVehicle Preview:");
            System.out.println("Year: " + tempVehicle.getYear());
            System.out.println("Make: " + tempVehicle.getMake());
            System.out.println("Model: " + tempVehicle.getModel());

            // Ask user if they want to save
            Scanner scanner = new Scanner(System.in);
            System.out.print("Do you want to save this vehicle to the database? (yes/no): ");
            String choice = scanner.nextLine().trim().toLowerCase();

            if (!choice.equals("yes")) {
                System.out.println(" Vehicle not saved.");
                return;
            }

            // DB credentials
            String dbUrl = "jdbc:mysql://10.10.10.64:3306/vin_decoder_db";
            String dbUsername = "GEN_USE";
            String dbPassword = "pass1";

            try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {

                // Check for duplicate vehicle saved by the same user
                String checkSql = "SELECT COUNT(*) FROM vehicles WHERE VIN_NUMBER = ? AND userID = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setString(1, vin);
                    checkStmt.setString(2, userId);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("This vehicle has already been saved by this user.");
                        return;
                    }
                }

                // Insert new vehicle
                String insertSql = "INSERT INTO vehicles (" +
                        "VIN_NUMBER, V_make, V_model, V_year, V_trim, V_type, V_bodyClass, " +
                        "V_doors, V_fuelType, V_DriveType, V_EngineModel, V_CylinderNum, " +
                        "V_EngineDisplace, V_TransStyle, V_TransSpeeds, " +
                        "V_PlantCountry, V_Manufacturer, V_GVWR, V_rows, V_seats, userID" +
                        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setString(1, vin);
                    pstmt.setString(2, tempVehicle.getMake());
                    pstmt.setString(3, tempVehicle.getModel());
                    pstmt.setString(4, tempVehicle.getYear());
                    pstmt.setString(5, extractDetail(body, "Trim"));
                    pstmt.setString(6, extractDetail(body, "VehicleType"));
                    pstmt.setString(7, extractDetail(body, "BodyClass"));
                    pstmt.setInt(8, parseIntSafe(extractDetail(body, "Doors")));
                    pstmt.setString(9, extractDetail(body, "FuelTypePrimary"));
                    pstmt.setString(10, extractDetail(body, "DriveType"));
                    pstmt.setString(11, extractDetail(body, "EngineModel"));
                    pstmt.setInt(12, parseIntSafe(extractDetail(body, "EngineCylinders")));
                    pstmt.setBigDecimal(13, parseBigDecimalSafe(extractDetail(body, "DisplacementL")));
                    pstmt.setString(14, extractDetail(body, "TransmissionStyle"));
                    pstmt.setInt(15, parseIntSafe(extractDetail(body, "TransmissionSpeeds")));
                    pstmt.setString(16, extractDetail(body, "PlantCountry"));
                    pstmt.setString(17, extractDetail(body, "Manufacturer"));
                    pstmt.setString(18, extractDetail(body, "GVWR"));
                    pstmt.setInt(19, parseIntSafe(extractDetail(body, "SeatRows")));
                    pstmt.setInt(20, parseIntSafe(extractDetail(body, "Seats")));
                    pstmt.setString(21, userId);

                    pstmt.executeUpdate();
                    System.out.println("Vehicle successfully saved to the database.");
                }

            }

        } catch (IOException | InterruptedException | SQLException e) {
            e.printStackTrace();
        }
    }



    // TEMP vehicle class
    static class Vehicle {
        private String vin, make, model, year, nickname;

        public Vehicle(String vin, String make, String model, String year, String nickname) {
            this.vin = vin;
            this.make = make;
            this.model = model;
            this.year = year;
            this.nickname = nickname;
        }

        public String getVin() {
            return vin;
        }

        public String getMake() {
            return make;
        }

        public String getModel() {
            return model;
        }

        public String getYear() {
            return year;
        }
    }

    public static String extractDetail(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int index = json.indexOf(searchKey);
        if (index == -1) return null;
        index = json.indexOf(":", index) + 1;
        while (json.charAt(index) == ' ' || json.charAt(index) == '"') index++;
        int end = json.indexOf("\"", index);
        if (end == -1) end = json.indexOf(",", index);
        String value = json.substring(index, end).replace("\\", "").trim();
        return value.isEmpty() ? null : value;
    }

    public static int parseIntSafe(String value) {
        try {
            return value != null ? Integer.parseInt(value) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static BigDecimal parseBigDecimalSafe(String value) {
        try {
            return value != null ? new BigDecimal(value).setScale(1, RoundingMode.HALF_UP) : null;
        } catch (NumberFormatException e) {
            return null;
        }

    }
    public static void main(String[] args) {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter your username: ");
            String username = scanner.nextLine().trim();

            String dbUrl = "jdbc:mysql://10.10.10.64:3306/vin_decoder_db";
            String dbUsername = "GEN_USE";
            String dbPassword = "pass1";

            String userId = null;

            try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
                System.out.println("yeeters");
                String userQuery = "SELECT userID FROM users WHERE username = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(userQuery)) {
                    pstmt.setString(1, username);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        userId = rs.getString("userID");
                        System.out.println(" User found. userID: " + userId);
                    } else {
                        System.out.println(" Username not found. Exiting.");
                        return;
                    }
                }
            } catch (SQLException e) {
                System.out.println(" Failed to connect to database or retrieve user.");
                e.printStackTrace();
                return;
            }

            System.out.print("Enter VIN to decode and save: ");
            String vin = scanner.nextLine().trim();

            insertVehicleFromVin(vin, userId);
        }

    }

