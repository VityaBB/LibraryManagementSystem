package com.library.repository;

import com.library.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserIdAndStatusIn(Long userId, List<String> statuses);
    
    long countByBookIdAndStatusIn(Long bookId, List<String> statuses);
    
    @Query(value = "SELECT l.* FROM loans l " +
           "LEFT JOIN books b ON l.book_id = b.id " +
           "LEFT JOIN users u ON l.user_id = u.id " +
           "WHERE (:bookTitle IS NULL OR CAST(b.title AS TEXT) ILIKE CONCAT('%', CAST(:bookTitle AS TEXT), '%')) " +
           "AND (:userName IS NULL OR CAST(CONCAT(u.first_name, ' ', u.last_name) AS TEXT) ILIKE CONCAT('%', CAST(:userName AS TEXT), '%'))",
           countQuery = "SELECT COUNT(l.id) FROM loans l " +
           "LEFT JOIN books b ON l.book_id = b.id " +
           "LEFT JOIN users u ON l.user_id = u.id " +
           "WHERE (:bookTitle IS NULL OR CAST(b.title AS TEXT) ILIKE CONCAT('%', CAST(:bookTitle AS TEXT), '%')) " +
           "AND (:userName IS NULL OR CAST(CONCAT(u.first_name, ' ', u.last_name) AS TEXT) ILIKE CONCAT('%', CAST(:userName AS TEXT), '%'))",
           nativeQuery = true)
    Page<Loan> searchLoans(
        @Param("bookTitle") String bookTitle,
        @Param("userName") String userName,
        Pageable pageable
    );
}