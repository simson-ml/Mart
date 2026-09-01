package com.example.ecommerce.config;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.*;
import org.springframework.security.web.*;

@Configuration
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filter(HttpSecurity h) throws Exception {
        h.csrf(c -> c.disable()).headers(x -> x.frameOptions(f -> f.sameOrigin()))
                .authorizeHttpRequests(a -> a.requestMatchers("/**").permitAll());
        return h.build();
    }
}
