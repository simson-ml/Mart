package com.example.ecommerce.product;

import jakarta.persistence.*;
import java.math.*;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(length = 2000)
    private String description;
    private BigDecimal price;
    private int stock;
    private String imageUrl;
    private double rating;
    @ManyToOne
    private Category category;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String x) {
        name = x;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String x) {
        description = x;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal x) {
        price = x;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int x) {
        stock = x;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String x) {
        imageUrl = x;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double x) {
        rating = x;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category x) {
        category = x;
    }
}
