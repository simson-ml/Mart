package com.example.ecommerce.wishlist;

import jakarta.persistence.*;
import com.example.ecommerce.user.*;
import com.example.ecommerce.product.*;

@Entity
public class WishlistItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    @ManyToOne
    private Product product;

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
}
