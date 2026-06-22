package com.library.menu;

import com.library.dao.PublisherDAO;
import com.library.models.Publisher;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static com.library.menu.ConsoleUtils.*;

public class PublisherMenu {
    private final PublisherDAO publisherDAO;

    public PublisherMenu(Connection connection) {
        this.publisherDAO = new PublisherDAO(connection);
    }

    public void showMenu() {
        while (true) {
            printHeader("УПРАВЛЕНИЕ ИЗДАТЕЛЯМИ");
            System.out.println("1. Добавить издателя");
            System.out.println("2. Список всех издателей");
            System.out.println("3. Найти издателя по ID");
            System.out.println("4. Обновить издателя");
            System.out.println("5. Удалить издателя");
            System.out.println("0. Назад");
            printSeparator();

            int choice = readInt("Выберите действие: ", 0, 5);
            try {
                switch (choice) {
                    case 1 -> addPublisher();
                    case 2 -> listAllPublishers();
                    case 3 -> findPublisherById();
                    case 4 -> updatePublisher();
                    case 5 -> deletePublisher();
                    case 0 -> { return; }
                    default -> printError("Неверный выбор");
                }
            } catch (SQLException e) {
                printError("Ошибка БД: " + e.getMessage());
            }
        }
    }

    private void addPublisher() throws SQLException {
        printHeader("ДОБАВЛЕНИЕ НОВОГО ИЗДАТЕЛЯ");
        Publisher publisher = new Publisher();
        publisher.setName(readString("Название издательства: "));
        publisher.setAddress(readString("Адрес: "));
        publisher.setPhone(readString("Телефон: "));
        publisher.setEmail(readString("Email: "));
        publisher.setWebsite(readString("Сайт: "));
        publisherDAO.addPublisher(publisher);
        printSuccess("Издатель добавлен! ID: " + publisher.getId());
    }

    private void listAllPublishers() throws SQLException {
        printHeader("СПИСОК ВСЕХ ИЗДАТЕЛЕЙ");
        List<Publisher> publishers = publisherDAO.getAllPublishers();
        if (publishers.isEmpty()) {
            printInfo("Издателей не найдено");
            return;
        }
        for (Publisher p : publishers) {
            System.out.println("ID: " + p.getId() + " | " + p.getName() + " | " + (p.getEmail() != null ? p.getEmail() : "Нет email") + " | " + (p.getPhone() != null ? p.getPhone() : "Нет телефона"));
        }
        printInfo("Всего издателей: " + publishers.size());
    }

    private void findPublisherById() throws SQLException {
        printHeader("ПОИСК ИЗДАТЕЛЯ ПО ID");
        int id = readInt("Введите ID издателя: ", 1, Integer.MAX_VALUE);
        Publisher publisher = publisherDAO.getPublisherById(id);
        if (publisher == null) {
            printError("Издатель с ID " + id + " не найден");
            return;
        }

        printTable("ИНФОРМАЦИЯ ОБ ИЗДАТЕЛЕ",
            "ID:          " + publisher.getId(),
            "Название:    " + publisher.getName(),
            "Адрес:       " + (publisher.getAddress() != null ? publisher.getAddress() : "Не указан"),
            "Телефон:     " + (publisher.getPhone() != null ? publisher.getPhone() : "Не указан"),
            "Email:       " + (publisher.getEmail() != null ? publisher.getEmail() : "Не указан"),
            "Сайт:        " + (publisher.getWebsite() != null ? publisher.getWebsite() : "Не указан")
        );
    }

    private void updatePublisher() throws SQLException {
        printHeader("ОБНОВЛЕНИЕ ИЗДАТЕЛЯ");
        int id = readInt("Введите ID издателя: ", 1, Integer.MAX_VALUE);
        Publisher publisher = publisherDAO.getPublisherById(id);
        if (publisher == null) {
            printError("Издатель не найден");
            return;
        }

        printInfo("Текущие данные:");
        System.out.println("Название: " + publisher.getName());
        System.out.println("Адрес: " + (publisher.getAddress() != null ? publisher.getAddress() : "Не указан"));
        System.out.println("Телефон: " + (publisher.getPhone() != null ? publisher.getPhone() : "Не указан"));
        System.out.println("Email: " + (publisher.getEmail() != null ? publisher.getEmail() : "Не указан"));
        System.out.println("Сайт: " + (publisher.getWebsite() != null ? publisher.getWebsite() : "Не указан"));

        printInfo("Введите новые данные (оставьте пустым для сохранения текущего значения)");

        String name = readString("Название (" + publisher.getName() + "): ");
        if (!name.trim().isEmpty()) publisher.setName(name);

        String address = readString("Адрес (" + (publisher.getAddress() != null ? publisher.getAddress() : "") + "): ");
        if (!address.trim().isEmpty()) publisher.setAddress(address);

        String phone = readString("Телефон (" + (publisher.getPhone() != null ? publisher.getPhone() : "") + "): ");
        if (!phone.trim().isEmpty()) publisher.setPhone(phone);

        String email = readString("Email (" + (publisher.getEmail() != null ? publisher.getEmail() : "") + "): ");
        if (!email.trim().isEmpty()) publisher.setEmail(email);

        String website = readString("Сайт (" + (publisher.getWebsite() != null ? publisher.getWebsite() : "") + "): ");
        if (!website.trim().isEmpty()) publisher.setWebsite(website);

        publisherDAO.updatePublisher(publisher);
        printSuccess("Издатель обновлён!");
    }

    private void deletePublisher() throws SQLException {
        printHeader("УДАЛЕНИЕ ИЗДАТЕЛЯ");
        int id = readInt("Введите ID издателя: ", 1, Integer.MAX_VALUE);
        Publisher publisher = publisherDAO.getPublisherById(id);
        if (publisher == null) {
            printError("Издатель не найден");
            return;
        }
        System.out.println("Издатель: " + publisher.getName());
        if (readYesNo("Вы уверены? (y/n): ")) {
            try {
                publisherDAO.deletePublisher(id);
                printSuccess("Издатель удалён!");
            } catch (SQLException e) {
                if (e.getMessage().contains("foreign key")) {
                    printError("Невозможно удалить издателя: есть книги, привязанные к нему");
                } else {
                    throw e;
                }
            }
        } else {
            printInfo("Операция отменена");
        }
    }
}