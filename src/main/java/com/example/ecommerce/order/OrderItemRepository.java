package com.example.ecommerce.order;

import org.springframework.data.jpa.repository.*;
import java.util.*;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order o);
}
