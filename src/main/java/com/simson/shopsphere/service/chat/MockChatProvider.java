package com.simson.shopsphere.service.chat;

import org.springframework.stereotype.Component;

import java.util.Locale;

@Component("mockChatProvider")
public class MockChatProvider implements ChatProvider {

    public static final String RESTRICTED_SCOPE_MESSAGE =
            "I am the ShopSphere Assistant and can only assist with ShopSphere products, categories, " +
            "shopping cart, checkout, orders, and store operations. Please let me know how I can help with your shopping experience!";

    @Override
    public String getReply(String userMessage, String context) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "Please provide a question about ShopSphere products, orders, or shopping features.";
        }

        String query = userMessage.toLowerCase(Locale.ROOT).trim();

        // 1. Greetings & Bot Intro
        if (query.matches("^(hi|hello|hey|help|greetings)(\\s.*|!|\\.)?$") || query.contains("who are you") || query.contains("what can you do")) {
            return "Hello! I am your ShopSphere AI Assistant. I can help you browse products, search our catalog, check stock availability, manage your shopping cart, guide you through checkout and simulated payment, or track your orders.";
        }

        // 2. Order History, Order Status & Tracking (Highest priority domain)
        if (query.contains("order")) {
            if (query.contains("place") || query.contains("checkout") || query.contains("how do i place")) {
                return "To place an order, go to your cart and click 'Proceed to Checkout'. " +
                       "Select or add your delivery address, pick your preferred payment method (Simulated UPI or Cash on Delivery), review your order summary, and click 'Place Order'.";
            }
            return "You can view your order history and live delivery timeline by visiting 'Track Your Orders' (/orders) from the header account menu. " +
                   "Each order shows its lifecycle status: Placed, Confirmed, Shipped, Delivered, or Cancelled.";
        }

        // 3. Payment & Simulated UPI / Cash on Delivery
        if (query.contains("payment") || query.contains("pay") || query.contains("upi") || query.contains("utr") || query.contains("cod") || query.contains("cash on delivery")) {
            return "ShopSphere features a simulated payment gateway. For UPI payments, scan the displayed QR code or enter a 12-digit transaction UTR number to simulate instant confirmation. " +
                   "Alternatively, choose Cash on Delivery (COD) to pay upon delivery.";
        }

        // 4. Cart Operations (Adding, Updating, Removing)
        if (query.contains("cart")) {
            if (query.contains("add") || query.contains("put")) {
                return "To add an item to your cart, click the 'Add to Cart' button on any product card or details page. " +
                       "If you are not logged in, items are stored in your session guest cart and will automatically merge when you sign in.";
            }
            if (query.contains("update") || query.contains("quantity") || query.contains("remove") || query.contains("delete") || query.contains("clear") || query.contains("work") || query.contains("item")) {
                return "On the Shopping Cart page (/cart), you can increase or decrease item quantities using the '+' and '-' buttons, " +
                       "or click the red trash icon to remove an item. Your subtotal and estimated total update in real-time.";
            }
            return "Your Shopping Cart (/cart) holds your selected products. You can apply promotional discount coupons, review item quantities, and proceed to checkout whenever you are ready.";
        }

        if (query.contains("add to cart") || query.contains("add product") || query.contains("put in cart")) {
            return "To add an item to your cart, click the 'Add to Cart' button on any product card or details page. " +
                   "If you are not logged in, items are stored in your session guest cart and will automatically merge when you sign in.";
        }

        // 5. Checkout
        if (query.contains("checkout") || query.contains("shipping address") || query.contains("delivery address")) {
            return "To place an order, go to your cart and click 'Proceed to Checkout'. " +
                   "Select or add your delivery address, pick your preferred payment method (Simulated UPI or Cash on Delivery), review your order summary, and click 'Place Order'.";
        }

        // 6. Searching by product name or keyword
        if (query.contains("search") || query.contains("keyword") || query.contains("autocomplete") || query.contains("search bar")) {
            return "You can search for any product using the search bar at the top of every page. " +
                   "Type product keywords or brand names (e.g., Apple, Samsung, Nike) to see instant autocomplete suggestions.";
        }

        // 7. Filtering products by category
        if (query.contains("filter") || query.contains("category")) {
            return "To filter products by category, visit the All Products page (/products) and use the sidebar filters. " +
                   "You can filter across Mobiles & 5G, Laptops & PC, Electronics, Fashion & Apparel, Home Appliances, and Groceries, or refine by brand, rating, and price range.";
        }

        // 8. Prices and stock availability
        if (query.contains("stock") || query.contains("availability") || query.contains("price") || query.contains("cost") || query.contains("available") || query.contains("how much")) {
            return "Live prices, discount percentages, and current inventory levels are displayed directly on every product card and details page. " +
                   "Products with zero stock are marked as 'Out of Stock' and cannot be added to the cart until restocked.";
        }

        // 9. Finding and browsing products
        if (query.contains("find") || query.contains("browse") || query.contains("catalog") || query.contains("discover") || query.contains("explore")) {
            return "You can explore products by visiting the Products catalog from the top navigation bar, clicking category icons on the homepage, or reviewing our Featured Deals and New Arrivals sections.";
        }

        // 10. Account login, registration, and store navigation
        if (query.contains("login") || query.contains("register") || query.contains("sign in") || query.contains("sign up") || query.contains("account") || query.contains("profile") || query.contains("password")) {
            return "You can sign in or create an account by clicking 'Sign In' or 'Register' in the top header. " +
                   "An account allows you to save delivery addresses, maintain a persistent wishlist, and view order history. If you forgot your password, use the 'Forgot Password' link on the login page.";
        }

        // 11. Wishlist / Comparison inquiries
        if (query.contains("wishlist") || query.contains("compare")) {
            return "You can save items to your Wishlist by clicking the heart icon on any product, or compare up to 4 items side-by-side using the Compare feature (/compare).";
        }

        // 12. Support / Returns
        if (query.contains("return") || query.contains("refund") || query.contains("support") || query.contains("contact")) {
            return "ShopSphere provides a 7-day easy return policy on eligible delivered items. " +
                   "You can request a return directly from your Order Details page, or contact our support team at support@shopsphere.com.";
        }

        // 13. General shopping terms
        if (query.contains("shop") || query.contains("store") || query.contains("item") || query.contains("deal") || query.contains("brand")) {
            return "ShopSphere offers thousands of verified products across Mobiles, Laptops, Electronics, Fashion, Appliances, and Groceries. Use the top navigation bar or search bar to find what you need!";
        }

        // 14. Restricted scope fallback for general-knowledge or unrelated requests
        return RESTRICTED_SCOPE_MESSAGE;
    }
}
