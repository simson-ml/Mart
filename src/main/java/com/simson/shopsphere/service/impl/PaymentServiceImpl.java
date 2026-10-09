package com.simson.shopsphere.service.impl;

import com.simson.shopsphere.entity.Order;
import com.simson.shopsphere.entity.Payment;
import com.simson.shopsphere.entity.PaymentMethod;
import com.simson.shopsphere.entity.PaymentStatus;
import com.simson.shopsphere.exception.BadRequestException;
import com.simson.shopsphere.exception.ResourceNotFoundException;
import com.simson.shopsphere.repository.PaymentRepository;
import com.simson.shopsphere.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;

    @Value("${app.upi.merchant-id:simson2610@nyes}")
    private String defaultMerchantUpiId;

    @Value("${app.upi.merchant-name:ShopSphere Mart}")
    private String defaultMerchantName;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public Payment processPayment(Order order, PaymentMethod paymentMethod, BigDecimal amount) {
        String txnId = "TXN-" + paymentMethod.name() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        PaymentStatus status = (paymentMethod == PaymentMethod.COD || paymentMethod == PaymentMethod.UPI)
                ? PaymentStatus.PENDING
                : PaymentStatus.PAID;

        String gatewayResponse;
        if (paymentMethod == PaymentMethod.COD) {
            gatewayResponse = "{\"status\":\"PENDING\",\"mode\":\"CASH_ON_DELIVERY\",\"note\":\"Pay on delivery\"}";
        } else if (paymentMethod == PaymentMethod.UPI) {
            gatewayResponse = "{\"status\":\"PENDING\",\"mode\":\"UPI\",\"merchantUpiId\":\"" + defaultMerchantUpiId + "\",\"note\":\"Awaiting customer UPI payment and UTR submission\"}";
        } else {
            gatewayResponse = "{\"status\":\"SUCCESS\",\"mode\":\"MOCK_INSTANT_PAYMENT\",\"provider\":\"MOCK_GATEWAY_V1\"}";
        }

        Payment payment = Payment.builder()
                .order(order)
                .transactionId(txnId)
                .paymentMethod(paymentMethod)
                .amount(amount)
                .paymentStatus(status)
                .paymentGatewayResponse(gatewayResponse)
                .build();

        log.info("Processed payment record: txnId={}, method={}, amount={}, status={}", txnId, paymentMethod, amount, status);
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

    @Override
    @Transactional
    public Payment submitUpiTransaction(Order order, String utrNumber, BigDecimal submittedAmount) {
        if (utrNumber == null || utrNumber.trim().length() < 6 || utrNumber.trim().length() > 30) {
            throw new BadRequestException("Please enter a valid 6 to 30 character UPI Reference / UTR Number.");
        }

        String cleanedUtr = utrNumber.trim().replaceAll("[^a-zA-Z0-9_-]", "");
        if (cleanedUtr.length() < 6) {
            throw new BadRequestException("Invalid UPI Reference / UTR Number format.");
        }

        if (submittedAmount != null && submittedAmount.compareTo(order.getNetAmount()) != 0) {
            throw new BadRequestException("The payment amount of ₹" + submittedAmount + " does not match the order payable amount of ₹" + order.getNetAmount() + ".");
        }

        List<Payment> existingPayments = paymentRepository.findByOrder(order);
        Payment payment;
        if (!existingPayments.isEmpty()) {
            payment = existingPayments.get(0);
        } else {
            payment = Payment.builder()
                    .order(order)
                    .paymentMethod(PaymentMethod.UPI)
                    .amount(order.getNetAmount())
                    .build();
        }

        String formattedDate = LocalDateTime.now().toString();
        payment.setTransactionId("UPI-UTR-" + cleanedUtr);
        payment.setPaymentMethod(PaymentMethod.UPI);
        payment.setAmount(order.getNetAmount());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setPaymentGatewayResponse("{\"status\":\"SUBMITTED_FOR_VERIFICATION\",\"mode\":\"UPI\",\"merchantUpiId\":\""
                + defaultMerchantUpiId + "\",\"utr\":\"" + cleanedUtr + "\",\"submittedAt\":\"" + formattedDate + "\"}");

        Payment savedPayment = paymentRepository.save(payment);
        log.info("UPI transaction UTR submitted for order #{}: UTR={}, amount={}", order.getOrderNumber(), cleanedUtr, order.getNetAmount());
        return savedPayment;
    }

    @Override
    public String generateUpiPaymentUri(String merchantUpiId, String merchantName, BigDecimal amount, String orderNumber) {
        String upiId = (merchantUpiId != null && !merchantUpiId.isBlank()) ? merchantUpiId.trim() : defaultMerchantUpiId;
        String payeeName = (merchantName != null && !merchantName.isBlank()) ? merchantName.trim() : defaultMerchantName;
        String formattedAmount = amount != null ? amount.setScale(2, RoundingMode.HALF_UP).toPlainString() : "0.00";
        String note = (orderNumber != null && !orderNumber.isBlank()) ? orderNumber.trim() : "ShopSphere Mart Order";

        String encodedPayeeName = URLEncoder.encode(payeeName, StandardCharsets.UTF_8).replace("+", "%20");
        String encodedNote = URLEncoder.encode(note, StandardCharsets.UTF_8).replace("+", "%20");

        return String.format("upi://pay?pa=%s&pn=%s&am=%s&cu=INR&tn=%s",
                upiId,
                encodedPayeeName,
                formattedAmount,
                encodedNote
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentByOrder(Order order) {
        List<Payment> payments = paymentRepository.findByOrder(order);
        return payments.isEmpty() ? null : payments.get(0);
    }
}
