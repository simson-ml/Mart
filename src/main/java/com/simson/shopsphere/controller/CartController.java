package com.simson.shopsphere.controller;

import com.simson.shopsphere.dto.CartDto;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.service.CartService;
import com.simson.shopsphere.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CartController.class);

    private final CartService cartService;
    private final UserService userService;

    public static final String SESSION_GUEST_CART = "SESSION_GUEST_CART";
    private static final String APPLIED_COUPON_SESSION_KEY = "APPLIED_COUPON_CODE";

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @SuppressWarnings("unchecked")
    private Map<Long, Integer> getSessionGuestCart(HttpSession session) {
        Map<Long, Integer> guestCart = (Map<Long, Integer>) session.getAttribute(SESSION_GUEST_CART);
        if (guestCart == null) {
            guestCart = new HashMap<>();
            session.setAttribute(SESSION_GUEST_CART, guestCart);
        }
        return guestCart;
    }

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        User user = userService.getCurrentAuthenticatedUser();
        String appliedCoupon = (String) session.getAttribute(APPLIED_COUPON_SESSION_KEY);
        CartDto cartDto;
        if (user != null) {
            cartDto = cartService.getCartDto(user, appliedCoupon);
        } else {
            Map<Long, Integer> guestCart = getSessionGuestCart(session);
            cartDto = cartService.getGuestCartDto(guestCart, appliedCoupon);
        }

        model.addAttribute("cart", cartDto);
        return "cart/cart";
    }

    @PostMapping("/add")
    public String addToCart(
            @RequestParam("productId") Long productId,
            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
            @RequestParam(value = "buyNow", required = false) Boolean buyNow,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        User user = userService.getCurrentAuthenticatedUser();
        try {
            if (user != null) {
                cartService.addToCart(user, productId, quantity);
            } else {
                Map<Long, Integer> guestCart = getSessionGuestCart(session);
                guestCart.put(productId, guestCart.getOrDefault(productId, 0) + quantity);
            }

            if (Boolean.TRUE.equals(buyNow)) {
                return "redirect:/checkout";
            }

            redirectAttributes.addFlashAttribute("successMessage", "Item added to cart successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateQuantity(
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") int quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        User user = userService.getCurrentAuthenticatedUser();
        try {
            if (user != null) {
                cartService.updateQuantity(user, productId, quantity);
            } else {
                Map<Long, Integer> guestCart = getSessionGuestCart(session);
                if (quantity <= 0) {
                    guestCart.remove(productId);
                } else {
                    guestCart.put(productId, quantity);
                }
            }
            redirectAttributes.addFlashAttribute("infoMessage", "Cart updated successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(
            @RequestParam("productId") Long productId,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        User user = userService.getCurrentAuthenticatedUser();
        if (user != null) {
            cartService.removeFromCart(user, productId);
        } else {
            Map<Long, Integer> guestCart = getSessionGuestCart(session);
            guestCart.remove(productId);
        }
        redirectAttributes.addFlashAttribute("infoMessage", "Item removed from cart.");
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(HttpSession session, RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentAuthenticatedUser();
        if (user != null) {
            cartService.clearCart(user);
        } else {
            session.removeAttribute(SESSION_GUEST_CART);
        }
        redirectAttributes.addFlashAttribute("infoMessage", "Your cart has been cleared.");
        return "redirect:/cart";
    }

    @PostMapping("/apply-coupon")
    public String applyCoupon(
            @RequestParam("couponCode") String couponCode,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        if (couponCode == null || couponCode.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please enter a coupon code.");
            return "redirect:/cart";
        }

        User user = userService.getCurrentAuthenticatedUser();
        try {
            CartDto testDto;
            if (user != null) {
                testDto = cartService.getCartDto(user, couponCode.trim());
            } else {
                Map<Long, Integer> guestCart = getSessionGuestCart(session);
                testDto = cartService.getGuestCartDto(guestCart, couponCode.trim());
            }

            if (testDto.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
                session.setAttribute(APPLIED_COUPON_SESSION_KEY, couponCode.trim().toUpperCase());
                redirectAttributes.addFlashAttribute("successMessage", "Coupon '" + couponCode.trim().toUpperCase() + "' applied! You saved ₹" + testDto.getDiscountAmount());
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Coupon could not be applied to this order.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/cart";
    }

    @PostMapping("/remove-coupon")
    public String removeCoupon(HttpSession session, RedirectAttributes redirectAttributes) {
        session.removeAttribute(APPLIED_COUPON_SESSION_KEY);
        redirectAttributes.addFlashAttribute("infoMessage", "Coupon removed.");
        return "redirect:/cart";
    }
}
