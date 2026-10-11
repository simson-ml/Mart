package com.simson.shopsphere.service.chat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MockChatProviderTest {

    private MockChatProvider provider;

    @BeforeEach
    void setUp() {
        provider = new MockChatProvider();
    }

    @Test
    @DisplayName("Topic 1: Finding and browsing products")
    void testFindingAndBrowsingProducts() {
        String reply = provider.getReply("How can I find products in the store?", "");
        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("catalog") || reply.toLowerCase().contains("products"));
        assertNotEquals(MockChatProvider.RESTRICTED_SCOPE_MESSAGE, reply);
    }

    @Test
    @DisplayName("Topic 2: Searching by product name or keyword")
    void testSearchByKeyword() {
        String reply = provider.getReply("How do I search for a product by keyword?", "");
        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("search"));
        assertNotEquals(MockChatProvider.RESTRICTED_SCOPE_MESSAGE, reply);
    }

    @Test
    @DisplayName("Topic 3: Filtering products by category")
    void testFilteringByCategory() {
        String reply = provider.getReply("How do I filter products by category?", "");
        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("filter") || reply.toLowerCase().contains("category"));
        assertNotEquals(MockChatProvider.RESTRICTED_SCOPE_MESSAGE, reply);
    }

    @Test
    @DisplayName("Topic 4: Understanding product prices and stock availability")
    void testPriceAndStockAvailability() {
        String reply = provider.getReply("How can I check product availability and stock?", "");
        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("stock") || reply.toLowerCase().contains("price"));
        assertNotEquals(MockChatProvider.RESTRICTED_SCOPE_MESSAGE, reply);
    }

    @Test
    @DisplayName("Topic 5: Adding products to the cart")
    void testAddingToCart() {
        String reply = provider.getReply("How do I add a product to the cart?", "");
        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("add to cart"));
        assertNotEquals(MockChatProvider.RESTRICTED_SCOPE_MESSAGE, reply);
    }

    @Test
    @DisplayName("Topic 6: Updating cart quantities and removing items")
    void testUpdatingAndRemovingCartItems() {
        String reply = provider.getReply("How does the shopping cart work and how to update quantity?", "");
        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("cart"));
        assertNotEquals(MockChatProvider.RESTRICTED_SCOPE_MESSAGE, reply);
    }

    @Test
    @DisplayName("Topic 7: Understanding the checkout process")
    void testCheckoutProcess() {
        String reply = provider.getReply("How do I place an order and checkout?", "");
        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("checkout") || reply.toLowerCase().contains("place order"));
        assertNotEquals(MockChatProvider.RESTRICTED_SCOPE_MESSAGE, reply);
    }

    @Test
    @DisplayName("Topic 8: Understanding the mock payment confirmation process")
    void testMockPaymentProcess() {
        String reply = provider.getReply("How does UPI simulated payment work?", "");
        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("upi") || reply.toLowerCase().contains("payment"));
        assertNotEquals(MockChatProvider.RESTRICTED_SCOPE_MESSAGE, reply);
    }

    @Test
    @DisplayName("Topic 9: Finding order history and order status")
    void testOrderHistoryAndStatus() {
        String reply = provider.getReply("Where can I find my order history?", "");
        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("order"));
        assertNotEquals(MockChatProvider.RESTRICTED_SCOPE_MESSAGE, reply);
    }

    @Test
    @DisplayName("Topic 10: Account login, registration, and store navigation")
    void testAccountLoginAndRegistration() {
        String reply = provider.getReply("How do I create an account or sign in?", "");
        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("sign in") || reply.toLowerCase().contains("account") || reply.toLowerCase().contains("login"));
        assertNotEquals(MockChatProvider.RESTRICTED_SCOPE_MESSAGE, reply);
    }

    @Test
    @DisplayName("Scope restriction: Unrelated general-knowledge query is politely restricted")
    void testRestrictedScopeResponse() {
        String reply = provider.getReply("Who was the 16th president of the United States?", "");
        assertEquals(MockChatProvider.RESTRICTED_SCOPE_MESSAGE, reply);
    }

    @Test
    @DisplayName("Scope restriction: Political or coding request is politely restricted")
    void testRestrictedScopeCodingRequest() {
        String reply = provider.getReply("Write me a python script to parse xml", "");
        assertEquals(MockChatProvider.RESTRICTED_SCOPE_MESSAGE, reply);
    }

    @Test
    @DisplayName("Null or empty query handling")
    void testEmptyMessageHandling() {
        String reply = provider.getReply("", "");
        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("please provide a question"));
    }
}
