-- ==============================================================================
-- SHOPSPHERE DATABASE SCHEMA - V1 INITIALIZATION
-- ==============================================================================

-- 1. Users Table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(20),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Categories Table
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(120) NOT NULL UNIQUE,
    description TEXT,
    image_url VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. Products Table
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT NOT NULL REFERENCES categories(id),
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(280) NOT NULL UNIQUE,
    description TEXT,
    brand VARCHAR(100) NOT NULL,
    price NUMERIC(12, 2) NOT NULL,
    discount_percentage NUMERIC(5, 2) NOT NULL DEFAULT 0.00,
    stock_quantity INT NOT NULL DEFAULT 0,
    sku VARCHAR(50) NOT NULL UNIQUE,
    image_url VARCHAR(500),
    rating NUMERIC(3, 2) NOT NULL DEFAULT 0.00,
    review_count INT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 4. Addresses Table
CREATE TABLE addresses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    pincode VARCHAR(20) NOT NULL,
    address_type VARCHAR(20) NOT NULL DEFAULT 'HOME',
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 5. Carts Table
CREATE TABLE carts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 6. Cart Items Table
CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_cart_product UNIQUE (cart_id, product_id)
);

-- 7. Wishlists Table
CREATE TABLE wishlists (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 8. Wishlist Items Table
CREATE TABLE wishlist_items (
    id BIGSERIAL PRIMARY KEY,
    wishlist_id BIGINT NOT NULL REFERENCES wishlists(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_wishlist_product UNIQUE (wishlist_id, product_id)
);

-- 9. Orders Table
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    total_amount NUMERIC(12, 2) NOT NULL,
    discount_amount NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    delivery_charge NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    net_amount NUMERIC(12, 2) NOT NULL,
    payment_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    order_status VARCHAR(30) NOT NULL DEFAULT 'PLACED',
    payment_method VARCHAR(30) NOT NULL DEFAULT 'COD',
    shipping_address_snapshot TEXT NOT NULL,
    coupon_code VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 10. Order Items Table
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id),
    product_name VARCHAR(255) NOT NULL,
    product_sku VARCHAR(50) NOT NULL,
    product_image_url VARCHAR(500),
    quantity INT NOT NULL,
    unit_price NUMERIC(12, 2) NOT NULL,
    subtotal NUMERIC(12, 2) NOT NULL
);

-- 11. Payments Table
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    transaction_id VARCHAR(100) NOT NULL UNIQUE,
    payment_method VARCHAR(30) NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    payment_status VARCHAR(30) NOT NULL,
    payment_gateway_response TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 12. Reviews Table
CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    verified_purchase BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_product_user_review UNIQUE (product_id, user_id)
);

-- 13. Coupons Table
CREATE TABLE coupons (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    discount_type VARCHAR(20) NOT NULL,
    discount_value NUMERIC(10, 2) NOT NULL,
    minimum_order_amount NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    maximum_discount NUMERIC(10, 2),
    start_date TIMESTAMP NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    usage_limit INT NOT NULL DEFAULT 100,
    usage_count INT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 14. Audit Logs Table
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    admin_email VARCHAR(150) NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity_name VARCHAR(100) NOT NULL,
    entity_id VARCHAR(100),
    details TEXT,
    ip_address VARCHAR(50),
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 15. Password Reset Tokens Table
CREATE TABLE password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(100) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    expiry_date TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_brand ON products(brand);
CREATE INDEX idx_products_active ON products(active);
CREATE INDEX idx_products_price ON products(price);
CREATE INDEX idx_products_rating ON products(rating);
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(order_status);
CREATE INDEX idx_orders_number ON orders(order_number);
CREATE INDEX idx_reviews_product ON reviews(product_id);
CREATE INDEX idx_audit_timestamp ON audit_logs(timestamp);
CREATE INDEX idx_password_reset_token ON password_reset_tokens(token);

