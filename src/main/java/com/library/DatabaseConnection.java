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
    
    private static final String DB_PASSWORD_ENV = "DB_PASSWORD";
    private static final String DB_URL_ENV = "DB_URL";
    private static final String DB_USER_ENV = "DB_USER";
    private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/library_db";
    private static final String DEFAULT_USER = "library_user";
    private static final String DEFAULT_PASSWORD = "123456";
    private static final String DEFAULT_DRIVER = "org.postgresql.Driver";
    
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
                password = System.getenv(DB_PASSWORD_ENV);
                if (password == null || password.isEmpty()) {
                    password = props.getProperty("db.password");
                    LOGGER.warning("Using password from properties file. Consider using " + DB_PASSWORD_ENV + " environment variable.");
                }
                driver = props.getProperty("db.driver");
                
                Class.forName(driver);
            } else {
                url = System.getenv(DB_URL_ENV) != null ? 
                    System.getenv(DB_URL_ENV) : DEFAULT_URL;
                user = System.getenv(DB_USER_ENV) != null ? 
                    System.getenv(DB_USER_ENV) : DEFAULT_USER;
                password = System.getenv(DB_PASSWORD_ENV) != null ? 
                    System.getenv(DB_PASSWORD_ENV) : DEFAULT_PASSWORD;
                driver = DEFAULT_DRIVER;
                
                if (password.equals(DEFAULT_PASSWORD) || password.isEmpty()) {
                    LOGGER.warning("Using default password. Please set " + DB_PASSWORD_ENV + " environment variable.");
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