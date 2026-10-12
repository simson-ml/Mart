-- ==============================================================================
-- SHOPSPHERE DATABASE MIGRATION - V3 SELLER MULTI-TENANCY SUPPORT
-- ==============================================================================

-- 1. Add seller_id column to products table
ALTER TABLE products ADD COLUMN seller_id BIGINT;
ALTER TABLE products ADD CONSTRAINT fk_products_seller FOREIGN KEY (seller_id) REFERENCES users(id);
CREATE INDEX idx_products_seller ON products(seller_id);

-- 2. Seed Default Verified Seller Merchant Account
-- Password: User@123 ($2a$10$eACCYoNO3smqC84hW2OGCOvEDGz3Pej.g8y4e9J5b82QzYn9Fomre)
INSERT INTO users (id, name, email, phone, password, role, enabled, created_at, updated_at) VALUES
(6, 'Apex Gadgets Store', 'seller@shopsphere.com', '+91 9876543220', '$2a$10$eACCYoNO3smqC84hW2OGCOvEDGz3Pej.g8y4e9J5b82QzYn9Fomre', 'SELLER', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 3. Associate all pre-existing catalog listings with the seeded seller
UPDATE products SET seller_id = 6 WHERE seller_id IS NULL;
