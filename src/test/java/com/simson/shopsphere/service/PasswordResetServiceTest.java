package com.simson.shopsphere.service;

import com.simson.shopsphere.entity.PasswordResetToken;
import com.simson.shopsphere.entity.Role;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.exception.BadRequestException;
import com.simson.shopsphere.repository.PasswordResetTokenRepository;
import com.simson.shopsphere.repository.UserRepository;
import com.simson.shopsphere.service.impl.PasswordResetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Password Reset Service Unit Tests")
class PasswordResetServiceTest {

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private PasswordResetServiceImpl passwordResetService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        passwordResetService = new PasswordResetServiceImpl(tokenRepository, userRepository, passwordEncoder);

        sampleUser = User.builder()
                .id(1L)
                .name("Simson Test")
                .email("test@shopsphere.com")
                .password("encoded_old_password")
                .role(Role.USER)
                .enabled(true)
                .build();
    }

    @Test
    void shouldCreatePasswordResetTokenForValidUser() {
        when(userRepository.findByEmailIgnoreCase("test@shopsphere.com")).thenReturn(Optional.of(sampleUser));

        String token = passwordResetService.createPasswordResetToken("test@shopsphere.com");

        assertThat(token).isNotBlank();
        verify(tokenRepository).invalidateExistingTokensForUser(sampleUser);

        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokenRepository).save(tokenCaptor.capture());

        PasswordResetToken saved = tokenCaptor.getValue();
        assertThat(saved.getToken()).isEqualTo(token);
        assertThat(saved.getUser()).isEqualTo(sampleUser);
        assertThat(saved.isUsed()).isFalse();
        assertThat(saved.getExpiryDate()).isAfter(LocalDateTime.now());
    }

    @Test
    void shouldReturnNullWhenUserNotFound() {
        when(userRepository.findByEmailIgnoreCase("unknown@example.com")).thenReturn(Optional.empty());

        String token = passwordResetService.createPasswordResetToken("unknown@example.com");

        assertThat(token).isNull();
        verify(tokenRepository, never()).save(any());
    }

    @Test
    void shouldValidateValidToken() {
        PasswordResetToken token = new PasswordResetToken("valid-token-123", sampleUser, LocalDateTime.now().plusMinutes(10));
        when(tokenRepository.findByToken("valid-token-123")).thenReturn(Optional.of(token));

        Optional<User> userOpt = passwordResetService.validatePasswordResetToken("valid-token-123");

        assertThat(userOpt).isPresent();
        assertThat(userOpt.get().getEmail()).isEqualTo("test@shopsphere.com");
    }

    @Test
    void shouldRejectExpiredToken() {
        PasswordResetToken token = new PasswordResetToken("expired-token", sampleUser, LocalDateTime.now().minusMinutes(5));
        when(tokenRepository.findByToken("expired-token")).thenReturn(Optional.of(token));

        Optional<User> userOpt = passwordResetService.validatePasswordResetToken("expired-token");

        assertThat(userOpt).isEmpty();
    }

    @Test
    void shouldResetPasswordSuccessfully() {
        PasswordResetToken token = new PasswordResetToken("reset-token-xyz", sampleUser, LocalDateTime.now().plusMinutes(10));
        when(tokenRepository.findByToken("reset-token-xyz")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("NewSecret123!")).thenReturn("encoded_new_password");

        passwordResetService.resetPassword("reset-token-xyz", "NewSecret123!");

        assertThat(sampleUser.getPassword()).isEqualTo("encoded_new_password");
        assertThat(token.isUsed()).isTrue();
        verify(userRepository).save(sampleUser);
        verify(tokenRepository).save(token);
    }

    @Test
    void shouldThrowWhenResettingWithShortPassword() {
        assertThatThrownBy(() -> passwordResetService.resetPassword("token", "123"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("at least 6 characters");
    }
}
