package com.library.menu;

import com.library.DatabaseConnection;
import com.library.dao.*;
import com.library.models.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainMenu {
    private static final Logger LOGGER = Logger.getLogger(MainMenu.class.getName());
    private static final String HEADER_TOP = "╔══════════════════════════════════════════════╗";
    private static final String HEADER_MID = "╠══════════════════════════════════════════════╣";
    private static final String HEADER_BOTTOM = "╚══════════════════════════════════════════════╝";
    private static final String MENU_BACK = "║  0.  Назад                                   ║";
    private static final String PROMPT_ACTION = "Выберите действие: ";
    private static final String ERROR_DB = "Ошибка БД: ";
    private static final String LABEL_ID = " ID: ";
    private static final String NOT_FOUND = " не найден";
    private static final String LABEL_TOTAL_COPIES = "Всего экземпляров: ";
    private static final String LABEL_GENRE_ID = " Жанр ID ";
    private static final String LABEL_AUTHOR_ID = " Автор ID ";
    private static final String ADDED = " добавлен";
    private static final String REMOVED = " удален";

    private final Scanner scanner = new Scanner(System.in);
    private final BookDAO bookDAO = new BookDAO();
    private final AuthorDAO authorDAO = new AuthorDAO();
    private final UserDAO userDAO = new UserDAO();
    private final LoanDAO loanDAO = new LoanDAO();
    private final PublisherDAO publisherDAO = new PublisherDAO();
    private final GenreDAO genreDAO = new GenreDAO();

    public void start() {
        DatabaseConnection.testConnection();
        LOGGER.info("");

        while (true) {
            printMainMenu();
            int choice = readInt(PROMPT_ACTION, 0, 6);

            switch (choice) {
                case 1 -> manageBooks();
                case 2 -> manageAuthors();
                case 3 -> manageUsers();
                case 4 -> manageLoans();
                case 5 -> managePublishers();
                case 6 -> searchBooks();
                case 0 -> {
                    LOGGER.info("\nДо свидания!");
                    return;
                }
                default -> LOGGER.warning("Неверный выбор");
            }
        }
    }

    private void printMainMenu() {
        LOGGER.info("\n" + HEADER_TOP);
        LOGGER.info("║               ГЛАВНОЕ МЕНЮ                   ║");
        LOGGER.info(HEADER_MID);
        LOGGER.info("║  1.  Управление книгами                      ║");
        LOGGER.info("║  2.  Управление авторами                     ║");
        LOGGER.info("║  3.  Управление пользователями               ║");
        LOGGER.info("║  4.  Управление выдачами                     ║");
        LOGGER.info("║  5.  Управление издателями                   ║");
        LOGGER.info("║  6.  Поиск книг                              ║");
        LOGGER.info("║  0.  Выход                                   ║");
        LOGGER.info(HEADER_BOTTOM);
    }

    private void manageBooks() {
        while (true) {
            LOGGER.info("\n" + HEADER_TOP);
            LOGGER.info("║            УПРАВЛЕНИЕ КНИГАМИ                ║");
            LOGGER.info(HEADER_MID);
            LOGGER.info("║  1.  Добавить книгу                          ║");
            LOGGER.info("║  2.  Список всех книг                        ║");
            LOGGER.info("║  3.  Найти книгу по ID                       ║");
            LOGGER.info("║  4.  Обновить книгу                          ║");
            LOGGER.info("║  5.  Удалить книгу                           ║");
            LOGGER.info(MENU_BACK);
            LOGGER.info(HEADER_BOTTOM);

            int choice = readInt(PROMPT_ACTION, 0, 5);
            try {
                switch (choice) {
                    case 1 -> addBook();
                    case 2 -> listAllBooks();
                    case 3 -> findBookById();
                    case 4 -> updateBook();
                    case 5 -> deleteBook();
                    case 0 -> { return; }
                    default -> LOGGER.warning("Неверный выбор");
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, ERROR_DB + e.getMessage(), e);
            }
        }
    }

    private void addBook() throws SQLException {
        LOGGER.info("\nДОБАВЛЕНИЕ НОВОЙ КНИГИ");

        LOGGER.info("Список доступных издателей:");
        List<Publisher> publishers = publisherDAO.getAllPublishers();
        if (publishers.isEmpty()) {
            LOGGER.info("В базе нет издателей!");
            LOGGER.info("Сначала добавьте издателя через меню 'Управление издателями'");
            return;
        }
        for (Publisher p : publishers) {
            LOGGER.info("  ID: " + p.getId() + " | " + p.getName());
        }
        LOGGER.info("");

        LOGGER.info("Список доступных жанров:");
        List<Genre> genres = genreDAO.getAllGenres();
        if (genres.isEmpty()) {
            LOGGER.info("В базе нет жанров!");
            LOGGER.info("Сначала добавьте жанры через SQL");
            return;
        }
        for (Genre g : genres) {
            LOGGER.info("  ID: " + g.getId() + " | " + g.getName());
        }
        LOGGER.info("");

        LOGGER.info("Список доступных авторов:");
        List<Author> authors = authorDAO.getAllAuthors();
        if (authors.isEmpty()) {
            LOGGER.info("В базе нет авторов!");
            LOGGER.info("Сначала добавьте авторов через меню 'Управление авторами'");
            return;
        }
        for (Author a : authors) {
            LOGGER.info("  ID: " + a.getId() + " | " + a.getFullName());
        }
        LOGGER.info("");

        Book book = new Book();
        book.setTitle(readString("Название: "));
        book.setIsbn(readString("ISBN: "));
        book.setPublicationYear(readInt("Год публикации: ", 1, 9999));

        int publisherId = readInt("ID издательства: ", 1, Integer.MAX_VALUE);
        Publisher publisher = publisherDAO.getPublisherById(publisherId);
        if (publisher == null) {
            LOGGER.info("Издатель с ID " + publisherId + NOT_FOUND);
            return;
        }
        book.setPublisherId(publisherId);

        book.setTotalCopies(readInt("Всего экземпляров: ", 0, Integer.MAX_VALUE));
        book.setPageCount(readInt("Количество страниц: ", 0, Integer.MAX_VALUE));
        book.setDescription(readString("Описание: "));

        bookDAO.addBook(book);
        LOGGER.info("Книга добавлена! ID: " + book.getId());

        LOGGER.info("\nДобавление жанров к книге");
        String genreIdsInput = readString("Введите ID жанров через запятую (например: 1,2,3): ");
        if (!genreIdsInput.trim().isEmpty()) {
            for (String idStr : genreIdsInput.split(",")) {
                try {
                    int genreId = Integer.parseInt(idStr.trim());
                    bookDAO.addBookGenre(book.getId(), genreId);
                    LOGGER.info(LABEL_GENRE_ID + genreId + ADDED);
                } catch (NumberFormatException e) {
                    LOGGER.info("  ID '" + idStr + "' не является числом");
                }
            }
        }

        LOGGER.info("\nДобавление авторов к книге");
        String authorIdsInput = readString("Введите ID авторов через запятую (например: 1,2,3): ");
        if (!authorIdsInput.trim().isEmpty()) {
            int order = 1;
            for (String idStr : authorIdsInput.split(",")) {
                try {
                    int authorId = Integer.parseInt(idStr.trim());
                    bookDAO.addBookAuthor(book.getId(), authorId, order);
                    LOGGER.info(LABEL_AUTHOR_ID + authorId + ADDED + " (порядок: " + order + ")");
                    order++;
                } catch (NumberFormatException e) {
                    LOGGER.info("  ID '" + idStr + "' не является числом");
                }
            }
        }
        LOGGER.info("Книга добавлена!");
    }

    private void listAllBooks() throws SQLException {
        LOGGER.info("\nСПИСОК ВСЕХ КНИГ");
        List<Book> books = bookDAO.getAllBooks();
        printBookList(books);
    }

    private void findBookById() throws SQLException {
        LOGGER.info("\nПОИСК КНИГИ ПО ID");
        int id = readInt("Введите ID книги: ", 1, Integer.MAX_VALUE);
        Book book = bookDAO.getBookById(id);
        if (book == null) {
            LOGGER.info("Книга с ID " + id + NOT_FOUND);
            return;
        }

        Publisher publisher = publisherDAO.getPublisherById(book.getPublisherId());
        String publisherInfo = (publisher != null) ? publisher.getName() + " (ID: " + publisher.getId() + ")" : "Не указан";

        List<Author> authors = bookDAO.getAuthorsByBookId(id);
        String authorsStr = formatAuthors(authors);

        List<Genre> genres = bookDAO.getGenresByBookId(id);
        String genresStr = formatGenres(genres);

        int available = bookDAO.getAvailableCopies(id);

        String idStr = "ID книги:          " + book.getId();
        String titleStr = "Название:          " + book.getTitle();
        String isbnStr = "ISBN:              " + book.getIsbn();
        String yearStr = "Год публикации:    " + book.getPublicationYear();
        String publisherStr = "Издательство:      " + publisherInfo;
        String authorsStrFull = "Авторы:            " + authorsStr;
        String genresStrFull = "Жанры:             " + genresStr;
        String totalStr = LABEL_TOTAL_COPIES + book.getTotalCopies();
        String availableStr = "Доступно:          " + available;
        String pagesStr = "Страниц:           " + book.getPageCount();
        String descStr = "Описание:          " + (book.getDescription() != null ? book.getDescription() : "Нет описания");

        printTable("ИНФОРМАЦИЯ О КНИГЕ",
            idStr, titleStr, isbnStr, yearStr,
            publisherStr, authorsStrFull, genresStrFull,
            totalStr, availableStr, pagesStr, descStr);
    }

    private void updateBook() throws SQLException {
        LOGGER.info("\nОБНОВЛЕНИЕ КНИГИ");
        int id = readInt("Введите ID книги: ", 1, Integer.MAX_VALUE);
        Book book = bookDAO.getBookById(id);
        if (book == null) {
            LOGGER.info("Книга не найдена");
            return;
        }

        LOGGER.info("Текущие данные:");
        LOGGER.info(book.toString());

        List<Author> currentAuthors = bookDAO.getAuthorsByBookId(id);
        LOGGER.info("\nТекущие авторы:");
        if (currentAuthors.isEmpty()) {
            LOGGER.info("  (нет)");
        } else {
            for (Author a : currentAuthors) {
                LOGGER.info(LABEL_ID + a.getId() + " | " + a.getFullName());
            }
        }

        List<Genre> currentGenres = bookDAO.getGenresByBookId(id);
        LOGGER.info("\nТекущие жанры:");
        if (currentGenres.isEmpty()) {
            LOGGER.info("  (нет)");
        } else {
            for (Genre g : currentGenres) {
                LOGGER.info(LABEL_ID + g.getId() + " | " + g.getName());
            }
        }

        LOGGER.info("\nВведите новые данные (оставьте пустым для сохранения текущего значения)");

        String title = readString("Название (" + book.getTitle() + "): ");
        if (!title.trim().isEmpty()) book.setTitle(title);

        String isbn = readString("ISBN (" + book.getIsbn() + "): ");
        if (!isbn.trim().isEmpty()) book.setIsbn(isbn);

        String year = readString("Год публикации (" + book.getPublicationYear() + "): ");
        if (!year.trim().isEmpty()) book.setPublicationYear(Integer.parseInt(year));

        String copies = readString(LABEL_TOTAL_COPIES + "(" + book.getTotalCopies() + "): ");
        if (!copies.trim().isEmpty()) book.setTotalCopies(Integer.parseInt(copies));

        String pages = readString("Количество страниц (" + book.getPageCount() + "): ");
        if (!pages.trim().isEmpty()) book.setPageCount(Integer.parseInt(pages));

        String description = readString("Описание (" + (book.getDescription() != null ? book.getDescription() : "") + "): ");
        if (!description.trim().isEmpty()) book.setDescription(description);

        bookDAO.updateBook(book);
        LOGGER.info("Основные данные книги обновлены!");

        updateGenres(id, currentGenres);
        updateAuthors(id, currentAuthors);

        LOGGER.info("\nКнига полностью обновлена!");
    }

    private void updateGenres(int bookId, List<Genre> currentGenres) throws SQLException {
        LOGGER.info("\n--- УПРАВЛЕНИЕ ЖАНРАМИ ---");
        LOGGER.info("1. Добавить жанры");
        LOGGER.info("2. Удалить жанры");
        LOGGER.info("3. Заменить все жанры");
        LOGGER.info("0. Пропустить");
        int choice = readInt(PROMPT_ACTION, 0, 3);

        List<Genre> allGenres = genreDAO.getAllGenres();
        LOGGER.info("Доступные жанры:");
        for (Genre g : allGenres) {
            LOGGER.info(LABEL_ID + g.getId() + " | " + g.getName());
        }

        switch (choice) {
            case 1 -> {
                String addGenres = readString("Введите ID жанров для добавления через запятую: ");
                if (!addGenres.trim().isEmpty()) {
                    for (String idStr : addGenres.split(",")) {
                        try {
                            int genreId = Integer.parseInt(idStr.trim());
                            if (currentGenres.stream().noneMatch(g -> g.getId() == genreId)) {
                                bookDAO.addBookGenre(bookId, genreId);
                                LOGGER.info(LABEL_GENRE_ID + genreId + ADDED);
                            } else {
                                LOGGER.info(LABEL_GENRE_ID + genreId + " уже есть");
                            }
                        } catch (NumberFormatException e) {
                            LOGGER.info("  ID '" + idStr + "' не является числом");
                        }
                    }
                    LOGGER.info("Жанры добавлены!");
                }
            }
            case 2 -> {
                if (currentGenres.isEmpty()) {
                    LOGGER.info("У книги нет жанров для удаления");
                } else {
                    String removeGenres = readString("Введите ID жанров для удаления через запятую: ");
                    if (!removeGenres.trim().isEmpty()) {
                        for (String idStr : removeGenres.split(",")) {
                            try {
                                int genreId = Integer.parseInt(idStr.trim());
                                bookDAO.deleteBookGenre(bookId, genreId);
                                LOGGER.info(LABEL_GENRE_ID + genreId + REMOVED);
                            } catch (NumberFormatException e) {
                                LOGGER.info("  ID '" + idStr + "' не является числом");
                            }
                        }
                    }
                }
            }
            case 3 -> {
                String newGenres = readString("Введите новые ID жанров через запятую (или Enter для очистки): ");
                bookDAO.deleteBookGenres(bookId);
                if (!newGenres.trim().isEmpty()) {
                    for (String idStr : newGenres.split(",")) {
                        try {
                            int genreId = Integer.parseInt(idStr.trim());
                            bookDAO.addBookGenre(bookId, genreId);
                            LOGGER.info(LABEL_GENRE_ID + genreId + ADDED);
                        } catch (NumberFormatException e) {
                            LOGGER.info("  ID '" + idStr + "' не является числом");
                        }
                    }
                }
                LOGGER.info("Жанры обновлены!");
            }
            default -> LOGGER.info("Жанры не изменены");
        }
    }

    private void updateAuthors(int bookId, List<Author> currentAuthors) throws SQLException {
        LOGGER.info("\n--- УПРАВЛЕНИЕ АВТОРАМИ ---");
        LOGGER.info("1. Добавить авторов");
        LOGGER.info("2. Удалить авторов");
        LOGGER.info("3. Заменить всех авторов");
        LOGGER.info("0. Пропустить");
        int choice = readInt(PROMPT_ACTION, 0, 3);

        List<Author> allAuthors = authorDAO.getAllAuthors();
        LOGGER.info("Доступные авторы:");
        for (Author a : allAuthors) {
            LOGGER.info(LABEL_ID + a.getId() + " | " + a.getFullName());
        }

        switch (choice) {
            case 1 -> {
                String addAuthors = readString("Введите ID авторов для добавления через запятую: ");
                if (!addAuthors.trim().isEmpty()) {
                    int maxOrder = currentAuthors.size();
                    for (String idStr : addAuthors.split(",")) {
                        try {
                            int authorId = Integer.parseInt(idStr.trim());
                            if (currentAuthors.stream().noneMatch(a -> a.getId() == authorId)) {
                                maxOrder++;
                                bookDAO.addBookAuthor(bookId, authorId, maxOrder);
                                LOGGER.info(LABEL_AUTHOR_ID + authorId + ADDED + " (порядок: " + maxOrder + ")");
                            } else {
                                LOGGER.info(LABEL_AUTHOR_ID + authorId + " уже есть");
                            }
                        } catch (NumberFormatException e) {
                            LOGGER.info("  ID '" + idStr + "' не является числом");
                        }
                    }
                    LOGGER.info("Авторы добавлены!");
                }
            }
            case 2 -> {
                if (currentAuthors.isEmpty()) {
                    LOGGER.info("У книги нет авторов для удаления");
                } else {
                    String removeAuthors = readString("Введите ID авторов для удаления через запятую: ");
                    if (!removeAuthors.trim().isEmpty()) {
                        for (String idStr : removeAuthors.split(",")) {
                            try {
                                int authorId = Integer.parseInt(idStr.trim());
                                bookDAO.deleteBookAuthor(bookId, authorId);
                                LOGGER.info(LABEL_AUTHOR_ID + authorId + REMOVED);
                            } catch (NumberFormatException e) {
                                LOGGER.info("  ID '" + idStr + "' не является числом");
                            }
                        }
                    }
                }
            }
            case 3 -> {
                String newAuthors = readString("Введите новые ID авторов через запятую (или Enter для очистки): ");
                bookDAO.deleteBookAuthors(bookId);
                if (!newAuthors.trim().isEmpty()) {
                    int order = 1;
                    for (String idStr : newAuthors.split(",")) {
                        try {
                            int authorId = Integer.parseInt(idStr.trim());
                            bookDAO.addBookAuthor(bookId, authorId, order);
                            LOGGER.info(LABEL_AUTHOR_ID + authorId + ADDED + " (порядок: " + order + ")");
                            order++;
                        } catch (NumberFormatException e) {
                            LOGGER.info("  ID '" + idStr + "' не является числом");
                        }
                    }
                }
                LOGGER.info("Авторы обновлены!");
            }
            default -> LOGGER.info("Авторы не изменены");
        }
    }

    private void deleteBook() throws SQLException {
        LOGGER.info("\nУДАЛЕНИЕ КНИГИ");
        int id = readInt("Введите ID книги: ", 1, Integer.MAX_VALUE);
        Book book = bookDAO.getBookById(id);
        if (book == null) {
            LOGGER.info("Книга не найдена");
            return;
        }
        LOGGER.info("Книга: " + book.getTitle());
        if (readYesNo("Вы уверены? (y/n): ")) {
            bookDAO.deleteBook(id);
            LOGGER.info("Книга удалена!");
        } else {
            LOGGER.info("Операция отменена");
        }
    }

    private void manageAuthors() {
        while (true) {
            LOGGER.info("\n" + HEADER_TOP);
            LOGGER.info("║           УПРАВЛЕНИЕ АВТОРАМИ                ║");
            LOGGER.info(HEADER_MID);
            LOGGER.info("║  1.  Добавить автора                         ║");
            LOGGER.info("║  2.  Список всех авторов                     ║");
            LOGGER.info("║  3.  Найти автора по ID                      ║");
            LOGGER.info("║  4.  Обновить автора                         ║");
            LOGGER.info("║  5.  Удалить автора                          ║");
            LOGGER.info(MENU_BACK);
            LOGGER.info(HEADER_BOTTOM);

            int choice = readInt(PROMPT_ACTION, 0, 5);
            try {
                switch (choice) {
                    case 1 -> addAuthor();
                    case 2 -> listAllAuthors();
                    case 3 -> findAuthorById();
                    case 4 -> updateAuthor();
                    case 5 -> deleteAuthor();
                    case 0 -> { return; }
                    default -> LOGGER.warning("Неверный выбор");
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, ERROR_DB + e.getMessage(), e);
            }
        }
    }

    private void addAuthor() throws SQLException {
        LOGGER.info("\nДОБАВЛЕНИЕ НОВОГО АВТОРА");
        Author author = new Author();
        author.setFirstName(readString("Имя: "));
        author.setLastName(readString("Фамилия: "));

        String birthDateStr = readString("Дата рождения (ГГГГ-ММ-ДД): ");
        if (!birthDateStr.trim().isEmpty()) {
            try {
                java.sql.Date sqlDate = java.sql.Date.valueOf(birthDateStr);
                author.setBirthDate(sqlDate.toString());
            } catch (IllegalArgumentException e) {
                LOGGER.info("Неверный формат даты. Используйте ГГГГ-ММ-ДД");
                return;
            }
        }
        author.setBiography(readString("Биография: "));
        authorDAO.addAuthor(author);
        LOGGER.info("Автор добавлен! ID: " + author.getId());
    }

    private void listAllAuthors() throws SQLException {
        LOGGER.info("\nСПИСОК ВСЕХ АВТОРОВ");
        List<Author> authors = authorDAO.getAllAuthors();
        if (authors.isEmpty()) {
            LOGGER.info("Авторов не найдено");
            return;
        }

        int idWidth = 4, nameWidth = 30, birthWidth = 14, bioWidth = 30;
        for (Author a : authors) {
            idWidth = Math.max(idWidth, String.valueOf(a.getId()).length());
            nameWidth = Math.max(nameWidth, a.getFullName().length());
            birthWidth = Math.max(birthWidth, (a.getBirthDate() != null ? a.getBirthDate() : "Не указана").length());
            bioWidth = Math.max(bioWidth, (a.getBiography() != null && a.getBiography().length() > 30) ? 30 : (a.getBiography() != null ? a.getBiography().length() : 4));
        }
        idWidth += 2;
        nameWidth += 2;
        birthWidth += 2;
        bioWidth += 2;

        String topLine = "┌" + "─".repeat(idWidth) + "┬" + "─".repeat(nameWidth) + "┬" + "─".repeat(birthWidth) + "┬" + "─".repeat(bioWidth) + "┐";
        String midLine = "├" + "─".repeat(idWidth) + "┼" + "─".repeat(nameWidth) + "┼" + "─".repeat(birthWidth) + "┼" + "─".repeat(bioWidth) + "┤";
        String bottomLine = "└" + "─".repeat(idWidth) + "┴" + "─".repeat(nameWidth) + "┴" + "─".repeat(birthWidth) + "┴" + "─".repeat(bioWidth) + "┘";

        LOGGER.info(topLine);
        LOGGER.info(String.format("│ %-" + (idWidth - 1) + "s│ %-" + (nameWidth - 1) + "s│ %-" + (birthWidth - 1) + "s│ %-" + (bioWidth - 1) + "s│", "ID", "Имя и фамилия", "Дата рождения", "Биография"));
        LOGGER.info(midLine);

        for (Author a : authors) {
            String bio = a.getBiography();
            if (bio != null && bio.length() > bioWidth - 1) bio = bio.substring(0, bioWidth - 4) + "...";
            String birth = a.getBirthDate() != null ? a.getBirthDate() : "Не указана";
            LOGGER.info(String.format("│ %-" + (idWidth - 1) + "d│ %-" + (nameWidth - 1) + "s│ %-" + (birthWidth - 1) + "s│ %-" + (bioWidth - 1) + "s│", a.getId(), a.getFullName(), birth, bio != null ? bio : "Нет"));
        }
        LOGGER.info(bottomLine);
    }

    private void findAuthorById() throws SQLException {
        LOGGER.info("\nПОИСК АВТОРА ПО ID");
        int id = readInt("Введите ID автора: ", 1, Integer.MAX_VALUE);
        Author author = authorDAO.getAuthorById(id);
        if (author == null) {
            LOGGER.info("Автор с ID " + id + NOT_FOUND);
            return;
        }

        String idStr = "ID:            " + author.getId();
        String firstNameStr = "Имя:           " + author.getFirstName();
        String lastNameStr = "Фамилия:       " + author.getLastName();
        String birthStr = "Дата рождения: " + (author.getBirthDate() != null ? author.getBirthDate() : "Не указана");
        String bioStr = "Биография:     " + (author.getBiography() != null ? author.getBiography() : "Нет");

        printTable("ИНФОРМАЦИЯ ОБ АВТОРЕ", idStr, firstNameStr, lastNameStr, birthStr, bioStr);
    }

    private void updateAuthor() throws SQLException {
        LOGGER.info("\nОБНОВЛЕНИЕ АВТОРА");
        int id = readInt("Введите ID автора: ", 1, Integer.MAX_VALUE);
        Author author = authorDAO.getAuthorById(id);
        if (author == null) {
            LOGGER.info("Автор не найден");
            return;
        }

        LOGGER.info("Текущие данные:");
        LOGGER.info("Имя: " + author.getFirstName());
        LOGGER.info("Фамилия: " + author.getLastName());
        LOGGER.info("Дата рождения: " + (author.getBirthDate() != null ? author.getBirthDate() : "Не указана"));
        LOGGER.info("Биография: " + (author.getBiography() != null ? author.getBiography() : "Нет"));
        LOGGER.info("Введите новые данные (оставьте пустым для сохранения текущего значения)");

        String firstName = readString("Имя (" + author.getFirstName() + "): ");
        if (!firstName.trim().isEmpty()) author.setFirstName(firstName);

        String lastName = readString("Фамилия (" + author.getLastName() + "): ");
        if (!lastName.trim().isEmpty()) author.setLastName(lastName);

        String birthDate = readString("Дата рождения (" + (author.getBirthDate() != null ? author.getBirthDate() : "") + "): ");
        if (!birthDate.trim().isEmpty()) author.setBirthDate(birthDate);

        String biography = readString("Биография (" + (author.getBiography() != null ? author.getBiography() : "") + "): ");
        if (!biography.trim().isEmpty()) author.setBiography(biography);

        authorDAO.updateAuthor(author);
        LOGGER.info("Автор обновлён!");
    }

    private void deleteAuthor() throws SQLException {
        LOGGER.info("\nУДАЛЕНИЕ АВТОРА");
        int id = readInt("Введите ID автора: ", 1, Integer.MAX_VALUE);
        Author author = authorDAO.getAuthorById(id);
        if (author == null) {
            LOGGER.info("Автор не найден");
            return;
        }
        LOGGER.info("Автор: " + author.getFullName());
        if (readYesNo("Вы уверены? (y/n): ")) {
            authorDAO.deleteAuthor(id);
            LOGGER.info("Автор удалён!");
        } else {
            LOGGER.info("Операция отменена");
        }
    }

    private void manageUsers() {
        while (true) {
            LOGGER.info("\n" + HEADER_TOP);
            LOGGER.info("║         УПРАВЛЕНИЕ ПОЛЬЗОВАТЕЛЯМИ            ║");
            LOGGER.info(HEADER_MID);
            LOGGER.info("║  1.  Добавить пользователя                   ║");
            LOGGER.info("║  2.  Список всех пользователей               ║");
            LOGGER.info("║  3.  Найти пользователя по ID                ║");
            LOGGER.info("║  4.  Обновить пользователя                   ║");
            LOGGER.info("║  5.  Удалить пользователя                    ║");
            LOGGER.info(MENU_BACK);
            LOGGER.info(HEADER_BOTTOM);

            int choice = readInt(PROMPT_ACTION, 0, 5);
            try {
                switch (choice) {
                    case 1 -> addUser();
                    case 2 -> listAllUsers();
                    case 3 -> findUserById();
                    case 4 -> updateUser();
                    case 5 -> deleteUser();
                    case 0 -> { return; }
                    default -> LOGGER.warning("Неверный выбор");
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, ERROR_DB + e.getMessage(), e);
            }
        }
    }

    private void addUser() throws SQLException {
        LOGGER.info("\nДОБАВЛЕНИЕ НОВОГО ПОЛЬЗОВАТЕЛЯ");
        User user = new User();
        user.setEmail(readString("Email: "));
        user.setPasswordHash(readString("Пароль (хеш): "));
        user.setFirstName(readString("Имя: "));
        user.setLastName(readString("Фамилия: "));
        user.setPhone(readString("Телефон: "));
        user.setAddress(readString("Адрес: "));
        user.setRole(readString("Роль (READER/LIBRARIAN/ADMIN): "));
        userDAO.addUser(user);
        LOGGER.info("Пользователь добавлен! ID: " + user.getId());
    }

    private void listAllUsers() throws SQLException {
        LOGGER.info("\nСПИСОК ВСЕХ ПОЛЬЗОВАТЕЛЕЙ");
        List<User> users = userDAO.getAllUsers();
        if (users.isEmpty()) {
            LOGGER.info("Пользователей не найдено");
            return;
        }

        int idWidth = 4, nameWidth = 25, emailWidth = 25, roleWidth = 12, statusWidth = 10;
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

        String topLine = "┌" + "─".repeat(idWidth) + "┬" + "─".repeat(nameWidth) + "┬" + "─".repeat(emailWidth) + "┬" + "─".repeat(roleWidth) + "┬" + "─".repeat(statusWidth) + "┐";
        String midLine = "├" + "─".repeat(idWidth) + "┼" + "─".repeat(nameWidth) + "┼" + "─".repeat(emailWidth) + "┼" + "─".repeat(roleWidth) + "┼" + "─".repeat(statusWidth) + "┤";
        String bottomLine = "└" + "─".repeat(idWidth) + "┴" + "─".repeat(nameWidth) + "┴" + "─".repeat(emailWidth) + "┴" + "─".repeat(roleWidth) + "┴" + "─".repeat(statusWidth) + "┘";

        LOGGER.info(topLine);
        LOGGER.info(String.format("│ %-" + (idWidth - 1) + "s│ %-" + (nameWidth - 1) + "s│ %-" + (emailWidth - 1) + "s│ %-" + (roleWidth - 1) + "s│ %-" + (statusWidth - 1) + "s│", "ID", "Имя и фамилия", "Email", "Роль", "Статус"));
        LOGGER.info(midLine);

        for (User u : users) {
            LOGGER.info(String.format("│ %-" + (idWidth - 1) + "d│ %-" + (nameWidth - 1) + "s│ %-" + (emailWidth - 1) + "s│ %-" + (roleWidth - 1) + "s│ %-" + (statusWidth - 1) + "s│",
                u.getId(), u.getFullName(), u.getEmail(), u.getRole(), u.isActive() ? "Активен" : "Неактивен"));
        }
        LOGGER.info(bottomLine);
    }

    private void findUserById() throws SQLException {
        LOGGER.info("\nПОИСК ПОЛЬЗОВАТЕЛЯ ПО ID");
        int id = readInt("Введите ID пользователя: ", 1, Integer.MAX_VALUE);
        User user = userDAO.getUserById(id);
        if (user == null) {
            LOGGER.info("Пользователь с ID " + id + NOT_FOUND);
            return;
        }

        List<Book> activeBooks = loanDAO.getActiveBooksByUserId(id);
        String booksStr = activeBooks.isEmpty() ? "Нет активных выдач" :
            activeBooks.stream().map(b -> b.getTitle() + " (ID: " + b.getId() + ")")
                .reduce((a, b) -> a + ", " + b).orElse("");

        String idStr = "ID:            " + user.getId();
        String firstNameStr = "Имя:           " + user.getFirstName();
        String lastNameStr = "Фамилия:       " + user.getLastName();
        String emailStr = "Email:         " + user.getEmail();
        String phoneStr = "Телефон:       " + (user.getPhone() != null ? user.getPhone() : "Не указан");
        String addressStr = "Адрес:         " + (user.getAddress() != null ? user.getAddress() : "Не указан");
        String roleStr = "Роль:          " + user.getRole();
        String activeStr = "Активен:       " + (user.isActive() ? "Да" : "Нет");
        String booksStrFull = "Книги на руках: " + booksStr;

        printTable("ИНФОРМАЦИЯ О ПОЛЬЗОВАТЕЛЕ",
            idStr, firstNameStr, lastNameStr, emailStr,
            phoneStr, addressStr, roleStr, activeStr, booksStrFull);
    }

    private void updateUser() throws SQLException {
        LOGGER.info("\nОБНОВЛЕНИЕ ПОЛЬЗОВАТЕЛЯ");
        int id = readInt("Введите ID пользователя: ", 1, Integer.MAX_VALUE);
        User user = userDAO.getUserById(id);
        if (user == null) {
            LOGGER.info("Пользователь не найден");
            return;
        }

        LOGGER.info("Текущие данные:");
        LOGGER.info("Имя: " + user.getFirstName());
        LOGGER.info("Фамилия: " + user.getLastName());
        LOGGER.info("Email: " + user.getEmail());
        LOGGER.info("Роль: " + user.getRole());
        LOGGER.info("Введите новые данные (оставьте пустым для сохранения текущего значения)");

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
        LOGGER.info("Пользователь обновлён!");
    }

    private void deleteUser() throws SQLException {
        LOGGER.info("\nУДАЛЕНИЕ ПОЛЬЗОВАТЕЛЯ");
        int id = readInt("Введите ID пользователя: ", 1, Integer.MAX_VALUE);
        User user = userDAO.getUserById(id);
        if (user == null) {
            LOGGER.info("Пользователь не найден");
            return;
        }
        LOGGER.info("Пользователь: " + user.getFullName() + " (" + user.getEmail() + ")");
        if (readYesNo("Вы уверены? (y/n): ")) {
            userDAO.deleteUser(id);
            LOGGER.info("Пользователь удалён!");
        } else {
            LOGGER.info("Операция отменена");
        }
    }

    private void manageLoans() {
        while (true) {
            LOGGER.info("\n" + HEADER_TOP);
            LOGGER.info("║           УПРАВЛЕНИЕ ВЫДАЧАМИ                ║");
            LOGGER.info(HEADER_MID);
            LOGGER.info("║  1.  Добавить выдачу                         ║");
            LOGGER.info("║  2.  Список всех выдач                       ║");
            LOGGER.info("║  3.  Активные выдачи                         ║");
            LOGGER.info("║  4.  Вернуть книгу                           ║");
            LOGGER.info("║  5.  Удалить выдачу                          ║");
            LOGGER.info(MENU_BACK);
            LOGGER.info(HEADER_BOTTOM);

            int choice = readInt(PROMPT_ACTION, 0, 5);
            try {
                switch (choice) {
                    case 1 -> addLoan();
                    case 2 -> listAllLoans();
                    case 3 -> listActiveLoans();
                    case 4 -> returnBook();
                    case 5 -> deleteLoan();
                    case 0 -> { return; }
                    default -> LOGGER.warning("Неверный выбор");
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, ERROR_DB + e.getMessage(), e);
            }
        }
    }

    private void addLoan() throws SQLException {
        LOGGER.info("\nНОВАЯ ВЫДАЧА КНИГИ");
        int bookId = readInt("ID книги: ", 1, Integer.MAX_VALUE);

        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            LOGGER.info("Книга с ID " + bookId + NOT_FOUND);
            return;
        }

        int available = bookDAO.getAvailableCopies(bookId);
        if (available <= 0) {
            LOGGER.info("Нет доступных экземпляров книги \"" + book.getTitle() + "\"");
            LOGGER.info(LABEL_TOTAL_COPIES + book.getTotalCopies() + ", доступно: 0");
            return;
        }

        int userId = readInt("ID пользователя: ", 1, Integer.MAX_VALUE);
        User user = userDAO.getUserById(userId);
        if (user == null) {
            LOGGER.info("Пользователь с ID " + userId + NOT_FOUND);
            return;
        }

        List<Loan> userLoans = loanDAO.getActiveLoansByUserAndBook(userId, bookId);
        if (!userLoans.isEmpty()) {
            LOGGER.info("Пользователь уже взял эту книгу и ещё не вернул");
            return;
        }

        Loan loan = new Loan();
        loan.setBookId(bookId);
        loan.setUserId(userId);

        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(30);

        loan.setLoanDate(today.toString());
        loan.setDueDate(dueDate.toString());
        loan.setStatus("ACTIVE");

        loanDAO.addLoan(loan);
        LOGGER.info("Книга выдана! ID выдачи: " + loan.getId());
        LOGGER.info("Дата возврата: " + loan.getDueDate());
        LOGGER.info("Осталось доступных экземпляров: " + (available - 1));
    }

    private void listAllLoans() throws SQLException {
        LOGGER.info("\nСПИСОК ВСЕХ ВЫДАЧ");
        List<Loan> loans = loanDAO.getAllLoans();
        if (loans.isEmpty()) {
            LOGGER.info("Выдач не найдено");
            return;
        }
        LOGGER.info("┌────┬──────────┬────────────┬────────────┬────────────┬────────────┬──────────────┐");
        LOGGER.info("│ ID │ Книга    │ Читатель   │ Дата выдачи│ Срок       │ Статус     │ Штраф        │");
        LOGGER.info("├────┼──────────┼────────────┼────────────┼────────────┼────────────┼──────────────┤");
        for (Loan l : loans) {
            LOGGER.info(String.format("│ %-2d │ %-8d │ %-10d │ %-10s │ %-10s │ %-10s │ %-12.2f │",
                l.getId(), l.getBookId(), l.getUserId(), l.getLoanDate(), l.getDueDate(), l.getStatus(), l.getFineAmount()));
        }
        LOGGER.info("└────┴──────────┴────────────┴────────────┴────────────┴────────────┴──────────────┘");
    }

    private void listActiveLoans() throws SQLException {
        LOGGER.info("\nАКТИВНЫЕ ВЫДАЧИ");
        List<Loan> loans = loanDAO.getActiveLoans();
        if (loans.isEmpty()) {
            LOGGER.info("Нет активных выдач");
            return;
        }
        LOGGER.info("┌────┬──────────┬────────────┬────────────┬────────────┬──────────────┐");
        LOGGER.info("│ ID │ Книга    │ Читатель   │ Дата выдачи│ Срок       │ Штраф        │");
        LOGGER.info("├────┼──────────┼────────────┼────────────┼────────────┼──────────────┤");
        for (Loan l : loans) {
            LOGGER.info(String.format("│ %-2d │ %-8d │ %-10d │ %-10s │ %-10s │ %-12.2f │",
                l.getId(), l.getBookId(), l.getUserId(), l.getLoanDate(), l.getDueDate(), l.getFineAmount()));
        }
        LOGGER.info("└────┴──────────┴────────────┴────────────┴────────────┴──────────────┘");
    }

    private void returnBook() throws SQLException {
        LOGGER.info("\nВОЗВРАТ КНИГИ");
        int loanId = readInt("Введите ID выдачи: ", 1, Integer.MAX_VALUE);

        Loan loan = loanDAO.getLoanById(loanId);
        if (loan == null) {
            LOGGER.info("Выдача с ID " + loanId + NOT_FOUND);
            return;
        }

        if (!loan.getStatus().equals("ACTIVE") && !loan.getStatus().equals("OVERDUE")) {
            LOGGER.info("Эта выдача уже завершена. Статус: " + loan.getStatus());
            return;
        }

        LOGGER.info("\nИнформация о выдаче:");
        LOGGER.info("  Книга ID: " + loan.getBookId());
        LOGGER.info("  Пользователь ID: " + loan.getUserId());
        LOGGER.info("  Дата выдачи: " + loan.getLoanDate());
        LOGGER.info("  Срок возврата: " + loan.getDueDate());
        LOGGER.info("  Статус: " + loan.getStatus());

        LocalDate today = LocalDate.now();
        LocalDate dueDate = LocalDate.parse(loan.getDueDate());

        double fine = 0;
        if (dueDate.isBefore(today)) {
            long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(dueDate, today);
            double autoFine = daysOverdue * 0.50;

            LOGGER.info("\nКнига просрочена на " + daysOverdue + " дней");
            LOGGER.info("  Автоматический штраф: " + autoFine + " руб. (0.50 руб/день)");
            LOGGER.info("  Введите 0 для автоматического расчёта");

            double userFine = readDouble("  Введите сумму штрафа: ");
            if (userFine == 0) {
                fine = autoFine;
                LOGGER.info("  Использован автоматический расчёт: " + fine + " руб.");
            } else if (userFine > 0) {
                fine = userFine;
            } else {
                LOGGER.info("  Штраф не может быть отрицательным. Использован авторасчёт.");
                fine = autoFine;
            }
        } else {
            LOGGER.info("\nКнига возвращена вовремя, штраф не назначается");
            fine = 0;
        }

        loanDAO.returnBook(loanId, fine);
        int available = bookDAO.getAvailableCopies(loan.getBookId());
        LOGGER.info("\nКнига возвращена! Штраф: " + fine + " руб.");
        LOGGER.info("Доступно экземпляров: " + available);
    }

    private void deleteLoan() throws SQLException {
        LOGGER.info("\nУДАЛЕНИЕ ВЫДАЧИ");
        int id = readInt("Введите ID выдачи: ", 1, Integer.MAX_VALUE);
        if (readYesNo("Вы уверены? (y/n): ")) {
            loanDAO.deleteLoan(id);
            LOGGER.info("Выдача удалена!");
        } else {
            LOGGER.info("Операция отменена");
        }
    }

    private void managePublishers() {
        while (true) {
            LOGGER.info("\n" + HEADER_TOP);
            LOGGER.info("║           УПРАВЛЕНИЕ ИЗДАТЕЛЯМИ              ║");
            LOGGER.info(HEADER_MID);
            LOGGER.info("║  1.  Добавить издателя                       ║");
            LOGGER.info("║  2.  Список всех издателей                   ║");
            LOGGER.info("║  3.  Найти издателя по ID                    ║");
            LOGGER.info("║  4.  Обновить издателя                       ║");
            LOGGER.info("║  5.  Удалить издателя                        ║");
            LOGGER.info(MENU_BACK);
            LOGGER.info(HEADER_BOTTOM);

            int choice = readInt(PROMPT_ACTION, 0, 5);
            try {
                switch (choice) {
                    case 1 -> addPublisher();
                    case 2 -> listAllPublishers();
                    case 3 -> findPublisherById();
                    case 4 -> updatePublisher();
                    case 5 -> deletePublisher();
                    case 0 -> { return; }
                    default -> LOGGER.warning("Неверный выбор");
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, ERROR_DB + e.getMessage(), e);
            }
        }
    }

    private void addPublisher() throws SQLException {
        LOGGER.info("\nДОБАВЛЕНИЕ НОВОГО ИЗДАТЕЛЯ");
        Publisher publisher = new Publisher();
        publisher.setName(readString("Название издательства: "));
        publisher.setAddress(readString("Адрес: "));
        publisher.setPhone(readString("Телефон: "));
        publisher.setEmail(readString("Email: "));
        publisher.setWebsite(readString("Сайт: "));
        publisherDAO.addPublisher(publisher);
        LOGGER.info("Издатель добавлен! ID: " + publisher.getId());
    }

    private void listAllPublishers() throws SQLException {
        LOGGER.info("\nСПИСОК ВСЕХ ИЗДАТЕЛЕЙ");
        List<Publisher> publishers = publisherDAO.getAllPublishers();
        if (publishers.isEmpty()) {
            LOGGER.info("Издателей не найдено");
            return;
        }

        int idWidth = 4, nameWidth = 30, emailWidth = 25, phoneWidth = 20;
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

        String topLine = "┌" + "─".repeat(idWidth) + "┬" + "─".repeat(nameWidth) + "┬" + "─".repeat(emailWidth) + "┬" + "─".repeat(phoneWidth) + "┐";
        String midLine = "├" + "─".repeat(idWidth) + "┼" + "─".repeat(nameWidth) + "┼" + "─".repeat(emailWidth) + "┼" + "─".repeat(phoneWidth) + "┤";
        String bottomLine = "└" + "─".repeat(idWidth) + "┴" + "─".repeat(nameWidth) + "┴" + "─".repeat(emailWidth) + "┴" + "─".repeat(phoneWidth) + "┘";

        LOGGER.info(topLine);
        LOGGER.info(String.format("│ %-" + (idWidth - 1) + "s│ %-" + (nameWidth - 1) + "s│ %-" + (emailWidth - 1) + "s│ %-" + (phoneWidth - 1) + "s│", "ID", "Название", "Email", "Телефон"));
        LOGGER.info(midLine);

        for (Publisher p : publishers) {
            LOGGER.info(String.format("│ %-" + (idWidth - 1) + "d│ %-" + (nameWidth - 1) + "s│ %-" + (emailWidth - 1) + "s│ %-" + (phoneWidth - 1) + "s│",
                p.getId(), p.getName(), p.getEmail() != null ? p.getEmail() : "", p.getPhone() != null ? p.getPhone() : ""));
        }
        LOGGER.info(bottomLine);
    }

    private void findPublisherById() throws SQLException {
        LOGGER.info("\nПОИСК ИЗДАТЕЛЯ ПО ID");
        int id = readInt("Введите ID издателя: ", 1, Integer.MAX_VALUE);
        Publisher publisher = publisherDAO.getPublisherById(id);
        if (publisher == null) {
            LOGGER.info("Издатель с ID " + id + NOT_FOUND);
            return;
        }

        String idStr = "ID:          " + publisher.getId();
        String nameStr = "Название:    " + publisher.getName();
        String addressStr = "Адрес:       " + (publisher.getAddress() != null ? publisher.getAddress() : "Не указан");
        String phoneStr = "Телефон:     " + (publisher.getPhone() != null ? publisher.getPhone() : "Не указан");
        String emailStr = "Email:       " + (publisher.getEmail() != null ? publisher.getEmail() : "Не указан");
        String websiteStr = "Сайт:        " + (publisher.getWebsite() != null ? publisher.getWebsite() : "Не указан");

        printTable("ИНФОРМАЦИЯ ОБ ИЗДАТЕЛЕ", idStr, nameStr, addressStr, phoneStr, emailStr, websiteStr);
    }

    private void updatePublisher() throws SQLException {
        LOGGER.info("\nОБНОВЛЕНИЕ ИЗДАТЕЛЯ");
        int id = readInt("Введите ID издателя: ", 1, Integer.MAX_VALUE);
        Publisher publisher = publisherDAO.getPublisherById(id);
        if (publisher == null) {
            LOGGER.info("Издатель не найден");
            return;
        }

        LOGGER.info("Текущие данные:");
        LOGGER.info("ID:          " + publisher.getId());
        LOGGER.info("Название:    " + publisher.getName());
        LOGGER.info("Адрес:       " + (publisher.getAddress() != null ? publisher.getAddress() : "Не указан"));
        LOGGER.info("Телефон:     " + (publisher.getPhone() != null ? publisher.getPhone() : "Не указан"));
        LOGGER.info("Email:       " + (publisher.getEmail() != null ? publisher.getEmail() : "Не указан"));
        LOGGER.info("Сайт:        " + (publisher.getWebsite() != null ? publisher.getWebsite() : "Не указан"));
        LOGGER.info("\nВведите новые данные (оставьте пустым для сохранения текущего значения)");

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
        LOGGER.info("Издатель обновлён!");
    }

    private void deletePublisher() throws SQLException {
        LOGGER.info("\nУДАЛЕНИЕ ИЗДАТЕЛЯ");
        int id = readInt("Введите ID издателя: ", 1, Integer.MAX_VALUE);
        Publisher publisher = publisherDAO.getPublisherById(id);
        if (publisher == null) {
            LOGGER.info("Издатель не найден");
            return;
        }
        LOGGER.info("Издатель: " + publisher.getName());
        if (readYesNo("Вы уверены? (y/n): ")) {
            try {
                publisherDAO.deletePublisher(id);
                LOGGER.info("Издатель удалён!");
            } catch (SQLException e) {
                if (e.getMessage().contains("foreign key")) {
                    LOGGER.info("Невозможно удалить издателя: есть книги, привязанные к нему");
                } else {
                    throw e;
                }
            }
        } else {
            LOGGER.info("Операция отменена");
        }
    }

    private void searchBooks() {
        LOGGER.info("\nПОИСК КНИГ");
        String title = readString("Название (или часть): ");
        String author = readString("Автор (или часть): ");
        String genre = readString("Жанр: ");

        try {
            int total = bookDAO.countBooks(title, author, genre);
            if (total == 0) {
                LOGGER.info("Книг не найдено");
                return;
            }

            int page = 1;
            int pageSize = 5;
            int totalPages = (int) Math.ceil((double) total / pageSize);

            while (true) {
                LOGGER.info("\nРезультаты поиска (стр. " + page + "/" + totalPages + ", всего: " + total + ")");
                List<Book> books = bookDAO.searchBooks(title, author, genre, page, pageSize);
                printBookList(books);

                if (page < totalPages) {
                    LOGGER.info("Нажмите Enter для следующей страницы, или q для выхода");
                    String input = scanner.nextLine();
                    if (input.equalsIgnoreCase("q")) break;
                    page++;
                } else {
                    break;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Ошибка поиска: " + e.getMessage(), e);
        }
    }

    private void printBookList(List<Book> books) {
        if (books.isEmpty()) {
            LOGGER.info("Книг не найдено");
            return;
        }
        LOGGER.info("┌────┬────────────────────────────────────────────┬──────────┬─────────────┐");
        LOGGER.info("│ ID │ Название                                   │ Год      │ Доступно    │");
        LOGGER.info("├────┼────────────────────────────────────────────┼──────────┼─────────────┤");
        for (Book b : books) {
            int available;
            try {
                available = bookDAO.getAvailableCopies(b.getId());
            } catch (SQLException e) {
                available = 0;
            }
            LOGGER.info(String.format("│ %-2d │ %-42s │ %-8d │ %-11d │",
                b.getId(),
                b.getTitle().length() > 42 ? b.getTitle().substring(0, 39) + "..." : b.getTitle(),
                b.getPublicationYear(),
                available));
        }
        LOGGER.info("└────┴────────────────────────────────────────────┴──────────┴─────────────┘");
    }

    private void printTable(String title, String... lines) {
        int maxLen = 0;
        for (String line : lines) {
            maxLen = Math.max(maxLen, line.length());
        }
        int width = Math.max(maxLen + 2, title.length() + 4);

        LOGGER.info("┌" + "─".repeat(width) + "┐");
        LOGGER.info("│" + centerString(title, width) + "│");
        LOGGER.info("├" + "─".repeat(width) + "┤");
        for (String line : lines) {
            LOGGER.info("│ " + padRight(line, width - 1) + "│");
        }
        LOGGER.info("└" + "─".repeat(width) + "┘");
    }

    private String padRight(String str, int length) {
        if (str.length() > length) return str.substring(0, length);
        return str + " ".repeat(length - str.length());
    }

    private String centerString(String str, int width) {
        if (str.length() >= width) return str;
        int padding = width - str.length();
        int leftPad = padding / 2;
        int rightPad = padding - leftPad;
        return " ".repeat(leftPad) + str + " ".repeat(rightPad);
    }

    private String formatAuthors(List<Author> authors) {
        if (authors.isEmpty()) return "Не указаны";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < authors.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(authors.get(i).getFullName()).append(" (ID: ").append(authors.get(i).getId()).append(")");
        }
        return sb.toString();
    }

    private String formatGenres(List<Genre> genres) {
        if (genres.isEmpty()) return "Не указаны";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < genres.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(genres.get(i).getName()).append(" (ID: ").append(genres.get(i).getId()).append(")");
        }
        return sb.toString();
    }

    private String readString(String prompt) {
        LOGGER.info(prompt);
        return scanner.nextLine().trim();
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            try {
                LOGGER.info(prompt);
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) return value;
                LOGGER.info("Введите число от " + min + " до " + max);
            } catch (NumberFormatException e) {
                LOGGER.info("Введите корректное число");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            try {
                LOGGER.info(prompt);
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) return 0;
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                LOGGER.info("Введите корректное число");
            }
        }
    }

    private boolean readYesNo(String prompt) {
        while (true) {
            String input = readString(prompt);
            if (input.equalsIgnoreCase("y") || input.equalsIgnoreCase("yes")) return true;
            if (input.equalsIgnoreCase("n") || input.equalsIgnoreCase("no")) return false;
            LOGGER.info("Введите y (да) или n (нет)");
        }
    }
}