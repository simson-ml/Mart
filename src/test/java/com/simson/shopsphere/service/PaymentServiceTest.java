package com.simson.shopsphere.service;

import com.simson.shopsphere.entity.Order;
import com.simson.shopsphere.entity.Payment;
import com.simson.shopsphere.entity.PaymentMethod;
import com.simson.shopsphere.entity.PaymentStatus;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.exception.BadRequestException;
import com.simson.shopsphere.repository.PaymentRepository;
import com.simson.shopsphere.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private User user;
    private Order order;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(paymentService, "defaultMerchantUpiId", "simson2610@nyes");
        ReflectionTestUtils.setField(paymentService, "defaultMerchantName", "ShopSphere Mart");

        user = User.builder().id(1L).email("rahul@example.com").name("Rahul Sharma").build();
        order = Order.builder()
                .id(10L)
                .orderNumber("ORD-20261009-TEST01")
                .user(user)
                .totalAmount(BigDecimal.valueOf(1500.00))
                .discountAmount(BigDecimal.ZERO)
                .deliveryCharge(BigDecimal.ZERO)
                .netAmount(BigDecimal.valueOf(1500.00))
                .paymentMethod(PaymentMethod.UPI)
                .paymentStatus(PaymentStatus.PENDING)
                .shippingAddressSnapshot("Flat 402, Green Acres, Bengaluru")
                .build();
    }

    @Test
    @DisplayName("UPI payment URI must be safely encoded and match the standard format")
    void testGenerateUpiPaymentUri() {
        String uri = paymentService.generateUpiPaymentUri(
                "simson2610@nyes",
                "ShopSphere Mart",
                BigDecimal.valueOf(1500.00),
                "ORD-20261009-TEST01"
        );

        assertNotNull(uri);
        assertTrue(uri.startsWith("upi://pay?"));
        assertTrue(uri.contains("pa=simson2610%40nyes") || uri.contains("pa=simson2610@nyes"));
        assertTrue(uri.contains("pn=ShopSphere%20Mart"));
        assertTrue(uri.contains("am=1500.00"));
        assertTrue(uri.contains("cu=INR"));
        assertTrue(uri.contains("tn=ORD-20261009-TEST01"));
    }

    @Test
    @DisplayName("Processing UPI payment creates a record with PENDING status")
    void testProcessPayment_UpiPending() {
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        Payment payment = paymentService.processPayment(order, PaymentMethod.UPI, BigDecimal.valueOf(1500.00));

        assertNotNull(payment);
        assertEquals(PaymentMethod.UPI, payment.getPaymentMethod());
        assertEquals(PaymentStatus.PENDING, payment.getPaymentStatus());
        assertEquals(BigDecimal.valueOf(1500.00), payment.getAmount());
        assertTrue(payment.getTransactionId().startsWith("TXN-UPI-"));
        assertTrue(payment.getPaymentGatewayResponse().contains("simson2610@nyes"));
    }

    @Test
    @DisplayName("Submitting valid UTR updates payment transaction ID and keeps status PENDING for verification")
    void testSubmitUpiTransaction_Success() {
        Payment existingPayment = Payment.builder()
                .id(1L)
                .order(order)
                .paymentMethod(PaymentMethod.UPI)
                .amount(BigDecimal.valueOf(1500.00))
                .paymentStatus(PaymentStatus.PENDING)
                .transactionId("TXN-UPI-OLD123")
                .build();

        when(paymentRepository.findByOrder(order)).thenReturn(List.of(existingPayment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        Payment updated = paymentService.submitUpiTransaction(order, "428198273612", BigDecimal.valueOf(1500.00));

        assertNotNull(updated);
        assertEquals("UPI-UTR-428198273612", updated.getTransactionId());
        assertEquals(PaymentStatus.PENDING, updated.getPaymentStatus());
        assertTrue(updated.getPaymentGatewayResponse().contains("428198273612"));
        assertTrue(updated.getPaymentGatewayResponse().contains("SUBMITTED_FOR_VERIFICATION"));
    }

    @Test
    @DisplayName("Submitting invalid or short UTR throws BadRequestException")
    void testSubmitUpiTransaction_InvalidUtr() {
        assertThrows(BadRequestException.class, () -> {
            paymentService.submitUpiTransaction(order, "123", BigDecimal.valueOf(1500.00));
        });

        assertThrows(BadRequestException.class, () -> {
            paymentService.submitUpiTransaction(order, "", BigDecimal.valueOf(1500.00));
        });

        assertThrows(BadRequestException.class, () -> {
            paymentService.submitUpiTransaction(order, null, BigDecimal.valueOf(1500.00));
        });
    }

    @Test
    @DisplayName("Submitting mismatched amount throws BadRequestException")
    void testSubmitUpiTransaction_MismatchedAmount() {
        assertThrows(BadRequestException.class, () -> {
            paymentService.submitUpiTransaction(order, "428198273612", BigDecimal.valueOf(999.00));
        });
    }
}
