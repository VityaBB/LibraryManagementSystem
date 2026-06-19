package com.library.menu;

public class ConsoleUtils {
    
    public static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // Ignore
        }
    }

    public static void printHeader(String title) {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.printf("║   %-41s║%n", title);
        System.out.println("╚══════════════════════════════════════════════╝");
    }

    public static void printSeparator() {
        System.out.println("──────────────────────────────────────────────────");
    }

    public static void waitForEnter() {
        System.out.print("Нажмите Enter для продолжения...");
        try {
            System.in.read();
        } catch (Exception e) {
            // Ignore
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