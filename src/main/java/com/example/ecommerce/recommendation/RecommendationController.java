package com.example.ecommerce.recommendation;

import org.springframework.web.bind.annotation.*;
import com.example.ecommerce.product.*;
import java.util.*;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
    private final ProductRepository p;

    public RecommendationController(ProductRepository p) {
        this.p = p;
    }

    @GetMapping
    public List<Product> recommended(@RequestParam(required = false) String category) {
        return category == null
                ? p.findAll().stream().sorted((a, b) -> Double.compare(b.getRating(), a.getRating())).limit(8).toList()
                : p.findByCategory_NameIgnoreCase(category);
    }
}
