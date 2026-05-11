package gui;

import logic.VoterService;
import javax.swing.*;
import java.awt.*;

public class RegisterForm extends JFrame {
    private JTextField nameField = new JTextField(20);
    private JTextField emailField = new JTextField(20);
    private JTextField courseField = new JTextField(20);
    private JTextField sectionField = new JTextField(20);
    private JPasswordField passField = new JPasswordField(20);
    private JButton registerButton = new JButton("Register");
    private VoterService voterService = new VoterService();

    public RegisterForm() {
        setTitle("VoteWise Registration");
        setSize(500, 360);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JLabel titleLabel = new JLabel("Create New Voter Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Course:"));
        formPanel.add(courseField);
        formPanel.add(new JLabel("Section:"));
        formPanel.add(sectionField);
        formPanel.add(new JLabel("Password (min 6 chars):"));
        formPanel.add(passField);
        formPanel.add(new JLabel("")); 
        formPanel.add(registerButton);

        add(titleLabel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);

        registerButton.addActionListener(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String course = courseField.getText();
            String section = sectionField.getText();
            String password = new String(passField.getPassword());

            if (!voterService.isValidLpuEmail(email)) {
                JOptionPane.showMessageDialog(this,
                    "Only official LPU email is allowed (example: amarjeet.yadava2025@lpu.in)",
                    "Invalid LPU Email",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!voterService.sendRegistrationOtp(email)) {
                JOptionPane.showMessageDialog(this,
                    "Unable to send OTP email. Configure SMTP and try again.\nDetails: " + voterService.getOtpDeliveryError(),
                    "OTP Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this,
                "OTP sent to your LPU email: " + email + "\n(OTP valid for 5 minutes)",
                "OTP Sent",
                JOptionPane.INFORMATION_MESSAGE);

            String enteredOtp = null;
            boolean otpVerified = false;
            int attempts = 3;

            while (attempts > 0 && !otpVerified) {
                Object[] options = {"Verify OTP", "Resend OTP", "Cancel"};
                JPanel otpPanel = new JPanel(new BorderLayout(10, 10));
                otpPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                otpPanel.add(new JLabel("Enter OTP (" + attempts + " attempt(s) left):"), BorderLayout.NORTH);
                JTextField otpField = new JTextField(20);
                otpPanel.add(otpField, BorderLayout.CENTER);

                int result = JOptionPane.showOptionDialog(this, otpPanel, "OTP Verification",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

                if (result == 2 || result == JOptionPane.CLOSED_OPTION) {
                    return;
                }

                if (result == 1) {
                    if (voterService.sendRegistrationOtp(email)) {
                        JOptionPane.showMessageDialog(this, "OTP resent to your email.", "OTP Resent", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to resend OTP: " + voterService.getOtpDeliveryError(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    continue;
                }

                enteredOtp = otpField.getText();
                if (voterService.verifyRegistrationOtp(email, enteredOtp)) {
                    otpVerified = true;
                } else {
                    attempts--;
                    if (attempts > 0) {
                        JOptionPane.showMessageDialog(this, "Invalid OTP. Please try again.", "Verification Failed", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

            if (!otpVerified) {
                JOptionPane.showMessageDialog(this,
                    "OTP verification failed after 3 attempts.",
                    "Verification Failed",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (voterService.register(name, email, password, course, section)) {
                JOptionPane.showMessageDialog(this, "Registration successful! Please login.");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Registration failed. Email may already exist, or one or more fields are invalid.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
