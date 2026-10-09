package com.simson.shopsphere.service;

import com.simson.shopsphere.dto.ProductDto;
import com.simson.shopsphere.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    Page<Product> getFilteredProducts(
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
    );

    List<Product> getFeaturedProducts();
    List<Product> getTrendingProducts();
    List<Product> getBestSellers();
    List<Product> getFlashDeals();
    List<Product> getNewArrivals();
    List<Product> getRelatedProducts(Product product);
    List<Product> getSmartRecommendations(Product product);
    List<Product> searchAutocomplete(String query, int limit);
    List<String> getAllBrands();

    Product getProductById(Long id);
    Product getProductBySlug(String slug);

    // Admin
    Product createProduct(ProductDto dto, String adminEmail);
    Product updateProduct(Long id, ProductDto dto, String adminEmail);
    void updateStock(Long id, int newQuantity, String adminEmail);
    void toggleProductStatus(Long id, String adminEmail);
    List<Product> getLowStockProducts(int threshold);
    long getTotalProductCount();
    long getLowStockCount(int threshold);
}
