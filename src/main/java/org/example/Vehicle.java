package org.example;

public class Vehicle {
    private String vin;
    private String make;
    private String model;
    private String year;
    private String nickname;


    public Vehicle(String vin, String make, String model, String year, int i, boolean b, String nickname, String sedan, String coupe, int i1, String gasoline, String fwd, String k24A4, int i2, double v, String automatic, int i3, String japan, String hondaMotorCo, String s, int i4, int i5) {
        this.vin = vin;
        this.make = make;
        this.model = model;
        this.year = year;
        this.nickname = nickname;
    }

    // Getters
    public String getVin() { return vin; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public String getYear() { return year; }
    public String getNickname() { return nickname; }

    // Optional: toString for easy logging
    @Override
    public String toString() {
        return String.format("VIN: %s | Make: %s | Model: %s | Year: %s | Nickname: %s",
                vin, make, model, year, nickname);
    }

    public void setIsSaved(boolean b) {
    }
}
