package com.simson.shopsphere.controller.api;

import com.simson.shopsphere.controller.CartController;
import com.simson.shopsphere.dto.ApiResponse;
import com.simson.shopsphere.dto.CartDto;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.service.CartService;
import com.simson.shopsphere.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart API", description = "Endpoints for managing user shopping cart and quantity updates")
public class ApiCartController {

    public ApiCartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }


    private final CartService cartService;
    private final UserService userService;

    @SuppressWarnings("unchecked")
    private java.util.Map<Long, Integer> getSessionGuestCart(jakarta.servlet.http.HttpSession session) {
        java.util.Map<Long, Integer> guestCart = (java.util.Map<Long, Integer>) session.getAttribute(CartController.SESSION_GUEST_CART);
        if (guestCart == null) {
            guestCart = new java.util.HashMap<>();
            session.setAttribute(CartController.SESSION_GUEST_CART, guestCart);
        }
        return guestCart;
    }

    @GetMapping
    @Operation(summary = "Get current user cart snapshot")
    public ResponseEntity<ApiResponse<CartDto>> getCart(@RequestParam(required = false) String couponCode,
                                                       jakarta.servlet.http.HttpSession session) {
        User user = userService.getCurrentAuthenticatedUser();
        CartDto cartDto;
        if (user != null) {
            cartDto = cartService.getCartDto(user, couponCode);
        } else {
            java.util.Map<Long, Integer> guestCart = getSessionGuestCart(session);
            cartDto = cartService.getGuestCartDto(guestCart, couponCode);
        }
        return ResponseEntity.ok(ApiResponse.ok("Cart fetched", cartDto));
    }

    @PostMapping("/add")
    @Operation(summary = "Add a product item to cart")
    public ResponseEntity<ApiResponse<Integer>> addToCart(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity,
            jakarta.servlet.http.HttpSession session
    ) {
        User user = userService.getCurrentAuthenticatedUser();
        int totalItems;
        if (user != null) {
            cartService.addToCart(user, productId, quantity);
            totalItems = cartService.getCartItemCount(user);
        } else {
            java.util.Map<Long, Integer> guestCart = getSessionGuestCart(session);
            guestCart.put(productId, guestCart.getOrDefault(productId, 0) + quantity);
            totalItems = guestCart.values().stream().mapToInt(Integer::intValue).sum();
        }
        return ResponseEntity.ok(ApiResponse.ok("Product added to cart", totalItems));
    }

    @PostMapping("/update")
    @Operation(summary = "Update quantity of a product in cart")
    public ResponseEntity<ApiResponse<CartDto>> updateQuantity(
            @RequestParam Long productId,
            @RequestParam int quantity,
            @RequestParam(required = false) String couponCode,
            jakarta.servlet.http.HttpSession session
    ) {
        User user = userService.getCurrentAuthenticatedUser();
        CartDto cartDto;
        if (user != null) {
            cartService.updateQuantity(user, productId, quantity);
            cartDto = cartService.getCartDto(user, couponCode);
        } else {
            java.util.Map<Long, Integer> guestCart = getSessionGuestCart(session);
            if (quantity <= 0) {
                guestCart.remove(productId);
            } else {
                guestCart.put(productId, quantity);
            }
            cartDto = cartService.getGuestCartDto(guestCart, couponCode);
        }
        return ResponseEntity.ok(ApiResponse.ok("Cart item updated", cartDto));
    }

    @DeleteMapping("/remove/{productId}")
    @Operation(summary = "Remove an item from cart")
    public ResponseEntity<ApiResponse<CartDto>> removeItem(
            @PathVariable Long productId,
            @RequestParam(required = false) String couponCode,
            jakarta.servlet.http.HttpSession session
    ) {
        User user = userService.getCurrentAuthenticatedUser();
        CartDto cartDto;
        if (user != null) {
            cartService.removeFromCart(user, productId);
            cartDto = cartService.getCartDto(user, couponCode);
        } else {
            java.util.Map<Long, Integer> guestCart = getSessionGuestCart(session);
            guestCart.remove(productId);
            cartDto = cartService.getGuestCartDto(guestCart, couponCode);
        }
        return ResponseEntity.ok(ApiResponse.ok("Item removed from cart", cartDto));
    }

    @GetMapping("/count")
    @Operation(summary = "Get total count of items in cart")
    public ResponseEntity<ApiResponse<Integer>> getCartCount(jakarta.servlet.http.HttpSession session) {
        User user = userService.getCurrentAuthenticatedUser();
        int count;
        if (user != null) {
            count = cartService.getCartItemCount(user);
        } else {
            java.util.Map<Long, Integer> guestCart = (java.util.Map<Long, Integer>) session.getAttribute(CartController.SESSION_GUEST_CART);
            count = guestCart != null ? guestCart.values().stream().mapToInt(Integer::intValue).sum() : 0;
        }
        return ResponseEntity.ok(ApiResponse.ok("Cart count retrieved", count));
    }
}
