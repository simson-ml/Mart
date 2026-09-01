package com.example.ecommerce.review;

import org.springframework.web.bind.annotation.*;
import com.example.ecommerce.auth.*;
import com.example.ecommerce.product.*;
import jakarta.servlet.http.*;
import java.util.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewRepository r;
    private final ProductRepository p;
    private final AuthController a;

    public ReviewController(ReviewRepository r, ProductRepository p, AuthController a) {
        this.r = r;
        this.p = p;
        this.a = a;
    }

    @GetMapping("/product/{id}")
    List<Review> all(@PathVariable Long id) {
        return r.findByProduct_Id(id);
    }

    @PostMapping("/product/{id}")
    Review add(@PathVariable Long id, @RequestBody Review x, HttpSession s) {
        x.setUser(a.current(s));
        x.setProduct(p.getReferenceById(id));
        Review z = r.save(x);
        var rs = r.findByProduct_Id(id);
        z.getProduct().setRating(rs.stream().mapToInt(Review::getRating).average().orElse(0));
        p.save(z.getProduct());
        return z;
    }
}
