package com.example.ecommerce.order;

import org.springframework.data.jpa.repository.*;
import com.example.ecommerce.user.*;
import java.util.*;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User u);
}
