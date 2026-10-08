package com.simson.shopsphere.repository;

import com.simson.shopsphere.entity.Role;
import com.simson.shopsphere.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmail(String email);
    boolean existsByEmailIgnoreCase(String email);
    long countByRole(Role role);

    @Query("SELECT u FROM User u WHERE " +
           "(:keyword IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);
}
