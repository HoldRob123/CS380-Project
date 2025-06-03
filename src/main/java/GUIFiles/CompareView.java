package GUIFiles;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CompareView extends JFrame {

    private JTextField searchField;
    private JTextArea withListingArea;
    private JPanel comparePanel;
    private JLabel compareLabel;

    public CompareView() {
        setTitle("Compare View");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top area - Compare section
        comparePanel = new JPanel();
        comparePanel.setLayout(new BoxLayout(comparePanel, BoxLayout.Y_AXIS));
        comparePanel.setBorder(BorderFactory.createTitledBorder("COMPARE"));
        compareLabel = new JLabel("VIN: 9348H2D4BA4R519573\nMake: Honda\nModel: Accord\nYear: 2002");
        comparePanel.add(compareLabel);

        // Center area - With section
        JPanel withPanel = new JPanel(new BorderLayout());
        withPanel.setBorder(BorderFactory.createTitledBorder("WITH"));

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        JButton confirmButton = new JButton("Confirm");

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(confirmButton, BorderLayout.EAST);

        withListingArea = new JTextArea();
        withListingArea.setEditable(false);
        withListingArea.setText("VIN: 548622G06UJG318535\nMake: Honda\nModel: Civic\nYear: 1997\n\n[select]");

        JScrollPane scrollPane = new JScrollPane(withListingArea);

        withPanel.add(searchPanel, BorderLayout.NORTH);
        withPanel.add(scrollPane, BorderLayout.CENTER);

        // Back button
        JButton backButton = new JButton("<-");
        backButton.addActionListener(e -> {
            // Return to main screen
            this.dispose();
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(comparePanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(withPanel, BorderLayout.CENTER);

        setVisible(true);

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = searchField.getText();
                // TODO: Replace with real search
                withListingArea.setText("Showing results for: " + query +
                        "\n\nVIN: 548622G06UJG318535\nMake: Honda\nModel: Civic\nYear: 1997\n\n[select]");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CompareView::new);
    }
}
