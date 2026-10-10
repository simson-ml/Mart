package com.simson.shopsphere.controller.admin;

import com.simson.shopsphere.dto.OrderTimelineStepDto;
import com.simson.shopsphere.entity.Order;
import com.simson.shopsphere.entity.OrderStatus;
import com.simson.shopsphere.entity.PaymentStatus;
import com.simson.shopsphere.entity.Role;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.exception.UnauthorizedAccessException;
import com.simson.shopsphere.service.OrderService;
import com.simson.shopsphere.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final OrderService orderService;
    private final UserService userService;

    public AdminOrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
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
    public String listOrders(
            @RequestParam(value = "status", required = false) OrderStatus status,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model
    ) {
        Page<Order> ordersPage = orderService.getAllOrdersAdmin(status, keyword, PageRequest.of(Math.max(0, page), size));

        model.addAttribute("ordersPage", ordersPage);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("keyword", keyword);
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("currentPage", page);

        return "admin/orders/list";
    }

    @GetMapping("/{id}")
    public String orderDetails(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id);
        List<OrderTimelineStepDto> timeline = orderService.getOrderTimeline(order);

        model.addAttribute("order", order);
        model.addAttribute("timeline", timeline);
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("paymentStatuses", PaymentStatus.values());

        return "admin/orders/details";
    }

    @PostMapping("/{id}/status")
    public String updateOrderStatus(
            @PathVariable Long id,
            @RequestParam("status") OrderStatus status,
            RedirectAttributes redirectAttributes
    ) {
        String adminEmail = getAuthenticatedAdminEmail();

        try {
            orderService.updateOrderStatus(id, status, adminEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Order status updated to " + status.getDisplayName());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/orders/" + id;
    }

    @PostMapping("/{id}/payment-status")
    public String updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam("paymentStatus") PaymentStatus paymentStatus,
            RedirectAttributes redirectAttributes
    ) {
        String adminEmail = getAuthenticatedAdminEmail();

        try {
            orderService.updatePaymentStatus(id, paymentStatus, adminEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Payment status updated to " + paymentStatus.name());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/orders/" + id;
    }
}
