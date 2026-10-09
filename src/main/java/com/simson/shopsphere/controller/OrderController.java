package com.simson.shopsphere.controller;

import com.simson.shopsphere.dto.OrderTimelineStepDto;
import com.simson.shopsphere.entity.Order;
import com.simson.shopsphere.entity.Payment;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.service.OrderService;
import com.simson.shopsphere.service.PaymentService;
import com.simson.shopsphere.service.UserService;
import org.springframework.beans.factory.annotation.Value;
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
    private final PaymentService paymentService;

    @Value("${app.upi.merchant-id:simson2610@nyes}")
    private String merchantUpiId;

    @Value("${app.upi.merchant-name:ShopSphere Mart}")
    private String merchantName;

    public OrderController(OrderService orderService, UserService userService, PaymentService paymentService) {
        this.orderService = orderService;
        this.userService = userService;
        this.paymentService = paymentService;
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

    @GetMapping("/confirmation/{orderNumber}")
    public String orderConfirmation(@PathVariable String orderNumber, Model model) {
        User user = userService.getCurrentAuthenticatedUser();
        Order order = orderService.getOrderByOrderNumber(orderNumber, user);
        Payment payment = paymentService.getPaymentByOrder(order);

        model.addAttribute("order", order);
        model.addAttribute("payment", payment);
        return "orders/confirmation";
    }

    @GetMapping("/{orderNumber}/payment")
    public String upiPaymentPage(@PathVariable String orderNumber, Model model) {
        User user = userService.getCurrentAuthenticatedUser();
        Order order = orderService.getOrderByOrderNumber(orderNumber, user);
        Payment payment = paymentService.getPaymentByOrder(order);

        String upiUri = paymentService.generateUpiPaymentUri(
                merchantUpiId,
                merchantName,
                order.getNetAmount(),
                order.getOrderNumber()
        );

        model.addAttribute("order", order);
        model.addAttribute("payment", payment);
        model.addAttribute("merchantUpiId", merchantUpiId);
        model.addAttribute("merchantName", merchantName);
        model.addAttribute("upiUri", upiUri);
        return "orders/payment";
    }

    @PostMapping("/{orderNumber}/payment/submit-utr")
    public String submitUtr(
            @PathVariable String orderNumber,
            @RequestParam("utrNumber") String utrNumber,
            RedirectAttributes redirectAttributes
    ) {
        User user = userService.getCurrentAuthenticatedUser();
        Order order = orderService.getOrderByOrderNumber(orderNumber, user);

        try {
            paymentService.submitUpiTransaction(order, utrNumber, order.getNetAmount());
            redirectAttributes.addFlashAttribute("successMessage", "UPI Transaction reference (UTR: " + utrNumber.trim() + ") submitted successfully! Our team will verify and confirm your order.");
            return "redirect:/orders/confirmation/" + order.getOrderNumber();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/orders/" + orderNumber + "/payment";
        }
    }

    @GetMapping("/{orderNumber}")
    public String orderDetails(@PathVariable String orderNumber, Model model) {
        User user = userService.getCurrentAuthenticatedUser();
        Order order = orderService.getOrderByOrderNumber(orderNumber, user);
        List<OrderTimelineStepDto> timeline = orderService.getOrderTimeline(order);
        Payment payment = paymentService.getPaymentByOrder(order);

        model.addAttribute("order", order);
        model.addAttribute("timeline", timeline);
        model.addAttribute("payment", payment);
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
