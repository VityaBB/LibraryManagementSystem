package com.library;

import com.library.menu.MainMenu;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    
    public static void main(String[] args) {
        boolean debugMode = Boolean.parseBoolean(System.getenv("APP_DEBUG"));
        
        if (debugMode) {
            LOGGER.info("Application started in DEBUG mode");
        } else {
            LOGGER.info("Application started in PRODUCTION mode");
        }

        LOGGER.info("========================================");
        LOGGER.info("        LibraryManagementSystem         ");
        LOGGER.info("========================================\n");
        
        try {
            MainMenu menu = new MainMenu();
            menu.start();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Application error", e);
        }
    }
}