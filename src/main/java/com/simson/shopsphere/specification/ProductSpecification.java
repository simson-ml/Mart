package com.simson.shopsphere.specification;

import com.simson.shopsphere.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> filter(
            String keyword,
            String categorySlug,
            String brand,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            BigDecimal minRating,
            Boolean inStockOnly,
            BigDecimal minDiscount,
            Boolean activeOnly
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Active filter
            if (activeOnly == null || activeOnly) {
                predicates.add(criteriaBuilder.isTrue(root.get("active")));
            }

            // Multi-token Smart Search across Name, Brand, Description, SKU, Category Name
            if (StringUtils.hasText(keyword)) {
                String[] tokens = keyword.trim().toLowerCase().split("\\s+");
                List<Predicate> tokenPredicates = new ArrayList<>();
                for (String token : tokens) {
                    if (!token.isBlank()) {
                        String searchPattern = "%" + token + "%";
                        Predicate nameMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchPattern);
                        Predicate brandMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("brand")), searchPattern);
                        Predicate descMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchPattern);
                        Predicate skuMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("sku")), searchPattern);
                        Predicate catMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("category").get("name")), searchPattern);

                        tokenPredicates.add(criteriaBuilder.or(nameMatch, brandMatch, descMatch, skuMatch, catMatch));
                    }
                }
                if (!tokenPredicates.isEmpty()) {
                    predicates.add(criteriaBuilder.and(tokenPredicates.toArray(new Predicate[0])));
                }
            }

            // Category filter
            if (StringUtils.hasText(categorySlug)) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("category").get("slug")),
                        categorySlug.trim().toLowerCase()
                ));
            }

            // Brand filter
            if (StringUtils.hasText(brand)) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("brand")),
                        brand.trim().toLowerCase()
                ));
            }

            // Price range filter
            if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) >= 0) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) > 0) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            // Rating filter
            if (minRating != null && minRating.compareTo(BigDecimal.ZERO) > 0) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), minRating));
            }

            // Stock availability filter
            if (inStockOnly != null && inStockOnly) {
                predicates.add(criteriaBuilder.greaterThan(root.get("stockQuantity"), 0));
            }

            // Discount filter
            if (minDiscount != null && minDiscount.compareTo(BigDecimal.ZERO) > 0) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("discountPercentage"), minDiscount));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
