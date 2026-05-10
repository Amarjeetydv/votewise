package gui;

// This file should now be located in the 'gui' folder

import logic.VoterService;
import logic.AdminService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginForm extends JFrame {
    private JTextField emailField = new JTextField(20);
    private JPasswordField passField = new JPasswordField(20);
    private JButton loginButton = new JButton("Login");
    private JButton registerButton = new JButton("Register");
    private JButton adminButton = new JButton("Admin Portal");
    private VoterService voterService = new VoterService();
    private AdminService adminService = new AdminService();

    public LoginForm() {
        setTitle("VoteWise Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("VoteWise Portal", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        mainPanel.add(new JLabel("Email:"));
        mainPanel.add(emailField);
        mainPanel.add(new JLabel("Password:"));
        mainPanel.add(passField);
        mainPanel.add(loginButton);
        mainPanel.add(registerButton);
        mainPanel.add(new JSeparator());
        mainPanel.add(new JSeparator());
        mainPanel.add(new JLabel("Staff only:"));
        mainPanel.add(adminButton);

        add(titleLabel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        loginButton.addActionListener(this::handleLogin);
        registerButton.addActionListener(e -> new RegisterForm().setVisible(true));
        adminButton.addActionListener(this::handleAdminLogin);

        // Enable the Enter key to trigger the login button
        getRootPane().setDefaultButton(loginButton);
    }

    private void handleLogin(ActionEvent e) {
        String email = emailField.getText();
        String password = new String(passField.getPassword());
        int voterId = voterService.authenticate(email, password);

        if (voterId != -1) {
            JOptionPane.showMessageDialog(this, "Login Successful!");
            this.dispose(); // Close login window
            new VotingForm(voterId).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials.", "Error", JOptionPane.ERROR_MESSAGE);
            passField.setText(""); // Clear password for the next attempt
        }
    }

    private void handleAdminLogin(ActionEvent e) {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField userField = new JTextField();
        JPasswordField adminPassField = new JPasswordField();
        panel.add(new JLabel("Username:"));
        panel.add(userField);
        panel.add(new JLabel("Password:"));
        panel.add(adminPassField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Admin Portal Login", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String user = userField.getText();
            String pass = new String(adminPassField.getPassword());
            if (adminService.login(user, pass)) {
                JOptionPane.showMessageDialog(this, "Admin Access Granted.");
                this.dispose();
                new AdminDashboard().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Unauthorized access.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
