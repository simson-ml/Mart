package com.simson.shopsphere.service;

import com.simson.shopsphere.entity.Order;
import com.simson.shopsphere.entity.Payment;
import com.simson.shopsphere.entity.PaymentMethod;
import com.simson.shopsphere.entity.PaymentStatus;

import java.math.BigDecimal;

public interface PaymentService {
    Payment processPayment(Order order, PaymentMethod paymentMethod, BigDecimal amount);
    void updatePaymentStatus(Long paymentId, PaymentStatus status);
}
