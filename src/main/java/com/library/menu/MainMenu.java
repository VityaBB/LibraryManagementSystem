package com.library.menu;

import com.library.DatabaseConnection;
import com.library.dao.*;
import com.library.models.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;


public class MainMenu {
    private final Scanner scanner = new Scanner(System.in);
    private final BookDAO bookDAO = new BookDAO();
    private final AuthorDAO authorDAO = new AuthorDAO();
    private final UserDAO userDAO = new UserDAO();
    private final LoanDAO loanDAO = new LoanDAO();
    private final PublisherDAO publisherDAO = new PublisherDAO();
    private final GenreDAO genreDAO = new GenreDAO();
    
    public void start() {
        DatabaseConnection.testConnection();
        System.out.println();

        while (true) {
            printMainMenu();
            int choice = readInt("Выберите действие: ", 0, 6);
            
            switch (choice) {
                case 1 -> manageBooks();
                case 2 -> manageAuthors();
                case 3 -> manageUsers();
                case 4 -> manageLoans();
                case 5 -> managePublishers();
                case 6 -> searchBooks();
                case 0 -> {
                    System.out.println("\n До свидания!");
                    return;
                }
            }
        }
    }

    private void printMainMenu() {
    System.out.println("\n╔══════════════════════════════════════════════╗");
    System.out.println("║               ГЛАВНОЕ МЕНЮ                   ║");
    System.out.println("╠══════════════════════════════════════════════╣");
    System.out.println("║  1.  Управление книгами                      ║");
    System.out.println("║  2.  Управление авторами                     ║");
    System.out.println("║  3.  Управление пользователями               ║");
    System.out.println("║  4.  Управление выдачами                     ║");
    System.out.println("║  5.  Управление издателями                   ║");
    System.out.println("║  6.  Поиск книг                              ║");
    System.out.println("║  0.  Выход                                   ║");
    System.out.println("╚══════════════════════════════════════════════╝");
}

//BOOKS
    private void manageBooks() {
        while (true) {
            System.out.println("\n╔══════════════════════════════════════════════╗");
            System.out.println("║            УПРАВЛЕНИЕ КНИГАМИ                ║");
            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.println("║  1.  Добавить книгу                          ║");
            System.out.println("║  2.  Список всех книг                        ║");
            System.out.println("║  3.  Найти книгу по ID                       ║");
            System.out.println("║  4.  Обновить книгу                          ║");
            System.out.println("║  5.  Удалить книгу                           ║");
            System.out.println("║  0.  Назад                                   ║");
            System.out.println("╚══════════════════════════════════════════════╝");
            
            int choice = readInt("Выберите действие: ", 0, 5);
            
            try {
                switch (choice) {
                    case 1 -> addBook();
                    case 2 -> listAllBooks();
                    case 3 -> findBookById();
                    case 4 -> updateBook();
                    case 5 -> deleteBook();
                    case 0 -> { return; }
                }
            } catch (SQLException e) {
                System.err.println(" Ошибка БД: " + e.getMessage());
            }
        }
    }

   private void addBook() throws SQLException {
    System.out.println("\nДОБАВЛЕНИЕ НОВОЙ КНИГИ");

    System.out.println("Список доступных издателей:");
    List<Publisher> publishers = publisherDAO.getAllPublishers();
    if (publishers.isEmpty()) {
        System.out.println("В базе нет издателей!");
        System.out.println("Сначала добавьте издателя через меню 'Управление издателями'");
        return;
    }
    for (Publisher p : publishers) {
        System.out.println("  ID: " + p.getId() + " | " + p.getName());
    }
    System.out.println();

    System.out.println("Список доступных жанров:");
    List<Genre> genres = genreDAO.getAllGenres();
    if (genres.isEmpty()) {
        System.out.println("В базе нет жанров!");
        System.out.println("Сначала добавьте жанры через SQL");
        return;
    }
    for (Genre g : genres) {
        System.out.println("  ID: " + g.getId() + " | " + g.getName());
    }
    System.out.println();

    System.out.println("Список доступных авторов:");
    List<Author> authors = authorDAO.getAllAuthors();
    if (authors.isEmpty()) {
        System.out.println("В базе нет авторов!");
        System.out.println("Сначала добавьте авторов через меню 'Управление авторами'");
        return;
    }
    for (Author a : authors) {
        System.out.println("  ID: " + a.getId() + " | " + a.getFullName());
    }
    System.out.println();

    Book book = new Book();
    book.setTitle(readString("Название: "));
    book.setIsbn(readString("ISBN: "));
    book.setPublicationYear(readInt("Год публикации: ", 1, 9999));

    int publisherId = readInt("ID издательства: ", 1, Integer.MAX_VALUE);
    Publisher publisher = publisherDAO.getPublisherById(publisherId);
    if (publisher == null) {
        System.out.println("Издатель с ID " + publisherId + " не найден");
        return;
    }
    book.setPublisherId(publisherId);

    book.setTotalCopies(readInt("Всего экземпляров: ", 0, Integer.MAX_VALUE));
    book.setPageCount(readInt("Количество страниц: ", 0, Integer.MAX_VALUE));
    book.setDescription(readString("Описание: "));

    bookDAO.addBook(book);
    System.out.println("Книга добавлена! ID: " + book.getId());

    System.out.println("\nДобавление жанров к книге");
    String genreIdsInput = readString("Введите ID жанров через запятую (например: 1,2,3): ");
    if (!genreIdsInput.trim().isEmpty()) {
        String[] ids = genreIdsInput.split(",");
        for (String idStr : ids) {
            try {
                int genreId = Integer.parseInt(idStr.trim());
                bookDAO.addBookGenre(book.getId(), genreId);
                System.out.println("  Жанр ID " + genreId + " добавлен");
            } catch (NumberFormatException e) {
                System.out.println("  ID '" + idStr + "' не является числом");
            }
        }
    }

    System.out.println("\nДобавление авторов к книге");
    String authorIdsInput = readString("Введите ID авторов через запятую (например: 1,2,3): ");
    if (!authorIdsInput.trim().isEmpty()) {
        String[] ids = authorIdsInput.split(",");
        int order = 1;
        for (String idStr : ids) {
            try {
                int authorId = Integer.parseInt(idStr.trim());
                bookDAO.addBookAuthor(book.getId(), authorId, order);
                System.out.println("  Автор ID " + authorId + " добавлен (порядок: " + order + ")");
                order++;
            } catch (NumberFormatException e) {
                System.out.println("  ID '" + idStr + "' не является числом");
            }
        }
    }

    System.out.println("Книга добавлена!");
}

    private void listAllBooks() throws SQLException {
        System.out.println("\n СПИСОК ВСЕХ КНИГ");
        List<Book> books = bookDAO.getAllBooks();
        printBookList(books);
    }
    
   private void findBookById() throws SQLException {
    System.out.println("\nПОИСК КНИГИ ПО ID");
    int id = readInt("Введите ID книги: ", 1, Integer.MAX_VALUE);
    Book book = bookDAO.getBookById(id);
    if (book == null) {
        System.out.println("Книга с ID " + id + " не найдена");
        return;
    }
    
    Publisher publisher = publisherDAO.getPublisherById(book.getPublisherId());
    String publisherInfo = (publisher != null) ? 
        publisher.getName() + " (ID: " + publisher.getId() + ")" : "Не указан";
    
    List<Author> authors = bookDAO.getAuthorsByBookId(id);
    String authorsStr;
    if (authors.isEmpty()) {
        authorsStr = "Не указаны";
    } else {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < authors.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(authors.get(i).getFullName())
              .append(" (ID: ").append(authors.get(i).getId()).append(")");
        }
        authorsStr = sb.toString();
    }
    
    List<Genre> genres = bookDAO.getGenresByBookId(id);
    String genresStr;
    if (genres.isEmpty()) {
        genresStr = "Не указаны";
    } else {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < genres.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(genres.get(i).getName())
              .append(" (ID: ").append(genres.get(i).getId()).append(")");
        }
        genresStr = sb.toString();
    }
    
    int available = bookDAO.getAvailableCopies(id);
    
    String idStr = "ID книги:          " + book.getId();
    String titleStr = "Название:          " + book.getTitle();
    String isbnStr = "ISBN:              " + book.getIsbn();
    String yearStr = "Год публикации:    " + book.getPublicationYear();
    String publisherStr = "Издательство:      " + publisherInfo;
    String authorsStrFull = "Авторы:            " + authorsStr;
    String genresStrFull = "Жанры:             " + genresStr;
    String totalStr = "Всего экземпляров: " + book.getTotalCopies();
    String availableStr = "Доступно:          " + available;
    String pagesStr = "Страниц:           " + book.getPageCount();
    String descStr = "Описание:          " + (book.getDescription() != null ? book.getDescription() : "Нет описания");
    
    int maxLen = Math.max(
        Math.max(idStr.length(), titleStr.length()),
        Math.max(isbnStr.length(), yearStr.length())
    );
    maxLen = Math.max(maxLen, publisherStr.length());
    maxLen = Math.max(maxLen, authorsStrFull.length());
    maxLen = Math.max(maxLen, genresStrFull.length());
    maxLen = Math.max(maxLen, totalStr.length());
    maxLen = Math.max(maxLen, availableStr.length());
    maxLen = Math.max(maxLen, pagesStr.length());
    maxLen = Math.max(maxLen, descStr.length());
    
    int width = maxLen + 2;
    
    String topLine = "┌" + "─".repeat(width) + "┐";
    String midLine = "├" + "─".repeat(width) + "┤";
    String bottomLine = "└" + "─".repeat(width) + "┘";
    
    System.out.println(topLine);
    System.out.println("│" + ConsoleUtils.centerString("ИНФОРМАЦИЯ О КНИГЕ", width) + "│");
    System.out.println(midLine);
    System.out.println("│ " + ConsoleUtils.padRight(idStr, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(titleStr, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(isbnStr, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(yearStr, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(publisherStr, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(authorsStrFull, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(genresStrFull, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(totalStr, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(availableStr, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(pagesStr, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(descStr, width - 1) + "│");
    System.out.println(bottomLine);
}
    

    
   private void updateBook() throws SQLException {
        System.out.println("\nОБНОВЛЕНИЕ КНИГИ");
        int id = readInt("Введите ID книги: ", 1, Integer.MAX_VALUE);
        Book book = bookDAO.getBookById(id);
        if (book == null) {
            System.out.println("Книга не найдена");
            return;
        }

        System.out.println("Текущие данные:");
        System.out.println(book.toFullString());

        List<Author> currentAuthors = bookDAO.getAuthorsByBookId(id);
        System.out.println("\nТекущие авторы:");
        if (currentAuthors.isEmpty()) {
            System.out.println("  (нет)");
        } else {
            for (Author a : currentAuthors) {
                System.out.println("  ID: " + a.getId() + " | " + a.getFullName());
            }
        }

        List<Genre> currentGenres = bookDAO.getGenresByBookId(id);
        System.out.println("\nТекущие жанры:");
        if (currentGenres.isEmpty()) {
            System.out.println("  (нет)");
        } else {
            for (Genre g : currentGenres) {
                System.out.println("  ID: " + g.getId() + " | " + g.getName());
            }
        }

        System.out.println("\nВведите новые данные (оставьте пустым для сохранения текущего значения)");

        String title = readString("Название (" + book.getTitle() + "): ");
        if (!title.trim().isEmpty()) book.setTitle(title);

        String isbn = readString("ISBN (" + book.getIsbn() + "): ");
        if (!isbn.trim().isEmpty()) book.setIsbn(isbn);

        String year = readString("Год публикации (" + book.getPublicationYear() + "): ");
        if (!year.trim().isEmpty()) book.setPublicationYear(Integer.parseInt(year));

        String copies = readString("Всего экземпляров (" + book.getTotalCopies() + "): ");
        if (!copies.trim().isEmpty()) book.setTotalCopies(Integer.parseInt(copies));

        String pages = readString("Количество страниц (" + book.getPageCount() + "): ");
        if (!pages.trim().isEmpty()) book.setPageCount(Integer.parseInt(pages));

        String description = readString("Описание (" + (book.getDescription() != null ? book.getDescription() : "") + "): ");
        if (!description.trim().isEmpty()) book.setDescription(description);

        bookDAO.updateBook(book);
        System.out.println("Основные данные книги обновлены!");

        System.out.println("\n--- УПРАВЛЕНИЕ ЖАНРАМИ ---");
        System.out.println("1. Добавить жанры");
        System.out.println("2. Удалить жанры");
        System.out.println("3. Заменить все жанры");
        System.out.println("0. Пропустить");
        int genreChoice = readInt("Выберите действие: ", 0, 3);

        List<Genre> allGenres = genreDAO.getAllGenres();
        System.out.println("Доступные жанры:");
        for (Genre g : allGenres) {
            System.out.println("  ID: " + g.getId() + " | " + g.getName());
        }

        switch (genreChoice) {
            case 1 -> {
                String addGenres = readString("Введите ID жанров для добавления через запятую: ");
                if (!addGenres.trim().isEmpty()) {
                    String[] ids = addGenres.split(",");
                    for (String idStr : ids) {
                        try {
                            int genreId = Integer.parseInt(idStr.trim());
                            boolean exists = currentGenres.stream().anyMatch(g -> g.getId() == genreId);
                            if (!exists) {
                                bookDAO.addBookGenre(id, genreId);
                                System.out.println("  Жанр ID " + genreId + " добавлен");
                            } else {
                                System.out.println("  Жанр ID " + genreId + " уже есть");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("  ID '" + idStr + "' не является числом");
                        }
                    }
                    System.out.println("Жанры добавлены!");
                }
            }
            case 2 -> {
                if (currentGenres.isEmpty()) {
                    System.out.println("У книги нет жанров для удаления");
                } else {
                    String removeGenres = readString("Введите ID жанров для удаления через запятую: ");
                    if (!removeGenres.trim().isEmpty()) {
                        String[] ids = removeGenres.split(",");
                        for (String idStr : ids) {
                            try {
                                int genreId = Integer.parseInt(idStr.trim());
                                bookDAO.deleteBookGenre(id, genreId);
                                System.out.println("  Жанр ID " + genreId + " удален");
                            } catch (NumberFormatException e) {
                                System.out.println("  ID '" + idStr + "' не является числом");
                            }
                        }
                    }
                }
            }
            case 3 -> {
                String newGenres = readString("Введите новые ID жанров через запятую (или Enter для очистки): ");
                bookDAO.deleteBookGenres(id);
                if (!newGenres.trim().isEmpty()) {
                    String[] ids = newGenres.split(",");
                    for (String idStr : ids) {
                        try {
                            int genreId = Integer.parseInt(idStr.trim());
                            bookDAO.addBookGenre(id, genreId);
                            System.out.println("  Жанр ID " + genreId + " добавлен");
                        } catch (NumberFormatException e) {
                            System.out.println("  ID '" + idStr + "' не является числом");
                        }
                    }
                }
                System.out.println("Жанры обновлены!");
            }
            default -> System.out.println("Жанры не изменены");
        }

        System.out.println("\n--- УПРАВЛЕНИЕ АВТОРАМИ ---");
        System.out.println("1. Добавить авторов");
        System.out.println("2. Удалить авторов");
        System.out.println("3. Заменить всех авторов");
        System.out.println("0. Пропустить");
        int authorChoice = readInt("Выберите действие: ", 0, 3);

        List<Author> allAuthors = authorDAO.getAllAuthors();
        System.out.println("Доступные авторы:");
        for (Author a : allAuthors) {
            System.out.println("  ID: " + a.getId() + " | " + a.getFullName());
        }

        switch (authorChoice) {
            case 1 -> {
                String addAuthors = readString("Введите ID авторов для добавления через запятую: ");
                if (!addAuthors.trim().isEmpty()) {
                    String[] ids = addAuthors.split(",");
                    int maxOrder = currentAuthors.size();
                    for (String idStr : ids) {
                        try {
                            int authorId = Integer.parseInt(idStr.trim());
                            boolean exists = currentAuthors.stream().anyMatch(a -> a.getId() == authorId);
                            if (!exists) {
                                maxOrder++;
                                bookDAO.addBookAuthor(id, authorId, maxOrder);
                                System.out.println("  Автор ID " + authorId + " добавлен (порядок: " + maxOrder + ")");
                            } else {
                                System.out.println("  Автор ID " + authorId + " уже есть");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("  ID '" + idStr + "' не является числом");
                        }
                    }
                    System.out.println("Авторы добавлены!");
                }
            }
            case 2 -> {
                if (currentAuthors.isEmpty()) {
                    System.out.println("У книги нет авторов для удаления");
                } else {
                    String removeAuthors = readString("Введите ID авторов для удаления через запятую: ");
                    if (!removeAuthors.trim().isEmpty()) {
                        String[] ids = removeAuthors.split(",");
                        for (String idStr : ids) {
                            try {
                                int authorId = Integer.parseInt(idStr.trim());
                                bookDAO.deleteBookAuthor(id, authorId);
                                System.out.println("  Автор ID " + authorId + " удален");
                            } catch (NumberFormatException e) {
                                System.out.println("  ID '" + idStr + "' не является числом");
                            }
                        }
                    }
                }
            }
            case 3 -> {
                String newAuthors = readString("Введите новые ID авторов через запятую (или Enter для очистки): ");
                bookDAO.deleteBookAuthors(id);
                if (!newAuthors.trim().isEmpty()) {
                    String[] ids = newAuthors.split(",");
                    int order = 1;
                    for (String idStr : ids) {
                        try {
                            int authorId = Integer.parseInt(idStr.trim());
                            bookDAO.addBookAuthor(id, authorId, order);
                            System.out.println("  Автор ID " + authorId + " добавлен (порядок: " + order + ")");
                            order++;
                        } catch (NumberFormatException e) {
                            System.out.println("  ID '" + idStr + "' не является числом");
                        }
                    }
                }
                System.out.println("Авторы обновлены!");
            }
            default -> System.out.println("Авторы не изменены");
        }

        System.out.println("\nКнига полностью обновлена!");
    }

    private void deleteBook() throws SQLException {
        System.out.println("\n УДАЛЕНИЕ КНИГИ");
        int id = readInt("Введите ID книги: ", 1, Integer.MAX_VALUE);
        Book book = bookDAO.getBookById(id);
        if (book == null) {
            System.out.println(" Книга не найдена");
            return;
        }
        
        System.out.println("Книга: " + book.getTitle());
        if (readYesNo("Вы уверены? (y/n): ")) {
            bookDAO.deleteBook(id);
            System.out.println(" Книга удалена!");
        } else {
            System.out.println(" Операция отменена");
        }
    }


//AUTHORS
private void manageAuthors() {
    while (true) {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║           УПРАВЛЕНИЕ АВТОРАМИ                ║");
        System.out.println("╠══════════════════════════════════════════════╣");
        System.out.println("║  1.  Добавить автора                         ║");
        System.out.println("║  2.  Список всех авторов                     ║");
        System.out.println("║  3.  Найти автора по ID                      ║");
        System.out.println("║  4.  Обновить автора                         ║");
        System.out.println("║  5.  Удалить автора                          ║");
        System.out.println("║  0.  Назад                                   ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        
        int choice = readInt("Выберите действие: ", 0, 5);
        
        try {
            switch (choice) {
                case 1 -> addAuthor();
                case 2 -> listAllAuthors();
                case 3 -> findAuthorById();
                case 4 -> updateAuthor();
                case 5 -> deleteAuthor();
                case 0 -> { return; }
            }
        } catch (SQLException e) {
            System.err.println(" Ошибка БД: " + e.getMessage());
        }
    }
}

private void addAuthor() throws SQLException {
    System.out.println("\n ДОБАВЛЕНИЕ НОВОГО АВТОРА");
    Author author = new Author();
    
    author.setFirstName(readString("Имя: "));
    author.setLastName(readString("Фамилия: "));
    
    String birthDateStr = readString("Дата рождения (ГГГГ-ММ-ДД): ");
    if (!birthDateStr.trim().isEmpty()) {
        try {
            java.sql.Date sqlDate = java.sql.Date.valueOf(birthDateStr);
            author.setBirthDate(sqlDate.toString());
        } catch (IllegalArgumentException e) {
            System.out.println(" Неверный формат даты. Используйте ГГГГ-ММ-ДД (например, 1875-12-08)");
            return;
        }
    }
    
    author.setBiography(readString("Биография: "));
    
    authorDAO.addAuthor(author);
    System.out.println(" Автор добавлен! ID: " + author.getId());
}

private void listAllAuthors() throws SQLException {
    System.out.println("\nСПИСОК ВСЕХ АВТОРОВ");
    List<Author> authors = authorDAO.getAllAuthors();
    if (authors.isEmpty()) {
        System.out.println("Авторов не найдено");
        return;
    }
    
    int idWidth = 4;
    int nameWidth = 30;
    int birthWidth = 14;
    int bioWidth = 30;
    
    for (Author a : authors) {
        idWidth = Math.max(idWidth, String.valueOf(a.getId()).length());
        nameWidth = Math.max(nameWidth, a.getFullName().length());
        birthWidth = Math.max(birthWidth, (a.getBirthDate() != null ? a.getBirthDate() : "Не указана").length());
        bioWidth = Math.max(bioWidth, (a.getBiography() != null && a.getBiography().length() > 30) ? 30 : 
            (a.getBiography() != null ? a.getBiography().length() : 4));
    }
    idWidth += 2;
    nameWidth += 2;
    birthWidth += 2;
    bioWidth += 2;
    
    String topLine = "┌" + "─".repeat(idWidth) + "┬" + "─".repeat(nameWidth) + 
                     "┬" + "─".repeat(birthWidth) + "┬" + "─".repeat(bioWidth) + "┐";
    String midLine = "├" + "─".repeat(idWidth) + "┼" + "─".repeat(nameWidth) + 
                     "┼" + "─".repeat(birthWidth) + "┼" + "─".repeat(bioWidth) + "┤";
    String bottomLine = "└" + "─".repeat(idWidth) + "┴" + "─".repeat(nameWidth) + 
                        "┴" + "─".repeat(birthWidth) + "┴" + "─".repeat(bioWidth) + "┘";
    

    System.out.println(topLine);
    
    System.out.printf("│ %-" + (idWidth - 1) + "s│ %-" + (nameWidth - 1) + "s│ %-" + (birthWidth - 1) + "s│ %-" + (bioWidth - 1) + "s│%n",
        "ID", "Имя и фамилия", "Дата рождения", "Биография");
    System.out.println(midLine);
    
    for (Author a : authors) {
        String bio = a.getBiography();
        if (bio != null && bio.length() > bioWidth - 1) {
            bio = bio.substring(0, bioWidth - 4) + "...";
        }
        String birth = a.getBirthDate() != null ? a.getBirthDate() : "Не указана";
        
        System.out.printf("│ %-" + (idWidth - 1) + "d│ %-" + (nameWidth - 1) + "s│ %-" + (birthWidth - 1) + "s│ %-" + (bioWidth - 1) + "s│%n",
            a.getId(),
            a.getFullName(),
            birth,
            bio != null ? bio : "Нет");
    }
    
    System.out.println(bottomLine);
}

private void findAuthorById() throws SQLException {
    System.out.println("\nПОИСК АВТОРА ПО ID");
    int id = readInt("Введите ID автора: ", 1, Integer.MAX_VALUE);
    Author author = authorDAO.getAuthorById(id);
    if (author == null) {
        System.out.println("Автор с ID " + id + " не найден");
        return;
    }
    
    String idStr = "ID:            " + author.getId();
    String firstNameStr = "Имя:           " + author.getFirstName();
    String lastNameStr = "Фамилия:       " + author.getLastName();
    String birthStr = "Дата рождения: " + (author.getBirthDate() != null ? author.getBirthDate() : "Не указана");
    String bioStr = "Биография:     " + (author.getBiography() != null ? author.getBiography() : "Нет");
    
    int maxLen = Math.max(
        Math.max(idStr.length(), firstNameStr.length()),
        Math.max(lastNameStr.length(), birthStr.length())
    );
    maxLen = Math.max(maxLen, bioStr.length());
    
    int width = maxLen + 2;
    
    String topLine = "┌" + "─".repeat(width) + "┐";
    String midLine = "├" + "─".repeat(width) + "┤";
    String bottomLine = "└" + "─".repeat(width) + "┘";
    
    System.out.println(topLine);
    System.out.println("│" + ConsoleUtils.centerString("ИНФОРМАЦИЯ ОБ АВТОРЕ", width) + "│");
    System.out.println(midLine);
    System.out.println("│ " + ConsoleUtils.padRight(idStr, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(firstNameStr, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(lastNameStr, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(birthStr, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(bioStr, width - 1) + "│");
    System.out.println(bottomLine);
}

private void updateAuthor() throws SQLException {
    System.out.println("\nОБНОВЛЕНИЕ АВТОРА");
    int id = readInt("Введите ID автора: ", 1, Integer.MAX_VALUE);
    Author author = authorDAO.getAuthorById(id);
    if (author == null) {
        System.out.println("Автор не найден");
        return;
    }
    
    System.out.println("Текущие данные:");
    System.out.println("Имя: " + author.getFirstName());
    System.out.println("Фамилия: " + author.getLastName());
    System.out.println("Дата рождения: " + (author.getBirthDate() != null ? author.getBirthDate() : "Не указана"));
    System.out.println("Биография: " + (author.getBiography() != null ? author.getBiography() : "Нет"));
    System.out.println("Введите новые данные (оставьте пустым для сохранения текущего значения)");
    
    String firstName = readString("Имя (" + author.getFirstName() + "): ");
    if (!firstName.trim().isEmpty()) {
        author.setFirstName(firstName);
    }
    
    String lastName = readString("Фамилия (" + author.getLastName() + "): ");
    if (!lastName.trim().isEmpty()) {
        author.setLastName(lastName);
    }
    
    String birthDate = readString("Дата рождения (" + (author.getBirthDate() != null ? author.getBirthDate() : "") + "): ");
    if (!birthDate.trim().isEmpty()) {
        author.setBirthDate(birthDate);
    } else {
    }
    
    String biography = readString("Биография (" + (author.getBiography() != null ? author.getBiography() : "") + "): ");
    if (!biography.trim().isEmpty()) {
        author.setBiography(biography);
    }
    
    authorDAO.updateAuthor(author);
    System.out.println("Автор обновлён!");
}

private void deleteAuthor() throws SQLException {
    System.out.println("\n УДАЛЕНИЕ АВТОРА");
    int id = readInt("Введите ID автора: ", 1, Integer.MAX_VALUE);
    Author author = authorDAO.getAuthorById(id);
    if (author == null) {
        System.out.println(" Автор не найден");
        return;
    }
    
    System.out.println("Автор: " + author.getFullName());
    if (readYesNo("Вы уверены? (y/n): ")) {
        authorDAO.deleteAuthor(id);
        System.out.println(" Автор удалён!");
    } else {
        System.out.println(" Операция отменена");
    }
}


//USERS
private void manageUsers() {
    while (true) {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║         УПРАВЛЕНИЕ ПОЛЬЗОВАТЕЛЯМИ            ║");
        System.out.println("╠══════════════════════════════════════════════╣");
        System.out.println("║  1.  Добавить пользователя                   ║");
        System.out.println("║  2.  Список всех пользователей               ║");
        System.out.println("║  3.  Найти пользователя по ID                ║");
        System.out.println("║  4.  Обновить пользователя                   ║");
        System.out.println("║  5.  Удалить пользователя                    ║");
        System.out.println("║  0.  Назад                                   ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        
        int choice = readInt("Выберите действие: ", 0, 5);
        
        try {
            switch (choice) {
                case 1 -> addUser();
                case 2 -> listAllUsers();
                case 3 -> findUserById();
                case 4 -> updateUser();
                case 5 -> deleteUser();
                case 0 -> { return; }
            }
        } catch (SQLException e) {
            System.err.println(" Ошибка БД: " + e.getMessage());
        }
    }
}

private void addUser() throws SQLException {
    System.out.println("\n ДОБАВЛЕНИЕ НОВОГО ПОЛЬЗОВАТЕЛЯ");
    User user = new User();
    
    user.setEmail(readString("Email: "));
    user.setPasswordHash(readString("Пароль (хеш): "));
    user.setFirstName(readString("Имя: "));
    user.setLastName(readString("Фамилия: "));
    user.setPhone(readString("Телефон: "));
    user.setAddress(readString("Адрес: "));
    user.setRole(readString("Роль (READER/LIBRARIAN/ADMIN): "));
    
    userDAO.addUser(user);
    System.out.println(" Пользователь добавлен! ID: " + user.getId());
}

private void listAllUsers() throws SQLException {
    System.out.println("\nСПИСОК ВСЕХ ПОЛЬЗОВАТЕЛЕЙ");
    List<User> users = userDAO.getAllUsers();
    if (users.isEmpty()) {
        System.out.println("Пользователей не найдено");
        return;
    }
    
    int idWidth = 4;
    int nameWidth = 25;
    int emailWidth = 25;
    int roleWidth = 12;
    int statusWidth = 10;
    
    for (User u : users) {
        idWidth = Math.max(idWidth, String.valueOf(u.getId()).length());
        nameWidth = Math.max(nameWidth, u.getFullName().length());
        emailWidth = Math.max(emailWidth, u.getEmail().length());
        roleWidth = Math.max(roleWidth, u.getRole().length());
        statusWidth = Math.max(statusWidth, (u.isActive() ? "Активен" : "Неактивен").length());
    }
    
    idWidth += 2;
    nameWidth += 2;
    emailWidth += 2;
    roleWidth += 2;
    statusWidth += 2;
    
    String topLine = "┌" + "─".repeat(idWidth) + "┬" + "─".repeat(nameWidth) + 
                     "┬" + "─".repeat(emailWidth) + "┬" + "─".repeat(roleWidth) + 
                     "┬" + "─".repeat(statusWidth) + "┐";
    String midLine = "├" + "─".repeat(idWidth) + "┼" + "─".repeat(nameWidth) + 
                     "┼" + "─".repeat(emailWidth) + "┼" + "─".repeat(roleWidth) + 
                     "┼" + "─".repeat(statusWidth) + "┤";
    String bottomLine = "└" + "─".repeat(idWidth) + "┴" + "─".repeat(nameWidth) + 
                        "┴" + "─".repeat(emailWidth) + "┴" + "─".repeat(roleWidth) + 
                        "┴" + "─".repeat(statusWidth) + "┘";
    

    System.out.println(topLine);
    
    System.out.printf("│ %-" + (idWidth - 1) + "s│ %-" + (nameWidth - 1) + "s│ %-" + (emailWidth - 1) + "s│ %-" + (roleWidth - 1) + "s│ %-" + (statusWidth - 1) + "s│%n",
        "ID", "Имя и фамилия", "Email", "Роль", "Статус");
    System.out.println(midLine);
    
    for (User u : users) {
        System.out.printf("│ %-" + (idWidth - 1) + "d│ %-" + (nameWidth - 1) + "s│ %-" + (emailWidth - 1) + "s│ %-" + (roleWidth - 1) + "s│ %-" + (statusWidth - 1) + "s│%n",
            u.getId(),
            u.getFullName(),
            u.getEmail(),
            u.getRole(),
            u.isActive() ? "Активен" : "Неактивен");
    }
    
    System.out.println(bottomLine);
}

private void findUserById() throws SQLException {
    System.out.println("\nПОИСК ПОЛЬЗОВАТЕЛЯ ПО ID");
    int id = readInt("Введите ID пользователя: ", 1, Integer.MAX_VALUE);
    User user = userDAO.getUserById(id);
    if (user == null) {
        System.out.println("Пользователь с ID " + id + " не найден");
        return;
    }
 
    List<Book> activeBooks = loanDAO.getActiveBooksByUserId(id);
    String booksStr;
    if (activeBooks.isEmpty()) {
        booksStr = "Нет активных выдач";
    } else {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < activeBooks.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(activeBooks.get(i).getTitle())
              .append(" (ID: ").append(activeBooks.get(i).getId()).append(")");
        }
        booksStr = sb.toString();
    }
    
    String idStr = "ID:            " + user.getId();
    String firstNameStr = "Имя:           " + user.getFirstName();
    String lastNameStr = "Фамилия:       " + user.getLastName();
    String emailStr = "Email:         " + user.getEmail();
    String phoneStr = "Телефон:       " + (user.getPhone() != null ? user.getPhone() : "Не указан");
    String addressStr = "Адрес:         " + (user.getAddress() != null ? user.getAddress() : "Не указан");
    String roleStr = "Роль:          " + user.getRole();
    String activeStr = "Активен:       " + (user.isActive() ? "Да" : "Нет");
    String booksStrFull = "Книги на руках: " + booksStr;  
    
    int maxLen = Math.max(
        Math.max(idStr.length(), firstNameStr.length()),
        Math.max(lastNameStr.length(), emailStr.length())
    );
    maxLen = Math.max(maxLen, phoneStr.length());
    maxLen = Math.max(maxLen, addressStr.length());
    maxLen = Math.max(maxLen, roleStr.length());
    maxLen = Math.max(maxLen, activeStr.length());
    maxLen = Math.max(maxLen, booksStrFull.length()); 
    int width = maxLen + 2;
    
    String topLine = "┌" + "─".repeat(width) + "┐";
    String midLine = "├" + "─".repeat(width) + "┤";
    String bottomLine = "└" + "─".repeat(width) + "┘";
    
    System.out.println(topLine);
    System.out.println("│" + ConsoleUtils.centerString("ИНФОРМАЦИЯ О ПОЛЬЗОВАТЕЛЕ", width) + "│");
    System.out.println(midLine);
    System.out.println("│ " + ConsoleUtils.padRight(idStr, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(firstNameStr, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(lastNameStr, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(emailStr, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(phoneStr, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(addressStr, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(roleStr, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(activeStr, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(booksStrFull, width - 1) + "│");  
    System.out.println(bottomLine);
}

private void updateUser() throws SQLException {
    System.out.println("\n ОБНОВЛЕНИЕ ПОЛЬЗОВАТЕЛЯ");
    int id = readInt("Введите ID пользователя: ", 1, Integer.MAX_VALUE);
    User user = userDAO.getUserById(id);
    if (user == null) {
        System.out.println(" Пользователь не найден");
        return;
    }
    
    System.out.println("Текущие данные:");
    System.out.println("Имя: " + user.getFirstName());
    System.out.println("Фамилия: " + user.getLastName());
    System.out.println("Email: " + user.getEmail());
    System.out.println("Роль: " + user.getRole());
    System.out.println(" Введите новые данные (оставьте пустым для сохранения текущего значения)");
    
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
    System.out.println(" Пользователь обновлён!");
}

private void deleteUser() throws SQLException {
    System.out.println("\n УДАЛЕНИЕ ПОЛЬЗОВАТЕЛЯ");
    int id = readInt("Введите ID пользователя: ", 1, Integer.MAX_VALUE);
    User user = userDAO.getUserById(id);
    if (user == null) {
        System.out.println(" Пользователь не найден");
        return;
    }
    
    System.out.println("Пользователь: " + user.getFullName() + " (" + user.getEmail() + ")");
    if (readYesNo("Вы уверены? (y/n): ")) {
        userDAO.deleteUser(id);
        System.out.println(" Пользователь удалён!");
    } else {
        System.out.println(" Операция отменена");
    }
}


//LOANS
private void manageLoans() {
    while (true) {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║           УПРАВЛЕНИЕ ВЫДАЧАМИ                ║");
        System.out.println("╠══════════════════════════════════════════════╣");
        System.out.println("║  1.  Добавить выдачу                         ║");
        System.out.println("║  2.  Список всех выдач                       ║");
        System.out.println("║  3.  Активные выдачи                         ║");
        System.out.println("║  4.  Вернуть книгу                           ║");
        System.out.println("║  5.  Удалить выдачу                          ║");
        System.out.println("║  0.  Назад                                   ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        
        int choice = readInt("Выберите действие: ", 0, 5);
        
        try {
            switch (choice) {
                case 1 -> addLoan();
                case 2 -> listAllLoans();
                case 3 -> listActiveLoans();
                case 4 -> returnBook();
                case 5 -> deleteLoan();
                case 0 -> { return; }
            }
        } catch (SQLException e) {
            System.err.println(" Ошибка БД: " + e.getMessage());
        }
    }
}

private void addLoan() throws SQLException {
    System.out.println("\nНОВАЯ ВЫДАЧА КНИГИ");
    int bookId = readInt("ID книги: ", 1, Integer.MAX_VALUE);
    
    // Проверяем, существует ли книга
    Book book = bookDAO.getBookById(bookId);
    if (book == null) {
        System.out.println("Книга с ID " + bookId + " не найдена");
        return;
    }
    
    // Проверяем доступные копии
    int available = bookDAO.getAvailableCopies(bookId);
    if (available <= 0) {
        System.out.println("Нет доступных экземпляров книги \"" + book.getTitle() + "\"");
        System.out.println("Всего экземпляров: " + book.getTotalCopies() + ", доступно: 0");
        return;
    }
    
    // Проверяем, существует ли пользователь
    int userId = readInt("ID пользователя: ", 1, Integer.MAX_VALUE);
    User user = userDAO.getUserById(userId);
    if (user == null) {
        System.out.println("Пользователь с ID " + userId + " не найден");
        return;
    }
    
    // Проверяем, есть ли у пользователя уже активная выдача этой книги
    List<Loan> userLoans = loanDAO.getActiveLoansByUserAndBook(userId, bookId);
    if (!userLoans.isEmpty()) {
        System.out.println("Пользователь уже взял эту книгу и ещё не вернул");
        return;
    }
    
    Loan loan = new Loan();
    loan.setBookId(bookId);
    loan.setUserId(userId);
    
    java.sql.Date loanDate = new java.sql.Date(System.currentTimeMillis());
    java.sql.Date dueDate = new java.sql.Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
    
    loan.setLoanDate(loanDate.toString());
    loan.setDueDate(dueDate.toString());
    loan.setStatus("ACTIVE");
    
    loanDAO.addLoan(loan);
    System.out.println("Книга выдана! ID выдачи: " + loan.getId());
    System.out.println("Дата возврата: " + loan.getDueDate());
    System.out.println("Осталось доступных экземпляров: " + (available - 1));
}

private void listAllLoans() throws SQLException {
    System.out.println("\n СПИСОК ВСЕХ ВЫДАЧ");
    List<Loan> loans = loanDAO.getAllLoans();
    if (loans.isEmpty()) {
        System.out.println(" Выдач не найдено");
        return;
    }
    System.out.println("┌────┬──────────┬────────────┬────────────┬────────────┬────────────┬──────────────┐");
    System.out.println("│ ID │ Книга    │ Читатель   │ Дата выдачи│ Срок       │ Статус     │ Штраф        │");
    System.out.println("├────┼──────────┼────────────┼────────────┼────────────┼────────────┼──────────────┤");
    for (Loan l : loans) {
        System.out.printf("│ %-2d │ %-8d │ %-10d │ %-10s │ %-10s │ %-10s │ %-12.2f │%n", 
            l.getId(), 
            l.getBookId(),
            l.getUserId(),
            l.getLoanDate(),
            l.getDueDate(),
            l.getStatus(),
            l.getFineAmount());
    }
    System.out.println("└────┴──────────┴────────────┴────────────┴────────────┴────────────┴──────────────┘");
}

private void listActiveLoans() throws SQLException {
    System.out.println("\n АКТИВНЫЕ ВЫДАЧИ");
    List<Loan> loans = loanDAO.getActiveLoans();
    if (loans.isEmpty()) {
        System.out.println(" Нет активных выдач");
        return;
    }
    System.out.println("┌────┬──────────┬────────────┬────────────┬────────────┬──────────────┐");
    System.out.println("│ ID │ Книга    │ Читатель   │ Дата выдачи│ Срок       │ Штраф        │");
    System.out.println("├────┼──────────┼────────────┼────────────┼────────────┼──────────────┤");
    for (Loan l : loans) {
        System.out.printf("│ %-2d │ %-8d │ %-10d │ %-10s │ %-10s │ %-12.2f │%n", 
            l.getId(), 
            l.getBookId(),
            l.getUserId(),
            l.getLoanDate(),
            l.getDueDate(),
            l.getFineAmount());
    }
    System.out.println("└────┴──────────┴────────────┴────────────┴────────────┴──────────────┘");
}

private void returnBook() throws SQLException {
    System.out.println("\nВОЗВРАТ КНИГИ");
    int loanId = readInt("Введите ID выдачи: ", 1, Integer.MAX_VALUE);
    
    Loan loan = loanDAO.getLoanById(loanId);
    if (loan == null) {
        System.out.println("Выдача с ID " + loanId + " не найдена");
        return;
    }
    
    if (!loan.getStatus().equals("ACTIVE") && !loan.getStatus().equals("OVERDUE")) {
        System.out.println("Эта выдача уже завершена. Статус: " + loan.getStatus());
        return;
    }
    
    System.out.println("\nИнформация о выдаче:");
    System.out.println("  Книга ID: " + loan.getBookId());
    System.out.println("  Пользователь ID: " + loan.getUserId());
    System.out.println("  Дата выдачи: " + loan.getLoanDate());
    System.out.println("  Срок возврата: " + loan.getDueDate());
    System.out.println("  Статус: " + loan.getStatus());
    
    java.time.LocalDate today = java.time.LocalDate.now();
    java.time.LocalDate dueDate = java.time.LocalDate.parse(loan.getDueDate());
    
    double fine = 0;
    if (dueDate.isBefore(today)) {
        long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(dueDate, today);
        double autoFine = daysOverdue * 0.50;
        
        System.out.println("\nКнига просрочена на " + daysOverdue + " дней");
        System.out.println("  Автоматический штраф: " + autoFine + " руб. (0.50 руб/день)");
        System.out.println("  Введите 0 для автоматического расчёта");
        
        double userFine = readDouble("  Введите сумму штрафа: ");
        if (userFine == 0) {
            fine = autoFine;
            System.out.println("  Использован автоматический расчёт: " + fine + " руб.");
        } else if (userFine > 0) {
            fine = userFine;
        } else {
            System.out.println("  Штраф не может быть отрицательным. Использован авторасчёт.");
            fine = autoFine;
        }
    } else {
        System.out.println("\nКнига возвращена вовремя, штраф не назначается");
        fine = 0;
    }
    
    loanDAO.returnBook(loanId, fine);
    
  
    int available = bookDAO.getAvailableCopies(loan.getBookId());
    System.out.println("\nКнига возвращена! Штраф: " + fine + " руб.");
    System.out.println("Доступно экземпляров: " + available);
}

private void deleteLoan() throws SQLException {
    System.out.println("\n УДАЛЕНИЕ ВЫДАЧИ");
    int id = readInt("Введите ID выдачи: ", 1, Integer.MAX_VALUE);
    if (readYesNo("Вы уверены? (y/n): ")) {
        loanDAO.deleteLoan(id);
        System.out.println(" Выдача удалена!");
    } else {
        System.out.println(" Операция отменена");
    }
}


    private void searchBooks() {
        System.out.println("\n ПОИСК КНИГ");
        String title = readString("Название (или часть): ");
        String author = readString("Автор (или часть): ");
        String genre = readString("Жанр: ");
        
        try {
            int total = bookDAO.countBooks(title, author, genre);
            if (total == 0) {
                System.out.println("Книг не найдено");
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
                    String input = scanner.nextLine();
                    if (input.equalsIgnoreCase("q")) break;
                    page++;
                } else {
                    break;
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка поиска: " + e.getMessage());
        }
    }

    private void printBookList(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println(" Книг не найдено");
            return;
        }
        System.out.println("┌────┬────────────────────────────────────────────┬──────────┬─────────────┐");
        System.out.println("│ ID │ Название                                   │ Год      │ Доступно    │");
        System.out.println("├────┼────────────────────────────────────────────┼──────────┼─────────────┤");
        for (Book b : books) {
            int available;
            try {
                available = bookDAO.getAvailableCopies(b.getId());
            } catch (SQLException e) {
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

    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println(" Введите число от " + min + " до " + max);
            } catch (NumberFormatException e) {
                System.out.println(" Введите корректное число");
            }
        }
    }

    private boolean readYesNo(String prompt) {
        while (true) {
            String input = readString(prompt);
            if (input.equalsIgnoreCase("y") || input.equalsIgnoreCase("yes")) return true;
            if (input.equalsIgnoreCase("n") || input.equalsIgnoreCase("no")) return false;
            System.out.println(" Введите y (да) или n (нет)");
        }
    }

    private double readDouble(String prompt) {
    while (true) {
        try {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) return 0;
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println("Введите корректное число");
        }
    }
}


//УПРАВЛЕНИЕ ИЗДАТЕЛЯМИ
private void managePublishers() {
    while (true) {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║           УПРАВЛЕНИЕ ИЗДАТЕЛЯМИ              ║");
        System.out.println("╠══════════════════════════════════════════════╣");
        System.out.println("║  1.  Добавить издателя                       ║");
        System.out.println("║  2.  Список всех издателей                   ║");
        System.out.println("║  3.  Найти издателя по ID                    ║");
        System.out.println("║  4.  Обновить издателя                       ║");
        System.out.println("║  5.  Удалить издателя                        ║");
        System.out.println("║  0.  Назад                                   ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        
        int choice = readInt("Выберите действие: ", 0, 5);
        
        try {
            switch (choice) {
                case 1 -> addPublisher();
                case 2 -> listAllPublishers();
                case 3 -> findPublisherById();
                case 4 -> updatePublisher();
                case 5 -> deletePublisher();
                case 0 -> { return; }
            }
        } catch (SQLException e) {
            System.err.println(" Ошибка БД: " + e.getMessage());
        }
    }
}

private void addPublisher() throws SQLException {
    System.out.println("\n ДОБАВЛЕНИЕ НОВОГО ИЗДАТЕЛЯ");
    Publisher publisher = new Publisher();
    
    publisher.setName(readString("Название издательства: "));
    publisher.setAddress(readString("Адрес: "));
    publisher.setPhone(readString("Телефон: "));
    publisher.setEmail(readString("Email: "));
    publisher.setWebsite(readString("Сайт: "));
    
    publisherDAO.addPublisher(publisher);
    System.out.println("Издатель добавлен! ID: " + publisher.getId());
}

private void listAllPublishers() throws SQLException {
    System.out.println("\nСПИСОК ВСЕХ ИЗДАТЕЛЕЙ");
    List<Publisher> publishers = publisherDAO.getAllPublishers();
    if (publishers.isEmpty()) {
        System.out.println("Издателей не найдено");
        return;
    }
    
    int idWidth = 4;
    int nameWidth = 30;
    int emailWidth = 25;
    int phoneWidth = 20;
    
    for (Publisher p : publishers) {
        idWidth = Math.max(idWidth, String.valueOf(p.getId()).length());
        nameWidth = Math.max(nameWidth, p.getName().length());
        emailWidth = Math.max(emailWidth, p.getEmail() != null ? p.getEmail().length() : 4);
        phoneWidth = Math.max(phoneWidth, p.getPhone() != null ? p.getPhone().length() : 4);
    }
    
    idWidth += 2;
    nameWidth += 2;
    emailWidth += 2;
    phoneWidth += 2;
    
    String topLine = "┌" + "─".repeat(idWidth) + "┬" + "─".repeat(nameWidth) + 
                     "┬" + "─".repeat(emailWidth) + "┬" + "─".repeat(phoneWidth) + "┐";
    String midLine = "├" + "─".repeat(idWidth) + "┼" + "─".repeat(nameWidth) + 
                     "┼" + "─".repeat(emailWidth) + "┼" + "─".repeat(phoneWidth) + "┤";
    String bottomLine = "└" + "─".repeat(idWidth) + "┴" + "─".repeat(nameWidth) + 
                        "┴" + "─".repeat(emailWidth) + "┴" + "─".repeat(phoneWidth) + "┘";
    

    System.out.println(topLine);
    
    System.out.printf("│ %-" + (idWidth - 1) + "s│ %-" + (nameWidth - 1) + "s│ %-" + (emailWidth - 1) + "s│ %-" + (phoneWidth - 1) + "s│%n",
        "ID", "Название", "Email", "Телефон");
    System.out.println(midLine);
    
    for (Publisher p : publishers) {
        System.out.printf("│ %-" + (idWidth - 1) + "d│ %-" + (nameWidth - 1) + "s│ %-" + (emailWidth - 1) + "s│ %-" + (phoneWidth - 1) + "s│%n",
            p.getId(),
            p.getName(),
            p.getEmail() != null ? p.getEmail() : "",
            p.getPhone() != null ? p.getPhone() : "");
    }
    
    System.out.println(bottomLine);
}

private void findPublisherById() throws SQLException {
    System.out.println("\nПОИСК ИЗДАТЕЛЯ ПО ID");
    int id = readInt("Введите ID издателя: ", 1, Integer.MAX_VALUE);
    Publisher publisher = publisherDAO.getPublisherById(id);
    if (publisher == null) {
        System.out.println("Издатель с ID " + id + " не найден");
        return;
    }
    
    String idStr = "ID:          " + publisher.getId();
    String nameStr = "Название:    " + publisher.getName();
    String addressStr = "Адрес:       " + (publisher.getAddress() != null ? publisher.getAddress() : "Не указан");
    String phoneStr = "Телефон:     " + (publisher.getPhone() != null ? publisher.getPhone() : "Не указан");
    String emailStr = "Email:       " + (publisher.getEmail() != null ? publisher.getEmail() : "Не указан");
    String websiteStr = "Сайт:        " + (publisher.getWebsite() != null ? publisher.getWebsite() : "Не указан");
    
    int maxLen = Math.max(
        Math.max(idStr.length(), nameStr.length()),
        Math.max(addressStr.length(), phoneStr.length())
    );
    maxLen = Math.max(maxLen, emailStr.length());
    maxLen = Math.max(maxLen, websiteStr.length());
    
    int width = maxLen + 2;
    
    String topLine = "┌" + "─".repeat(width) + "┐";
    String midLine = "├" + "─".repeat(width) + "┤";
    String bottomLine = "└" + "─".repeat(width) + "┘";
    
    System.out.println(topLine);
    System.out.println("│" + ConsoleUtils.centerString("ИНФОРМАЦИЯ ОБ ИЗДАТЕЛЕ", width) + "│");
    System.out.println(midLine);
    System.out.println("│ " + ConsoleUtils.padRight(idStr, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(nameStr, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(addressStr, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(phoneStr, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(emailStr, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(websiteStr, width - 1) + "│");
    System.out.println(bottomLine);
}

private void updatePublisher() throws SQLException {
    System.out.println("\nОБНОВЛЕНИЕ ИЗДАТЕЛЯ");
    int id = readInt("Введите ID издателя: ", 1, Integer.MAX_VALUE);
    Publisher publisher = publisherDAO.getPublisherById(id);
    if (publisher == null) {
        System.out.println("Издатель не найден");
        return;
    }
    
    String idStr = "ID:          " + publisher.getId();
    String nameStr = "Название:    " + publisher.getName();
    String addressStr = "Адрес:       " + (publisher.getAddress() != null ? publisher.getAddress() : "Не указан");
    String phoneStr = "Телефон:     " + (publisher.getPhone() != null ? publisher.getPhone() : "Не указан");
    String emailStr = "Email:       " + (publisher.getEmail() != null ? publisher.getEmail() : "Не указан");
    String websiteStr = "Сайт:        " + (publisher.getWebsite() != null ? publisher.getWebsite() : "Не указан");
    
    int maxLen = Math.max(
        Math.max(idStr.length(), nameStr.length()),
        Math.max(addressStr.length(), phoneStr.length())
    );
    maxLen = Math.max(maxLen, emailStr.length());
    maxLen = Math.max(maxLen, websiteStr.length());
    
    int minWidth = 30;
    int width = Math.max(maxLen + 2, minWidth);
    
    String topLine = "┌" + "─".repeat(width) + "┐";
    String midLine = "├" + "─".repeat(width) + "┤";
    String bottomLine = "└" + "─".repeat(width) + "┘";
    
    System.out.println("Текущие данные:");
    System.out.println(topLine);
    System.out.println("│" + ConsoleUtils.centerString("ИНФОРМАЦИЯ ОБ ИЗДАТЕЛЕ", width) + "│");
    System.out.println(midLine);
    System.out.println("│ " + ConsoleUtils.padRight(idStr, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(nameStr, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(addressStr, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(phoneStr, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(emailStr, width - 1) + "│");
    System.out.println("│ " + ConsoleUtils.padRight(websiteStr, width - 1) + "│");
    System.out.println(bottomLine);
    
    System.out.println("\nВведите новые данные (оставьте пустым для сохранения текущего значения)");
    
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
    System.out.println("Издатель обновлён!");
}

private void deletePublisher() throws SQLException {
    System.out.println("\n УДАЛЕНИЕ ИЗДАТЕЛЯ");
    int id = readInt("Введите ID издателя: ", 1, Integer.MAX_VALUE);
    Publisher publisher = publisherDAO.getPublisherById(id);
    if (publisher == null) {
        System.out.println("Издатель не найден");
        return;
    }
    
    System.out.println("Издатель: " + publisher.getName());
    if (readYesNo("Вы уверены? (y/n): ")) {
        try {
            publisherDAO.deletePublisher(id);
            System.out.println("Издатель удалён!");
        } catch (SQLException e) {
            if (e.getMessage().contains("foreign key")) {
                System.out.println("Невозможно удалить издателя: есть книги, привязанные к нему");
            } else {
                throw e;
            }
        }
    } else {
        System.out.println("Операция отменена");
    }
}

}