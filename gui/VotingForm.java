package gui;

import db.CandidateDAO;
import db.VoteDAO;
import logic.Candidate;
import db.ElectionDAO;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VotingForm extends JFrame {
    private int voterId;
    private JButton voteButton = new JButton("Cast Vote");
    private JLabel selectedLabel = new JLabel("No candidate selected");
    private int selectedId = -1;
    
    private CandidateDAO candidateDAO = new CandidateDAO();
    private VoteDAO voteDAO = new VoteDAO();
    private ElectionDAO electionDAO = new ElectionDAO();

    public VotingForm(int voterId) {
        this.voterId = voterId;
        setTitle("VoteWise - Cast Your Vote");
        setSize(650, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);

        ElectionDAO.ElectionInfo electionInfo = electionDAO.getElectionInfo();

        JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        JLabel electionLabel = new JLabel("Election: " + electionInfo.name + " - " + electionInfo.post, SwingConstants.CENTER);
        electionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel instructionLabel = new JLabel("Select a Candidate to Vote", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(electionLabel);
        topPanel.add(instructionLabel);
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with candidate buttons
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 3, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        List<Candidate> candidates = candidateDAO.getAllCandidates();
        for (Candidate c : candidates) {
            JButton btn = new JButton("<html><center>" + c.getName() + "<br/>" + c.getSymbol() + "</center></html>");
            btn.setFont(new Font("Arial", Font.PLAIN, 12));
            btn.setPreferredSize(new Dimension(120, 80));
            int candId = c.getId();
            
            btn.addActionListener(e -> {
                selectedId = candId;
                selectedLabel.setText("Selected: " + c.getName() + " (Symbol: " + c.getSymbol() + ")");
            });
            centerPanel.add(btn);
        }
        
        JScrollPane scroll = new JScrollPane(centerPanel);
        add(scroll, BorderLayout.CENTER);
        
        // South panel
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BorderLayout());
        
        selectedLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        southPanel.add(selectedLabel, BorderLayout.NORTH);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));
        buttonPanel.add(voteButton);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(southPanel, BorderLayout.SOUTH);

        voteButton.addActionListener(e -> castVote());
    }

    private void castVote() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a candidate.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Confirm your vote?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (voteDAO.castVote(voterId, selectedId)) {
                JOptionPane.showMessageDialog(this, "Vote cast successfully!");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error casting vote.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
