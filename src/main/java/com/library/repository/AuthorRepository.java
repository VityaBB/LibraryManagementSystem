package com.library.repository;

import com.library.model.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    @Query(value = "SELECT a.* FROM authors a " +
           "WHERE (:firstName IS NULL OR CAST(a.first_name AS TEXT) ILIKE CONCAT('%', CAST(:firstName AS TEXT), '%')) " +
           "AND (:lastName IS NULL OR CAST(a.last_name AS TEXT) ILIKE CONCAT('%', CAST(:lastName AS TEXT), '%'))",
           countQuery = "SELECT COUNT(a.id) FROM authors a " +
           "WHERE (:firstName IS NULL OR CAST(a.first_name AS TEXT) ILIKE CONCAT('%', CAST(:firstName AS TEXT), '%')) " +
           "AND (:lastName IS NULL OR CAST(a.last_name AS TEXT) ILIKE CONCAT('%', CAST(:lastName AS TEXT), '%'))",
           nativeQuery = true)
    Page<Author> searchAuthors(
        @Param("firstName") String firstName,
        @Param("lastName") String lastName,
        Pageable pageable
    );
}