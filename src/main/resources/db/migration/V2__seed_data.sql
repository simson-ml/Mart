-- ==============================================================================
-- SHOPSPHERE DATABASE SEED DATA - V2
-- ==============================================================================

-- 1. Seed Users (Passwords: Admin@123 and User@123 encrypted with BCrypt)
-- BCrypt hash for 'Admin@123': $2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a
-- BCrypt hash for 'User@123':  $2a$10$eACCYoNO3smqC84hW2OGCOvEDGz3Pej.g8y4e9J5b82QzYn9Fomre
INSERT INTO users (id, name, email, phone, password, role, enabled, created_at, updated_at) VALUES
(1, 'ShopSphere Administrator', 'admin@shopsphere.com', '+91 9876543210', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'ADMIN', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Rahul Sharma', 'rahul@example.com', '+91 9876543211', '$2a$10$eACCYoNO3smqC84hW2OGCOvEDGz3Pej.g8y4e9J5b82QzYn9Fomre', 'USER', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Priya Patel', 'priya@example.com', '+91 9876543212', '$2a$10$eACCYoNO3smqC84hW2OGCOvEDGz3Pej.g8y4e9J5b82QzYn9Fomre', 'USER', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Amit Verma', 'amit@example.com', '+91 9876543213', '$2a$10$eACCYoNO3smqC84hW2OGCOvEDGz3Pej.g8y4e9J5b82QzYn9Fomre', 'USER', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Sneha Reddy', 'sneha@example.com', '+91 9876543214', '$2a$10$eACCYoNO3smqC84hW2OGCOvEDGz3Pej.g8y4e9J5b82QzYn9Fomre', 'USER', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 2. Seed Addresses
INSERT INTO addresses (id, user_id, full_name, phone, address_line1, address_line2, city, state, pincode, address_type, is_default, created_at, updated_at) VALUES
(1, 2, 'Rahul Sharma', '+91 9876543211', 'Flat 402, Green Acres Residency', 'MG Road, Sector 14', 'Bengaluru', 'Karnataka', '560001', 'HOME', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, 'Rahul Sharma', '+91 9876543211', 'Tech Park Block B, 5th Floor', 'Outer Ring Road', 'Bengaluru', 'Karnataka', '560103', 'WORK', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 3, 'Priya Patel', '+91 9876543212', 'A-12, Shanti Niketan Society', 'Near City Center Mall', 'Mumbai', 'Maharashtra', '400001', 'HOME', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 3. Seed Categories (10 Categories with High Quality Images)
INSERT INTO categories (id, name, slug, description, image_url, active, created_at, updated_at) VALUES
(1, 'Mobiles', 'mobiles', 'Latest smartphones, 5G devices, and feature phones from top brands.', 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=400&q=80', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Laptops', 'laptops', 'High-performance laptops, ultrabooks, and gaming powerhouses.', 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=400&q=80', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Electronics', 'electronics', 'Audio systems, smart TVs, cameras, and smart home gadgets.', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=400&q=80', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Fashion', 'fashion', 'Trendy apparel, footwear, and designer clothing for men and women.', 'https://images.unsplash.com/photo-1445205170230-053b83016050?auto=format&fit=crop&w=400&q=80', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Home Appliances', 'home-appliances', 'Refrigerators, washing machines, microwaves, and air conditioners.', 'https://images.unsplash.com/photo-1556911220-e15b29be8c8f?auto=format&fit=crop&w=400&q=80', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Groceries', 'groceries', 'Daily essentials, gourmet food, organic produce, and staples.', 'https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=400&q=80', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'Accessories', 'accessories', 'Smartwatches, headphones, chargers, cables, and cases.', 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=400&q=80', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 'Beauty', 'beauty', 'Skincare, haircare, fragrances, and luxury wellness products.', 'https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?auto=format&fit=crop&w=400&q=80', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 'Sports', 'sports', 'Fitness equipment, sportswear, outdoor gear, and accessories.', 'https://images.unsplash.com/photo-1517838277536-f5f99be501cd?auto=format&fit=crop&w=400&q=80', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 'Books', 'books', 'Bestsellers, academic references, fiction, and competitive exam books.', 'https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&w=400&q=80', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 4. Seed Products (47 Products across all 10 categories with realistic photos and discounts)
INSERT INTO products (id, category_id, name, slug, description, brand, price, discount_percentage, stock_quantity, sku, image_url, rating, review_count, active, created_at, updated_at) VALUES
-- Mobiles (Cat 1)
(1, 1, 'Samsung Galaxy S24 Ultra 5G (Titanium Gray, 256GB)', 'samsung-galaxy-s24-ultra-5g', 'Experience Galaxy AI with the pinnacle of smartphone engineering. Dynamic AMOLED 2X, 200MP Quad Tele System, and Snapdragon 8 Gen 3 for Galaxy.', 'Samsung', 129999.00, 15.00, 25, 'MOB-SAM-S24U-256', 'https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?auto=format&fit=crop&w=600&q=80', 4.80, 142, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 1, 'Apple iPhone 15 Pro Max (Natural Titanium, 256GB)', 'apple-iphone-15-pro-max', 'Forged in titanium and featuring the groundbreaking A17 Pro chip, a customizable Action button, and the most versatile iPhone camera system.', 'Apple', 159900.00, 10.00, 18, 'MOB-APP-IP15PM-256', 'https://images.unsplash.com/photo-1695048133142-1a20484d2569?auto=format&fit=crop&w=600&q=80', 4.90, 230, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 1, 'Google Pixel 8 Pro (Bay Blue, 128GB)', 'google-pixel-8-pro', 'Google Tensor G3 powers cutting-edge AI features, pro-level cameras, and 7 years of OS updates with Super Actua display.', 'Google', 93999.00, 20.00, 14, 'MOB-GOO-PX8P-128', 'https://images.unsplash.com/photo-1598327105666-5b89351aff97?auto=format&fit=crop&w=600&q=80', 4.60, 89, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 1, 'OnePlus 12 5G (Flowy Emerald, 16GB RAM, 512GB)', 'oneplus-12-5g', 'Fast and Smooth evolved with 4th Gen Hasselblad Camera for Mobile, Snapdragon 8 Gen 3, and 100W SUPERVOOC charging.', 'OnePlus', 69999.00, 15.00, 30, 'MOB-ONE-12-512', 'https://images.unsplash.com/photo-1565849904461-04a58ad377e0?auto=format&fit=crop&w=600&q=80', 4.70, 115, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 1, 'Xiaomi 14 Ultra (Leica Quad Camera, 512GB)', 'xiaomi-14-ultra', 'Legendary mobile photography with 1-inch sensor, stepless variable aperture, Snapdragon 8 Gen 3, and WQHD+ AMOLED display.', 'Xiaomi', 99999.00, 12.00, 20, 'MOB-XIA-14U-512', 'https://images.unsplash.com/photo-1580910051074-3eb694886505?auto=format&fit=crop&w=600&q=80', 4.70, 68, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 1, 'Nothing Phone (2) (Dark Grey, 12GB RAM, 256GB)', 'nothing-phone-2', 'Unique Glyph interface with upgraded Snapdragon 8+ Gen 1 chip, 50 MP dual rear cameras, and 6.7-inch flexible LTPO OLED display.', 'Nothing', 44999.00, 25.00, 22, 'MOB-NOT-PH2-256', 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=600&q=80', 4.50, 94, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Laptops (Cat 2)
(7, 2, 'Apple MacBook Air M3 (15.3-inch, 16GB RAM, 512GB SSD)', 'apple-macbook-air-m3-15', 'Supercharged by the next-generation M3 chip, impossibly thin design, up to 18 hours of battery life, and Liquid Retina display.', 'Apple', 154900.00, 10.00, 12, 'LAP-APP-M3-15', 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?auto=format&fit=crop&w=600&q=80', 4.90, 78, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 2, 'Dell XPS 14 OLED (Intel Core Ultra 7, 32GB RAM, 1TB SSD)', 'dell-xps-14-oled', 'Crafted with machined aluminum and Gorilla Glass 3. 3.2K OLED InfinityEdge touch display with NVIDIA GeForce RTX 4050.', 'Dell', 189990.00, 15.00, 8, 'LAP-DEL-XPS14-1TB', 'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?auto=format&fit=crop&w=600&q=80', 4.70, 42, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 2, 'ASUS ROG Zephyrus G16 Gaming Laptop (RTX 4070, 16GB)', 'asus-rog-zephyrus-g16', 'Ultra-slim AI gaming laptop with 2.5K 240Hz OLED display, Intel Core Ultra 9 processor, and ROG Intelligent Cooling.', 'ASUS', 174990.00, 20.00, 6, 'LAP-ASU-ROG-G16', 'https://images.unsplash.com/photo-1603302576837-37561b2e2302?auto=format&fit=crop&w=600&q=80', 4.80, 64, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 2, 'Lenovo ThinkPad X1 Carbon Gen 12 (Core Ultra 7, 16GB)', 'lenovo-thinkpad-x1-carbon-gen12', 'The quintessential business ultrabook with carbon-fiber weave chassis, Dolby Atmos audio, and military-grade durability.', 'Lenovo', 162900.00, 15.00, 15, 'LAP-LEN-X1C-G12', 'https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?auto=format&fit=crop&w=600&q=80', 4.60, 31, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, 2, 'HP Spectre x360 2-in-1 Touch Laptop (16GB, 1TB SSD)', 'hp-spectre-x360-touch-laptop', 'Stunning gem-cut design, 2.8K OLED touch display, Intel Core Ultra 7 processor, and included HP tilt stylus pen.', 'HP', 149990.00, 18.00, 10, 'LAP-HP-SPEC-1TB', 'https://images.unsplash.com/photo-1541807084-5c52b6b3adef?auto=format&fit=crop&w=600&q=80', 4.70, 53, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Electronics (Cat 3)
(12, 3, 'Sony WH-1000XM5 Wireless Noise Cancelling Headphones', 'sony-wh-1000xm5-headphones', 'Industry-leading noise cancellation with two processors and 8 microphones. Hi-Res Audio wireless and 30-hour battery life.', 'Sony', 29990.00, 25.00, 40, 'ELE-SNY-WHXM5-BLK', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=600&q=80', 4.85, 310, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(13, 3, 'LG 55-inch OLED evo C3 4K Smart TV', 'lg-55-inch-oled-evo-c3-4k-tv', 'Self-lit OLED pixels with infinite contrast, α9 AI Processor Gen6, Dolby Vision IQ, and NVIDIA G-Sync gaming compatibility.', 'LG', 119990.00, 30.00, 10, 'ELE-LG-OLED55C3', 'https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?auto=format&fit=crop&w=600&q=80', 4.90, 85, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(14, 3, 'Canon EOS R6 Mark II Mirrorless Camera (Body Only)', 'canon-eos-r6-mark-ii', '24.2 MP full-frame CMOS sensor, 40 fps electronic shutter, 4K 60p oversampled video, and dual pixel CMOS AF II.', 'Canon', 215995.00, 10.00, 5, 'ELE-CAN-R6M2-BOD', 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&w=600&q=80', 4.75, 29, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(15, 3, 'Bose SoundLink Revolve+ II Bluetooth Speaker', 'bose-soundlink-revolve-plus-ii', 'Deep, loud and immersive 360° sound with water and dust-resistant design (IP55) and up to 17 hours of battery life.', 'Bose', 24500.00, 20.00, 22, 'ELE-BOS-REVPLUS2', 'https://images.unsplash.com/photo-1545454675-3531b543be5d?auto=format&fit=crop&w=600&q=80', 4.70, 94, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(16, 3, 'JBL Flip 6 Waterproof Portable Bluetooth Speaker', 'jbl-flip-6-waterproof-speaker', 'Powerful JBL Original Pro Sound with 2-way speaker system, IP67 waterproof and dustproof rating, and 12-hour playtime.', 'JBL', 9999.00, 30.00, 35, 'ELE-JBL-FLIP6-BLU', 'https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?auto=format&fit=crop&w=600&q=80', 4.65, 215, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Fashion (Cat 4)
(17, 4, 'Levi''s Men''s 511 Slim Fit Stretch Denim Jeans', 'levis-mens-511-slim-fit-jeans', 'A modern slim with room to move. Added stretch for all-day comfort and classic 5-pocket styling.', 'Levi''s', 3999.00, 30.00, 50, 'FAS-LEV-511-BLU', 'https://images.unsplash.com/photo-1542272604-780c96856592?auto=format&fit=crop&w=600&q=80', 4.40, 180, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(18, 4, 'Tommy Hilfiger Men''s Regular Fit Cotton Polo Shirt', 'tommy-hilfiger-mens-regular-fit-polo', 'Crafted from breathable 100% organic cotton pique with the iconic embroidered flag on the chest.', 'Tommy Hilfiger', 3499.00, 25.00, 60, 'FAS-TH-POLO-NAVY', 'https://images.unsplash.com/photo-1581655353564-df123a1eb820?auto=format&fit=crop&w=600&q=80', 4.50, 120, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(19, 4, 'Zara Women''s Structured Double-Breasted Blazer', 'zara-womens-structured-blazer', 'Tailored lapel collar blazer with shoulder pads, front flap pockets, and embossed golden button fastening.', 'Zara', 6990.00, 20.00, 28, 'FAS-ZAR-BLAZER-BLK', 'https://images.unsplash.com/photo-1591047139829-d91aecb6caea?auto=format&fit=crop&w=600&q=80', 4.60, 74, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(20, 4, 'Nike Air Jordan 1 Retro High OG (Chicago Red)', 'nike-air-jordan-1-retro-high-og', 'The legendary sneaker crafted in premium full-grain leather with encapsulated Nike Air cushioning and iconic wings logo.', 'Nike', 16995.00, 15.00, 15, 'FAS-NIK-AJ1-RED', 'https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?auto=format&fit=crop&w=600&q=80', 4.95, 412, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(21, 4, 'Adidas Ultraboost Light Running Shoes (Cloud White)', 'adidas-ultraboost-light-shoes', 'Epic energy return with Light BOOST midsole cushioning and Primeknit+ breathable textile upper.', 'Adidas', 14999.00, 25.00, 32, 'FAS-ADI-UB-WHT', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=600&q=80', 4.80, 188, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Home Appliances (Cat 5)
(22, 5, 'Samsung 653L Side-by-Side Inverter Refrigerator (Black Glass)', 'samsung-653l-side-by-side-refrigerator', 'SpaceMax Technology with Twin Cooling Plus, Digital Inverter compressor with 20-year warranty and smart WiFi connectivity.', 'Samsung', 84990.00, 22.00, 9, 'APP-SAM-SBS653-BLK', 'https://images.unsplash.com/photo-1584992236310-6edddc08acff?auto=format&fit=crop&w=600&q=80', 4.70, 56, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(23, 5, 'IFB 8.5 kg 5 Star Fully Automatic Front Load Washing Machine', 'ifb-8-5kg-front-load-washing-machine', 'Powered by AI neural network wash algorithm with steam refresh, 9 swirl wash, and 4-year comprehensive warranty.', 'IFB', 38990.00, 20.00, 16, 'APP-IFB-FL85-SIL', 'https://images.unsplash.com/photo-1626806787461-102c1bfaaea1?auto=format&fit=crop&w=600&q=80', 4.55, 110, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(24, 5, 'Dyson V12 Detect Slim Cordless Vacuum Cleaner', 'dyson-v12-detect-slim-cordless-vacuum', 'Laser illumination reveals invisible microscopic dust. Piezo sensor scientifically measures particles with LCD screen report.', 'Dyson', 45900.00, 15.00, 14, 'APP-DYS-V12-SLIM', 'https://images.unsplash.com/photo-1558317374-067fb5f30001?auto=format&fit=crop&w=600&q=80', 4.80, 88, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(25, 5, 'Philips Digital Air Fryer HD9252 (4.1L Capacity)', 'philips-digital-air-fryer-hd9252', 'Rapid Air technology for healthy frying with up to 90% less fat. Touchscreen with 7 preset cooking programs.', 'Philips', 8995.00, 30.00, 45, 'APP-PHI-AF-HD9252', 'https://images.unsplash.com/photo-1585659722983-3a675dabf23d?auto=format&fit=crop&w=600&q=80', 4.60, 260, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(26, 5, 'De''Longhi Dedica Deluxe Espresso Coffee Machine', 'delonghi-dedica-deluxe-espresso-machine', 'Slim 15 bar pump espresso maker with adjustable milk frother, rapid thermoblock heating, and stainless steel housing.', 'De''Longhi', 22990.00, 25.00, 18, 'APP-DEL-ESP-RED', 'https://images.unsplash.com/photo-1517668808822-9ebb02ae2a0e?auto=format&fit=crop&w=600&q=80', 4.75, 95, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Groceries (Cat 6)
(27, 6, 'Fortune Sunlite Refined Sunflower Oil (5L Can)', 'fortune-sunlite-sunflower-oil-5l', 'Enriched with Vitamin A and D. Light and healthy cooking oil processed with advanced refining standards.', 'Fortune', 749.00, 15.00, 120, 'GRO-FOR-OIL-5L', 'https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?auto=format&fit=crop&w=600&q=80', 4.50, 340, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(28, 6, 'India Gate Basmati Rice Feast Rozzana (5kg Bag)', 'india-gate-basmati-rice-feast-5kg', 'Aged to perfection with enticing aroma and fluffy grains. Ideal for biryanis, pulao, and everyday rice dishes.', 'India Gate', 485.00, 20.00, 95, 'GRO-IND-RICE-5KG', 'https://images.unsplash.com/photo-1586201375761-83865001e31c?auto=format&fit=crop&w=600&q=80', 4.65, 410, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(29, 6, 'Organic India Tulsi Green Tea Classic (100 Tea Bags)', 'organic-india-tulsi-green-tea-100', 'Rich in antioxidants and adaptogens. Supports immune defense, reduces stress, and boosts metabolism naturally.', 'Organic India', 595.00, 10.00, 80, 'GRO-ORG-TEA-100B', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?auto=format&fit=crop&w=600&q=80', 4.70, 195, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(30, 6, 'Nutella Hazelnut Spread with Cocoa (750g Jar)', 'nutella-hazelnut-spread-750g', 'The original hazelnut spread with skim milk and cocoa. Perfect on warm toast, pancakes, and breakfast treats.', 'Ferrero', 640.00, 15.00, 60, 'GRO-FER-NUT-750', 'https://images.unsplash.com/photo-1589733955941-5eeaf752f6dd?auto=format&fit=crop&w=600&q=80', 4.85, 520, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(31, 6, 'Ferrero Rocher Premium Hazelnut Chocolates (Box of 24)', 'ferrero-rocher-chocolates-24-pack', 'Crisp hazelnut and milk chocolate-covered specialty with smooth hazelnut filling and whole crunchy hazelnut centre.', 'Ferrero Rocher', 995.00, 20.00, 70, 'GRO-FER-ROC-24P', 'https://images.unsplash.com/photo-1549007994-cb92caebd54b?auto=format&fit=crop&w=600&q=80', 4.90, 380, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Accessories (Cat 7)
(32, 7, 'Apple Watch Series 9 GPS (45mm Midnight Aluminum)', 'apple-watch-series-9-45mm', 'Powered by S9 SiP with Double Tap gesture control, brighter Always-On Retina display, and advanced health metrics.', 'Apple', 44900.00, 10.00, 25, 'ACC-APP-AW9-45M', 'https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?auto=format&fit=crop&w=600&q=80', 4.85, 140, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(33, 7, 'Anker 737 Power Bank PowerCore 24K (140W Fast Charge)', 'anker-737-power-bank-24k', 'Ultra-powerful two-way charging with Smart Digital Display, 24,000mAh capacity, and GaNPrime technology.', 'Anker', 11999.00, 25.00, 35, 'ACC-ANK-737-24K', 'https://images.unsplash.com/photo-1609592807664-8464303498bd?auto=format&fit=crop&w=600&q=80', 4.75, 98, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(34, 7, 'Logitech MX Master 3S Wireless Performance Mouse', 'logitech-mx-master-3s-mouse', '8K DPI any-surface tracking with quiet clicks and MagSpeed electromagnetic scrolling wheel.', 'Logitech', 9995.00, 20.00, 40, 'ACC-LOG-MXM3S-GRY', 'https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?auto=format&fit=crop&w=600&q=80', 4.90, 310, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(35, 7, 'Apple AirPods Pro 2nd Generation with MagSafe Case (USB-C)', 'apple-airpods-pro-2-usbc', 'Active Noise Cancellation with Adaptive Audio, Transparency mode, and Personalized Spatial Audio with dynamic head tracking.', 'Apple', 24900.00, 15.00, 30, 'ACC-APP-APP2-USBC', 'https://images.unsplash.com/photo-1600294037681-c80b4cb5b434?auto=format&fit=crop&w=600&q=80', 4.85, 420, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Beauty (Cat 8)
(36, 8, 'Estee Lauder Advanced Night Repair Synchronized Serum (50ml)', 'estee-lauder-advanced-night-repair-50ml', 'Revolutionary Chronolux Power Signal Technology helps reduce the look of multiple signs of aging.', 'Estee Lauder', 8900.00, 15.00, 30, 'BEA-EST-ANR-50ML', 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?auto=format&fit=crop&w=600&q=80', 4.70, 75, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(37, 8, 'Clinique Moisture Surge 100H Auto-Replenishing Hydrator', 'clinique-moisture-surge-100h-50ml', 'Oil-free gel-cream moisturizer with Aloe Bio-ferment and Hyaluronic Acid that penetrates over 10 layers deep.', 'Clinique', 3200.00, 20.00, 45, 'BEA-CLI-MS100H-50', 'https://images.unsplash.com/photo-1556228720-195a672e8a03?auto=format&fit=crop&w=600&q=80', 4.65, 130, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(38, 8, 'Dior Sauvage Eau de Parfum (100ml)', 'dior-sauvage-eau-de-parfum-100ml', 'An intensely fresh composition with spicy Calabrian bergamot and sensual Papua New Guinean vanilla extract.', 'Dior', 11500.00, 10.00, 20, 'BEA-DIO-SAUV-100', 'https://images.unsplash.com/photo-1523293182086-7651a899d37f?auto=format&fit=crop&w=600&q=80', 4.90, 280, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(39, 8, 'The Ordinary Niacinamide 10% + Zinc 1% (60ml)', 'the-ordinary-niacinamide-10-zinc-1-60ml', 'High-strength vitamin and mineral blemish formula that reduces the appearance of skin blemishes and congestion.', 'The Ordinary', 1200.00, 25.00, 80, 'BEA-ORD-NIA-60ML', 'https://images.unsplash.com/photo-1608248597358-1f0945951d8d?auto=format&fit=crop&w=600&q=80', 4.60, 340, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Sports (Cat 9)
(40, 9, 'Decathlon Domyos Hex Dumbbell Pair (10kg Each)', 'decathlon-domyos-hex-dumbbell-10kg', 'Ergonomic knurled chrome handle with durable rubber hexagonal coating that protects gym flooring.', 'Decathlon', 4999.00, 25.00, 30, 'SPO-DEC-HEX-10KG', 'https://images.unsplash.com/photo-1583454110551-21f2fa2afe61?auto=format&fit=crop&w=600&q=80', 4.60, 85, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(41, 9, 'Yonex Astrox 99 Pro Badminton Racket (4U G5)', 'yonex-astrox-99-pro-racket', 'Rotational Generator System with Namd graphite for explosive power and steep angled smashes.', 'Yonex', 16990.00, 20.00, 18, 'SPO-YON-AX99P-4U', 'https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?auto=format&fit=crop&w=600&q=80', 4.80, 62, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(42, 9, 'Nivia Storm Football Size 5 (All-Weather Match Ball)', 'nivia-storm-football-size-5', '32-panel stitched construction with reinforced rubber bladder for high durability and optimum bounce.', 'Nivia', 1299.00, 30.00, 50, 'SPO-NIV-STRM-SZ5', 'https://images.unsplash.com/photo-1614632537190-23e4146777db?auto=format&fit=crop&w=600&q=80', 4.55, 140, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(43, 9, 'Boldfit High Density Anti-Skid Yoga Mat (6mm with Strap)', 'boldfit-high-density-yoga-mat-6mm', 'Eco-friendly TPE material with laser alignment markings, water-resistant surface, and dual-layer non-slip texture.', 'Boldfit', 1999.00, 30.00, 65, 'SPO-BLD-YOGA-6MM', 'https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?auto=format&fit=crop&w=600&q=80', 4.70, 220, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Books (Cat 10)
(44, 10, 'Designing Data-Intensive Applications by Martin Kleppmann', 'designing-data-intensive-applications', 'The definitive guide to the principles, algorithms, and trade-offs of modern distributed systems and database storage.', 'O''Reilly Media', 1750.00, 20.00, 70, 'BOK-ORE-DDIA-ENG', 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=600&q=80', 4.95, 450, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(45, 10, 'Atomic Habits by James Clear', 'atomic-habits-james-clear', 'An easy & proven way to build good habits and break bad ones. Transform your daily routine with microscopic changes.', 'Random House', 699.00, 30.00, 110, 'BOK-RND-AH-ENG', 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&w=600&q=80', 4.90, 890, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(46, 10, 'System Design Interview – An Insider''s Guide (Alex Xu)', 'system-design-interview-alex-xu', 'Step-by-step framework to approach large-scale distributed system architectural interview problems.', 'Independently Published', 2499.00, 15.00, 55, 'BOK-IND-SDI-ENG', 'https://images.unsplash.com/photo-1532012164546-f432f2e3777a?auto=format&fit=crop&w=600&q=80', 4.85, 320, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(47, 10, 'The Psychology of Money by Morgan Housel', 'the-psychology-of-money-morgan-housel', 'Timeless lessons on wealth, greed, and happiness doing well with money isn''t necessarily about what you know.', 'Harriman House', 499.00, 25.00, 90, 'BOK-HAR-PSY-ENG', 'https://images.unsplash.com/photo-1589829085413-56de8ae18c73?auto=format&fit=crop&w=600&q=80', 4.85, 610, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 5. Seed Coupons
INSERT INTO coupons (id, code, discount_type, discount_value, minimum_order_amount, maximum_discount, start_date, expiry_date, usage_limit, usage_count, active, created_at) VALUES
(1, 'WELCOME10', 'PERCENTAGE', 10.00, 500.00, 1000.00, CURRENT_TIMESTAMP, '2030-12-31 23:59:59', 500, 5, TRUE, CURRENT_TIMESTAMP),
(2, 'FESTIVE20', 'PERCENTAGE', 20.00, 1500.00, 2000.00, CURRENT_TIMESTAMP, '2030-12-31 23:59:59', 200, 12, TRUE, CURRENT_TIMESTAMP),
(3, 'FLAT500', 'FIXED_AMOUNT', 500.00, 2999.00, 500.00, CURRENT_TIMESTAMP, '2030-12-31 23:59:59', 300, 8, TRUE, CURRENT_TIMESTAMP),
(4, 'SUPERDEAL', 'PERCENTAGE', 15.00, 1000.00, 1500.00, CURRENT_TIMESTAMP, '2030-12-31 23:59:59', 1000, 25, TRUE, CURRENT_TIMESTAMP);

-- 6. Seed Sample User Cart
INSERT INTO carts (id, user_id, created_at, updated_at) VALUES
(1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO cart_items (id, cart_id, product_id, quantity, created_at, updated_at) VALUES
(1, 1, 12, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- Sony WH-1000XM5
(2, 1, 45, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP); -- Atomic Habits x 2

-- 7. Seed Sample User Wishlist
INSERT INTO wishlists (id, user_id, created_at, updated_at) VALUES
(1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO wishlist_items (id, wishlist_id, product_id, created_at) VALUES
(1, 1, 1, CURRENT_TIMESTAMP), -- Samsung S24 Ultra
(2, 1, 7, CURRENT_TIMESTAMP); -- MacBook Air M3

-- 8. Seed Completed Orders
INSERT INTO orders (id, order_number, user_id, total_amount, discount_amount, delivery_charge, net_amount, payment_status, order_status, payment_method, shipping_address_snapshot, coupon_code, notes, created_at, updated_at) VALUES
(1, 'ORD-20261001-9812', 2, 29990.00, 5998.00, 0.00, 23992.00, 'PAID', 'DELIVERED', 'ONLINE_MOCK', 'Rahul Sharma, +91 9876543211, Flat 402, Green Acres Residency, MG Road, Sector 14, Bengaluru, Karnataka - 560001', 'WELCOME10', 'Delivered safely at front desk', '2026-10-01 10:30:00', '2026-10-03 16:45:00'),
(2, 'ORD-20261005-4123', 2, 4999.00, 1000.00, 100.00, 4099.00, 'PAID', 'SHIPPED', 'ONLINE_MOCK', 'Rahul Sharma, +91 9876543211, Flat 402, Green Acres Residency, MG Road, Sector 14, Bengaluru, Karnataka - 560001', NULL, 'In transit with BlueDart courier', '2026-10-05 14:15:00', '2026-10-06 09:20:00'),
(3, 'ORD-20261007-8834', 3, 159900.00, 12792.00, 0.00, 147108.00, 'PENDING', 'CONFIRMED', 'COD', 'Priya Patel, +91 9876543212, A-12, Shanti Niketan Society, Near City Center Mall, Mumbai, Maharashtra - 400001', 'FESTIVE20', 'Customer requested delivery before 5 PM', '2026-10-07 11:00:00', '2026-10-07 11:30:00');

-- 9. Seed Order Items
INSERT INTO order_items (id, order_id, product_id, product_name, product_sku, product_image_url, quantity, unit_price, subtotal) VALUES
(1, 1, 12, 'Sony WH-1000XM5 Wireless Noise Cancelling Headphones', 'ELE-SNY-WHXM5-BLK', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=600&q=80', 1, 23992.00, 23992.00),
(2, 2, 40, 'Decathlon Domyos Hex Dumbbell Pair (10kg Each)', 'SPO-DEC-HEX-10KG', 'https://images.unsplash.com/photo-1583454110551-21f2fa2afe61?auto=format&fit=crop&w=600&q=80', 1, 3749.00, 3749.00),
(3, 3, 2, 'Apple iPhone 15 Pro Max (Natural Titanium, 256GB)', 'MOB-APP-IP15PM-256', 'https://images.unsplash.com/photo-1695048133142-1a20484d2569?auto=format&fit=crop&w=600&q=80', 1, 143910.00, 143910.00);

-- 10. Seed Payments
INSERT INTO payments (id, order_id, transaction_id, payment_method, amount, payment_status, payment_gateway_response, created_at) VALUES
(1, 1, 'TXN-SHP-20261001-9812-OK', 'ONLINE_MOCK', 23992.00, 'PAID', '{"status":"SUCCESS","mode":"UPI","provider":"MOCK_GATEWAY"}', '2026-10-01 10:32:00'),
(2, 2, 'TXN-SHP-20261005-4123-OK', 'ONLINE_MOCK', 4099.00, 'PAID', '{"status":"SUCCESS","mode":"NET_BANKING","provider":"MOCK_GATEWAY"}', '2026-10-05 14:17:00'),
(3, 3, 'TXN-SHP-20261007-8834-COD', 'COD', 147108.00, 'PENDING', '{"status":"PENDING","mode":"CASH_ON_DELIVERY"}', '2026-10-07 11:00:00');

-- 11. Seed Reviews
INSERT INTO reviews (id, product_id, user_id, rating, comment, verified_purchase, created_at, updated_at) VALUES
(1, 12, 2, 5, 'The active noise cancellation is unmatched! Extremely lightweight and the soundstage is breathtaking for daily music listening and office work.', TRUE, '2026-10-04 09:30:00', '2026-10-04 09:30:00'),
(2, 40, 2, 5, 'Heavy duty build with solid rubber finish that prevents scuffs on my tiles. Knurled grip provides tremendous grip stability during deadlifts.', TRUE, '2026-10-06 18:00:00', '2026-10-06 18:00:00');

-- 12. Seed Audit Logs
INSERT INTO audit_logs (id, admin_email, action, entity_name, entity_id, details, ip_address, timestamp) VALUES
(1, 'admin@shopsphere.com', 'SYSTEM_INITIALIZATION', 'SYSTEM', '1', 'Initial database migration and product catalog seeded successfully.', '127.0.0.1', CURRENT_TIMESTAMP);

-- Advance Identity Sequences past seed data
ALTER TABLE users ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE addresses ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE categories ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE products ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE carts ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE cart_items ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE wishlists ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE wishlist_items ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE orders ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE order_items ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE payments ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE reviews ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE coupons ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE audit_logs ALTER COLUMN id RESTART WITH 1000;
