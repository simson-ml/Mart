package com.simson.shopsphere.controller.api;

import com.simson.shopsphere.dto.ApiResponse;
import com.simson.shopsphere.entity.Product;
import com.simson.shopsphere.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products API", description = "Endpoints for product search, catalog filtering, and product details")
public class ApiProductController {

    public ApiProductController(ProductService productService) {
        this.productService = productService;
    }


    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Search and filter products with dynamic criteria")
    public ResponseEntity<ApiResponse<Page<Product>>> getProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(required = false) Boolean inStockOnly,
            @RequestParam(required = false) BigDecimal minDiscount,
            @RequestParam(required = false, defaultValue = "newest") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        Page<Product> products = productService.getFilteredProducts(
                keyword, category, brand, minPrice, maxPrice, minRating, inStockOnly, minDiscount, sortBy, page, size
        );
        return ResponseEntity.ok(ApiResponse.ok("Products retrieved successfully", products));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed product by ID")
    public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.ok("Product found", product));
    }

    @GetMapping("/autocomplete")
    @Operation(summary = "Get live autocomplete search suggestions for products")
    public ResponseEntity<ApiResponse<List<java.util.Map<String, Object>>>> autocomplete(
            @RequestParam("q") String query,
            @RequestParam(value = "limit", defaultValue = "6") int limit
    ) {
        List<Product> matches = productService.searchAutocomplete(query, limit);
        List<java.util.Map<String, Object>> suggestions = matches.stream().map(p -> {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", p.getId());
            map.put("name", p.getName());
            map.put("slug", p.getSlug());
            map.put("brand", p.getBrand());
            map.put("imageUrl", p.getImageUrl());
            map.put("price", p.getPrice());
            map.put("discountedPrice", p.getDiscountedPrice());
            map.put("discountPercentage", p.getDiscountPercentage());
            map.put("categoryName", p.getCategory() != null ? p.getCategory().getName() : "");
            return map;
        }).toList();
        return ResponseEntity.ok(ApiResponse.ok("Autocomplete suggestions", suggestions));
    }

    @GetMapping("/brands")
    @Operation(summary = "Get list of all available product brands")
    public ResponseEntity<ApiResponse<List<String>>> getBrands() {
        return ResponseEntity.ok(ApiResponse.ok("Brands retrieved", productService.getAllBrands()));
    }
}
