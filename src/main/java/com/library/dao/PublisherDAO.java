package com.library.dao;

import com.library.models.Publisher;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PublisherDAO {
    private final Connection connection;

    public PublisherDAO(Connection connection) {
        this.connection = connection;
    }

    public void addPublisher(Publisher publisher) throws SQLException {
        String sql = "INSERT INTO publishers (name, address, phone, email, website) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, publisher.getName());
            stmt.setString(2, publisher.getAddress());
            stmt.setString(3, publisher.getPhone());
            stmt.setString(4, publisher.getEmail());
            stmt.setString(5, publisher.getWebsite());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                publisher.setId(rs.getInt(1));
            }
        }
    }

    public List<Publisher> getAllPublishers() throws SQLException {
        String sql = "SELECT id, name, address, phone, email, website FROM publishers ORDER BY name";
        List<Publisher> publishers = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                publishers.add(mapResultSetToPublisher(rs));
            }
        }
        return publishers;
    }

    public Publisher getPublisherById(int id) throws SQLException {
        String sql = "SELECT id, name, address, phone, email, website FROM publishers WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToPublisher(rs);
            }
        }
        return null;
    }

    public Publisher getPublisherByName(String name) throws SQLException {
        String sql = "SELECT id, name, address, phone, email, website FROM publishers WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToPublisher(rs);
            }
        }
        return null;
    }

    public void updatePublisher(Publisher publisher) throws SQLException {
        String sql = "UPDATE publishers SET name = ?, address = ?, phone = ?, email = ?, website = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, publisher.getName());
            stmt.setString(2, publisher.getAddress());
            stmt.setString(3, publisher.getPhone());
            stmt.setString(4, publisher.getEmail());
            stmt.setString(5, publisher.getWebsite());
            stmt.setInt(6, publisher.getId());
            stmt.executeUpdate();
        }
    }

    public void deletePublisher(int id) throws SQLException {
        String sql = "DELETE FROM publishers WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Publisher mapResultSetToPublisher(ResultSet rs) throws SQLException {
        Publisher publisher = new Publisher();
        publisher.setId(rs.getInt("id"));
        publisher.setName(rs.getString("name"));
        publisher.setAddress(rs.getString("address"));
        publisher.setPhone(rs.getString("phone"));
        publisher.setEmail(rs.getString("email"));
        publisher.setWebsite(rs.getString("website"));
        return publisher;
    }
}