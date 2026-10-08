package com.simson.shopsphere.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_sku", nullable = false, length = 50)
    private String productSku;

    @Column(name = "product_image_url", length = 500)
    private String productImageUrl;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    public OrderItem() {}

    public OrderItem(Long id, Order order, Product product, String productName, String productSku,
                     String productImageUrl, Integer quantity, BigDecimal unitPrice, BigDecimal subtotal) {
        this.id = id;
        this.order = order;
        this.product = product;
        this.productName = productName;
        this.productSku = productSku;
        this.productImageUrl = productImageUrl;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    public static OrderItemBuilder builder() {
        return new OrderItemBuilder();
    }

    public static class OrderItemBuilder {
        private Long id;
        private Order order;
        private Product product;
        private String productName;
        private String productSku;
        private String productImageUrl;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;

        public OrderItemBuilder id(Long id) { this.id = id; return this; }
        public OrderItemBuilder order(Order order) { this.order = order; return this; }
        public OrderItemBuilder product(Product product) { this.product = product; return this; }
        public OrderItemBuilder productName(String productName) { this.productName = productName; return this; }
        public OrderItemBuilder productSku(String productSku) { this.productSku = productSku; return this; }
        public OrderItemBuilder productImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; return this; }
        public OrderItemBuilder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public OrderItemBuilder unitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; return this; }
        public OrderItemBuilder subtotal(BigDecimal subtotal) { this.subtotal = subtotal; return this; }
        public OrderItem build() {
            return new OrderItem(id, order, product, productName, productSku, productImageUrl, quantity, unitPrice, subtotal);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getProductSku() { return productSku; }
    public void setProductSku(String productSku) { this.productSku = productSku; }
    public String getProductImageUrl() { return productImageUrl; }
    public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}
