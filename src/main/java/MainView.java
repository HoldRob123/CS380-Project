import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MainView extends JFrame {
    private JTextField searchField;
    private JButton confirmButton, filterButton;
    private JPanel filterPanel, resultPanel;
    private JScrollPane scrollPane;

    private JComboBox<String> yearBox, makeBox, modelBox, countryBox;
    private JCheckBox gas, diesel, ev, savedOnly;

    private List<Vehicle> vehicleDatabase;

    private VINDecoderMain mainApp;  // Add this field if not already present

    // Add this constructor below your field declarations
    public MainView(VINDecoderMain mainApp) {
        this(); // call the default constructor to reuse the GUI setup
        this.mainApp = mainApp; // store the reference for future use
    }

    public MainView() {
        setTitle("vBreed Main Screen");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setBounds(20, 20, 60, 25);
        add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(80, 20, 400, 25);
        add(searchField);

        confirmButton = new JButton("Confirm");
        confirmButton.setBounds(80, 60, 100, 25);
        add(confirmButton);

        filterButton = new JButton("Filter");
        filterButton.setBounds(190, 60, 100, 25);
        add(filterButton);

        filterPanel = createFilterPanel();
        add(filterPanel);

        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(resultPanel);
        scrollPane.setBounds(20, 100, 740, 440);
        add(scrollPane);

        setupFakeData();

        // Event listeners
        filterButton.addActionListener(e -> filterPanel.setVisible(!filterPanel.isVisible()));
        confirmButton.addActionListener(e -> performSearch());
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(null);
        panel.setBounds(300, 60, 220, 260);
        panel.setBorder(BorderFactory.createTitledBorder("Filter Menu"));
        panel.setVisible(false);

        JLabel yearLabel = new JLabel("Year:");
        yearLabel.setBounds(10, 20, 100, 20);
        panel.add(yearLabel);

        yearBox = new JComboBox<>(new String[]{"", "1997", "2002", "2005"});
        yearBox.setBounds(100, 20, 100, 20);
        panel.add(yearBox);

        JLabel makeLabel = new JLabel("Make:");
        makeLabel.setBounds(10, 50, 100, 20);
        panel.add(makeLabel);

        makeBox = new JComboBox<>(new String[]{"", "Honda", "Toyota"});
        makeBox.setBounds(100, 50, 100, 20);
        panel.add(makeBox);

        JLabel modelLabel = new JLabel("Model:");
        modelLabel.setBounds(10, 80, 100, 20);
        panel.add(modelLabel);

        modelBox = new JComboBox<>(new String[]{"", "Civic", "Accord", "Corolla"});
        modelBox.setBounds(100, 80, 100, 20);
        panel.add(modelBox);

        JLabel countryLabel = new JLabel("Country:");
        countryLabel.setBounds(10, 110, 100, 20);
        panel.add(countryLabel);

        countryBox = new JComboBox<>(new String[]{"", "USA", "Japan"});
        countryBox.setBounds(100, 110, 100, 20);
        panel.add(countryBox);

        JLabel fuelLabel = new JLabel("Fuel:");
        fuelLabel.setBounds(10, 140, 100, 20);
        panel.add(fuelLabel);

        gas = new JCheckBox("Gas");
        gas.setBounds(100, 140, 70, 20);
        panel.add(gas);

        diesel = new JCheckBox("Diesel");
        diesel.setBounds(100, 160, 70, 20);
        panel.add(diesel);

        ev = new JCheckBox("EV");
        ev.setBounds(100, 180, 70, 20);
        panel.add(ev);

        savedOnly = new JCheckBox("Saved only");
        savedOnly.setBounds(10, 210, 150, 20);
        panel.add(savedOnly);

        return panel;
    }

    private void setupFakeData() {
        vehicleDatabase = new ArrayList<>();
        vehicleDatabase.add(new Vehicle("934892HAD84R319573", "Project Car", "Honda", "Accord", 2002, true,
                "EX", "Sedan", "Coupe", 4, "Gasoline", "FWD", "K24A4", 4, 2.4, "Automatic", 5,
                "Japan", "Honda Motor Co", "3501-4000 lbs", 2, 5));
        vehicleDatabase.add(new Vehicle("548622G0BU6381355", "Lucy", "Honda", "Civic", 1997, true,
                "DX", "Sedan", "Sedan", 4, "Gasoline", "FWD", "D16Y7", 4, 1.6, "Manual", 5,
                "USA", "Honda Mfg", "3001-3500 lbs", 2, 5));
        vehicleDatabase.add(new Vehicle("9101TGG873HS22884", "", "Toyota", "Corolla", 2005, false,
                "LE", "Sedan", "Sedan", 4, "Gasoline", "FWD", "1ZZ-FE", 4, 1.8, "Automatic", 4,
                "USA", "Toyota Motor Corp", "3001-3500 lbs", 2, 5));
    }

    private void performSearch() {
        String query = searchField.getText().toLowerCase();
        resultPanel.removeAll();

        List<Vehicle> results = vehicleDatabase.stream()
                .filter(v -> query.isEmpty() || v.getVIN().toLowerCase().contains(query)
                        || v.getMake().toLowerCase().contains(query)
                        || v.getModel().toLowerCase().contains(query)
                        || String.valueOf(v.getYear()).contains(query)
                        || (v.getNickname() != null && v.getNickname().toLowerCase().contains(query)))
                .filter(this::matchesFilter)
                .collect(Collectors.toList());

        for (Vehicle v : results) {
            JPanel card = new JPanel(new BorderLayout());
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
            card.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            JPanel info = new JPanel();
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            if (v.getNickname() != null && !v.getNickname().isEmpty())
                info.add(new JLabel("Nickname: " + v.getNickname()));
            info.add(new JLabel("VIN: " + v.getVIN()));
            info.add(new JLabel("Make: " + v.getMake()));
            info.add(new JLabel("Model: " + v.getModel()));
            info.add(new JLabel("Year: " + v.getYear()));

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

    private boolean matchesFilter(Vehicle v) {
        String year = (String) yearBox.getSelectedItem();
        String make = (String) makeBox.getSelectedItem();
        String model = (String) modelBox.getSelectedItem();
        String country = (String) countryBox.getSelectedItem();

        if (!year.isEmpty() && !String.valueOf(v.getYear()).equals(year)) return false;
        if (!make.isEmpty() && !v.getMake().equalsIgnoreCase(make)) return false;
        if (!model.isEmpty() && !v.getModel().equalsIgnoreCase(model)) return false;
        if (!country.isEmpty() && !v.getCountry().equalsIgnoreCase(country)) return false;

        if (savedOnly.isSelected() && !v.getIsSaved()) return false;

        boolean fuelMatch = !gas.isSelected() && !diesel.isSelected() && !ev.isSelected();
        fuelMatch |= gas.isSelected() && "Gasoline".equalsIgnoreCase(v.getFuelType());
        fuelMatch |= diesel.isSelected() && "Diesel".equalsIgnoreCase(v.getFuelType());
        fuelMatch |= ev.isSelected() && "EV".equalsIgnoreCase(v.getFuelType());

        return fuelMatch;
    }

    private void showVehicleOptions(Vehicle vehicle, Component invoker) {
        JPopupMenu menu = new JPopupMenu();

        if (vehicle.getNickname() != null && !vehicle.getNickname().isEmpty()) {
            JMenuItem edit = new JMenuItem("Edit Name");
            edit.addActionListener(e -> {
                String newName = JOptionPane.showInputDialog(this, "New nickname:", vehicle.getNickname());
                if (newName != null && !newName.trim().isEmpty()) {
                    vehicle.setNickname(newName.trim());
                    JOptionPane.showMessageDialog(this, "Nickname updated.");
                    performSearch();
                }
            });
            menu.add(edit);
        }

        JMenuItem info = new JMenuItem("Full Information");
        info.addActionListener(e -> JOptionPane.showMessageDialog(this, vehicle.fullDescription(), "Vehicle Info", JOptionPane.INFORMATION_MESSAGE));
        menu.add(info);

        JMenuItem compare = new JMenuItem("Compare Vehicle");
        compare.addActionListener(e -> JOptionPane.showMessageDialog(this, "Compare not implemented yet."));
        menu.add(compare);

        JMenuItem remove = new JMenuItem("Remove from Saved");
        remove.addActionListener(e -> {
            vehicle.setIsSaved(false);
            JOptionPane.showMessageDialog(this, "Removed from saved.");
            performSearch();
        });
        menu.add(remove);

        menu.show(invoker, invoker.getWidth() / 2, invoker.getHeight() / 2);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainView::new);
    }
}
