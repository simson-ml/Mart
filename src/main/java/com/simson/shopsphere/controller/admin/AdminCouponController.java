package com.simson.shopsphere.controller.admin;

import com.simson.shopsphere.dto.CouponDto;
import com.simson.shopsphere.entity.Coupon;
import com.simson.shopsphere.entity.DiscountType;
import com.simson.shopsphere.entity.Role;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.exception.UnauthorizedAccessException;
import com.simson.shopsphere.service.CouponService;
import com.simson.shopsphere.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/coupons")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCouponController {

    private final CouponService couponService;
    private final UserService userService;

    public AdminCouponController(CouponService couponService, UserService userService) {
        this.couponService = couponService;
        this.userService = userService;
    }

    private String getAuthenticatedAdminEmail() {
        User currentAdmin = userService.getCurrentAuthenticatedUser();
        if (currentAdmin == null || currentAdmin.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("Administrative authentication required.");
        }
        return currentAdmin.getEmail();
    }

    @GetMapping
    public String listCoupons(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model
    ) {
        Page<Coupon> couponsPage = couponService.getAllCouponsAdmin(PageRequest.of(Math.max(0, page), size));

        model.addAttribute("couponsPage", couponsPage);
        model.addAttribute("discountTypes", DiscountType.values());
        model.addAttribute("currentPage", page);

        if (!model.containsAttribute("couponDto")) {
            model.addAttribute("couponDto", CouponDto.builder()
                    .discountType(DiscountType.PERCENTAGE)
                    .startDate(LocalDateTime.now())
                    .expiryDate(LocalDateTime.now().plusMonths(3))
                    .active(true)
                    .build());
        }

        return "admin/coupons/list";
    }

    @PostMapping("/create")
    public String createCoupon(
            @Valid @ModelAttribute("couponDto") CouponDto couponDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        String adminEmail = getAuthenticatedAdminEmail();

        if (bindingResult.hasErrors()) {
            model.addAttribute("couponsPage", couponService.getAllCouponsAdmin(PageRequest.of(0, 10)));
            model.addAttribute("discountTypes", DiscountType.values());
            return "admin/coupons/list";
        }

        try {
            couponService.createCoupon(couponDto, adminEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Coupon created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/coupons";
    }

    @PostMapping("/toggle/{id}")
    public String toggleCouponStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String adminEmail = getAuthenticatedAdminEmail();

        couponService.toggleCouponStatus(id, adminEmail);
        redirectAttributes.addFlashAttribute("infoMessage", "Coupon status updated.");
        return "redirect:/admin/coupons";
    }
}
