package com.simson.shopsphere.controller.api;

import com.simson.shopsphere.dto.ApiResponse;
import com.simson.shopsphere.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/coupons")
@Tag(name = "Coupon API", description = "Endpoints for validating discount coupons")
public class ApiCouponController {

    public ApiCouponController(CouponService couponService) {
        this.couponService = couponService;
    }


    private final CouponService couponService;

    @GetMapping("/validate")
    @Operation(summary = "Validate coupon and compute discount amount")
    public ResponseEntity<ApiResponse<BigDecimal>> validateCoupon(
            @RequestParam String code,
            @RequestParam BigDecimal orderAmount
    ) {
        BigDecimal discount = couponService.calculateDiscount(code, orderAmount);
        return ResponseEntity.ok(ApiResponse.ok("Coupon valid", discount));
    }
}
