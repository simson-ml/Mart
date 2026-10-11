package com.simson.shopsphere.service.chat;

import com.simson.shopsphere.config.ChatbotProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionRateLimiterTest {

    private ChatbotProperties properties;
    private SessionRateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        properties = new ChatbotProperties();
        properties.getRateLimit().setMaxRequests(10);
        properties.getRateLimit().setWindowSeconds(60);
        rateLimiter = new SessionRateLimiter(properties);
    }

    @Test
    @DisplayName("Allows up to 10 requests in 60s window for a session")
    void testPermitsUpToLimit() {
        String sessionId = "sess-abc-1";

        for (int i = 0; i < 10; i++) {
            assertTrue(rateLimiter.tryAcquire(sessionId), "Request " + (i + 1) + " should be allowed");
        }

        // 11th request should be rejected
        assertFalse(rateLimiter.tryAcquire(sessionId), "11th request should exceed the limit");
    }

    @Test
    @DisplayName("Different sessions have independent rate limits")
    void testIndependentSessionLimits() {
        String sessionA = "sess-user-a";
        String sessionB = "sess-user-b";

        for (int i = 0; i < 10; i++) {
            assertTrue(rateLimiter.tryAcquire(sessionA));
        }

        assertFalse(rateLimiter.tryAcquire(sessionA));
        // Session B should still have full quota
        assertTrue(rateLimiter.tryAcquire(sessionB));
    }
}
