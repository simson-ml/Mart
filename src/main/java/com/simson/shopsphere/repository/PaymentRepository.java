package com.simson.shopsphere.repository;

import com.simson.shopsphere.entity.Order;
import com.simson.shopsphere.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTransactionId(String transactionId);
    List<Payment> findByOrder(Order order);
}
