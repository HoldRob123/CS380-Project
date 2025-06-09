import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// TODO: TRANSFER FROM FAKE VEHICLES TO REAL VEHICLES
public class VINDecoderCompare {
    private Vehicle vehicleA;
    private Vehicle vehicleB;
    private CompareView compareView;
    private VehicleLibrary library = new VehicleLibrary();
    private final VINDecoderMain mainApp;

    public VINDecoderCompare(VINDecoderMain mainApp) {
        this.mainApp = mainApp;
    }

    public void run(Vehicle vehicleA) {
        vehicleA = mainApp.solidifyVehicle(vehicleA);
        this.vehicleA = vehicleA;
        this.compareView = new CompareView(this);
        compareView.displayVehicleA(vehicleA);
        compareView.setVisible(true);
        mainApp.getMainView().setVisible(false);

        compareView.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                returnToMainView();
            }
        });
    }

    private Vehicle selectVehicleFromResults(List<Vehicle> vehicles) {
        String[] options = vehicles.stream()
                .map(v -> v.getYear() + " " + v.getMake() + " " + v.getModel() + " (" + v.getVIN() + ")")
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(
                compareView,
                "Select a vehicle to compare with:",
                "Choose Vehicle B",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);

        if (selected != null) {
            int index = Arrays.asList(options).indexOf(selected);
            return vehicles.get(index);
        }
        return null;
    }

    public void setVehicleB(Vehicle vehicleB) {
        // Grab full information from Vehicle B
        vehicleB = mainApp.solidifyVehicle(vehicleB);
        this.vehicleB = vehicleB;
        compareView.displayVehicleB(vehicleB);
    }

    public void performComparison() {
        if (vehicleB == null) {
            compareView.showMessage("Please select a vehicle to compare first",
                    "Comparison Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String comparisonResult = generateComparisonResult();
        compareView.showComparisonResult(comparisonResult);
    }

    private String generateComparisonResult() {
        StringBuilder result = new StringBuilder();

        // General Description of the Vehicle if Provided in Library
        String genA = library.getGeneralWrittenExplaination(vehicleA.getMake(), vehicleA.getModel());
        String genB = library.getGeneralWrittenExplaination(vehicleB.getMake(), vehicleB.getModel());

        if(!genA.isEmpty()) {
            result.append(genA);
            result.append("\n");
        }
        if(!genB.isEmpty()) {
            result.append(genB);
            result.append("\n");
        }

        // Value Comparisons
        // Age
        String yrComp = vehicleA.getYear() >= vehicleB.getYear() ? "younger" : "older";
        int yrDiff = Math.abs(vehicleA.getYear() - vehicleB.getYear());
        result.append("The " + vehicleA.getMake() + " " + vehicleA.getModel() + " is " + yrDiff + " years " + yrComp +
                " than the " + vehicleB.getMake() + " " +vehicleB.getYear() + "!\n");

        //Weight
        if(!vehicleA.getGvwr().equals("") && !vehicleB.getGvwr().equals("")) {
            int aLowGvwr = extractLowerBound(vehicleA.getGvwr());
            int bLowGvwr = extractLowerBound(vehicleB.getGvwr());
            String weComp = aLowGvwr > bLowGvwr ? "heavier" : "lighter";
            int weDiff = Math.abs(aLowGvwr - bLowGvwr);
            result.append("The " + vehicleA.getMake() + " " + vehicleA.getModel() + " is " + weDiff + " pounds " + weComp +
                    " than the " + vehicleB.getMake() + " " + vehicleB.getYear() + "!\n");
        }

        //Body Class
        if(vehicleA.getBodyClass() != null && vehicleB.getBodyClass() != null) {
            if(!vehicleA.getBodyClass().equals(vehicleB.getBodyClass())) {
                result.append("The " + vehicleA.getMake() + " " + vehicleA.getModel() + " has " + vehicleA.getBodyClass() + " whereas the "
                        + vehicleB.getMake() + " " + vehicleB.getYear() + " has " + vehicleB.getBodyClass() + "!\n");
            }
        }

        // Displacement
        if(vehicleA.getDisplacementL() != 0.0 && vehicleB.getDisplacementL() != 0.0) {
            String disComp = vehicleA.getDisplacementL() >= vehicleB.getDisplacementL() ? "higher" : "lower";
            double disDiff = Math.abs(vehicleA.getDisplacementL() - vehicleB.getDisplacementL());
            result.append("The " + vehicleA.getMake() + " " + vehicleA.getModel() + " has " + disDiff + "L " + disComp +
                    " displacement than the " + vehicleB.getMake() + " " + vehicleB.getYear() + "!\n\n");
        }

        //Drive Type
        if(vehicleA.getDriveType() != null && vehicleB.getDriveType() != null) {
            if(!vehicleA.getDriveType().equals(vehicleB.getDriveType())) {
                result.append("The " + vehicleA.getMake() + " " + vehicleA.getModel() + " has " + vehicleA.getDriveType() + " whereas the "
                        + vehicleB.getMake() + " " + vehicleB.getYear() + " has " + vehicleB.getDriveType() + "!\n");
            }
        }


        result.append(String.format("%-60s | %-60s | %-60s%n", "Attribute", "Vehicle A", "Vehicle B"));
        result.append("-".repeat(200)).append("\n");
        compareAttribute(result, "VIN", vehicleA.getVIN(), vehicleB.getVIN());
        compareAttribute(result, "Nickname",
                vehicleA.getNickname() != null ? vehicleA.getNickname() : "N/A",
                vehicleB.getNickname() != null ? vehicleB.getNickname() : "N/A");
        compareAttribute(result, "Make", vehicleA.getMake(), vehicleB.getMake());
        compareAttribute(result, "Model", vehicleA.getModel(), vehicleB.getModel());
        compareAttribute(result, "Year", String.valueOf(vehicleA.getYear()), String.valueOf(vehicleB.getYear()));
        compareAttribute(result, "Trim", vehicleA.getTrim(), vehicleB.getTrim());
        compareAttribute(result, "Vehicle Type", vehicleA.getVehicleType(), vehicleB.getVehicleType());
        compareAttribute(result, "Body Class", vehicleA.getBodyClass(), vehicleB.getBodyClass());
        compareAttribute(result, "Doors", String.valueOf(vehicleA.getDoors()), String.valueOf(vehicleB.getDoors()));
        compareAttribute(result, "Fuel Type", vehicleA.getFuelTypePrimary(), vehicleB.getFuelTypePrimary());
        compareAttribute(result, "Drive Type", vehicleA.getDriveType(), vehicleB.getDriveType());
        compareAttribute(result, "Engine Model", vehicleA.getEngineModel(), vehicleB.getEngineModel());
        compareAttribute(result, "Engine Cylinders", String.valueOf(vehicleA.getEngineCylinder()), String.valueOf(vehicleB.getEngineCylinder()));
        compareAttribute(result, "Displacement (L)", String.valueOf(vehicleA.getDisplacementL()), String.valueOf(vehicleB.getDisplacementL()));
        compareAttribute(result, "Transmission Style", vehicleA.getTransmissionStyle(), vehicleB.getTransmissionStyle());
        compareAttribute(result, "Transmission Speed", String.valueOf(vehicleA.getTransmissionSpeed()), String.valueOf(vehicleB.getTransmissionSpeed()));
        compareAttribute(result, "Plant Country", vehicleA.getPlantCountry(), vehicleB.getPlantCountry());
        compareAttribute(result, "Manufacturer", vehicleA.getManufacturer(), vehicleB.getManufacturer());
        compareAttribute(result, "GVWR", vehicleA.getGvwr(), vehicleB.getGvwr());
        compareAttribute(result, "Seat Rows", String.valueOf(vehicleA.getSeatRows()), String.valueOf(vehicleB.getSeatRows()));
        compareAttribute(result, "Seats", String.valueOf(vehicleA.getSeats()), String.valueOf(vehicleB.getSeats()));

        return result.toString();
    }

    // Value comparison helper method
    private void compareAttribute(StringBuilder result, String attribute, String valueA, String valueB) {
        String highlight = valueA.equals(valueB) ? "" : "*";
        result.append(String.format("%-60s | %-60s | %-60s%n",
                highlight + attribute + highlight,
                highlight + (valueA != null ? valueA : "N/A") + highlight,
                highlight + (valueB != null ? valueB : "N/A") + highlight));
    }

    public void returnToMainView() {
        if (compareView != null) {
            compareView.dispose();
        }
        mainApp.getMainView().setVisible(true);
    }

    private int extractLowerBound(String gvwr) {
        // Example input: "Class 2: 6,001 - 10,000 lb"
        if (gvwr == null || gvwr.isEmpty()) return Integer.MIN_VALUE;

        try {
            // Extract digits after the colon
            String[] parts = gvwr.split(":");
            if (parts.length < 2) return Integer.MIN_VALUE;

            // Find the lower bound number in the range string
            String rangePart = parts[1].trim(); // e.g. "6,001 - 10,000 lb"
            String numberOnly = rangePart.split("-")[0].replaceAll("[^\\d]", "");
            return Integer.parseInt(numberOnly);
        } catch (Exception e) {
            // Fallback if anything goes wrong
            return Integer.MIN_VALUE;
        }
    }

    public void handleSearch(String query, Map<String, String> filters) {
        String lowerQuery = query.toLowerCase();

        List<Vehicle> results = mainApp.confirmSearch(query).stream()
                .filter(v -> !v.getVIN().equals(vehicleA.getVIN()))
                .filter(v ->
                        (v.getVIN() != null && v.getVIN().toLowerCase().contains(lowerQuery)) ||
                                (v.getNickname() != null && v.getNickname().toLowerCase().contains(lowerQuery))
                )
                .collect(Collectors.toList());

        results = applyFilters(results, filters);
        showSearchResults(results);
    }

    public void handleFilterSearch(Map<String, String> filters) {
        List<Vehicle> results = mainApp.confirmFilter(filters).stream()
                .filter(v -> !v.getVIN().equals(vehicleA.getVIN()))
                .collect(Collectors.toList());

        showSearchResults(results);
    }

    private List<Vehicle> applyFilters(List<Vehicle> vehicles, Map<String, String> filters) {
        return vehicles.stream()
                .filter(v -> filters.get("year").isEmpty() ||
                        String.valueOf(v.getYear()).contains(filters.get("year")))
                .filter(v -> filters.get("make").isEmpty() ||
                        v.getMake().toLowerCase().contains(filters.get("make").toLowerCase()))
                .filter(v -> filters.get("model").isEmpty() ||
                        v.getModel().toLowerCase().contains(filters.get("model").toLowerCase()))
                .filter(v -> filters.get("fuel").isEmpty() ||
                        (v.getFuelTypePrimary() != null &&
                                v.getFuelTypePrimary().equalsIgnoreCase(filters.get("fuel"))))
                .collect(Collectors.toList());
    }

    private void showSearchResults(List<Vehicle> results) {
        if (results.isEmpty()) {
            compareView.showMessage("No matching vehicles found", "No Results", JOptionPane.INFORMATION_MESSAGE);
        } else {
            Vehicle selected = selectVehicleFromResults(results);
            if (selected != null) {
                setVehicleB(selected);
            }
        }
    }

}