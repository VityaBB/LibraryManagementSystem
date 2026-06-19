package com.library;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConnection {
    private static String url;
    private static String user;
    private static String password;
    private static String driver;

    static {
        try {
            
            Properties props = new Properties();
            InputStream input = DatabaseConnection.class.getClassLoader()
                    .getResourceAsStream("application.properties");
            
            if (input != null) {
                props.load(input);
                url = props.getProperty("db.url");
                user = props.getProperty("db.user");
                password = props.getProperty("db.password");
                driver = props.getProperty("db.driver");
                
                
                Class.forName(driver);
            } else {
                
                url = "jdbc:postgresql://localhost:5432/library_db";
                user = "library_user";
                password = "123456";
                driver = "org.postgresql.Driver";
                Class.forName(driver);
            }
        } catch (Exception e) {
            System.err.println("Ошибка загрузки настроек БД: " + e.getMessage());
            e.printStackTrace();
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
                e.printStackTrace();
            }
        }
    }

    public static void testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println(" Подключение к БД успешно!");
        } catch (SQLException e) {
            System.err.println(" Ошибка подключения к БД: " + e.getMessage());
        }
    }
}