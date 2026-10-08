package com.simson.shopsphere.controller;

import com.simson.shopsphere.dto.RegisterRequest;
import com.simson.shopsphere.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final com.simson.shopsphere.service.PasswordResetService passwordResetService;

    public AuthController(UserService userService, com.simson.shopsphere.service.PasswordResetService passwordResetService) {
        this.userService = userService;
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model
    ) {
        if (userService.getCurrentAuthenticatedUser() != null) {
            return "redirect:/";
        }
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid email or password. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully.");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        if (userService.getCurrentAuthenticatedUser() != null) {
            return "redirect:/";
        }
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegistration(
            @Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            userService.registerUser(registerRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please login with your credentials.");
            return "redirect:/login";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordForm() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(
            @RequestParam("email") String email,
            RedirectAttributes redirectAttributes
    ) {
        String token = passwordResetService.createPasswordResetToken(email);
        if (token != null) {
            log.info("Simulated Password Reset URL for {}: /reset-password?token={}", email, token);
            redirectAttributes.addFlashAttribute("infoMessage",
                    "Password reset token generated! In this demo environment, you can use this direct link: /reset-password?token=" + token);
        } else {
            redirectAttributes.addFlashAttribute("infoMessage",
                    "If an account is associated with " + email + ", reset instructions have been generated.");
        }
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam(value = "token", required = false) String token,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        if (token == null || passwordResetService.validatePasswordResetToken(token).isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "The password reset link is invalid or has expired. Please request a new one.");
            return "redirect:/forgot-password";
        }
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(
            @RequestParam("token") String token,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("token", token);
            model.addAttribute("errorMessage", "Passwords do not match. Please try again.");
            return "auth/reset-password";
        }

        try {
            passwordResetService.resetPassword(token, password);
            redirectAttributes.addFlashAttribute("successMessage", "Your password has been successfully reset. Please log in with your new password.");
            return "redirect:/login";
        } catch (Exception ex) {
            model.addAttribute("token", token);
            model.addAttribute("errorMessage", ex.getMessage());
            return "auth/reset-password";
        }
    }
}
