package com.example.ecommerce.admin;

import org.springframework.web.bind.annotation.*;
import java.util.*;
import com.example.ecommerce.user.*;
import com.example.ecommerce.product.*;
import com.example.ecommerce.order.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserRepository u;
    private final ProductRepository p;
    private final OrderRepository o;

    public AdminController(UserRepository u, ProductRepository p, OrderRepository o) {
        this.u = u;
        this.p = p;
        this.o = o;
    }

    @GetMapping("/dashboard")
    Map<String, Object> dash() {
        return Map.of("totalUsers", u.count(), "totalProducts", p.count(), "totalOrders", o.count(), "orders",
                o.findAll());
    }

    @GetMapping("/users")
    List<User> users() {
        return u.findAll();
    }

    @PutMapping("/users/{id}/enabled")
    User enabled(@PathVariable Long id, @RequestParam boolean value) {
        User x = u.getReferenceById(id);
        x.setEnabled(value);
        return u.save(x);
    }
}
