package com.library.dao;

import com.library.DatabaseConnection;
import com.library.models.Book;
import com.library.models.Loan;
import com.library.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO {
    
    public void addLoan(Loan loan) throws SQLException {
        String sql = "INSERT INTO loans (book_id, user_id, loan_date, due_date, status) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, loan.getBookId());
            stmt.setInt(2, loan.getUserId());
            
            if (loan.getLoanDate() != null) {
                stmt.setDate(3, java.sql.Date.valueOf(loan.getLoanDate()));
            } else {
                stmt.setNull(3, java.sql.Types.DATE);
            }
            
            if (loan.getDueDate() != null) {
                stmt.setDate(4, java.sql.Date.valueOf(loan.getDueDate()));
            } else {
                stmt.setNull(4, java.sql.Types.DATE);
            }
            
            stmt.setString(5, loan.getStatus());
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                loan.setId(rs.getInt(1));
            }
        }
    }

    public List<Loan> getAllLoans() throws SQLException {
        String sql = "SELECT id, book_id, user_id, loan_date, due_date, return_date, status, fine_amount FROM loans ORDER BY loan_date DESC";
        List<Loan> loans = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        }
        return loans;
    }

    public List<Loan> getActiveLoans() throws SQLException {
        String sql = "SELECT id, book_id, user_id, loan_date, due_date, return_date, status, fine_amount FROM loans WHERE status = 'ACTIVE' ORDER BY due_date";
        List<Loan> loans = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        }
        return loans;
    }

    public List<Loan> getLoansByUserId(int userId) throws SQLException {
        String sql = "SELECT id, book_id, user_id, loan_date, due_date, return_date, status, fine_amount FROM loans WHERE user_id = ? ORDER BY loan_date DESC";
        List<Loan> loans = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        }
        return loans;
    }

    public List<Loan> getLoansByBookId(int bookId) throws SQLException {
        String sql = "SELECT id, book_id, user_id, loan_date, due_date, return_date, status, fine_amount FROM loans WHERE book_id = ? ORDER BY loan_date DESC";
        List<Loan> loans = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        }
        return loans;
    }

    public void returnBook(int loanId, double fineAmount) throws SQLException {
        String sql = "UPDATE loans SET status = 'RETURNED', return_date = CURRENT_DATE, fine_amount = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, fineAmount);
            stmt.setInt(2, loanId);
            stmt.executeUpdate();
        }
    }

    public void updateLoan(Loan loan) throws SQLException {
        String sql = "UPDATE loans SET book_id = ?, user_id = ?, loan_date = ?, due_date = ?, " +
                     "return_date = ?, status = ?, fine_amount = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, loan.getBookId());
            stmt.setInt(2, loan.getUserId());
            stmt.setString(3, loan.getLoanDate());
            stmt.setString(4, loan.getDueDate());
            stmt.setString(5, loan.getReturnDate());
            stmt.setString(6, loan.getStatus());
            stmt.setDouble(7, loan.getFineAmount());
            stmt.setInt(8, loan.getId());
            
            stmt.executeUpdate();
        }
    }

    public void deleteLoan(int id) throws SQLException {
        String sql = "DELETE FROM loans WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Loan mapResultSetToLoan(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setId(rs.getInt("id"));
        loan.setBookId(rs.getInt("book_id"));
        loan.setUserId(rs.getInt("user_id"));
        loan.setLoanDate(rs.getString("loan_date"));
        loan.setDueDate(rs.getString("due_date"));
        loan.setReturnDate(rs.getString("return_date"));
        loan.setStatus(rs.getString("status"));
        loan.setFineAmount(rs.getDouble("fine_amount"));
        return loan;
    }

    public Loan getLoanById(int id) throws SQLException {
        String sql = "SELECT id, book_id, user_id, loan_date, due_date, return_date, status, fine_amount FROM loans WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToLoan(rs);
            }
        }
        return null;
    }

    public List<Loan> getActiveLoansByUserAndBook(int userId, int bookId) throws SQLException {
        String sql = "SELECT id, book_id, user_id, loan_date, due_date, return_date, status, fine_amount FROM loans WHERE user_id = ? AND book_id = ? AND status IN ('ACTIVE', 'OVERDUE')";
        List<Loan> loans = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        }
        return loans;
    }

    public List<User> getUsersByBookId(int bookId) throws SQLException {
        String sql = "SELECT u.id, u.email, u.password_hash, u.first_name, u.last_name, u.phone, u.address, u.registration_date, u.is_active, u.role FROM users u " +
                    "JOIN loans l ON u.id = l.user_id " +
                    "WHERE l.book_id = ? AND l.status IN ('ACTIVE', 'OVERDUE')";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setRole(rs.getString("role"));
                user.setActive(rs.getBoolean("is_active"));
                users.add(user);
            }
        }
        return users;
    }

    public List<Book> getActiveBooksByUserId(int userId) throws SQLException {
        String sql = "SELECT b.id, b.title, b.isbn, b.publication_year, b.publisher_id, b.total_copies, b.page_count, b.description FROM books b " +
                    "JOIN loans l ON b.id = l.book_id " +
                    "WHERE l.user_id = ? AND l.status IN ('ACTIVE', 'OVERDUE')";
        List<Book> books = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                book.setIsbn(rs.getString("isbn"));
                book.setPublicationYear(rs.getInt("publication_year"));
                book.setPublisherId(rs.getInt("publisher_id"));
                book.setTotalCopies(rs.getInt("total_copies"));
                book.setPageCount(rs.getInt("page_count"));
                book.setDescription(rs.getString("description"));
                books.add(book);
            }
        }
        return books;
    }
}