package db;

import logic.Candidate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CandidateDAO {
    public List<Candidate> getAllCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        String sql = "SELECT id, name, symbol FROM candidates";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String symbol = rs.getString("symbol");
                candidates.add(new Candidate(id, name, symbol));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching candidates: " + e.getMessage());
        }
        return candidates;
    }

    public boolean addCandidate(String name, String symbol) {
        String sql = "INSERT INTO candidates (name, symbol) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, symbol);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding candidate: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCandidate(int id) {
        String sql = "DELETE FROM candidates WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting candidate: " + e.getMessage());
            return false;
        }
    }

    public List<Object[]> getVoteResults() {
        List<Object[]> results = new ArrayList<>();
        String sql = "SELECT c.id, c.name, c.symbol, COUNT(v.id) as vote_count FROM candidates c " +
                     "LEFT JOIN votes v ON c.id = v.candidate_id GROUP BY c.id, c.name, c.symbol";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String symbol = rs.getString("symbol");
                int votes = rs.getInt("vote_count");
                results.add(new Object[]{id, name, symbol, votes});
            }
        } catch (SQLException e) { 
            System.err.println("Error fetching results: " + e.getMessage()); 
        }
        return results;
    }
}
