package com.simson.shopsphere.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ai.chatbot")
public class ChatbotProperties {

    /**
     * Provider selection: "mock" (default, deterministic local fallback) or "gemini"
     */
    private String provider = "mock";

    /**
     * Google Gemini API key. Must remain server-side and never exposed to the client.
     */
    private String geminiApiKey = "";

    /**
     * Gemini model identifier (e.g., gemini-1.5-flash).
     */
    private String geminiModel = "gemini-1.5-flash";

    /**
     * Base Gemini API URL.
     */
    private String geminiApiUrl = "https://generativelanguage.googleapis.com/v1beta/models/";

    /**
     * Maximum allowed message length in characters. Default: 1000.
     */
    private int maxMessageLength = 1000;

    /**
     * Timeout in seconds for outbound Gemini provider calls.
     */
    private int timeoutSeconds = 10;

    /**
     * Rate limiting settings.
     */
    private RateLimit rateLimit = new RateLimit();

    /**
     * Per-session caching settings.
     */
    private Cache cache = new Cache();

    public static class RateLimit {
        private int maxRequests = 10;
        private int windowSeconds = 60;

        public int getMaxRequests() {
            return maxRequests;
        }

        public void setMaxRequests(int maxRequests) {
            this.maxRequests = maxRequests;
        }

        public int getWindowSeconds() {
            return windowSeconds;
        }

        public void setWindowSeconds(int windowSeconds) {
            this.windowSeconds = windowSeconds;
        }
    }

    public static class Cache {
        private boolean enabled = true;
        private int maxEntriesPerSession = 50;
        private int expireMinutes = 30;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getMaxEntriesPerSession() {
            return maxEntriesPerSession;
        }

        public void setMaxEntriesPerSession(int maxEntriesPerSession) {
            this.maxEntriesPerSession = maxEntriesPerSession;
        }

        public int getExpireMinutes() {
            return expireMinutes;
        }

        public void setExpireMinutes(int expireMinutes) {
            this.expireMinutes = expireMinutes;
        }
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getGeminiApiKey() {
        return geminiApiKey;
    }

    public void setGeminiApiKey(String geminiApiKey) {
        this.geminiApiKey = geminiApiKey;
    }

    public String getGeminiModel() {
        return geminiModel;
    }

    public void setGeminiModel(String geminiModel) {
        this.geminiModel = geminiModel;
    }

    public String getGeminiApiUrl() {
        return geminiApiUrl;
    }

    public void setGeminiApiUrl(String geminiApiUrl) {
        this.geminiApiUrl = geminiApiUrl;
    }

    public int getMaxMessageLength() {
        return maxMessageLength;
    }

    public void setMaxMessageLength(int maxMessageLength) {
        this.maxMessageLength = maxMessageLength;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public RateLimit getRateLimit() {
        return rateLimit;
    }

    public void setRateLimit(RateLimit rateLimit) {
        this.rateLimit = rateLimit;
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }
}
