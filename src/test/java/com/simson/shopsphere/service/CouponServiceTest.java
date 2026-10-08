package com.simson.shopsphere.service;

import com.simson.shopsphere.entity.Coupon;
import com.simson.shopsphere.entity.DiscountType;
import com.simson.shopsphere.exception.InvalidCouponException;
import com.simson.shopsphere.repository.CouponRepository;
import com.simson.shopsphere.service.impl.CouponServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private CouponServiceImpl couponService;

    private Coupon percentageCoupon;
    private Coupon expiredCoupon;

    @BeforeEach
    void setUp() {
        percentageCoupon = Coupon.builder()
                .id(1L)
                .code("SAVE20")
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(BigDecimal.valueOf(20.00))
                .minimumOrderAmount(BigDecimal.valueOf(1000.00))
                .maximumDiscount(BigDecimal.valueOf(500.00))
                .startDate(LocalDateTime.now().minusDays(2))
                .expiryDate(LocalDateTime.now().plusDays(10))
                .usageLimit(100)
                .usageCount(5)
                .active(true)
                .build();

        expiredCoupon = Coupon.builder()
                .id(2L)
                .code("OLD50")
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(BigDecimal.valueOf(50.00))
                .startDate(LocalDateTime.now().minusDays(20))
                .expiryDate(LocalDateTime.now().minusDays(2))
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Valid coupon with cap should calculate capped discount")
    void testCalculateDiscount_WithCap() {
        when(couponRepository.findByCodeIgnoreCase("SAVE20")).thenReturn(Optional.of(percentageCoupon));

        // Subtotal = 5000. 20% of 5000 = 1000. But maximumDiscount is 500.
        BigDecimal discount = couponService.calculateDiscount("SAVE20", BigDecimal.valueOf(5000.00));
        assertEquals(new BigDecimal("500.00"), discount);
    }

    @Test
    @DisplayName("Coupon below minimum order amount should throw InvalidCouponException")
    void testCoupon_MinimumOrderViolation() {
        when(couponRepository.findByCodeIgnoreCase("SAVE20")).thenReturn(Optional.of(percentageCoupon));

        // Subtotal = 800 (below min 1000)
        assertThrows(InvalidCouponException.class, () -> {
            couponService.validateCoupon("SAVE20", BigDecimal.valueOf(800.00));
        });
    }

    @Test
    @DisplayName("Expired coupon should throw InvalidCouponException")
    void testCoupon_Expired() {
        when(couponRepository.findByCodeIgnoreCase("OLD50")).thenReturn(Optional.of(expiredCoupon));

        assertThrows(InvalidCouponException.class, () -> {
            couponService.validateCoupon("OLD50", BigDecimal.valueOf(2000.00));
        });
    }
}
