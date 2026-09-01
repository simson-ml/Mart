package com.example.ecommerce.order;

import org.springframework.web.bind.annotation.*;
import com.example.ecommerce.auth.*;
import com.example.ecommerce.cart.*;
import com.example.ecommerce.common.*;
import jakarta.servlet.http.*;
import java.math.*;
import java.util.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderRepository o;
    private final OrderItemRepository oi;
    private final CartRepository c;
    private final AuthController a;

    public OrderController(OrderRepository o, OrderItemRepository oi, CartRepository c, AuthController a) {
        this.o = o;
        this.oi = oi;
        this.c = c;
        this.a = a;
    }

    @PostMapping
    public Order checkout(@RequestBody Map<String, String> b, HttpSession s) {
        var u = a.current(s);
        var cart = c.findByUser(u);
        if (cart.isEmpty())
            throw new ApiException("Cart is empty");
        Order x = new Order();
        x.setUser(u);
        x.setShippingAddress(b.getOrDefault("address", "Not provided"));
        BigDecimal total = cart.stream()
                .map(i -> i.getProduct().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        x.setTotalAmount(total);
        o.save(x);
        for (var z : cart) {
            if (z.getProduct().getStock() < z.getQuantity())
                throw new ApiException("Insufficient stock");
            z.getProduct().setStock(z.getProduct().getStock() - z.getQuantity());
            OrderItem item = new OrderItem();
            item.setOrder(x);
            item.setProduct(z.getProduct());
            item.setQuantity(z.getQuantity());
            item.setPrice(z.getProduct().getPrice());
            oi.save(item);
        }
        c.deleteByUser(u);
        return x;
    }

    @GetMapping
    public List<Order> history(HttpSession s) {
        return o.findByUser(a.current(s));
    }

    @PutMapping("/{id}/status")
    public Order status(@PathVariable Long id, @RequestParam OrderStatus status) {
        Order x = o.findById(id).orElseThrow(() -> new ApiException("Order not found"));
        x.setStatus(status);
        return o.save(x);
    }

    @PostMapping("/{id}/cancel")
    public Order cancel(@PathVariable Long id) {
        Order x = o.findById(id).orElseThrow(() -> new ApiException("Order not found"));
        x.setStatus(OrderStatus.CANCELLED);
        return o.save(x);
    }
}
