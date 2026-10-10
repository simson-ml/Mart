package com.simson.shopsphere.controller.admin;

import com.simson.shopsphere.dto.ProductDto;
import com.simson.shopsphere.entity.Product;
import com.simson.shopsphere.entity.Role;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.exception.UnauthorizedAccessException;
import com.simson.shopsphere.service.CategoryService;
import com.simson.shopsphere.service.ProductService;
import com.simson.shopsphere.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/products")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AdminProductController.class);

    private final ProductService productService;
    private final CategoryService categoryService;
    private final UserService userService;

    public AdminProductController(ProductService productService, CategoryService categoryService, UserService userService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    private String getAuthenticatedAdminEmail() {
        User currentAdmin = userService.getCurrentAuthenticatedUser();
        if (currentAdmin == null || currentAdmin.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("Administrative authentication required.");
        }
        return currentAdmin.getEmail();
    }

    @GetMapping
    public String listProducts(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "category", required = false) String categorySlug,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model
    ) {
        Page<Product> productsPage = productService.getFilteredProducts(
                keyword, categorySlug, null, null, null, null, null, null, "newest", page, size
        );

        model.addAttribute("productsPage", productsPage);
        model.addAttribute("categories", categoryService.getAllActiveCategories());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", categorySlug);
        model.addAttribute("currentPage", page);

        return "admin/products/list";
    }

    @GetMapping("/new")
    public String newProductForm(Model model) {
        if (!model.containsAttribute("productDto")) {
            model.addAttribute("productDto", ProductDto.builder().active(true).build());
        }
        model.addAttribute("categories", categoryService.getAllActiveCategories());
        model.addAttribute("isEdit", false);
        return "admin/products/form";
    }

    @PostMapping("/create")
    public String createProduct(
            @Valid @ModelAttribute("productDto") ProductDto productDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllActiveCategories());
            model.addAttribute("isEdit", false);
            return "admin/products/form";
        }

        String adminEmail = getAuthenticatedAdminEmail();

        try {
            productService.createProduct(productDto, adminEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Product created successfully!");
            return "redirect:/admin/products";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("categories", categoryService.getAllActiveCategories());
            model.addAttribute("isEdit", false);
            return "admin/products/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        ProductDto dto = ProductDto.builder()
                .id(product.getId())
                .categoryId(product.getCategory().getId())
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
        return "admin/products/form";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @Valid @ModelAttribute("productDto") ProductDto productDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllActiveCategories());
            model.addAttribute("isEdit", true);
            return "admin/products/form";
        }

        String adminEmail = getAuthenticatedAdminEmail();

        try {
            productService.updateProduct(id, productDto, adminEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Product updated successfully!");
            return "redirect:/admin/products";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("categories", categoryService.getAllActiveCategories());
            model.addAttribute("isEdit", true);
            return "admin/products/form";
        }
    }

    @PostMapping("/stock/{id}")
    public String updateStock(
            @PathVariable Long id,
            @RequestParam("stockQuantity") int stockQuantity,
            RedirectAttributes redirectAttributes
    ) {
        String adminEmail = getAuthenticatedAdminEmail();

        productService.updateStock(id, stockQuantity, adminEmail);
        redirectAttributes.addFlashAttribute("successMessage", "Inventory updated successfully.");
        return "redirect:/admin/products";
    }

    @PostMapping("/toggle/{id}")
    public String toggleProductStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String adminEmail = getAuthenticatedAdminEmail();

        productService.toggleProductStatus(id, adminEmail);
        redirectAttributes.addFlashAttribute("infoMessage", "Product status updated.");
        return "redirect:/admin/products";
    }
}
