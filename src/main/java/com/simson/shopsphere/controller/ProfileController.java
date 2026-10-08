package com.simson.shopsphere.controller;

import com.simson.shopsphere.dto.PasswordChangeRequest;
import com.simson.shopsphere.dto.ProfileUpdateRequest;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/account")
public class ProfileController {

    public ProfileController(UserService userService) {
        this.userService = userService;
    }


    private final UserService userService;

    @GetMapping("/profile")
    public String profilePage(Model model) {
        User user = userService.getCurrentAuthenticatedUser();
        if (!model.containsAttribute("profileRequest")) {
            model.addAttribute("profileRequest", ProfileUpdateRequest.builder()
                    .name(user.getName())
                    .phone(user.getPhone())
                    .build());
        }
        if (!model.containsAttribute("passwordRequest")) {
            model.addAttribute("passwordRequest", new PasswordChangeRequest());
        }
        model.addAttribute("user", user);
        return "account/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @Valid @ModelAttribute("profileRequest") ProfileUpdateRequest profileRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        User user = userService.getCurrentAuthenticatedUser();
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("passwordRequest", new PasswordChangeRequest());
            return "account/profile";
        }

        userService.updateProfile(user, profileRequest);
        redirectAttributes.addFlashAttribute("successMessage", "Profile details updated successfully.");
        return "redirect:/account/profile";
    }

    @PostMapping("/password/change")
    public String changePassword(
            @Valid @ModelAttribute("passwordRequest") PasswordChangeRequest passwordRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        User user = userService.getCurrentAuthenticatedUser();
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("profileRequest", ProfileUpdateRequest.builder().name(user.getName()).phone(user.getPhone()).build());
            return "account/profile";
        }

        try {
            userService.changePassword(user, passwordRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/account/profile";
    }
}
