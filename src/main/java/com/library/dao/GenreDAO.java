package com.library.dao;

import com.library.DatabaseConnection;
import com.library.models.Genre;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GenreDAO {

    public List<Genre> getAllGenres() throws SQLException {
        String sql = "SELECT * FROM genres ORDER BY name";
        List<Genre> genres = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
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
        String sql = "SELECT * FROM genres WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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
}