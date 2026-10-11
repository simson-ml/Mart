package com.simson.shopsphere.service;

import com.simson.shopsphere.dto.ChatApiResponse;
import com.simson.shopsphere.dto.ChatRequest;
import com.simson.shopsphere.entity.User;

public interface ChatService {

    /**
     * Processes a customer chat request with validation, rate-limiting, caching,
     * provider abstraction, and graceful fallback.
     *
     * @param sessionId the caller's session ID for rate limiting and cache isolation
     * @param request the validated chat request containing the user message
     * @param currentUser the authenticated user, or null if guest
     * @return the structured ChatApiResponse envelope
     */
    ChatApiResponse processChat(String sessionId, ChatRequest request, User currentUser);
}
