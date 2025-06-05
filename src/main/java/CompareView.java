
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CompareView extends JFrame {

    private JTextField searchField;
    private JTextArea withListingArea;
    private JPanel comparePanel;
    private JLabel compareLabel;
    private Vehicle compareVehicle;

    public CompareView(Vehicle compareVehicle) {
        this.compareVehicle = compareVehicle;

        setTitle("Compare View");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  // dispose only, so main view stays
        setSize(600, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        comparePanel = new JPanel();
        comparePanel.setLayout(new BoxLayout(comparePanel, BoxLayout.Y_AXIS));
        comparePanel.setBorder(BorderFactory.createTitledBorder("COMPARE"));

        // Format vehicle info nicely for label (multi-line label with HTML)
        String compareInfo = "<html>VIN: " + compareVehicle.getVIN() + "<br>" +
                "Make: " + compareVehicle.getMake() + "<br>" +
                "Model: " + compareVehicle.getModel() + "<br>" +
                "Year: " + compareVehicle.getYear() + "</html>";
        compareLabel = new JLabel(compareInfo);
        comparePanel.add(compareLabel);

        JPanel withPanel = new JPanel(new BorderLayout());
        withPanel.setBorder(BorderFactory.createTitledBorder("WITH"));

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        JButton confirmButton = new JButton("Confirm");

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(confirmButton, BorderLayout.EAST);

        withListingArea = new JTextArea();
        withListingArea.setEditable(false);
        withListingArea.setText("Type to search for vehicles to compare with...");

        JScrollPane scrollPane = new JScrollPane(withListingArea);

        withPanel.add(searchPanel, BorderLayout.NORTH);
        withPanel.add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("<- Back");
        backButton.addActionListener(e -> this.dispose());

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
                // TODO: Implement real search logic here
                withListingArea.setText("Showing results for: " + query +
                        "\n\nVIN: 548622G06UJG318535\nMake: Honda\nModel: Civic\nYear: 1997\n\n[select]");
            }
        });
    }
}