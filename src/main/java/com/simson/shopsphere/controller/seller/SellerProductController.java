package com.simson.shopsphere.controller.seller;

import com.simson.shopsphere.dto.ProductDto;
import com.simson.shopsphere.entity.Product;
import com.simson.shopsphere.entity.Role;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.exception.UnauthorizedAccessException;
import com.simson.shopsphere.service.CategoryService;
import com.simson.shopsphere.service.OrderService;
import com.simson.shopsphere.service.ProductService;
import com.simson.shopsphere.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/seller")
@PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
public class SellerProductController {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SellerProductController.class);

    private final ProductService productService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final OrderService orderService;

    public SellerProductController(ProductService productService,
                                   CategoryService categoryService,
                                   UserService userService,
                                   OrderService orderService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.orderService = orderService;
    }

    private User getAuthenticatedSeller() {
        User user = userService.getCurrentAuthenticatedUser();
        if (user == null || (user.getRole() != Role.SELLER && user.getRole() != Role.ADMIN)) {
            throw new UnauthorizedAccessException("Seller authentication required.");
        }
        return user;
    }

    @GetMapping({"", "/dashboard"})
    public String dashboard(Model model) {
        User seller = getAuthenticatedSeller();

        model.addAttribute("totalProducts", productService.getSellerProductCount(seller));
        model.addAttribute("activeProducts", productService.getSellerActiveProductCount(seller));
        model.addAttribute("lowStockCount", productService.getSellerLowStockCount(seller, 5));
        model.addAttribute("incomingOrderCount", orderService.getSellerOrderCount(seller));
        model.addAttribute("recentProducts", productService.getSellerProducts(seller, 0, 5).getContent());
        model.addAttribute("recentOrders", orderService.getSellerOrders(seller, PageRequest.of(0, 5)).getContent());
        model.addAttribute("sellerName", seller.getName());

        return "seller/dashboard";
    }

    @GetMapping("/products")
    public String listProducts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model
    ) {
        User seller = getAuthenticatedSeller();
        Page<Product> productsPage = productService.getSellerProducts(seller, page, size);

        model.addAttribute("productsPage", productsPage);
        model.addAttribute("currentPage", page);
        return "seller/products/list";
    }

    @GetMapping("/products/new")
    public String newProductForm(Model model) {
        if (!model.containsAttribute("productDto")) {
            model.addAttribute("productDto", ProductDto.builder().active(true).build());
        }
        model.addAttribute("categories", categoryService.getAllActiveCategories());
        model.addAttribute("isEdit", false);
        return "seller/products/form";
    }

    @PostMapping("/products/create")
    public String createProduct(
            @Valid @ModelAttribute("productDto") ProductDto productDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        User seller = getAuthenticatedSeller();

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllActiveCategories());
            model.addAttribute("isEdit", false);
            return "seller/products/form";
        }

        try {
            productService.createSellerProduct(productDto, seller);
            redirectAttributes.addFlashAttribute("successMessage", "Listing created successfully!");
            return "redirect:/seller/products";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("categories", categoryService.getAllActiveCategories());
            model.addAttribute("isEdit", false);
            return "seller/products/form";
        }
    }

    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        User seller = getAuthenticatedSeller();
        Product product = productService.getProductById(id);

        if (!seller.isAdmin() && (product.getSeller() == null || !product.getSeller().getId().equals(seller.getId()))) {
            throw new UnauthorizedAccessException("You are not authorized to edit this product.");
        }

        ProductDto dto = ProductDto.builder()
                .id(product.getId())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .brand(product.getBrand())
                .price(product.getPrice())
                .discountPercentage(product.getDiscountPercentage())
                .stockQuantity(product.getStockQuantity())
                .sku(product.getSku())
                .imageUrl(product.getImageUrl())
                .active(product.isActive())
                .build();

        model.addAttribute("productDto", dto);
        model.addAttribute("categories", categoryService.getAllActiveCategories());
        model.addAttribute("isEdit", true);
        return "seller/products/form";
    }

    @PostMapping("/products/update/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @Valid @ModelAttribute("productDto") ProductDto productDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        User seller = getAuthenticatedSeller();

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllActiveCategories());
            model.addAttribute("isEdit", true);
            return "seller/products/form";
        }

        try {
            productService.updateSellerProduct(id, productDto, seller);
            redirectAttributes.addFlashAttribute("successMessage", "Listing updated successfully!");
            return "redirect:/seller/products";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("categories", categoryService.getAllActiveCategories());
            model.addAttribute("isEdit", true);
            return "seller/products/form";
        }
    }

    @PostMapping("/products/stock/{id}")
    public String updateStock(
            @PathVariable Long id,
            @RequestParam("stockQuantity") int stockQuantity,
            RedirectAttributes redirectAttributes
    ) {
        User seller = getAuthenticatedSeller();
        productService.updateSellerStock(id, stockQuantity, seller);
        redirectAttributes.addFlashAttribute("successMessage", "Stock updated successfully.");
        return "redirect:/seller/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User seller = getAuthenticatedSeller();
        productService.deleteSellerProduct(id, seller);
        redirectAttributes.addFlashAttribute("successMessage", "Product listing removed or archived successfully.");
        return "redirect:/seller/products";
    }
}
