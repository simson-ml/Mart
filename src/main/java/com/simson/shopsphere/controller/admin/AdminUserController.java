package com.simson.shopsphere.controller.admin;

import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }


    private final UserService userService;

    @GetMapping
    public String listUsers(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model
    ) {
        Page<User> usersPage = userService.getAllUsers(keyword, PageRequest.of(Math.max(0, page), size, Sort.by("createdAt").descending()));

        model.addAttribute("usersPage", usersPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);

        return "admin/users/list";
    }

    @PostMapping("/{id}/toggle")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User currentAdmin = userService.getCurrentAuthenticatedUser();
        String adminEmail = currentAdmin != null ? currentAdmin.getEmail() : "admin@shopsphere.com";

        try {
            userService.toggleUserStatus(id, adminEmail);
            redirectAttributes.addFlashAttribute("successMessage", "User account status toggled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/users";
    }
}
