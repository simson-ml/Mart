package com.simson.shopsphere.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "transaction_id", nullable = false, unique = true, length = 100)
    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 30)
    private PaymentMethod paymentMethod;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 30)
    private PaymentStatus paymentStatus;

    @Column(name = "payment_gateway_response", columnDefinition = "TEXT")
    private String paymentGatewayResponse;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Payment() {}

    public Payment(Long id, Order order, String transactionId, PaymentMethod paymentMethod,
                   BigDecimal amount, PaymentStatus paymentStatus, String paymentGatewayResponse) {
        this.id = id;
        this.order = order;
        this.transactionId = transactionId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.paymentGatewayResponse = paymentGatewayResponse;
    }

    public static PaymentBuilder builder() {
        return new PaymentBuilder();
    }

    public static class PaymentBuilder {
        private Long id;
        private Order order;
        private String transactionId;
        private PaymentMethod paymentMethod;
        private BigDecimal amount;
        private PaymentStatus paymentStatus;
        private String paymentGatewayResponse;

        public PaymentBuilder id(Long id) { this.id = id; return this; }
        public PaymentBuilder order(Order order) { this.order = order; return this; }
        public PaymentBuilder transactionId(String transactionId) { this.transactionId = transactionId; return this; }
        public PaymentBuilder paymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; return this; }
        public PaymentBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public PaymentBuilder paymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; return this; }
        public PaymentBuilder paymentGatewayResponse(String paymentGatewayResponse) { this.paymentGatewayResponse = paymentGatewayResponse; return this; }
        public Payment build() {
            return new Payment(id, order, transactionId, paymentMethod, amount, paymentStatus, paymentGatewayResponse);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getPaymentGatewayResponse() { return paymentGatewayResponse; }
    public void setPaymentGatewayResponse(String paymentGatewayResponse) { this.paymentGatewayResponse = paymentGatewayResponse; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
