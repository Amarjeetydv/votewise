package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Data Access Object for vote operations.
 * Ensures atomic transactions: vote insertion + voter status update occur together or not at all.
 */
public class VoteDAO {
    /**
     * Cast a vote atomically.
     * Uses database transaction to ensure vote insertion and voter status update happen together.
     * Prevents partial updates and ensures consistency.
     */
    public boolean castVote(int voterId, int candidateId) {
        String insertVoteSql = "INSERT INTO votes (voter_id, candidate_id) VALUES (?, ?)";
        String updateVoterSql = "UPDATE voters SET hasVoted = TRUE WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            // Disable auto-commit to start manual transaction
            conn.setAutoCommit(false);

            try (PreparedStatement voteStmt = conn.prepareStatement(insertVoteSql);
                 PreparedStatement voterStmt = conn.prepareStatement(updateVoterSql)) {
                
                // Step 1: Insert the vote record
                voteStmt.setInt(1, voterId);
                voteStmt.setInt(2, candidateId);
                voteStmt.executeUpdate();

                // Step 2: Mark voter as voted to prevent duplicate voting
                voterStmt.setInt(1, voterId);
                voterStmt.executeUpdate();

                // Commit both operations together (all-or-nothing)
                conn.commit();
                return true;
            } catch (SQLException e) {
                // Rollback both operations if either fails
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Transaction failed: " + e.getMessage());
            return false;
        }
    }
}
