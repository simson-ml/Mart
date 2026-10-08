package com.simson.shopsphere.security;

import com.simson.shopsphere.entity.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);


    private final RequestCache requestCache = new HttpSessionRequestCache();
    private final com.simson.shopsphere.service.CartService cartService;
    private final com.simson.shopsphere.repository.UserRepository userRepository;

    public CustomAuthenticationSuccessHandler(com.simson.shopsphere.service.CartService cartService,
                                              com.simson.shopsphere.repository.UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        log.info("User {} successfully authenticated with authorities {}", authentication.getName(), authentication.getAuthorities());

        // Merge guest cart into user's persistent cart if guest cart exists in session
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        if (session != null) {
            @SuppressWarnings("unchecked")
            java.util.Map<Long, Integer> guestCart = (java.util.Map<Long, Integer>) session.getAttribute(com.simson.shopsphere.controller.CartController.SESSION_GUEST_CART);
            if (guestCart != null && !guestCart.isEmpty()) {
                userRepository.findByEmailIgnoreCase(authentication.getName()).ifPresent(user -> {
                    try {
                        cartService.mergeGuestCart(user, guestCart);
                        log.info("Merged {} guest cart items for user {}", guestCart.size(), user.getEmail());
                    } catch (Exception e) {
                        log.error("Failed to merge guest cart for user {}: {}", user.getEmail(), e.getMessage());
                    }
                });
                session.removeAttribute(com.simson.shopsphere.controller.CartController.SESSION_GUEST_CART);
            }
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_" + Role.ADMIN.name()));

        SavedRequest savedRequest = requestCache.getRequest(request, response);

        if (isAdmin) {
            clearAuthenticationAttributes(request);
            getRedirectStrategy().sendRedirect(request, response, "/admin/dashboard");
            return;
        }

        if (savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl();
            // Don't redirect back to login or error pages
            if (!targetUrl.contains("/login") && !targetUrl.contains("/error")) {
                clearAuthenticationAttributes(request);
                getRedirectStrategy().sendRedirect(request, response, targetUrl);
                return;
            }
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
