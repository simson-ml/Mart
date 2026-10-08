package com.simson.shopsphere.dto;

import java.math.BigDecimal;

public class CartSummaryDto {
    private int totalItems;
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal discount = BigDecimal.ZERO;
    private BigDecimal deliveryCharge = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;
    private String couponCode;

    public CartSummaryDto() {}

    public CartSummaryDto(int totalItems, BigDecimal subtotal, BigDecimal discount, BigDecimal deliveryCharge, BigDecimal total, String couponCode) {
        this.totalItems = totalItems;
        this.subtotal = subtotal;
        this.discount = discount;
        this.deliveryCharge = deliveryCharge;
        this.total = total;
        this.couponCode = couponCode;
    }

    public static CartSummaryDtoBuilder builder() {
        return new CartSummaryDtoBuilder();
    }

    public static class CartSummaryDtoBuilder {
        private int totalItems;
        private BigDecimal subtotal = BigDecimal.ZERO;
        private BigDecimal discount = BigDecimal.ZERO;
        private BigDecimal deliveryCharge = BigDecimal.ZERO;
        private BigDecimal total = BigDecimal.ZERO;
        private String couponCode;

        public CartSummaryDtoBuilder totalItems(int totalItems) { this.totalItems = totalItems; return this; }
        public CartSummaryDtoBuilder subtotal(BigDecimal subtotal) { this.subtotal = subtotal; return this; }
        public CartSummaryDtoBuilder discount(BigDecimal discount) { this.discount = discount; return this; }
        public CartSummaryDtoBuilder deliveryCharge(BigDecimal deliveryCharge) { this.deliveryCharge = deliveryCharge; return this; }
        public CartSummaryDtoBuilder total(BigDecimal total) { this.total = total; return this; }
        public CartSummaryDtoBuilder couponCode(String couponCode) { this.couponCode = couponCode; return this; }

        public CartSummaryDto build() {
            return new CartSummaryDto(totalItems, subtotal, discount, deliveryCharge, total, couponCode);
        }
    }

    public int getTotalItems() { return totalItems; }
    public void setTotalItems(int totalItems) { this.totalItems = totalItems; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public BigDecimal getDeliveryCharge() { return deliveryCharge; }
    public void setDeliveryCharge(BigDecimal deliveryCharge) { this.deliveryCharge = deliveryCharge; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
}
