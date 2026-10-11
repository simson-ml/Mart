package com.simson.shopsphere.service.impl;

import com.simson.shopsphere.config.ChatbotProperties;
import com.simson.shopsphere.dto.ChatApiResponse;
import com.simson.shopsphere.dto.ChatRequest;
import com.simson.shopsphere.entity.Category;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.exception.BadRequestException;
import com.simson.shopsphere.exception.ChatProviderException;
import com.simson.shopsphere.exception.RateLimitExceededException;
import com.simson.shopsphere.service.CategoryService;
import com.simson.shopsphere.service.ChatService;
import com.simson.shopsphere.service.chat.GeminiChatProvider;
import com.simson.shopsphere.service.chat.MockChatProvider;
import com.simson.shopsphere.service.chat.SessionChatCache;
import com.simson.shopsphere.service.chat.SessionRateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatServiceImpl.class);

    private final ChatbotProperties properties;
    private final MockChatProvider mockChatProvider;
    private final GeminiChatProvider geminiChatProvider;
    private final SessionRateLimiter rateLimiter;
    private final SessionChatCache chatCache;
    private final CategoryService categoryService;

    public ChatServiceImpl(
            ChatbotProperties properties,
            MockChatProvider mockChatProvider,
            GeminiChatProvider geminiChatProvider,
            SessionRateLimiter rateLimiter,
            SessionChatCache chatCache,
            CategoryService categoryService) {
        this.properties = properties;
        this.mockChatProvider = mockChatProvider;
        this.geminiChatProvider = geminiChatProvider;
        this.rateLimiter = rateLimiter;
        this.chatCache = chatCache;
        this.categoryService = categoryService;
    }

    @Override
    public ChatApiResponse processChat(String sessionId, ChatRequest request, User currentUser) {
        // 1. Input Validation
        if (request == null || request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            throw new BadRequestException("Please enter a valid message.");
        }

        String message = request.getMessage().trim();
        int maxLen = properties.getMaxMessageLength() > 0 ? properties.getMaxMessageLength() : 1000;
        if (message.length() > maxLen) {
            throw new BadRequestException("Message exceeds maximum allowed length of " + maxLen + " characters.");
        }

        // 2. Per-Session Rate Limiting
        if (!rateLimiter.tryAcquire(sessionId)) {
            throw new RateLimitExceededException("Rate limit exceeded. Maximum 10 requests per minute allowed.");
        }

        // 3. Per-Session Cache Lookup
        Optional<String> cached = chatCache.get(sessionId, message);
        if (cached.isPresent()) {
            log.debug("Serving response from session cache for session: {}", sessionId);
            return ChatApiResponse.success(cached.get());
        }

        // 4. Build Safe Application Context (Data minimization: non-sensitive store metadata only)
        String storeContext = buildSafeStoreContext();

        // 5. Provider Selection and Invocation with Graceful Fallback
        String reply = null;
        String configuredProvider = properties.getProvider();

        if ("gemini".equalsIgnoreCase(configuredProvider)) {
            String apiKey = properties.getGeminiApiKey();
            if (apiKey == null || apiKey.trim().isEmpty()) {
                log.info("Gemini provider selected but GEMINI_API_KEY is not configured. Falling back to MockChatProvider.");
                reply = mockChatProvider.getReply(message, storeContext);
            } else {
                try {
                    reply = geminiChatProvider.getReply(message, storeContext);
                } catch (Exception ex) {
                    log.warn("Gemini provider execution failed ({}). Gracefully falling back to MockChatProvider.", ex.getMessage());
                    reply = mockChatProvider.getReply(message, storeContext);
                }
            }
        } else {
            // Default: MockChatProvider
            reply = mockChatProvider.getReply(message, storeContext);
        }

        if (reply == null || reply.isBlank()) {
            throw new ChatProviderException("Unable to produce a valid response.");
        }

        // 6. Cache Response for Session
        chatCache.put(sessionId, message, reply);

        return ChatApiResponse.success(reply);
    }

    private String buildSafeStoreContext() {
        try {
            List<Category> categories = categoryService.getAllActiveCategories();
            if (categories != null && !categories.isEmpty()) {
                String catNames = categories.stream()
                        .map(Category::getName)
                        .collect(Collectors.joining(", "));
                return "ShopSphere Store Context: Available Product Categories: [" + catNames + "]. " +
                       "Key Features: Free Shipping on orders over ₹999, 7-day easy returns, " +
                       "Simulated UPI QR & UTR confirmation, Cash on Delivery.";
            }
        } catch (Exception e) {
            log.debug("Unable to fetch categories for store context: {}", e.getMessage());
        }

        return "ShopSphere Store Context: Categories include Mobiles, Laptops, Electronics, Fashion, Appliances, Groceries. " +
               "Features: Free shipping over ₹999, 7-day returns, simulated UPI and COD checkout.";
    }
}
