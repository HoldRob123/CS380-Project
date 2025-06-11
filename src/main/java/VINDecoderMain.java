import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
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

public class VINDecoderMain {

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/vin_vehicle_db?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String DB_USERNAME = "GEN_USE";
    private static final String DB_PASSWORD = "pass1";

    // User Credentials
    public String currentUser = "";
    public static int currentUserID = -1;

    // Vehicle Cache
    protected List<Vehicle> cacheVehicles = new ArrayList<>();


    // GUI References
    private LoginScreen loginScreen;
    public MainView mainView;
    private VehicleLibrary vehicleLibrary = new VehicleLibrary();
    private Random r = new Random();

    public MainView getMainView() {
        return mainView;
    }

    // Starts VINDecoder Main
    public void run() {
        // If this comment is above input - change to cwu
        String input = "C:\\Users\\isaka\\OneDrive\\Documents\\GitHub\\cs380project\\src\\main\\resources\\vinresults.txt";
        loadCachedVehicles(input);

        // Launches Login Page
        SwingUtilities.invokeLater(() -> {
            LoginScreen loginScreen = new LoginScreen(this);
            loginScreen.setVisible(true);
        });
    }

    // Logs in based on the credentials the user has entered
    public String tryLogin(String username, String password) {
        // Input validation (redundant but safe)
        if (username == null || username.trim().isEmpty() ||
                password == null || password.isEmpty()) {
            return "Username and password cannot be empty";
        }

        String result;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            // Check if username exists
            String checkUserSql = "SELECT userID, userPassword FROM users WHERE userName = ?";
            try (PreparedStatement checkUserStmt = conn.prepareStatement(checkUserSql)) {
                checkUserStmt.setString(1, username);
                ResultSet rs = checkUserStmt.executeQuery();

                if (rs.next()) {
                    // Verify password
                    String correctPassword = rs.getString("userPassword");
                    if (correctPassword.equals(password)) {
                        this.currentUser = username;
                        currentUserID = rs.getInt("userID");
                        result = "Login Successful!";
                    } else {
                        result = "Incorrect password";
                    }
                } else {
                    // Create new user
                    if (createUser(username, password)) {
                        this.currentUser = username;
                        result = "Account Created!";
                    } else {
                        result = "Failed to create account";
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result = "Database error: " + e.getMessage();
        }
        return result;
    }

    // Creates a new user row in the mySQL database
    private boolean createUser(String username, String password) {
        String insertSql = "INSERT INTO users (userName, userPassword) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {

            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            int rows = insertStmt.executeUpdate();

            if (rows > 0) {
                try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        currentUserID = generatedKeys.getInt(1);
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            System.out.println("Database error occurred:");
            e.printStackTrace();
            return false;
        }
    }

    public void onLoginSuccess() {
        if (loginScreen != null) {
            loginScreen.dispose();
            loginScreen = null;
        }
        mainView = new MainView(this);
        mainView.setVisible(true);
    }

    // Make some VINS available by filtering search upon launch
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


        if (mainView.getSavedOnly().isSelected()) {
            filters.put("savedOnly", "true");
        }

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
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
            card.setBorder(BorderFactory.createLineBorder(Color.gray));

            JPanel info = new JPanel();
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            if (v.getSaved()) {
                info.add(new JLabel("Nickname:" + v.getNickname()));
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

        // Check if vehicle is saved in the database
        boolean isSaved = false;
        try {
            isSaved = vehicleExistsInDB(vehicle.getVIN(), currentUserID);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(mainView, "Error checking vehicle status: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        if (isSaved) {
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
                    boolean removed = removeVehicle(vehicle, currentUserID);
                    if (removed) {
                        vehicle.setIsSaved(false);
                        JOptionPane.showMessageDialog(mainView, "Vehicle removed from saved");
                        performSearch();
                    } else {
                        JOptionPane.showMessageDialog(mainView, "Failed to remove vehicle.",
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
                if (nickname.equals("N/A") || nickname.isEmpty()) {
                    nickname = JOptionPane.showInputDialog(mainView,
                            "Enter a nickname for this vehicle:");
                    if (nickname.equals("N/A") || nickname.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(mainView,
                                "Vehicle must have a nickname to be saved.");
                        return;
                    }
                    vehicle.setNickname(nickname.trim());
                }

                try {
                    Vehicle vehicleToSave = solidifyVehicle(vehicle);
                    vehicleToSave.setIsSaved(true);
                    boolean saved = saveVehicle(vehicleToSave, currentUserID);
                    if (saved) {
                        JOptionPane.showMessageDialog(mainView, "Vehicle saved successfully!");
                    } else {
                        JOptionPane.showMessageDialog(mainView, "Failed to save vehicle.");
                    }
                } catch (Exception ex) {
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

    // Allows the user to search for a VIN or nickname
    public ArrayList<Vehicle> confirmSearch(String identifier) {
        ArrayList<Vehicle> vehicleList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {

        String sql = "";

        // If identifier is not a 17-char VIN, treat it as a search keyword
        if (identifier.length() != 17) {
            sql = """
                SELECT VIN_NUMBER, nickname, V_make, V_model, V_year
                FROM vehicles
                WHERE V_make LIKE ? OR V_model LIKE ? OR nickname LIKE ?
                """;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                String keyword = "%" + identifier + "%"; // partial match
                stmt.setString(1, keyword);
                stmt.setString(2, keyword);
                stmt.setString(3, keyword);

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
            return vehicleList; // return matched name results
        }

            // Try to find VIN in MySQL
            sql = "SELECT VIN_NUMBER, nickname, V_make, V_model, V_year FROM vehicles WHERE VIN_NUMBER = ?";
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

    // Facilitator class for all filtering means
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

    // This method filters cached vehicles based on criteria from the provided filterList map.
    public ArrayList<Vehicle> filterCachedVINS(Map<String, String> filterList) {

        // Initialize an empty list to store vehicles that match the filter criteria
        ArrayList<Vehicle> results = new ArrayList<>();

        // Loop through each vehicle in the cached list
        for (Vehicle v : cacheVehicles) {
            // Assume the vehicle matches the filters unless proven otherwise
            boolean matches = true;

            // Check if the filter contains a "make" key and if the vehicle's make contains the filter value (case-insensitive)
            if (filterList.containsKey("make") && !v.getMake().toLowerCase().contains(filterList.get("make").toLowerCase())) {
                matches = false;
            }

            // Check if the filter contains a "model" key and if the vehicle's model contains the filter value (case-insensitive)
            if (filterList.containsKey("model") && !v.getModel().toLowerCase().contains(filterList.get("model").toLowerCase())) {
                matches = false;
            }

            // Check if the filter contains a "year" key
            if (filterList.containsKey("year")) {
                try {
                    // Parse the year value from the filter and compare it to the vehicle's year
                    int yearFilter = Integer.parseInt(filterList.get("year"));
                    if (v.getYear() != yearFilter) {
                        matches = false;
                    }
                } catch (NumberFormatException e) {
                    vehicleLibrary.getRealisticYear(v.getMake());
                }
            }

            // If the vehicle matches all specified filters, add it to the results list
            if (matches) {
                results.add(v);
            }
        }
        // Return the filtered list of vehicles
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
                params.add(currentUserID);
            } else {
                sql.append(" WHERE 1=1");
            }

            // Add filters if present
            if (filterList.get("make") != null && !filterList.get("make").isBlank()) {
                sql.append(" AND v.V_make LIKE ?");
                params.add("%" + filterList.get("make") + "%");
            }

            if (filterList.get("model") != null && !filterList.get("model").isBlank()) {
                sql.append(" AND v.V_model LIKE ?");
                params.add("%" + filterList.get("model") + "%");
            }

            if (filterList.get("year") != null && !filterList.get("year").isBlank()) {
                sql.append(" AND v.V_year = ?");
                params.add(Integer.parseInt(filterList.get("year")));
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

            VehicleLibrary vehicleLibrary = new VehicleLibrary();

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject obj = resultsArray.getJSONObject(i);
                String nhtsaMake = obj.optString("Make_Name", make);
                String nhtsaModel = obj.optString("Model_Name", "Unknown");

                if (!model.isEmpty() && !nhtsaModel.toLowerCase().contains(model.toLowerCase())) {
                    continue;
                }
                // Gets a realistic year if none is specified
                int year = sYear.isEmpty() ? vehicleLibrary.getRealisticYear(nhtsaMake) : Integer.parseInt(sYear);
                String unknownVIN = "Unknown-" + r.nextInt(999999999);
                Vehicle temp = new Vehicle(unknownVIN, "N/A", nhtsaMake, nhtsaModel, year, false);
                temp.setIsSaved(false);
                results.add(temp);
            }

        } catch (Exception e) {
            System.err.println("Failed to retrieve data from NHTSA: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }

    // Method to convert a temporary vehicle object to a permanent vehicle object
    public Vehicle solidifyVehicle(Vehicle tempVehicle) {

        // Use VIN in search
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
            String gvwr = getValue(results, "GVWR (Gross Vehicle Weight Rating)");
            int seatRows = parseInt(getValue(results, "Number of Seat Rows")); // Substitute if needed
            int seats = parseInt(getValue(results, "Seats"));

            Vehicle permVehicle = new Vehicle(tempVehicle.getVIN(), tempVehicle.getNickname(), tempVehicle.getMake(),
                    tempVehicle.getModel(), tempVehicle.getYear(), tempVehicle.getSaved(), trim, vehicleType, bodyClass, doors, fuelTypePrimary,
                    driveType, engineModel, engineCylinders, displacement, transmissionStyle, transmissionSpeeds, plantCountry,
                    manufacturer, gvwr, seatRows,
                    seats);
            //System.out.println(permVehicle.fullDescription());
            return permVehicle;

        } catch (Exception e) {
            System.err.println("Failed to retrieve data from NHTSA: " + e.getMessage());
            e.printStackTrace();
            return tempVehicle; // Fallback to original
        }
    }

    // Save a vehicle to the mySQL database associated with the user
    public boolean saveVehicle(Vehicle vehicle, int userId) {
        // First verify required fields
        if (vehicle.getVIN() == null || vehicle.getVIN().isEmpty()) {
            System.out.println("Cannot save vehicle: VIN is required");
            return false;
        }

        String insertSql = "INSERT INTO vehicles (VIN_NUMBER, userID, nickname, V_make, V_model, V_year, " +
                "V_trim, V_type, V_bodyClass, V_doors, V_fuelType, V_DriveType, V_EngineModel, " +
                "V_CylinderNum, V_EngineDisplace, V_TransStyle, V_TransSpeeds, V_PlantCountry, " +
                "V_Manufacturer, V_GVWR, V_rows, V_seats) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                // Set all parameters with null checks
                stmt.setString(1, vehicle.getVIN());
                stmt.setInt(2, userId);
                stmt.setString(3, vehicle.getNickname() != null ? vehicle.getNickname() : "");
                stmt.setString(4, vehicle.getMake() != null ? vehicle.getMake() : "");
                stmt.setString(5, vehicle.getModel() != null ? vehicle.getModel() : "");
                stmt.setInt(6, vehicle.getYear());
                stmt.setString(7, vehicle.getTrim() != null ? vehicle.getTrim() : "");
                stmt.setString(8, vehicle.getVehicleType() != null ? vehicle.getVehicleType() : "");
                stmt.setString(9, vehicle.getBodyClass() != null ? vehicle.getBodyClass() : "");
                stmt.setInt(10, vehicle.getDoors());
                stmt.setString(11, vehicle.getFuelTypePrimary() != null ? vehicle.getFuelTypePrimary() : "");
                stmt.setString(12, vehicle.getDriveType() != null ? vehicle.getDriveType() : "");
                stmt.setString(13, vehicle.getEngineModel() != null ? vehicle.getEngineModel() : "");
                stmt.setInt(14, vehicle.getEngineCylinder());
                stmt.setDouble(15, vehicle.getDisplacementL());
                stmt.setString(16, vehicle.getTransmissionStyle() != null ? vehicle.getTransmissionStyle() : "");
                stmt.setInt(17, vehicle.getTransmissionSpeed());
                stmt.setString(18, vehicle.getPlantCountry() != null ? vehicle.getPlantCountry() : "");
                stmt.setString(19, vehicle.getManufacturer() != null ? vehicle.getManufacturer() : "");
                stmt.setString(20, vehicle.getGvwr() != null ? vehicle.getGvwr() : "");
                stmt.setInt(21, vehicle.getSeatRows());
                stmt.setInt(22, vehicle.getSeats());

                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    conn.commit();
                    vehicle.setIsSaved(true);
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Error saving vehicle: " + e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
            return false;
        }
    }

    // Removes vehicle from mySQL and any association to the user
    public boolean removeVehicle(Vehicle vehicle, int userId) {
        // Since we have ON DELETE CASCADE in the database, we only need to delete from vehicles
        String deleteSql = "DELETE FROM vehicles WHERE VIN_NUMBER = ? AND userID = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
                stmt.setString(1, vehicle.getVIN());
                stmt.setInt(2, userId);

                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error removing vehicle: " + e.getMessage());
            return false;
        }
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