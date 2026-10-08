package com.simson.shopsphere.repository;

import com.simson.shopsphere.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findBySlug(String slug);
    Optional<Category> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
    boolean existsBySlug(String slug);
    List<Category> findByActiveTrueOrderByNameAsc();
}
