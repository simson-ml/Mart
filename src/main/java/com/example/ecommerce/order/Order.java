package com.example.ecommerce.order;

import jakarta.persistence.*;
import java.math.*;
import java.time.*;
import com.example.ecommerce.user.*;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    private BigDecimal totalAmount;
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PLACED;
    private String shippingAddress;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User x) {
        user = x;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal x) {
        totalAmount = x;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus x) {
        status = x;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String x) {
        shippingAddress = x;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
