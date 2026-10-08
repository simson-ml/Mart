package com.simson.shopsphere.repository;

import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByUser(User user);
}
