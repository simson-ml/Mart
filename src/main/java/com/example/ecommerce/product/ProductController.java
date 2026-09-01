package com.example.ecommerce.product;

import org.springframework.web.bind.annotation.*;
import java.util.*;
import com.example.ecommerce.common.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository p;
    private final CategoryRepository c;

    public ProductController(ProductRepository p, CategoryRepository c) {
        this.p = p;
        this.c = c;
    }

    @GetMapping
    public List<Product> all(@RequestParam(required = false) String q,
            @RequestParam(required = false) String category) {
        if (q != null)
            return p.findByNameContainingIgnoreCase(q);
        if (category != null)
            return p.findByCategory_NameIgnoreCase(category);
        return p.findAll();
    }

    @GetMapping("/{id}")
    public Product one(@PathVariable Long id) {
        return p.findById(id).orElseThrow(() -> new ApiException("Product not found"));
    }

    @PostMapping
    public Product add(@RequestBody Product x, @RequestParam(required = false) String category) {
        if (category != null)
            x.setCategory(c.findByNameIgnoreCase(category).orElseGet(() -> {
                Category z = new Category();
                z.setName(category);
                return c.save(z);
            }));
        return p.save(x);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product x) {
        Product a = one(id);
        a.setName(x.getName());
        a.setDescription(x.getDescription());
        a.setPrice(x.getPrice());
        a.setStock(x.getStock());
        a.setImageUrl(x.getImageUrl());
        return p.save(a);
    }

    @DeleteMapping("/{id}")
    public void del(@PathVariable Long id) {
        p.deleteById(id);
    }

    @GetMapping("/categories")
    public List<Category> cats() {
        return c.findAll();
    }
}
