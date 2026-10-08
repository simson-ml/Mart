package com.simson.shopsphere.controller;

import com.simson.shopsphere.dto.OrderTimelineStepDto;
import com.simson.shopsphere.entity.Order;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.service.OrderService;
import com.simson.shopsphere.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping
    public String listOrders(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model
    ) {
        User user = userService.getCurrentAuthenticatedUser();
        Page<Order> ordersPage = orderService.getUserOrdersPaged(user, PageRequest.of(Math.max(0, page), size));
        model.addAttribute("ordersPage", ordersPage);
        model.addAttribute("currentPage", page);
        return "orders/history";
    }

    @GetMapping("/{orderNumber}")
    public String orderDetails(@PathVariable String orderNumber, Model model) {
        User user = userService.getCurrentAuthenticatedUser();
        Order order = orderService.getOrderByOrderNumber(orderNumber, user);
        List<OrderTimelineStepDto> timeline = orderService.getOrderTimeline(order);

        model.addAttribute("order", order);
        model.addAttribute("timeline", timeline);
        return "orders/details";
    }

    @GetMapping("/{orderNumber}/track")
    public String trackOrder(@PathVariable String orderNumber, Model model) {
        return orderDetails(orderNumber, model);
    }

    @GetMapping("/{orderNumber}/invoice")
    public String viewInvoice(@PathVariable String orderNumber, Model model) {
        User user = userService.getCurrentAuthenticatedUser();
        Order order = orderService.getOrderByOrderNumber(orderNumber, user);
        model.addAttribute("order", order);
        return "orders/invoice";
    }

    @PostMapping("/{orderNumber}/cancel")
    public String cancelOrder(
            @PathVariable String orderNumber,
            @RequestParam(value = "reason", required = false, defaultValue = "Customer requested cancellation") String reason,
            RedirectAttributes redirectAttributes
    ) {
        User user = userService.getCurrentAuthenticatedUser();
        orderService.cancelOrder(orderNumber, user, reason);
        redirectAttributes.addFlashAttribute("successMessage", "Order #" + orderNumber + " has been cancelled.");
        return "redirect:/orders/" + orderNumber;
    }
}
