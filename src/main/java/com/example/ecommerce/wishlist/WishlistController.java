package com.example.ecommerce.wishlist;

import org.springframework.web.bind.annotation.*;
import com.example.ecommerce.auth.*;
import com.example.ecommerce.product.*;
import jakarta.servlet.http.*;
import java.util.*;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {
    private final WishlistRepository r;
    private final ProductRepository p;
    private final AuthController a;

    public WishlistController(WishlistRepository r, ProductRepository p, AuthController a) {
        this.r = r;
        this.p = p;
        this.a = a;
    }

    @GetMapping
    List<WishlistItem> all(HttpSession s) {
        return r.findByUser(a.current(s));
    }

    @PostMapping("/{id}")
    WishlistItem add(@PathVariable Long id, HttpSession s) {
        var u = a.current(s);
        return r.findByUserAndProduct_Id(u, id).orElseGet(() -> {
            WishlistItem x = new WishlistItem();
            x.setUser(u);
            x.setProduct(p.getReferenceById(id));
            return r.save(x);
        });
    }

    @DeleteMapping("/{id}")
    void del(@PathVariable Long id) {
        r.deleteById(id);
    }
}
