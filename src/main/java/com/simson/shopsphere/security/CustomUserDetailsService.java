package com.simson.shopsphere.security;

import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CustomUserDetailsService.class);

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> {
                    log.warn("Login attempt failed: user email not found: {}", email);
                    return new UsernameNotFoundException("Invalid email or password");
                });

        if (!user.isEnabled()) {
            log.warn("Login attempt for disabled account: {}", email);
            throw new UsernameNotFoundException("Account is disabled. Please contact administrator.");
        }

        return new CustomUserDetails(user);
    }
}
