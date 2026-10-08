package com.simson.shopsphere.service.impl;

import com.simson.shopsphere.dto.AdminDashboardDto;
import com.simson.shopsphere.entity.Category;
import com.simson.shopsphere.entity.Order;
import com.simson.shopsphere.entity.OrderStatus;
import com.simson.shopsphere.entity.Product;
import com.simson.shopsphere.repository.CategoryRepository;
import com.simson.shopsphere.repository.OrderRepository;
import com.simson.shopsphere.repository.ProductRepository;
import com.simson.shopsphere.repository.UserRepository;
import com.simson.shopsphere.service.DashboardService;
import com.simson.shopsphere.service.OrderService;
import com.simson.shopsphere.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DashboardServiceImpl.class);

    public DashboardServiceImpl(OrderRepository orderRepository, ProductRepository productRepository, CategoryRepository categoryRepository, UserRepository userRepository, ProductService productService, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.productService = productService;
        this.orderService = orderService;
    }


    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final OrderService orderService;

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardDto getDashboardData() {
        BigDecimal totalSales = orderService.getTotalSales();
        java.time.LocalDateTime startOfToday = java.time.LocalDate.now().atStartOfDay();
        BigDecimal salesToday = orderRepository.calculateSalesSince(startOfToday);
        if (salesToday == null) salesToday = BigDecimal.ZERO;

        long totalOrders = orderRepository.count();
        long totalUsers = userRepository.count();
        long totalProducts = productRepository.countByActiveTrue();
        long lowStockCount = productService.getLowStockCount(10);
        long pendingOrdersCount = orderRepository.countByOrderStatusIn(List.of(
                OrderStatus.PLACED, OrderStatus.CONFIRMED, OrderStatus.PACKED, OrderStatus.SHIPPED, OrderStatus.OUT_FOR_DELIVERY
        ));
        long deliveredOrdersCount = orderRepository.countByOrderStatus(OrderStatus.DELIVERED);
        long pendingReturnsCount = orderRepository.countByOrderStatus(OrderStatus.RETURN_REQUESTED);

        List<Order> recentOrders = orderService.getRecentOrders(8);
        List<Product> lowStockProducts = productService.getLowStockProducts(10);

        // Category distribution (real DB counts)
        Map<String, Long> categoryDistribution = new LinkedHashMap<>();
        List<Category> categories = categoryRepository.findAll();
        for (Category cat : categories) {
            long count = cat.getProducts() != null ? cat.getProducts().stream().filter(Product::isActive).count() : 0;
            categoryDistribution.put(cat.getName(), count);
        }

        // Sales by Month (from real orders)
        Map<String, BigDecimal> salesByMonth = new LinkedHashMap<>();
        List<Order> allOrders = orderRepository.findAll();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy");

        for (Order o : allOrders) {
            if (o.getOrderStatus() != OrderStatus.CANCELLED && o.getOrderStatus() != OrderStatus.REFUNDED && o.getCreatedAt() != null) {
                String monthKey = o.getCreatedAt().format(monthFormatter);
                salesByMonth.put(monthKey, salesByMonth.getOrDefault(monthKey, BigDecimal.ZERO).add(o.getNetAmount()));
            }
        }

        return AdminDashboardDto.builder()
                .totalSales(totalSales)
                .salesToday(salesToday)
                .totalOrders(totalOrders)
                .totalUsers(totalUsers)
                .totalProducts(totalProducts)
                .lowStockCount(lowStockCount)
                .pendingOrdersCount(pendingOrdersCount)
                .deliveredOrdersCount(deliveredOrdersCount)
                .pendingReturnsCount(pendingReturnsCount)
                .recentOrders(recentOrders)
                .lowStockProducts(lowStockProducts)
                .categoryDistribution(categoryDistribution)
                .salesByMonth(salesByMonth)
                .build();
    }
}
