package com.example.ecommerce.auth;

import com.example.ecommerce.user.*;
import com.example.ecommerce.common.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.servlet.http.*;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder encoder;

    public AuthController(UserRepository u, PasswordEncoder e) {
        users = u;
        encoder = e;
    }

    record AuthRequest(String name, String email, String password) {
    }

    @PostMapping("/signup")
    public Map<String, Object> signup(@RequestBody AuthRequest r) {
        if (r.email() == null || r.password() == null || r.name() == null)
            throw new ApiException("Name, email and password required");
        if (users.existsByEmail(r.email()))
            throw new ApiException("Email already registered");
        User u = new User();
        u.setName(r.name());
        u.setEmail(r.email().toLowerCase());
        u.setPassword(encoder.encode(r.password()));
        users.save(u);
        return Map.of("message", "Account created", "user", safe(u));
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody AuthRequest r, HttpSession s) {
        User u = users.findByEmail(r.email().toLowerCase()).orElseThrow(() -> new ApiException("Invalid credentials"));
        if (!u.isEnabled() || !encoder.matches(r.password(), u.getPassword()))
            throw new ApiException("Invalid credentials");
        s.setAttribute("uid", u.getId());
        return Map.of("message", "Login successful", "user", safe(u));
    }

    @GetMapping("/me")
    public Map<String, Object> me(HttpSession s) {
        return Map.of("user", safe(current(s)));
    }

    @PostMapping("/logout")
    public Map<String, String> logout(HttpSession s) {
        s.invalidate();
        return Map.of("message", "Logged out");
    }

    @PutMapping("/profile")
    public Map<String, Object> profile(@RequestBody AuthRequest r, HttpSession s) {
        User u = current(s);
        if (r.name() != null)
            u.setName(r.name());
        users.save(u);
        return Map.of("user", safe(u));
    }

    @PostMapping("/change-password")
    public Map<String, String> change(@RequestBody Map<String, String> b, HttpSession s) {
        User u = current(s);
        if (!encoder.matches(b.get("oldPassword"), u.getPassword()))
            throw new ApiException("Old password incorrect");
        u.setPassword(encoder.encode(b.get("newPassword")));
        users.save(u);
        return Map.of("message", "Password changed");
    }

    public User current(HttpSession s) {
        Object id = s.getAttribute("uid");
        if (id == null)
            throw new ApiException("Login required");
        return users.findById((Long) id).orElseThrow(() -> new ApiException("User not found"));
    }

    private Map<String, Object> safe(User u) {
        return Map.of("id", u.getId(), "name", u.getName(), "email", u.getEmail(), "role", u.getRole().name());
    }
}
