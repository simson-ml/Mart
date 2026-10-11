package com.simson.shopsphere.service.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simson.shopsphere.config.ChatbotProperties;
import com.simson.shopsphere.exception.ChatProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GeminiChatProviderTest {

    private ChatbotProperties properties;
    private ObjectMapper objectMapper;

    static class FakeHttpResponse implements HttpResponse<String> {
        private final int statusCode;
        private final String body;

        FakeHttpResponse(int statusCode, String body) {
            this.statusCode = statusCode;
            this.body = body;
        }

        @Override public int statusCode() { return statusCode; }
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
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("GeminiChatProvider throws ChatProviderException when API key is missing")
    void testMissingApiKeyThrowsException() {
        properties.setGeminiApiKey("");
        GeminiChatProvider provider = new GeminiChatProvider(properties, objectMapper, req -> new FakeHttpResponse(200, ""));

        ChatProviderException ex = assertThrows(ChatProviderException.class, () ->
                provider.getReply("How do I find a product?", ""));

        assertTrue(ex.getMessage().contains("Gemini API key is not configured"));
    }

    @Test
    @DisplayName("GeminiChatProvider parses valid candidate JSON response")
    void testSuccessfulResponseParsing() {
        properties.setGeminiApiKey("secret-test-key-12345");
        properties.setGeminiModel("gemini-1.5-flash");

        String jsonResponse = """
            {
              "candidates": [
                {
                  "content": {
                    "parts": [
                      {
                        "text": "You can find products by browsing the catalog or using our search bar."
                      }
                    ],
                    "role": "model"
                  },
                  "finishReason": "STOP"
                }
              ]
            }
            """;

        GeminiChatProvider provider = new GeminiChatProvider(properties, objectMapper, req -> new FakeHttpResponse(200, jsonResponse));

        String reply = provider.getReply("How can I find products?", "store context");

        assertNotNull(reply);
        assertEquals("You can find products by browsing the catalog or using our search bar.", reply);
    }

    @Test
    @DisplayName("GeminiChatProvider handles non-200 HTTP error without exposing key")
    void testHttpErrorStatusHandling() {
        properties.setGeminiApiKey("secret-test-key-12345");

        GeminiChatProvider provider = new GeminiChatProvider(properties, objectMapper, req -> new FakeHttpResponse(403, "Forbidden"));

        ChatProviderException ex = assertThrows(ChatProviderException.class, () ->
                provider.getReply("Check order status", ""));

        assertTrue(ex.getMessage().contains("403"));
        assertFalse(ex.getMessage().contains("secret-test-key-12345"));
    }

    @Test
    @DisplayName("GeminiChatProvider handles network IOException safely without leaking key")
    void testNetworkFailureHandling() {
        properties.setGeminiApiKey("secret-test-key-12345");

        GeminiChatProvider provider = new GeminiChatProvider(properties, objectMapper, req -> {
            throw new IOException("Connection timed out");
        });

        ChatProviderException ex = assertThrows(ChatProviderException.class, () ->
                provider.getReply("What is the return policy?", ""));

        assertTrue(ex.getMessage().contains("Gemini communication failure"));
        assertFalse(ex.getMessage().contains("secret-test-key-12345"));
    }
}
