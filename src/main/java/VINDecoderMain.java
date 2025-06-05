/****************************************
 VIN DECODER - CS 380
 Isak Jacobson
 Saul Rodriguez-Tapia
 Holden Robinson

 Push Date: 6/4/25
 Push Number: 9
 Last Modified By: Isak
 *****************************************/

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


public class VINDecoderMain {

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/vin_decoder_db?useSSL=false&serverTimezone=UTC";;
    private static final String DB_USERNAME = "GEN_USE";
    private static final String DB_PASSWORD = "pass1";

    // User Credentials
    public String currentUser = "";
    public int currentUserID = -1;

    // Fake Data
    private List<Vehicle> vehicleDatabase;

    // GUI References
    private LoginScreen loginScreen;
    private CompareView compareView;
    public MainView mainView;

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
        String insertSql = "INSERT INTO users (username, password) VALUES (?, ?)";

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

    public void onLoginSuccess() {
        if (loginScreen != null) {
            loginScreen.dispose();
            loginScreen = null;
        }
        mainView = new MainView(this);
        mainView.setVisible(true);
        performSearch();
    }

    // Fake data for testing; replace with DB or API calls later
//    private void fakeData() {
//        vehicleDatabase = new ArrayList<>();
//        vehicleDatabase.add(new Vehicle("934892HAD84R319573", "Project Car", "Honda", "Accord", 2002, true,
//                "EX", "Sedan", "Coupe", 4, "Gasoline", "FWD", "K24A4", 4, 2.4, "Automatic", 5,
//                "Japan", "Honda Motor Co", "3501-4000 lbs", 2, 5));
//        vehicleDatabase.add(new Vehicle("548622G0BU6381355", "Lucy", "Honda", "Civic", 1997, true,
//                "DX", "Sedan", "Sedan", 4, "Gasoline", "FWD", "D16Y7", 4, 1.6, "Manual", 5,
//                "USA", "Honda Mfg", "3001-3500 lbs", 2, 5));
//        vehicleDatabase.add(new Vehicle("9101TGG873HS22884", "", "Toyota", "Corolla", 2005, false,
//                "LE", "Sedan", "Sedan", 4, "Gasoline", "FWD", "1ZZ-FE", 4, 1.8, "Automatic", 4,
//                "USA", "Toyota Motor Corp", "3001-3500 lbs", 2, 5));
//    }

    // Perform search & filtering and update GUI results panel
    public void performSearch() {
        if (mainView == null) return;

        String query = mainView.getSearchField().getText().toLowerCase();

        // Grab all the filters
        Map<String, String> filters = new TreeMap<>(); // from GUI

        filters.put("year", mainView.getYearBox().getText().trim());
        filters.put("make", mainView.getMakeBox().getText().trim());
        filters.put("model", mainView.getModelBox().getText().trim());
        filters.put("country", mainView.getCountryBox().getText().trim());
        filters.put("fuel", mainView.getGasType().getSelectedItem().toString());


        JPanel resultPanel = mainView.getResultPanel();
        resultPanel.removeAll();

        List<Vehicle> results;

        if (!query.isEmpty()) {
            // User entered text -> Search by VIN or query string
            results = confirmSearch(query);  // This method should return List<Vehicle>
        } else {
            // No query -> Use filters
            results = confirmFilter(filters); // Already returns filtered list
        }

        // Step 3: Display results in GUI
        for (Vehicle v : results) {
            JPanel card = new JPanel(new BorderLayout());
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
            card.setBorder(BorderFactory.createLineBorder(Color.gray));

            JPanel info = new JPanel();
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.add(new JLabel(v.toString()));

            JButton options = new JButton("...");
            options.setPreferredSize(new Dimension(50, 30));
            options.addActionListener(e -> showVehicleOptions(v, options));

            card.add(info, BorderLayout.CENTER);
            card.add(options, BorderLayout.EAST);
            resultPanel.add(card);
        }

        resultPanel.revalidate();
        resultPanel.repaint();
    }


    // Filter logic matching the GUI filter fields
    private boolean matchesFilter(Vehicle v) {
        if (mainView == null) return true;

        String year = mainView.getYearBox().getText().trim();
        String make = mainView.getMakeBox().getText().trim();
        String model = mainView.getModelBox().getText().trim();
        String country = mainView.getCountryBox().getText().trim();
        String fuel = (String) mainView.getGasType().getSelectedItem();

        if (!year.isEmpty() && !String.valueOf(v.getYear()).equals(year)) return false;
        if (!make.isEmpty() && !v.getMake().equalsIgnoreCase(make)) return false;
        if (!model.isEmpty() && !v.getModel().equalsIgnoreCase(model)) return false;
        if (!country.isEmpty() && !v.getPlantCountry().equalsIgnoreCase(country)) return false;
        if (mainView.getSavedOnly().isSelected() && !v.getSaved()) return false;

        if (fuel != null && !fuel.isEmpty()) {
            if (v.getFuelTypePrimary() == null || !v.getFuelTypePrimary().equalsIgnoreCase(fuel)) {
                return false;
            }
        }
        return true;
    }

    // Popup menu when clicking vehicle options "..." button
    private void showVehicleOptions(Vehicle vehicle, Component invoker) {
        JPopupMenu menu = new JPopupMenu();

        if (vehicle.getNickname() != null && !vehicle.getNickname().isEmpty()) {
            JMenuItem edit = new JMenuItem("Edit Name");
            edit.addActionListener(e -> {
                String newName = JOptionPane.showInputDialog(mainView, "New nickname:", vehicle.getNickname());
                if (newName != null && !newName.trim().isEmpty()) {
                    vehicle.setNickname(newName.trim());
                    JOptionPane.showMessageDialog(mainView, "Nickname updated.");
                    performSearch();
                }
            });
            menu.add(edit);
        }

        JMenuItem info = new JMenuItem("Full Information");
        info.addActionListener(e -> JOptionPane.showMessageDialog(mainView, vehicle.fullDescription(), "Vehicle Info", JOptionPane.INFORMATION_MESSAGE));
        menu.add(info);

        JMenuItem compare = new JMenuItem("Compare Vehicle");
        compare.addActionListener(e -> openCompareView(vehicle));
        menu.add(compare);

        JMenuItem remove = new JMenuItem("Remove from Saved");
        remove.addActionListener(e -> {
            vehicle.setIsSaved(false);
            JOptionPane.showMessageDialog(mainView, "Removed from saved.");
            performSearch();
        });
        menu.add(remove);

        menu.show(invoker, invoker.getWidth() / 2, invoker.getHeight() / 2);
    }

    // Method to direct to CompareView GUI once "Compare Vehicle" is clicked
    public void openCompareView(Vehicle vehicle) {
        CompareView compareView = new CompareView(vehicle);
        compareView.setVisible(true);
        mainView.setVisible(false);  // hide the main view
        // Add a WindowListener to show mainView back when compareView is closed
        compareView.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                mainView.setVisible(true);  // show main view again when compareView closes
            }
        });
    }


    // TODO (+HOLDEN): MAKE GUI AND WRITE LOGIC TO RETURN WHAT USER HAS TYPED IN SEARCH
    public String directSearch() {
        return null;
    }

    // TODO (+HOLDEN) (+SAUL): WRITE LOGIC TO SEARCH NHTSA AND MYSQL WITH THE GIVEN IDENTIFIER AND RETURN A LIST OF TEMP VEHICLE OBJECTS
    public ArrayList<Vehicle> confirmSearch(String identifier) {
        ArrayList<Vehicle> vehicleList = new ArrayList<>();

        if (identifier.length() != 17) {
            System.out.println("Invalid VIN length. VIN must be exactly 17 characters.");
            return vehicleList;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {

            // Try to find vehicle in MySQL
            String sql = "SELECT VIN_NUMBER, nickname, V_make, V_model, V_year FROM vehicles WHERE VIN_NUMBER = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, identifier);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Vehicle vehicle = new Vehicle(
                            rs.getString("VIN_NUMBER"),
                            rs.getString("nickname"),
                            rs.getString("V_make"),
                            rs.getString("V_model"),
                            rs.getInt("V_year")
                    );
                    vehicle.setIsSaved(true);
                    vehicleList.add(vehicle);
                }
            }

            // If not found in MySQL, query NHTSA
            if (vehicleList.isEmpty()) {
                String url = "https://vpic.nhtsa.dot.gov/api/vehicles/DecodeVinValues/" + identifier + "?format=json";

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Accept", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    String responseBody = response.body();

                    // Manually extract values
                    String make = extractJsonValue(responseBody, "Make");
                    String model = extractJsonValue(responseBody, "Model");
                    String yearStr = extractJsonValue(responseBody, "ModelYear");

                    int year = 0;
                    try {
                        year = Integer.parseInt(yearStr);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid year format from API.");
                    }

                    Vehicle vehicle = new Vehicle(identifier, "", make, model, year);
                    vehicle.setIsSaved(false);
                    vehicleList.add(vehicle);
                } else {
                    System.out.println("NHTSA API call failed with status: " + response.statusCode());
                }
            }

        } catch (SQLException | IOException | InterruptedException e) {
            System.out.println("Error in confirmSearch:");
            e.printStackTrace();
        }

        return vehicleList;
    }

    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return "";

        startIndex += searchKey.length();
        int endIndex = json.indexOf("\"", startIndex);
        if (endIndex == -1) return "";

        return json.substring(startIndex, endIndex);
    }

    // TODO (+HOLDEN) (+SAUL): WRITE LOGIC TO SEARCH FOR THE VEHICLE IN MYSQL AND NHTSA GIVEN AN ARRAY LIST OF ATTRIBUTES
    public ArrayList<Vehicle> confirmFilter(Map<String, String> filterList) {
        // Only look at saved vehicles by user
        if (filterList.containsKey("savedOnly")) {
            return getSavedFilteredVehicles(filterList);
        } else {
            // Look at saved vehicles by user and unsaved vehicles
            ArrayList<Vehicle> results = getSavedFilteredVehicles(filterList);
            results.addAll(getFilteredNHTSAVehicles(filterList));
            return results;
        }
    }

    // Helper Method that grabs vehicles in mySQL that match the filters applied by the user
    private ArrayList<Vehicle> getSavedFilteredVehicles(Map<String, String> filterList) {
        ArrayList<Vehicle> results = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT v.* FROM vehicles v");
        List<Object> params = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {

            // If "savedOnly" is in the filter list, get the current user's ID
            if (filterList.containsKey("savedOnly")) {
                String userIdQuery = "SELECT userID FROM users WHERE userName = ?";
                try (PreparedStatement userStmt = conn.prepareStatement(userIdQuery)) {
                    userStmt.setString(1, currentUser);
                    ResultSet userRs = userStmt.executeQuery();
                    if (userRs.next()) {
                        currentUserID = userRs.getInt("userID");
                    } else {
                        System.out.println("No user found with that username.");
                        return results; // Return empty list
                    }
                }

                sql.append(" JOIN saved_vehicles sv ON v.VIN_NUMBER = sv.vin_number");
                sql.append(" JOIN users u ON sv.user_id = u.userID");
                sql.append(" WHERE u.userID = ?");
                params.add(currentUserID);
            } else {
                sql.append(" WHERE 1=1");
            }

            // Add filters if present
            if (filterList.containsKey("make")) {
                sql.append(" AND v.V_make LIKE ?");
                params.add("%" + filterList.get("make") + "%");
            }

            if (filterList.containsKey("model")) {
                sql.append(" AND v.V_model LIKE ?");
                params.add("%" + filterList.get("model") + "%");
            }

            if (filterList.containsKey("year")) {
                sql.append(" AND v.V_year = ?");
                params.add(filterList.get("year"));
            }

            // Executes SQL command based on filters applied
            try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    pstmt.setObject(i + 1, params.get(i));
                }

                ResultSet rs = pstmt.executeQuery();

                // Adds Vehicles found in mySQL to result listing
                while (rs.next()) {
                    Vehicle vehicle = new Vehicle(
                            rs.getString("VIN_NUMBER"),
                            rs.getString("Nickname"),
                            rs.getString("V_make"),
                            rs.getString("V_model"),
                            rs.getInt("V_year")
                    );
                    vehicle.setIsSaved(true);
                    results.add(vehicle);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainView,"Could not establish connection with NHTSA API!", "NHTSA Error!", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return results;
    }

    // Helper method that grabs vehicles that match the filters the user applied
    //TODO: Figure out a workaround to not getting vehicle VINs from this method of search
    private ArrayList<Vehicle> getFilteredNHTSAVehicles(Map<String, String> filterList) {
        ArrayList<Vehicle> results = new ArrayList<>();

        try {
            String make = filterList.getOrDefault("make", "").trim();
            String model = filterList.getOrDefault("model", "").trim();
            String sYear = filterList.getOrDefault("year", "").trim();

            // Validate required inputs
            if (make.isEmpty() || sYear.isEmpty()) {
                System.out.println("Skipping NHTSA API call: 'make' or 'year' is missing.");
                return results;
            }

            // Build the API URL
            String apiUrl = String.format(
                    "https://vpic.nhtsa.dot.gov/api/vehicles/GetModelsForMakeYear/make/%s/modelyear/%s?format=json",
                    URLEncoder.encode(make, "UTF-8"),
                    URLEncoder.encode(sYear, "UTF-8")
            );

            System.out.println("Calling NHTSA URL: " + apiUrl);

            // Send the request
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            // Read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            // Parse JSON
            JSONObject json = new JSONObject(content.toString());
            JSONArray resultsArray = json.getJSONArray("Results");
            int year = Integer.parseInt(sYear);

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject obj = resultsArray.getJSONObject(i);
                String nhtsaMake = obj.getString("Make_Name");
                String nhtsaModel = obj.getString("Model_Name");

                if (!model.isEmpty() && !nhtsaModel.toLowerCase().contains(model.toLowerCase())) {
                    continue;
                }
                Vehicle temp = new Vehicle("<VIN UNKNOWN>", "N/A", nhtsaMake, nhtsaModel, year);
                temp.setIsSaved(false);
                results.add(temp);
            }

        } catch (Exception e) {
            System.err.println("Failed to retrieve data from NHTSA: " + e.getMessage());
            e.printStackTrace();
        }

        return results;
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
    public void saveVehicle(Vehicle vehicle, int userId) {

        String insertSql = "INSERT INTO vehicles (VIN_NUMBER, userID, nickname, V_make, V_model, V_year, trim, vehicleType, bodyClass, doors, fuelTypePrimary, driveType, engineModel, engineCylinders, displacementL, transmissionStyle, transmissionSpeed, plantCountry, manufacturer, GVWR, seatRows, seats) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(insertSql)) {

            stmt.setString(1, vehicle.getVIN());
            stmt.setInt(2, userId);
            stmt.setString(3, vehicle.getNickname());
            stmt.setString(4, vehicle.getMake());
            stmt.setString(5, vehicle.getModel());
            stmt.setInt(6, vehicle.getYear());
            stmt.setString(7, vehicle.getTrim());
            stmt.setString(8, vehicle.getVehicleType());
            stmt.setString(9, vehicle.getBodyClass());
            stmt.setInt(10, vehicle.getDoors());
            stmt.setString(11, vehicle.getFuelTypePrimary());
            stmt.setString(12, vehicle.getDriveType());
            stmt.setString(13, vehicle.getEngineModel());
            stmt.setInt(14, vehicle.getEngineCylinder());
            stmt.setDouble(15, vehicle.getDisplacementL());
            stmt.setString(16, vehicle.getTransmissionStyle());
            stmt.setInt(17, vehicle.getTransmissionSpeed());
            stmt.setString(18, vehicle.getPlantCountry());
            stmt.setString(19, vehicle.getManufacturer());
            stmt.setString(20, vehicle.getGvwr());
            stmt.setInt(21, vehicle.getSeatRows());
            stmt.setInt(22, vehicle.getSeats());

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Vehicle saved successfully.");
            } else {
                System.out.println("Vehicle save failed.");
            }

        } catch (SQLException e) {
            System.out.println("SQL error while saving vehicle:");
            e.printStackTrace();
        }
    }

    // TODO (+HOLDEN) (+SAUL): WRITE LOGIC TO REMOVE VEHICLE OBJECT FROM MYSQL
    public void removeVehicle(Vehicle vehicle, int userId) {

        String deleteSql = "DELETE FROM vehicles WHERE VIN_NUMBER = ? AND userID = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(deleteSql)) {

            stmt.setString(1, vehicle.getVIN());
            stmt.setInt(2, userId);

            int rowsDeleted = stmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Vehicle removed successfully.");
            } else {
                System.out.println("No matching vehicle found to delete.");
            }

        } catch (SQLException e) {
            System.out.println("SQL error while removing vehicle:");
            e.printStackTrace();
        }
    }

    // TODO (+HOLDEN): WRITE GUI LOGIC TO OPEN A TEXT BOX AND TAKE USER INPUT FROM A RESULTING PROMPT BOX
    public void editName(Vehicle vehicle) {
        String newName = "new name";
        vehicle.setNickname(newName);
    }

    public static void main(String[] args) {
        VINDecoderMain decoder = new VINDecoderMain();
        decoder.run();
    }
}