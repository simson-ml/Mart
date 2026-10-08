package com.simson.shopsphere.service;

import com.simson.shopsphere.dto.CheckoutRequest;
import com.simson.shopsphere.entity.*;
import com.simson.shopsphere.exception.InsufficientStockException;
import com.simson.shopsphere.repository.*;
import com.simson.shopsphere.service.impl.OrderServiceImpl;
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
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CartService cartService;

    @Mock
    private CouponService couponService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Address address;
    private Product product;
    private Cart cart;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("rahul@example.com").name("Rahul Sharma").build();

        address = Address.builder()
                .id(1L)
                .user(user)
                .fullName("Rahul Sharma")
                .phone("+91 9876543210")
                .addressLine1("Flat 402, Green Acres")
                .city("Bengaluru")
                .state("Karnataka")
                .pincode("560001")
                .isDefault(true)
                .build();

        product = Product.builder()
                .id(100L)
                .name("MacBook Air M3")
                .sku("LAP-APP-M3")
                .price(BigDecimal.valueOf(100000.00))
                .discountPercentage(BigDecimal.ZERO)
                .stockQuantity(10)
                .active(true)
                .build();

        cart = Cart.builder().id(1L).user(user).items(new ArrayList<>()).build();
        cartItem = CartItem.builder().id(1L).cart(cart).product(product).quantity(2).build();
        cart.getItems().add(cartItem);
    }

    @Test
    @DisplayName("Placing an order should decrement product stock and clear the cart")
    void testPlaceOrder_SuccessStockReduction() {
        CheckoutRequest checkoutRequest = CheckoutRequest.builder()
                .addressId(1L)
                .paymentMethod(PaymentMethod.COD)
                .build();

        when(cartService.getOrCreateUserCart(user)).thenReturn(cart);
        when(addressRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(address));
        when(productRepository.findByIdWithLock(100L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(99L);
            return o;
        });

        Order placedOrder = orderService.placeOrder(user, checkoutRequest);

        assertNotNull(placedOrder);
        // Initial stock was 10, customer bought 2 -> Remaining must be 8
        assertEquals(8, product.getStockQuantity());
        verify(productRepository, times(1)).save(product);
        verify(cartService, times(1)).clearCart(user);
        verify(paymentService, times(1)).processPayment(any(Order.class), eq(PaymentMethod.COD), any(BigDecimal.class));
    }

    @Test
    @DisplayName("Placing an order when quantity exceeds available stock should fail atomically")
    void testPlaceOrder_InsufficientStockFails() {
        // Change cart item quantity to 20 when stock is 10
        cartItem.setQuantity(20);

        CheckoutRequest checkoutRequest = CheckoutRequest.builder()
                .addressId(1L)
                .paymentMethod(PaymentMethod.COD)
                .build();

        when(cartService.getOrCreateUserCart(user)).thenReturn(cart);
        when(addressRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(address));
        when(productRepository.findByIdWithLock(100L)).thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class, () -> {
            orderService.placeOrder(user, checkoutRequest);
        });

        // Stock must remain unchanged at 10
        assertEquals(10, product.getStockQuantity());
        verify(orderRepository, never()).save(any(Order.class));
        verify(cartService, never()).clearCart(user);
    }
}
