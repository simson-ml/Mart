package com.simson.shopsphere.service;

import com.simson.shopsphere.entity.Product;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.entity.Wishlist;

import java.util.List;

public interface WishlistService {
    Wishlist getOrCreateUserWishlist(User user);
    List<Product> getUserWishlistProducts(User user);
    void addToWishlist(User user, Long productId);
    void removeFromWishlist(User user, Long productId);
    void moveToCart(User user, Long productId);
    boolean isProductInWishlist(User user, Long productId);
    int getWishlistItemCount(User user);
}
