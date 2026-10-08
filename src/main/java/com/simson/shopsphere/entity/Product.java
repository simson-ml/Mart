package com.simson.shopsphere.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, length = 280)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 100)
    private String brand;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "discount_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "review_count", nullable = false)
    private Integer reviewCount = 0;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Product() {}

    public Product(Long id, Category category, String name, String slug, String description, String brand,
                   BigDecimal price, BigDecimal discountPercentage, Integer stockQuantity, String sku,
                   String imageUrl, BigDecimal rating, Integer reviewCount, boolean active) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.brand = brand;
        this.price = price;
        this.discountPercentage = discountPercentage != null ? discountPercentage : BigDecimal.ZERO;
        this.stockQuantity = stockQuantity != null ? stockQuantity : 0;
        this.sku = sku;
        this.imageUrl = imageUrl;
        this.rating = rating != null ? rating : BigDecimal.ZERO;
        this.reviewCount = reviewCount != null ? reviewCount : 0;
        this.active = active;
    }

    public static ProductBuilder builder() {
        return new ProductBuilder();
    }

    public static class ProductBuilder {
        private Long id;
        private Category category;
        private String name;
        private String slug;
        private String description;
        private String brand;
        private BigDecimal price;
        private BigDecimal discountPercentage = BigDecimal.ZERO;
        private Integer stockQuantity = 0;
        private String sku;
        private String imageUrl;
        private BigDecimal rating = BigDecimal.ZERO;
        private Integer reviewCount = 0;
        private boolean active = true;

        public ProductBuilder id(Long id) { this.id = id; return this; }
        public ProductBuilder category(Category category) { this.category = category; return this; }
        public ProductBuilder name(String name) { this.name = name; return this; }
        public ProductBuilder slug(String slug) { this.slug = slug; return this; }
        public ProductBuilder description(String description) { this.description = description; return this; }
        public ProductBuilder brand(String brand) { this.brand = brand; return this; }
        public ProductBuilder price(BigDecimal price) { this.price = price; return this; }
        public ProductBuilder discountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; return this; }
        public ProductBuilder stockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; return this; }
        public ProductBuilder sku(String sku) { this.sku = sku; return this; }
        public ProductBuilder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
        public ProductBuilder rating(BigDecimal rating) { this.rating = rating; return this; }
        public ProductBuilder reviewCount(Integer reviewCount) { this.reviewCount = reviewCount; return this; }
        public ProductBuilder active(boolean active) { this.active = active; return this; }
        public Product build() {
            return new Product(id, category, name, slug, description, brand, price, discountPercentage, stockQuantity, sku, imageUrl, rating, reviewCount, active);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public BigDecimal getDiscountedPrice() {
        if (price == null) {
            return BigDecimal.ZERO;
        }
        if (discountPercentage == null || discountPercentage.compareTo(BigDecimal.ZERO) <= 0) {
            return price;
        }
        BigDecimal discountFactor = BigDecimal.valueOf(100).subtract(discountPercentage)
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        return price.multiply(discountFactor).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getSavingsAmount() {
        if (price == null) {
            return BigDecimal.ZERO;
        }
        return price.subtract(getDiscountedPrice()).setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isOutOfStock() {
        return stockQuantity == null || stockQuantity <= 0;
    }

    public boolean isLowStock() {
        return stockQuantity != null && stockQuantity > 0 && stockQuantity <= 5;
    }
}
