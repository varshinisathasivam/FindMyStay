package com.findmystay.test;

import com.findmystay.dao.*;
import com.findmystay.model.*;
import com.findmystay.service.*;
import com.findmystay.util.DatabaseConnection;
import com.findmystay.util.PasswordUtil;

import java.sql.Connection;
import java.util.List;

/**
 * Verification test suite for database and DAO operations against MySQL.
 */
public class DatabaseVerificationTest {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("STARTING FINDMYSTAY DATABASE & DAO TEST SUITE");
        System.out.println("=================================================");

        int passed = 0;
        int failed = 0;

        // Test 1: JDBC Connection
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("[PASS] 1. JDBC Connection successful: " + conn.getMetaData().getDatabaseProductName() + " " + conn.getMetaData().getDatabaseProductVersion());
                passed++;
            } else {
                System.err.println("[FAIL] 1. Connection was null or closed");
                failed++;
            }
        } catch (Exception e) {
            System.err.println("[FAIL] 1. Connection exception: " + e.getMessage());
            failed++;
        }

        // Test 2: User Authentication & BCrypt
        try {
            UserService userService = new UserService();
            User customer = userService.authenticate("customer@findmystay.com", "Customer@123");
            if (customer != null && customer.getRole() == User.Role.CUSTOMER) {
                System.out.println("[PASS] 2. Customer Authentication: " + customer.getFullName() + " (" + customer.getEmail() + ")");
                passed++;
            } else {
                System.err.println("[FAIL] 2. Customer authentication returned null or wrong role");
                failed++;
            }

            User owner = userService.authenticate("owner@findmystay.com", "Owner@123");
            if (owner != null && owner.getRole() == User.Role.OWNER) {
                System.out.println("[PASS] 2b. Owner Authentication: " + owner.getFullName() + " (" + owner.getEmail() + ")");
                passed++;
            } else {
                System.err.println("[FAIL] 2b. Owner authentication failed");
                failed++;
            }

            User admin = userService.authenticate("admin@findmystay.com", "Admin@123");
            if (admin != null && admin.getRole() == User.Role.ADMIN) {
                System.out.println("[PASS] 2c. Admin Authentication: " + admin.getFullName() + " (" + admin.getEmail() + ")");
                passed++;
            } else {
                System.err.println("[FAIL] 2c. Admin authentication failed");
                failed++;
            }
        } catch (Exception e) {
            System.err.println("[FAIL] 2. User authentication exception: " + e.getMessage());
            failed++;
        }

        // Test 3: PropertyDAO Searches & Filters
        try {
            PropertyDAO propDAO = new PropertyDAO();
            List<Property> chennaiStays = propDAO.searchProperties("Chennai", null, null, null, null, null, null, null, null, null, null);
            if (chennaiStays != null && chennaiStays.size() >= 3) {
                System.out.println("[PASS] 3. Property Search by City (Chennai): found " + chennaiStays.size() + " stays");
                passed++;
            } else {
                System.err.println("[FAIL] 3. Chennai stays count less than expected: " + (chennaiStays != null ? chennaiStays.size() : 0));
                failed++;
            }

            List<Property> featured = propDAO.getFeaturedProperties(6);
            if (featured != null && !featured.isEmpty()) {
                System.out.println("[PASS] 3b. Featured Verified Stays: loaded " + featured.size() + " stays");
                passed++;
            } else {
                System.err.println("[FAIL] 3b. Featured stays list empty");
                failed++;
            }

            Property p1 = propDAO.findById(1);
            if (p1 != null && p1.getRoomTypes() != null && !p1.getRoomTypes().isEmpty()) {
                System.out.println("[PASS] 3c. Property Details & RoomTypes: " + p1.getTitle() + " has " + p1.getRoomTypes().size() + " room types");
                passed++;
            } else {
                System.err.println("[FAIL] 3c. Room types for property 1 missing");
                failed++;
            }
        } catch (Exception e) {
            System.err.println("[FAIL] 3. PropertyDAO exception: " + e.getMessage());
            failed++;
        }

        // Test 4: BookingDAO Operations
        try {
            BookingDAO bookingDAO = new BookingDAO();
            List<Booking> bookings = bookingDAO.findByCustomerId(1);
            if (bookings != null && !bookings.isEmpty()) {
                System.out.println("[PASS] 4. Customer Bookings: customer #1 has " + bookings.size() + " bookings (Ref: " + bookings.get(0).getBookingRef() + ")");
                passed++;
            } else {
                System.err.println("[FAIL] 4. Customer bookings list empty");
                failed++;
            }
        } catch (Exception e) {
            System.err.println("[FAIL] 4. BookingDAO exception: " + e.getMessage());
            failed++;
        }

        // Test 5: WishlistDAO & ReviewDAO
        try {
            WishlistDAO wishlistDAO = new WishlistDAO();
            List<Wishlist> wishlists = wishlistDAO.findByUserId(1);
            System.out.println("[PASS] 5. Wishlist retrieved: " + (wishlists != null ? wishlists.size() : 0) + " items");
            passed++;

            ReviewDAO reviewDAO = new ReviewDAO();
            List<Review> reviews = reviewDAO.findByPropertyId(1);
            System.out.println("[PASS] 5b. Reviews retrieved: " + (reviews != null ? reviews.size() : 0) + " reviews for stay #1");
            passed++;
        } catch (Exception e) {
            System.err.println("[FAIL] 5. Wishlist/Review exception: " + e.getMessage());
            failed++;
        }

        System.out.println("=================================================");
        System.out.println("TEST SUITE RESULTS: " + passed + " PASSED, " + failed + " FAILED");
        System.out.println("=================================================");

        if (failed > 0) {
            System.exit(1);
        }
    }
}

