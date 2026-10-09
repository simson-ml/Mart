package com.simson.shopsphere.service.impl;

import com.simson.shopsphere.dto.ProductDto;
import com.simson.shopsphere.entity.Category;
import com.simson.shopsphere.entity.Product;
import com.simson.shopsphere.exception.BadRequestException;
import com.simson.shopsphere.exception.DuplicateResourceException;
import com.simson.shopsphere.exception.ResourceNotFoundException;
import com.simson.shopsphere.repository.CategoryRepository;
import com.simson.shopsphere.repository.ProductRepository;
import com.simson.shopsphere.service.AuditLogService;
import com.simson.shopsphere.service.FileStorageService;
import com.simson.shopsphere.service.ProductService;
import com.simson.shopsphere.specification.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProductServiceImpl.class);

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, FileStorageService fileStorageService, AuditLogService auditLogService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.fileStorageService = fileStorageService;
        this.auditLogService = auditLogService;
    }


    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;
    private final AuditLogService auditLogService;

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");
    }

    private Sort resolveSort(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        return switch (sortBy.toLowerCase()) {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "rating" -> Sort.by(Sort.Direction.DESC, "rating");
            case "discount" -> Sort.by(Sort.Direction.DESC, "discountPercentage");
            case "popular" -> Sort.by(Sort.Direction.DESC, "reviewCount");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getFilteredProducts(
            String keyword,
            String categorySlug,
            String brand,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            BigDecimal minRating,
            Boolean inStockOnly,
            BigDecimal minDiscount,
            String sortBy,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), resolveSort(sortBy));
        Specification<Product> spec = ProductSpecification.filter(
                keyword, categorySlug, brand, minPrice, maxPrice, minRating, inStockOnly, minDiscount, true
        );
        return productRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getFeaturedProducts() {
        return productRepository.findTop8ByActiveTrueOrderByRatingDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getTrendingProducts() {
        return productRepository.findTop8ByActiveTrueOrderByReviewCountDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getBestSellers() {
        return productRepository.findTop8ByActiveTrueOrderByReviewCountDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getFlashDeals() {
        return productRepository.findTop12ByActiveTrueAndDiscountPercentageGreaterThanEqualOrderByDiscountPercentageDesc(BigDecimal.valueOf(10));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getNewArrivals() {
        return productRepository.findTop8ByActiveTrueOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getRelatedProducts(Product product) {
        return productRepository.findTop4ByCategoryAndActiveTrueAndIdNotOrderByRatingDesc(product.getCategory(), product.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getSmartRecommendations(Product product) {
        if (product == null) return java.util.Collections.emptyList();
        BigDecimal minPrice = product.getPrice().multiply(new BigDecimal("0.60"));
        BigDecimal maxPrice = product.getPrice().multiply(new BigDecimal("1.40"));
        return productRepository.findSmartRecommendations(
                product.getCategory(),
                product.getId(),
                minPrice,
                maxPrice,
                org.springframework.data.domain.PageRequest.of(0, 4)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> searchAutocomplete(String query, int limit) {
        if (query == null || query.trim().length() < 2) {
            return List.of();
        }
        int maxResults = Math.min(Math.max(limit, 1), 20);
        return productRepository.findAutocompleteSuggestions(query.trim(), org.springframework.data.domain.PageRequest.of(0, maxResults));
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllBrands() {
        return productRepository.findDistinctBrands();
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductBySlug(String slug) {
        return productRepository.findBySlug(slug.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with slug: " + slug));
    }

    @Override
    @Transactional
    public Product createProduct(ProductDto dto, String adminEmail) {
        String sku = dto.getSku().trim().toUpperCase();
        if (productRepository.existsBySku(sku)) {
            throw new DuplicateResourceException("Product with SKU '" + sku + "' already exists.");
        }

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));

        String slug = generateSlug(dto.getName());
        if (productRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        String imageUrl = dto.getImageUrl();
        if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
            imageUrl = fileStorageService.storeFile(dto.getImageFile(), "products");
        }
        if (imageUrl == null || imageUrl.isBlank()) {
            imageUrl = fileStorageService.getDefaultProductImage();
        }

        Product product = Product.builder()
                .name(dto.getName().trim())
                .slug(slug)
                .description(dto.getDescription())
                .brand(dto.getBrand().trim())
                .category(category)
                .price(dto.getPrice())
                .discountPercentage(dto.getDiscountPercentage() != null ? dto.getDiscountPercentage() : BigDecimal.ZERO)
                .stockQuantity(dto.getStockQuantity() != null ? dto.getStockQuantity() : 0)
                .sku(sku)
                .imageUrl(imageUrl)
                .rating(BigDecimal.ZERO)
                .reviewCount(0)
                .active(dto.isActive())
                .build();

        Product saved = productRepository.save(product);
        auditLogService.log(adminEmail, "CREATE_PRODUCT", "Product", String.valueOf(saved.getId()),
                "Created product: " + saved.getName() + " (SKU: " + saved.getSku() + ")", "127.0.0.1");

        return saved;
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, ProductDto dto, String adminEmail) {
        Product product = getProductById(id);

        String sku = dto.getSku().trim().toUpperCase();
        if (!product.getSku().equalsIgnoreCase(sku) && productRepository.existsBySku(sku)) {
            throw new DuplicateResourceException("Product with SKU '" + sku + "' already exists.");
        }

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));

        product.setName(dto.getName().trim());
        product.setDescription(dto.getDescription());
        product.setBrand(dto.getBrand().trim());
        product.setCategory(category);
        product.setPrice(dto.getPrice());
        product.setDiscountPercentage(dto.getDiscountPercentage() != null ? dto.getDiscountPercentage() : BigDecimal.ZERO);
        product.setStockQuantity(dto.getStockQuantity() != null ? dto.getStockQuantity() : 0);
        product.setSku(sku);
        product.setActive(dto.isActive());

        if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
            String newImage = fileStorageService.storeFile(dto.getImageFile(), "products");
            if (newImage != null) {
                product.setImageUrl(newImage);
            }
        } else if (dto.getImageUrl() != null && !dto.getImageUrl().isBlank()) {
            product.setImageUrl(dto.getImageUrl().trim());
        }

        Product updated = productRepository.save(product);
        auditLogService.log(adminEmail, "UPDATE_PRODUCT", "Product", String.valueOf(updated.getId()),
                "Updated product: " + updated.getName(), "127.0.0.1");

        return updated;
    }

    @Override
    @Transactional
    public void updateStock(Long id, int newQuantity, String adminEmail) {
        if (newQuantity < 0) {
            throw new BadRequestException("Stock quantity cannot be negative.");
        }
        Product product = getProductById(id);
        int oldStock = product.getStockQuantity();
        product.setStockQuantity(newQuantity);
        productRepository.save(product);

        auditLogService.log(adminEmail, "UPDATE_STOCK", "Product", String.valueOf(product.getId()),
                "Stock changed for " + product.getName() + " from " + oldStock + " to " + newQuantity, "127.0.0.1");
    }

    @Override
    @Transactional
    public void toggleProductStatus(Long id, String adminEmail) {
        Product product = getProductById(id);
        product.setActive(!product.isActive());
        productRepository.save(product);

        auditLogService.log(adminEmail, "TOGGLE_PRODUCT_STATUS", "Product", String.valueOf(product.getId()),
                "Product " + product.getName() + " active=" + product.isActive(), "127.0.0.1");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getLowStockProducts(int threshold) {
        Specification<Product> spec = (root, query, cb) ->
                cb.and(cb.isTrue(root.get("active")), cb.lessThanOrEqualTo(root.get("stockQuantity"), threshold));
        return productRepository.findAll(spec, PageRequest.of(0, 10, Sort.by("stockQuantity").ascending())).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalProductCount() {
        return productRepository.countByActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public long getLowStockCount(int threshold) {
        return productRepository.countByStockQuantityLessThanEqual(threshold);
    }
}
