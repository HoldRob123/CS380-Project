/****************************************
 VIN DECODER - CS 380
 Isak Jacobson
 Saul Rodriguez-Tapia
 Holden Robinson

 Push Date: 5/26/25
 Push Number: 3
 Last Modified By: Isak
 *****************************************/


import java.util.ArrayList;


public class VINDecoderMain {

    // Facilitator method for main screen
    public void run() {
        // TODO (+HOLDEN): NEEDS GUI LOGIC TO CHECK AND SAVE USER INPUT IN USER/PW FIELDS
        String username = "user";
        String password = "pw";
        //
        saveLogin(username, password);

        // Testing --
        Vehicle temp = new Vehicle("12345678901234567", "name", "make", "model", 2025);
        Vehicle perm = solidifyVehicle(temp);
        perm.setIsSaved(true);
        System.out.println(perm.toString());
        //
    }

    // TODO (+HOLDEN): WRITE LOGIC TO COMPARE USERNAMES AND PASSWORDS TO MYSQL
    public boolean saveLogin(String username, String password) {
        return false;
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
        return null;
    }

    // TODO (+HOLDEN) (+SAUL): WRITE LOGIC TO SEARCH FOR THE VEHICLE IN MYSQL AND NHTSA GIVEN AN ARRAY LIST OF ATTRIBUTES
    public ArrayList<Vehicle> confirmFilter(ArrayList<String> filters) {
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
