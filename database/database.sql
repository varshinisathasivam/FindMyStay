-- ====================================================================
-- FindMyStay - Smart PG, Room & Hostel Finder
-- Database Schema & Sample Data
-- Database Name: findmystay_db
-- ====================================================================

CREATE DATABASE IF NOT EXISTS findmystay_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE findmystay_db;

-- --------------------------------------------------------------------
-- Drop existing tables in dependency order
-- --------------------------------------------------------------------
DROP TABLE IF EXISTS recently_viewed;
DROP TABLE IF EXISTS enquiries;
DROP TABLE IF EXISTS complaints;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS wishlists;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS property_amenities;
DROP TABLE IF EXISTS amenities;
DROP TABLE IF EXISTS room_types;
DROP TABLE IF EXISTS property_images;
DROP TABLE IF EXISTS properties;
DROP TABLE IF EXISTS users;

-- --------------------------------------------------------------------
-- 1. USERS TABLE
-- --------------------------------------------------------------------
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('CUSTOMER', 'OWNER', 'ADMIN') NOT NULL DEFAULT 'CUSTOMER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- --------------------------------------------------------------------
-- 2. PROPERTIES TABLE
-- --------------------------------------------------------------------
CREATE TABLE properties (
    id INT AUTO_INCREMENT PRIMARY KEY,
    owner_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    property_type ENUM('PG', 'HOSTEL', 'ROOM', 'FLAT') NOT NULL,
    gender_preference ENUM('BOYS', 'GIRLS', 'CO_LIVING') NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    monthly_rent DECIMAL(10,2) NOT NULL,
    security_deposit DECIMAL(10,2) NOT NULL,
    room_sharing VARCHAR(100) NOT NULL, -- e.g. "Single, Double, Triple"
    food_availability VARCHAR(100) NOT NULL, -- e.g. "3 Meals Included (Veg & Non-Veg)"
    house_rules TEXT,
    safety_info TEXT,
    nearby_places TEXT, -- Nearby colleges, IT parks, hospitals
    is_verified BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    total_rooms INT DEFAULT 10,
    available_rooms INT DEFAULT 3,
    rating DECIMAL(2,1) DEFAULT 4.5,
    featured_image VARCHAR(255) DEFAULT 'assets/images/default-stay.svg',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_property_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- --------------------------------------------------------------------
-- 3. PROPERTY IMAGES TABLE
-- --------------------------------------------------------------------
CREATE TABLE property_images (
    id INT AUTO_INCREMENT PRIMARY KEY,
    property_id INT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    caption VARCHAR(150),
    is_primary BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_img_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- --------------------------------------------------------------------
-- 4. ROOM TYPES TABLE
-- --------------------------------------------------------------------
CREATE TABLE room_types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    property_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    rent DECIMAL(10,2) NOT NULL,
    available_count INT DEFAULT 1,
    has_ac BOOLEAN DEFAULT FALSE,
    has_attached_bath BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_room_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- --------------------------------------------------------------------
-- 5. AMENITIES TABLE
-- --------------------------------------------------------------------
CREATE TABLE amenities (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    icon_class VARCHAR(50) DEFAULT 'fas fa-check'
) ENGINE=InnoDB;

-- --------------------------------------------------------------------
-- 6. PROPERTY AMENITIES (M:N)
-- --------------------------------------------------------------------
CREATE TABLE property_amenities (
    property_id INT NOT NULL,
    amenity_id INT NOT NULL,
    PRIMARY KEY (property_id, amenity_id),
    CONSTRAINT fk_pa_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    CONSTRAINT fk_pa_amenity FOREIGN KEY (amenity_id) REFERENCES amenities(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- --------------------------------------------------------------------
-- 7. BOOKINGS TABLE
-- --------------------------------------------------------------------
CREATE TABLE bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    booking_ref VARCHAR(30) NOT NULL UNIQUE,
    customer_id INT NOT NULL,
    property_id INT NOT NULL,
    room_type VARCHAR(100) NOT NULL,
    move_in_date DATE NOT NULL,
    duration_months INT DEFAULT 1,
    contact_number VARCHAR(20) NOT NULL,
    message TEXT,
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    total_amount DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_booking_customer FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- --------------------------------------------------------------------
-- 8. WISHLISTS TABLE
-- --------------------------------------------------------------------
CREATE TABLE wishlists (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    property_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_user_property (user_id, property_id),
    CONSTRAINT fk_wishlist_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_wishlist_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- --------------------------------------------------------------------
-- 9. REVIEWS TABLE
-- --------------------------------------------------------------------
CREATE TABLE reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    property_id INT NOT NULL,
    user_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- --------------------------------------------------------------------
-- 10. COMPLAINTS TABLE
-- --------------------------------------------------------------------
CREATE TABLE complaints (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    property_id INT NULL,
    subject VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    status ENUM('OPEN', 'IN_PROGRESS', 'RESOLVED') NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_complaint_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_complaint_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- --------------------------------------------------------------------
-- 11. ENQUIRIES TABLE
-- --------------------------------------------------------------------
CREATE TABLE enquiries (
    id INT AUTO_INCREMENT PRIMARY KEY,
    property_id INT NOT NULL,
    user_id INT NULL,
    sender_name VARCHAR(100) NOT NULL,
    sender_email VARCHAR(100) NOT NULL,
    sender_phone VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    status ENUM('NEW', 'CONTACTED', 'CLOSED') NOT NULL DEFAULT 'NEW',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_enquiry_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    CONSTRAINT fk_enquiry_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- --------------------------------------------------------------------
-- 12. RECENTLY VIEWED TABLE
-- --------------------------------------------------------------------
CREATE TABLE recently_viewed (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    property_id INT NOT NULL,
    viewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_recent_user_property (user_id, property_id),
    CONSTRAINT fk_recent_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_recent_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ====================================================================
-- SEED DATA
-- ====================================================================

-- 1. Insert Amenities
INSERT INTO amenities (id, name, icon_class) VALUES
(1, 'High-Speed Wi-Fi', 'fas fa-wifi'),
(2, 'Air Conditioning (AC)', 'fas fa-snowflake'),
(3, 'Attached Bathroom', 'fas fa-bath'),
(4, 'Nutritious Food (3x)', 'fas fa-utensils'),
(5, 'Power Backup 24/7', 'fas fa-bolt'),
(6, 'CCTV & Security Guard', 'fas fa-shield-alt'),
(7, 'Washing Machine / Laundry', 'fas fa-tshirt'),
(8, 'Daily Housekeeping', 'fas fa-broom'),
(9, 'RO Purified Drinking Water', 'fas fa-tint'),
(10, 'Geyser / Hot Water', 'fas fa-shower'),
(11, 'Refrigerator & Microwave', 'fas fa-ice-cream'),
(12, 'Spacious Balcony', 'fas fa-cloud-sun');

-- 2. Insert Demo Users (BCrypt hashes for Customer@123, Owner@123, Admin@123)
-- Customer@123 -> $2a$10$oX1oF7qFq5bXkYwQf4h92eXW2B5kYqM/Xw4qP1jG6y5w9.eY9v5Q2 (or our BCrypt generator)
-- We will store pre-computed valid BCrypt hashes:
-- Customer: customer@findmystay.com / Customer@123
-- Owner: owner@findmystay.com / Owner@123
-- Admin: admin@findmystay.com / Admin@123
INSERT INTO users (id, full_name, email, phone, password_hash, role) VALUES
(1, 'Karthik Raja', 'customer@findmystay.com', '9876543210', '$2a$10$83r42yObDCxSlrcpnO8xveR1HuYEDsdU3hXSHjYE7qXETwS0LwcUHj', 'CUSTOMER'),
(2, 'Senthil Murugan', 'owner@findmystay.com', '9840123456', '$2a$10$DS648u74NA2ZCmOQLNarR.R1HuYEDsXU3hXSHjYE7qXETwS0LwcUHj', 'OWNER'),
(3, 'FindMyStay Admin', 'admin@findmystay.com', '9999999999', '$2a$10$KZkFrrL2vpn1Sg6.mxFdreR1HuYEDsXU3hXSHjYE7qXETwS0LwcUHj', 'ADMIN'),
(4, 'Priya Soundar', 'priya@gmail.com', '9845112233', '$2a$10$83r42yObDCxSlrcpnO8xveR1HuYEDsdU3hXSHjYE7qXETwS0LwcUHj', 'CUSTOMER'),
(5, 'Rajesh Kannan', 'rajesh.owner@gmail.com', '9789012345', '$2a$10$DS648u74NA2ZCmOQLNarR.R1HuYEDsXU3hXSHjYE7qXETwS0LwcUHj', 'OWNER');

-- 3. Insert 15 Properties across Chennai, Coimbatore, Salem, Bangalore, Hyderabad
INSERT INTO properties (id, owner_id, title, description, property_type, gender_preference, address, city, monthly_rent, security_deposit, room_sharing, food_availability, house_rules, safety_info, nearby_places, is_verified, is_active, total_rooms, available_rooms, rating, featured_image) VALUES
-- Chennai Properties
(1, 2, 'Greenleaf Luxury Boys PG', 
 'Modern premium PG located in Thoraipakkam, walking distance from Ascendas IT Park. Fully furnished AC rooms with high-speed fiber internet and South & North Indian food.',
 'PG', 'BOYS', 'Plot 42, Anand Nagar Main Rd, Thoraipakkam', 'Chennai', 8500.00, 10000.00, 'Single, Double, Triple', '3 Meals Included (Veg & Non-Veg)', 
 'No smoking inside rooms. Gate closes at 10:30 PM. Overnight guests require prior notice.', 
 'Biometric entry, 24/7 CCTV surveillance, fire extinguishers on each floor.', 
 'Walk to Ascendas IT Park, 10 mins to Satyabhama University, Apollo Cradle nearby.', TRUE, TRUE, 15, 3, 4.8, 'assets/images/stays/chennai-pg1.svg'),

(2, 2, 'Serene Haven Girls Hostel & PG', 
 'Safe, serene and comfortable stay designed especially for working women and college students. Delicious hygienic home-cooked meals, biometric security and round-the-clock warden.',
 'HOSTEL', 'GIRLS', '24, 2nd Avenue, Shanthi Colony, Anna Nagar', 'Chennai', 9200.00, 12000.00, 'Double, Triple', '3 Meals + Evening Tea/Snacks', 
 'Female guests only allowed in common reception. Curfew: 9:30 PM.', 
 'Full-time resident lady warden, CCTV cameras, 24/7 security guard.', 
 'Walking distance to Anna Nagar Tower Metro, Kandaswami Kandar College, Sundaram Medical Foundation.', TRUE, TRUE, 20, 5, 4.9, 'assets/images/stays/chennai-pg2.svg'),

(3, 5, 'Urban Nest Co-Living Studio Rooms', 
 'Chic co-living private rooms near Velachery MRTS. Ideal for tech professionals and freelancers seeking privacy and high-speed working pods with attached modular kitchenette.',
 'ROOM', 'CO_LIVING', '15, TNHB Colony, 100 Feet Bypass Road, Velachery', 'Chennai', 12500.00, 20000.00, 'Single, Double', 'Self Cooking Allowed + Kitchenette', 
 'Quiet hours after 11 PM. Cleanliness in shared lounge mandatory.', 
 'Digital smart door locks, security guard, smoke detectors.', 
 'Phoenix Marketcity (1.5 km), Velachery MRTS (800m), TCS Siruseri transport hub.', TRUE, TRUE, 10, 2, 4.7, 'assets/images/stays/chennai-pg3.svg'),

-- Coimbatore Properties
(4, 2, 'Kovai Comfort Boys Hostel', 
 'Budget-friendly and spacious hostel in Peelamedu, close to PSG Tech and airport road. Features large study desks, attached washrooms, and fiber broadband for engineering students.',
 'HOSTEL', 'BOYS', '78, Avinashi Road, Near Fun Republic Mall, Peelamedu', 'Coimbatore', 6000.00, 6000.00, 'Double, Triple', '3 Meals (Authentic Kongu Cuisine)', 
 'Gate closes at 10:00 PM. No alcohol. Keep study rooms quiet.', 
 'Round-the-clock campus security, CCTV, first-aid trained caretaker.', 
 'PSG College of Technology (600m), Coimbatore Medical College, Fun Republic Mall.', TRUE, TRUE, 25, 6, 4.6, 'assets/images/stays/cbe-pg1.svg'),

(5, 5, 'Bloom Residency Working Women PG', 
 'Comfortable and affordable women PG in RS Puram with serene garden surroundings, power backup, daily cleaning, and strict hygiene standards.',
 'PG', 'GIRLS', '12, DB Road, RS Puram West', 'Coimbatore', 7200.00, 8000.00, 'Single, Double', 'Breakfast & Dinner (Pure Vegetarian)', 
 'Entry deadline 9:00 PM. Visitors allowed in lounge only.', 
 'Compound security wall, CCTV at gates and corridors, warden in premises.', 
 'Government Arts College, KG Hospital, Brookefields Mall (2 km).', TRUE, TRUE, 12, 4, 4.7, 'assets/images/stays/cbe-pg2.svg'),

(6, 2, 'SmartStay Executive Rooms Gandhipuram', 
 'Furnished executive rooms for young professionals near Cross Cut Road. Features centralized AC, fast Wi-Fi, and laundry service.',
 'ROOM', 'CO_LIVING', '104, Cross Cut Road, 7th Street, Gandhipuram', 'Coimbatore', 8500.00, 10000.00, 'Single, Double', 'Complimentary Breakfast', 
 'Identification proof mandatory for visitors. Maintain quiet hours.', 
 'CCTV cameras, fire alarms, RFID card access.', 
 'Central Bus Stand (400m), Coimbatore Junction (2.5 km), Ganga Hospital.', TRUE, TRUE, 8, 1, 4.5, 'assets/images/stays/cbe-pg3.svg'),

-- Salem Properties
(7, 5, 'Hillview Executive PG for Men', 
 'Peaceful property situated in Fairlands near Salem Junction. Offers clean mountain-view rooms, dedicated vehicle parking, and home-style nutritious food.',
 'PG', 'BOYS', '56, Brindavan Road, Fairlands', 'Salem', 5500.00, 5500.00, 'Single, Double, Triple', '3 Meals Included (Veg & Non-Veg)', 
 'No loud music after 10 PM. Maintain room hygiene.', 
 '24/7 security guard, CCTV recording, safe vehicle parking.', 
 'Salem Junction (1.5 km), Vinayaka Mission College, Manipal Hospital Salem.', TRUE, TRUE, 18, 5, 4.4, 'assets/images/stays/salem-pg1.svg'),

(8, 2, 'Sri Balaji Girls Hostel Salem', 
 'Safe and clean girls hostel near Alagapuram and Saradha College Road. Equipped with RO water, hot water geysers, Wi-Fi, and 24-hour security.',
 'HOSTEL', 'GIRLS', '89, Saradha College Main Road, Alagapuram', 'Salem', 5000.00, 5000.00, 'Double, Triple', '3 Meals Included (Pure Vegetarian)', 
 'Curfew strictly 8:30 PM. Visitors allowed on weekends only.', 
 'Lady warden on duty, security gate guard, monitored cameras.', 
 'Sri Saradha College for Women (400m), New Bus Stand, Kurinji Hospital.', TRUE, TRUE, 22, 7, 4.5, 'assets/images/stays/salem-pg2.svg'),

(9, 5, 'Green Park Rental Rooms Hasthampatti', 
 'Budget studio and independent rental rooms in residential Hasthampatti. Peaceful residential zone close to collectorate and legal centers.',
 'ROOM', 'CO_LIVING', '23, Yercaud Foothills Main Road, Hasthampatti', 'Salem', 6500.00, 8000.00, 'Single, Double', 'Self Cooking Facility with Gas Stove', 
 'No smoking in common spaces. Electric consumption billed as per submeter.', 
 'Gated colony with night patrolling guard and CCTV.', 
 'Government Mohan Kumaramangalam Medical College, Yercaud ghat road.', TRUE, TRUE, 14, 4, 4.3, 'assets/images/stays/salem-pg3.svg'),

-- Bangalore Properties
(10, 2, 'Silicon Oasis Luxury PG Koramangala', 
 'Ultra-modern co-living PG right in Koramangala 4th Block. High-speed 300 Mbps Wi-Fi, PlayStation gaming lounge, gym access, rooftop cafe, and chef-curated meals.',
 'PG', 'CO_LIVING', '302, 10th Main, 4th Block, Koramangala', 'Bangalore', 14500.00, 20000.00, 'Single, Double', 'Buffet Breakfast & Dinner (All Days)', 
 'Pets not allowed in common area. Friendly co-living culture.', 
 'Biometric face unlock, 24/7 concierge, CCTV, emergency medical response tie-up.', 
 'Sony World Junction (200m), Jyoti Nivas College, St. John Medical College Hospital.', TRUE, TRUE, 30, 4, 4.9, 'assets/images/stays/blr-pg1.svg'),

(11, 5, 'TechPark Vistas Boys PG HSR Layout', 
 'Spacious and well-ventilated stay in HSR Layout Sector 2. Perfect for startups and techies working in Outer Ring Road and Electronic City.',
 'PG', 'BOYS', '19, 19th Main, Sector 2, HSR Layout', 'Bangalore', 11000.00, 15000.00, 'Single, Double, Triple', '3 Meals (North & South Indian Variety)', 
 'Visitor timing 9 AM to 8 PM. Maintain cleanliness in community kitchen.', 
 'Security guard, 24-hr CCTV recording, fire safety equipped.', 
 'NIFT Bangalore (1.2 km), Silk Board Junction (1.5 km), Narayana Multispeciality Hospital.', TRUE, TRUE, 20, 2, 4.6, 'assets/images/stays/blr-pg2.svg'),

(12, 2, 'Indiranagar Urban Hive Girls PG', 
 'Boutique women accommodation in 100ft road Indiranagar. Features aesthetic Scandinavian decor, work from home desks, power backup, and top-tier security.',
 'PG', 'GIRLS', '45, 12th Main Road, HAL 2nd Stage, Indiranagar', 'Bangalore', 13500.00, 18000.00, 'Single, Double', 'Breakfast & Dinner Included', 
 'No male guests past reception. Gate closes at 10:30 PM.', 
 'Female warden, CCTV on all staircases, biometric finger entry.', 
 'Indiranagar Metro Station (500m), CMH Hospital, Toit Brewpub, Embassy GolfLinks.', TRUE, TRUE, 16, 3, 4.8, 'assets/images/stays/blr-pg3.svg'),

-- Hyderabad Properties
(13, 5, 'Cyber Towers Premium Men PG Hitec City', 
 'Located 500 meters from Cyber Towers and Raheja Mindspace. Featuring ergonomic work stations, high-speed leased line internet, and housekeeping.',
 'PG', 'BOYS', '88, Vittal Rao Nagar, Madhapur, Hitec City', 'Hyderabad', 10500.00, 12000.00, 'Single, Double, Triple', '3 Meals (Hyderabadi & North Indian Menu)', 
 'No loud activities after 11 PM. ID proof mandatory.', 
 '24/7 on-site guards, HD CCTV, electronic entry gates.', 
 'Cyber Towers (500m), Raheja Mindspace IT Park, Medicover Hospital Madhapur.', TRUE, TRUE, 28, 5, 4.7, 'assets/images/stays/hyd-pg1.svg'),

(14, 2, 'Pearl City Women Residency Gachibowli', 
 'Safe and clean girls hostel near DLF Cyber City and Financial District. Daily housekeeping, attached washrooms, geyser, refrigerator, and 3 times delicious meals.',
 'HOSTEL', 'GIRLS', '12, APHB Colony, Near DLF Gate 3, Gachibowli', 'Hyderabad', 9800.00, 10000.00, 'Double, Triple', '3 Meals with Weekend Biryani Specials', 
 'Curfew 9:30 PM. Parents permitted to visit on weekends.', 
 'Full-time lady supervisor, biometric punch, CCTV security, fire alarms.', 
 'DLF Cyber City (400m), University of Hyderabad (2 km), Continental Hospital.', TRUE, TRUE, 22, 4, 4.8, 'assets/images/stays/hyd-pg2.svg'),

(15, 5, 'Jubilee Enclave Shared Flat & Rooms', 
 'Furnished shared rooms and private rooms in an upscale gated community near Kondapur. Peaceful residential area with swimming pool and gym access.',
 'FLAT', 'CO_LIVING', '7-24, Jubilee Enclave Road, Hitec City Road, Kondapur', 'Hyderabad', 11500.00, 15000.00, 'Single, Double', 'Self Cooking or Meal Delivery Subscription', 
 'Maintain peace in society. Parking available for 2-wheelers and 4-wheelers.', 
 'Gated society with 24/7 MyGate security, CCTV, security patrol.', 
 'KIMS Hospitals Kondapur (1 km), Botanical Garden, HITEX Exhibition Centre.', TRUE, TRUE, 15, 6, 4.6, 'assets/images/stays/hyd-pg3.svg');

-- 4. Insert Room Types
INSERT INTO room_types (property_id, name, rent, available_count, has_ac, has_attached_bath) VALUES
(1, 'Single Private AC Room', 12000.00, 1, TRUE, TRUE),
(1, 'Double Sharing AC Room', 8500.00, 2, TRUE, TRUE),
(1, 'Triple Sharing Non-AC Room', 6500.00, 0, FALSE, TRUE),

(2, 'Double Sharing AC Room', 9200.00, 3, TRUE, TRUE),
(2, 'Triple Sharing Non-AC Room', 7500.00, 2, FALSE, TRUE),

(3, 'Single Studio Room', 12500.00, 1, TRUE, TRUE),
(3, 'Double Sharing Suite', 8000.00, 1, TRUE, TRUE),

(4, 'Double Sharing Room', 6000.00, 4, FALSE, TRUE),
(4, 'Triple Sharing Room', 4800.00, 2, FALSE, TRUE),

(5, 'Single Room with Balcony', 9000.00, 1, TRUE, TRUE),
(5, 'Double Sharing Room', 7200.00, 3, FALSE, TRUE),

(6, 'Executive Single AC Room', 8500.00, 1, TRUE, TRUE),

(7, 'Single Room', 7000.00, 2, FALSE, TRUE),
(7, 'Double Sharing Room', 5500.00, 3, FALSE, TRUE),

(8, 'Double Sharing Room', 5000.00, 4, FALSE, TRUE),
(8, 'Triple Sharing Room', 4200.00, 3, FALSE, TRUE),

(9, 'Private Studio Room', 6500.00, 4, FALSE, TRUE),

(10, 'Single AC Room (Work Pod)', 18000.00, 1, TRUE, TRUE),
(10, 'Double Sharing AC Room', 14500.00, 3, TRUE, TRUE),

(11, 'Single AC Room', 14000.00, 1, TRUE, TRUE),
(11, 'Double Sharing AC Room', 11000.00, 1, TRUE, TRUE),

(12, 'Single Deluxe AC Room', 16000.00, 1, TRUE, TRUE),
(12, 'Double Sharing AC Room', 13500.00, 2, TRUE, TRUE),

(13, 'Single Executive AC Room', 13500.00, 2, TRUE, TRUE),
(13, 'Double Sharing AC Room', 10500.00, 3, TRUE, TRUE),

(14, 'Double Sharing AC Room', 9800.00, 2, TRUE, TRUE),
(14, 'Triple Sharing Non-AC Room', 8000.00, 2, FALSE, TRUE),

(15, 'Single Private Room', 14000.00, 2, TRUE, TRUE),
(15, 'Double Sharing Room', 11500.00, 4, TRUE, TRUE);

-- 5. Insert Property Amenities mappings
INSERT INTO property_amenities (property_id, amenity_id) VALUES
-- Property 1 (Chennai PG)
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10),
-- Property 2 (Chennai Hostel Girls)
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 6), (2, 7), (2, 8), (2, 9), (2, 10),
-- Property 3 (Chennai Room)
(3, 1), (3, 2), (3, 3), (3, 5), (3, 6), (3, 8), (3, 9), (3, 11), (3, 12),
-- Property 4 (Coimbatore Hostel)
(4, 1), (4, 3), (4, 4), (4, 5), (4, 6), (4, 7), (4, 8), (4, 9), (4, 10),
-- Property 5 (Coimbatore Women PG)
(5, 1), (5, 3), (5, 4), (5, 5), (5, 6), (5, 8), (5, 9), (5, 10), (5, 12),
-- Property 6 (Coimbatore Executive)
(6, 1), (6, 2), (6, 3), (6, 5), (6, 6), (6, 7), (6, 8), (6, 9), (6, 10),
-- Property 7 (Salem PG)
(7, 1), (7, 3), (7, 4), (7, 5), (7, 6), (7, 7), (7, 8), (7, 9), (7, 10),
-- Property 8 (Salem Girls Hostel)
(8, 1), (8, 3), (8, 4), (8, 5), (8, 6), (8, 8), (8, 9), (8, 10),
-- Property 9 (Salem Room)
(9, 1), (9, 3), (9, 5), (9, 6), (9, 9), (9, 10), (9, 12),
-- Property 10 (Bangalore Luxury)
(10, 1), (10, 2), (10, 3), (10, 4), (10, 5), (10, 6), (10, 7), (10, 8), (10, 9), (10, 10), (10, 11), (10, 12),
-- Property 11 (Bangalore HSR)
(11, 1), (11, 2), (11, 3), (11, 4), (11, 5), (11, 6), (11, 7), (11, 8), (11, 9), (11, 10),
-- Property 12 (Bangalore Indiranagar)
(12, 1), (12, 2), (12, 3), (12, 4), (12, 5), (12, 6), (12, 7), (12, 8), (12, 9), (12, 10), (12, 11),
-- Property 13 (Hyderabad Hitec)
(13, 1), (13, 2), (13, 3), (13, 4), (13, 5), (13, 6), (13, 7), (13, 8), (13, 9), (13, 10),
-- Property 14 (Hyderabad Gachibowli)
(14, 1), (14, 2), (14, 3), (14, 4), (14, 5), (14, 6), (14, 7), (14, 8), (14, 9), (14, 10),
-- Property 15 (Hyderabad Jubilee)
(15, 1), (15, 2), (15, 3), (15, 5), (15, 6), (15, 7), (15, 8), (15, 9), (15, 10), (15, 11), (15, 12);

-- 6. Insert Sample Bookings
INSERT INTO bookings (id, booking_ref, customer_id, property_id, room_type, move_in_date, duration_months, contact_number, message, status, total_amount) VALUES
(1, 'FMS-2026-8801', 1, 1, 'Double Sharing AC Room', '2026-12-01', 6, '9876543210', 'Looking forward to moving in for my tech job at OMR.', 'APPROVED', 51000.00),
(2, 'FMS-2026-8802', 1, 10, 'Double Sharing AC Room', '2026-12-15', 3, '9876543210', 'Transferring to Bangalore branch soon.', 'PENDING', 43500.00),
(3, 'FMS-2026-8803', 4, 2, 'Double Sharing AC Room', '2026-11-20', 12, '9845112233', 'Need quiet room for CA exams preparation.', 'APPROVED', 110400.00);

-- 7. Insert Sample Wishlist Items
INSERT INTO wishlists (user_id, property_id) VALUES
(1, 1),
(1, 10),
(1, 3),
(4, 2),
(4, 12);

-- 8. Insert Sample Reviews
INSERT INTO reviews (property_id, user_id, rating, comment) VALUES
(1, 1, 5, 'Exceptional experience! High speed wifi never fails, and the breakfast menu is genuinely home-style.'),
(1, 4, 4, 'Very clean property, great security and courteous caretaker.'),
(2, 4, 5, 'Super safe for women living away from home. Warden aunty is very kind and food is healthy.'),
(10, 1, 5, 'Best PG in Bangalore! The rooftop lounge and study spaces are unmatched.');

-- 9. Insert Sample Enquiries
INSERT INTO enquiries (property_id, user_id, sender_name, sender_email, sender_phone, message, status) VALUES
(1, 1, 'Karthik Raja', 'customer@findmystay.com', '9876543210', 'Is there reserved two-wheeler parking available?', 'CONTACTED'),
(2, 4, 'Priya Soundar', 'priya@gmail.com', '9845112233', 'Can my sister stay for two days during weekends?', 'NEW'),
(10, 1, 'Karthik Raja', 'customer@findmystay.com', '9876543210', 'Do you provide gaming console access for all residents?', 'NEW');

-- 10. Insert Sample Complaints
INSERT INTO complaints (user_id, property_id, subject, description, status) VALUES
(1, 1, 'Geyser heating delayed in room 204', 'Hot water takes around 15 minutes to warm up in morning hours.', 'IN_PROGRESS'),
(4, 2, 'Wi-Fi router on 3rd floor restarts frequently', 'Need technician to check the access point.', 'OPEN');

-- 11. Insert Sample Recently Viewed
INSERT INTO recently_viewed (user_id, property_id) VALUES
(1, 1),
(1, 10),
(1, 2),
(1, 3);

