/****************************************
 VIN DECODER - CS 380
 Isak Jacobson
 Saul Rodriguez-Tapia
 Holden Robinson

 Push Date: 6/4/25
 Push Number: 11
 Last Modified By: Isak
 *****************************************/

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
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
    private static final String DB_URL = "jdbc:mysql://localhost:3306/vin_vehicle_db?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String DB_USERNAME = "GEN_USE";
    private static final String DB_PASSWORD = "pass1";

    // User Credentials
    public String currentUser = "";
    public int currentUserID = -1;

    // Vehicle Cache
    protected List<Vehicle> cacheVehicles = new ArrayList<>();
    Random yr = new Random();


    // GUI References
    private LoginScreen loginScreen;
    private CompareView compareView;
    public MainView mainView;

    // Facilitator method for main screen
    public void run() {
        // Loads Vehicle Cache
//        URL url = getClass().getClassLoader().getResource("/textfiles/vinresults.txt");
//        System.out.println("Resource URL: " + url);
//
//
//        InputStream input = getClass().getClassLoader().getResourceAsStream("/textfiles/vinresults.txt");
//        if (input == null) {
//            System.err.println("File not found!");
//            return;
//        }

        String input = "C:\\Users\\isaka\\OneDrive\\Documents\\GitHub\\CS380-Project\\src\\main\\resources\\textfiles\\vinresults.txt";
        loadCachedVehicles(input);

        // Launches Login Page
        SwingUtilities.invokeLater(() -> {
            LoginScreen loginScreen = new LoginScreen(this);
            loginScreen.setVisible(true);
        });
    }

    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }

    public MainView getMainView() {
        return mainView;
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
                        result = "Credentials are incorrect";
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
                insertStmt.setString(2, password);
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



    // Make some VINS available by filtering search
    public void loadCachedVehicles(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath.toString()))) {
            String line;
            String vin = null;
            String make = "";
            String model = "";
            int year = 0;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) continue;

                if (line.matches("^\\w{17}.*")) {
                    vin = line.split(" ")[0]; // Grab VIN from the first line
                } else if (line.startsWith("Make:")) {
                    make = line.substring(5).trim();
                } else if (line.startsWith("Model:")) {
                    model = line.substring(6).trim();
                } else if (line.startsWith("Year:")) {
                    try {
                        year = Integer.parseInt(line.substring(5).trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid year format: " + line);
                        year = 0;
                    }
                } else if (line.startsWith("]")) {
                    // End of one vehicle block
                    if (vin != null && !make.isEmpty() && !model.isEmpty() && year != 0) {
                        cacheVehicles.add(new Vehicle(vin, "N/A", make, model, year, false));
                        System.out.println("✅ Cached: " + vin);
                    } else {
                        System.out.println("⚠️ Skipping incomplete entry: " + vin);
                    }
                    // Reset for next block
                    vin = null;
                    make = "";
                    model = "";
                    year = 0;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



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

        // Holds the returned vehicles
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
            if (v.getSaved()) {
                info.add(new JLabel("Nickname:" + v.getNickname()));
            } else {
                info.add(new JLabel("VIN:" + v.getVIN()));
            }
            info.add(new JLabel("Model:" + v.getModel()));
            info.add(new JLabel("Make:" + v.getMake()));
            info.add(new JLabel("Year:" + v.getYear()));


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



    // Popup menu when clicking vehicle options "..." button
    private void showVehicleOptions(Vehicle vehicle, Component invoker) {
        JPopupMenu menu = new JPopupMenu();

        // Always show these options
        JMenuItem info = new JMenuItem("Full Information");
        info.addActionListener(e -> {
            Vehicle full = solidifyVehicle(vehicle);
            JOptionPane.showMessageDialog(mainView, full.fullDescription(),
                    "Vehicle Info", JOptionPane.INFORMATION_MESSAGE);
        });
        menu.add(info);

        JMenuItem compare = new JMenuItem("Compare Vehicle");
        compare.addActionListener(e -> openCompareView(vehicle));
        menu.add(compare);

        // Dynamic options based on saved status
        if (vehicle.getSaved()) {
            // Options for saved vehicles
            JMenuItem edit = new JMenuItem("Edit Nickname");
            edit.addActionListener(e -> {
                String newName = JOptionPane.showInputDialog(mainView,
                        "New nickname:", vehicle.getNickname());
                if (newName != null && !newName.trim().isEmpty()) {
                    vehicle.setNickname(newName.trim());
                    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                        String sql = "UPDATE vehicles SET nickname = ? WHERE VIN_NUMBER = ? AND userID = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                            stmt.setString(1, vehicle.getNickname());
                            stmt.setString(2, vehicle.getVIN());
                            stmt.setInt(3, currentUserID);
                            stmt.executeUpdate();
                            JOptionPane.showMessageDialog(mainView, "Nickname updated successfully!");
                            performSearch();
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(mainView, "Error updating nickname: " + ex.getMessage(),
                                "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            menu.add(edit);

            JMenuItem remove = new JMenuItem("Remove from Saved");
            remove.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(mainView,
                        "Are you sure you want to remove this vehicle from your saved vehicles?",
                        "Confirm Removal", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                        removeVehicle(vehicle, currentUserID);
                        vehicle.setIsSaved(false);
                        JOptionPane.showMessageDialog(mainView, "Vehicle removed from saved.");
                        performSearch();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(mainView, "Error removing vehicle: " + ex.getMessage(),
                                "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            menu.add(remove);
        } else {
            // Option for unsaved vehicles
            JMenuItem save = new JMenuItem("Save Vehicle");
            save.addActionListener(e -> {
                String nickname = vehicle.getNickname();
                if (nickname == null || nickname.isEmpty()) {
                    nickname = JOptionPane.showInputDialog(mainView,
                            "Enter a nickname for this vehicle:");
                    if (nickname == null || nickname.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(mainView,
                                "Vehicle must have a nickname to be saved.");
                        return;
                    }
                    vehicle.setNickname(nickname.trim());
                }

                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                    // Get user ID if not already set
                    if (currentUserID == -1) {
                        String sql = "SELECT userID FROM users WHERE userName = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                            stmt.setString(1, currentUser);
                            ResultSet rs = stmt.executeQuery();
                            if (rs.next()) {
                                currentUserID = rs.getInt("userID");
                            }
                        }
                    }

                    if (!vehicleExistsInDB(vehicle.getVIN(), currentUserID)) {
                        Vehicle vehicleToSave = solidifyVehicle(vehicle);
                        vehicleToSave.setIsSaved(true);
                        saveVehicle(vehicleToSave, currentUserID);
                        JOptionPane.showMessageDialog(mainView, "Vehicle saved successfully!");
                        performSearch();
                    } else {
                        JOptionPane.showMessageDialog(mainView,
                                "This vehicle is already in your saved vehicles.");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(mainView, "Error saving vehicle: " + ex.getMessage(),
                            "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            menu.add(save);
        }

        menu.show(invoker, invoker.getWidth() / 2, invoker.getHeight() / 2);
    }

    // Helper method to check if vehicle exists in db
    private boolean vehicleExistsInDB(String vin, int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM vehicles WHERE VIN_NUMBER = ? AND userID = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, vin);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }



    // Method to direct to CompareView GUI once "Compare Vehicle" is clicked
    public void openCompareView(Vehicle vehicle) {
        VINDecoderCompare compareApp = new VINDecoderCompare(this); // Pass the current instance
        compareApp.run(vehicle);
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
                            rs.getInt("V_year"),
                            true
                    );
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

                    Vehicle vehicle = new Vehicle(identifier, "", make, model, year, false);
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



    // TODO (+HOLDEN) (+SAUL): WRITE LOGIC TO SEARCH FOR THE VEHICLE IN MYSQL AND NHTSA GIVEN AN ARRAY LIST OF ATTRIBUTES
    public ArrayList<Vehicle> confirmFilter(Map<String, String> filterList) {
        ArrayList<Vehicle> results = new ArrayList<>();

        if (filterList.containsKey("savedOnly")) {
            results.addAll(getSavedFilteredVehicles(filterList));
            return results;
        }

        // Not savedOnly: Merge all three sources
        Set<Vehicle> merged = new HashSet<>();  // avoids duplicates based on equals/hashCode

        merged.addAll(getSavedFilteredVehicles(filterList));
        merged.addAll(getFilteredNHTSAVehicles(filterList));
        merged.addAll(filterCachedVINS(filterList));

        results.addAll(merged);
        return results;
    }


    // Apply filters on the collection of precached vehicles
    public ArrayList<Vehicle> filterCachedVINS(Map<String, String> filterList) {
        ArrayList<Vehicle> results = new ArrayList<>();

        for (Vehicle v : cacheVehicles) {
            boolean matches = true;

            if (filterList.containsKey("make") && !v.getMake().toLowerCase().contains(filterList.get("make").toLowerCase())) {
                matches = false;
            }
            if (filterList.containsKey("model") && !v.getModel().toLowerCase().contains(filterList.get("model").toLowerCase())) {
                matches = false;
            }
            if (filterList.containsKey("year")) {
                try {
                    int yearFilter = Integer.parseInt(filterList.get("year"));
                    if (v.getYear() != yearFilter) {
                        matches = false;
                    }
                } catch (NumberFormatException e) {
                    matches = false;
                }
            }

            if (matches) {
                results.add(v);
            }
        }

        return results;
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
                params.add(Optional.of(currentUserID));
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
                            rs.getInt("V_year"),
                            true // isSaved
                    );
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
    private ArrayList<Vehicle> getFilteredNHTSAVehicles(Map<String, String> filterList) {
        ArrayList<Vehicle> results = new ArrayList<>();

        try {
            String make = filterList.getOrDefault("make", "").trim();
            String model = filterList.getOrDefault("model", "").trim();
            String sYear = filterList.getOrDefault("year", "").trim();

            if ((make.isEmpty()) && (sYear.isEmpty())) {
                System.err.println("Skipping NHTSA API call: both 'make' and 'year' are missing.");
                return results;
            }

            String apiUrl;
            boolean useMakeYear = !make.isEmpty() && !sYear.isEmpty();
            boolean useMakeOnly = !make.isEmpty() && sYear.isEmpty();

            if (useMakeYear) {
                // Full filter (preferred)
                apiUrl = String.format(
                        "https://vpic.nhtsa.dot.gov/api/vehicles/GetModelsForMakeYear/make/%s/modelyear/%s?format=json",
                        URLEncoder.encode(make, "UTF-8"),
                        URLEncoder.encode(sYear, "UTF-8")
                );
            } else if (useMakeOnly) {
                // Just make
                apiUrl = String.format(
                        "https://vpic.nhtsa.dot.gov/api/vehicles/GetModelsForMake/%s?format=json",
                        URLEncoder.encode(make, "UTF-8")
                );
            } else {
                // Just year — no direct endpoint for this, so return empty
                System.err.println("No suitable API endpoint for year-only filtering.");
                return results;
            }

            System.out.println("Calling NHTSA URL: " + apiUrl);

            // Send request
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            // Read response
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

            // Random Year if none is specified
            int year = sYear.isEmpty() ? yr.nextInt(2025-1990+1) + 1990 : Integer.parseInt(sYear);

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject obj = resultsArray.getJSONObject(i);
                String nhtsaMake = obj.optString("Make_Name", make);
                String nhtsaModel = obj.optString("Model_Name", "Unknown");

                if (!model.isEmpty() && !nhtsaModel.toLowerCase().contains(model.toLowerCase())) {
                    continue;
                }

                Vehicle temp = new Vehicle("<VIN UNKNOWN>", "N/A", nhtsaMake, nhtsaModel, year, false);
                temp.setIsSaved(false);
                results.add(temp);
            }

        } catch (Exception e) {
            System.err.println("Failed to retrieve data from NHTSA: " + e.getMessage());
            e.printStackTrace();
        }

        return results;
    }





    public Vehicle solidifyVehicle(Vehicle tempVehicle) {

        String vin = tempVehicle.getVIN();
        String apiUrl = "https://vpic.nhtsa.dot.gov/api/vehicles/DecodeVin/" + vin + "?format=json";

        try {
            // Establish HTTP connection to API
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            JSONObject json = new JSONObject(response.toString());
            JSONArray results = json.getJSONArray("Results");

            // Extract useful fields
            String trim = getValue(results, "Trim");
            String vehicleType = getValue(results, "Vehicle Type");
            String bodyClass = getValue(results, "Body Class");
            int doors = parseInt(getValue(results, "Doors"));
            String fuelTypePrimary = getValue(results, "FuelType - Primary");
            String driveType = getValue(results, "Drive Type");
            String engineModel = getValue(results, "Engine Model");
            int engineCylinders = parseInt(getValue(results, "Engine Cylinders"));
            double displacement = parseDouble(getValue(results, "Displacement (L)"));
            String transmissionStyle = getValue(results, "Transmission Style");
            int transmissionSpeeds = parseInt(getValue(results, "Transmission Speeds"));
            String plantCountry = getValue(results, "Plant Country");
            String manufacturer = getValue(results, "Manufacturer");
            String gvwr = getValue(results, "GVWR");
            int seatRows = parseInt(getValue(results, "Number of Seat Rows")); // Substitute if needed
            int seats = parseInt(getValue(results, "Seats"));
            System.out.println(results.toString(2));



            Vehicle permVehicle = new Vehicle(tempVehicle.getVIN(), tempVehicle.getNickname(), tempVehicle.getMake(),
                    tempVehicle.getModel(), tempVehicle.getYear(), false, trim, vehicleType, bodyClass, doors, fuelTypePrimary,
                    driveType, engineModel, engineCylinders, displacement, transmissionStyle, transmissionSpeeds, plantCountry,
                    manufacturer, gvwr, seatRows,
                    seats);
            System.out.println(permVehicle.fullDescription());
            return permVehicle;

        } catch (Exception e) {
            System.err.println("Failed to retrieve data from NHTSA: " + e.getMessage());
            e.printStackTrace();
            return tempVehicle; // Fallback to original
        }
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

    // === JSON HELPER METHODS ====
    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return "";

        startIndex += searchKey.length();
        int endIndex = json.indexOf("\"", startIndex);
        if (endIndex == -1) return "";

        return json.substring(startIndex, endIndex);
    }

    // JSON to help parse return values from NHTSA
    private String getValue(JSONArray results, String variableName) {
        for (int i = 0; i < results.length(); i++) {
            JSONObject obj = results.getJSONObject(i);
            if (variableName.equalsIgnoreCase(obj.optString("Variable"))) {
                Object valueObj = obj.opt("Value");
                return valueObj != null ? valueObj.toString() : "";
            }
        }
        return "";
    }

    private int parseInt(String val) {
        try {
            return Integer.parseInt(val);
        } catch (Exception e) {
            return 0;
        }
    }

    private double parseDouble(String val) {
        try {
            return Double.parseDouble(val);
        } catch (Exception e) {
            return 0.0;
        }
    }
}