import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

// TODO: TRANSFER FROM FAKE VEHICLES TO REAL VEHICLES
public class VINDecoderCompare {
    private Vehicle vehicleA;
    private Vehicle vehicleB;
    private CompareView compareView;
    private final VINDecoderMain mainApp;

    public VINDecoderCompare(VINDecoderMain mainApp) {
        this.mainApp = mainApp;
    }

    public void run(Vehicle vehicleA) {
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

    public void reselectVehicleB() {
        if (vehicleA == null) return;

        String currentSearch = compareView.getSearchFieldText();
        if (currentSearch != null && !currentSearch.isEmpty()) {
            handleSearch(currentSearch);
        } else {
            compareView.showMessage("Please enter a search term first",
                    "Search Needed", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void returnToMainView() {
        if (compareView != null) {
            compareView.dispose();
        }
        mainApp.getMainView().setVisible(true);
    }

    public void handleSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            compareView.showMessage("Please enter a search term", "Search Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        mainApp.getMainView().getSearchField().setText(query);
        mainApp.performSearch();

        List<Vehicle> results = mainApp.getVehicleDatabase().stream()
                .filter(v -> !v.getVIN().equals(vehicleA.getVIN()))
                .filter(v -> matchesSearchCriteria(v, query))
                .collect(Collectors.toList());

        if (results.isEmpty()) {
            compareView.showMessage("No matching vehicles found", "No Results", JOptionPane.INFORMATION_MESSAGE);
        } else {
            Vehicle selected = selectVehicleFromResults(results);
            if (selected != null) {
                setVehicleB(selected);
            }
        }
    }

    private boolean matchesSearchCriteria(Vehicle vehicle, String query) {
        String q = query.toLowerCase();
        return vehicle.getVIN().toLowerCase().contains(q)
                || vehicle.getMake().toLowerCase().contains(q)
                || vehicle.getModel().toLowerCase().contains(q)
                || String.valueOf(vehicle.getYear()).contains(q)
                || (vehicle.getNickname() != null && vehicle.getNickname().toLowerCase().contains(q));
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
        result.append(String.format("%-30s | %-30s | %-30s%n", "Attribute", "Vehicle A", "Vehicle B"));
        result.append("-".repeat(95)).append("\n");

        // Compare all attributes
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

    private void compareAttribute(StringBuilder result, String attribute, String valueA, String valueB) {
        String highlight = valueA.equals(valueB) ? "" : "*";
        result.append(String.format("%-30s | %-30s | %-30s%n",
                highlight + attribute + highlight,
                highlight + (valueA != null ? valueA : "N/A") + highlight,
                highlight + (valueB != null ? valueB : "N/A") + highlight));
    }

    // Getter for vehicle database from parent class

    public Vehicle getVehicleA() {
        return vehicleA;
    }

    public Vehicle getVehicleB() {
        return vehicleB;
    }
}