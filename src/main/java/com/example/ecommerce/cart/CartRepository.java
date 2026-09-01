package com.example.ecommerce.cart;

import org.springframework.data.jpa.repository.*;
import com.example.ecommerce.user.*;
import java.util.*;

public interface CartRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User u);

    Optional<CartItem> findByUserAndProduct_Id(User u, Long id);

    void deleteByUser(User u);
}
