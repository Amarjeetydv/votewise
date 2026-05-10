package db;

// This file should now be located in the 'db' folder

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VoterDAO {
    /**
     * Validates voter and returns their ID, or -1 if invalid.
     */
    public int validateVoter(String email, String password) {
        String sql = "SELECT id FROM voters WHERE email = ? AND password = ? AND hasVoted = FALSE";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during voter validation: " + e.getMessage());
        }
        return -1;
    }

    public boolean registerVoter(String name, String email, String password) {
        String sql = "INSERT INTO voters (name, email, password) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error during voter registration: " + e.getMessage());
            return false;
        }
    }

    public List<String[]> getAllVoters() {
        List<String[]> voters = new ArrayList<>();
        String sql = "SELECT name, email, hasVoted FROM voters";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                voters.add(new String[]{rs.getString("name"), rs.getString("email"), String.valueOf(rs.getBoolean("hasVoted"))});
            }
        } catch (SQLException e) {
            System.err.println("Error fetching voters: " + e.getMessage());
        }
        return voters;
    }
}
