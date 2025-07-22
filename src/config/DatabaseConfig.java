package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database connection configuration for MySQL
 * Handles connection pooling and database operations
 */
public class DatabaseConfig {
    
    // Database connection parameters
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "library_management_system";
    private static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;
    
    // Default credentials (should be configured via environment variables in production)
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = ""; // Set your MySQL password here
    
    // Connection pool settings
    private static final int MAX_CONNECTIONS = 10;
    private static final int CONNECTION_TIMEOUT = 30000; // 30 seconds
    
    // Static connection instance
    private static Connection connection = null;
    
    /**
     * Gets a database connection
     * @return Database connection
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load MySQL JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Set connection properties
                Properties props = new Properties();
                props.setProperty("user", getUsername());
                props.setProperty("password", getPassword());
                props.setProperty("useSSL", "false");
                props.setProperty("serverTimezone", "UTC");
                props.setProperty("allowPublicKeyRetrieval", "true");
                props.setProperty("autoReconnect", "true");
                props.setProperty("useUnicode", "true");
                props.setProperty("characterEncoding", "UTF-8");
                
                // Create connection
                connection = DriverManager.getConnection(DB_URL, props);
                
                System.out.println("✅ Database connected successfully!");
                
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found. Please add mysql-connector-java to your classpath.", e);
            } catch (SQLException e) {
                System.err.println("❌ Failed to connect to database: " + e.getMessage());
                throw e;
            }
        }
        
        return connection;
    }
    
    /**
     * Creates a new database connection (for multi-threading)
     * @return New database connection
     * @throws SQLException if connection fails
     */
    public static Connection createNewConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            Properties props = new Properties();
            props.setProperty("user", getUsername());
            props.setProperty("password", getPassword());
            props.setProperty("useSSL", "false");
            props.setProperty("serverTimezone", "UTC");
            props.setProperty("allowPublicKeyRetrieval", "true");
            props.setProperty("autoReconnect", "true");
            props.setProperty("useUnicode", "true");
            props.setProperty("characterEncoding", "UTF-8");
            
            return DriverManager.getConnection(DB_URL, props);
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }
    }
    
    /**
     * Closes the database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔌 Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
    
    /**
     * Tests the database connection
     * @return true if connection is successful
     */
    public static boolean testConnection() {
        try (Connection testConn = createNewConnection()) {
            return testConn != null && !testConn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets database username from environment variable or default
     * @return database username
     */
    private static String getUsername() {
        String envUsername = System.getenv("DB_USERNAME");
        return envUsername != null ? envUsername : DB_USERNAME;
    }
    
    /**
     * Gets database password from environment variable or default
     * @return database password
     */
    private static String getPassword() {
        String envPassword = System.getenv("DB_PASSWORD");
        return envPassword != null ? envPassword : DB_PASSWORD;
    }
    
    /**
     * Gets the database URL
     * @return database URL
     */
    public static String getDatabaseUrl() {
        return DB_URL;
    }
    
    /**
     * Gets database connection info
     * @return connection info string
     */
    public static String getConnectionInfo() {
        return String.format("Database: %s\nHost: %s:%s\nUsername: %s", 
                           DB_NAME, DB_HOST, DB_PORT, getUsername());
    }
    
    /**
     * Initializes database connection and verifies schema
     * @throws SQLException if initialization fails
     */
    public static void initialize() throws SQLException {
        Connection conn = getConnection();
        
        // Verify database schema exists
        try (var stmt = conn.createStatement()) {
            var rs = stmt.executeQuery("SHOW TABLES LIKE 'books'");
            if (!rs.next()) {
                throw new SQLException("Database schema not found. Please run the setup script first.");
            }
            System.out.println("✅ Database schema verified.");
        }
    }
    
    /**
     * Main method for testing database connection
     */
    public static void main(String[] args) {
        System.out.println("🗄️  Testing MySQL Database Connection");
        System.out.println("====================================");
        
        try {
            System.out.println("Connection Info:");
            System.out.println(getConnectionInfo());
            System.out.println();
            
            if (testConnection()) {
                System.out.println("✅ Database connection successful!");
                
                // Test some basic queries
                try (Connection conn = getConnection()) {
                    // Test query
                    try (var stmt = conn.createStatement()) {
                        var rs = stmt.executeQuery("SELECT COUNT(*) as book_count FROM books");
                        if (rs.next()) {
                            System.out.println("📚 Total books in database: " + rs.getInt("book_count"));
                        }
                        
                        rs = stmt.executeQuery("SELECT COUNT(*) as member_count FROM members");
                        if (rs.next()) {
                            System.out.println("👥 Total members in database: " + rs.getInt("member_count"));
                        }
                        
                        rs = stmt.executeQuery("SELECT COUNT(*) as active_borrowings FROM book_borrowings WHERE is_returned = FALSE");
                        if (rs.next()) {
                            System.out.println("📖 Active borrowings: " + rs.getInt("active_borrowings"));
                        }
                    }
                }
                
            } else {
                System.out.println("❌ Database connection failed!");
                System.out.println("\n🔧 Troubleshooting:");
                System.out.println("1. Make sure MySQL is running");
                System.out.println("2. Check username and password");
                System.out.println("3. Verify database exists: " + DB_NAME);
                System.out.println("4. Run setup-database.bat first");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
            System.out.println("\n🔧 Solution: Run setup-database.bat to create the database");
        } finally {
            closeConnection();
        }
    }
}
