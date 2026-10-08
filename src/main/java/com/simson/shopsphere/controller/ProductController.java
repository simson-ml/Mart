package com.simson.shopsphere.controller;

import com.simson.shopsphere.dto.ReviewRequest;
import com.simson.shopsphere.entity.Product;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.service.ProductService;
import com.simson.shopsphere.service.ReviewService;
import com.simson.shopsphere.service.UserService;
import com.simson.shopsphere.service.WishlistService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    public ProductController(ProductService productService, ReviewService reviewService, UserService userService, WishlistService wishlistService) {
        this.productService = productService;
        this.reviewService = reviewService;
        this.userService = userService;
        this.wishlistService = wishlistService;
    }


    private final ProductService productService;
    private final ReviewService reviewService;
    private final UserService userService;
    private final WishlistService wishlistService;

    @GetMapping
    public String listProducts(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "category", required = false) String categorySlug,
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "minRating", required = false) BigDecimal minRating,
            @RequestParam(value = "inStockOnly", required = false) Boolean inStockOnly,
            @RequestParam(value = "minDiscount", required = false) BigDecimal minDiscount,
            @RequestParam(value = "sortBy", required = false, defaultValue = "newest") String sortBy,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "12") int size,
            Model model
    ) {
        Page<Product> productsPage = productService.getFilteredProducts(
                keyword, categorySlug, brand, minPrice, maxPrice, minRating, inStockOnly, minDiscount, sortBy, page, size
        );

        List<String> allBrands = productService.getAllBrands();

        model.addAttribute("productsPage", productsPage);
        model.addAttribute("brands", allBrands);
        model.addAttribute("selectedKeyword", keyword);
        model.addAttribute("selectedCategory", categorySlug);
        model.addAttribute("selectedBrand", brand);
        model.addAttribute("selectedMinPrice", minPrice);
        model.addAttribute("selectedMaxPrice", maxPrice);
        model.addAttribute("selectedMinRating", minRating);
        model.addAttribute("selectedInStockOnly", inStockOnly);
        model.addAttribute("selectedMinDiscount", minDiscount);
        model.addAttribute("selectedSortBy", sortBy);
        model.addAttribute("currentPage", page);

        return "products/list";
    }

    @GetMapping("/{slug}")
    public String productDetails(@PathVariable String slug, Model model) {
        Product product = productService.getProductBySlug(slug);
        User currentUser = userService.getCurrentAuthenticatedUser();

        List<Product> relatedProducts = productService.getRelatedProducts(product);
        List<Product> smartRecommendations = productService.getSmartRecommendations(product);
        boolean canReview = reviewService.canUserReview(currentUser, product);
        boolean inWishlist = wishlistService.isProductInWishlist(currentUser, product.getId());

        model.addAttribute("product", product);
        model.addAttribute("relatedProducts", relatedProducts);
        model.addAttribute("smartRecommendations", smartRecommendations);
        model.addAttribute("canReview", canReview);
        model.addAttribute("inWishlist", inWishlist);
        model.addAttribute("reviews", reviewService.getProductReviewsList(product));
        model.addAttribute("reviewRequest", ReviewRequest.builder().productId(product.getId()).rating(5).build());

        return "products/details";
    }
}
