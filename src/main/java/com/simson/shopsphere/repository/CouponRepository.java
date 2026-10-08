package com.simson.shopsphere.repository;

import com.simson.shopsphere.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCodeIgnoreCase(String code);
    Optional<Coupon> findByCodeIgnoreCaseAndActiveTrue(String code);
    boolean existsByCodeIgnoreCase(String code);
    Page<Coupon> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Modifying
    @Query("UPDATE Coupon c SET c.usageCount = c.usageCount + 1 WHERE UPPER(c.code) = UPPER(:code) AND (c.usageLimit IS NULL OR c.usageCount < c.usageLimit)")
    int incrementCouponUsageAtomic(@Param("code") String code);
}
