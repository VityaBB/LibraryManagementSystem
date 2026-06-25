package com.library.repository;

import com.library.model.Publisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {
    @Query(value = "SELECT p.* FROM publishers p " +
           "WHERE (:name IS NULL OR CAST(p.name AS TEXT) ILIKE '%' || CAST(:name AS TEXT) || '%')",
           countQuery = "SELECT COUNT(p.id) FROM publishers p " +
           "WHERE (:name IS NULL OR CAST(p.name AS TEXT) ILIKE '%' || CAST(:name AS TEXT) || '%')",
           nativeQuery = true)
    Page<Publisher> searchPublishers(
        @Param("name") String name,
        Pageable pageable
    );
}