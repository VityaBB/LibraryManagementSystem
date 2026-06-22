package com.library.menu;

import com.library.dao.UserDAO;
import com.library.dao.LoanDAO;
import com.library.models.User;
import com.library.models.Book;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static com.library.menu.ConsoleUtils.*;

public class UserMenu {
    private final UserDAO userDAO;
    private final LoanDAO loanDAO;

    public UserMenu(Connection connection) {
        this.userDAO = new UserDAO(connection);
        this.loanDAO = new LoanDAO(connection);
    }

    public void showMenu() {
        while (true) {
            printHeader("УПРАВЛЕНИЕ ПОЛЬЗОВАТЕЛЯМИ");
            System.out.println("1. Добавить пользователя");
            System.out.println("2. Список всех пользователей");
            System.out.println("3. Найти пользователя по ID");
            System.out.println("4. Обновить пользователя");
            System.out.println("5. Удалить пользователя");
            System.out.println("0. Назад");
            printSeparator();

            int choice = readInt("Выберите действие: ", 0, 5);
            try {
                switch (choice) {
                    case 1 -> addUser();
                    case 2 -> listAllUsers();
                    case 3 -> findUserById();
                    case 4 -> updateUser();
                    case 5 -> deleteUser();
                    case 0 -> { return; }
                    default -> printError("Неверный выбор");
                }
            } catch (SQLException e) {
                printError("Ошибка БД: " + e.getMessage());
            }
        }
    }

    private void addUser() throws SQLException {
        printHeader("ДОБАВЛЕНИЕ НОВОГО ПОЛЬЗОВАТЕЛЯ");
        User user = new User();
        user.setEmail(readString("Email: "));
        user.setPasswordHash(readString("Пароль (хеш): "));
        user.setFirstName(readString("Имя: "));
        user.setLastName(readString("Фамилия: "));
        user.setPhone(readString("Телефон: "));
        user.setAddress(readString("Адрес: "));
        user.setRole(readString("Роль (READER/LIBRARIAN/ADMIN): "));
        userDAO.addUser(user);
        printSuccess("Пользователь добавлен! ID: " + user.getId());
    }

    private void listAllUsers() throws SQLException {
        printHeader("СПИСОК ВСЕХ ПОЛЬЗОВАТЕЛЕЙ");
        List<User> users = userDAO.getAllUsers();
        if (users.isEmpty()) {
            printInfo("Пользователей не найдено");
            return;
        }
        for (User u : users) {
            System.out.println("ID: " + u.getId() + " | " + u.getFullName() + " | " + u.getEmail() + " | " + u.getRole() + " | " + (u.isActive() ? "Активен" : "Неактивен"));
        }
        printInfo("Всего пользователей: " + users.size());
    }

    private void findUserById() throws SQLException {
        printHeader("ПОИСК ПОЛЬЗОВАТЕЛЯ ПО ID");
        int id = readInt("Введите ID пользователя: ", 1, Integer.MAX_VALUE);
        User user = userDAO.getUserById(id);
        if (user == null) {
            printError("Пользователь с ID " + id + " не найден");
            return;
        }

        List<Book> activeBooks = loanDAO.getActiveBooksByUserId(id);

        printTable("ИНФОРМАЦИЯ О ПОЛЬЗОВАТЕЛЕ",
            "ID:            " + user.getId(),
            "Имя:           " + user.getFirstName(),
            "Фамилия:       " + user.getLastName(),
            "Email:         " + user.getEmail(),
            "Телефон:       " + (user.getPhone() != null ? user.getPhone() : "Не указан"),
            "Адрес:         " + (user.getAddress() != null ? user.getAddress() : "Не указан"),
            "Роль:          " + user.getRole(),
            "Активен:       " + (user.isActive() ? "Да" : "Нет"),
            "Книги на руках: " + (activeBooks.isEmpty() ? "Нет активных выдач" :
                activeBooks.stream().map(b -> b.getTitle() + " (ID: " + b.getId() + ")")
                    .reduce((a, b) -> a + ", " + b).orElse(""))
        );
    }

    private void updateUser() throws SQLException {
        printHeader("ОБНОВЛЕНИЕ ПОЛЬЗОВАТЕЛЯ");
        int id = readInt("Введите ID пользователя: ", 1, Integer.MAX_VALUE);
        User user = userDAO.getUserById(id);
        if (user == null) {
            printError("Пользователь не найден");
            return;
        }

        printInfo("Текущие данные:");
        System.out.println("Имя: " + user.getFirstName());
        System.out.println("Фамилия: " + user.getLastName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Роль: " + user.getRole());

        printInfo("Введите новые данные (оставьте пустым для сохранения текущего значения)");

        String firstName = readString("Имя (" + user.getFirstName() + "): ");
        if (!firstName.trim().isEmpty()) user.setFirstName(firstName);

        String lastName = readString("Фамилия (" + user.getLastName() + "): ");
        if (!lastName.trim().isEmpty()) user.setLastName(lastName);

        String email = readString("Email (" + user.getEmail() + "): ");
        if (!email.trim().isEmpty()) user.setEmail(email);

        String role = readString("Роль (" + user.getRole() + "): ");
        if (!role.trim().isEmpty()) user.setRole(role);

        String active = readString("Активен (true/false) (" + user.isActive() + "): ");
        if (!active.trim().isEmpty()) user.setActive(Boolean.parseBoolean(active));

        userDAO.updateUser(user);
        printSuccess("Пользователь обновлён!");
    }

    private void deleteUser() throws SQLException {
        printHeader("УДАЛЕНИЕ ПОЛЬЗОВАТЕЛЯ");
        int id = readInt("Введите ID пользователя: ", 1, Integer.MAX_VALUE);
        User user = userDAO.getUserById(id);
        if (user == null) {
            printError("Пользователь не найден");
            return;
        }
        System.out.println("Пользователь: " + user.getFullName() + " (" + user.getEmail() + ")");
        if (readYesNo("Вы уверены? (y/n): ")) {
            userDAO.deleteUser(id);
            printSuccess("Пользователь удалён!");
        } else {
            printInfo("Операция отменена");
        }
    }
}