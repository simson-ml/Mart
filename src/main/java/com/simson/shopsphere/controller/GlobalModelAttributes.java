package com.simson.shopsphere.controller;

import com.simson.shopsphere.entity.Category;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.service.CartService;
import com.simson.shopsphere.service.CategoryService;
import com.simson.shopsphere.service.UserService;
import com.simson.shopsphere.service.WishlistService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalModelAttributes {

    public GlobalModelAttributes(CategoryService categoryService, UserService userService, CartService cartService, WishlistService wishlistService) {
        this.categoryService = categoryService;
        this.userService = userService;
        this.cartService = cartService;
        this.wishlistService = wishlistService;
    }


    private final CategoryService categoryService;
    private final UserService userService;
    private final CartService cartService;
    private final WishlistService wishlistService;

    @ModelAttribute("allCategories")
    public List<Category> populateCategories() {
        return categoryService.getAllActiveCategories();
    }

    @ModelAttribute("currentUser")
    public User populateCurrentUser() {
        return userService.getCurrentAuthenticatedUser();
    }

    @ModelAttribute("cartCount")
    public int populateCartCount(HttpServletRequest request) {
        User user = userService.getCurrentAuthenticatedUser();
        if (user != null) {
            return cartService.getCartItemCount(user);
        }
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        if (session != null) {
            @SuppressWarnings("unchecked")
            java.util.Map<Long, Integer> guestCart = (java.util.Map<Long, Integer>) session.getAttribute(CartController.SESSION_GUEST_CART);
            if (guestCart != null) {
                return guestCart.values().stream().mapToInt(Integer::intValue).sum();
            }
        }
        return 0;
    }

    @ModelAttribute("wishlistCount")
    public int populateWishlistCount() {
        User user = userService.getCurrentAuthenticatedUser();
        return wishlistService.getWishlistItemCount(user);
    }

    @ModelAttribute("currentUri")
    public String populateCurrentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
