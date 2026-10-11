package com.simson.shopsphere.service.chat;

import com.simson.shopsphere.config.ChatbotProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SessionChatCacheTest {

    private ChatbotProperties properties;
    private SessionChatCache cache;

    @BeforeEach
    void setUp() {
        properties = new ChatbotProperties();
        properties.getCache().setEnabled(true);
        properties.getCache().setMaxEntriesPerSession(50);
        properties.getCache().setExpireMinutes(30);
        cache = new SessionChatCache(properties);
    }

    @Test
    @DisplayName("Caches normalized questions and returns exact reply")
    void testCachePutAndGetNormalized() {
        String sessionId = "sess-123";
        String question = "How do I add a product to the cart?";
        String reply = "Click the 'Add to Cart' button.";

        cache.put(sessionId, question, reply);

        Optional<String> cached = cache.get(sessionId, "  how do i add a product to the cart?  ");
        assertTrue(cached.isPresent());
        assertEquals(reply, cached.get());
    }

    @Test
    @DisplayName("Different sessions do not share cached replies")
    void testSessionIsolation() {
        String sessionA = "sess-alpha";
        String sessionB = "sess-beta";
        String question = "Where is my order?";
        String reply = "Visit your Track Orders page.";

        cache.put(sessionA, question, reply);

        assertTrue(cache.get(sessionA, question).isPresent());
        assertFalse(cache.get(sessionB, question).isPresent(), "Session B must not see Session A's cached answers");
    }

    @Test
    @DisplayName("Disabled cache always returns empty")
    void testDisabledCache() {
        properties.getCache().setEnabled(false);
        cache.put("sess-1", "question", "reply");

        assertFalse(cache.get("sess-1", "question").isPresent());
    }
}
