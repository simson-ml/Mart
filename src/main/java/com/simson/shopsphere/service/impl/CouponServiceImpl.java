package com.simson.shopsphere.service.impl;

import com.simson.shopsphere.dto.CouponDto;
import com.simson.shopsphere.entity.Coupon;
import com.simson.shopsphere.exception.DuplicateResourceException;
import com.simson.shopsphere.exception.InvalidCouponException;
import com.simson.shopsphere.exception.ResourceNotFoundException;
import com.simson.shopsphere.repository.CouponRepository;
import com.simson.shopsphere.service.AuditLogService;
import com.simson.shopsphere.service.CouponService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CouponServiceImpl implements CouponService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CouponServiceImpl.class);

    public CouponServiceImpl(CouponRepository couponRepository, AuditLogService auditLogService) {
        this.couponRepository = couponRepository;
        this.auditLogService = auditLogService;
    }


    private final CouponRepository couponRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional(readOnly = true)
    public Coupon getCouponByCode(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        return couponRepository.findByCodeIgnoreCaseAndActiveTrue(code.trim()).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Coupon validateCoupon(String code, BigDecimal orderSubtotal) {
        if (code == null || code.isBlank()) {
            throw new InvalidCouponException("Please provide a valid coupon code.");
        }

        Coupon coupon = couponRepository.findByCodeIgnoreCase(code.trim())
                .orElseThrow(() -> new InvalidCouponException("Coupon '" + code.trim() + "' does not exist."));

        if (!coupon.isActive()) {
            throw new InvalidCouponException("Coupon '" + code.trim() + "' is inactive.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartDate())) {
            throw new InvalidCouponException("Coupon '" + code.trim() + "' has not started yet.");
        }
        if (now.isAfter(coupon.getExpiryDate())) {
            throw new InvalidCouponException("Coupon '" + code.trim() + "' has expired.");
        }

        if (coupon.getUsageLimit() != null && coupon.getUsageCount() >= coupon.getUsageLimit()) {
            throw new InvalidCouponException("Coupon '" + code.trim() + "' usage limit has been reached.");
        }

        if (orderSubtotal != null && coupon.getMinimumOrderAmount() != null &&
                orderSubtotal.compareTo(coupon.getMinimumOrderAmount()) < 0) {
            throw new InvalidCouponException("Minimum order amount of ₹" + coupon.getMinimumOrderAmount() + " required to use coupon " + code.trim() + ".");
        }

        return coupon;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateDiscount(String code, BigDecimal orderSubtotal) {
        if (code == null || code.isBlank() || orderSubtotal == null) {
            return BigDecimal.ZERO;
        }
        Coupon coupon = validateCoupon(code, orderSubtotal);
        return coupon.calculateDiscount(orderSubtotal);
    }

    @Override
    @Transactional
    public void incrementCouponUsage(String code) {
        if (code == null || code.isBlank()) {
            return;
        }
        int updated = couponRepository.incrementCouponUsageAtomic(code.trim());
        if (updated == 0) {
            log.warn("Coupon {} usage limit reached or coupon deactivated during atomic increment", code);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Coupon> getAllCouponsAdmin(Pageable pageable) {
        return couponRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Coupon getCouponById(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));
    }

    @Override
    @Transactional
    public Coupon createCoupon(CouponDto dto, String adminEmail) {
        String code = dto.getCode().trim().toUpperCase();
        if (couponRepository.existsByCodeIgnoreCase(code)) {
            throw new DuplicateResourceException("Coupon with code '" + code + "' already exists.");
        }

        Coupon coupon = Coupon.builder()
                .code(code)
                .discountType(dto.getDiscountType())
                .discountValue(dto.getDiscountValue())
                .minimumOrderAmount(dto.getMinimumOrderAmount() != null ? dto.getMinimumOrderAmount() : BigDecimal.ZERO)
                .maximumDiscount(dto.getMaximumDiscount())
                .startDate(dto.getStartDate())
                .expiryDate(dto.getExpiryDate())
                .usageLimit(dto.getUsageLimit() != null ? dto.getUsageLimit() : 100)
                .usageCount(0)
                .active(dto.isActive())
                .build();

        Coupon saved = couponRepository.save(coupon);
        auditLogService.log(adminEmail, "CREATE_COUPON", "Coupon", String.valueOf(saved.getId()),
                "Created coupon: " + saved.getCode(), "127.0.0.1");

        return saved;
    }

    @Override
    @Transactional
    public Coupon updateCoupon(Long id, CouponDto dto, String adminEmail) {
        Coupon coupon = getCouponById(id);
        String code = dto.getCode().trim().toUpperCase();

        if (!coupon.getCode().equalsIgnoreCase(code) && couponRepository.existsByCodeIgnoreCase(code)) {
            throw new DuplicateResourceException("Coupon with code '" + code + "' already exists.");
        }

        coupon.setCode(code);
        coupon.setDiscountType(dto.getDiscountType());
        coupon.setDiscountValue(dto.getDiscountValue());
        coupon.setMinimumOrderAmount(dto.getMinimumOrderAmount() != null ? dto.getMinimumOrderAmount() : BigDecimal.ZERO);
        coupon.setMaximumDiscount(dto.getMaximumDiscount());
        coupon.setStartDate(dto.getStartDate());
        coupon.setExpiryDate(dto.getExpiryDate());
        coupon.setUsageLimit(dto.getUsageLimit() != null ? dto.getUsageLimit() : 100);
        coupon.setActive(dto.isActive());

        Coupon updated = couponRepository.save(coupon);
        auditLogService.log(adminEmail, "UPDATE_COUPON", "Coupon", String.valueOf(updated.getId()),
                "Updated coupon: " + updated.getCode(), "127.0.0.1");

        return updated;
    }

    @Override
    @Transactional
    public void toggleCouponStatus(Long id, String adminEmail) {
        Coupon coupon = getCouponById(id);
        coupon.setActive(!coupon.isActive());
        couponRepository.save(coupon);

        auditLogService.log(adminEmail, "TOGGLE_COUPON_STATUS", "Coupon", String.valueOf(coupon.getId()),
                "Coupon " + coupon.getCode() + " active=" + coupon.isActive(), "127.0.0.1");
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalCouponCount() {
        return couponRepository.count();
    }
}
