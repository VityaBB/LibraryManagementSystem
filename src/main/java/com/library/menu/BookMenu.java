package com.library.menu;

import com.library.dao.BookDAO;
import com.library.dao.AuthorDAO;
import com.library.dao.GenreDAO;
import com.library.dao.PublisherDAO;
import com.library.models.Book;
import com.library.models.Author;
import com.library.models.Genre;
import com.library.models.Publisher;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static com.library.menu.ConsoleUtils.*;

public class BookMenu {
    private final BookDAO bookDAO;
    private final AuthorDAO authorDAO;
    private final GenreDAO genreDAO;
    private final PublisherDAO publisherDAO;

    public BookMenu(Connection connection) {
        this.bookDAO = new BookDAO(connection);
        this.authorDAO = new AuthorDAO(connection);
        this.genreDAO = new GenreDAO(connection);
        this.publisherDAO = new PublisherDAO(connection);
    }

    public void showMenu() {
        while (true) {
            printHeader("УПРАВЛЕНИЕ КНИГАМИ");
            System.out.println("1. Добавить книгу");
            System.out.println("2. Список всех книг");
            System.out.println("3. Найти книгу по ID");
            System.out.println("4. Обновить книгу");
            System.out.println("5. Удалить книгу");
            System.out.println("0. Назад");
            printSeparator();

            int choice = readInt("Выберите действие: ", 0, 5);
            try {
                switch (choice) {
                    case 1 -> addBook();
                    case 2 -> listAllBooks();
                    case 3 -> findBookById();
                    case 4 -> updateBook();
                    case 5 -> deleteBook();
                    case 0 -> { return; }
                    default -> printError("Неверный выбор");
                }
            } catch (SQLException e) {
                printError("Ошибка БД: " + e.getMessage());
            }
        }
    }

    private void addBook() throws SQLException {
        printHeader("ДОБАВЛЕНИЕ НОВОЙ КНИГИ");

        showPublishers();
        showGenres();
        showAuthors();

        Book book = new Book();
        book.setTitle(readString("Название: "));
        book.setIsbn(readString("ISBN: "));
        book.setPublicationYear(readInt("Год публикации: ", 1, 9999));

        int publisherId = readInt("ID издательства: ", 1, Integer.MAX_VALUE);
        Publisher publisher = publisherDAO.getPublisherById(publisherId);
        if (publisher == null) {
            printError("Издатель с ID " + publisherId + " не найден");
            return;
        }
        book.setPublisherId(publisherId);

        book.setTotalCopies(readInt("Всего экземпляров: ", 0, Integer.MAX_VALUE));
        book.setPageCount(readInt("Количество страниц: ", 0, Integer.MAX_VALUE));
        book.setDescription(readString("Описание: "));

        bookDAO.addBook(book);
        printSuccess("Книга добавлена! ID: " + book.getId());

        addGenresToBook(book.getId());
        addAuthorsToBook(book.getId());
    }

    private void showPublishers() throws SQLException {
        printInfo("Список доступных издателей:");
        List<Publisher> publishers = publisherDAO.getAllPublishers();
        if (publishers.isEmpty()) {
            printError("В базе нет издателей!");
            return;
        }
        for (Publisher p : publishers) {
            System.out.println("  ID: " + p.getId() + " | " + p.getName());
        }
    }

    private void showGenres() throws SQLException {
        printInfo("Список доступных жанров:");
        List<Genre> genres = genreDAO.getAllGenres();
        if (genres.isEmpty()) {
            printError("В базе нет жанров!");
            return;
        }
        for (Genre g : genres) {
            System.out.println("  ID: " + g.getId() + " | " + g.getName());
        }
    }

    private void showAuthors() throws SQLException {
        printInfo("Список доступных авторов:");
        List<Author> authors = authorDAO.getAllAuthors();
        if (authors.isEmpty()) {
            printError("В базе нет авторов!");
            return;
        }
        for (Author a : authors) {
            System.out.println("  ID: " + a.getId() + " | " + a.getFullName());
        }
    }

    private void addGenresToBook(int bookId) throws SQLException {
        String genreIdsInput = readString("Введите ID жанров через запятую: ");
        if (!genreIdsInput.trim().isEmpty()) {
            for (String idStr : genreIdsInput.split(",")) {
                try {
                    int genreId = Integer.parseInt(idStr.trim());
                    bookDAO.addBookGenre(bookId, genreId);
                    printInfo("Жанр ID " + genreId + " добавлен");
                } catch (NumberFormatException e) {
                    printError("ID '" + idStr + "' не является числом");
                }
            }
        }
    }

    private void addAuthorsToBook(int bookId) throws SQLException {
        String authorIdsInput = readString("Введите ID авторов через запятую: ");
        if (!authorIdsInput.trim().isEmpty()) {
            int order = 1;
            for (String idStr : authorIdsInput.split(",")) {
                try {
                    int authorId = Integer.parseInt(idStr.trim());
                    bookDAO.addBookAuthor(bookId, authorId, order);
                    printInfo("Автор ID " + authorId + " добавлен (порядок: " + order + ")");
                    order++;
                } catch (NumberFormatException e) {
                    printError("ID '" + idStr + "' не является числом");
                }
            }
        }
    }

    private void listAllBooks() throws SQLException {
        printHeader("СПИСОК ВСЕХ КНИГ");
        List<Book> books = bookDAO.getAllBooks();
        printBookList(books);
    }

    private void findBookById() throws SQLException {
        printHeader("ПОИСК КНИГИ ПО ID");
        int id = readInt("Введите ID книги: ", 1, Integer.MAX_VALUE);
        Book book = bookDAO.getBookById(id);
        if (book == null) {
            printError("Книга с ID " + id + " не найдена");
            return;
        }

        Publisher publisher = publisherDAO.getPublisherById(book.getPublisherId());
        String publisherInfo = (publisher != null) ? publisher.getName() + " (ID: " + publisher.getId() + ")" : "Не указан";

        List<Author> authors = authorDAO.getAuthorsByBookId(id);
        List<Genre> genres = genreDAO.getGenresByBookId(id);
        int available = bookDAO.getAvailableCopies(id);

        printTable("ИНФОРМАЦИЯ О КНИГЕ",
            "ID книги:          " + book.getId(),
            "Название:          " + book.getTitle(),
            "ISBN:              " + book.getIsbn(),
            "Год публикации:    " + book.getPublicationYear(),
            "Издательство:      " + publisherInfo,
            "Авторы:            " + formatAuthors(authors),
            "Жанры:             " + formatGenres(genres),
            "Всего экземпляров: " + book.getTotalCopies(),
            "Доступно:          " + available,
            "Страниц:           " + book.getPageCount(),
            "Описание:          " + (book.getDescription() != null ? book.getDescription() : "Нет описания")
        );
    }

    private void updateBook() throws SQLException {
        printHeader("ОБНОВЛЕНИЕ КНИГИ");
        int id = readInt("Введите ID книги: ", 1, Integer.MAX_VALUE);
        Book book = bookDAO.getBookById(id);
        if (book == null) {
            printError("Книга не найдена");
            return;
        }

        printInfo("Текущие данные:");
        System.out.println(book);

        List<Author> currentAuthors = authorDAO.getAuthorsByBookId(id);
        List<Genre> currentGenres = genreDAO.getGenresByBookId(id);

        System.out.println("\nТекущие авторы:");
        if (currentAuthors.isEmpty()) {
            System.out.println("  (нет)");
        } else {
            for (Author a : currentAuthors) {
                System.out.println("  ID: " + a.getId() + " | " + a.getFullName());
            }
        }

        System.out.println("\nТекущие жанры:");
        if (currentGenres.isEmpty()) {
            System.out.println("  (нет)");
        } else {
            for (Genre g : currentGenres) {
                System.out.println("  ID: " + g.getId() + " | " + g.getName());
            }
        }

        printInfo("Введите новые данные (оставьте пустым для сохранения текущего значения)");

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
        printSuccess("Основные данные книги обновлены!");

        updateGenres(id, currentGenres);
        updateAuthors(id, currentAuthors);

        printSuccess("Книга полностью обновлена!");
    }

    private void updateGenres(int bookId, List<Genre> currentGenres) throws SQLException {
        printSubHeader("УПРАВЛЕНИЕ ЖАНРАМИ");
        System.out.println("1. Добавить жанры");
        System.out.println("2. Удалить жанры");
        System.out.println("3. Заменить все жанры");
        System.out.println("0. Пропустить");
        int choice = readInt("Выберите действие: ", 0, 3);

        if (choice == 0) {
            printInfo("Жанры не изменены");
            return;
        }

        List<Genre> allGenres = genreDAO.getAllGenres();
        System.out.println("Доступные жанры:");
        for (Genre g : allGenres) {
            System.out.println("  ID: " + g.getId() + " | " + g.getName());
        }

        switch (choice) {
            case 1 -> {
                String addGenres = readString("Введите ID жанров для добавления: ");
                if (!addGenres.trim().isEmpty()) {
                    for (String idStr : addGenres.split(",")) {
                        try {
                            int genreId = Integer.parseInt(idStr.trim());
                            if (currentGenres.stream().noneMatch(g -> g.getId() == genreId)) {
                                bookDAO.addBookGenre(bookId, genreId);
                                printInfo("Жанр ID " + genreId + " добавлен");
                            } else {
                                printInfo("Жанр ID " + genreId + " уже есть");
                            }
                        } catch (NumberFormatException e) {
                            printError("ID '" + idStr + "' не является числом");
                        }
                    }
                    printSuccess("Жанры добавлены!");
                }
            }
            case 2 -> {
                if (currentGenres.isEmpty()) {
                    printError("У книги нет жанров для удаления");
                } else {
                    String removeGenres = readString("Введите ID жанров для удаления: ");
                    if (!removeGenres.trim().isEmpty()) {
                        for (String idStr : removeGenres.split(",")) {
                            try {
                                int genreId = Integer.parseInt(idStr.trim());
                                bookDAO.deleteBookGenre(bookId, genreId);
                                printInfo("Жанр ID " + genreId + " удален");
                            } catch (NumberFormatException e) {
                                printError("ID '" + idStr + "' не является числом");
                            }
                        }
                    }
                }
            }
            case 3 -> {
                String newGenres = readString("Введите новые ID жанров (или Enter для очистки): ");
                bookDAO.deleteBookGenres(bookId);
                if (!newGenres.trim().isEmpty()) {
                    for (String idStr : newGenres.split(",")) {
                        try {
                            int genreId = Integer.parseInt(idStr.trim());
                            bookDAO.addBookGenre(bookId, genreId);
                            printInfo("Жанр ID " + genreId + " добавлен");
                        } catch (NumberFormatException e) {
                            printError("ID '" + idStr + "' не является числом");
                        }
                    }
                }
                printSuccess("Жанры обновлены!");
            }
        }
    }

    private void updateAuthors(int bookId, List<Author> currentAuthors) throws SQLException {
        printSubHeader("УПРАВЛЕНИЕ АВТОРАМИ");
        System.out.println("1. Добавить авторов");
        System.out.println("2. Удалить авторов");
        System.out.println("3. Заменить всех авторов");
        System.out.println("0. Пропустить");
        int choice = readInt("Выберите действие: ", 0, 3);

        if (choice == 0) {
            printInfo("Авторы не изменены");
            return;
        }

        List<Author> allAuthors = authorDAO.getAllAuthors();
        System.out.println("Доступные авторы:");
        for (Author a : allAuthors) {
            System.out.println("  ID: " + a.getId() + " | " + a.getFullName());
        }

        switch (choice) {
            case 1 -> {
                String addAuthors = readString("Введите ID авторов для добавления: ");
                if (!addAuthors.trim().isEmpty()) {
                    int maxOrder = currentAuthors.size();
                    for (String idStr : addAuthors.split(",")) {
                        try {
                            int authorId = Integer.parseInt(idStr.trim());
                            if (currentAuthors.stream().noneMatch(a -> a.getId() == authorId)) {
                                maxOrder++;
                                bookDAO.addBookAuthor(bookId, authorId, maxOrder);
                                printInfo("Автор ID " + authorId + " добавлен (порядок: " + maxOrder + ")");
                            } else {
                                printInfo("Автор ID " + authorId + " уже есть");
                            }
                        } catch (NumberFormatException e) {
                            printError("ID '" + idStr + "' не является числом");
                        }
                    }
                    printSuccess("Авторы добавлены!");
                }
            }
            case 2 -> {
                if (currentAuthors.isEmpty()) {
                    printError("У книги нет авторов для удаления");
                } else {
                    String removeAuthors = readString("Введите ID авторов для удаления: ");
                    if (!removeAuthors.trim().isEmpty()) {
                        for (String idStr : removeAuthors.split(",")) {
                            try {
                                int authorId = Integer.parseInt(idStr.trim());
                                bookDAO.deleteBookAuthor(bookId, authorId);
                                printInfo("Автор ID " + authorId + " удален");
                            } catch (NumberFormatException e) {
                                printError("ID '" + idStr + "' не является числом");
                            }
                        }
                    }
                }
            }
            case 3 -> {
                String newAuthors = readString("Введите новые ID авторов (или Enter для очистки): ");
                bookDAO.deleteBookAuthors(bookId);
                if (!newAuthors.trim().isEmpty()) {
                    int order = 1;
                    for (String idStr : newAuthors.split(",")) {
                        try {
                            int authorId = Integer.parseInt(idStr.trim());
                            bookDAO.addBookAuthor(bookId, authorId, order);
                            printInfo("Автор ID " + authorId + " добавлен (порядок: " + order + ")");
                            order++;
                        } catch (NumberFormatException e) {
                            printError("ID '" + idStr + "' не является числом");
                        }
                    }
                }
                printSuccess("Авторы обновлены!");
            }
        }
    }

    private void deleteBook() throws SQLException {
        printHeader("УДАЛЕНИЕ КНИГИ");
        int id = readInt("Введите ID книги: ", 1, Integer.MAX_VALUE);
        Book book = bookDAO.getBookById(id);
        if (book == null) {
            printError("Книга не найдена");
            return;
        }
        System.out.println("Книга: " + book.getTitle());
        if (readYesNo("Вы уверены? (y/n): ")) {
            bookDAO.deleteBook(id);
            printSuccess("Книга удалена!");
        } else {
            printInfo("Операция отменена");
        }
    }
}