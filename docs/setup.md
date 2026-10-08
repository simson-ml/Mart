# Installation & Local Setup Guide

## System Requirements
- **Java**: JDK 21 LTS (or JDK 21+)
- **Build Tool**: Apache Maven 3.9+
- **Database (Optional for Dev)**: PostgreSQL 16+ (Defaults to in-memory H2 database for seamless development)
- **Containerization (Optional)**: Docker & Docker Compose

---

## 1. Quick Start (Development Profile with H2)

Run the project directly using Maven from Windows PowerShell:

```powershell
# Navigate into the project root directory
cd C:\Users\Sim\.gemini\antigravity\scratch\ShopSphere

# Run Spring Boot application
mvn spring-boot:run
```

Once started, open your browser and navigate to:
- Storefront: `http://localhost:8080/`
- Admin Dashboard: `http://localhost:8080/admin`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 Web Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:shopspheredb`, User: `sa`, Password: empty)

---

## 2. Seed Accounts & Credentials

| Role | Email | Password | Access |
| :--- | :--- | :--- | :--- |
| **Super Admin** | `admin@shopsphere.com` | `Admin@ShopSphere2026!` | Full Administrative Dashboard & Management |
| **Demo Customer** | `rahul@example.com` | `User@123` | Customer Cart, Wishlist, Checkout, Orders |

> **Viva / Demo Tip**: The login page (`/login`) includes **Quick-Fill Buttons** to instantly log in as Admin or Customer during viva demonstrations.

---

## 3. Running with PostgreSQL (Production Profile)

1. Start PostgreSQL service on `localhost:5432` with database `shopspheredb`.
2. Launch with the `prod` profile:

```powershell
mvn spring-boot:run -Dspring-boot.run.profiles=prod -Dspring-boot.run.arguments="--DB_HOST=localhost --DB_PORT=5432 --DB_NAME=shopspheredb --DB_USERNAME=postgres --DB_PASSWORD=your_password"
```

---

## 4. Running with Docker Compose

To spin up both PostgreSQL and the Spring Boot application container simultaneously:

```powershell
docker compose up --build
```
Access the application on `http://localhost:8080`.
