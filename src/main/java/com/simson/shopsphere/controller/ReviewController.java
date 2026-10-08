package com.simson.shopsphere.controller;

import com.simson.shopsphere.dto.ReviewRequest;
import com.simson.shopsphere.entity.Product;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.service.ProductService;
import com.simson.shopsphere.service.ReviewService;
import com.simson.shopsphere.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    public ReviewController(ReviewService reviewService, ProductService productService, UserService userService) {
        this.reviewService = reviewService;
        this.productService = productService;
        this.userService = userService;
    }


    private final ReviewService reviewService;
    private final ProductService productService;
    private final UserService userService;

    @PostMapping("/add")
    public String addReview(
            @Valid @ModelAttribute("reviewRequest") ReviewRequest reviewRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        User user = userService.getCurrentAuthenticatedUser();
        Product product = productService.getProductById(reviewRequest.getProductId());

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please provide a valid rating and review comment.");
            return "redirect:/products/" + product.getSlug();
        }

        try {
            reviewService.submitReview(user, reviewRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Thank you! Your verified review has been published.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/products/" + product.getSlug();
    }

    @PostMapping("/delete/{id}")
    public String deleteReview(@PathVariable Long id, @RequestParam("productSlug") String productSlug, RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentAuthenticatedUser();
        reviewService.deleteReview(id, user);
        redirectAttributes.addFlashAttribute("infoMessage", "Review removed.");
        return "redirect:/products/" + productSlug;
    }
}
