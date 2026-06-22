package com.library.dao;
import com.library.models.Genre;
import com.library.DatabaseConnection;
import com.library.models.Author;
import com.library.models.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    
    public void addBook(Book book) throws SQLException {
        String sql = "INSERT INTO books (title, isbn, publication_year, publisher_id, " +
                     "total_copies, page_count, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getIsbn());
            stmt.setInt(3, book.getPublicationYear());
            stmt.setInt(4, book.getPublisherId());
            stmt.setInt(5, book.getTotalCopies());
            stmt.setInt(6, book.getPageCount());
            stmt.setString(7, book.getDescription());
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                book.setId(rs.getInt(1));
            }
        }
    }

    public List<Book> getAllBooks() throws SQLException {
        String sql = "SELECT id, title, isbn, publication_year, publisher_id, total_copies, page_count, description FROM books ORDER BY id";
        List<Book> books = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        }
        return books;
    }

    public Book getBookById(int id) throws SQLException {
        String sql = "SELECT id, title, isbn, publication_year, publisher_id, total_copies, page_count, description FROM books WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToBook(rs);
            }
        }
        return null;
    }

    public List<Book> searchBooks(String title, String author, String genre, 
                                   int page, int pageSize) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DISTINCT b.id, b.title, b.isbn, b.publication_year, b.publisher_id, ");
        sql.append("b.total_copies, b.page_count, b.description, p.name as publisher_name, ");
        sql.append("STRING_AGG(DISTINCT a.last_name || ' ' || a.first_name, ', ') AS authors, ");
        sql.append("STRING_AGG(DISTINCT g.name, ', ') AS genres ");
        sql.append("FROM books b ");
        sql.append("LEFT JOIN book_authors ba ON b.id = ba.book_id ");
        sql.append("LEFT JOIN authors a ON ba.author_id = a.id ");
        sql.append("LEFT JOIN book_genres bg ON b.id = bg.book_id ");
        sql.append("LEFT JOIN genres g ON bg.genre_id = g.id ");
        sql.append("LEFT JOIN publishers p ON b.publisher_id = p.id ");
        sql.append("WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        
        if (title != null && !title.trim().isEmpty()) {
            sql.append("AND b.title ILIKE ? ");
            params.add("%" + title + "%");
        }
        if (author != null && !author.trim().isEmpty()) {
            sql.append("AND (a.first_name ILIKE ? OR a.last_name ILIKE ?) ");
            params.add("%" + author + "%");
            params.add("%" + author + "%");
        }
        if (genre != null && !genre.trim().isEmpty()) {
            sql.append("AND g.name = ? ");
            params.add(genre);
        }
        
        sql.append("GROUP BY b.id, p.name ");
        sql.append("ORDER BY b.id ");
        sql.append("LIMIT ? OFFSET ?");
        
        params.add(pageSize);
        params.add((page - 1) * pageSize);
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            List<Book> books = new ArrayList<>();
            while (rs.next()) {
                Book book = mapResultSetToBook(rs);
                books.add(book);
            }
            return books;
        }
    }

    public void updateBook(Book book) throws SQLException {
        String sql = "UPDATE books SET title = ?, isbn = ?, publication_year = ?, " +
                     "publisher_id = ?, total_copies = ?, " +
                     "page_count = ?, description = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getIsbn());
            stmt.setInt(3, book.getPublicationYear());
            stmt.setInt(4, book.getPublisherId());
            stmt.setInt(5, book.getTotalCopies());       
            stmt.setInt(6, book.getPageCount());
            stmt.setString(7, book.getDescription());
            stmt.setInt(8, book.getId());
            
            stmt.executeUpdate();
        }
    }

    public void deleteBook(int id) throws SQLException {
        String sql = "DELETE FROM books WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.setTitle(rs.getString("title"));
        book.setIsbn(rs.getString("isbn"));
        book.setPublicationYear(rs.getInt("publication_year"));
        book.setPublisherId(rs.getInt("publisher_id"));
        book.setTotalCopies(rs.getInt("total_copies"));
        book.setPageCount(rs.getInt("page_count"));
        book.setDescription(rs.getString("description"));
        return book;
    }

    public int countBooks(String title, String author, String genre) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(DISTINCT b.id) FROM books b ");
        sql.append("LEFT JOIN book_authors ba ON b.id = ba.book_id ");
        sql.append("LEFT JOIN authors a ON ba.author_id = a.id ");
        sql.append("LEFT JOIN book_genres bg ON b.id = bg.book_id ");
        sql.append("LEFT JOIN genres g ON bg.genre_id = g.id ");
        sql.append("WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        
        if (title != null && !title.trim().isEmpty()) {
            sql.append("AND b.title ILIKE ? ");
            params.add("%" + title + "%");
        }
        if (author != null && !author.trim().isEmpty()) {
            sql.append("AND (a.first_name ILIKE ? OR a.last_name ILIKE ?) ");
            params.add("%" + author + "%");
            params.add("%" + author + "%");
        }
        if (genre != null && !genre.trim().isEmpty()) {
            sql.append("AND g.name = ? ");
            params.add(genre);
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public void addBookGenre(int bookId, int genreId) throws SQLException {
        String sql = "INSERT INTO book_genres (book_id, genre_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            stmt.setInt(2, genreId);
            stmt.executeUpdate();
        }
    }

    public void addBookAuthor(int bookId, int authorId, int order) throws SQLException {
        String sql = "INSERT INTO book_authors (book_id, author_id, author_order) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            stmt.setInt(2, authorId);
            stmt.setInt(3, order);
            stmt.executeUpdate();
        }
    }

    public void deleteBookGenres(int bookId) throws SQLException {
        String sql = "DELETE FROM book_genres WHERE book_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
        }
    }

    public void deleteBookAuthors(int bookId) throws SQLException {
        String sql = "DELETE FROM book_authors WHERE book_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
        }
    }

    public List<Genre> getGenresByBookId(int bookId) throws SQLException {
        String sql = "SELECT g.id, g.name, g.description FROM genres g JOIN book_genres bg ON g.id = bg.genre_id WHERE bg.book_id = ?";
        List<Genre> genres = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Genre g = new Genre();
                g.setId(rs.getInt("id"));
                g.setName(rs.getString("name"));
                g.setDescription(rs.getString("description"));
                genres.add(g);
            }
        }
        return genres;
    }

    public List<Author> getAuthorsByBookId(int bookId) throws SQLException {
        String sql = "SELECT a.id, a.first_name, a.last_name, a.birth_date, a.biography FROM authors a JOIN book_authors ba ON a.id = ba.author_id WHERE ba.book_id = ? ORDER BY ba.author_order";
        List<Author> authors = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Author a = new Author();
                a.setId(rs.getInt("id"));
                a.setFirstName(rs.getString("first_name"));
                a.setLastName(rs.getString("last_name"));
                a.setBirthDate(rs.getString("birth_date"));
                a.setBiography(rs.getString("biography"));
                authors.add(a);
            }
        }
        return authors;
    }

    public int getAvailableCopies(int bookId) throws SQLException {
        String sql = """
            SELECT (b.total_copies - (
                SELECT COUNT(*) 
                FROM loans l 
                WHERE l.book_id = b.id 
                AND l.status IN ('ACTIVE', 'OVERDUE')
            )) AS available
            FROM books b
            WHERE b.id = ?
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("available");
            }
        }
        return 0;
    }

    public void deleteBookGenre(int bookId, int genreId) throws SQLException {
        String sql = "DELETE FROM book_genres WHERE book_id = ? AND genre_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            stmt.setInt(2, genreId);
            stmt.executeUpdate();
        }
    }

    public void deleteBookAuthor(int bookId, int authorId) throws SQLException {
        String sql = "DELETE FROM book_authors WHERE book_id = ? AND author_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            stmt.setInt(2, authorId);
            stmt.executeUpdate();
        }
    }
}