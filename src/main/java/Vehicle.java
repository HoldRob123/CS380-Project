
public class Vehicle {

    // Essential Fields
    private String VIN;
    private String nickname;
    private String make;
    private String model;
    private int year;

    // Non-Essential Fields
    private Boolean isSaved;
    private String trim;
    private String vehicleType;
    private String bodyClass;
    private int doors;
    private String fuelTypePrimary;
    private String driveType;
    private String engineModel;
    private int engineCylinder;
    private double displacementL;
    private String transmissionStyle;
    private int transmissionSpeed;
    private String plantCountry;
    private String manufacturer;
    private String GVWR;
    private int seatRows;
    private int seats;

    // Temporary GUIFiles.Vehicle Constructor: Essential Fields Only
    public Vehicle(String VIN, String nickname, String make, String model, int year, boolean isSaved) {
        this.VIN = VIN;
        this.nickname = nickname;
        this.make = make;
        this.model = model;
        this.year = year;
        this.isSaved = isSaved;

    }

    // Permanent GUIFiles.Vehicle Constructor: ALl fields
    public Vehicle(String VIN, String nickname, String make, String model, int year, boolean isSaved,
                   String trim, String vehicleType, String bodyClass, int doors, String fuelTypePrimary,
                   String driveType, String engineModel, int engineCylinder, double displacementL,
                   String transmissionStyle, int transmissionSpeed, String plantCountry, String manufacturer,
                   String gvwr, int seatRows, int seats) {
        this.VIN = VIN;
        this.nickname = nickname;
        this.make = make;
        this.model = model;
        this.year = year;
        this.isSaved = isSaved;
        this.trim = trim;
        this.vehicleType = vehicleType;
        this.bodyClass = bodyClass;
        this.doors = doors;
        this.fuelTypePrimary = fuelTypePrimary;
        this.driveType = driveType;
        this.engineModel = engineModel;
        this.engineCylinder = engineCylinder;
        this.displacementL = displacementL;
        this.transmissionStyle = transmissionStyle;
        this.transmissionSpeed = transmissionSpeed;
        this.plantCountry = plantCountry;
        this.manufacturer = manufacturer;
        this.GVWR = gvwr;
        this.seatRows = seatRows;
        this.seats = seats;
    }

    // Setters
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setIsSaved(Boolean isSaved) {
        this.isSaved = isSaved;
    }
    public void setIsSaved(boolean b) {
    }


    // Getters
    public String getNickname() {
        return nickname;
    }

    public Boolean getSaved() {
        return isSaved;
    }

    public String getVIN() {
        return VIN;
    }

    public String getMake() { return make; }

    public String getModel() { return model; }

    public int getYear() { return year; }

    public String getTrim() { return trim; }

    public String getVehicleType() { return vehicleType; }

    public String getBodyClass() { return bodyClass; }

    public int getDoors() {
        return doors;
    }

    public String getFuelTypePrimary() { return fuelTypePrimary; }

    public String getDriveType() { return driveType; }

    public String getEngineModel() { return engineModel; }

    public int getEngineCylinder() {
        return engineCylinder;
    }

    public double getDisplacementL() {
        return displacementL;
    }

    public String getTransmissionStyle() { return transmissionStyle; }

    public int getTransmissionSpeed() { return transmissionSpeed; }

    public String getPlantCountry() { return plantCountry; }

    public String getManufacturer() { return manufacturer; }

    public String getGvwr() { return GVWR; }

    public int getSeatRows() {
        return seatRows;
    }

    public int getSeats() {
        return seats;
    }

    public String fullDescription() {
        StringBuilder sb = new StringBuilder();

        sb.append("VIN: ").append(VIN).append("\n");
        sb.append("Nickname: ").append(nickname == null || nickname.isEmpty() ? "N/A" : nickname).append("\n");
        sb.append("Make: ").append(make).append("\n");
        sb.append("Model: ").append(model).append("\n");
        sb.append("Year: ").append(year).append("\n");
        sb.append("Saved: ").append(isSaved != null && isSaved ? "Yes" : "No").append("\n");
        sb.append("Trim: ").append(trim == null ? "N/A" : trim).append("\n");
        sb.append("GUIFiles.Vehicle Type: ").append(vehicleType == null ? "N/A" : vehicleType).append("\n");
        sb.append("Body Class: ").append(bodyClass == null ? "N/A" : bodyClass).append("\n");
        sb.append("Doors: ").append(doors).append("\n");
        sb.append("Fuel Type: ").append(fuelTypePrimary == null ? "N/A" : fuelTypePrimary).append("\n");
        sb.append("Drive Type: ").append(driveType == null ? "N/A" : driveType).append("\n");
        sb.append("Engine Model: ").append(engineModel == null ? "N/A" : engineModel).append("\n");
        sb.append("Engine Cylinders: ").append(engineCylinder).append("\n");
        sb.append("Displacement (L): ").append(displacementL).append("\n");
        sb.append("Transmission Style: ").append(transmissionStyle == null ? "N/A" : transmissionStyle).append("\n");
        sb.append("Transmission Speed: ").append(transmissionSpeed).append("\n");
        sb.append("Plant Country: ").append(plantCountry == null ? "N/A" : plantCountry).append("\n");
        sb.append("Manufacturer: ").append(manufacturer == null ? "N/A" : manufacturer).append("\n");
        sb.append("GVWR: ").append(GVWR == null ? "N/A" : GVWR).append("\n");
        sb.append("Seat Rows: ").append(seatRows).append("\n");
        sb.append("Seats: ").append(seats).append("\n");

        return sb.toString();
    }

    //toString: currently includes logic for a vehicle listing
    @Override
    public String toString() {
        String listing = "";
        if(isSaved) {
            listing += "Nickname: " + nickname + "\n";
        } else {
            listing += "VIN: " + VIN + "\n";
        }
        listing += "Make: " + make + "\n";
        listing += "Model: " + model + "\n";
        listing += "Year: " + year + "\n";
        return listing;
    }
}