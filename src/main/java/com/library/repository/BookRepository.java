package com.library.repository;

import com.library.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("SELECT DISTINCT b FROM Book b " +
           "LEFT JOIN b.authors a " +
           "LEFT JOIN b.genres g " +
           "WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
           "AND (:authorId IS NULL OR a.id = :authorId) " +
           "AND (:genreId IS NULL OR g.id = :genreId) " +
           "AND (:publicationYear IS NULL OR b.publicationYear = :publicationYear)")
    Page<Book> searchBooks(
        @Param("title") String title,
        @Param("authorId") Long authorId,
        @Param("genreId") Long genreId,
        @Param("publicationYear") Integer publicationYear,
        Pageable pageable
    );
}