package com.simson.shopsphere.repository;

import com.simson.shopsphere.entity.Category;
import com.simson.shopsphere.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @EntityGraph(attributePaths = {"category"})
    Optional<Product> findBySlug(String slug);

    @EntityGraph(attributePaths = {"category"})
    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);
    boolean existsBySlug(String slug);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithLock(@Param("id") Long id);

    @EntityGraph(attributePaths = {"category"})
    List<Product> findTop8ByActiveTrueOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"category"})
    List<Product> findTop8ByActiveTrueOrderByRatingDesc();

    @EntityGraph(attributePaths = {"category"})
    List<Product> findTop8ByActiveTrueOrderByReviewCountDesc();

    @EntityGraph(attributePaths = {"category"})
    List<Product> findTop8ByActiveTrueAndDiscountPercentageGreaterThanOrderByDiscountPercentageDesc(BigDecimal minDiscount);

    @EntityGraph(attributePaths = {"category"})
    List<Product> findTop12ByActiveTrueAndDiscountPercentageGreaterThanEqualOrderByDiscountPercentageDesc(BigDecimal minDiscount);

    @EntityGraph(attributePaths = {"category"})
    List<Product> findTop4ByCategoryAndActiveTrueAndIdNotOrderByRatingDesc(Category category, Long id);

    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.active = true AND p.id <> :id AND p.price BETWEEN :minPrice AND :maxPrice ORDER BY p.rating DESC")
    List<Product> findSmartRecommendations(@Param("category") Category category, @Param("id") Long id, @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

    long countByStockQuantityLessThanEqual(Integer threshold);
    long countByActiveTrue();

    @EntityGraph(attributePaths = {"category"})
    @Query("SELECT p FROM Product p WHERE p.active = true AND (" +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.category.name) LIKE LOWER(CONCAT('%', :query, '%'))" +
           ") ORDER BY p.rating DESC, p.reviewCount DESC")
    List<Product> findAutocompleteSuggestions(@Param("query") String query, Pageable pageable);

    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.active = true ORDER BY p.brand ASC")
    List<String> findDistinctBrands();
}
