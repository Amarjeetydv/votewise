package gui;

import logic.VoterService;
import javax.swing.*;
import java.awt.*;

public class RegisterForm extends JFrame {
    private JTextField nameField = new JTextField(20);
    private JTextField emailField = new JTextField(20);
    private JPasswordField passField = new JPasswordField(20);
    private JButton registerButton = new JButton("Register");
    private VoterService voterService = new VoterService();

    public RegisterForm() {
        setTitle("VoteWise Registration");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JLabel titleLabel = new JLabel("Create New Voter Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Password (min 6 chars):"));
        formPanel.add(passField);
        formPanel.add(new JLabel("")); 
        formPanel.add(registerButton);

        add(titleLabel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);

        registerButton.addActionListener(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String password = new String(passField.getPassword());

            if (voterService.register(name, email, password)) {
                JOptionPane.showMessageDialog(this, "Registration successful! Please login.");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Registration failed. Email might already exist or fields are empty.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
