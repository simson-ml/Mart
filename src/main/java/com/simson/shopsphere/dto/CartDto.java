package com.simson.shopsphere.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartDto {
    private Long id;
    private List<CartItemDto> items = new ArrayList<>();
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal originalTotal = BigDecimal.ZERO;
    private BigDecimal totalSavings = BigDecimal.ZERO;
    private BigDecimal discount = BigDecimal.ZERO;
    private BigDecimal discountAmount = BigDecimal.ZERO;
    private BigDecimal deliveryCharge = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;
    private BigDecimal netTotal = BigDecimal.ZERO;
    private int totalItemCount;
    private String couponCode;

    public CartDto() {}

    public CartDto(Long id, List<CartItemDto> items, BigDecimal subtotal, BigDecimal originalTotal, BigDecimal totalSavings, BigDecimal discount, BigDecimal discountAmount, BigDecimal deliveryCharge, BigDecimal total, BigDecimal netTotal, int totalItemCount, String couponCode) {
        this.id = id;
        this.items = items;
        this.subtotal = subtotal;
        this.originalTotal = originalTotal;
        this.totalSavings = totalSavings;
        this.discount = discount;
        this.discountAmount = discountAmount;
        this.deliveryCharge = deliveryCharge;
        this.total = total;
        this.netTotal = netTotal;
        this.totalItemCount = totalItemCount;
        this.couponCode = couponCode;
    }

    public static CartDtoBuilder builder() {
        return new CartDtoBuilder();
    }

    public static class CartDtoBuilder {
        private Long id;
        private List<CartItemDto> items = new ArrayList<>();
        private BigDecimal subtotal = BigDecimal.ZERO;
        private BigDecimal originalTotal = BigDecimal.ZERO;
        private BigDecimal totalSavings = BigDecimal.ZERO;
        private BigDecimal discount = BigDecimal.ZERO;
        private BigDecimal discountAmount = BigDecimal.ZERO;
        private BigDecimal deliveryCharge = BigDecimal.ZERO;
        private BigDecimal total = BigDecimal.ZERO;
        private BigDecimal netTotal = BigDecimal.ZERO;
        private int totalItemCount;
        private String couponCode;

        public CartDtoBuilder id(Long id) { this.id = id; return this; }
        public CartDtoBuilder items(List<CartItemDto> items) { this.items = items; return this; }
        public CartDtoBuilder subtotal(BigDecimal subtotal) { this.subtotal = subtotal; return this; }
        public CartDtoBuilder originalTotal(BigDecimal originalTotal) { this.originalTotal = originalTotal; return this; }
        public CartDtoBuilder totalSavings(BigDecimal totalSavings) { this.totalSavings = totalSavings; return this; }
        public CartDtoBuilder discount(BigDecimal discount) { this.discount = discount; this.discountAmount = discount; return this; }
        public CartDtoBuilder discountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; this.discount = discountAmount; return this; }
        public CartDtoBuilder deliveryCharge(BigDecimal deliveryCharge) { this.deliveryCharge = deliveryCharge; return this; }
        public CartDtoBuilder total(BigDecimal total) { this.total = total; this.netTotal = total; return this; }
        public CartDtoBuilder netTotal(BigDecimal netTotal) { this.netTotal = netTotal; this.total = netTotal; return this; }
        public CartDtoBuilder totalItemCount(int totalItemCount) { this.totalItemCount = totalItemCount; return this; }
        public CartDtoBuilder couponCode(String couponCode) { this.couponCode = couponCode; return this; }

        public CartDto build() {
            if (netTotal == null || (netTotal.compareTo(BigDecimal.ZERO) == 0 && total != null && total.compareTo(BigDecimal.ZERO) > 0)) {
                netTotal = total;
            }
            if (total == null || (total.compareTo(BigDecimal.ZERO) == 0 && netTotal != null && netTotal.compareTo(BigDecimal.ZERO) > 0)) {
                total = netTotal;
            }
            if (discountAmount == null || (discountAmount.compareTo(BigDecimal.ZERO) == 0 && discount != null && discount.compareTo(BigDecimal.ZERO) > 0)) {
                discountAmount = discount;
            }
            if (discount == null || (discount.compareTo(BigDecimal.ZERO) == 0 && discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0)) {
                discount = discountAmount;
            }
            return new CartDto(id, items, subtotal, originalTotal, totalSavings, discount, discountAmount, deliveryCharge, total, netTotal, totalItemCount, couponCode);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public List<CartItemDto> getItems() { return items; }
    public void setItems(List<CartItemDto> items) { this.items = items; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getOriginalTotal() { return originalTotal; }
    public void setOriginalTotal(BigDecimal originalTotal) { this.originalTotal = originalTotal; }

    public BigDecimal getTotalSavings() { return totalSavings; }
    public void setTotalSavings(BigDecimal totalSavings) { this.totalSavings = totalSavings; }

    public BigDecimal getDiscount() { return discount != null ? discount : discountAmount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; this.discountAmount = discount; }

    public BigDecimal getDiscountAmount() { return discountAmount != null ? discountAmount : discount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; this.discount = discountAmount; }

    public BigDecimal getDeliveryCharge() { return deliveryCharge; }
    public void setDeliveryCharge(BigDecimal deliveryCharge) { this.deliveryCharge = deliveryCharge; }

    public BigDecimal getTotal() { return total != null ? total : netTotal; }
    public void setTotal(BigDecimal total) { this.total = total; this.netTotal = total; }

    public BigDecimal getNetTotal() { return netTotal != null ? netTotal : total; }
    public void setNetTotal(BigDecimal netTotal) { this.netTotal = netTotal; this.total = netTotal; }

    public int getTotalItemCount() { return totalItemCount; }
    public void setTotalItemCount(int totalItemCount) { this.totalItemCount = totalItemCount; }

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
}
