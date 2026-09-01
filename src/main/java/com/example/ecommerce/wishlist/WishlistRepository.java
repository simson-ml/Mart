package com.example.ecommerce.wishlist;

import org.springframework.data.jpa.repository.*;
import com.example.ecommerce.user.*;
import java.util.*;

public interface WishlistRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findByUser(User u);

    Optional<WishlistItem> findByUserAndProduct_Id(User u, Long p);
}
