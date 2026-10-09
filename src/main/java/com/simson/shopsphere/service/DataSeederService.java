package com.simson.shopsphere.service;

import com.simson.shopsphere.entity.Role;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DataSeederService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeederService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.name:${ADMIN_NAME:SIMSON S (Admin)}}")
    private String adminName;

    @Value("${app.admin.email:${ADMIN_EMAIL:admin@shopsphere.com}}")
    private String adminEmail;

    @Value("${app.admin.password:${ADMIN_PASSWORD:Admin@123}}")
    private String adminPassword;

    public DataSeederService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        String targetEmail = adminEmail.toLowerCase().trim();
        User admin = userRepository.findByEmailIgnoreCase(targetEmail).orElseGet(() -> User.builder()
                .name(adminName)
                .email(targetEmail)
                .phone("+91 9876543210")
                .role(Role.ADMIN)
                .enabled(true)
                .build());

        admin.setName(adminName);
        admin.setRole(Role.ADMIN);
        admin.setEnabled(true);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        userRepository.save(admin);
        log.info("Admin user synchronized successfully: email={}", targetEmail);

        // Seed demo customer if user table is empty of normal users
        long customerCount = userRepository.countByRole(Role.USER);
        if (customerCount == 0) {
            User demoCustomer = User.builder()
                    .name("Rahul Sharma")
                    .email("rahul@example.com")
                    .phone("+91 9811122334")
                    .password(passwordEncoder.encode("User@123"))
                    .role(Role.USER)
                    .enabled(true)
                    .build();
            userRepository.save(demoCustomer);
            log.info("Demo customer seeded: email={}", demoCustomer.getEmail());
        }
    }
}
