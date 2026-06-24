package com.library.repository;

import com.library.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("SELECT b FROM Book b WHERE (:publicationYear IS NULL OR b.publicationYear = :publicationYear)")
    Page<Book> findByPublicationYear(@Param("publicationYear") Integer publicationYear, Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<Book> findByTitleContainingIgnoreCase(@Param("title") String title, Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')) AND b.publicationYear = :publicationYear")
    Page<Book> findByTitleContainingIgnoreCaseAndPublicationYear(@Param("title") String title, @Param("publicationYear") Integer publicationYear, Pageable pageable);
}