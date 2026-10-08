# ShopSphere (ShopSphere) — Architectural Blueprint

## 1. Architectural Style
ShopSphere is architected following the **Clean Layered Architecture** paradigm, ensuring clear separation of concerns, high maintainability, loose coupling, and strict encapsulation of business logic within transactional domain services.

```
+-------------------------------------------------------------------+
|                        Client Browser / Web                       |
|   (Thymeleaf Server-Rendered HTML5, CSS3, Bootstrap 5, JS Fetch)  |
+---------------------------------+---------------------------------+
                                  | HTTP / HTTPS
                                  v
+-------------------------------------------------------------------+
|                     Spring Security 6 Gateway                     |
|    (BCrypt, Role-Based Access Control: ROLE_USER vs ROLE_ADMIN,   |
|         CSRF Protection, Session Management, Remember-Me)         |
+---------------------------------+---------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
|                    Presentation / Controller Layer                |
|      (HomeController, ProductController, CartController,          |
|    CheckoutController, OrderController, AdminDashboardController,  |
|          AdminProductController, GlobalExceptionHandler)          |
+---------------------------------+---------------------------------+
                                  | DTOs / View Models
                                  v
+-------------------------------------------------------------------+
|                      Business Service Layer                       |
|       (OrderService, ProductService, CartService, UserService,    |
|       CouponService, PaymentService, FileStorageService)          |
+---------------------------------+---------------------------------+
                                  | Entities / JPA Specifications
                                  v
+-------------------------------------------------------------------+
|                     Data Access / Repository Layer                |
|         (Spring Data JPA Repositories + Hibernate 6 ORM)          |
+---------------------------------+---------------------------------+
                                  | JDBC / Connection Pool (HikariCP)
                                  v
+-------------------------------------------------------------------+
|                         Database Layer                            |
|       PostgreSQL 16 (Production) / H2 In-Memory (Development)     |
|          Automated Schema Migrations via Flyway (V1, V2)          |
+-------------------------------------------------------------------+
```

---

## 2. Core Package Structure

```
com.simson.shopsphere
├── config
│   ├── WebMvcConfig.java             # Static resource handling & file uploads
│   └── OpenApiConfig.java            # Springdoc OpenAPI 3.0 / Swagger UI
├── controller
│   ├── GlobalModelAttributes.java    # Global categories, cart & user context
│   ├── HomeController.java           # Homepage & promotions
│   ├── AuthController.java           # Register, login, forgot password
│   ├── ProductController.java        # Multi-facet search, filtering, details
│   ├── CartController.java           # Cart management & coupon applications
│   ├── WishlistController.java       # Wishlist operations & move to cart
│   ├── AddressController.java        # Multi-address management
│   ├── CheckoutController.java       # Checkout workflow & order placement
│   ├── OrderController.java          # Order history, details & tracking
│   ├── ReviewController.java         # Customer verified reviews
│   ├── ProfileController.java        # User profile & password updates
│   ├── api/                          # REST API Endpoints for AJAX & Swagger
│   │   ├── ApiProductController.java
│   │   ├── ApiCartController.java
│   │   ├── ApiWishlistController.java
│   │   └── ApiCouponController.java
│   └── admin/                        # Admin Portal (Secured with ROLE_ADMIN)
│       ├── AdminDashboardController.java
│       ├── AdminProductController.java
│       ├── AdminCategoryController.java
│       ├── AdminOrderController.java
│       ├── AdminUserController.java
│       ├── AdminCouponController.java
│       └── AdminAuditController.java
├── dto                               # Strongly typed Data Transfer Objects
├── entity                            # Normalized JPA Relational Entities & Enums
├── exception                         # Global exception advice & domain errors
├── repository                        # Spring Data JPA interfaces
├── security                          # CustomUserDetails, Services & SecurityConfig
├── service                           # Business interfaces
│   └── impl                          # Transactional service implementations
└── specification                     # Dynamic JPA Specifications for filtering
```

---

## 3. Key Design Principles

1. **Defense in Depth**:
   - Price calculations are executed strictly server-side inside `Product.getDiscountedPrice()`. Any client-side price payload is completely ignored.
   - Stock is reserved atomically inside `@Transactional` order placement methods.
2. **Auditability**:
   - Every administrative modification (product creation, inventory alteration, status change) writes an immutable record to the `audit_logs` table.
3. **Resilience & Safe State Transitions**:
   - Orders follow a strict state machine (`PLACED` -> `CONFIRMED` -> `PACKED` -> `SHIPPED` -> `OUT_FOR_DELIVERY` -> `DELIVERED`).
   - Order cancellations restore the reserved inventory immediately.
