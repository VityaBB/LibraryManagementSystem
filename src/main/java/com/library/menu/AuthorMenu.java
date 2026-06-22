package com.library.menu;

import com.library.dao.AuthorDAO;
import com.library.models.Author;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static com.library.menu.ConsoleUtils.*;

public class AuthorMenu {
    private final AuthorDAO authorDAO;

    public AuthorMenu(Connection connection) {
        this.authorDAO = new AuthorDAO(connection);
    }

    public void showMenu() {
        while (true) {
            printHeader("УПРАВЛЕНИЕ АВТОРАМИ");
            System.out.println("1. Добавить автора");
            System.out.println("2. Список всех авторов");
            System.out.println("3. Найти автора по ID");
            System.out.println("4. Обновить автора");
            System.out.println("5. Удалить автора");
            System.out.println("0. Назад");
            printSeparator();

            int choice = readInt("Выберите действие: ", 0, 5);
            try {
                switch (choice) {
                    case 1 -> addAuthor();
                    case 2 -> listAllAuthors();
                    case 3 -> findAuthorById();
                    case 4 -> updateAuthor();
                    case 5 -> deleteAuthor();
                    case 0 -> { return; }
                    default -> printError("Неверный выбор");
                }
            } catch (SQLException e) {
                printError("Ошибка БД: " + e.getMessage());
            }
        }
    }

    private void addAuthor() throws SQLException {
        printHeader("ДОБАВЛЕНИЕ НОВОГО АВТОРА");
        Author author = new Author();
        author.setFirstName(readString("Имя: "));
        author.setLastName(readString("Фамилия: "));

        String birthDateStr = readString("Дата рождения (ГГГГ-ММ-ДД): ");
        if (!birthDateStr.trim().isEmpty()) {
            try {
                java.sql.Date sqlDate = java.sql.Date.valueOf(birthDateStr);
                author.setBirthDate(sqlDate.toString());
            } catch (IllegalArgumentException e) {
                printError("Неверный формат даты. Используйте ГГГГ-ММ-ДД");
                return;
            }
        }
        author.setBiography(readString("Биография: "));
        authorDAO.addAuthor(author);
        printSuccess("Автор добавлен! ID: " + author.getId());
    }

    private void listAllAuthors() throws SQLException {
        printHeader("СПИСОК ВСЕХ АВТОРОВ");
        List<Author> authors = authorDAO.getAllAuthors();
        if (authors.isEmpty()) {
            printInfo("Авторов не найдено");
            return;
        }

        for (Author a : authors) {
            String birth = a.getBirthDate() != null ? a.getBirthDate() : "Не указана";
            String bio = a.getBiography() != null ? a.getBiography() : "Нет";
            System.out.println("ID: " + a.getId() + " | " + a.getFullName() + " | Дата: " + birth + " | " + bio);
        }
        printInfo("Всего авторов: " + authors.size());
    }

    private void findAuthorById() throws SQLException {
        printHeader("ПОИСК АВТОРА ПО ID");
        int id = readInt("Введите ID автора: ", 1, Integer.MAX_VALUE);
        Author author = authorDAO.getAuthorById(id);
        if (author == null) {
            printError("Автор с ID " + id + " не найден");
            return;
        }

        printTable("ИНФОРМАЦИЯ ОБ АВТОРЕ",
            "ID:            " + author.getId(),
            "Имя:           " + author.getFirstName(),
            "Фамилия:       " + author.getLastName(),
            "Дата рождения: " + (author.getBirthDate() != null ? author.getBirthDate() : "Не указана"),
            "Биография:     " + (author.getBiography() != null ? author.getBiography() : "Нет")
        );
    }

    private void updateAuthor() throws SQLException {
        printHeader("ОБНОВЛЕНИЕ АВТОРА");
        int id = readInt("Введите ID автора: ", 1, Integer.MAX_VALUE);
        Author author = authorDAO.getAuthorById(id);
        if (author == null) {
            printError("Автор не найден");
            return;
        }

        printInfo("Текущие данные:");
        System.out.println("Имя: " + author.getFirstName());
        System.out.println("Фамилия: " + author.getLastName());
        System.out.println("Дата рождения: " + (author.getBirthDate() != null ? author.getBirthDate() : "Не указана"));
        System.out.println("Биография: " + (author.getBiography() != null ? author.getBiography() : "Нет"));

        printInfo("Введите новые данные (оставьте пустым для сохранения текущего значения)");

        String firstName = readString("Имя (" + author.getFirstName() + "): ");
        if (!firstName.trim().isEmpty()) author.setFirstName(firstName);

        String lastName = readString("Фамилия (" + author.getLastName() + "): ");
        if (!lastName.trim().isEmpty()) author.setLastName(lastName);

        String birthDate = readString("Дата рождения (" + (author.getBirthDate() != null ? author.getBirthDate() : "") + "): ");
        if (!birthDate.trim().isEmpty()) author.setBirthDate(birthDate);

        String biography = readString("Биография (" + (author.getBiography() != null ? author.getBiography() : "") + "): ");
        if (!biography.trim().isEmpty()) author.setBiography(biography);

        authorDAO.updateAuthor(author);
        printSuccess("Автор обновлён!");
    }

    private void deleteAuthor() throws SQLException {
        printHeader("УДАЛЕНИЕ АВТОРА");
        int id = readInt("Введите ID автора: ", 1, Integer.MAX_VALUE);
        Author author = authorDAO.getAuthorById(id);
        if (author == null) {
            printError("Автор не найден");
            return;
        }
        System.out.println("Автор: " + author.getFullName());
        if (readYesNo("Вы уверены? (y/n): ")) {
            authorDAO.deleteAuthor(id);
            printSuccess("Автор удалён!");
        } else {
            printInfo("Операция отменена");
        }
    }
}