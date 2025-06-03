/****************************************
 VIN DECODER - CS 380
 Isak Jacobson
 Saul Rodriguez-Tapia
 Holden Robinson

 Push Date: 6/2/25
 Push Number: 5
 Last Modified By: Isak
 *****************************************/

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;


public class VINDecoderMain {

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://10.10.15.3:3306/vin_decoder_db?useSSL=false&serverTimezone=UTC";;
    private static final String DB_USERNAME = "GEN_USE";
    private static final String DB_PASSWORD = "pass1";

    // User Credentials
    public String currentUser = "";

    // Facilitator method for main screen
    public void run() {
        // Launches Login Page
        SwingUtilities.invokeLater(() -> {
            LoginScreen loginScreen = new LoginScreen(this);
            loginScreen.setVisible(true);
        });
    }
    // logs in based on the credentials the user has entered
    // TODO (+SAUL) TEST DATABASE WITH THIS METHOD!
    public String tryLogin(String username, String password) {
        String result;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {

            // First: check if username exists
            String checkUserSql = "SELECT userPassword FROM users WHERE userName = ?";
            try (PreparedStatement checkUserStmt = conn.prepareStatement(checkUserSql)) {
                checkUserStmt.setString(1, username);
                ResultSet rs = checkUserStmt.executeQuery();

                if (rs.next()) {
                    // Username exists: now check password
                    String correctPassword = rs.getString("userPassword");
                    if (correctPassword.equals(password)) {
                        currentUser = username;
                        result = "Login Successful!";
                    } else {
                        result = "Someone with this username exists but the password is incorrect.";
                    }
                } else {
                    // Username does not exist: create new user
                    createUser(username, password);
                    currentUser = username;
                    result = "Account Created!";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result = "Database Connection Failed:\n" + e.getMessage();
        }

        return result;
    }



    // Helper method to tryLogin that will insert a new user entry in mySQL
    private static void createUser(String username, String password) {
        String checkSql = "SELECT COUNT(*) FROM users WHERE userName = ?";
        String insertSql = "INSERT INTO users (userName, userPassword) VALUES (?, ?)";

        try (Connection connect = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {

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



    // TODO (+HOLDEN): MAKE GUI AND WRITE LOGIC TO RETURN WHAT USER HAS TYPED IN SEARCH
    public String directSearch() {
        return null;
    }

    // TODO (+HOLDEN) (+SAUL): WRITE LOGIC TO SEARCH NHTSA AND MYSQL WITH THE GIVEN IDENTIFIER AND RETURN A LIST OF TEMP VEHICLE OBJECTS
    public ArrayList<Vehicle> confirmSearch(String identifier) {
        return null;
    }


    // TODO (+HOLDEN): MAKE GUI AND WRITE LOGIC TO TRANSPOSE THE FILTERS THE USER APPLIED IN GUI TO AN ARRAY LIST OF ATTRIBUTES
    public ArrayList<String> applyFilter() {
            ArrayList<String> filterList = new ArrayList<String>();
            return filterList;
    }

    // TODO (+HOLDEN) (+SAUL): WRITE LOGIC TO SEARCH FOR THE VEHICLE IN MYSQL AND NHTSA GIVEN AN ARRAY LIST OF ATTRIBUTES
    public ArrayList<Vehicle> confirmFilter(ArrayList<String> filterList) {

//        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
//            Integer userId = null;
//
//            if (isSavedOnly) {
//                // Get userId from username
//                String userIdQuery = "SELECT userID FROM users WHERE userName = ?";
//                try (PreparedStatement userStmt = conn.prepareStatement(userIdQuery)) {
//                    userStmt.setString(1, username);
//                    ResultSet userRs = userStmt.executeQuery();
//                    if (userRs.next()) {
//                        userId = userRs.getInt("userID");
//                    } else {
//                        System.out.println(" No user found with that username.");
//                        return vehicleList; // empty list
//                    }
//                }
//            }
//
//            StringBuilder sql = new StringBuilder("SELECT v.* FROM vehicles v");
//            List<Object> params = new ArrayList<>();
//
//            if (isSavedOnly) {
//                sql.append(" JOIN saved_vehicles sv ON v.VIN_NUMBER = sv.vin_number");
//                sql.append(" JOIN users u ON sv.user_id = u.userID");
//                sql.append(" WHERE u.userID = ?");
//                params.add(userId);
//            } else {
//                sql.append(" WHERE 1=1");
//            }
//
//            if (vin != null) {
//                sql.append(" AND v.VIN_NUMBER = ?");
//                params.add(vin);
//            }
//            if (make != null) {
//                sql.append(" AND v.V_make LIKE ?");
//                params.add("%" + make + "%");
//            }
//            if (model != null) {
//                sql.append(" AND v.V_model LIKE ?");
//                params.add("%" + model + "%");
//            }
//            if (year != null) {
//                sql.append(" AND v.V_year = ?");
//                params.add(year);
//            }
//            if (nickname != null) {
//                sql.append(" AND v.nickname LIKE ?");
//                params.add("%" + nickname + "%");
//            }
//
//            try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
//                for (int i = 0; i < params.size(); i++) {
//                    pstmt.setObject(i + 1, params.get(i));
//                }
//
//                ResultSet rs = pstmt.executeQuery();
//
//                while (rs.next()) {
//                    Vehicle vehicle = new Vehicle(
//                            rs.getString("VIN_NUMBER"),
//                            rs.getString("V_make"),
//                            rs.getString("V_model"),
//                            rs.getString("V_year"),
//                            rs.getString("nickname")
//                    );
//                    vehicleList.add(vehicle);
//                }
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return vehicleList;
        return null;
    }


    // TODO (+SAUL): WRITE LOGIC TO CONVERT A TEMP VEHICLE TO A PERM VEHICLE WITH NHTSA API CALL
    public Vehicle solidifyVehicle(Vehicle tempVehicle) {
        Vehicle permVehicle = new Vehicle(tempVehicle.getVIN(), tempVehicle.getNickname(), tempVehicle.getMake(),
                tempVehicle.getModel(), tempVehicle.getYear(), false, "trim", "vehicleType", "bodyClass", 0, "fuelTypePrimary",
                "driveType", "engineModel", 1, 2.0, "transmissionStyle", 3, "plantCountry", "manufacturer", "gvwr", 4,
                4);
        return permVehicle;
    }

    // TODO (+HOLDEN) (+SAUL): WRITE LOGIC TO SAVE VEHICLE OBJECT TO MYSQL
    public void saveVehicle(Vehicle vehicle) {

    }

    // TODO (+HOLDEN) (+SAUL): WRITE LOGIC TO REMOVE VEHICLE OBJECT FROM MYSQL
    public void removeVehicle(Vehicle vehicle) {

    }

    // TODO (+HOLDEN): WRITE GUI LOGIC TO OPEN A TEXT BOX AND TAKE USER INPUT FROM A RESULTING PROMPT BOX
    public void editName(Vehicle vehicle) {
        String newName = "new name";
        vehicle.setNickname(newName);
    }

    // TODO (+HOLDEN): GUI LOGIC FOR SWITCHING SCREENS
    public void useCompare(Vehicle vehicle) {
        VINDecoderCompare decoder = new VINDecoderCompare();
        decoder.run(vehicle);
    }

    public static void main(String[] args) {
        VINDecoderMain decoder = new VINDecoderMain();
        decoder.run();
    }
}
