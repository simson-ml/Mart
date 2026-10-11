package com.simson.shopsphere.controller.api;

import com.simson.shopsphere.dto.ChatApiResponse;
import com.simson.shopsphere.dto.ChatRequest;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.exception.BadRequestException;
import com.simson.shopsphere.exception.ChatProviderException;
import com.simson.shopsphere.exception.RateLimitExceededException;
import com.simson.shopsphere.security.CustomUserDetails;
import com.simson.shopsphere.service.ChatService;
import com.simson.shopsphere.util.ClientIpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
@Tag(name = "Chat API", description = "Endpoints for ShopSphere AI Chatbot customer support")
public class ApiChatController {

    private static final Logger log = LoggerFactory.getLogger(ApiChatController.class);

    private final ChatService chatService;

    public ApiChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    @Operation(summary = "Send a question to the ShopSphere AI Assistant")
    public ResponseEntity<ChatApiResponse> chat(
            @Valid @RequestBody ChatRequest request,
            HttpServletRequest httpServletRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String sessionId = resolveSessionId(httpServletRequest);
        User currentUser = userDetails != null ? userDetails.getUser() : null;

        ChatApiResponse response = chatService.processChat(sessionId, request, currentUser);
        return ResponseEntity.ok(response);
    }

    private String resolveSessionId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return session.getId();
        }
        // Fallback to client IP combined with user-agent if session doesn't exist yet
        String ip = ClientIpUtil.getClientIp(request);
        return "ip-" + (ip != null ? ip : "unknown");
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ChatApiResponse> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ChatApiResponse.error("VALIDATION_ERROR", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ChatApiResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Please enter a valid message.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ChatApiResponse.error("VALIDATION_ERROR", message));
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ChatApiResponse> handleRateLimit(RateLimitExceededException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ChatApiResponse.error("RATE_LIMIT_EXCEEDED", ex.getMessage()));
    }

    @ExceptionHandler(ChatProviderException.class)
    public ResponseEntity<ChatApiResponse> handleProviderError(ChatProviderException ex) {
        log.warn("Chat provider error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ChatApiResponse.error("PROVIDER_UNAVAILABLE", "The AI assistant is temporarily unavailable. Please try again shortly."));
    }
}
