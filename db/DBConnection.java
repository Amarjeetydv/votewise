package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection manager using DAO pattern.
 * Centralizes database configuration and provides connection factory method.
 */
public class DBConnection {
    // Database credentials - centralized configuration
    private static final String URL = "jdbc:mysql://localhost:3306/VoteWise";
    private static final String USER = "root";
    private static final String PASSWORD = "q525bs67";

    /**
     * Factory method to get database connection.
     * Throws SQLException if connection fails.
     */
    public static Connection getConnection() throws SQLException {
        // Returns a new connection to the VoteWise database
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
