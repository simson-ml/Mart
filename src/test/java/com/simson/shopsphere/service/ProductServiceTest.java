package com.simson.shopsphere.service;

import com.simson.shopsphere.dto.ProductDto;
import com.simson.shopsphere.entity.Category;
import com.simson.shopsphere.entity.Product;
import com.simson.shopsphere.repository.CategoryRepository;
import com.simson.shopsphere.repository.ProductRepository;
import com.simson.shopsphere.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private ProductServiceImpl productService;

    private Category category;
    private Product product;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Electronics")
                .slug("electronics")
                .active(true)
                .build();

        product = Product.builder()
                .id(1L)
                .name("Sony Headphones")
                .slug("sony-headphones")
                .brand("Sony")
                .price(BigDecimal.valueOf(10000.00))
                .discountPercentage(BigDecimal.valueOf(20.00))
                .stockQuantity(15)
                .sku("ELE-SNY-WH-001")
                .category(category)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Discounted price should be calculated accurately on backend")
    void testDiscountCalculation() {
        // Original: 10,000, Discount: 20% -> Expected Discounted: 8,000
        BigDecimal discountedPrice = product.getDiscountedPrice();
        assertEquals(new BigDecimal("8000.00"), discountedPrice);

        BigDecimal savings = product.getSavingsAmount();
        assertEquals(new BigDecimal("2000.00"), savings);
    }

    @Test
    @DisplayName("Product stock update must record audit log and update quantity")
    void testUpdateStock() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.updateStock(1L, 25, "admin@shopsphere.com");

        assertEquals(25, product.getStockQuantity());
        verify(productRepository, times(1)).save(product);
        verify(auditLogService, times(1)).log(eq("admin@shopsphere.com"), eq("UPDATE_STOCK"), eq("Product"), eq("1"), anyString(), anyString());
    }

    @Test
    @DisplayName("Create product should persist entity and generate slug")
    void testCreateProduct() {
        ProductDto dto = ProductDto.builder()
                .categoryId(1L)
                .name("Dell XPS 15")
                .brand("Dell")
                .price(BigDecimal.valueOf(150000.00))
                .discountPercentage(BigDecimal.valueOf(10.00))
                .stockQuantity(8)
                .sku("LAP-DEL-XPS15")
                .active(true)
                .build();

        when(productRepository.existsBySku("LAP-DEL-XPS15")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(fileStorageService.getDefaultProductImage()).thenReturn("/images/products/placeholder.svg");
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product created = productService.createProduct(dto, "admin@shopsphere.com");

        assertNotNull(created);
        assertEquals("Dell XPS 15", created.getName());
        assertEquals("dell-xps-15", created.getSlug());
        assertEquals(8, created.getStockQuantity());
        verify(productRepository, times(1)).save(any(Product.class));
    }
}
