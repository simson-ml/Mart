package com.simson.shopsphere.controller;

import com.simson.shopsphere.entity.Product;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.service.UserService;
import com.simson.shopsphere.service.WishlistService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/wishlist")
public class WishlistController {

    public WishlistController(WishlistService wishlistService, UserService userService) {
        this.wishlistService = wishlistService;
        this.userService = userService;
    }


    private final WishlistService wishlistService;
    private final UserService userService;

    @GetMapping
    public String viewWishlist(Model model) {
        User user = userService.getCurrentAuthenticatedUser();
        List<Product> products = wishlistService.getUserWishlistProducts(user);
        model.addAttribute("wishlistProducts", products);
        return "wishlist/wishlist";
    }

    @PostMapping("/add/{productId}")
    public String addToWishlist(@PathVariable Long productId, RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentAuthenticatedUser();
        wishlistService.addToWishlist(user, productId);
        redirectAttributes.addFlashAttribute("successMessage", "Product added to your wishlist!");
        return "redirect:/wishlist";
    }

    @PostMapping("/remove/{productId}")
    public String removeFromWishlist(@PathVariable Long productId, RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentAuthenticatedUser();
        wishlistService.removeFromWishlist(user, productId);
        redirectAttributes.addFlashAttribute("infoMessage", "Item removed from wishlist.");
        return "redirect:/wishlist";
    }

    @PostMapping("/move-to-cart/{productId}")
    public String moveToCart(@PathVariable Long productId, RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentAuthenticatedUser();
        wishlistService.moveToCart(user, productId);
        redirectAttributes.addFlashAttribute("successMessage", "Item moved to cart!");
        return "redirect:/cart";
    }
}
