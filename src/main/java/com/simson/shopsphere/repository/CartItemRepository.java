package com.simson.shopsphere.repository;

import com.simson.shopsphere.entity.Cart;
import com.simson.shopsphere.entity.CartItem;
import com.simson.shopsphere.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
    void deleteByCartAndProduct(Cart cart, Product product);
    void deleteAllByCart(Cart cart);
}
