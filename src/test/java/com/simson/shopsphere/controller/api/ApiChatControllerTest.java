package com.simson.shopsphere.controller.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class ApiChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Valid chat request returns HTTP 200 with required envelope")
    void testSuccessfulChat() throws Exception {
        String payload = """
            {
              "message": "How can I find products in the store?"
            }
            """;

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.reply", not(emptyOrNullString())))
                .andExpect(jsonPath("$.error", nullValue()));
    }

    @Test
    @DisplayName("Empty or whitespace message returns HTTP 400 with VALIDATION_ERROR envelope")
    void testEmptyMessageReturns400() throws Exception {
        String payload = """
            {
              "message": "   "
            }
            """;

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.data", nullValue()))
                .andExpect(jsonPath("$.error.code", is("VALIDATION_ERROR")))
                .andExpect(jsonPath("$.error.message", containsString("valid message")));
    }

    @Test
    @DisplayName("Oversized message (> 1000 chars) returns HTTP 400 with VALIDATION_ERROR envelope")
    void testOversizedMessageReturns400() throws Exception {
        String longMessage = "a".repeat(1001);
        String payload = "{\"message\": \"" + longMessage + "\"}";

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.data", nullValue()))
                .andExpect(jsonPath("$.error.code", is("VALIDATION_ERROR")));
    }

    @Test
    @DisplayName("Session exceeding 10 requests in rolling 60s window returns HTTP 429")
    void testRateLimitReturns429() throws Exception {
        MockHttpSession session = new MockHttpSession();
        String payload = """
            {
              "message": "How does the shopping cart work?"
            }
            """;

        // First 10 requests should succeed
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(post("/api/v1/chat")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payload))
                    .andExpect(status().isOk());
        }

        // 11th request must receive HTTP 429 Too Many Requests
        mockMvc.perform(post("/api/v1/chat")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.data", nullValue()))
                .andExpect(jsonPath("$.error.code", is("RATE_LIMIT_EXCEEDED")))
                .andExpect(jsonPath("$.error.message", containsString("Rate limit exceeded")));
    }

    @Test
    @DisplayName("Unauthenticated guest user can access /api/v1/chat without login redirect")
    void testGuestAccessAllowed() throws Exception {
        String payload = """
            {
              "message": "How do I create an account?"
            }
            """;

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.reply", not(emptyOrNullString())));
    }
}
