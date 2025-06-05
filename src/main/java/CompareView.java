import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class CompareView extends JFrame {
    private JTextField searchField;
    private JTextArea vehicleADescArea;
    private JTextArea vehicleBDescArea;
    private VINDecoderCompare controller;

    public CompareView(VINDecoderCompare controller) {
        this.controller = controller;
        setupUI();
    }

    private void setupUI() {
        setTitle("Compare View");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top Panel with back button
        JButton backButton = new JButton("<- Back");
        backButton.addActionListener(e -> controller.returnToMainView());

        JButton analyzeButton = new JButton("Analyze Comparison");
        analyzeButton.addActionListener(e -> controller.performComparison());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(analyzeButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> controller.handleSearch(searchField.getText()));

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        add(searchPanel, BorderLayout.SOUTH);

        // Comparison Panel
        JPanel comparisonPanel = new JPanel(new GridLayout(1, 2));
        vehicleADescArea = createDescriptionArea("Vehicle A");
        vehicleBDescArea = createDescriptionArea("Vehicle B (Select a vehicle to compare)");

        comparisonPanel.add(new JScrollPane(vehicleADescArea));
        comparisonPanel.add(new JScrollPane(vehicleBDescArea));
        add(comparisonPanel, BorderLayout.CENTER);

        // Reselect Button
        JButton reselectButton = new JButton("Reselect Vehicle");
        reselectButton.addActionListener(e -> controller.reselectVehicleB());

        JPanel button1Panel = new JPanel(new FlowLayout());
        button1Panel.add(reselectButton);
        button1Panel.add(analyzeButton);

        topPanel.add(button1Panel, BorderLayout.EAST);
    }

    public String getSearchFieldText() {
        return searchField.getText();
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
        scrollPane.setPreferredSize(new Dimension(900, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Comparison Result", JOptionPane.PLAIN_MESSAGE);
    }
}