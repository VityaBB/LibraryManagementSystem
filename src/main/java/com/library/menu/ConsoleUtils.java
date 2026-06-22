package com.library.menu;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsoleUtils {
    private static final Logger LOGGER = Logger.getLogger(ConsoleUtils.class.getName());
    
    private ConsoleUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    public static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void printHeader(String title) {
        LOGGER.info("\n╔══════════════════════════════════════════════╗");
        LOGGER.info(String.format("║   %-41s║", title));
        LOGGER.info("╚══════════════════════════════════════════════╝");
    }

    public static void printSeparator() {
        LOGGER.info("──────────────────────────────────────────────────");
    }

    public static void waitForEnter() {
        LOGGER.info("Press Enter to continue...");
        try {
            System.in.read();
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    public static String centerString(String str, int width) {
        if (str.length() >= width) {
            return str;
        }
        int padding = width - str.length();
        int leftPad = padding / 2;
        int rightPad = padding - leftPad;
        return " ".repeat(leftPad) + str + " ".repeat(rightPad);
    }

    public static String padRight(String str, int length) {
        if (str.length() > length) {
            return str.substring(0, length);
        }
        return str + " ".repeat(length - str.length());
    }
}