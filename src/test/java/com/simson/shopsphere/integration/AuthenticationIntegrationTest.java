package com.simson.shopsphere.integration;

import com.simson.shopsphere.dto.RegisterRequest;
import com.simson.shopsphere.entity.Role;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.repository.UserRepository;
import com.simson.shopsphere.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class AuthenticationIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("User registration should hash password with BCrypt and assign USER role")
    void testUserRegistration() {
        RegisterRequest request = RegisterRequest.builder()
                .name("Integration Test User")
                .email("test.user@shopsphere.com")
                .phone("+91 9999988888")
                .password("SecurePass@123")
                .confirmPassword("SecurePass@123")
                .build();

        User registered = userService.registerUser(request);

        assertNotNull(registered.getId());
        assertEquals("test.user@shopsphere.com", registered.getEmail());
        assertEquals(Role.USER, registered.getRole());
        assertTrue(registered.isEnabled());

        // Password must be BCrypt hashed and never stored in plain text
        assertNotEquals("SecurePass@123", registered.getPassword());
        assertTrue(passwordEncoder.matches("SecurePass@123", registered.getPassword()));
    }
}
