package com.simson.shopsphere.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "delivery_charge", nullable = false, precision = 12, scale = 2)
    private BigDecimal deliveryCharge = BigDecimal.ZERO;

    @Column(name = "net_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal netAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 30)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false, length = 30)
    private OrderStatus orderStatus = OrderStatus.PLACED;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 30)
    private PaymentMethod paymentMethod = PaymentMethod.COD;

    @Column(name = "shipping_address_snapshot", nullable = false, columnDefinition = "TEXT")
    private String shippingAddressSnapshot;

    @Column(name = "coupon_code", length = 50)
    private String couponCode;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Order() {}

    public Order(Long id, String orderNumber, User user, BigDecimal totalAmount, BigDecimal discountAmount,
                 BigDecimal deliveryCharge, BigDecimal netAmount, PaymentStatus paymentStatus,
                 OrderStatus orderStatus, PaymentMethod paymentMethod, String shippingAddressSnapshot,
                 String couponCode, String notes) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.user = user;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        this.deliveryCharge = deliveryCharge != null ? deliveryCharge : BigDecimal.ZERO;
        this.netAmount = netAmount;
        this.paymentStatus = paymentStatus != null ? paymentStatus : PaymentStatus.PENDING;
        this.orderStatus = orderStatus != null ? orderStatus : OrderStatus.PLACED;
        this.paymentMethod = paymentMethod != null ? paymentMethod : PaymentMethod.COD;
        this.shippingAddressSnapshot = shippingAddressSnapshot;
        this.couponCode = couponCode;
        this.notes = notes;
    }

    public static OrderBuilder builder() {
        return new OrderBuilder();
    }

    public static class OrderBuilder {
        private Long id;
        private String orderNumber;
        private User user;
        private BigDecimal totalAmount;
        private BigDecimal discountAmount = BigDecimal.ZERO;
        private BigDecimal deliveryCharge = BigDecimal.ZERO;
        private BigDecimal netAmount;
        private PaymentStatus paymentStatus = PaymentStatus.PENDING;
        private OrderStatus orderStatus = OrderStatus.PLACED;
        private PaymentMethod paymentMethod = PaymentMethod.COD;
        private String shippingAddressSnapshot;
        private String couponCode;
        private String notes;

        public OrderBuilder id(Long id) { this.id = id; return this; }
        public OrderBuilder orderNumber(String orderNumber) { this.orderNumber = orderNumber; return this; }
        public OrderBuilder user(User user) { this.user = user; return this; }
        public OrderBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public OrderBuilder discountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; return this; }
        public OrderBuilder deliveryCharge(BigDecimal deliveryCharge) { this.deliveryCharge = deliveryCharge; return this; }
        public OrderBuilder netAmount(BigDecimal netAmount) { this.netAmount = netAmount; return this; }
        public OrderBuilder paymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; return this; }
        public OrderBuilder orderStatus(OrderStatus orderStatus) { this.orderStatus = orderStatus; return this; }
        public OrderBuilder paymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; return this; }
        public OrderBuilder shippingAddressSnapshot(String shippingAddressSnapshot) { this.shippingAddressSnapshot = shippingAddressSnapshot; return this; }
        public OrderBuilder couponCode(String couponCode) { this.couponCode = couponCode; return this; }
        public OrderBuilder notes(String notes) { this.notes = notes; return this; }
        public Order build() {
            return new Order(id, orderNumber, user, totalAmount, discountAmount, deliveryCharge, netAmount, paymentStatus, orderStatus, paymentMethod, shippingAddressSnapshot, couponCode, notes);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    public BigDecimal getDeliveryCharge() { return deliveryCharge; }
    public void setDeliveryCharge(BigDecimal deliveryCharge) { this.deliveryCharge = deliveryCharge; }
    public BigDecimal getNetAmount() { return netAmount; }
    public void setNetAmount(BigDecimal netAmount) { this.netAmount = netAmount; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public OrderStatus getOrderStatus() { return orderStatus; }
    public void setOrderStatus(OrderStatus orderStatus) { this.orderStatus = orderStatus; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getShippingAddressSnapshot() { return shippingAddressSnapshot; }
    public void setShippingAddressSnapshot(String shippingAddressSnapshot) { this.shippingAddressSnapshot = shippingAddressSnapshot; }
    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public boolean isCancellable() {
        return orderStatus == OrderStatus.PLACED || orderStatus == OrderStatus.CONFIRMED;
    }

    public boolean isDelivered() {
        return orderStatus == OrderStatus.DELIVERED;
    }
}
