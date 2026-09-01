package com.example.ecommerce.payment;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    @PostMapping("/simulate")
    public Map<String, Object> pay(@RequestBody Map<String, Object> b) {
        return Map.of("status", "SUCCESS", "transactionId", "SIM-" + UUID.randomUUID(), "message",
                "Demo payment successful. Integrate Razorpay/Stripe in production.");
    }
}
