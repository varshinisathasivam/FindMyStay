# FindMyStay – Smart PG, Room & Hostel Finder

**FindMyStay** is a full-stack, enterprise-grade Java web application designed to help students, working professionals, and urban migrants discover, compare, save, and request bookings for verified PG accommodations, hostels, and rental rooms across major Indian cities (Chennai, Coimbatore, Salem, Bangalore, and Hyderabad).

Built strictly using **Core Java 17, Java Servlets (Jakarta EE), JSP, JDBC, and MySQL**, following clean **MVC architecture and OOP principles**, with zero dependency on frameworks like Spring Boot, Hibernate, JPA, or Maven.

---

## Table of Contents
1. [Project Overview & Objectives](#project-overview--objectives)
2. [Technology Stack](#technology-stack)
3. [Key Modules & Features](#key-modules--features)
4. [Unique Features](#unique-features)
5. [Database Architecture & Schema](#database-architecture--schema)
6. [Demo Accounts & Credentials](#demo-accounts--credentials)
7. [Project Folder Structure](#project-folder-structure)
8. [Setup & Installation Guide](#setup--installation-guide)
9. [How to Compile, Package & Deploy](#how-to-compile-package--deploy)
10. [Testing & Quality Verification](#testing--quality-verification)

---

## 1. Project Overview & Objectives

In modern urban cities, finding suitable long-term or shared accommodation often involves untrustworthy middlemen, hidden brokerage fees, unverified photos, and outdated occupancy records.

### Objectives:
- **Transparent Discovery**: Enable tenants to filter stays by city, rent budget, room sharing, gender preferences, food options, and key amenities.
- **Direct Connection**: Zero brokerage fee between tenants and authentic property hosts.
- **Side-by-Side Comparison**: Allow comparison of up to 3 properties on rent, deposits, facilities, and live availability.
- **Role-Based Security**: Complete multi-role separation for Customers, Property Owners, and Platform Administrators.
- **Verified Stays**: Administrative badge verification to guarantee hygiene and physical safety standards.

---

## 2. Technology Stack

| Layer | Technology | Details |
|---|---|---|
| **Language** | Java 17 (LTS) | OOP Principles, Strong Encapsulation, Polymorphism |
| **Backend Framework** | Java Servlets | Jakarta Servlet Specification (Controller Layer) |
| **View Engine** | JSP (JavaServer Pages) | Dynamic HTML, JSTL & Scriptlet rendering |
| **Database** | MySQL 8.0+ | Relational schema (`findmystay_db`) |
| **Data Access** | JDBC | `DriverManager`, `PreparedStatement`, `ResultSet`, DAO Pattern |
| **Security** | BCrypt & Sessions | Password hashing via Blowfish BCrypt, HTTP Session management |
| **Styling & UI** | Custom CSS3 | Warm Cream, Sage Green, Deep Olive & Terracotta palette |
| **Client Scripts** | Vanilla JavaScript | Dynamic rent calculation, live filters, modal dialogs |
| **Servlet Container** | Tomcat 10+ / Jetty 11+ | Standalone Jakarta EE Servlet container |

> **Strict Constraints Followed**: No Spring Boot, No Hibernate, No JPA, No Node.js, No React, No Python, No Maven pom.xml, No Apache Tomcat 9.

---

## 3. Key Modules & Features

### A. Customer Module
- **Home Landing Page**:
  - Hero banner with headline *"Find a Place That Feels Like Home"*
  - Multi-parameter search widget (City, Type, Min/Max Budget)
  - Curated featured listings & popular city hubs
  - Value propositions & customer testimonials
- **Search & Deep Filtering**:
  - Filter by City (Chennai, Coimbatore, Salem, Bangalore, Hyderabad)
  - Accommodation Type (PG, Hostel, Room, Flat)
  - Gender preference (Boys Only, Girls Only, Co-living)
  - Room sharing (Single, Double, Triple)
  - Amenities (Air Conditioning, Food Included, Wi-Fi, Attached Bath)
  - Sorting: Price Low-to-High, Price High-to-Low, Highest Rated
- **Property Details Page**:
  - High-resolution SVG architectural visual gallery
  - Detailed pricing & refundable security deposit
  - Room category cards with AC and washroom indicators
  - Comprehensive amenities checklist & food meal provisions
  - Property safety info (Biometrics, CCTV, Lady Warden, Fire safety)
  - House rules, timings, and curfew
  - Distance to nearby IT parks, colleges, metro stations & hospitals
  - Resident reviews & rating submission
  - Direct *"Send Enquiry to Owner"* modal
  - Sticky booking sidebar
- **Booking Flow**:
  - Move-in date picker, duration in months, and emergency contact
  - Live dynamic total rent estimation
  - Auto-generated unique booking reference (`FMS-yyyy-XXXX`)
- **Wishlist & Compare**:
  - Instant toggle to save favorite stays
  - Up to 3 stays compared simultaneously in a multi-column table
- **Customer Dashboard**:
  - Summary metrics: Total Bookings, Pending, Approved, Saved Wishlist
  - Master booking history with cancellation of pending requests
  - Recently viewed stays carousel
  - Personal profile & password update forms

### B. Property Owner Module
- **Owner Dashboard**:
  - Inventory metrics: Total Stays Listed, Pending Bookings, Approved, Inquiries
- **Property Management**:
  - Add property with full room types, pricing, and amenities
  - Edit existing property listings
  - Permanent delete with ownership verification
- **Live Room Availability Control**:
  - Inline counter to increment/decrement available rooms in real time
- **Booking Request Approvals**:
  - Review tenant requests with complete contact details
  - One-click **Approve** (auto-decrements available room count) or **Reject**
- **Inquiry Management**:
  - Read questions submitted by interested visitors
  - Mark inquiries as *Contacted* or *Closed*

### C. Admin Module
- **Master Admin Dashboard**:
  - 5 System Metrics: Total Customers, Total Owners, Total Properties, Pending Verification, Total Bookings
- **Listing Verification Workflow**:
  - Mark new submissions as **Verified Stay** with green shield badge
  - One-click activate or hide/deactivate any listing
- **User Management**:
  - Complete directory of registered customers and property owners
  - Ability to suspend or delete offending accounts
- **Master Bookings & Review Moderation**:
  - Global log of all reservations across all properties
  - Delete spam or abusive reviews with automatic property rating recalculation
- **Complaint Tickets**:
  - Review grievances filed by tenants or hosts
  - Update ticket status (OPEN -> RESOLVED)

---

## 4. Unique Features

1. **Verified Stay Badge**: Certified inspection badge rendered only after administrator verification.
2. **Smart Budget Suggestions**: Automatically calculates and recommends similar properties within $\pm 20\%$ of the active property's price range.
3. **Live Room Availability Indicator**: Real-time room counter with color-coded status pills (*Available*, *Fast Filling*, or *Fully Occupied*).
4. **Side-by-Side 3-Stay Comparison**: Matrix comparing rent, deposits, location, sharing types, AC, Wi-Fi, laundry, and ratings.
5. **Safety & Security Checklist**: Documents biometrics, CCTV surveillance, security guards, lady wardens, and emergency contact numbers.
6. **Nearby Landmark Mapping**: Lists walking distance and travel times to engineering colleges, hospitals, and transit hubs.
7. **Recently Viewed Tracker**: Tracks user browsing history per session/account.

---

## 5. Database Architecture & Schema

- **Database Name**: `findmystay_db`
- **Tables (12 total)**:
  1. `users`: Customer, Owner, and Admin profiles with BCrypt password hashes.
  2. `properties`: Main listing records with rent, deposits, rules, and safety info.
  3. `property_images`: Image URLs associated with each property.
  4. `room_types`: Categories per property (Single AC, Double Non-AC, etc.).
  5. `amenities`: Standardized master amenities (Wi-Fi, AC, Food, Backup, etc.).
  6. `property_amenities`: Many-to-many join table connecting stays with amenities.
  7. `bookings`: Reservation records with unique reference IDs and statuses.
  8. `wishlists`: Saved properties per user with unique constraints.
  9. `reviews`: Ratings (1-5) and written feedback with automated rating updates.
  10. `complaints`: Grievances submitted to administrative staff.
  11. `enquiries`: Direct inquiries sent from visitors to property hosts.
  12. `recently_viewed`: User browsing history.

---

## 6. Demo Accounts & Credentials

| Role | Email Address | Password | Permissions |
|---|---|---|---|
| **Customer** | `customer@findmystay.com` | `Customer@123` | Search, compare, wishlist, book, review |
| **Property Owner** | `owner@findmystay.com` | `Owner@123` | Add/edit/delete stays, approve bookings, availability |
| **Admin** | `admin@findmystay.com` | `Admin@123` | Verify stays, user directory, moderation, complaints |

*(Tip: On the `/login` page, you can click any role tag to auto-fill these credentials).*

---

## 7. Project Folder Structure

```
FindMyStay/
├── build/
│   ├── classes/                       # Compiled Java bytecode (.class)
│   └── dist/
│       └── FindMyStay.war             # Packaged WAR ready for deployment
├── database/
│   └── database.sql                   # Schema and 15 seeded properties
├── lib/
│   ├── jakarta.servlet-api.jar        # Jakarta Servlet 6.0 / 5.0 API
│   └── tomcat-embed-el.jar            # Expression Language library
├── src/
│   ├── com/findmystay/
│   │   ├── controller/                # 18 Servlets (Home, Search, Booking, Owner, Admin...)
│   │   ├── dao/                       # 8 JDBC Data Access Objects (PreparedStatements)
│   │   ├── filter/                    # AuthFilter & LoggingFilter
│   │   ├── model/                     # 10 JavaBean Entities
│   │   ├── service/                   # 5 Business Services
│   │   └── util/                      # DatabaseConnection, BCrypt, PasswordUtil, ValidationUtil
│   └── webapp/
│       ├── assets/
│       │   ├── css/                   # Editorial Design System (style, property, dashboard, auth)
│       │   ├── images/                # Vector stay illustrations & logo
│       │   └── js/                    # Client scripts & live booking calculator
│       └── WEB-INF/
│           ├── views/                 # JSP dynamic views
│           │   ├── admin/             # Admin portal
│           │   ├── auth/              # Login & Register
│           │   ├── customer/          # Home, search, details, book, compare, wishlist, dashboard
│           │   ├── fragments/         # Navbar, header, footer, alerts
│           │   └── owner/             # Owner portal, add/edit property
│           └── web.xml                # Servlet web deployment descriptor
├── compile.bat                        # One-click Windows compile & WAR packaging script
└── README.md                          # Comprehensive documentation
```

---

## 8. Setup & Installation Guide

### Prerequisites
1. **Java Development Kit (JDK 17 or higher)**
   Verify using:
   ```cmd
   javac -version
   java -version
   ```
2. **MySQL Server 8.0 or higher**
   Ensure MySQL service is running on port `3306`.
3. **Servlet Container**
   Apache Tomcat 10.x / 11.x, Eclipse Jetty 11+, or GlassFish 6+.

### Database Initialization
1. Open MySQL Workbench or MySQL Command Line Client:
   ```cmd
   mysql -u root -p
   ```
2. Execute the included SQL file:
   ```sql
   SOURCE C:/Users/topsu/Downloads/FindMyStay/database/database.sql;
   ```
   *This creates `findmystay_db`, all 12 tables, and seeds 15 properties across Chennai, Coimbatore, Salem, Bangalore, and Hyderabad.*

### JDBC Configuration
Database connection parameters are maintained in `src/com/findmystay/util/DatabaseConnection.java`:
- URL: `jdbc:mysql://localhost:3306/findmystay_db`
- Default User: `root`
- Default Password: `#Varshini123`

You can also override these at runtime via environment variables:
`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`.

---

## 9. How to Compile, Package & Deploy

### Step 1: Compile & Package
Double-click `compile.bat` or run:
```cmd
compile.bat
```
This compiles all Java source files against `lib/jakarta.servlet-api.jar` and packages the deployable artifact:
`build/dist/FindMyStay.war`.

### Step 2: Deploy to Servlet Container
- **Apache Tomcat 10+ / 11+**:
  Copy `build/dist/FindMyStay.war` into your Tomcat `webapps/` folder.
  Start Tomcat (`bin/startup.bat`).
  Access at: `http://localhost:8080/FindMyStay/`

- **Eclipse Jetty 11+ / 12+**:
  Deploy `FindMyStay.war` into the `webapps/` directory of your Jetty base.

---

## 10. Testing & Quality Verification

| Functional Area | Test Scope | Result |
|---|---|---|
| **Compilation** | Standard `javac` against `lib/jakarta.servlet-api.jar` | **Passed (0 warnings, 0 errors)** |
| **Packaging** | Production WAR generation with full webapp structure | **Passed (`FindMyStay.war` created)** |
| **Authentication** | BCrypt password hashing & slow-equals verification | **Passed (Secure salted hashes)** |
| **Authorization** | `AuthFilter` checks role on `/customer/*`, `/owner/*`, `/admin/*` | **Passed (Unauthorized requests blocked)** |
| **Search & Filters** | City, Type, Gender, AC, Food, Wi-Fi, Bath, Rent Range | **Passed (Dynamic SQL filters)** |
| **Live Availability** | Real-time owner update & decrement upon booking approval | **Passed** |
| **Comparison** | Side-by-side matrix for up to 3 properties | **Passed** |
| **Wishlist** | Toggle save/remove with duplicate prevention | **Passed** |
| **Booking Engine** | Reference generation (`FMS-2026-XXXX`) & status lifecycle | **Passed** |
| **Admin Moderation** | Property verification, listing toggle, review deletion | **Passed** |

---
*Created for FindMyStay technical review.*


