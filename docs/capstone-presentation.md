# Capstone Project Presentation & Viva Defense

---

### Slide 1: Project Title & Identity
**Project Title**: ShopSphere — Enterprise-Grade Full-Stack E-Commerce Platform  
**Owner / Developer**: SIMSON S  
**Department**: B.Tech Artificial Intelligence & Data Science  
**Domain**: Full-Stack Enterprise Software Engineering, Distributed Systems, Transactional Integrity & Web Security  

---

### Slide 2: Problem Statement
Modern e-commerce requires real-time stock synchronization, zero tolerance for inventory overselling, defense against client-side price tampering, strict order lifecycle management, and frictionless customer shopping with guest persistence. ShopSphere solves these challenges with a resilient, secure Spring Boot 3 enterprise architecture.

---

### Slide 3: Existing System Limitations
- Vulnerability to client-side price/discount tampering via browser inspection.
- Concurrency race conditions resulting in negative stock balances.
- Unconstrained order state transitions (e.g. jumping directly from `PLACED` to `DELIVERED`).
- Session loss for guest shoppers before login.
- Insecure file upload endpoints vulnerable to path traversal.
- Lack of immutable audit logging for administrative actions.

---

### Slide 4: Proposed Solution (ShopSphere)
ShopSphere introduces a clean layered, Spring Boot 3 enterprise application utilizing Spring Security 6 with role-based authorization, BCrypt encryption, dynamic JPA specifications, pessimistic database locking, centralized order state transition validation, guest session merging, and responsive Thymeleaf server-side rendering with dark/light mode.

---

### Slide 5: Core Objectives & Deliverables
1. **Zero Mock Features**: Every single feature, button, search filter, and chart is 100% functional and database-backed.
2. **Strict State Machine**: Centralized `OrderStatusTransitionValidator` enforcing valid lifecycle transitions (`PLACED` → `CONFIRMED` → `PACKED` → `SHIPPED` → `OUT_FOR_DELIVERY` → `DELIVERED` and return/cancellation paths).
3. **Pessimistic Concurrency Locking**: `@Lock(LockModeType.PESSIMISTIC_WRITE)` guarantees atomic stock deduction and zero overselling.
4. **Guest Cart & Cart Merging**: Unauthenticated users can build carts; their session cart is seamlessly merged into their persistent database cart upon login.
5. **Token-Based Password Reset**: Single-use 15-minute cryptographically strong reset tokens.
6. **Executive Dashboard**: Real-time business KPIs (Daily & Monthly Sales, Low-Stock Warnings, Return Requests) with Chart.js visualization.
7. **Automated Testing Suite**: 53 automated unit and integration tests with 100% pass rate.

---

### Slide 6: Technology Stack
- **Backend**: Java 21 LTS, Spring Boot 3.3.4, Spring MVC, Spring Data JPA, Spring Security 6, Hibernate 6, Flyway, Maven
- **Database**: PostgreSQL 16 (Primary Production), H2 In-Memory (Development & Testing)
- **Frontend**: Thymeleaf 3, HTML5, Modern CSS3 (CSS Variables for Dark/Light Theme), Bootstrap 5.3, Bootstrap Icons, Chart.js, Fetch API
- **API & Docs**: Springdoc OpenAPI 3.0 / Swagger UI, Docker, Docker Compose

---

### Slide 7: Database Design & Schema Architecture
Normalized 3NF relational schema containing 15 relational tables:
- `users`, `categories`, `products`, `addresses`, `carts`, `cart_items`, `wishlists`, `wishlist_items`, `orders`, `order_items`, `payments`, `reviews`, `coupons`, `audit_logs`, and `password_reset_tokens`.
- Primary keys via `BIGSERIAL`, indexed foreign keys, and unique constraint enforcement for optimal query performance.

---

### Slide 8: Key Functional Modules
- **Dynamic Search & Autocomplete**: Asynchronous debounced autocomplete (`/api/products/autocomplete`) and multi-facet filtering.
- **Product Details & Smart Recommendations**: Multi-angle image views, verified reviews, and AI-driven category/price recommendations.
- **Side-by-Side Product Comparison**: Compare specifications, ratings, and pricing across products.
- **Checkout & Idempotency**: Session-bound UUID idempotency tokens to prevent duplicate order submissions.
- **GST Tax Invoices**: Formatted printable tax invoices with subtotal, discount, delivery fee, and net total breakdowns.
- **Admin Control Center**: Product CRUD, inventory replenishment modals, category controls, order fulfillment, coupon engine, and user supervision.

---

### Slide 9: Security Architecture
- BCrypt password hashing with 10 salt rounds.
- Server-side price and discount computation (client price parameters discarded).
- Parameterized SQL queries via JPA to eliminate SQL injection.
- Automatic CSRF protection and sanitized file uploads preventing path traversal and executable uploads.
- Client IP resolution from `X-Forwarded-For` and proxy headers for immutable audit logging.

---

### Slide 10: Testing & Quality Assurance
- **53 Automated Tests** across unit and integration levels.
- `OrderStatusTransitionValidatorTest`: Verifies all valid, invalid, cancellation, return, and terminal state transitions.
- `PasswordResetServiceTest`: Validates secure token generation, expiration, and password updates.
- `FileStorageSecurityTest`: Validates traversal blocking, MIME type validation, and file size limits.
- `CartServiceTest`, `CouponServiceTest`, `OrderServiceTest`, `ProductServiceTest`, `SecurityAccessTest`, `AuthenticationIntegrationTest`.

---

### Slide 11: Viva Questions & Technical Defense

**Q1: How does ShopSphere prevent stock overselling during simultaneous checkouts?**  
*Answer*: We utilize JPA pessimistic locking (`@Lock(LockModeType.PESSIMISTIC_WRITE)`) in `ProductRepository.findByIdWithLock(productId)` inside the transactional `placeOrder` method. The database row lock ensures sequential, synchronized stock verification and deduction.

**Q2: How does the order state transition validation work?**  
*Answer*: We implemented a centralized `OrderStatusTransitionValidator` using an `EnumMap<OrderStatus, Set<OrderStatus>>` transition matrix. Any illegal transition (such as skipping from `PLACED` to `DELIVERED`) throws an `InvalidOrderStatusException` and is blocked at the service level.

**Q3: How does guest cart merging work upon login?**  
*Answer*: Unauthenticated users have their cart stored in the HTTP session (`SESSION_GUEST_CART`). When they authenticate, `CustomAuthenticationSuccessHandler` intercepts the session, retrieves the guest items, merges them via `cartService.mergeGuestCart(user, guestCart)`, and removes the temporary session attribute.

**Q4: How does ShopSphere protect against file upload attacks?**  
*Answer*: `FileStorageServiceImpl` strictly verifies file sizes (max 5MB), checks extensions against a whitelist (`jpg, jpeg, png, webp`), checks MIME types, cleans the filename with `StringUtils.cleanPath()`, generates a random UUID for the physical storage filename, and resolves paths inside the configured base upload directory to prevent directory traversal (`..`).

---

**ShopSphere** &copy; 2026. Designed and Developed by **SIMSON S**.
