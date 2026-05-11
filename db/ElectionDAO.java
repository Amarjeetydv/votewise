package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO for election settings and ballot statistics.
 */
public class ElectionDAO {
    public static class ElectionInfo {
        public String name;
        public String post;
        public String description;

        public ElectionInfo(String name, String post, String description) {
            this.name = name;
            this.post = post;
            this.description = description;
        }
    }

    public static class BallotStats {
        public int totalRegistered;
        public int totalVoted;

        public BallotStats(int totalRegistered, int totalVoted) {
            this.totalRegistered = totalRegistered;
            this.totalVoted = totalVoted;
        }

        public int getRemaining() {
            return totalRegistered - totalVoted;
        }

        public double getVotingPercentage() {
            if (totalRegistered == 0) return 0;
            return (double) totalVoted / totalRegistered * 100;
        }
    }

    public ElectionInfo getElectionInfo() {
        String sql = "SELECT name, post, description FROM election LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return new ElectionInfo(
                        rs.getString("name"),
                        rs.getString("post"),
                        rs.getString("description")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching election info: " + e.getMessage());
        }
        return new ElectionInfo("Election", "Post", "");
    }

    public boolean setElectionInfo(String name, String post, String description) {
        String sql = "UPDATE election SET name = ?, post = ?, description = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, post);
            pstmt.setString(3, description);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating election info: " + e.getMessage());
            return false;
        }
    }

    public BallotStats getBallotStats() {
        String sql = "SELECT COUNT(*) as total FROM voters; SELECT COUNT(*) as voted FROM voters WHERE hasVoted = TRUE;";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) as total FROM voters");
             ResultSet rs = pstmt.executeQuery()) {

            int totalRegistered = 0;
            if (rs.next()) {
                totalRegistered = rs.getInt("total");
            }

            String sql2 = "SELECT COUNT(*) as voted FROM voters WHERE hasVoted = TRUE";
            try (PreparedStatement pstmt2 = conn.prepareStatement(sql2);
                 ResultSet rs2 = pstmt2.executeQuery()) {
                int totalVoted = 0;
                if (rs2.next()) {
                    totalVoted = rs2.getInt("voted");
                }
                return new BallotStats(totalRegistered, totalVoted);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching ballot stats: " + e.getMessage());
        }
        return new BallotStats(0, 0);
    }
}
