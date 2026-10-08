package com.simson.shopsphere.service;

import com.simson.shopsphere.dto.PasswordChangeRequest;
import com.simson.shopsphere.dto.ProfileUpdateRequest;
import com.simson.shopsphere.dto.RegisterRequest;
import com.simson.shopsphere.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    User registerUser(RegisterRequest request);
    User getCurrentAuthenticatedUser();
    Optional<User> findByEmail(String email);
    User findById(Long id);
    User updateProfile(User user, ProfileUpdateRequest request);
    void changePassword(User user, PasswordChangeRequest request);
    Page<User> getAllUsers(String keyword, Pageable pageable);
    void toggleUserStatus(Long userId, String adminEmail);
    long getTotalUserCount();
}
