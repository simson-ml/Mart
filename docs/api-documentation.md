# ShopSphere REST API & Swagger Documentation

ShopSphere exposes OpenAPI 3.0-compliant endpoints for seamless client interaction and developer integration.

## Interactive Swagger UI
When running the application, access the interactive Swagger portal at:
```
http://localhost:8080/swagger-ui.html
```
OpenAPI JSON Specification:
```
http://localhost:8080/api-docs
```

---

## Key REST Endpoints

### 1. Products API
- `GET /api/products` — Dynamic faceted search and catalog filtering.
  - Query parameters: `keyword`, `category`, `brand`, `minPrice`, `maxPrice`, `minRating`, `inStockOnly`, `minDiscount`, `sortBy`, `page`, `size`
- `GET /api/products/{id}` — Retrieve complete product details.
- `GET /api/products/brands` — Get list of all distinct product brands.

### 2. Cart API (Authenticated)
- `GET /api/cart` — Get current user cart snapshot with discounts and totals.
- `POST /api/cart/add?productId={id}&quantity={qty}` — Add item to cart.
- `POST /api/cart/update?productId={id}&quantity={qty}` — Adjust item quantity.
- `DELETE /api/cart/remove/{productId}` — Remove an item from the cart.
- `GET /api/cart/count` — Get item count for navbar badge.

### 3. Wishlist API (Authenticated)
- `POST /api/wishlist/toggle/{productId}` — Toggle wishlist state for product.
- `GET /api/wishlist/count` — Get total items saved in wishlist.

### 4. Coupon API (Authenticated)
- `GET /api/coupons/validate?code={code}&orderAmount={amount}` — Compute discount amount for coupon.

### 5. AI Chatbot API (Public / Authenticated)
- `POST /api/v1/chat` — Send customer questions to the ShopSphere AI Assistant.

**Request Envelope:**
```json
{
  "message": "How can I find products in a category?"
}
```

**Successful Response (HTTP 200):**
```json
{
  "success": true,
  "data": {
    "reply": "You can browse products and use the available category filters on the /products page."
  },
  "error": null
}
```

**Validation Error Response (HTTP 400):**
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Please enter a valid message."
  }
}
```

**Rate Limit Exceeded Response (HTTP 429):**
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "RATE_LIMIT_EXCEEDED",
    "message": "Rate limit exceeded. Maximum 10 requests per minute allowed."
  }
}
```

**Provider Unavailable Response (HTTP 503):**
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "PROVIDER_UNAVAILABLE",
    "message": "The AI assistant is temporarily unavailable. Please try again shortly."
  }
}
```

