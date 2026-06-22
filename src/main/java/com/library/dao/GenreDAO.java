package com.library.dao;

import com.library.models.Genre;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GenreDAO {
    private final Connection connection;

    public GenreDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Genre> getAllGenres() throws SQLException {
        String sql = "SELECT id, name, description FROM genres ORDER BY name";
        List<Genre> genres = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
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

    public Genre getGenreById(int id) throws SQLException {
        String sql = "SELECT id, name, description FROM genres WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Genre g = new Genre();
                g.setId(rs.getInt("id"));
                g.setName(rs.getString("name"));
                g.setDescription(rs.getString("description"));
                return g;
            }
        }
        return null;
    }

    public List<Genre> getGenresByBookId(int bookId) throws SQLException {
        String sql = "SELECT g.id, g.name, g.description FROM genres g JOIN book_genres bg ON g.id = bg.genre_id WHERE bg.book_id = ?";
        List<Genre> genres = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
}