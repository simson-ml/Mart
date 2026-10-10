package com.simson.shopsphere.controller.admin;

import com.simson.shopsphere.dto.CategoryDto;
import com.simson.shopsphere.entity.Category;
import com.simson.shopsphere.entity.Role;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.exception.UnauthorizedAccessException;
import com.simson.shopsphere.service.CategoryService;
import com.simson.shopsphere.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/categories")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {

    private final CategoryService categoryService;
    private final UserService userService;

    public AdminCategoryController(CategoryService categoryService, UserService userService) {
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
    public String listCategories(Model model) {
        List<Category> categories = categoryService.getAllCategoriesAdmin();
        model.addAttribute("categories", categories);
        if (!model.containsAttribute("categoryDto")) {
            model.addAttribute("categoryDto", new CategoryDto());
        }
        return "admin/categories/list";
    }

    @PostMapping("/create")
    public String createCategory(
            @Valid @ModelAttribute("categoryDto") CategoryDto categoryDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        String adminEmail = getAuthenticatedAdminEmail();

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategoriesAdmin());
            return "admin/categories/list";
        }

        try {
            categoryService.createCategory(categoryDto, adminEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Category created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/categories";
    }

    @PostMapping("/update/{id}")
    public String updateCategory(
            @PathVariable Long id,
            @Valid @ModelAttribute("categoryDto") CategoryDto categoryDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        String adminEmail = getAuthenticatedAdminEmail();

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategoriesAdmin());
            return "admin/categories/list";
        }

        try {
            categoryService.updateCategory(id, categoryDto, adminEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Category updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/categories";
    }

    @PostMapping("/toggle/{id}")
    public String toggleCategoryStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String adminEmail = getAuthenticatedAdminEmail();

        categoryService.toggleCategoryStatus(id, adminEmail);
        redirectAttributes.addFlashAttribute("infoMessage", "Category status updated.");
        return "redirect:/admin/categories";
    }
}
