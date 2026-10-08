package com.simson.shopsphere.service.impl;

import com.simson.shopsphere.entity.Order;
import com.simson.shopsphere.entity.Payment;
import com.simson.shopsphere.entity.PaymentMethod;
import com.simson.shopsphere.entity.PaymentStatus;
import com.simson.shopsphere.exception.ResourceNotFoundException;
import com.simson.shopsphere.repository.PaymentRepository;
import com.simson.shopsphere.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PaymentServiceImpl.class);

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }


    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public Payment processPayment(Order order, PaymentMethod paymentMethod, BigDecimal amount) {
        String txnId = "TXN-" + paymentMethod.name() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        PaymentStatus status = (paymentMethod == PaymentMethod.COD) ? PaymentStatus.PENDING : PaymentStatus.PAID;
        String gatewayResponse = (paymentMethod == PaymentMethod.COD)
                ? "{\"status\":\"PENDING\",\"mode\":\"CASH_ON_DELIVERY\",\"note\":\"Pay on delivery\"}"
                : "{\"status\":\"SUCCESS\",\"mode\":\"MOCK_INSTANT_PAYMENT\",\"provider\":\"MOCK_GATEWAY_V1\"}";

        Payment payment = Payment.builder()
                .order(order)
                .transactionId(txnId)
                .paymentMethod(paymentMethod)
                .amount(amount)
                .paymentStatus(status)
                .paymentGatewayResponse(gatewayResponse)
                .build();

        log.info("Processed payment: txnId={}, method={}, amount={}, status={}", txnId, paymentMethod, amount, status);
        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public void updatePaymentStatus(Long paymentId, PaymentStatus status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found with id: " + paymentId));
        payment.setPaymentStatus(status);
        paymentRepository.save(payment);
    }
}
