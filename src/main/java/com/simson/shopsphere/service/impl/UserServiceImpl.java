package com.simson.shopsphere.service.impl;

import com.simson.shopsphere.dto.PasswordChangeRequest;
import com.simson.shopsphere.dto.ProfileUpdateRequest;
import com.simson.shopsphere.dto.RegisterRequest;
import com.simson.shopsphere.entity.Cart;
import com.simson.shopsphere.entity.Role;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.entity.Wishlist;
import com.simson.shopsphere.exception.BadRequestException;
import com.simson.shopsphere.exception.DuplicateResourceException;
import com.simson.shopsphere.exception.ResourceNotFoundException;
import com.simson.shopsphere.repository.CartRepository;
import com.simson.shopsphere.repository.UserRepository;
import com.simson.shopsphere.repository.WishlistRepository;
import com.simson.shopsphere.service.AuditLogService;
import com.simson.shopsphere.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository, CartRepository cartRepository, WishlistRepository wishlistRepository, PasswordEncoder passwordEncoder, AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.wishlistRepository = wishlistRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }


    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final WishlistRepository wishlistRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public User registerUser(RegisterRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateResourceException("An account with this email address already exists.");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Password and confirm password do not match.");
        }

        User user = User.builder()
                .name(request.getName().trim())
                .email(normalizedEmail)
                .phone(request.getPhone() != null ? request.getPhone().trim() : null)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER) // Always enforce USER role from registration
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);

        // Initialize user cart and wishlist
        Cart cart = Cart.builder().user(savedUser).build();
        cartRepository.save(cart);

        Wishlist wishlist = Wishlist.builder().user(savedUser).build();
        wishlistRepository.save(wishlist);

        log.info("New user registered successfully: id={}, email={}", savedUser.getId(), savedUser.getEmail());
        return savedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return userRepository.findByEmail(auth.getName().trim().toLowerCase())
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase());
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    @Transactional
    public User updateProfile(User user, ProfileUpdateRequest request) {
        user.setName(request.getName().trim());
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone().trim());
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(User user, PasswordChangeRequest request) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect.");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("New password and confirm password do not match.");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed for user id={}", user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> getAllUsers(String keyword, Pageable pageable) {
        return userRepository.searchUsers(keyword, pageable);
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long userId, String adminEmail) {
        User user = findById(userId);
        if (user.isAdmin()) {
            throw new BadRequestException("Cannot disable administrator accounts.");
        }
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
        auditLogService.log(adminEmail, "USER_STATUS_TOGGLE", "User", String.valueOf(user.getId()),
                "User " + user.getEmail() + " enabled=" + user.isEnabled(), "127.0.0.1");
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalUserCount() {
        return userRepository.count();
    }
}
