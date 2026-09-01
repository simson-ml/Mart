package com.example.ecommerce.product;

import org.springframework.data.jpa.repository.*;
import java.util.*;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String q);

    List<Product> findByCategory_NameIgnoreCase(String c);
}
