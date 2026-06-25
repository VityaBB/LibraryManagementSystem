package com.library.repository;

import com.library.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    
    @Query(value = "SELECT u.* FROM users u " +
           "WHERE (:firstName IS NULL OR CAST(u.first_name AS TEXT) ILIKE '%' || CAST(:firstName AS TEXT) || '%') " +
           "AND (:lastName IS NULL OR CAST(u.last_name AS TEXT) ILIKE '%' || CAST(:lastName AS TEXT) || '%') " +
           "AND (:phone IS NULL OR CAST(u.phone AS TEXT) ILIKE '%' || CAST(:phone AS TEXT) || '%')",
           countQuery = "SELECT COUNT(u.id) FROM users u " +
           "WHERE (:firstName IS NULL OR CAST(u.first_name AS TEXT) ILIKE '%' || CAST(:firstName AS TEXT) || '%') " +
           "AND (:lastName IS NULL OR CAST(u.last_name AS TEXT) ILIKE '%' || CAST(:lastName AS TEXT) || '%') " +
           "AND (:phone IS NULL OR CAST(u.phone AS TEXT) ILIKE '%' || CAST(:phone AS TEXT) || '%')",
           nativeQuery = true)
    Page<User> searchUsers(
        @Param("firstName") String firstName,
        @Param("lastName") String lastName,
        @Param("phone") String phone,
        Pageable pageable
    );
}