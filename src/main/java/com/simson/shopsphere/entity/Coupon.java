package com.simson.shopsphere.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 20)
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "minimum_order_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal minimumOrderAmount = BigDecimal.ZERO;

    @Column(name = "maximum_discount", precision = 10, scale = 2)
    private BigDecimal maximumDiscount;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "usage_limit", nullable = false)
    private Integer usageLimit = 100;

    @Column(name = "usage_count", nullable = false)
    private Integer usageCount = 0;

    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Coupon() {}

    public Coupon(Long id, String code, DiscountType discountType, BigDecimal discountValue,
                  BigDecimal minimumOrderAmount, BigDecimal maximumDiscount, LocalDateTime startDate,
                  LocalDateTime expiryDate, Integer usageLimit, Integer usageCount, boolean active) {
        this.id = id;
        this.code = code;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minimumOrderAmount = minimumOrderAmount != null ? minimumOrderAmount : BigDecimal.ZERO;
        this.maximumDiscount = maximumDiscount;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.usageLimit = usageLimit != null ? usageLimit : 100;
        this.usageCount = usageCount != null ? usageCount : 0;
        this.active = active;
    }

    public static CouponBuilder builder() {
        return new CouponBuilder();
    }

    public static class CouponBuilder {
        private Long id;
        private String code;
        private DiscountType discountType;
        private BigDecimal discountValue;
        private BigDecimal minimumOrderAmount = BigDecimal.ZERO;
        private BigDecimal maximumDiscount;
        private LocalDateTime startDate;
        private LocalDateTime expiryDate;
        private Integer usageLimit = 100;
        private Integer usageCount = 0;
        private boolean active = true;

        public CouponBuilder id(Long id) { this.id = id; return this; }
        public CouponBuilder code(String code) { this.code = code; return this; }
        public CouponBuilder discountType(DiscountType discountType) { this.discountType = discountType; return this; }
        public CouponBuilder discountValue(BigDecimal discountValue) { this.discountValue = discountValue; return this; }
        public CouponBuilder minimumOrderAmount(BigDecimal minimumOrderAmount) { this.minimumOrderAmount = minimumOrderAmount; return this; }
        public CouponBuilder maximumDiscount(BigDecimal maximumDiscount) { this.maximumDiscount = maximumDiscount; return this; }
        public CouponBuilder startDate(LocalDateTime startDate) { this.startDate = startDate; return this; }
        public CouponBuilder expiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; return this; }
        public CouponBuilder usageLimit(Integer usageLimit) { this.usageLimit = usageLimit; return this; }
        public CouponBuilder usageCount(Integer usageCount) { this.usageCount = usageCount; return this; }
        public CouponBuilder active(boolean active) { this.active = active; return this; }
        public Coupon build() {
            return new Coupon(id, code, discountType, discountValue, minimumOrderAmount, maximumDiscount, startDate, expiryDate, usageLimit, usageCount, active);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public DiscountType getDiscountType() { return discountType; }
    public void setDiscountType(DiscountType discountType) { this.discountType = discountType; }
    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }
    public BigDecimal getMinimumOrderAmount() { return minimumOrderAmount; }
    public void setMinimumOrderAmount(BigDecimal minimumOrderAmount) { this.minimumOrderAmount = minimumOrderAmount; }
    public BigDecimal getMaximumDiscount() { return maximumDiscount; }
    public void setMaximumDiscount(BigDecimal maximumDiscount) { this.maximumDiscount = maximumDiscount; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
    public Integer getUsageLimit() { return usageLimit; }
    public void setUsageLimit(Integer usageLimit) { this.usageLimit = usageLimit; }
    public Integer getUsageCount() { return usageCount; }
    public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isCurrentlyValid() {
        LocalDateTime now = LocalDateTime.now();
        return active
                && !now.isBefore(startDate)
                && !now.isAfter(expiryDate)
                && (usageLimit == null || usageCount < usageLimit);
    }

    public BigDecimal calculateDiscount(BigDecimal orderSubtotal) {
        if (orderSubtotal == null || !isCurrentlyValid()) {
            return BigDecimal.ZERO;
        }
        if (minimumOrderAmount != null && orderSubtotal.compareTo(minimumOrderAmount) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount = BigDecimal.ZERO;
        if (discountType == DiscountType.PERCENTAGE) {
            discount = orderSubtotal.multiply(discountValue)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (maximumDiscount != null && discount.compareTo(maximumDiscount) > 0) {
                discount = maximumDiscount;
            }
        } else if (discountType == DiscountType.FIXED_AMOUNT) {
            discount = discountValue;
            if (discount.compareTo(orderSubtotal) > 0) {
                discount = orderSubtotal;
            }
        }
        return discount.setScale(2, RoundingMode.HALF_UP);
    }
}
