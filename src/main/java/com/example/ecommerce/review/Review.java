package com.example.ecommerce.review;

import jakarta.persistence.*;
import java.time.*;
import com.example.ecommerce.user.*;
import com.example.ecommerce.product.*;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    @ManyToOne
    private Product product;
    private int rating;
    @Column(length = 2000)
    private String comment;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product x) {
        product = x;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int x) {
        rating = x;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String x) {
        comment = x;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
