package com.simson.shopsphere.service;

import com.simson.shopsphere.dto.CartDto;
import com.simson.shopsphere.entity.Cart;
import com.simson.shopsphere.entity.User;

public interface CartService {
    Cart getOrCreateUserCart(User user);
    CartDto getCartDto(User user, String couponCode);
    CartDto getGuestCartDto(java.util.Map<Long, Integer> guestItems, String couponCode);
    void mergeGuestCart(User user, java.util.Map<Long, Integer> guestItems);
    void addToCart(User user, Long productId, int quantity);
    void updateQuantity(User user, Long productId, int quantity);
    void removeFromCart(User user, Long productId);
    void clearCart(User user);
    int getCartItemCount(User user);
}
