package com.library.repository;

import com.library.model.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    @Query(value = "SELECT g.* FROM genres g " +
           "WHERE (:name IS NULL OR CAST(g.name AS TEXT) ILIKE '%' || CAST(:name AS TEXT) || '%')",
           countQuery = "SELECT COUNT(g.id) FROM genres g " +
           "WHERE (:name IS NULL OR CAST(g.name AS TEXT) ILIKE '%' || CAST(:name AS TEXT) || '%')",
           nativeQuery = true)
    Page<Genre> searchGenres(
        @Param("name") String name,
        Pageable pageable
    );
}