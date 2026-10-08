package com.simson.shopsphere.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;

public class ProductDto {
    private Long id;

    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Name must not exceed 200 characters")
    private String name;

    private String slug;

    @NotBlank(message = "Product description is required")
    private String description;

    @NotBlank(message = "Brand is required")
    @Size(max = 100, message = "Brand must not exceed 100 characters")
    private String brand;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    private BigDecimal discountPercentage = BigDecimal.ZERO;
    private Integer stockQuantity = 0;

    private String sku;
    private String imageUrl;

    @NotNull(message = "Category is required")
    private Long categoryId;

    private String categoryName;
    private boolean active = true;
    private boolean featured;
    private BigDecimal sellingPrice;
    private BigDecimal rating = BigDecimal.ZERO;
    private Integer reviewCount = 0;
    private MultipartFile imageFile;

    public ProductDto() {}

    public ProductDto(Long id, String name, String slug, String description, String brand, BigDecimal price, BigDecimal discountPercentage, Integer stockQuantity, String sku, String imageUrl, Long categoryId, String categoryName, boolean active, boolean featured, BigDecimal sellingPrice, BigDecimal rating, Integer reviewCount, MultipartFile imageFile) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.brand = brand;
        this.price = price;
        this.discountPercentage = discountPercentage;
        this.stockQuantity = stockQuantity;
        this.sku = sku;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.active = active;
        this.featured = featured;
        this.sellingPrice = sellingPrice;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.imageFile = imageFile;
    }

    public static ProductDtoBuilder builder() {
        return new ProductDtoBuilder();
    }

    public static class ProductDtoBuilder {
        private Long id;
        private String name;
        private String slug;
        private String description;
        private String brand;
        private BigDecimal price;
        private BigDecimal discountPercentage = BigDecimal.ZERO;
        private Integer stockQuantity = 0;
        private String sku;
        private String imageUrl;
        private Long categoryId;
        private String categoryName;
        private boolean active = true;
        private boolean featured;
        private BigDecimal sellingPrice;
        private BigDecimal rating = BigDecimal.ZERO;
        private Integer reviewCount = 0;
        private MultipartFile imageFile;

        public ProductDtoBuilder id(Long id) { this.id = id; return this; }
        public ProductDtoBuilder name(String name) { this.name = name; return this; }
        public ProductDtoBuilder slug(String slug) { this.slug = slug; return this; }
        public ProductDtoBuilder description(String description) { this.description = description; return this; }
        public ProductDtoBuilder brand(String brand) { this.brand = brand; return this; }
        public ProductDtoBuilder price(BigDecimal price) { this.price = price; return this; }
        public ProductDtoBuilder discountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; return this; }
        public ProductDtoBuilder stockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; return this; }
        public ProductDtoBuilder sku(String sku) { this.sku = sku; return this; }
        public ProductDtoBuilder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
        public ProductDtoBuilder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }
        public ProductDtoBuilder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
        public ProductDtoBuilder active(boolean active) { this.active = active; return this; }
        public ProductDtoBuilder featured(boolean featured) { this.featured = featured; return this; }
        public ProductDtoBuilder sellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; return this; }
        public ProductDtoBuilder rating(BigDecimal rating) { this.rating = rating; return this; }
        public ProductDtoBuilder reviewCount(Integer reviewCount) { this.reviewCount = reviewCount; return this; }
        public ProductDtoBuilder imageFile(MultipartFile imageFile) { this.imageFile = imageFile; return this; }

        public ProductDto build() {
            return new ProductDto(id, name, slug, description, brand, price, discountPercentage, stockQuantity, sku, imageUrl, categoryId, categoryName, active, featured, sellingPrice, rating, reviewCount, imageFile);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }

    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }

    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }

    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }

    public MultipartFile getImageFile() { return imageFile; }
    public void setImageFile(MultipartFile imageFile) { this.imageFile = imageFile; }
}
