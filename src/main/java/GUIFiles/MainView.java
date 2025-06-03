package GUIFiles;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainView extends JFrame {
    private JTextField searchField;
    private JPanel filterPanel;
    private JPanel resultPanel;
    private JScrollPane scrollPane;
    private List<Vehicle> vehicleDatabase;
    private JComboBox<String> yearBox, makeBox, modelBox;
    private JCheckBox gasBox, dieselBox, evBox;

    public MainView() {
        setTitle("Main View with Search");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top search panel
        JPanel topPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        JButton confirmButton = new JButton("Confirm");
        topPanel.add(searchField, BorderLayout.CENTER);
        topPanel.add(confirmButton, BorderLayout.WEST);

        // Filter button panel
        filterPanel = new JPanel();
        JButton filterButton = new JButton("\u2630");
        filterButton.setPreferredSize(new Dimension(40, 30));
        filterButton.addActionListener(e -> showFilterPopup());
        filterPanel.add(filterButton);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(topPanel, BorderLayout.CENTER);
        topContainer.add(filterPanel, BorderLayout.EAST);
        add(topContainer, BorderLayout.NORTH);

        // Search results
        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(resultPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Fake data
        vehicleDatabase = new ArrayList<>();
        vehicleDatabase.add(new Vehicle("VIN: 934892HAD84R319573", "Honda", "Accord", "2002", ""));
        vehicleDatabase.add(new Vehicle("VIN: 548622G0BU6381355", "Honda", "Civic", "1997", "Lucy"));
        vehicleDatabase.add(new Vehicle("VIN: 9101TGG873HS22884", "Toyota", "Corolla", "2005", ""));

        confirmButton.addActionListener(e -> performSearch());

        setVisible(true);
    }

    private void performSearch() {
        String query = searchField.getText().toLowerCase();
        resultPanel.removeAll();

        List<Vehicle> results = vehicleDatabase.stream()
                .filter(v -> v.matchesQuery(query))
                .filter(v -> {
                    boolean yearMatch = yearBox.getSelectedItem().equals("Any") || v.year.equals(yearBox.getSelectedItem());
                    boolean makeMatch = makeBox.getSelectedItem().equals("Any") || v.make.equalsIgnoreCase((String) makeBox.getSelectedItem());
                    boolean modelMatch = modelBox.getSelectedItem().equals("Any") || v.model.equalsIgnoreCase((String) modelBox.getSelectedItem());
                    return yearMatch && makeMatch && modelMatch;
                })
                .collect(Collectors.toList());

        for (Vehicle vehicle : results) {
            JPanel vehicleCard = new JPanel(new BorderLayout());
            vehicleCard.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            vehicleCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            if (!vehicle.nickname.isEmpty()) {
                textPanel.add(new JLabel("Nickname: " + vehicle.nickname));
            }
            textPanel.add(new JLabel(vehicle.vin));
            textPanel.add(new JLabel("Make: " + vehicle.make));
            textPanel.add(new JLabel("Model: " + vehicle.model));
            textPanel.add(new JLabel("Year: " + vehicle.year));

            JButton moreOptions = new JButton("...");
            moreOptions.setPreferredSize(new Dimension(50, 30));
            moreOptions.addActionListener(e -> showVehicleOptions(vehicle, moreOptions));
            vehicleCard.add(textPanel, BorderLayout.CENTER);
            vehicleCard.add(moreOptions, BorderLayout.EAST);

            resultPanel.add(vehicleCard);
        }

        resultPanel.revalidate();
        resultPanel.repaint();
    }

    private void showVehicleOptions(Vehicle vehicle, Component invoker) {
        JPopupMenu menu = new JPopupMenu();
        if (!vehicle.nickname.isEmpty()) {
            JMenuItem editName = new JMenuItem("Edit Name");
            editName.addActionListener(e -> JOptionPane.showMessageDialog(this, "Edit name clicked for " + vehicle.nickname));
            menu.add(editName);

            JMenuItem compare = new JMenuItem("Compare Vehicle");
            compare.addActionListener(e -> JOptionPane.showMessageDialog(this, "Compare clicked for " + vehicle.nickname));
            menu.add(compare);

            JMenuItem info = new JMenuItem("Full Information");
            info.addActionListener(e -> JOptionPane.showMessageDialog(this, vehicle.toString()));
            menu.add(info);

            JMenuItem remove = new JMenuItem("Remove from Saved");
            remove.addActionListener(e -> JOptionPane.showMessageDialog(this, vehicle.nickname + " removed from saved."));
            menu.add(remove);
        } else {
            JMenuItem compare = new JMenuItem("Compare Vehicle");
            compare.addActionListener(e -> JOptionPane.showMessageDialog(this, "Compare clicked for " + vehicle.vin));
            menu.add(compare);

            JMenuItem info = new JMenuItem("Full Information");
            info.addActionListener(e -> JOptionPane.showMessageDialog(this, vehicle.toString()));
            menu.add(info);
        }
        menu.show(invoker, invoker.getWidth()/2, invoker.getHeight()/2);
    }

    private void showFilterPopup() {
        JFrame popup = new JFrame("Filter Menu");
        popup.setSize(300, 400);
        popup.setLocationRelativeTo(this);
        JPanel filterOptions = new JPanel();
        filterOptions.setLayout(new GridLayout(0, 2));

        filterOptions.add(new JLabel("Year:"));
        yearBox = new JComboBox<>(new String[]{"Any", "1997", "2002", "2005"});
        filterOptions.add(yearBox);

        filterOptions.add(new JLabel("Make:"));
        makeBox = new JComboBox<>(new String[]{"Any", "Honda", "Toyota"});
        filterOptions.add(makeBox);

        filterOptions.add(new JLabel("Model:"));
        modelBox = new JComboBox<>(new String[]{"Any", "Accord", "Civic", "Corolla"});
        filterOptions.add(modelBox);

        filterOptions.add(new JLabel("Fuel Type:"));
        JPanel fuelPanel = new JPanel();
        gasBox = new JCheckBox("Gas");
        dieselBox = new JCheckBox("Diesel");
        evBox = new JCheckBox("EV");
        fuelPanel.add(gasBox);
        fuelPanel.add(dieselBox);
        fuelPanel.add(evBox);
        filterOptions.add(fuelPanel);

        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(e -> {
            popup.dispose();
            performSearch();
        });
        filterOptions.add(applyButton);

        popup.add(filterOptions);
        popup.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainView::new);
    }

    class Vehicle {
        String vin;
        String make;
        String model;
        String year;
        String nickname;

        public Vehicle(String vin, String make, String model, String year, String nickname) {
            this.vin = vin;
            this.make = make;
            this.model = model;
            this.year = year;
            this.nickname = nickname;
        }

        public boolean matchesQuery(String query) {
            return vin.toLowerCase().contains(query) ||
                    make.toLowerCase().contains(query) ||
                    model.toLowerCase().contains(query) ||
                    year.toLowerCase().contains(query) ||
                    nickname.toLowerCase().contains(query);
        }

        @Override
        public String toString() {
            return (nickname.isEmpty() ? "" : "Nickname: " + nickname + "\n") +
                    vin + "\nMake: " + make + "\nModel: " + model + "\nYear: " + year;
        }
    }
}
