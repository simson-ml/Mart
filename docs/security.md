# Security Engineering & Protection Measures

ShopSphere implements enterprise-grade security mechanisms designed according to OWASP Top 10 guidelines:

---

## 1. Authentication & Role-Based Authorization
- **BCrypt Password Hashing**: All customer and administrator passwords are encrypted using `BCryptPasswordEncoder` with standard 10 salt rounds. Plaintext credentials are never persisted or logged.
- **Role Isolation**: Strict privilege boundaries between `ROLE_USER` and `ROLE_ADMIN`.
  - `/admin/**` endpoints are protected with `.hasRole("ADMIN")`.
  - Unauthorized access attempts are intercepted and return clean `403 Forbidden` error views without leaking application context.
- **Account Disablement Check**: Disabled user accounts are halted at authentication time inside `CustomUserDetailsService`.

---

## 2. Protection Against Common Web Vulnerabilities

| Attack Vector | Countermeasure Implemented |
| :--- | :--- |
| **SQL Injection** | Exclusively parameterized queries via Spring Data JPA and Hibernate Criteria Builder API (`ProductSpecification`). |
| **Cross-Site Scripting (XSS)** | Thymeleaf HTML context-aware variable escaping (`th:text`). User inputs are sanitized prior to persistence. |
| **Cross-Site Request Forgery (CSRF)** | CSRF token validation active on all state-changing `POST` requests and checkout transactions. |
| **Path Traversal Attacks** | Image uploads sanitize filenames via `StringUtils.cleanPath()` and reject files containing `..`, `/`, or `\`. File extensions are restricted to `jpg, jpeg, png, webp, svg`. |
| **Price Tampering** | Price calculations, discounts, taxes, and shipping fees are computed **strictly on the backend**. Frontend price parameters are completely discarded. |
| **Over-selling / Race Conditions** | Order placement operates within `@Transactional` boundaries with stock re-verification prior to commitment. |
| **Sensitive Credential Leakage** | Database passwords and administrative accounts are supplied through environment variables (`ADMIN_EMAIL`, `ADMIN_PASSWORD`, `DB_PASSWORD`). |

---

## 3. Administrative Audit Trail
All administrative modifications (creating products, updating inventory, transitioning order statuses) record an immutable audit entry in the `audit_logs` table detailing:
- Admin email address
- Exact action performed (e.g., `UPDATE_STOCK`, `UPDATE_ORDER_STATUS`)
- Target entity and entity ID
- Operational details and client IP address
- Exact UTC timestamp
