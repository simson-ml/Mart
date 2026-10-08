package com.simson.shopsphere.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "wishlist_items", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"wishlist_id", "product_id"})
})
public class WishlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wishlist_id", nullable = false)
    private Wishlist wishlist;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public WishlistItem() {}

    public WishlistItem(Long id, Wishlist wishlist, Product product) {
        this.id = id;
        this.wishlist = wishlist;
        this.product = product;
    }

    public static WishlistItemBuilder builder() {
        return new WishlistItemBuilder();
    }

    public static class WishlistItemBuilder {
        private Long id;
        private Wishlist wishlist;
        private Product product;

        public WishlistItemBuilder id(Long id) { this.id = id; return this; }
        public WishlistItemBuilder wishlist(Wishlist wishlist) { this.wishlist = wishlist; return this; }
        public WishlistItemBuilder product(Product product) { this.product = product; return this; }
        public WishlistItem build() {
            return new WishlistItem(id, wishlist, product);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Wishlist getWishlist() { return wishlist; }
    public void setWishlist(Wishlist wishlist) { this.wishlist = wishlist; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
