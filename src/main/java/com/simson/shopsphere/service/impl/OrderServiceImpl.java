package com.simson.shopsphere.service.impl;

import com.simson.shopsphere.dto.CheckoutRequest;
import com.simson.shopsphere.dto.OrderTimelineStepDto;
import com.simson.shopsphere.entity.*;
import com.simson.shopsphere.exception.*;
import com.simson.shopsphere.repository.*;
import com.simson.shopsphere.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final CartService cartService;
    private final CouponService couponService;
    private final PaymentService paymentService;
    private final AuditLogService auditLogService;
    private final OrderStatusTransitionValidator statusTransitionValidator;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository,
            AddressRepository addressRepository,
            CartService cartService,
            CouponService couponService,
            PaymentService paymentService,
            AuditLogService auditLogService,
            OrderStatusTransitionValidator statusTransitionValidator
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.addressRepository = addressRepository;
        this.cartService = cartService;
        this.couponService = couponService;
        this.paymentService = paymentService;
        this.auditLogService = auditLogService;
        this.statusTransitionValidator = statusTransitionValidator;
    }

    private static final BigDecimal FREE_DELIVERY_THRESHOLD = BigDecimal.valueOf(999.00);
    private static final BigDecimal STANDARD_DELIVERY_FEE = BigDecimal.valueOf(99.00);

    private String generateOrderNumber() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uniqueSuffix = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "ORD-" + datePart + "-" + uniqueSuffix;
    }

    @Override
    @Transactional
    public Order placeOrder(User user, CheckoutRequest checkoutRequest) {
        Cart cart = cartService.getOrCreateUserCart(user);
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BadRequestException("Your cart is empty. Please add items to your cart before checking out.");
        }

        Address address = addressRepository.findByIdAndUser(checkoutRequest.getAddressId(), user)
                .orElseThrow(() -> new BadRequestException("Please select a valid delivery address."));

        // 1. Re-validate stock and compute server-side prices with pessimistic concurrency lock
        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            Product product = productRepository.findByIdWithLock(cartItem.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + cartItem.getProduct().getName()));

            if (!product.isActive()) {
                throw new BadRequestException("Product '" + product.getName() + "' is no longer active for purchase.");
            }

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for '" + product.getName() + "'. Available: " + product.getStockQuantity() + ", requested: " + cartItem.getQuantity());
            }

            // Decrement stock under row lock
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            // Calculate exact unit price on server
            BigDecimal unitPrice = product.getDiscountedPrice();
            BigDecimal itemSubtotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            subtotal = subtotal.add(itemSubtotal);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .productName(product.getName())
                    .productSku(product.getSku())
                    .productImageUrl(product.getImageUrl())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(unitPrice)
                    .subtotal(itemSubtotal)
                    .build();

            orderItems.add(orderItem);
        }

        // 2. Delivery charge calculation
        BigDecimal deliveryCharge = BigDecimal.ZERO;
        if (subtotal.compareTo(FREE_DELIVERY_THRESHOLD) < 0) {
            deliveryCharge = STANDARD_DELIVERY_FEE;
        }

        // 3. Discount / Coupon calculation with atomic check
        BigDecimal discountAmount = BigDecimal.ZERO;
        String couponCode = checkoutRequest.getCouponCode();
        if (couponCode != null && !couponCode.isBlank()) {
            try {
                discountAmount = couponService.calculateDiscount(couponCode.trim(), subtotal);
                couponService.incrementCouponUsage(couponCode.trim());
            } catch (Exception e) {
                log.warn("Failed to apply coupon during checkout: {}", e.getMessage());
                discountAmount = BigDecimal.ZERO;
                couponCode = null;
            }
        }

        BigDecimal netAmount = subtotal.subtract(discountAmount).add(deliveryCharge);
        if (netAmount.compareTo(BigDecimal.ZERO) < 0) {
            netAmount = BigDecimal.ZERO;
        }

        // 4. Create Order Entity
        PaymentMethod paymentMethod = checkoutRequest.getPaymentMethod() != null ? checkoutRequest.getPaymentMethod() : PaymentMethod.COD;
        PaymentStatus initialPaymentStatus = (paymentMethod == PaymentMethod.COD || paymentMethod == PaymentMethod.UPI)
                ? PaymentStatus.PENDING
                : PaymentStatus.PAID;

        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .user(user)
                .totalAmount(subtotal)
                .discountAmount(discountAmount)
                .deliveryCharge(deliveryCharge)
                .netAmount(netAmount)
                .paymentStatus(initialPaymentStatus)
                .orderStatus(OrderStatus.PLACED)
                .paymentMethod(paymentMethod)
                .shippingAddressSnapshot(address.getFormattedAddress())
                .couponCode(couponCode)
                .notes(checkoutRequest.getNotes())
                .build();

        Order savedOrder = orderRepository.save(order);

        // 5. Attach Order Items to saved Order
        for (OrderItem item : orderItems) {
            item.setOrder(savedOrder);
            orderItemRepository.save(item);
        }
        savedOrder.setItems(orderItems);

        // 6. Process Payment record
        paymentService.processPayment(savedOrder, paymentMethod, netAmount);

        // 7. Clear user cart
        cartService.clearCart(user);

        log.info("Order placed successfully: orderNumber={}, user={}, total={}", savedOrder.getOrderNumber(), user.getEmail(), savedOrder.getNetAmount());
        return savedOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderByOrderNumber(String orderNumber, User user) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with order number: " + orderNumber));

        if (user != null && user.getRole() != Role.ADMIN && !order.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to view this order.");
        }
        return order;
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> getUserOrdersPaged(User user, Pageable pageable) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    @Override
    @Transactional
    public void cancelOrder(String orderNumber, User user, String reason) {
        Order order = getOrderByOrderNumber(orderNumber, user);

        // Centralized state transition validation
        statusTransitionValidator.validateTransition(order.getOrderStatus(), OrderStatus.CANCELLED);

        order.setOrderStatus(OrderStatus.CANCELLED);
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            order.setPaymentStatus(PaymentStatus.REFUNDED);
        }

        // Restore inventory with concurrency locking
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                if (item.getProduct() != null) {
                    productRepository.findByIdWithLock(item.getProduct().getId()).ifPresent(product -> {
                        product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                        productRepository.save(product);
                    });
                }
            }
        }

        orderRepository.save(order);
        log.info("Order {} cancelled by user {}. Reason: {}", orderNumber, user.getEmail(), reason);
    }

    @Override
    @Transactional
    public void requestReturn(String orderNumber, User user, String reason) {
        Order order = getOrderByOrderNumber(orderNumber, user);

        // Transition from DELIVERED -> RETURN_REQUESTED
        statusTransitionValidator.validateTransition(order.getOrderStatus(), OrderStatus.RETURN_REQUESTED);

        order.setOrderStatus(OrderStatus.RETURN_REQUESTED);
        if (order.getNotes() == null) {
            order.setNotes("Return Reason: " + reason);
        } else {
            order.setNotes(order.getNotes() + " | Return Reason: " + reason);
        }

        orderRepository.save(order);
        log.info("Return requested for order {} by user {}. Reason: {}", orderNumber, user.getEmail(), reason);
    }

    @Override
    @Transactional
    public void processReturn(Long orderId, boolean approved, String adminEmail) {
        Order order = getOrderById(orderId);
        OrderStatus targetStatus = approved ? OrderStatus.RETURN_APPROVED : OrderStatus.RETURN_REJECTED;

        statusTransitionValidator.validateTransition(order.getOrderStatus(), targetStatus);
        order.setOrderStatus(targetStatus);

        if (approved) {
            order.setOrderStatus(OrderStatus.REFUNDED);
            order.setPaymentStatus(PaymentStatus.REFUNDED);

            // Restore inventory upon approved return
            if (order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    if (item.getProduct() != null) {
                        productRepository.findByIdWithLock(item.getProduct().getId()).ifPresent(product -> {
                            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                            productRepository.save(product);
                        });
                    }
                }
            }
        }

        orderRepository.save(order);
        auditLogService.log(adminEmail, "PROCESS_RETURN", "Order", String.valueOf(orderId),
                "Processed return for order #" + order.getOrderNumber() + ": " + (approved ? "APPROVED & REFUNDED" : "REJECTED"), "127.0.0.1");
    }

    @Override
    public List<OrderTimelineStepDto> getOrderTimeline(Order order) {
        List<OrderTimelineStepDto> steps = new ArrayList<>();
        OrderStatus[] forwardStatuses = {
                OrderStatus.PLACED,
                OrderStatus.CONFIRMED,
                OrderStatus.PACKED,
                OrderStatus.SHIPPED,
                OrderStatus.OUT_FOR_DELIVERY,
                OrderStatus.DELIVERED
        };

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            steps.add(OrderTimelineStepDto.builder()
                    .status(OrderStatus.PLACED)
                    .title("Order Placed")
                    .description("Order was received")
                    .completed(true)
                    .current(false)
                    .timestamp(order.getCreatedAt())
                    .build());
            steps.add(OrderTimelineStepDto.builder()
                    .status(OrderStatus.CANCELLED)
                    .title("Order Cancelled")
                    .description("Order was cancelled and refund processed if applicable")
                    .completed(true)
                    .current(true)
                    .timestamp(order.getUpdatedAt())
                    .build());
            return steps;
        }

        if (order.getOrderStatus() == OrderStatus.RETURN_REQUESTED || order.getOrderStatus() == OrderStatus.RETURN_APPROVED ||
                order.getOrderStatus() == OrderStatus.RETURN_REJECTED || order.getOrderStatus() == OrderStatus.REFUNDED) {
            for (OrderStatus s : forwardStatuses) {
                steps.add(OrderTimelineStepDto.builder()
                        .status(s)
                        .title(s.getDisplayName())
                        .description(s.getDescription())
                        .completed(true)
                        .current(false)
                        .timestamp(order.getCreatedAt())
                        .build());
            }
            steps.add(OrderTimelineStepDto.builder()
                    .status(order.getOrderStatus())
                    .title(order.getOrderStatus().getDisplayName())
                    .description(order.getOrderStatus().getDescription())
                    .completed(true)
                    .current(true)
                    .timestamp(order.getUpdatedAt())
                    .build());
            return steps;
        }

        int currentOrdinal = order.getOrderStatus().ordinal();

        for (OrderStatus status : forwardStatuses) {
            boolean isCompleted = status.ordinal() <= currentOrdinal;
            boolean isCurrent = status == order.getOrderStatus();
            LocalDateTime time = isCompleted ? (isCurrent ? order.getUpdatedAt() : order.getCreatedAt()) : null;

            steps.add(OrderTimelineStepDto.builder()
                    .status(status)
                    .title(status.getDisplayName())
                    .description(status.getDescription())
                    .completed(isCompleted)
                    .current(isCurrent)
                    .timestamp(time)
                    .build());
        }

        return steps;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> getAllOrdersAdmin(OrderStatus status, String keyword, Pageable pageable) {
        return orderRepository.searchOrdersAdmin(status, keyword, pageable);
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus newStatus, String adminEmail) {
        Order order = getOrderById(orderId);
        OrderStatus currentStatus = order.getOrderStatus();

        if (currentStatus == newStatus) {
            return;
        }

        // Enforce centralized state machine validation
        statusTransitionValidator.validateTransition(currentStatus, newStatus);

        order.setOrderStatus(newStatus);

        // If COD order marked delivered, mark payment PAID
        if (newStatus == OrderStatus.DELIVERED && order.getPaymentMethod() == PaymentMethod.COD) {
            order.setPaymentStatus(PaymentStatus.PAID);
        }

        if (newStatus == OrderStatus.CANCELLED) {
            if (order.getPaymentStatus() == PaymentStatus.PAID) {
                order.setPaymentStatus(PaymentStatus.REFUNDED);
            }
            // Restore inventory
            if (order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    if (item.getProduct() != null) {
                        productRepository.findByIdWithLock(item.getProduct().getId()).ifPresent(product -> {
                            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                            productRepository.save(product);
                        });
                    }
                }
            }
        }

        orderRepository.save(order);
        auditLogService.log(adminEmail, "UPDATE_ORDER_STATUS", "Order", String.valueOf(orderId),
                "Changed status of order #" + order.getOrderNumber() + " from " + currentStatus + " to " + newStatus, "127.0.0.1");

        log.info("Order #{} status updated to {} by admin {}", order.getOrderNumber(), newStatus, adminEmail);
    }

    @Override
    @Transactional
    public void updatePaymentStatus(Long orderId, PaymentStatus newStatus, String adminEmail) {
        Order order = getOrderById(orderId);
        order.setPaymentStatus(newStatus);
        orderRepository.save(order);
        auditLogService.log(adminEmail, "UPDATE_PAYMENT_STATUS", "Order", String.valueOf(orderId),
                "Changed payment status of order #" + order.getOrderNumber() + " to " + newStatus, "127.0.0.1");
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalOrderCount() {
        return orderRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalSales() {
        BigDecimal sales = orderRepository.calculateTotalSales();
        return sales != null ? sales : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public long getPendingOrderCount() {
        return orderRepository.countByOrderStatusIn(List.of(OrderStatus.PLACED, OrderStatus.CONFIRMED, OrderStatus.PACKED));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getRecentOrders(int limit) {
        return orderRepository.findRecentOrders(PageRequest.of(0, limit));
    }
}
