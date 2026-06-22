package com.library;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static String url;
    private static String user;
    private static String password;
    private static String driver;

    private DatabaseConnection() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    static {
        try {
            Properties props = new Properties();
            InputStream input = DatabaseConnection.class.getClassLoader()
                    .getResourceAsStream("application.properties");
            
            if (input != null) {
                props.load(input);
                url = props.getProperty("db.url");
                user = props.getProperty("db.user");
                password = System.getenv("DB_PASSWORD");
                if (password == null || password.isEmpty()) {
                    password = props.getProperty("db.password");
                    LOGGER.warning("Using password from properties file. Consider using DB_PASSWORD environment variable.");
                }
                driver = props.getProperty("db.driver");
                
                Class.forName(driver);
            } else {
                url = System.getenv("DB_URL") != null ? 
                    System.getenv("DB_URL") : "jdbc:postgresql://localhost:5432/library_db";
                user = System.getenv("DB_USER") != null ? 
                    System.getenv("DB_USER") : "library_user";
                password = System.getenv("DB_PASSWORD") != null ? 
                    System.getenv("DB_PASSWORD") : "123456";
                driver = "org.postgresql.Driver";
                
                if (password.equals("123456") || password.isEmpty()) {
                    LOGGER.warning("Using default password. Please set DB_PASSWORD environment variable.");
                }
                Class.forName(driver);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading database configuration", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing database connection", e);
            }
        }
    }

    public static void testConnection() {
        try (Connection conn = getConnection()) {
            LOGGER.info("Database connection successful!");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection failed", e);
        }
    }
}