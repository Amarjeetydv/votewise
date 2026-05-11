package db;

import logic.SecurityUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {
    public boolean validateAdmin(String username, String password) {
        String sql = "SELECT id, password FROM admin WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }

                int adminId = rs.getInt("id");
                String storedPassword = rs.getString("password");
                if (!SecurityUtil.verifyPassword(password, storedPassword)) {
                    return false;
                }

                // Migrate existing plain-text admin password to hash after successful login.
                if (!SecurityUtil.isPasswordHashed(storedPassword)) {
                    updateAdminPasswordHash(conn, adminId, SecurityUtil.hashPassword(password));
                }
                
                // Update last_login timestamp
                updateAdminLastLogin(adminId);
                
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Admin validation error [State: " + e.getSQLState() + "]: " + e.getMessage());
            return false;
        }
    }

    private void updateAdminPasswordHash(Connection conn, int adminId, String hashedPassword) throws SQLException {
        String updateSql = "UPDATE admin SET password = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            pstmt.setString(1, hashedPassword);
            pstmt.setInt(2, adminId);
            pstmt.executeUpdate();
        }
    }

    public boolean updateAdminLastLogin(int adminId) {
        String sql = "UPDATE admin SET last_login = NOW() WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, adminId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating admin last_login: " + e.getMessage());
            return false;
        }
    }
}
