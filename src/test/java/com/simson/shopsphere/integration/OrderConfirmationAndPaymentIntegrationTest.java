package com.simson.shopsphere.integration;

import com.simson.shopsphere.entity.*;
import com.simson.shopsphere.repository.AddressRepository;
import com.simson.shopsphere.repository.OrderRepository;
import com.simson.shopsphere.repository.PaymentRepository;
import com.simson.shopsphere.repository.ProductRepository;
import com.simson.shopsphere.repository.UserRepository;
import com.simson.shopsphere.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class OrderConfirmationAndPaymentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CartService cartService;

    private User rahul;
    private User priya;
    private Address rahulAddress;
    private Product product;
    private Order rahulOrder;
    private Order priyaOrder;

    @BeforeEach
    void setUp() {
        rahul = userRepository.findByEmailIgnoreCase("rahul@example.com")
                .orElseThrow(() -> new IllegalStateException("Seeded user rahul@example.com not found"));

        priya = userRepository.findByEmailIgnoreCase("priya@example.com")
                .orElseThrow(() -> new IllegalStateException("Seeded user priya@example.com not found"));

        rahulAddress = addressRepository.findByUserAndIsDefaultTrue(rahul)
                .orElseGet(() -> addressRepository.save(Address.builder()
                        .user(rahul)
                        .fullName("Rahul Sharma")
                        .phone("+91 9876543211")
                        .addressLine1("Flat 402, Green Acres")
                        .city("Bengaluru")
                        .state("Karnataka")
                        .pincode("560001")
                        .addressType(AddressType.HOME)
                        .isDefault(true)
                        .build()));

        product = productRepository.findAll().stream().filter(Product::isActive).findFirst()
                .orElseThrow(() -> new IllegalStateException("No active product found"));

        // Create a test order for Rahul
        rahulOrder = Order.builder()
                .orderNumber("ORD-RAHUL-1001")
                .user(rahul)
                .totalAmount(product.getDiscountedPrice())
                .discountAmount(BigDecimal.ZERO)
                .deliveryCharge(BigDecimal.ZERO)
                .netAmount(product.getDiscountedPrice())
                .paymentMethod(PaymentMethod.UPI)
                .paymentStatus(PaymentStatus.PENDING)
                .orderStatus(OrderStatus.PLACED)
                .shippingAddressSnapshot(rahulAddress.getFormattedAddress())
                .build();
        rahulOrder = orderRepository.save(rahulOrder);

        OrderItem item = OrderItem.builder()
                .order(rahulOrder)
                .product(product)
                .productName(product.getName())
                .productSku(product.getSku())
                .productImageUrl(product.getImageUrl())
                .quantity(1)
                .unitPrice(product.getDiscountedPrice())
                .subtotal(product.getDiscountedPrice())
                .build();
        rahulOrder.setItems(new ArrayList<>());
        rahulOrder.getItems().add(item);
        rahulOrder = orderRepository.save(rahulOrder);

        Payment payment = Payment.builder()
                .order(rahulOrder)
                .paymentMethod(PaymentMethod.UPI)
                .amount(rahulOrder.getNetAmount())
                .paymentStatus(PaymentStatus.PENDING)
                .transactionId("TXN-UPI-INIT001")
                .paymentGatewayResponse("{\"status\":\"PENDING\",\"mode\":\"UPI\",\"merchantUpiId\":\"simson2610@nyes\"}")
                .build();
        paymentRepository.save(payment);

        // Create a test order for Priya
        priyaOrder = Order.builder()
                .orderNumber("ORD-PRIYA-2002")
                .user(priya)
                .totalAmount(BigDecimal.valueOf(2500.00))
                .discountAmount(BigDecimal.ZERO)
                .deliveryCharge(BigDecimal.ZERO)
                .netAmount(BigDecimal.valueOf(2500.00))
                .paymentMethod(PaymentMethod.COD)
                .paymentStatus(PaymentStatus.PENDING)
                .orderStatus(OrderStatus.PLACED)
                .shippingAddressSnapshot("A-12, Shanti Niketan Society, Mumbai")
                .build();
        priyaOrder = orderRepository.save(priyaOrder);
    }

    @Test
    @DisplayName("Unauthenticated access to /orders/confirmation/{orderNumber} must redirect to login")
    void testAnonymousAccessToConfirmationRedirects() throws Exception {
        mockMvc.perform(get("/orders/confirmation/" + rahulOrder.getOrderNumber()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "rahul@example.com", roles = {"USER"})
    @DisplayName("Authenticated customer viewing own order confirmation should succeed with status 200 and display details")
    void testCustomerCanAccessOwnOrderConfirmation() throws Exception {
        mockMvc.perform(get("/orders/confirmation/" + rahulOrder.getOrderNumber()))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/confirmation"))
                .andExpect(model().attributeExists("order"))
                .andExpect(content().string(containsString(rahulOrder.getOrderNumber())))
                .andExpect(content().string(containsString("Thank You for Your Order!")))
                .andExpect(content().string(containsString("Delivery Address")));
    }

    @Test
    @WithMockUser(username = "rahul@example.com", roles = {"USER"})
    @DisplayName("Customer attempting to access another user's order confirmation must be forbidden (403)")
    void testCustomerCannotAccessAnotherCustomerOrderConfirmation() throws Exception {
        mockMvc.perform(get("/orders/confirmation/" + priyaOrder.getOrderNumber()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "rahul@example.com", roles = {"USER"})
    @DisplayName("Dedicated UPI payment page /orders/{orderNumber}/payment loads with valid URI and merchant info")
    void testUpiPaymentPageLoads() throws Exception {
        mockMvc.perform(get("/orders/" + rahulOrder.getOrderNumber() + "/payment"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/payment"))
                .andExpect(model().attributeExists("order", "upiUri", "merchantUpiId"))
                .andExpect(model().attribute("merchantUpiId", "simson2610@nyes"))
                .andExpect(content().string(containsString("simson2610@nyes")))
                .andExpect(content().string(containsString("Pay with UPI App")))
                .andExpect(content().string(containsString("UPI Reference / UTR Number")));
    }

    @Test
    @WithMockUser(username = "rahul@example.com", roles = {"USER"})
    @DisplayName("Checkout payment alias /checkout/payment/{orderNumber} redirects to /orders/{orderNumber}/payment")
    void testCheckoutPaymentAliasRedirect() throws Exception {
        mockMvc.perform(get("/checkout/payment/" + rahulOrder.getOrderNumber()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/" + rahulOrder.getOrderNumber() + "/payment"));
    }

    @Test
    @WithMockUser(username = "rahul@example.com", roles = {"USER"})
    @DisplayName("Submitting valid UTR updates payment record and redirects to order confirmation")
    void testSubmitUtrSuccess() throws Exception {
        mockMvc.perform(post("/orders/" + rahulOrder.getOrderNumber() + "/payment/submit-utr")
                .param("utrNumber", "428198273612")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/confirmation/" + rahulOrder.getOrderNumber()))
                .andExpect(flash().attributeExists("successMessage"));

        Payment updatedPayment = paymentRepository.findByOrder(rahulOrder).get(0);
        assertEquals("UPI-UTR-428198273612", updatedPayment.getTransactionId());
        assertEquals(PaymentStatus.PENDING, updatedPayment.getPaymentStatus());
    }

    @Test
    @WithMockUser(username = "rahul@example.com", roles = {"USER"})
    @DisplayName("Submitting invalid short UTR redirects back to payment page with error")
    void testSubmitUtrInvalid() throws Exception {
        mockMvc.perform(post("/orders/" + rahulOrder.getOrderNumber() + "/payment/submit-utr")
                .param("utrNumber", "12")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/" + rahulOrder.getOrderNumber() + "/payment"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @WithMockUser(username = "admin@shopsphere.com", roles = {"ADMIN"})
    @DisplayName("Admin can view order details including payment method and UTR history")
    void testAdminOrderDetailsDisplaysPaymentAndUtr() throws Exception {
        mockMvc.perform(get("/admin/orders/" + rahulOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/orders/details"))
                .andExpect(model().attributeExists("order"))
                .andExpect(content().string(containsString("UPI / QR Code")));
    }
}
