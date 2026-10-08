package com.simson.shopsphere.repository;

import com.simson.shopsphere.entity.Product;
import com.simson.shopsphere.entity.Wishlist;
import com.simson.shopsphere.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    Optional<WishlistItem> findByWishlistAndProduct(Wishlist wishlist, Product product);
    boolean existsByWishlistAndProduct(Wishlist wishlist, Product product);
    void deleteByWishlistAndProduct(Wishlist wishlist, Product product);
}
