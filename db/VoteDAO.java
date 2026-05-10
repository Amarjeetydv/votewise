package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class VoteDAO {
    public boolean castVote(int voterId, int candidateId) {
        String insertVoteSql = "INSERT INTO votes (voter_id, candidate_id) VALUES (?, ?)";
        String updateVoterSql = "UPDATE voters SET hasVoted = TRUE WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Start Transaction

            try (PreparedStatement voteStmt = conn.prepareStatement(insertVoteSql);
                 PreparedStatement voterStmt = conn.prepareStatement(updateVoterSql)) {
                
                // 1. Record the vote
                voteStmt.setInt(1, voterId);
                voteStmt.setInt(2, candidateId);
                voteStmt.executeUpdate();

                // 2. Update voter status
                voterStmt.setInt(1, voterId);
                voterStmt.executeUpdate();

                conn.commit(); // Commit Transaction
                return true;
            } catch (SQLException e) {
                conn.rollback(); // Rollback if any step fails
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Transaction failed: " + e.getMessage());
            return false;
        }
    }
}
