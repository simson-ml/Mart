package com.simson.shopsphere.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    public SecurityConfig(CustomAuthenticationSuccessHandler authenticationSuccessHandler, CustomUserDetailsService userDetailsService) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.userDetailsService = userDetailsService;
    }


    private final CustomAuthenticationSuccessHandler authenticationSuccessHandler;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .headers(headers -> headers
                .addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
            )
            .csrf(csrf -> csrf
                .csrfTokenRepository(org.springframework.security.web.csrf.CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers(
                    new AntPathRequestMatcher("/h2-console/**"),
                    new AntPathRequestMatcher("/api/**")
                )
            )
            .authorizeHttpRequests(auth -> auth
                // Static Assets
                .requestMatchers(
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/uploads/**",
                    "/favicon.ico"
                ).permitAll()
                // Swagger / OpenAPI docs & H2 console
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/api-docs/**",
                    "/v3/api-docs/**",
                    "/h2-console/**"
                ).permitAll()
                // Public Browsing & Auth Pages
                .requestMatchers(
                    "/",
                    "/login",
                    "/register",
                    "/forgot-password",
                    "/reset-password",
                    "/products/**",
                    "/categories/**",
                    "/cart/**",
                    "/compare/**",
                    "/api/products/**",
                    "/api/cart/**",
                    "/api/compare/**",
                    "/error/**"
                ).permitAll()
                // Admin Portal
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Customer Secured Endpoints
                .requestMatchers(
                    "/account/**",
                    "/wishlist/**",
                    "/checkout/**",
                    "/orders/**",
                    "/reviews/add",
                    "/api/wishlist/**",
                    "/api/checkout/**",
                    "/api/coupons/**"
                ).authenticated()
                // Any other request
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .loginProcessingUrl("/login")
                .successHandler(authenticationSuccessHandler)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .rememberMe(remember -> remember
                .key("shopsphere-secret-remember-me-key-2026")
                .tokenValiditySeconds(7 * 24 * 60 * 60) // 7 days
                .userDetailsService(userDetailsService)
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID", "remember-me")
                .permitAll()
            );

        return http.build();
    }
}
