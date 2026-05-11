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
    public static class VoterAuthData {
        public int id;
        public String storedPassword;
        public boolean hasVoted;

        public VoterAuthData(int id, String storedPassword, boolean hasVoted) {
            this.id = id;
            this.storedPassword = storedPassword;
            this.hasVoted = hasVoted;
        }
    }

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

    public VoterAuthData getVoterAuthData(String email) {
        String sql = "SELECT id, password, hasVoted FROM voters WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new VoterAuthData(
                            rs.getInt("id"),
                            rs.getString("password"),
                            rs.getBoolean("hasVoted")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching voter auth data: " + e.getMessage());
        }
        return null;
    }

    public boolean updateVoterPasswordHash(int voterId, String newHash) {
        String sql = "UPDATE voters SET password = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newHash);
            pstmt.setInt(2, voterId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating voter password hash: " + e.getMessage());
            return false;
        }
    }

    public boolean updateVoterLastLogin(int voterId) {
        String sql = "UPDATE voters SET last_login = NOW() WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, voterId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating voter last_login: " + e.getMessage());
            return false;
        }
    }

    public boolean registerVoter(String name, String email, String password, String course, String section) {
        String sql = "INSERT INTO voters (name, email, password, course, section) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setString(4, course);
            pstmt.setString(5, section);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error during voter registration: " + e.getMessage());
            return false;
        }
    }

    public List<String[]> getAllVoters() {
        List<String[]> voters = new ArrayList<>();
        String sql = "SELECT name, email, course, section, hasVoted FROM voters";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                voters.add(new String[]{
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("course"),
                    rs.getString("section"),
                    String.valueOf(rs.getBoolean("hasVoted"))
                });
            }
        } catch (SQLException e) {
            System.err.println("Error fetching voters: " + e.getMessage());
        }
        return voters;
    }
}
