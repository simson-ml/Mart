package com.simson.shopsphere.controller;

import com.simson.shopsphere.dto.AddressDto;
import com.simson.shopsphere.dto.CartDto;
import com.simson.shopsphere.dto.CheckoutRequest;
import com.simson.shopsphere.entity.Address;
import com.simson.shopsphere.entity.Order;
import com.simson.shopsphere.entity.PaymentMethod;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.service.AddressService;
import com.simson.shopsphere.service.CartService;
import com.simson.shopsphere.service.OrderService;
import com.simson.shopsphere.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
public class CheckoutController {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CheckoutController.class);

    private final CartService cartService;
    private final AddressService addressService;
    private final OrderService orderService;
    private final UserService userService;

    public CheckoutController(CartService cartService, AddressService addressService, OrderService orderService, UserService userService) {
        this.cartService = cartService;
        this.addressService = addressService;
        this.orderService = orderService;
        this.userService = userService;
    }

    private static final String APPLIED_COUPON_SESSION_KEY = "APPLIED_COUPON_CODE";
    private static final String CHECKOUT_IDEMPOTENCY_TOKEN = "CHECKOUT_IDEMPOTENCY_TOKEN";

    @GetMapping("/checkout")
    public String checkoutPage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentAuthenticatedUser();
        String couponCode = (String) session.getAttribute(APPLIED_COUPON_SESSION_KEY);
        CartDto cart = cartService.getCartDto(user, couponCode);

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your cart is empty. Add products before proceeding to checkout.");
            return "redirect:/cart";
        }

        List<Address> addresses = addressService.getUserAddresses(user);
        Address defaultAddress = addressService.getDefaultAddress(user);

        CheckoutRequest checkoutRequest = CheckoutRequest.builder()
                .addressId(defaultAddress != null ? defaultAddress.getId() : null)
                .paymentMethod(PaymentMethod.COD)
                .couponCode(couponCode)
                .build();

        String idempotencyToken = UUID.randomUUID().toString();
        session.setAttribute(CHECKOUT_IDEMPOTENCY_TOKEN, idempotencyToken);

        model.addAttribute("cart", cart);
        model.addAttribute("addresses", addresses);
        model.addAttribute("checkoutRequest", checkoutRequest);
        model.addAttribute("newAddressDto", new AddressDto());
        model.addAttribute("idempotencyToken", idempotencyToken);

        return "checkout/checkout";
    }

    @PostMapping("/checkout/place-order")
    public String placeOrder(
            @Valid @ModelAttribute("checkoutRequest") CheckoutRequest checkoutRequest,
            BindingResult bindingResult,
            @RequestParam(value = "idempotencyToken", required = false) String idempotencyToken,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        User user = userService.getCurrentAuthenticatedUser();

        // Idempotency token validation to prevent double-submit
        String validToken = (String) session.getAttribute(CHECKOUT_IDEMPOTENCY_TOKEN);
        if (validToken == null || !validToken.equals(idempotencyToken)) {
            log.warn("Duplicate or invalid checkout attempt detected for user: {}", user.getEmail());
            redirectAttributes.addFlashAttribute("errorMessage", "Duplicate submission detected. Please check your order history.");
            return "redirect:/orders/history";
        }

        String couponCode = (String) session.getAttribute(APPLIED_COUPON_SESSION_KEY);
        checkoutRequest.setCouponCode(couponCode);

        if (bindingResult.hasErrors()) {
            CartDto cart = cartService.getCartDto(user, couponCode);
            model.addAttribute("cart", cart);
            model.addAttribute("addresses", addressService.getUserAddresses(user));
            model.addAttribute("newAddressDto", new AddressDto());
            model.addAttribute("idempotencyToken", validToken);
            return "checkout/checkout";
        }

        try {
            // Invalidate token before execution
            session.removeAttribute(CHECKOUT_IDEMPOTENCY_TOKEN);
            Order order = orderService.placeOrder(user, checkoutRequest);
            session.removeAttribute(APPLIED_COUPON_SESSION_KEY);
            return "redirect:/orders/confirmation/" + order.getOrderNumber();
        } catch (Exception e) {
            log.error("Failed to place order for user: {}", user.getEmail(), e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/checkout";
        }
    }

    @PostMapping("/checkout/address/save-and-select")
    public String saveAndSelectAddress(
            @Valid @ModelAttribute("newAddressDto") AddressDto addressDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        User user = userService.getCurrentAuthenticatedUser();
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please fix address form errors.");
            return "redirect:/checkout";
        }
        addressService.createAddress(user, addressDto);
        redirectAttributes.addFlashAttribute("successMessage", "Address added successfully.");
        return "redirect:/checkout";
    }
}
