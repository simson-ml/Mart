package com.simson.shopsphere.service;

import com.simson.shopsphere.dto.CouponDto;
import com.simson.shopsphere.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface CouponService {
    Coupon getCouponByCode(String code);
    BigDecimal calculateDiscount(String code, BigDecimal orderSubtotal);
    Coupon validateCoupon(String code, BigDecimal orderSubtotal);
    void incrementCouponUsage(String code);

    // Admin
    Page<Coupon> getAllCouponsAdmin(Pageable pageable);
    Coupon getCouponById(Long id);
    Coupon createCoupon(CouponDto dto, String adminEmail);
    Coupon updateCoupon(Long id, CouponDto dto, String adminEmail);
    void toggleCouponStatus(Long id, String adminEmail);
    long getTotalCouponCount();
}
