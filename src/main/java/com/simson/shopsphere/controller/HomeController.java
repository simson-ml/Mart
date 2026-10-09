package com.simson.shopsphere.controller;

import com.simson.shopsphere.entity.Category;
import com.simson.shopsphere.entity.Product;
import com.simson.shopsphere.service.CategoryService;
import com.simson.shopsphere.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class HomeController {

    public HomeController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }


    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping("/")
    public String home(Model model) {
        List<Category> categories = categoryService.getAllActiveCategories();
        List<Product> flashDeals = productService.getFlashDeals();
        List<Product> featured = productService.getFeaturedProducts();
        List<Product> trending = productService.getTrendingProducts();
        List<Product> bestSellers = productService.getBestSellers();
        List<Product> newArrivals = productService.getNewArrivals();

        model.addAttribute("categories", categories);
        model.addAttribute("flashDeals", flashDeals);
        model.addAttribute("featuredProducts", featured);
        model.addAttribute("trendingProducts", trending);
        model.addAttribute("bestSellers", bestSellers);
        model.addAttribute("newArrivals", newArrivals);

        return "index";
    }

    @GetMapping("/categories/{slug}")
    public String categoryProducts(@PathVariable String slug) {
        return "redirect:/products?category=" + slug;
    }
}
