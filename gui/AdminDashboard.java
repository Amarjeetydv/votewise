package gui;

import logic.AdminService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Properties;

public class AdminDashboard extends JFrame {
    private AdminService adminService = new AdminService();
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JTable voterTable;
    private DefaultTableModel voterTableModel;
    private JLabel winnerLabel = new JLabel("Current Winner: None");
    private JLabel ballotStatsLabel = new JLabel("Ballot Status: Loading...");

    public AdminDashboard() {
        setTitle("VoteWise - Admin Dashboard");
        setSize(900, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Results & Candidates
        JPanel resultsPanel = new JPanel(new BorderLayout());
        String[] columnNames = {"ID", "Name", "Symbol", "Votes"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultsTable = new JTable(tableModel);
        resultsTable.setAutoCreateRowSorter(true);
        resultsTable.setRowHeight(36);
        resultsPanel.add(new JScrollPane(resultsTable), BorderLayout.CENTER);

        winnerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        winnerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        winnerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        ballotStatsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        ballotStatsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ballotStatsLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        JPanel centerInfoPanel = new JPanel(new GridLayout(2, 1));
        centerInfoPanel.add(ballotStatsLabel);
        centerInfoPanel.add(winnerLabel);

        JPanel resultsControl = new JPanel();
        JButton addBtn = new JButton("Add Candidate");
        JButton deleteBtn = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");
        JButton exportResultsBtn = new JButton("Export Results (CSV)");
        JButton electionSettingsBtn = new JButton("Election Settings");
        JButton smtpBtn = new JButton("SMTP Settings");
        JButton logoutBtn = new JButton("Logout");
        resultsControl.setLayout(new GridLayout(2, 4, 5, 5));
        resultsControl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        resultsControl.add(addBtn);
        resultsControl.add(deleteBtn);
        resultsControl.add(refreshBtn);
        resultsControl.add(exportResultsBtn);
        resultsControl.add(electionSettingsBtn);
        resultsControl.add(smtpBtn);
        resultsControl.add(logoutBtn);
        resultsControl.add(new JLabel()); // Empty cell to fill grid

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(centerInfoPanel, BorderLayout.NORTH);
        southPanel.add(resultsControl, BorderLayout.SOUTH);
        resultsPanel.add(southPanel, BorderLayout.SOUTH);

        // initial population of results
        refreshResults();

        // Tab 2: Voter List
        JPanel voterPanel = new JPanel(new BorderLayout());
        String[] voterColumns = {"Name", "Email", "Course", "Section", "Has Voted"};
        voterTableModel = new DefaultTableModel(voterColumns, 0);
        voterTable = new JTable(voterTableModel);
        voterTable.setAutoCreateRowSorter(true);
        voterTable.setRowHeight(25);
            refreshVoters();
        voterPanel.add(new JScrollPane(voterTable), BorderLayout.CENTER);
        
        JButton refreshVotersBtn = new JButton("Refresh");
        JPanel voterControl = new JPanel();
            voterControl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        voterControl.add(refreshVotersBtn);
        voterPanel.add(voterControl, BorderLayout.SOUTH);

        tabbedPane.addTab("Results & Candidates", resultsPanel);
        tabbedPane.addTab("Registered Voters", voterPanel);
        
        add(tabbedPane, BorderLayout.CENTER);

        addBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Enter Candidate Name:");
            if (name != null && !name.trim().isEmpty()) {
                String symbol = JOptionPane.showInputDialog(this, "Enter Election Symbol (e.g., Book, Pen, Lotus):");
                if (symbol != null && !symbol.trim().isEmpty()) {
                    if (adminService.addCandidate(name.trim(), symbol.trim())) {
                        JOptionPane.showMessageDialog(this, "Candidate Added Successfully!");
                        refreshResults();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to add candidate.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        deleteBtn.addActionListener(e -> {
            int selectedRow = resultsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a candidate to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int modelRow = resultsTable.convertRowIndexToModel(selectedRow);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            String name = (String) tableModel.getValueAt(modelRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this, "Delete " + name + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (adminService.removeCandidate(id)) {
                    JOptionPane.showMessageDialog(this, "Candidate Deleted!");
                    refreshResults();
                } else {
                    JOptionPane.showMessageDialog(this, "Cannot delete. Candidate may have votes.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        refreshBtn.addActionListener(e -> refreshResults());
        refreshVotersBtn.addActionListener(e -> refreshVoters());
        exportResultsBtn.addActionListener(e -> exportResults());
        electionSettingsBtn.addActionListener(e -> openElectionSettingsDialog());
        smtpBtn.addActionListener(e -> openSmtpSettingsDialog());

        logoutBtn.addActionListener(e -> {
            this.dispose();
            new LoginForm().setVisible(true);
        });
    }

    private void refreshResults() {
        tableModel.setRowCount(0);
        List<Object[]> results = adminService.getResults();

        int maxVotes = 0;
        String winnerName = "";
        boolean tie = false;

        for (Object[] row : results) {
            tableModel.addRow(row);
            int votes = (int) row[3];
            if (votes > maxVotes) {
                maxVotes = votes;
                winnerName = (String) row[1];
                tie = false;
            } else if (votes == maxVotes && maxVotes > 0) {
                tie = true;
            }
        }

        if (maxVotes == 0) {
            winnerLabel.setText("Current Winner: No votes cast");
        } else if (tie) {
            winnerLabel.setText("Current Winner: Tie!");
        } else {
            winnerLabel.setText("Current Winner: " + winnerName + " (" + maxVotes + " votes)");
        }
        updateBallotStats();
    }

    private void updateBallotStats() {
        db.ElectionDAO.BallotStats stats = adminService.getBallotStats();
        String statsText = String.format("Ballot Status: %d registered | %d voted | %d remaining (%.1f%% voted)",
                stats.totalRegistered, stats.totalVoted, stats.getRemaining(), stats.getVotingPercentage());
        ballotStatsLabel.setText(statsText);
    }

    private void refreshVoters() {
        voterTableModel.setRowCount(0);
        List<String[]> voters = adminService.getVoterList();
        for (String[] voter : voters) {
            voterTableModel.addRow(voter);
        }
    }

    private void exportResults() {
        List<Object[]> results = adminService.getResults();
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No results to export.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (adminService.exportResultsToCSV(results)) {
            JOptionPane.showMessageDialog(this, "Results exported to CSV successfully.", "Export Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to export results.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openElectionSettingsDialog() {
        db.ElectionDAO.ElectionInfo info = adminService.getElectionInfo();
        JTextField nameField = new JTextField(info.name, 30);
        JTextField postField = new JTextField(info.post, 20);
        JTextArea descArea = new JTextArea(info.description, 3, 30);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.add(new JLabel("Election Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Post/Position:"));
        formPanel.add(postField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(new JScrollPane(descArea));
        panel.add(formPanel, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, panel, "Election Settings", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String post = postField.getText().trim();
            String desc = descArea.getText().trim();
            if (name.isEmpty() || post.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Election name and post are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (adminService.setElectionInfo(name, post, desc)) {
                JOptionPane.showMessageDialog(this, "Election settings saved successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save election settings.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openSmtpSettingsDialog() {
        Properties smtp = adminService.getSmtpSettings();

        JTextField hostField = new JTextField(smtp.getProperty("host", "smtp.gmail.com"));
        JTextField portField = new JTextField(smtp.getProperty("port", "587"));
        JTextField userField = new JTextField(smtp.getProperty("user", ""));
        JPasswordField passField = new JPasswordField(smtp.getProperty("pass", ""));
        JTextField fromField = new JTextField(smtp.getProperty("from", ""));

        JPanel panel = new JPanel(new GridLayout(5, 2, 8, 8));
        panel.add(new JLabel("SMTP Host:"));
        panel.add(hostField);
        panel.add(new JLabel("SMTP Port:"));
        panel.add(portField);
        panel.add(new JLabel("SMTP User:"));
        panel.add(userField);
        panel.add(new JLabel("SMTP Password:"));
        panel.add(passField);
        panel.add(new JLabel("From Email:"));
        panel.add(fromField);

        Object[] options = {"Test SMTP", "Save", "Cancel"};
        while (true) {
            int result = JOptionPane.showOptionDialog(
                    this,
                    panel,
                    "SMTP Settings",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[1]
            );

            if (result == 2 || result == JOptionPane.CLOSED_OPTION) {
                return;
            }

            String host = hostField.getText().trim();
            String port = portField.getText().trim();
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword());
            String from = fromField.getText().trim();

            if (host.isEmpty() || port.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Host, port, user and password are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            if (from.isEmpty()) {
                from = user;
                fromField.setText(from);
            }

            if (result == 0) {
                String testRecipient = JOptionPane.showInputDialog(this, "Test recipient email:", from);
                if (testRecipient == null || testRecipient.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Recipient email is required for SMTP test.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    continue;
                }

                if (adminService.testSmtpSettings(host, port, user, pass, from, testRecipient.trim())) {
                    JOptionPane.showMessageDialog(this, "SMTP test email sent successfully to " + testRecipient.trim() + ".");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "SMTP test failed. Details: " + adminService.getSmtpTestError(),
                            "SMTP Test Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
                continue;
            }

            if (adminService.saveSmtpSettings(host, port, user, pass, from)) {
                JOptionPane.showMessageDialog(this, "SMTP settings saved successfully.");
                return;
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save SMTP settings.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
