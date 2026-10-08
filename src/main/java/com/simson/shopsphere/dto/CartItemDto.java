package com.simson.shopsphere.dto;

import java.math.BigDecimal;

public class CartItemDto {
    private Long id;
    private Long productId;
    private String productName;
    private String productSlug;
    private String productBrand;
    private String productSku;
    private String productImageUrl;
    private BigDecimal originalPrice;
    private BigDecimal discountPercentage = BigDecimal.ZERO;
    private BigDecimal unitPrice;
    private BigDecimal sellingPrice;
    private int quantity;
    private BigDecimal subtotal;
    private int availableStock;

    public CartItemDto() {}

    public CartItemDto(Long id, Long productId, String productName, String productSlug, String productBrand, String productSku, String productImageUrl, BigDecimal originalPrice, BigDecimal discountPercentage, BigDecimal unitPrice, BigDecimal sellingPrice, int quantity, BigDecimal subtotal, int availableStock) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.productSlug = productSlug;
        this.productBrand = productBrand;
        this.productSku = productSku;
        this.productImageUrl = productImageUrl;
        this.originalPrice = originalPrice;
        this.discountPercentage = discountPercentage;
        this.unitPrice = unitPrice;
        this.sellingPrice = sellingPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.availableStock = availableStock;
    }

    public static CartItemDtoBuilder builder() {
        return new CartItemDtoBuilder();
    }

    public static class CartItemDtoBuilder {
        private Long id;
        private Long productId;
        private String productName;
        private String productSlug;
        private String productBrand;
        private String productSku;
        private String productImageUrl;
        private BigDecimal originalPrice;
        private BigDecimal discountPercentage = BigDecimal.ZERO;
        private BigDecimal unitPrice;
        private BigDecimal sellingPrice;
        private int quantity;
        private BigDecimal subtotal;
        private int availableStock;

        public CartItemDtoBuilder id(Long id) { this.id = id; return this; }
        public CartItemDtoBuilder productId(Long productId) { this.productId = productId; return this; }
        public CartItemDtoBuilder productName(String productName) { this.productName = productName; return this; }
        public CartItemDtoBuilder productSlug(String productSlug) { this.productSlug = productSlug; return this; }
        public CartItemDtoBuilder productBrand(String productBrand) { this.productBrand = productBrand; return this; }
        public CartItemDtoBuilder productSku(String productSku) { this.productSku = productSku; return this; }
        public CartItemDtoBuilder productImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; return this; }
        public CartItemDtoBuilder originalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; return this; }
        public CartItemDtoBuilder discountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; return this; }
        public CartItemDtoBuilder unitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; this.sellingPrice = unitPrice; return this; }
        public CartItemDtoBuilder sellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; this.unitPrice = sellingPrice; return this; }
        public CartItemDtoBuilder quantity(int quantity) { this.quantity = quantity; return this; }
        public CartItemDtoBuilder subtotal(BigDecimal subtotal) { this.subtotal = subtotal; return this; }
        public CartItemDtoBuilder availableStock(int availableStock) { this.availableStock = availableStock; return this; }

        public CartItemDto build() {
            if (sellingPrice == null && unitPrice != null) sellingPrice = unitPrice;
            if (unitPrice == null && sellingPrice != null) unitPrice = sellingPrice;
            return new CartItemDto(id, productId, productName, productSlug, productBrand, productSku, productImageUrl, originalPrice, discountPercentage, unitPrice, sellingPrice, quantity, subtotal, availableStock);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductSlug() { return productSlug; }
    public void setProductSlug(String productSlug) { this.productSlug = productSlug; }

    public String getProductBrand() { return productBrand; }
    public void setProductBrand(String productBrand) { this.productBrand = productBrand; }

    public String getProductSku() { return productSku; }
    public void setProductSku(String productSku) { this.productSku = productSku; }

    public String getProductImageUrl() { return productImageUrl; }
    public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }

    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }

    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }

    public BigDecimal getUnitPrice() { return unitPrice != null ? unitPrice : sellingPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; this.sellingPrice = unitPrice; }

    public BigDecimal getSellingPrice() { return sellingPrice != null ? sellingPrice : unitPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; this.unitPrice = sellingPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public int getAvailableStock() { return availableStock; }
    public void setAvailableStock(int availableStock) { this.availableStock = availableStock; }
}
