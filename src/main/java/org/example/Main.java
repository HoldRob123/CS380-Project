import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


//THIS IS THE SEARCH LOGIC PORTION (VERY ROUGH VERSION)
public class Main {
    public static void main(String[] args) {
        String vin = "1N6BA1F43GN516269"; // Sample VIN hardcoded
        String url = "https://vpic.nhtsa.dot.gov/api/vehicles/DecodeVinValues/" + vin + "?format=json";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            //GEN_USE    pass1



            // Database connection info
            String dbUrl = "jdbc:mysql://10.10.2.168:3306/vin_decoder_db"; // your DB name
            String dbUsername = "GEN_USE"; // your MySQL username
            String dbPassword = "pass1"; // your MySQL password

            Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);


            String sql = "INSERT INTO vehicles (" +
                    "VIN_NUMBER, V_make, V_model, V_year, V_trim, V_type, V_bodyClass, " +
                    "V_doors, V_fuelType, V_DriveType, V_EngineModel, V_CylinderNum, " +
                    "V_EngineDisplace, V_TransStyle, V_TransSpeeds, " +
                    "V_PlantCountry, V_Manufacturer, V_GVWR, V_rows, V_seats" +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


            PreparedStatement pstmt = conn.prepareStatement(sql);


            // Setting the parameters
            pstmt.setString(1, vin);                                   // VIN_NUMBER
            pstmt.setString(2, extractDetail(body, "Make"));           // V_make
            pstmt.setString(3, extractDetail(body, "Model"));          // V_model
            pstmt.setString(4, extractDetail(body, "ModelYear"));      // V_year
            pstmt.setString(5, extractDetail(body, "Trim"));           // V_trim
            pstmt.setString(6, extractDetail(body, "VehicleType"));    // V_type
            pstmt.setString(7, extractDetail(body, "BodyClass"));      // V_bodyClass

            pstmt.setInt(8, parseIntSafe(extractDetail(body, "Doors")));               // V_doors
            pstmt.setString(9, extractDetail(body, "FuelTypePrimary"));                // V_fuelType
            pstmt.setString(10, extractDetail(body, "DriveType"));                     // V_DriveType
            pstmt.setString(11, extractDetail(body, "EngineModel"));                   // V_EngineModel
            pstmt.setInt(12, parseIntSafe(extractDetail(body, "EngineCylinders")));    // V_CylinderNum
            pstmt.setBigDecimal(13, parseBigDecimalSafe(extractDetail(body, "DisplacementL"))); // V_EngineDisplace
            pstmt.setString(14, extractDetail(body, "TransmissionStyle"));             // V_TransStyle
            pstmt.setInt(15, parseIntSafe(extractDetail(body, "TransmissionSpeeds"))); // V_TransSpeeds
            pstmt.setString(16, extractDetail(body, "PlantCountry"));                  // V_PlantCountry
            pstmt.setString(17, extractDetail(body, "Manufacturer"));                  // V_Manufacturer
            pstmt.setString(18, extractDetail(body, "GVWR"));                          // V_GVWR
            pstmt.setInt(19, parseIntSafe(extractDetail(body, "SeatRows")));           // V_rows
            pstmt.setInt(20, parseIntSafe(extractDetail(body, "Seats")));              // V_seats
            pstmt.executeUpdate();
            System.out.println("org.example.Vehicle inserted into database!");

            conn.close();

        } catch (IOException | InterruptedException | SQLException e) {
            e.printStackTrace();
        }
    }

    // Utility method to extract a value from JSON manually
    public static String extractDetail(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int index = json.indexOf(searchKey);
        if (index == -1) {
            return null;
        }

        index = json.indexOf(":", index) + 1;
        while (json.charAt(index) == ' ' || json.charAt(index) == '"') index++;
        int end = json.indexOf("\"", index);
        if (end == -1) {
            end = json.indexOf(",", index);
        }

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

    // Helper method to safely parse BigDecimal with one decimal place
    public static BigDecimal parseBigDecimalSafe(String value) {
        try {
            return value != null ? new BigDecimal(value).setScale(1, RoundingMode.HALF_UP) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}


