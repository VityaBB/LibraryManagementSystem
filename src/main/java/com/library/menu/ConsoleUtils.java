package com.library.menu;

import java.util.Scanner;
import com.library.models.Book;
import com.library.models.Author;
import com.library.models.Genre;
import com.library.dao.BookDAO;
import java.sql.SQLException;
import java.util.List;

public class ConsoleUtils {
    private static final Scanner scanner = new Scanner(System.in);

    private ConsoleUtils() {}

    public static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static int readInt(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Введите число от " + min + " до " + max);
            } catch (NumberFormatException e) {
                System.out.println("Введите корректное число");
            }
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    return 0;
                }
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Введите корректное число");
            }
        }
    }

    public static boolean readYesNo(String prompt) {
        while (true) {
            String input = readString(prompt);
            if (input.equalsIgnoreCase("y") || input.equalsIgnoreCase("yes")) {
                return true;
            }
            if (input.equalsIgnoreCase("n") || input.equalsIgnoreCase("no")) {
                return false;
            }
            System.out.println("Введите y (да) или n (нет)");
        }
    }

    public static void printBookList(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("Книг не найдено");
            return;
        }
        System.out.println("┌────┬────────────────────────────────────────────┬──────────┬─────────────┐");
        System.out.println("│ ID │ Название                                   │ Год      │ Доступно    │");
        System.out.println("├────┼────────────────────────────────────────────┼──────────┼─────────────┤");
        for (Book b : books) {
            int available = 0;
            try {
                available = b.getTotalCopies();
            } catch (Exception e) {
                available = 0;
            }
            System.out.printf("│ %-2d │ %-42s │ %-8d │ %-11d │%n",
                b.getId(),
                b.getTitle().length() > 42 ? b.getTitle().substring(0, 39) + "..." : b.getTitle(),
                b.getPublicationYear(),
                available);
        }
        System.out.println("└────┴────────────────────────────────────────────┴──────────┴─────────────┘");
    }

    public static void printTable(String title, String... lines) {
        int maxLen = 0;
        for (String line : lines) {
            maxLen = Math.max(maxLen, line.length());
        }
        int width = Math.max(maxLen + 2, title.length() + 4);

        System.out.println("┌" + "─".repeat(width) + "┐");
        System.out.println("│" + centerString(title, width) + "│");
        System.out.println("├" + "─".repeat(width) + "┤");
        for (String line : lines) {
            System.out.println("│ " + padRight(line, width - 1) + "│");
        }
        System.out.println("└" + "─".repeat(width) + "┘");
    }

    public static String padRight(String str, int length) {
        if (str.length() > length) {
            return str.substring(0, length);
        }
        return str + " ".repeat(length - str.length());
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

    public static String formatAuthors(List<Author> authors) {
        if (authors.isEmpty()) {
            return "Не указаны";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < authors.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(authors.get(i).getFullName()).append(" (ID: ").append(authors.get(i).getId()).append(")");
        }
        return sb.toString();
    }

    public static String formatGenres(List<Genre> genres) {
        if (genres.isEmpty()) {
            return "Не указаны";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < genres.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(genres.get(i).getName()).append(" (ID: ").append(genres.get(i).getId()).append(")");
        }
        return sb.toString();
    }

    public static void printHeader(String title) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  " + title);
        System.out.println("=".repeat(50));
    }

    public static void printSubHeader(String title) {
        System.out.println("\n--- " + title + " ---");
    }

    public static void printSuccess(String message) {
        System.out.println("[OK] " + message);
    }

    public static void printError(String message) {
        System.err.println("[ERROR] " + message);
    }

    public static void printInfo(String message) {
        System.out.println("[INFO] " + message);
    }

    public static void printSeparator() {
        System.out.println("-".repeat(50));
    }
}