# Automated Testing & Verification Suite

ShopSphere contains comprehensive automated unit and integration tests verifying business logic, security boundaries, and inventory management.

## 1. Executing Test Suites

Run all automated tests via Maven:

```powershell
mvn clean test
```

---

## 2. Test Coverage Overview

### Unit Test Suites (`src/test/java/com/simson/shopsphere/service/`)
- **`ProductServiceTest`**:
  - Tests price discount calculations on server (`Product.getDiscountedPrice()`).
  - Tests savings computation and slug generation.
  - Tests inventory stock quantity updates and audit logging.
- **`CartServiceTest`**:
  - Verifies rejection of quantities exceeding current warehouse stock.
  - Verifies rejection of zero and negative quantities.
  - Verifies cart summation logic.
- **`CouponServiceTest`**:
  - Verifies percentage vs fixed discount strategies.
  - Verifies maximum discount caps.
  - Verifies minimum order thresholds and expiration date checks.
- **`OrderServiceTest`**:
  - Verifies transactional order placement.
  - Verifies atomic stock deduction (e.g. Stock 10 - 2 purchased = 8 remaining).
  - Verifies rejection of orders with insufficient stock.

### Integration Test Suites (`src/test/java/com/simson/shopsphere/integration/`)
- **`AuthenticationIntegrationTest`**:
  - Tests full user registration flow.
  - Verifies BCrypt password hashing and `ROLE_USER` assignment.
- **`SecurityAccessTest`**:
  - Verifies unauthenticated users cannot access `/admin/**`.
  - Verifies normal users receive `403 Forbidden` on admin endpoints.
  - Verifies administrators have full access to `/admin/dashboard`.
  - Verifies public catalog access without authentication.
