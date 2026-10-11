# ShopSphere — Modern Full-Stack E-Commerce Platform

**Author / Owner:** SIMSON S  
**Degree / Specialization:** B.Tech Artificial Intelligence & Data Science  
**Project:** Full-Stack Enterprise Capstone Project — ShopSphere

[![Java](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6.x-blue.svg)](https://spring.io/projects/spring-security)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.x-green.svg)](https://www.thymeleaf.org/)
[![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-purple.svg)](https://getbootstrap.com/)
[![Tests](https://img.shields.io/badge/Tests-114%20Passed%20(100%25)-brightgreen.svg)]()

---

## 📌 Project Overview

**ShopSphere** is a high-performance, secure, production-grade e-commerce application engineered from scratch using **Java 21 LTS**, **Spring Boot 3**, **Spring Security 6**, **Spring Data JPA / Hibernate 6**, **PostgreSQL / H2**, and **Thymeleaf HTML5 / Bootstrap 5**.

Designed and developed by **SIMSON S**, ShopSphere combines modern consumer-facing e-commerce features with an enterprise-ready administrative back office, strict state-machine order workflows, concurrency-safe inventory controls, and robust application security.

---

## 🌟 Key Architecture & Capabilities

### 🛒 1. Customer Shopping Experience
- **Catalog & Discovery**: 10 distinct categories (Mobiles, Laptops, Electronics, Fashion, Home Appliances, Groceries, Accessories, Beauty, Sports, Books).
- **Live Search Autocomplete**: Debounced, asynchronous search endpoint (`/api/products/autocomplete`) with live thumbnail dropdown.
- **Multi-Facet Filtering & Sorting**: Real-time faceted search by category, brand, price range, rating, in-stock availability, and discount percentage.
- **Product Details & Smart Recommendations**: Multi-angle image views, verified reviews, stock status indicators, and AI-powered recommendations based on category and price affinity.
- **Product Comparison Engine**: Side-by-side comparison of technical specifications, prices, and ratings with persistent session state.
- **Cart & Guest Session Merging**: Full guest cart capabilities with seamless automatic cart merging into the user's persistent database cart upon login.
- **Promo Coupon System**: Atomic usage increments with minimum order validation, percentage discounts, and maximum discount caps.
- **Multi-Address Book**: Manage multiple shipping addresses with default address designation.
- **Multi-Step Checkout & Idempotency**: Session-bound UUID idempotency tokens to prevent duplicate double-click orders.
- **Order Lifecycle & Visual Timeline**: Track orders through `PLACED` → `CONFIRMED` → `PACKED` → `SHIPPED` → `OUT_FOR_DELIVERY` → `DELIVERED`.
- **Order Cancellation & Return Requests**: Enforced return workflows (`RETURN_REQUESTED` → `RETURN_APPROVED` / `RETURN_REJECTED` → `REFUNDED`) with automatic inventory restoration.
- **Downloadable & Printable Tax Invoices**: Printable GST-compliant tax invoices with breakdown for subtotal, savings, delivery fee, and net total.
- **Dark / Light Mode**: Dynamic theme switcher with `localStorage` persistence and CSS variable support.

### 🛡️ 2. Back-Office Administrative Portal (`/admin`)
- **Executive Real-Time Dashboard**: Live business KPIs (Total Revenue, Today's Sales, Monthly Sales, Total Orders, Active Catalog, Pending Fulfillment, Return Requests, and Low-Stock Warnings) backed by **Chart.js** revenue trends and category share visualizers.
- **Product Management**: Full CRUD, secure multipart file uploads, SKU duplicate prevention, inventory replenishment modals, and active toggles.
- **Category Management**: Create, update, and toggle category visibility.
- **Order Fulfillment & Lifecycle Controls**: Transition orders according to strict state-machine rules.
- **User Account Supervision**: Activate or suspend customer accounts.
- **Coupon Manager**: Create promo discount codes with start/end expiry and usage constraints.
- **Audit Logging**: Immutable audit logs capturing user email, action, entity, entity ID, details, and client IP resolution (handling `X-Forwarded-For` and proxy networks).

### 🔒 3. Enterprise Security & Concurrency Engineering
- **State Machine Transition Validation**: Centralized `OrderStatusTransitionValidator` preventing illegal state jumps (e.g. `PLACED` cannot skip directly to `DELIVERED` or `REFUNDED`).
- **Pessimistic Locking (`@Lock(LockModeType.PESSIMISTIC_WRITE)`)**: Guarantees zero stock overselling during simultaneous high-concurrency checkouts.
- **Token-Based Password Reset**: Single-use 15-minute cryptographically strong reset tokens with automated token invalidation.
- **File Upload Protection**: Filename sanitization, UUID obfuscation, path canonicalization traversal checks, 5MB file cap, and strict MIME/extension whitelist (`jpg`, `jpeg`, `png`, `webp`).
- **CSRF & XSS Protection**: Synchronizer tokens on all state-mutating requests and automatic header injection on AJAX calls.
- **Dynamic Secret Seeding**: Environment-variable-driven admin seeding (`ADMIN_EMAIL`, `ADMIN_PASSWORD`) that only seeds when 0 admins exist and never overwrites existing passwords on startup.

### 🤖 4. ShopSphere AI Assistant (Intelligent Store Chatbot)
- **Modern Floating Widget**: Embedded in all customer-facing views (`index`, `/products`, `/cart`, `/checkout`, `/orders`, etc.) with ShopSphere brand colors, suggested question chips, character counter, mobile-responsive layout, and safe text rendering (XSS protection).
- **Dual-Provider Architecture**:
  - `MockChatProvider`: 100% offline, deterministic provider answering at least 10 domain FAQ topics with zero external API dependencies.
  - `GeminiChatProvider`: Production LLM integration powered by Google Gemini API (`gemini-1.5-flash`), with server-side API key protection and configurable timeouts.
- **Server-Side Domain Guardrails**: Fixed system instruction restricting questions exclusively to ShopSphere product catalog, availability, cart operations, checkout, payment simulation, and order tracking. Prompt injection and jailbreak attempts are safely neutralized.
- **Per-Session Sliding-Window Rate Limiting**: Enforces a strict quota of at most 10 messages per rolling 60-second window per session (returns `429 Too Many Requests`).
- **Bounded In-Memory Session Caching**: Automatically caches identical normalized questions within an active session (30-minute expiry, max 50 entries/session) to eliminate redundant LLM invocations without leaking cross-customer state.
- **Graceful Failure Handling**: If the Gemini provider times out, encounters rate limiting, or has an invalid/missing API key, the chatbot automatically falls back to `MockChatProvider` with zero user disruption.
- **Safety Boundary Note**: The assistant provides store assistance and guidance only; it **never** initiates monetary transactions, executes arbitrary code/SQL, or modifies order states autonomously.

---

## 🏗️ Layered Architecture

```text
Browser / Client (Thymeleaf, Bootstrap 5, Fetch API, Dark Theme)
                         ↓ HTTP / HTTPS (Port 8080)
            Spring Security 6 Gateway (BCrypt, RBAC, CSRF)
                         ↓
               Controllers (MVC & REST Endpoints)
                         ↓
             Business Services (@Transactional, State Engine)
                         ↓
             Spring Data JPA Repositories (Hibernate 6)
                         ↓
       PostgreSQL 16 (Production) / H2 In-Memory (Development)
```

---

## 📦 Package Structure

```text
com.simson.shopsphere
├── config/              # Application & Web MVC Configurations
├── controller/          # Customer MVC & API Controllers
│   ├── admin/           # Admin Portal Controllers
│   └── api/             # RESTful API Endpoints (Swagger / OpenAPI)
├── dto/                 # Data Transfer Objects & Requests
├── entity/              # Normalized JPA Entities
├── exception/           # Custom Exceptions & GlobalExceptionHandler
├── repository/          # Spring Data JPA Repository Interfaces
├── security/            # Spring Security 6, Auth Handlers, UserDetails
├── service/             # Service Interfaces & Business State Machines
│   └── impl/            # Transactional Service Implementations
├── specification/       # JPA Criteria Specifications (Dynamic Filtering)
├── util/                # ClientIpUtil & Helper Utilities
└── ShopSphereApplication.java # Main Application Bootstrap
```

---

## 🚀 Getting Started (Windows PowerShell)

### Prerequisites
- **JDK**: Java 21 LTS or Java 26
- **Maven**: 3.9+
- **Database**: PostgreSQL 16 (or built-in H2 in-memory mode)

### 1. Build and Run the Application

```powershell
# Navigate to project root
cd C:\Users\Sim\.gemini\antigravity\scratch\ShopSphere

# Run the test suite
mvn clean test

# Package the application
mvn package -DskipTests

# Start the Spring Boot application
mvn spring-boot:run
```

Once running, access the portal at:
- **Storefront**: [http://localhost:8080/](http://localhost:8080/)
- **Admin Portal**: [http://localhost:8080/admin](http://localhost:8080/admin)
- **Interactive Swagger Docs**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **H2 Web Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (JDBC URL: `jdbc:h2:mem:shopspheredb`, User: `sa`, Password: empty)

---

## 🔑 Default Credentials

| Role | Email | Password | Purpose |
| :--- | :--- | :--- | :--- |
| **Super Admin** | `admin@shopsphere.com` | `Admin@ShopSphere2026!` | Complete administrative management |
| **Demo Customer** | `rahul@example.com` | `User@123` | Browsing, cart, checkout, tracking |

---

---

## ⚙️ AI Chatbot Configuration & Local Setup

### 1. Zero-Network Default (Mock Provider)
By default, the application runs with the offline deterministic `MockChatProvider`. No API key or internet access is required:
```properties
ai.chatbot.provider=mock
```
This provider is fully functional and deterministically handles queries across all 10 domain topics:
1. Product browsing & catalog navigation
2. Product search & keyword matching
3. Category filters & sidebar refinement
4. Price details & real-time stock availability
5. Adding items to cart & guest session preservation
6. Modifying cart quantities & removing items
7. Multi-step checkout workflow & shipping selection
8. Simulated payment gateways (UPI 12-digit UTR confirmation & Cash on Delivery)
9. Order history & status timeline tracking
10. Account login, registration, and profile navigation

### 2. Enabling Live Google Gemini LLM Provider
To switch to the real Google Gemini LLM:
1. Obtain an API key from Google AI Studio.
2. Set the environment variable in your terminal before launching:
```powershell
$env:CHATBOT_PROVIDER = "gemini"
$env:GEMINI_API_KEY = "your_actual_gemini_api_key_here"
$env:GEMINI_MODEL = "gemini-1.5-flash"
```
Or define in `application.properties`:
```properties
ai.chatbot.provider=gemini
ai.chatbot.gemini-api-key=your_actual_gemini_api_key_here
ai.chatbot.gemini-model=gemini-1.5-flash
```
*Security Note: API keys are strictly kept server-side and are NEVER returned in API responses or sent to the browser.*

### 3. Chatbot REST API Endpoint
- **URL**: `POST /api/v1/chat`
- **Content-Type**: `application/json`
- **Request Body**:
```json
{
  "message": "How do I check product availability?"
}
```
- **Response Envelope (200 OK)**:
```json
{
  "success": true,
  "data": {
    "reply": "Live prices, discount percentages, and current inventory levels are displayed directly on every product card..."
  },
  "error": null
}
```
- **Error Status Codes**:
  - `400 Bad Request`: Message is blank, null, or exceeds 1,000 characters (`VALIDATION_ERROR`).
  - `429 Too Many Requests`: Exceeded 10 requests per rolling 60 seconds per session (`RATE_LIMIT_EXCEEDED`).
  - `503 Service Unavailable`: AI provider unavailable and fallback cannot be produced (`PROVIDER_UNAVAILABLE`).

---

## 🧪 Automated Testing Suite

The application includes 114 comprehensive unit and integration tests with **100% pass rate**:

```powershell
mvn test
```

### Core Business & Security Tests (80 tests)
- `OrderStatusTransitionValidatorTest` (26 tests): Verifies state-machine rules and illegal jumps.
- `SecurityAccessTest` (14 tests): Verifies RBAC role enforcement and public storefront paths.
- `OrderConfirmationAndPaymentIntegrationTest` (8 tests): Verifies end-to-end checkout, payment simulation, and UTR submission.
- `PasswordResetServiceTest` (6 tests): Single-use token generation, expiry, and password resetting.
- `PaymentServiceTest` (5 tests): UPI UTR validation, COD status transitions, and payment logging.
- `FileStorageSecurityTest` (5 tests): Path traversal defense, file type whitelisting, and size restrictions.
- `AddressServiceTest` (4 tests): Multi-address management and default address enforcement.
- `CartServiceTest` (3 tests): Cart arithmetic, quantity caps, and stock limits.
- `CouponServiceTest` (3 tests): Min order constraints, percentage discounts, and max deduction caps.
- `ProductServiceTest` (3 tests): Faceted criteria query execution and pagination.
- `OrderServiceTest` (2 tests): Pessimistic write locking and inventory decrement.
- `AuthenticationIntegrationTest` (1 test): Customer registration and authentication lifecycle.

### AI Chatbot Tests (34 tests)
- `MockChatProviderTest` (13 tests): Validates all 10 FAQ domain answers, greetings, and restricted-scope response for off-topic queries.
- `ChatServiceTest` (7 tests): Input validation, rate limiting, session cache isolation, and transparent Gemini fallback to Mock.
- `ApiChatControllerTest` (5 tests): MockMvc integration tests for 200 envelopes, 400 validation errors, 429 rate limit triggers, and guest access.
- `GeminiChatProviderTest` (4 tests): Missing key validation, response parsing, 403 HTTP error masking, and network timeout handling without secret leakage.
- `SessionChatCacheTest` (3 tests): Question normalization, cache hits, and strict session isolation.
- `SessionRateLimiterTest` (2 tests): Rolling 60s window limit (10 requests) and multi-session independence.

---

## 🏛️ Architecture Note: Spring Boot vs. Servlet Specification
The ShopSphere platform is built on **Spring Boot 3.3.4 (Java 21, Spring MVC, Spring Security 6, Spring Data JPA)**.
While the academic project guideline originally outlined a raw Servlet-based architecture (`HttpServlet` / `web.xml`), ShopSphere utilizes Spring Boot's modern MVC controller and filter chain (`@RestController`, `@Service`, Spring Security `SecurityFilterChain`, `CookieCsrfTokenRepository`).
The AI Chatbot integration seamlessly follows this Spring Boot paradigm via `ApiChatController` at `POST /api/v1/chat`, dependency-injected `ChatProvider` implementations, thread-safe session-level sliding-window rate limiters, and in-memory expiring caches without introducing conflicting legacy servlets or framework migrations.

---

## 📚 Documentation Directory

- [System Architecture](docs/architecture.md)
- [Database Schema & ER Model](docs/database-design.md)
- [REST API Specifications](docs/api-documentation.md)
- [Security & Concurrency Architecture](docs/security.md)
- [Setup & Deployment Guide](docs/setup.md)
- [Automated Testing Documentation](docs/testing.md)
- [User & Admin Guide](docs/user-guide.md)
- [Capstone Presentation & Viva Reference](docs/capstone-presentation.md)

---

**ShopSphere** &copy; 2026. Designed and Developed by **SIMSON S**.

