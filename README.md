# ShopSphere

Full-stack Spring Boot e-commerce backend starter.

## Implemented phases

1. Authentication/profile/password management
2. Product & category management
3. Search and category filtering
4. Persistent shopping cart
5. Wishlist
6. Order checkout and tracking statuses
7. Demo payment endpoint (replace with real gateway in production)
8. Admin dashboard and user controls
9. Reviews and ratings
10. Address management + recommendation endpoint

## Run

```bash
mvn spring-boot:run
```

Open `http://localhost:8080/home.html`.

Demo admin: admin@shopsphere.com / Admin@123

## Production upgrades still required

Role-based endpoint authorization, CSRF/session hardening, DTOs, service layer extraction, PostgreSQL, database migrations, real payment gateway/webhooks, email/OTP, image storage, tests, logging, Docker and CI/CD.
