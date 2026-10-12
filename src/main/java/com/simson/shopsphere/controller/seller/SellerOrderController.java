package com.simson.shopsphere.controller.seller;

import com.simson.shopsphere.entity.Order;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/seller/orders")
@PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
public class SellerOrderController {

    private final OrderService orderService;
    private final UserService userService;

    public SellerOrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    private User getAuthenticatedSeller() {
        User user = userService.getCurrentAuthenticatedUser();
        if (user == null || (user.getRole() != Role.SELLER && user.getRole() != Role.ADMIN)) {
            throw new UnauthorizedAccessException("Seller authentication required.");
        }
        return user;
    }

    @GetMapping
    public String listOrders(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model
    ) {
        User seller = getAuthenticatedSeller();
        Page<Order> ordersPage = orderService.getSellerOrders(seller, PageRequest.of(Math.max(0, page), Math.max(1, size)));

        model.addAttribute("ordersPage", ordersPage);
        model.addAttribute("currentPage", page);
        return "seller/orders/list";
    }

    @GetMapping("/{orderNumber}")
    public String orderDetails(@PathVariable String orderNumber, Model model) {
        User seller = getAuthenticatedSeller();
        Order order = orderService.getSellerOrderDetails(orderNumber, seller);

        model.addAttribute("order", order);
        model.addAttribute("currentSeller", seller);
        return "seller/orders/details";
    }
}
