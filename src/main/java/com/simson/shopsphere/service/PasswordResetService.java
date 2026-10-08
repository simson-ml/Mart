package com.simson.shopsphere.service;

import com.simson.shopsphere.entity.User;
import java.util.Optional;

public interface PasswordResetService {

    String createPasswordResetToken(String email);

    Optional<User> validatePasswordResetToken(String token);

    void resetPassword(String token, String newPassword);

    void cleanupExpiredTokens();
}
