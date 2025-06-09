import javax.swing.*;

public class MainView extends JFrame {
    private JTextField searchField;
    private JButton confirmButton, filterButton;
    private JPanel filterPanel, resultPanel;
    private JScrollPane scrollPane;

    private JTextField yearBox, makeBox, modelBox;
    private JCheckBox savedOnly;

    public MainView(VINDecoderMain mainApp) {
        setTitle("VBG GO! Main Screen - " + mainApp.currentUser);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setLayout(null);

        JLayeredPane layeredPane = getLayeredPane();

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setBounds(20, 20, 60, 25);
        layeredPane.add(searchLabel, JLayeredPane.DEFAULT_LAYER);

        searchField = new JTextField();
        searchField.setBounds(80, 20, 400, 25);
        layeredPane.add(searchField, JLayeredPane.DEFAULT_LAYER);

        confirmButton = new JButton("Confirm");
        confirmButton.setBounds(80, 60, 100, 25);
        layeredPane.add(confirmButton, JLayeredPane.DEFAULT_LAYER);

        filterButton = new JButton("Filter");
        filterButton.setBounds(190, 60, 100, 25);
        layeredPane.add(filterButton, JLayeredPane.DEFAULT_LAYER);

        filterPanel = createFilterPanel();
        layeredPane.add(filterPanel, JLayeredPane.POPUP_LAYER); // Keeps it always on top

        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));

        scrollPane = new JScrollPane(resultPanel);
        scrollPane.setBounds(20, 100, 540, 440);
        layeredPane.add(scrollPane, JLayeredPane.DEFAULT_LAYER);

        // Event listeners
        filterButton.addActionListener(e -> filterPanel.setVisible(!filterPanel.isVisible()));
        confirmButton.addActionListener(e -> {
            if (!searchField.getText().isEmpty() || hasActiveFilters()) {
                mainApp.performSearch();
            }
        });
    }

    private boolean hasActiveFilters() {
        return !yearBox.getText().isEmpty() || !makeBox.getText().isEmpty()
                || !modelBox.getText().isEmpty() || savedOnly.isSelected();
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(null);
        panel.setBounds(300, 60, 220, 160);
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

        savedOnly = new JCheckBox("Saved only");
        savedOnly.setBounds(10, 110, 150, 20);
        panel.add(savedOnly);

        return panel;
    }

    // Getters for VINDecoderMain use
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

    public JCheckBox getSavedOnly() {
        return savedOnly;
    }
}
