package gui;

import logic.AdminService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {
    private AdminService adminService = new AdminService();
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JTable voterTable;
    private DefaultTableModel voterTableModel;
    private JLabel winnerLabel = new JLabel("Current Winner: None");

    public AdminDashboard() {
        setTitle("VoteWise - Admin Dashboard");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Results & Candidates
        JPanel resultsPanel = new JPanel(new BorderLayout());
        String[] columnNames = {"ID", "Name", "Party", "Votes"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultsTable = new JTable(tableModel);
        resultsTable.setAutoCreateRowSorter(true);
            refreshResults();
        resultsTable.setRowHeight(36);
        resultsPanel.add(new JScrollPane(resultsTable), BorderLayout.CENTER);

        winnerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        winnerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        winnerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel resultsControl = new JPanel();
        JButton addBtn = new JButton("Add Candidate");
        JButton deleteBtn = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");
        JButton logoutBtn = new JButton("Logout");
        resultsControl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        resultsControl.add(addBtn);
        resultsControl.add(deleteBtn);
        resultsControl.add(refreshBtn);
        resultsControl.add(logoutBtn);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(winnerLabel, BorderLayout.NORTH);
        southPanel.add(resultsControl, BorderLayout.SOUTH);
        resultsPanel.add(southPanel, BorderLayout.SOUTH);

        // Tab 2: Voter List
        JPanel voterPanel = new JPanel(new BorderLayout());
        String[] voterColumns = {"Name", "Email", "Has Voted"};
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
                String party = JOptionPane.showInputDialog(this, "Enter Party Name:");
                if (party != null && !party.trim().isEmpty()) {
                    if (adminService.addCandidate(name.trim(), party.trim())) {
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
    }

    private void refreshVoters() {
        voterTableModel.setRowCount(0);
        List<String[]> voters = adminService.getVoterList();
        for (String[] voter : voters) {
            voterTableModel.addRow(voter);
        }
    }
}
