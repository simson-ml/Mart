package com.example.ecommerce.review;

import org.springframework.data.jpa.repository.*;
import java.util.*;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProduct_Id(Long id);
}
