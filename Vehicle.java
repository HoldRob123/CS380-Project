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

    // Temporary Vehicle Constructor: Essential Fields Only
    public Vehicle(String VIN, String nickname, String make, String model, int year) {
        this.VIN = VIN;
        this.nickname = nickname;
        this.make = make;
        this.model = model;
        this.year = year;

    }

    // Permanent Vehicle Constructor: ALl fields
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
        gvwr = GVWR;
        this.seatRows = seatRows;
        this.seats = seats;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setIsSaved(Boolean isSaved) {
        this.isSaved = isSaved;
    }

    public String getVIN() {
        return VIN;
    }

    public String getNickname() {
        return nickname;
    }



    public Boolean getSaved() {
        return isSaved;
    }

    public double getDisplacementL() {
        return displacementL;
    }

    public int getDoors() {
        return doors;
    }

    public int getEngineCylinder() {
        return engineCylinder;
    }

    public int getSeats() {
        return seats;
    }

    public int getSeatRows() {
        return seatRows;
    }


}

