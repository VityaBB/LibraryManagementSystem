package com.library.dao;

import com.library.models.Author;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthorDAO {
    private final Connection connection;

    public AuthorDAO(Connection connection) {
        this.connection = connection;
    }

    public void addAuthor(Author author) throws SQLException {
        String sql = "INSERT INTO authors (first_name, last_name, birth_date, biography) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, author.getFirstName());
            stmt.setString(2, author.getLastName());
            String birthDateStr = author.getBirthDate();
            if (birthDateStr != null && !birthDateStr.trim().isEmpty()) {
                stmt.setDate(3, java.sql.Date.valueOf(birthDateStr));
            } else {
                stmt.setNull(3, Types.DATE);
            }
            stmt.setString(4, author.getBiography());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                author.setId(rs.getInt(1));
            }
        }
    }

    public List<Author> getAllAuthors() throws SQLException {
        String sql = "SELECT id, first_name, last_name, birth_date, biography FROM authors ORDER BY last_name, first_name";
        List<Author> authors = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                authors.add(mapResultSetToAuthor(rs));
            }
        }
        return authors;
    }

    public Author getAuthorById(int id) throws SQLException {
        String sql = "SELECT id, first_name, last_name, birth_date, biography FROM authors WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToAuthor(rs);
            }
        }
        return null;
    }

    public void updateAuthor(Author author) throws SQLException {
        String sql = "UPDATE authors SET first_name = ?, last_name = ?, birth_date = ?, biography = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, author.getFirstName());
            stmt.setString(2, author.getLastName());
            String birthDateStr = author.getBirthDate();
            if (birthDateStr != null && !birthDateStr.trim().isEmpty()) {
                stmt.setDate(3, java.sql.Date.valueOf(birthDateStr));
            } else {
                stmt.setNull(3, Types.DATE);
            }
            stmt.setString(4, author.getBiography());
            stmt.setInt(5, author.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteAuthor(int id) throws SQLException {
        String sql = "DELETE FROM authors WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Author> getAuthorsByBookId(int bookId) throws SQLException {
        String sql = "SELECT a.id, a.first_name, a.last_name, a.birth_date, a.biography FROM authors a JOIN book_authors ba ON a.id = ba.author_id WHERE ba.book_id = ? ORDER BY ba.author_order";
        List<Author> authors = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                authors.add(mapResultSetToAuthor(rs));
            }
        }
        return authors;
    }

    private Author mapResultSetToAuthor(ResultSet rs) throws SQLException {
        Author author = new Author();
        author.setId(rs.getInt("id"));
        author.setFirstName(rs.getString("first_name"));
        author.setLastName(rs.getString("last_name"));
        author.setBirthDate(rs.getString("birth_date"));
        author.setBiography(rs.getString("biography"));
        return author;
    }
}