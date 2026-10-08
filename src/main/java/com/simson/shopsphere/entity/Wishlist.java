package com.simson.shopsphere.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wishlists")
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WishlistItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Wishlist() {}

    public Wishlist(Long id, User user, List<WishlistItem> items) {
        this.id = id;
        this.user = user;
        if (items != null) this.items = items;
    }

    public static WishlistBuilder builder() {
        return new WishlistBuilder();
    }

    public static class WishlistBuilder {
        private Long id;
        private User user;
        private List<WishlistItem> items = new ArrayList<>();

        public WishlistBuilder id(Long id) { this.id = id; return this; }
        public WishlistBuilder user(User user) { this.user = user; return this; }
        public WishlistBuilder items(List<WishlistItem> items) { this.items = items; return this; }
        public Wishlist build() {
            return new Wishlist(id, user, items);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public List<WishlistItem> getItems() { return items; }
    public void setItems(List<WishlistItem> items) { this.items = items; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
