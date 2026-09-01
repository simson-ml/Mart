package com.example.ecommerce.order;

import jakarta.persistence.*;
import java.math.*;
import com.example.ecommerce.product.*;

@Entity
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Order order;
    @ManyToOne
    private Product product;
    private int quantity;
    private BigDecimal price;

    public Long getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order x) {
        order = x;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product x) {
        product = x;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int x) {
        quantity = x;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal x) {
        price = x;
    }
}
