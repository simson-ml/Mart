package com.simson.shopsphere.repository;

import com.simson.shopsphere.entity.Product;
import com.simson.shopsphere.entity.Review;
import com.simson.shopsphere.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProductOrderByCreatedAtDesc(Product product);
    Page<Review> findByProductOrderByCreatedAtDesc(Product product, Pageable pageable);
    Optional<Review> findByProductAndUser(Product product, User user);
    boolean existsByProductAndUser(Product product, User user);
    long countByProduct(Product product);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product = :product")
    Double calculateAverageRating(@Param("product") Product product);

    @Query("SELECT CASE WHEN COUNT(oi) > 0 THEN TRUE ELSE FALSE END " +
           "FROM OrderItem oi JOIN oi.order o " +
           "WHERE o.user = :user AND oi.product = :product AND o.orderStatus = 'DELIVERED'")
    boolean hasPurchasedAndDelivered(@Param("user") User user, @Param("product") Product product);
}
