package GUIFiles;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class MainView extends JFrame {
    private JTextField searchField;
    private JButton confirmButton, filterButton;
    private JPanel filterPanel, resultPanel;

    public MainView() {
        setTitle("vBreed Main Screen");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 500);
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
        filterButton.setBounds(410, 60, 70, 25);
        add(filterButton);

        filterPanel = new JPanel();
        filterPanel.setLayout(null);
        filterPanel.setBounds(480, 60, 180, 230);
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter Menu"));
        filterPanel.setVisible(false);

        JLabel yearLabel = new JLabel("Year:");
        yearLabel.setBounds(10, 20, 100, 20);
        filterPanel.add(yearLabel);

        JComboBox<String> yearBox = new JComboBox<>(new String[]{"", "2020", "2021", "2022"});
        yearBox.setBounds(70, 20, 100, 20);
        filterPanel.add(yearBox);

        JLabel makeLabel = new JLabel("Make:");
        makeLabel.setBounds(10, 50, 100, 20);
        filterPanel.add(makeLabel);

        JComboBox<String> makeBox = new JComboBox<>(new String[]{"", "Honda", "Toyota"});
        makeBox.setBounds(70, 50, 100, 20);
        filterPanel.add(makeBox);

        JLabel modelLabel = new JLabel("Model:");
        modelLabel.setBounds(10, 80, 100, 20);
        filterPanel.add(modelLabel);

        JComboBox<String> modelBox = new JComboBox<>(new String[]{"", "Civic", "Accord"});
        modelBox.setBounds(70, 80, 100, 20);
        filterPanel.add(modelBox);

        JLabel countryLabel = new JLabel("Country:");
        countryLabel.setBounds(10, 110, 100, 20);
        filterPanel.add(countryLabel);

        JComboBox<String> countryBox = new JComboBox<>(new String[]{"", "USA", "Japan"});
        countryBox.setBounds(70, 110, 100, 20);
        filterPanel.add(countryBox);

        JLabel fuelLabel = new JLabel("Fuel:");
        fuelLabel.setBounds(10, 140, 100, 20);
        filterPanel.add(fuelLabel);

        JCheckBox gas = new JCheckBox("Gas");
        gas.setBounds(70, 140, 50, 20);
        filterPanel.add(gas);

        JCheckBox diesel = new JCheckBox("Diesel");
        diesel.setBounds(70, 160, 70, 20);
        filterPanel.add(diesel);

        JCheckBox ev = new JCheckBox("EV");
        ev.setBounds(70, 180, 50, 20);
        filterPanel.add(ev);

        JCheckBox savedOnly = new JCheckBox("Saved only");
        savedOnly.setBounds(10, 200, 120, 20);
        filterPanel.add(savedOnly);

        add(filterPanel);

        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(resultPanel);
        scrollPane.setBounds(20, 300, 650, 150);
        add(scrollPane);

        filterButton.addActionListener(e -> filterPanel.setVisible(!filterPanel.isVisible()));
    }

    private void displaySearchResults(String query) {
        resultPanel.removeAll();
        if (query != null && !query.trim().isEmpty()) {
            for (int i = 1; i <= 3; i++) {
                String vehicleInfo = String.format("%s - 2022 Honda Civic (%s)", query, i);
                JButton resultButton = new JButton(vehicleInfo);
                JPopupMenu menu = new JPopupMenu();
                menu.add(new JMenuItem("Save Vehicle"));
                menu.add(new JMenuItem("Compare Vehicle"));
                menu.add(new JMenuItem("Full Information"));

                resultButton.addActionListener(ev -> menu.show(resultButton, 0, resultButton.getHeight()));
                resultPanel.add(resultButton);
            }
        }
        resultPanel.revalidate();
        resultPanel.repaint();
    }
}
