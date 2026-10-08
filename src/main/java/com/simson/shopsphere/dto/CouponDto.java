package com.simson.shopsphere.dto;

import com.simson.shopsphere.entity.DiscountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CouponDto {
    private Long id;

    @NotBlank(message = "Coupon code is required")
    private String code;

    private String description;

    @NotNull(message = "Discount type is required")
    private DiscountType discountType = DiscountType.PERCENTAGE;

    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.01", message = "Discount value must be greater than 0")
    private BigDecimal discountValue;

    private BigDecimal minimumOrderAmount = BigDecimal.ZERO;
    private BigDecimal maximumDiscount;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime expiryDate;

    private Integer usageLimit;
    private int timesUsed;
    private boolean active = true;

    public CouponDto() {}

    public CouponDto(Long id, String code, String description, DiscountType discountType, BigDecimal discountValue, BigDecimal minimumOrderAmount, BigDecimal maximumDiscount, LocalDateTime startDate, LocalDateTime expiryDate, Integer usageLimit, int timesUsed, boolean active) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minimumOrderAmount = minimumOrderAmount;
        this.maximumDiscount = maximumDiscount;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.usageLimit = usageLimit;
        this.timesUsed = timesUsed;
        this.active = active;
    }

    public static CouponDtoBuilder builder() {
        return new CouponDtoBuilder();
    }

    public static class CouponDtoBuilder {
        private Long id;
        private String code;
        private String description;
        private DiscountType discountType = DiscountType.PERCENTAGE;
        private BigDecimal discountValue;
        private BigDecimal minimumOrderAmount = BigDecimal.ZERO;
        private BigDecimal maximumDiscount;
        private LocalDateTime startDate;
        private LocalDateTime expiryDate;
        private Integer usageLimit;
        private int timesUsed;
        private boolean active = true;

        public CouponDtoBuilder id(Long id) { this.id = id; return this; }
        public CouponDtoBuilder code(String code) { this.code = code; return this; }
        public CouponDtoBuilder description(String description) { this.description = description; return this; }
        public CouponDtoBuilder discountType(DiscountType discountType) { this.discountType = discountType; return this; }
        public CouponDtoBuilder discountValue(BigDecimal discountValue) { this.discountValue = discountValue; return this; }
        public CouponDtoBuilder minimumOrderAmount(BigDecimal minimumOrderAmount) { this.minimumOrderAmount = minimumOrderAmount; return this; }
        public CouponDtoBuilder maximumDiscount(BigDecimal maximumDiscount) { this.maximumDiscount = maximumDiscount; return this; }
        public CouponDtoBuilder startDate(LocalDateTime startDate) { this.startDate = startDate; return this; }
        public CouponDtoBuilder expiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; return this; }
        public CouponDtoBuilder usageLimit(Integer usageLimit) { this.usageLimit = usageLimit; return this; }
        public CouponDtoBuilder timesUsed(int timesUsed) { this.timesUsed = timesUsed; return this; }
        public CouponDtoBuilder active(boolean active) { this.active = active; return this; }

        public CouponDto build() {
            return new CouponDto(id, code, description, discountType, discountValue, minimumOrderAmount, maximumDiscount, startDate, expiryDate, usageLimit, timesUsed, active);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

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

    public int getTimesUsed() { return timesUsed; }
    public void setTimesUsed(int timesUsed) { this.timesUsed = timesUsed; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
