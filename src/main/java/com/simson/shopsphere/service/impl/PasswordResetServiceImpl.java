package com.simson.shopsphere.service.impl;

import com.simson.shopsphere.entity.PasswordResetToken;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.exception.BadRequestException;
import com.simson.shopsphere.repository.PasswordResetTokenRepository;
import com.simson.shopsphere.repository.UserRepository;
import com.simson.shopsphere.service.PasswordResetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetServiceImpl.class);
    private static final int TOKEN_EXPIRY_MINUTES = 15;

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordResetServiceImpl(PasswordResetTokenRepository tokenRepository,
                                    UserRepository userRepository,
                                    PasswordEncoder passwordEncoder) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String createPasswordResetToken(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email.trim());
        if (userOpt.isEmpty()) {
            log.warn("Password reset requested for non-existent email: {}", email);
            return null;
        }

        User user = userOpt.get();

        // Invalidate prior unused tokens for this user
        tokenRepository.invalidateExistingTokensForUser(user);

        // Generate cryptographically strong random token
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(TOKEN_EXPIRY_MINUTES);
        PasswordResetToken resetToken = new PasswordResetToken(token, user, expiryDate);
        tokenRepository.save(resetToken);

        log.info("Password reset token generated for user: {} (expires in {} mins)", user.getEmail(), TOKEN_EXPIRY_MINUTES);
        return token;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> validatePasswordResetToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return Optional.empty();
        }

        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            return Optional.empty();
        }

        PasswordResetToken resetToken = tokenOpt.get();
        if (!resetToken.isValid()) {
            return Optional.empty();
        }

        return Optional.of(resetToken.getUser());
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            throw new BadRequestException("Password must be at least 6 characters long.");
        }

        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid or expired password reset link."));

        if (!resetToken.isValid()) {
            throw new BadRequestException("This password reset link has expired or has already been used.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        tokenRepository.invalidateExistingTokensForUser(user);
        log.info("Password successfully reset for user: {}", user.getEmail());
    }

    @Override
    @Scheduled(cron = "0 0 * * * *") // Run hourly
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}
