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

    @Test
    @DisplayName("Form login with valid admin credentials should succeed and redirect to /admin/dashboard")
    void testAdminLoginSuccess() throws Exception {
        mockMvc.perform(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin("/login")
                .user("email", "admin@shopsphere.com")
                .password("password", "Admin@123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl("/admin/dashboard"));
    }

    @Test
    @DisplayName("Form login with invalid credentials should fail and redirect to /login?error=true")
    void testAdminLoginFailureInvalidPassword() throws Exception {
        mockMvc.perform(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin("/login")
                .user("email", "admin@shopsphere.com")
                .password("password", "WrongPassword!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl("/login?error=true"));
    }

    @Test
    @WithMockUser(username = "rahul@example.com", roles = {"USER"})
    @DisplayName("Buy Now action on cart should redirect directly to /checkout")
    void testBuyNowDirectCheckout() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/cart/add")
                .param("productId", "1")
                .param("quantity", "2")
                .param("buyNow", "true")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl("/checkout"));
    }

    @Test
    @WithMockUser(username = "rahul@example.com", roles = {"USER"})
    @DisplayName("Authenticated customer must be able to access /account/addresses/add")
    void testCustomerCanAccessAddAddressPage() throws Exception {
        mockMvc.perform(get("/account/addresses/add"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Unauthenticated request to /account/addresses/add must redirect to login")
    void testAnonymousAccessToAddAddressRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/account/addresses/add"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "rahul@example.com", roles = {"USER"})
    @DisplayName("Customer can successfully submit new address form with CSRF token")
    void testCustomerCanSubmitNewAddress() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/account/addresses/add")
                .param("fullName", "Rahul Sharma")
                .param("phone", "9876543211")
                .param("addressLine1", "Flat 402, Green Acres Residency")
                .param("city", "Bengaluru")
                .param("state", "Karnataka")
                .param("pincode", "560001")
                .param("addressType", "HOME")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl("/account/addresses"));
    }

    @Test
    @DisplayName("Login page must not contain admin credentials or admin quick-fill button")
    void testLoginPageDoesNotExposeAdminCredentials() throws Exception {
        org.springframework.test.web.servlet.MvcResult result = mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        org.junit.jupiter.api.Assertions.assertFalse(content.contains("admin@shopsphere.com"), "Login HTML should not contain admin email");
        org.junit.jupiter.api.Assertions.assertFalse(content.contains("Admin@123"), "Login HTML should not contain admin password");
        org.junit.jupiter.api.Assertions.assertFalse(content.contains("fill-admin-btn"), "Login HTML should not contain fill-admin-btn button ID");
    }

    @Test
    @DisplayName("Static JavaScript assets must not contain admin credentials or fill-admin-btn")
    void testStaticJsDoesNotContainAdminCredentials() throws Exception {
        java.nio.file.Path jsPath = java.nio.file.Paths.get("src/main/resources/static/js/main.js");
        if (java.nio.file.Files.exists(jsPath)) {
            String jsContent = java.nio.file.Files.readString(jsPath);
            org.junit.jupiter.api.Assertions.assertFalse(jsContent.contains("Admin@123"), "main.js must not contain plaintext admin password");
            org.junit.jupiter.api.Assertions.assertFalse(jsContent.contains("admin@shopsphere.com"), "main.js must not contain admin email");
            org.junit.jupiter.api.Assertions.assertFalse(jsContent.contains("fill-admin-btn"), "main.js must not contain fill-admin-btn handler");
        }
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = {"USER"})
    @DisplayName("Customer role must receive 403 Forbidden on all admin POST mutation endpoints")
    void testCustomerForbiddenFromAdminMutations() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/admin/categories/create")
                .param("name", "Malicious Category")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/admin/products/create")
                .param("name", "Malicious Product")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/admin/users/1/toggle")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/admin/orders/1/status")
                .param("status", "DELIVERED")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Anonymous user must be redirected to login on admin mutation endpoints")
    void testAnonymousRedirectedFromAdminMutations() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/admin/categories/create")
                .param("name", "Test Category")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}
