package com.simson.shopsphere.repository;

import com.simson.shopsphere.entity.Order;
import com.simson.shopsphere.entity.OrderStatus;
import com.simson.shopsphere.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserOrderByCreatedAtDesc(User user);
    Page<Order> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    Optional<Order> findByOrderNumber(String orderNumber);
    Optional<Order> findByOrderNumberAndUser(String orderNumber, User user);

    long countByOrderStatus(OrderStatus orderStatus);
    long countByOrderStatusIn(List<OrderStatus> statuses);

    @Query("SELECT COALESCE(SUM(o.netAmount), 0) FROM Order o WHERE o.orderStatus <> 'CANCELLED' AND o.orderStatus <> 'REFUNDED'")
    BigDecimal calculateTotalSales();

    @Query("SELECT COALESCE(SUM(o.netAmount), 0) FROM Order o WHERE (o.orderStatus <> 'CANCELLED' AND o.orderStatus <> 'REFUNDED') AND o.createdAt >= :since")
    BigDecimal calculateSalesSince(@Param("since") LocalDateTime since);

    @Query("SELECT o FROM Order o ORDER BY o.createdAt DESC")
    List<Order> findRecentOrders(Pageable pageable);

    @Query("SELECT o FROM Order o ORDER BY o.createdAt DESC")
    List<Order> findTop10RecentOrders(Pageable pageable);

    @Query("SELECT o FROM Order o WHERE " +
           "(:status IS NULL OR o.orderStatus = :status) AND " +
           "(:keyword IS NULL OR LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(o.user.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(o.user.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Order> searchOrdersAdmin(@Param("status") OrderStatus status, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt >= :since")
    long countOrdersSince(@Param("since") LocalDateTime since);
}
