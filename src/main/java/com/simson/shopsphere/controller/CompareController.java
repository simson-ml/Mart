package com.simson.shopsphere.controller;

import com.simson.shopsphere.dto.ApiResponse;
import com.simson.shopsphere.entity.Product;
import com.simson.shopsphere.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CompareController {

    private static final String COMPARE_SESSION_KEY = "COMPARE_PRODUCT_IDS";
    private static final int MAX_COMPARE_ITEMS = 4;

    private final ProductService productService;

    public CompareController(ProductService productService) {
        this.productService = productService;
    }

    @SuppressWarnings("unchecked")
    private List<Long> getCompareList(HttpSession session) {
        List<Long> list = (List<Long>) session.getAttribute(COMPARE_SESSION_KEY);
        if (list == null) {
            list = new ArrayList<>();
            session.setAttribute(COMPARE_SESSION_KEY, list);
        }
        return list;
    }

    @GetMapping("/compare")
    public String comparePage(HttpSession session, Model model) {
        List<Long> productIds = getCompareList(session);
        List<Product> products = new ArrayList<>();
        for (Long id : productIds) {
            try {
                products.add(productService.getProductById(id));
            } catch (Exception ignored) {
            }
        }
        model.addAttribute("products", products);
        return "products/compare";
    }

    @PostMapping("/api/compare/add/{productId}")
    @ResponseBody
    public ApiResponse<Integer> addToCompare(@PathVariable Long productId, HttpSession session) {
        List<Long> list = getCompareList(session);
        if (list.contains(productId)) {
            return ApiResponse.ok("Product is already in comparison list", list.size());
        }
        if (list.size() >= MAX_COMPARE_ITEMS) {
            return ApiResponse.error("You can compare up to " + MAX_COMPARE_ITEMS + " products at once.");
        }
        list.add(productId);
        session.setAttribute(COMPARE_SESSION_KEY, list);
        return ApiResponse.ok("Added to comparison list", list.size());
    }

    @PostMapping("/api/compare/remove/{productId}")
    @ResponseBody
    public ApiResponse<Integer> removeFromCompare(@PathVariable Long productId, HttpSession session) {
        List<Long> list = getCompareList(session);
        list.remove(productId);
        session.setAttribute(COMPARE_SESSION_KEY, list);
        return ApiResponse.ok("Removed from comparison", list.size());
    }

    @PostMapping("/compare/clear")
    public String clearCompare(HttpSession session, RedirectAttributes redirectAttributes) {
        session.removeAttribute(COMPARE_SESSION_KEY);
        redirectAttributes.addFlashAttribute("successMessage", "Comparison list cleared.");
        return "redirect:/compare";
    }
}
