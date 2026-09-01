package com.example.ecommerce.product;

import org.springframework.data.jpa.repository.*;
import java.util.*;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNameIgnoreCase(String n);
}
