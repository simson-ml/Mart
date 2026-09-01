package com.example.ecommerce.cart;

import org.springframework.web.bind.annotation.*;
import java.util.*;
import com.example.ecommerce.auth.*;
import com.example.ecommerce.user.*;
import com.example.ecommerce.product.*;
import com.example.ecommerce.common.*;
import jakarta.servlet.http.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartRepository r;
    private final ProductRepository p;
    private final AuthController a;

    public CartController(CartRepository r, ProductRepository p, AuthController a) {
        this.r = r;
        this.p = p;
        this.a = a;
    }

    @GetMapping
    public List<CartItem> get(HttpSession s) {
        return r.findByUser(a.current(s));
    }

    @PostMapping("/{pid}")
    public CartItem add(@PathVariable Long pid, @RequestParam(defaultValue = "1") int quantity, HttpSession s) {
        User u = a.current(s);
        CartItem x = r.findByUserAndProduct_Id(u, pid).orElseGet(CartItem::new);
        x.setUser(u);
        x.setProduct(p.findById(pid).orElseThrow(() -> new ApiException("Product not found")));
        x.setQuantity(x.getQuantity() + quantity);
        return r.save(x);
    }

    @PutMapping("/{id}")
    public CartItem qty(@PathVariable Long id, @RequestParam int quantity) {
        CartItem x = r.findById(id).orElseThrow(() -> new ApiException("Cart item not found"));
        x.setQuantity(quantity);
        return r.save(x);
    }

    @DeleteMapping("/{id}")
    public void del(@PathVariable Long id) {
        r.deleteById(id);
    }
}
