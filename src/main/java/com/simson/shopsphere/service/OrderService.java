package com.simson.shopsphere.service;

import com.simson.shopsphere.dto.CheckoutRequest;
import com.simson.shopsphere.dto.OrderTimelineStepDto;
import com.simson.shopsphere.entity.Order;
import com.simson.shopsphere.entity.OrderStatus;
import com.simson.shopsphere.entity.PaymentStatus;
import com.simson.shopsphere.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    Order placeOrder(User user, CheckoutRequest checkoutRequest);
    Order getOrderByOrderNumber(String orderNumber, User user);
    Order getOrderById(Long id);
    List<Order> getUserOrders(User user);
    Page<Order> getUserOrdersPaged(User user, Pageable pageable);
    void cancelOrder(String orderNumber, User user, String reason);
    void requestReturn(String orderNumber, User user, String reason);
    List<OrderTimelineStepDto> getOrderTimeline(Order order);

    // Admin
    Page<Order> getAllOrdersAdmin(OrderStatus status, String keyword, Pageable pageable);
    void updateOrderStatus(Long orderId, OrderStatus newStatus, String adminEmail);
    void processReturn(Long orderId, boolean approved, String adminEmail);
    void updatePaymentStatus(Long orderId, PaymentStatus newStatus, String adminEmail);
    long getTotalOrderCount();
    BigDecimal getTotalSales();
    long getPendingOrderCount();
    List<Order> getRecentOrders(int limit);
}
