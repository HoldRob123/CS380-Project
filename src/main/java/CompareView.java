import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CompareView extends JFrame {
    private JTextField searchField;
    private JTextArea vehicleADescArea;
    private JTextArea vehicleBDescArea;
    private JPanel filterPanel;
    private JComboBox<String> gasType;
    private JTextField yearField, makeField, modelField;
    private final VINDecoderCompare controller;

    public CompareView(VINDecoderCompare controller) {
        this.controller = controller;
        setupUI();
    }

    private void setupUI() {
        setTitle("Compare View");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 700);
        setLayout(new BorderLayout());

        // Top panel with back button and action buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("← Back");
        backButton.addActionListener(e -> controller.returnToMainView());
        topPanel.add(backButton, BorderLayout.WEST);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton analyzeButton = new JButton("Analyze Comparison");
        analyzeButton.addActionListener(e -> controller.performComparison());
        buttonPanel.add(analyzeButton);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Filter panel (initially hidden)
        filterPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filters"));
        filterPanel.setVisible(false);

        yearField = new JTextField();
        makeField = new JTextField();
        modelField = new JTextField();
        gasType = new JComboBox<>(new String[]{"", "Gasoline", "Diesel", "Electric", "Hybrid"});

        filterPanel.add(new JLabel("Year:"));
        filterPanel.add(yearField);
        filterPanel.add(new JLabel("Make:"));
        filterPanel.add(makeField);
        filterPanel.add(new JLabel("Model:"));
        filterPanel.add(modelField);
        filterPanel.add(new JLabel("Fuel Type:"));
        filterPanel.add(gasType);

        // Search panel with toggle filter button
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> performSearch());

        JButton toggleFilterButton = new JButton("Filters ▼");
        toggleFilterButton.addActionListener(e -> {
            filterPanel.setVisible(!filterPanel.isVisible());
            toggleFilterButton.setText(filterPanel.isVisible() ? "Filters ▲" : "Filters ▼");
            pack();
        });

        JPanel searchControls = new JPanel(new BorderLayout());
        searchControls.add(searchButton, BorderLayout.EAST);
        searchControls.add(toggleFilterButton, BorderLayout.WEST);

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchControls, BorderLayout.EAST);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(searchPanel, BorderLayout.NORTH);
        southPanel.add(filterPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        // Comparison panels
        JPanel comparisonPanel = new JPanel(new GridLayout(1, 2));
        vehicleADescArea = createDescriptionArea("Vehicle A");
        vehicleBDescArea = createDescriptionArea("Vehicle B (Select a vehicle to compare)");
        comparisonPanel.add(new JScrollPane(vehicleADescArea));
        comparisonPanel.add(new JScrollPane(vehicleBDescArea));
        add(comparisonPanel, BorderLayout.CENTER);
    }

    private void performSearch() {
        Map<String, String> filters = new HashMap<>();
        filters.put("year", yearField.getText().trim());
        filters.put("make", makeField.getText().trim());
        filters.put("model", modelField.getText().trim());
        filters.put("fuel", gasType.getSelectedItem().toString());

        String query = searchField.getText().trim();

        if (!query.isEmpty()) {
            controller.handleSearch(query, filters);
        } else {
            controller.handleFilterSearch(filters);
        }
    }

    private JTextArea createDescriptionArea(String title) {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createTitledBorder(title));
        return area;
    }

    public void displayVehicleA(Vehicle vehicle) {
        vehicleADescArea.setText(vehicle.fullDescription());
        vehicleADescArea.setBorder(BorderFactory.createTitledBorder(
                "Vehicle A: " + vehicle.getYear() + " " + vehicle.getMake() + " " + vehicle.getModel()));
    }

    public void displayVehicleB(Vehicle vehicle) {
        vehicleBDescArea.setText(vehicle.fullDescription());
        vehicleBDescArea.setBorder(BorderFactory.createTitledBorder(
                "Vehicle B: " + vehicle.getYear() + " " + vehicle.getMake() + " " + vehicle.getModel()));
    }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public void showComparisonResult(String result) {
        JTextArea textArea = new JTextArea(result);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(1200, 700));

        JOptionPane.showMessageDialog(this, scrollPane, "Comparison Result", JOptionPane.PLAIN_MESSAGE);
    }

    public String getSearchFieldText() {
        return searchField.getText();
    }
}