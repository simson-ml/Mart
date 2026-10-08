package com.simson.shopsphere.service;

import com.simson.shopsphere.dto.CartDto;
import com.simson.shopsphere.entity.Cart;
import com.simson.shopsphere.entity.CartItem;
import com.simson.shopsphere.entity.Product;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.exception.BadRequestException;
import com.simson.shopsphere.exception.InsufficientStockException;
import com.simson.shopsphere.repository.CartItemRepository;
import com.simson.shopsphere.repository.CartRepository;
import com.simson.shopsphere.repository.ProductRepository;
import com.simson.shopsphere.service.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CouponService couponService;

    @InjectMocks
    private CartServiceImpl cartService;

    private User user;
    private Product product;
    private Cart cart;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("rahul@example.com").name("Rahul").build();
        product = Product.builder()
                .id(10L)
                .name("Samsung Galaxy S24")
                .price(BigDecimal.valueOf(50000.00))
                .discountPercentage(BigDecimal.valueOf(10.00))
                .stockQuantity(5)
                .active(true)
                .build();

        cart = Cart.builder()
                .id(1L)
                .user(user)
                .items(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Adding quantity exceeding available stock should throw InsufficientStockException")
    void testAddToCart_ExceedsStock() {
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        assertThrows(InsufficientStockException.class, () -> {
            cartService.addToCart(user, 10L, 10); // requested 10 when stock is 5
        });
    }

    @Test
    @DisplayName("Adding invalid negative quantity should throw BadRequestException")
    void testAddToCart_InvalidQuantity() {
        assertThrows(BadRequestException.class, () -> {
            cartService.addToCart(user, 10L, -2);
        });
    }

    @Test
    @DisplayName("Valid addition should store item in cart")
    void testAddToCart_Success() {
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.empty());

        cartService.addToCart(user, 10L, 2);

        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }
}
