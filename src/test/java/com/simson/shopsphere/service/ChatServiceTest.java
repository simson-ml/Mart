package com.simson.shopsphere.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simson.shopsphere.config.ChatbotProperties;
import com.simson.shopsphere.dto.ChatApiResponse;
import com.simson.shopsphere.dto.ChatRequest;
import com.simson.shopsphere.entity.Category;
import com.simson.shopsphere.exception.BadRequestException;
import com.simson.shopsphere.exception.RateLimitExceededException;
import com.simson.shopsphere.service.chat.GeminiChatProvider;
import com.simson.shopsphere.service.chat.MockChatProvider;
import com.simson.shopsphere.service.chat.SessionChatCache;
import com.simson.shopsphere.service.chat.SessionRateLimiter;
import com.simson.shopsphere.service.impl.ChatServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    private ChatbotProperties properties;
    private MockChatProvider mockChatProvider;
    private GeminiChatProvider geminiChatProvider;
    private SessionRateLimiter rateLimiter;
    private SessionChatCache chatCache;

    @Mock
    private CategoryService categoryService;

    private ChatServiceImpl chatService;
    private final AtomicBoolean geminiShouldFail = new AtomicBoolean(false);

    static class SimpleHttpResponse implements HttpResponse<String> {
        private final int code;
        private final String body;
        SimpleHttpResponse(int code, String body) { this.code = code; this.body = body; }
        @Override public int statusCode() { return code; }
        @Override public String body() { return body; }
        @Override public HttpRequest request() { return null; }
        @Override public Optional<HttpResponse<String>> previousResponse() { return Optional.empty(); }
        @Override public HttpHeaders headers() { return HttpHeaders.of(Collections.emptyMap(), (a, b) -> true); }
        @Override public Optional<SSLSession> sslSession() { return Optional.empty(); }
        @Override public URI uri() { return URI.create("https://generativelanguage.googleapis.com"); }
        @Override public HttpClient.Version version() { return HttpClient.Version.HTTP_2; }
    }

    @BeforeEach
    void setUp() {
        properties = new ChatbotProperties();
        properties.setProvider("mock");
        properties.setMaxMessageLength(1000);
        properties.getRateLimit().setMaxRequests(10);
        properties.getRateLimit().setWindowSeconds(60);
        properties.getCache().setEnabled(true);

        mockChatProvider = new MockChatProvider();
        geminiShouldFail.set(false);

        geminiChatProvider = new GeminiChatProvider(properties, new ObjectMapper(), req -> {
            if (geminiShouldFail.get()) {
                throw new IOException("Simulated Gemini connection failure");
            }
            return new SimpleHttpResponse(200, """
                {
                  "candidates": [
                    {
                      "content": {
                        "parts": [{"text": "Gemini answer for products."}],
                        "role": "model"
                      }
                    }
                  ]
                }
                """);
        });

        rateLimiter = new SessionRateLimiter(properties);
        chatCache = new SessionChatCache(properties);

        chatService = new ChatServiceImpl(
                properties,
                mockChatProvider,
                geminiChatProvider,
                rateLimiter,
                chatCache,
                categoryService
        );
    }

    @Test
    @DisplayName("Valid message produces successful response")
    void testValidMessageSuccess() {
        when(categoryService.getAllActiveCategories()).thenReturn(List.of(
                Category.builder().name("Mobiles").slug("mobiles").active(true).build()
        ));

        ChatRequest request = new ChatRequest("How can I find products in the store?");
        ChatApiResponse response = chatService.processChat("session-1", request, null);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertNotNull(response.getData().getReply());
        assertNull(response.getError());
    }

    @Test
    @DisplayName("Empty or null message throws BadRequestException")
    void testBlankMessageThrowsBadRequest() {
        ChatRequest emptyReq = new ChatRequest("   ");

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                chatService.processChat("session-1", emptyReq, null));

        assertEquals("Please enter a valid message.", ex.getMessage());
    }

    @Test
    @DisplayName("Oversized message exceeding 1000 characters throws BadRequestException")
    void testOversizedMessageThrowsBadRequest() {
        String longText = "a".repeat(1001);
        ChatRequest longReq = new ChatRequest(longText);

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                chatService.processChat("session-1", longReq, null));

        assertTrue(ex.getMessage().contains("Message exceeds maximum allowed length"));
    }

    @Test
    @DisplayName("Exceeding 10 requests per minute throws RateLimitExceededException")
    void testRateLimitExceeded() {
        ChatRequest request = new ChatRequest("How do I checkout?");

        for (int i = 0; i < 10; i++) {
            chatService.processChat("session-rate", request, null);
        }

        RateLimitExceededException ex = assertThrows(RateLimitExceededException.class, () ->
                chatService.processChat("session-rate", request, null));

        assertTrue(ex.getMessage().contains("Rate limit exceeded"));
    }

    @Test
    @DisplayName("Repeated identical question in same session returns cached response")
    void testSessionCaching() {
        ChatRequest request1 = new ChatRequest("How does the shopping cart work?");
        ChatApiResponse res1 = chatService.processChat("session-cached", request1, null);

        ChatRequest request2 = new ChatRequest("how does the shopping cart work?");
        ChatApiResponse res2 = chatService.processChat("session-cached", request2, null);

        assertEquals(res1.getData().getReply(), res2.getData().getReply());
    }

    @Test
    @DisplayName("Gemini failure gracefully falls back to MockChatProvider without throwing exception")
    void testGeminiFallbackToMock() {
        properties.setProvider("gemini");
        properties.setGeminiApiKey("some-gemini-key");
        geminiShouldFail.set(true);

        ChatRequest request = new ChatRequest("Where can I find my order history?");
        ChatApiResponse response = chatService.processChat("session-fallback", request, null);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getData().getReply());
        assertTrue(response.getData().getReply().toLowerCase().contains("order"));
    }

    @Test
    @DisplayName("Gemini selected with missing key safely falls back to MockChatProvider")
    void testGeminiMissingKeyFallback() {
        properties.setProvider("gemini");
        properties.setGeminiApiKey("");

        ChatRequest request = new ChatRequest("How do I search for a product?");
        ChatApiResponse response = chatService.processChat("session-nokey", request, null);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getData().getReply());
    }
}
