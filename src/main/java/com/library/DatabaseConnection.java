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
    private static final String PROPERTIES_FILE = "application.properties";
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
                    .getResourceAsStream(PROPERTIES_FILE);

            if (input != null) {
                props.load(input);
                url = props.getProperty("db.url");
                user = props.getProperty("db.user");
                password = props.getProperty("db.password");
                driver = props.getProperty("db.driver", DEFAULT_DRIVER);
                Class.forName(driver);
            } else {
                throw new RuntimeException("Application properties file not found: " + PROPERTIES_FILE);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load database configuration", e);
            throw new RuntimeException("Failed to initialize DatabaseConnection", e);
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