# Database Design & Entity Relationship Specifications

## 1. Relational ER Diagram

```mermaid
erDiagram
    USERS ||--o{ ADDRESSES : "maintains"
    USERS ||--o| CARTS : "owns"
    USERS ||--o| WISHLISTS : "owns"
    USERS ||--o{ ORDERS : "places"
    USERS ||--o{ REVIEWS : "writes"

    CATEGORIES ||--o{ PRODUCTS : "contains"

    CARTS ||--o{ CART_ITEMS : "contains"
    PRODUCTS ||--o{ CART_ITEMS : "referenced_in"

    WISHLISTS ||--o{ WISHLIST_ITEMS : "contains"
    PRODUCTS ||--o{ WISHLIST_ITEMS : "saved_in"

    ORDERS ||--o{ ORDER_ITEMS : "comprises"
    ORDERS ||--o{ PAYMENTS : "paid_via"
    PRODUCTS ||--o{ ORDER_ITEMS : "sold_as"

    PRODUCTS ||--o{ REVIEWS : "evaluated_by"
    
    USERS {
        bigserial id PK
        varchar name
        varchar email UK
        varchar phone
        varchar password
        varchar role
        boolean enabled
        timestamp created_at
        timestamp updated_at
    }

    CATEGORIES {
        bigserial id PK
        varchar name UK
        varchar slug UK
        text description
        varchar image_url
        boolean active
        timestamp created_at
        timestamp updated_at
    }

    PRODUCTS {
        bigserial id PK
        bigint category_id FK
        varchar name
        varchar slug UK
        varchar brand
        numeric price
        numeric discount_percentage
        int stock_quantity
        varchar sku UK
        varchar image_url
        numeric rating
        int review_count
        boolean active
        timestamp created_at
        timestamp updated_at
    }

    ORDERS {
        bigserial id PK
        varchar order_number UK
        bigint user_id FK
        numeric total_amount
        numeric discount_amount
        numeric delivery_charge
        numeric net_amount
        varchar payment_status
        varchar order_status
        varchar payment_method
        text shipping_address_snapshot
        varchar coupon_code
        text notes
        timestamp created_at
        timestamp updated_at
    }

    ORDER_ITEMS {
        bigserial id PK
        bigint order_id FK
        bigint product_id FK
        varchar product_name
        varchar product_sku
        varchar product_image_url
        int quantity
        numeric unit_price
        numeric subtotal
    }

    COUPONS {
        bigserial id PK
        varchar code UK
        varchar discount_type
        numeric discount_value
        numeric minimum_order_amount
        numeric maximum_discount
        timestamp start_date
        timestamp expiry_date
        int usage_limit
        int usage_count
        boolean active
    }

    AUDIT_LOGS {
        bigserial id PK
        varchar admin_email
        varchar action
        varchar entity_name
        varchar entity_id
        text details
        varchar ip_address
        timestamp timestamp
    }
```

---

## 2. Table DDL & Relationships

### `users`
- Stores user credentials and authorization roles (`USER`, `ADMIN`).
- Passwords are strictly hashed with **BCrypt**.
- `email` has a unique constraint to ensure identity isolation.

### `products`
- Maintains catalog metadata, brand, SKU code, real-time inventory count (`stock_quantity`), rating, and discount percentage.
- Indexes placed on `category_id`, `brand`, `active`, `price`, and `rating` for optimized searching and filtering.

### `orders` & `order_items`
- Maintains immutable snapshots of the shipping address and the exact unit price (`unit_price`) at the instant of order placement, preventing retroactive pricing modifications.

### `coupons`
- Supports both `PERCENTAGE` and `FIXED_AMOUNT` discount strategies with configurable validity windows, minimum purchase constraints, and usage caps.
