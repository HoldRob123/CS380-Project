<<<<<<< Updated upstream:GUIFiles/LoginScreen.java
package GUIFiles;
=======
// delete me
>>>>>>> Stashed changes:src/LoginScreen.java

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton guestButton, confirmButton;

    public LoginScreen() {
        setTitle("vBreed Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(null);

        JLabel titleLabel = new JLabel("vBreed");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        titleLabel.setBounds(160, 10, 100, 30);
        panel.add(titleLabel);

        JLabel instruction = new JLabel("Type in existing account credentials or log new credentials below:");
        instruction.setBounds(20, 40, 360, 20);
        panel.add(instruction);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(20, 70, 100, 25);
        panel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(120, 70, 200, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(20, 100, 100, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(120, 100, 200, 25);
        panel.add(passwordField);

        guestButton = new JButton("Continue as Guest");
        guestButton.setBounds(40, 150, 150, 30);
        panel.add(guestButton);

        confirmButton = new JButton("Confirm");
        confirmButton.setBounds(210, 150, 110, 30);
        panel.add(confirmButton);

        add(panel);

        guestButton.addActionListener(e -> goToMainScreen());
        confirmButton.addActionListener(e -> attemptLogin());
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username or password fields cannot be left blank.", "Login Error", JOptionPane.ERROR_MESSAGE);
        } else if (username.equals("existingAccount") && password.equals("correctPassword")) {
            goToMainScreen();
        } else if (username.equals("existingAccount")) {
            JOptionPane.showMessageDialog(this, "Someone with this username exists but the password is incorrect.", "Login Error", JOptionPane.ERROR_MESSAGE);
        } else {
            goToMainScreen(); // default fallback for demo
        }
    }

    private void goToMainScreen() {
        new MainView().setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}