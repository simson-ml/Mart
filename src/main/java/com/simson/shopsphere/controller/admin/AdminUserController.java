package com.simson.shopsphere.controller.admin;

import com.simson.shopsphere.entity.Role;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.exception.UnauthorizedAccessException;
import com.simson.shopsphere.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
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
        String adminEmail = getAuthenticatedAdminEmail();

        try {
            userService.toggleUserStatus(id, adminEmail);
            redirectAttributes.addFlashAttribute("successMessage", "User account status toggled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/users";
    }
}
