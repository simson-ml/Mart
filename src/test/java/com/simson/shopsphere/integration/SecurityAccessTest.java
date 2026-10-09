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
}
