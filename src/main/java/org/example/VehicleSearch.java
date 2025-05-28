package org.example;

import java.sql.*;
import java.util.*;

public class VehicleSearch {

    public static List<Vehicle> searchVehicles(
            String vin, String make, String model, String year,
            String nickname, boolean isSavedOnly, String username
    ) {
        List<Vehicle> vehicleList = new ArrayList<>();

        // DB credentials
        String dbUrl = "jdbc:mysql://10.10.2.168:3306/vin_decoder_db";
        String dbUsername = "GEN_USE";
        String dbPassword = "pass1";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            Integer userId = null;

            if (isSavedOnly) {
                // 🔍 Get userId from username
                String userIdQuery = "SELECT userID FROM users WHERE userName = ?";
                try (PreparedStatement userStmt = conn.prepareStatement(userIdQuery)) {
                    userStmt.setString(1, username);
                    ResultSet userRs = userStmt.executeQuery();
                    if (userRs.next()) {
                        userId = userRs.getInt("userID");
                    } else {
                        System.out.println(" No user found with that username.");
                        return vehicleList; // empty list
                    }
                }
            }

            StringBuilder sql = new StringBuilder("SELECT v.* FROM vehicles v");
            List<Object> params = new ArrayList<>();

            if (isSavedOnly) {
                sql.append(" JOIN saved_vehicles sv ON v.VIN_NUMBER = sv.vin_number");
                sql.append(" JOIN users u ON sv.user_id = u.userID");
                sql.append(" WHERE u.userID = ?");
                params.add(userId);
            } else {
                sql.append(" WHERE 1=1");
            }

            if (vin != null) {
                sql.append(" AND v.VIN_NUMBER = ?");
                params.add(vin);
            }
            if (make != null) {
                sql.append(" AND v.V_make LIKE ?");
                params.add("%" + make + "%");
            }
            if (model != null) {
                sql.append(" AND v.V_model LIKE ?");
                params.add("%" + model + "%");
            }
            if (year != null) {
                sql.append(" AND v.V_year = ?");
                params.add(year);
            }
            if (nickname != null) {
                sql.append(" AND v.nickname LIKE ?");
                params.add("%" + nickname + "%");
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    pstmt.setObject(i + 1, params.get(i));
                }

                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    Vehicle vehicle = new Vehicle(
                            rs.getString("VIN_NUMBER"),
                            rs.getString("V_make"),
                            rs.getString("V_model"),
                            rs.getString("V_year"),
                            rs.getString("nickname")
                    );
                    vehicleList.add(vehicle);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vehicleList;
    }
}

