package com.simson.shopsphere.service;

import com.simson.shopsphere.dto.ProductDto;
import com.simson.shopsphere.entity.*;
import com.simson.shopsphere.exception.UnauthorizedAccessException;
import com.simson.shopsphere.repository.*;
import com.simson.shopsphere.service.impl.OrderServiceImpl;
import com.simson.shopsphere.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerServiceAndSecurityTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CartService cartService;

    @Mock
    private CouponService couponService;

    @Mock
    private PaymentService paymentService;

    private ProductServiceImpl productService;
    private OrderServiceImpl orderService;

    private User seller1;
    private User seller2;
    private User admin;
    private Category category;
    private Product productOfSeller1;

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl(
                productRepository,
                categoryRepository,
                fileStorageService,
                auditLogService,
                orderItemRepository
        );

        orderService = new OrderServiceImpl(
                orderRepository,
                orderItemRepository,
                productRepository,
                addressRepository,
                cartService,
                couponService,
                paymentService,
                auditLogService,
                new OrderStatusTransitionValidator()
        );

        seller1 = User.builder()
                .id(101L)
                .name("Seller One")
                .email("seller1@shopsphere.com")
                .role(Role.SELLER)
                .build();

        seller2 = User.builder()
                .id(102L)
                .name("Seller Two")
                .email("seller2@shopsphere.com")
                .role(Role.SELLER)
                .build();

        admin = User.builder()
                .id(1L)
                .name("Admin User")
                .email("admin@shopsphere.com")
                .role(Role.ADMIN)
                .build();

        category = Category.builder()
                .id(1L)
                .name("Fashion")
                .slug("fashion")
                .active(true)
                .build();

        productOfSeller1 = Product.builder()
                .id(501L)
                .name("Seller 1 Artisan Shoes")
                .slug("seller-1-artisan-shoes")
                .brand("Artisan")
                .price(BigDecimal.valueOf(2500.00))
                .discountPercentage(BigDecimal.ZERO)
                .stockQuantity(20)
                .sku("FAS-ART-001")
                .category(category)
                .seller(seller1)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Seller creates a product listing and seller association is established")
    void testCreateSellerProduct() {
        ProductDto dto = ProductDto.builder()
                .categoryId(1L)
                .name("Artisan Leather Belt")
                .brand("Artisan")
                .price(BigDecimal.valueOf(1200.00))
                .stockQuantity(15)
                .sku("FAS-BLT-001")
                .active(true)
                .build();

        when(productRepository.existsBySku("FAS-BLT-001")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(fileStorageService.getDefaultProductImage()).thenReturn("/images/products/placeholder.svg");
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product created = productService.createSellerProduct(dto, seller1);

        assertNotNull(created);
        assertEquals("Artisan Leather Belt", created.getName());
        assertEquals(seller1, created.getSeller());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Seller 2 cannot update Seller 1's product (Ownership isolation)")
    void testSellerCannotUpdateOtherSellerProduct() {
        when(productRepository.findById(501L)).thenReturn(Optional.of(productOfSeller1));

        ProductDto updateDto = ProductDto.builder()
                .categoryId(1L)
                .name("Hacked Product Name")
                .sku("FAS-ART-001")
                .price(BigDecimal.valueOf(10.00))
                .build();

        assertThrows(UnauthorizedAccessException.class, () ->
                productService.updateSellerProduct(501L, updateDto, seller2)
        );

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Seller 2 cannot modify stock of Seller 1's product")
    void testSellerCannotUpdateOtherSellerStock() {
        when(productRepository.findById(501L)).thenReturn(Optional.of(productOfSeller1));

        assertThrows(UnauthorizedAccessException.class, () ->
                productService.updateSellerStock(501L, 999, seller2)
        );

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Seller 2 cannot delete Seller 1's product")
    void testSellerCannotDeleteOtherSellerProduct() {
        when(productRepository.findById(501L)).thenReturn(Optional.of(productOfSeller1));

        assertThrows(UnauthorizedAccessException.class, () ->
                productService.deleteSellerProduct(501L, seller2)
        );

        verify(productRepository, never()).delete(any(Product.class));
    }

    @Test
    @DisplayName("Deleting product referenced in orders safely deactivates rather than hard deletes")
    void testSafeDeactivationWhenReferencedInOrders() {
        when(productRepository.findById(501L)).thenReturn(Optional.of(productOfSeller1));
        when(orderItemRepository.existsByProductId(501L)).thenReturn(true);
        when(productRepository.save(any(Product.class))).thenReturn(productOfSeller1);

        productService.deleteSellerProduct(501L, seller1);

        assertFalse(productOfSeller1.isActive());
        verify(productRepository, times(1)).save(productOfSeller1);
        verify(productRepository, never()).delete(any(Product.class));
    }

    @Test
    @DisplayName("Deleting product NOT referenced in orders performs hard delete")
    void testHardDeleteWhenNotReferencedInOrders() {
        when(productRepository.findById(501L)).thenReturn(Optional.of(productOfSeller1));
        when(orderItemRepository.existsByProductId(501L)).thenReturn(false);

        productService.deleteSellerProduct(501L, seller1);

        verify(productRepository, times(1)).delete(productOfSeller1);
    }

    @Test
    @DisplayName("Seller cannot view order details if order contains no products from that seller")
    void testSellerOrderAccessIsolation() {
        Order orderWithOnlySeller1Items = Order.builder()
                .id(1001L)
                .orderNumber("ORD-2026-001")
                .build();

        OrderItem item = OrderItem.builder()
                .id(1L)
                .product(productOfSeller1)
                .order(orderWithOnlySeller1Items)
                .build();
        orderWithOnlySeller1Items.getItems().add(item);

        when(orderRepository.findByOrderNumber("ORD-2026-001")).thenReturn(Optional.of(orderWithOnlySeller1Items));

        // Seller 1 can view
        Order allowedOrder = orderService.getSellerOrderDetails("ORD-2026-001", seller1);
        assertNotNull(allowedOrder);

        // Seller 2 cannot view
        assertThrows(UnauthorizedAccessException.class, () ->
                orderService.getSellerOrderDetails("ORD-2026-001", seller2)
        );
    }
}
