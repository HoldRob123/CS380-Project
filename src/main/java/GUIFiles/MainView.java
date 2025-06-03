package GUIFiles;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainView extends JFrame {
    private JTextField searchField;
    private JPanel filterPanel;
    private JPanel resultPanel;
    private JScrollPane scrollPane;
    private List<GUIFiles.Vehicle> vehicleDatabase;
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
        vehicleDatabase.add(new GUIFiles.Vehicle(
                "934892HAD84R319573",
                "Project Car",
                "Honda",
                "Accord",
                2002,
                true,
                "EX",
                "Sedan",
                "Coupe",
                4,
                "Gasoline",
                "FWD",
                "K24A4",
                4,
                2.4,
                "Automatic",
                5,
                "Japan",
                "Honda Motor Co",
                "3501-4000 lbs",
                2,
                5
        ));

        vehicleDatabase.add(new GUIFiles.Vehicle(
                "548622G0BU6381355",
                "Lucy",
                "Honda",
                "Civic",
                1997,
                true,
                "DX",
                "Sedan",
                "Sedan",
                4,
                "Gasoline",
                "FWD",
                "D16Y7",
                4,
                1.6,
                "Manual",
                5,
                "USA",
                "Honda Mfg",
                "3001-3500 lbs",
                2,
                5
        ));

        vehicleDatabase.add(new GUIFiles.Vehicle(
                "9101TGG873HS22884",
                "", // No nickname
                "Toyota",
                "Corolla",
                2005,
                false,
                "LE",
                "Sedan",
                "Sedan",
                4,
                "Gasoline",
                "FWD",
                "1ZZ-FE",
                4,
                1.8,
                "Automatic",
                4,
                "USA",
                "Toyota Motor Corp",
                "3001-3500 lbs",
                2,
                5
        ));

        vehicleDatabase.add(new GUIFiles.Vehicle(
                "9101TGG873HS22884",
                "", // No nickname
                "Toyota",
                "Corolla",
                2005,
                false,
                "LE",
                "Sedan",
                "Sedan",
                4,
                "Gasoline",
                "FWD",
                "1ZZ-FE",
                4,
                1.8,
                "Automatic",
                4,
                "USA",
                "Toyota Motor Corp",
                "3001-3500 lbs",
                2,
                5
        ));

        vehicleDatabase.add(new GUIFiles.Vehicle(
                "9101TGG873HS22884",
                "", // No nickname
                "Toyota",
                "Corolla",
                2005,
                false,
                "LE",
                "Sedan",
                "Sedan",
                4,
                "Gasoline",
                "FWD",
                "1ZZ-FE",
                4,
                1.8,
                "Automatic",
                4,
                "USA",
                "Toyota Motor Corp",
                "3001-3500 lbs",
                2,
                5
        ));

        vehicleDatabase.add(new GUIFiles.Vehicle(
                "9101TGG873HS22884",
                "", // No nickname
                "Toyota",
                "Corolla",
                2005,
                false,
                "LE",
                "Sedan",
                "Sedan",
                4,
                "Gasoline",
                "FWD",
                "1ZZ-FE",
                4,
                1.8,
                "Automatic",
                4,
                "USA",
                "Toyota Motor Corp",
                "3001-3500 lbs",
                2,
                5
        ));

        vehicleDatabase.add(new GUIFiles.Vehicle(
                "9101TGG873HS22884",
                "", // No nickname
                "Toyota",
                "Corolla",
                2005,
                false,
                "LE",
                "Sedan",
                "Sedan",
                4,
                "Gasoline",
                "FWD",
                "1ZZ-FE",
                4,
                1.8,
                "Automatic",
                4,
                "USA",
                "Toyota Motor Corp",
                "3001-3500 lbs",
                2,
                5
        ));

        vehicleDatabase.add(new GUIFiles.Vehicle(
                "9101TGG873HS22884",
                "", // No nickname
                "Toyota",
                "Corolla",
                2005,
                false,
                "LE",
                "Sedan",
                "Sedan",
                4,
                "Gasoline",
                "FWD",
                "1ZZ-FE",
                4,
                1.8,
                "Automatic",
                4,
                "USA",
                "Toyota Motor Corp",
                "3001-3500 lbs",
                2,
                5
        ));

        vehicleDatabase.add(new GUIFiles.Vehicle(
                "9101TGG873HS22884",
                "", // No nickname
                "Toyota",
                "Corolla",
                2005,
                false,
                "LE",
                "Sedan",
                "Sedan",
                4,
                "Gasoline",
                "FWD",
                "1ZZ-FE",
                4,
                1.8,
                "Automatic",
                4,
                "USA",
                "Toyota Motor Corp",
                "3001-3500 lbs",
                2,
                5
        ));

        vehicleDatabase.add(new GUIFiles.Vehicle(
                "9101TGG873HS22884",
                "", // No nickname
                "Toyota",
                "Corolla",
                2005,
                false,
                "LE",
                "Sedan",
                "Sedan",
                4,
                "Gasoline",
                "FWD",
                "1ZZ-FE",
                4,
                1.8,
                "Automatic",
                4,
                "USA",
                "Toyota Motor Corp",
                "3001-3500 lbs",
                2,
                5
        ));

        confirmButton.addActionListener(e -> performSearch());

        setVisible(true);
    }

    private void performSearch() {
        String query = searchField.getText().toLowerCase();
        resultPanel.removeAll();

        List<GUIFiles.Vehicle> results = vehicleDatabase.stream()
                .filter(v -> v.getVIN().toLowerCase().contains(query)
                        || v.getMake().toLowerCase().contains(query)
                        || v.getModel().toLowerCase().contains(query)
                        || String.valueOf(v.getYear()).contains(query)
                        || (v.getNickname() != null && v.getNickname().toLowerCase().contains(query)))
                .filter(v -> {
                    boolean yearMatch = yearBox.getSelectedItem().equals("Any") || String.valueOf(v.getYear()).equals(yearBox.getSelectedItem());
                    boolean makeMatch = makeBox.getSelectedItem().equals("Any") || v.getMake().equalsIgnoreCase((String) makeBox.getSelectedItem());
                    boolean modelMatch = modelBox.getSelectedItem().equals("Any") || v.getModel().equalsIgnoreCase((String) modelBox.getSelectedItem());
                    return yearMatch && makeMatch && modelMatch;
                })
                .collect(Collectors.toList());

        for (GUIFiles.Vehicle vehicle : results) {
            JPanel vehicleCard = new JPanel(new BorderLayout());
            vehicleCard.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            vehicleCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            if (vehicle.getNickname() != null && !vehicle.getNickname().isEmpty()) {
                textPanel.add(new JLabel("Nickname: " + vehicle.getNickname()));
            }
            textPanel.add(new JLabel("VIN: " + vehicle.getVIN()));
            textPanel.add(new JLabel("Make: " + vehicle.getMake()));
            textPanel.add(new JLabel("Model: " + vehicle.getModel()));
            textPanel.add(new JLabel("Year: " + vehicle.getYear()));

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
        if (vehicle.getNickname() != null && !vehicle.getNickname().isEmpty()) {
            JMenuItem editName = new JMenuItem("Edit Name");
            editName.addActionListener(e -> {
                String newName = JOptionPane.showInputDialog(this, "Enter new nickname:", vehicle.getNickname());
                if (newName != null && !newName.trim().isEmpty()) {
                    vehicle.setNickname(newName.trim());
                    JOptionPane.showMessageDialog(this, "Nickname updated to: " + newName);
                    performSearch();
                }
            });
            menu.add(editName);

            JMenuItem compare = new JMenuItem("Compare Vehicle");
            compare.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> new CompareView(vehicle));
            });
            menu.add(compare);


            JMenuItem info = new JMenuItem("Full Information");
            info.addActionListener(e -> {
                JOptionPane.showMessageDialog(this, vehicle.fullDescription(), "Vehicle Info", JOptionPane.INFORMATION_MESSAGE);
            });
            menu.add(info);

            JMenuItem remove = new JMenuItem("Remove from Saved");
            remove.addActionListener(e -> {
                vehicle.setIsSaved(false);
                JOptionPane.showMessageDialog(this, vehicle.getNickname() + " removed from saved.");
                performSearch();
            });
            menu.add(remove);
        } else {
            JMenuItem compare = new JMenuItem("Compare Vehicle");
            compare.addActionListener(e -> {
                JOptionPane.showMessageDialog(this, "Comparison feature not implemented yet for " + vehicle.getVIN());
            });
            menu.add(compare);

            JMenuItem info = new JMenuItem("Full Information");
            info.addActionListener(e -> {
                JOptionPane.showMessageDialog(this, vehicle.fullDescription(), "Vehicle Info", JOptionPane.INFORMATION_MESSAGE);
            });
            menu.add(info);
        }
        menu.show(invoker, invoker.getWidth() / 2, invoker.getHeight() / 2);
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
}
