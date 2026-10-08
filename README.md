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
[![Tests](https://img.shields.io/badge/Tests-53%20Passed%20(100%25)-brightgreen.svg)]()

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

## 🧪 Automated Testing Suite

The application includes 53 comprehensive unit and integration tests with **100% pass rate**:

```powershell
mvn test
```

- `OrderStatusTransitionValidatorTest`: Verifies all valid, invalid, cancellation, return, and terminal state transitions.
- `PasswordResetServiceTest`: Tests token generation, single-use invalidation, expiry checks, and password updates.
- `FileStorageSecurityTest`: Tests path traversal rejection, extension whitelist, MIME matching, and file size limits.
- `CartServiceTest`: Tests cart totals, quantity updates, and stock validations.
- `CouponServiceTest`: Tests coupon calculations, discount caps, and expiry.
- `OrderServiceTest`: Tests pessimistic concurrency order placement and inventory deduction.
- `ProductServiceTest`: Tests product filtering and catalog lookups.
- `AuthenticationIntegrationTest`: Tests user registration and login workflows.
- `SecurityAccessTest`: Tests RBAC endpoint security for admin and public routes.

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
