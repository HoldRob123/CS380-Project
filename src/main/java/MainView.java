import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainView extends JFrame {
    private JTextField searchField;
    private JButton confirmButton, filterButton;
    private JPanel filterPanel, resultPanel;
    private JScrollPane scrollPane;

    private JTextField yearBox, makeBox, modelBox, countryBox;
    private JComboBox gasType;
    private JCheckBox savedOnly;

    private VINDecoderMain mainApp;  // Reference to main app logic

    public MainView(VINDecoderMain mainApp) {
        this(); // call default constructor for GUI setup
        this.mainApp = mainApp;
    }

    public MainView() {
        setTitle("vBreed Main Screen");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 600);
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
        scrollPane.setBounds(20, 100, 540, 440);
        add(scrollPane);

        // Event listeners
        filterButton.addActionListener(e -> filterPanel.setVisible(!filterPanel.isVisible()));
        confirmButton.addActionListener(e -> {
            if (mainApp != null) {
                mainApp.performSearch();
            }
        });
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(null);
        panel.setBounds(300, 60, 220, 220);
        panel.setBorder(BorderFactory.createTitledBorder("Filter Menu"));
        panel.setVisible(false);

        JLabel yearLabel = new JLabel("Year:");
        yearLabel.setBounds(10, 20, 100, 20);
        panel.add(yearLabel);

        yearBox = new JTextField();
        yearBox.setBounds(100, 20, 100, 20);
        panel.add(yearBox);

        JLabel makeLabel = new JLabel("Make:");
        makeLabel.setBounds(10, 50, 100, 20);
        panel.add(makeLabel);

        makeBox = new JTextField();
        makeBox.setBounds(100, 50, 100, 20);
        panel.add(makeBox);

        JLabel modelLabel = new JLabel("Model:");
        modelLabel.setBounds(10, 80, 100, 20);
        panel.add(modelLabel);

        modelBox = new JTextField();
        modelBox.setBounds(100, 80, 100, 20);
        panel.add(modelBox);

        JLabel countryLabel = new JLabel("Country:");
        countryLabel.setBounds(10, 110, 100, 20);
        panel.add(countryLabel);

        countryBox = new JTextField();
        countryBox.setBounds(100, 110, 100, 20);
        panel.add(countryBox);

        JLabel fuelLabel = new JLabel("Fuel:");
        fuelLabel.setBounds(10, 140, 100, 20);
        panel.add(fuelLabel);

        gasType = new JComboBox<>(new String[]{"", "gas", "diesel", "ev"});
        gasType.setBounds(100, 140, 70, 20);
        panel.add(gasType);

        savedOnly = new JCheckBox("Saved only");
        savedOnly.setBounds(10, 170, 150, 20);
        panel.add(savedOnly);

        return panel;
    }

    // Getters for fields that VINDecoderMain will need

    public JTextField getSearchField() {
        return searchField;
    }

    public JPanel getResultPanel() {
        return resultPanel;
    }

    public JTextField getYearBox() {
        return yearBox;
    }

    public JTextField getMakeBox() {
        return makeBox;
    }

    public JTextField getModelBox() {
        return modelBox;
    }

    public JTextField getCountryBox() {
        return countryBox;
    }

    public JComboBox getGasType() {
        return gasType;
    }

    public JCheckBox getSavedOnly() {
        return savedOnly;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }
}