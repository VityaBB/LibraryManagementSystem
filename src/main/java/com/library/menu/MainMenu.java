package com.library.menu;

import com.library.DatabaseConnection;
import com.library.dao.BookDAO;
import com.library.models.Book;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static com.library.menu.ConsoleUtils.*;

public class MainMenu {
    private static final String HEADER_TOP = "╔══════════════════════════════════════════════╗";
    private static final String HEADER_MID = "╠══════════════════════════════════════════════╣";
    private static final String HEADER_BOTTOM = "╚══════════════════════════════════════════════╝";

    public void start() {
        DatabaseConnection.testConnection();
        System.out.println();

        try (Connection connection = DatabaseConnection.getConnection()) {
            BookMenu bookMenu = new BookMenu(connection);
            AuthorMenu authorMenu = new AuthorMenu(connection);
            UserMenu userMenu = new UserMenu(connection);
            LoanMenu loanMenu = new LoanMenu(connection);
            PublisherMenu publisherMenu = new PublisherMenu(connection);

            while (true) {
                printMainMenu();
                int choice = readInt("Выберите действие: ", 0, 6);

                switch (choice) {
                    case 1 -> bookMenu.showMenu();
                    case 2 -> authorMenu.showMenu();
                    case 3 -> userMenu.showMenu();
                    case 4 -> loanMenu.showMenu();
                    case 5 -> publisherMenu.showMenu();
                    case 6 -> searchBooks(connection);
                    case 0 -> {
                        System.out.println("\nДо свидания!");
                        return;
                    }
                    default -> printError("Неверный выбор");
                }
            }
        } catch (SQLException e) {
            printError("Не удалось подключиться к базе данных: " + e.getMessage());
        }
    }

    private void printMainMenu() {
        System.out.println("\n" + HEADER_TOP);
        System.out.println("║               ГЛАВНОЕ МЕНЮ                   ║");
        System.out.println(HEADER_MID);
        System.out.println("║  1.  Управление книгами                      ║");
        System.out.println("║  2.  Управление авторами                     ║");
        System.out.println("║  3.  Управление пользователями               ║");
        System.out.println("║  4.  Управление выдачами                     ║");
        System.out.println("║  5.  Управление издателями                   ║");
        System.out.println("║  6.  Поиск книг                              ║");
        System.out.println("║  0.  Выход                                   ║");
        System.out.println(HEADER_BOTTOM);
    }

    private void searchBooks(Connection connection) {
        printHeader("ПОИСК КНИГ");
        String title = readString("Название (или часть): ");
        String author = readString("Автор (или часть): ");
        String genre = readString("Жанр: ");

        try {
            BookDAO bookDAO = new BookDAO(connection);
            int total = bookDAO.countBooks(title, author, genre);
            if (total == 0) {
                printInfo("Книг не найдено");
                return;
            }

            int page = 1;
            int pageSize = 5;
            int totalPages = (int) Math.ceil((double) total / pageSize);

            while (true) {
                System.out.println("\nРезультаты поиска (стр. " + page + "/" + totalPages + ", всего: " + total + ")");
                List<Book> books = bookDAO.searchBooks(title, author, genre, page, pageSize);
                printBookList(books);

                if (page < totalPages) {
                    System.out.println("Нажмите Enter для следующей страницы, или q для выхода");
                    String input = readString("");
                    if (input.equalsIgnoreCase("q")) break;
                    page++;
                } else {
                    break;
                }
            }
        } catch (SQLException e) {
            printError("Ошибка поиска: " + e.getMessage());
        }
    }
}