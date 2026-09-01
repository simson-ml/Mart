package com.example.ecommerce.address;

import org.springframework.web.bind.annotation.*;
import com.example.ecommerce.auth.*;
import jakarta.servlet.http.*;
import java.util.*;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {
    private final AddressRepository r;
    private final AuthController a;

    public AddressController(AddressRepository r, AuthController a) {
        this.r = r;
        this.a = a;
    }

    @GetMapping
    List<Address> all(HttpSession s) {
        return r.findByUser(a.current(s));
    }

    @PostMapping
    Address add(@RequestBody Address x, HttpSession s) {
        x.setUser(a.current(s));
        return r.save(x);
    }

    @DeleteMapping("/{id}")
    void del(@PathVariable Long id) {
        r.deleteById(id);
    }
}
