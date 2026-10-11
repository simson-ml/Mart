package com.simson.shopsphere.service.chat;

import com.simson.shopsphere.config.ChatbotProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SessionRateLimiter {

    private static final int MAX_TRACKED_SESSIONS = 5000;
    private final ChatbotProperties properties;
    private final Map<String, Deque<Long>> sessionTimestamps = new ConcurrentHashMap<>();
    private final AtomicInteger cleanupCounter = new AtomicInteger(0);

    public SessionRateLimiter(ChatbotProperties properties) {
        this.properties = properties;
    }

    /**
     * Checks if the request is permitted for the given session ID under the rate limit.
     *
     * @param sessionId the unique session identifier
     * @return true if permitted, false if rate limit exceeded
     */
    public boolean tryAcquire(String sessionId) {
        String key = (sessionId != null && !sessionId.isBlank()) ? sessionId : "anonymous";
        long now = System.currentTimeMillis();
        long windowMillis = (long) properties.getRateLimit().getWindowSeconds() * 1000L;
        int maxRequests = properties.getRateLimit().getMaxRequests();
        long cutoff = now - windowMillis;

        // Opportunistic cleanup every 100 requests or if map grows large
        if (cleanupCounter.incrementAndGet() % 100 == 0 || sessionTimestamps.size() > MAX_TRACKED_SESSIONS) {
            cleanupStaleSessions(cutoff);
        }

        final boolean[] allowed = new boolean[]{false};

        sessionTimestamps.compute(key, (k, deque) -> {
            if (deque == null) {
                deque = new ArrayDeque<>();
            }

            // Remove expired timestamps outside the rolling window
            while (!deque.isEmpty() && deque.peekFirst() <= cutoff) {
                deque.pollFirst();
            }

            if (deque.size() < maxRequests) {
                deque.addLast(now);
                allowed[0] = true;
            } else {
                allowed[0] = false;
            }

            return deque;
        });

        return allowed[0];
    }

    /**
     * Cleans up sessions whose timestamps are completely outside the window.
     */
    public void cleanupStaleSessions(long cutoff) {
        sessionTimestamps.entrySet().removeIf(entry -> {
            Deque<Long> deque = entry.getValue();
            synchronized (deque) {
                while (!deque.isEmpty() && deque.peekFirst() <= cutoff) {
                    deque.pollFirst();
                }
                return deque.isEmpty();
            }
        });
    }

    /**
     * Resets rate limiter state (useful for tests).
     */
    public void reset() {
        sessionTimestamps.clear();
        cleanupCounter.set(0);
    }
}
