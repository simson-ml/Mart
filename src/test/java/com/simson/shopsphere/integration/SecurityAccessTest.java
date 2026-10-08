package com.simson.shopsphere.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class SecurityAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Unauthenticated request to /admin/dashboard must redirect to login")
    void testAnonymousAccessToAdmin() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "rahul@example.com", roles = {"USER"})
    @DisplayName("Normal USER role must be forbidden from accessing /admin/**")
    void testUserRoleForbiddenFromAdmin() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@shopsphere.com", roles = {"ADMIN"})
    @DisplayName("ADMIN role must have full access to /admin/dashboard")
    void testAdminAccessSuccess() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Public storefront endpoints must be accessible without login")
    void testPublicEndpointsAccess() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk());
    }
}
