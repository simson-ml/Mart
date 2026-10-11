package com.simson.shopsphere.service.chat;

import com.simson.shopsphere.config.ChatbotProperties;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionChatCache {

    private final ChatbotProperties properties;
    private final Map<String, Map<String, CacheEntry>> sessionCaches = new ConcurrentHashMap<>();

    public SessionChatCache(ChatbotProperties properties) {
        this.properties = properties;
    }

    private static class CacheEntry {
        final String reply;
        final long createdAt;

        CacheEntry(String reply, long createdAt) {
            this.reply = reply;
            this.createdAt = createdAt;
        }
    }

    /**
     * Normalizes a user message for consistent cache lookups.
     */
    public String normalize(String message) {
        if (message == null) {
            return "";
        }
        return message.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * Retrieves a cached reply for this specific session and normalized message, if still valid.
     */
    public Optional<String> get(String sessionId, String message) {
        if (!properties.getCache().isEnabled()) {
            return Optional.empty();
        }

        String key = (sessionId != null && !sessionId.isBlank()) ? sessionId : "anonymous";
        String normalizedMsg = normalize(message);

        Map<String, CacheEntry> cache = sessionCaches.get(key);
        if (cache == null) {
            return Optional.empty();
        }

        synchronized (cache) {
            CacheEntry entry = cache.get(normalizedMsg);
            if (entry == null) {
                return Optional.empty();
            }

            long expireMillis = (long) properties.getCache().getExpireMinutes() * 60 * 1000L;
            if (System.currentTimeMillis() - entry.createdAt > expireMillis) {
                cache.remove(normalizedMsg);
                return Optional.empty();
            }

            return Optional.of(entry.reply);
        }
    }

    /**
     * Caches a reply specifically for this session.
     */
    public void put(String sessionId, String message, String reply) {
        if (!properties.getCache().isEnabled() || reply == null || reply.isBlank()) {
            return;
        }

        String key = (sessionId != null && !sessionId.isBlank()) ? sessionId : "anonymous";
        String normalizedMsg = normalize(message);
        int maxEntries = properties.getCache().getMaxEntriesPerSession();

        Map<String, CacheEntry> cache = sessionCaches.computeIfAbsent(key, k ->
                Collections.synchronizedMap(new LinkedHashMap<String, CacheEntry>(maxEntries + 1, 0.75f, true) {
                    @Override
                    protected boolean removeEldestEntry(Map.Entry<String, CacheEntry> eldest) {
                        return size() > maxEntries;
                    }
                })
        );

        synchronized (cache) {
            cache.put(normalizedMsg, new CacheEntry(reply, System.currentTimeMillis()));
        }
    }

    /**
     * Clears cached data (useful for test resets).
     */
    public void clear() {
        sessionCaches.clear();
    }
}
