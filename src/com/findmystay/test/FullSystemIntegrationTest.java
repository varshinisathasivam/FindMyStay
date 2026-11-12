package com.findmystay.test;

import com.findmystay.controller.LoginServlet;
import com.findmystay.dao.*;
import com.findmystay.model.*;
import com.findmystay.service.*;
import com.findmystay.util.DatabaseConnection;
import com.findmystay.util.PasswordUtil;
import com.findmystay.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.util.List;

public class FullSystemIntegrationTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  FindMyStay Full-Stack End-to-End Test Suite");
        System.out.println("=================================================");

        UserService userService = new UserService();
        PropertyService propertyService = new PropertyService();
        BookingService bookingService = new BookingService();
        ReviewService reviewService = new ReviewService();
        WishlistService wishlistService = new WishlistService();
        ComplaintDAO complaintDAO = new ComplaintDAO();
        EnquiryDAO enquiryDAO = new EnquiryDAO();

        // -------------------------------------------------------------
        // MODULE 1: Registration
        // -------------------------------------------------------------
        System.out.println("\n[1] Testing Registration Module...");
        String uniqueEmail = "testuser_" + System.currentTimeMillis() + "@gmail.com";
        boolean registered = userService.register("Test User", uniqueEmail, "9123456780", "SecurePass@123", User.Role.CUSTOMER);
        check(registered, "New Customer Registration with unique email");

        // Duplicate email rejection
        boolean dupRegistered = userService.register("Test User Dup", uniqueEmail, "9123456780", "SecurePass@123", User.Role.CUSTOMER);
        check(!dupRegistered, "Duplicate Email Registration is correctly rejected");

        // Form validation
        boolean invalidEmailReg = userService.register("Bad Email User", "notanemail", "9123456780", "Pass@123", User.Role.CUSTOMER);
        check(!invalidEmailReg, "Invalid Email Registration is correctly rejected");

        boolean weakPassReg = userService.register("Weak Pass User", "weakpass@test.com", "9123456780", "123", User.Role.CUSTOMER);
        check(!weakPassReg, "Weak Password Registration is correctly rejected");

        // -------------------------------------------------------------
        // MODULE 2: Login & Authentication
        // -------------------------------------------------------------
        System.out.println("\n[2] Testing Authentication & Login Module...");
        User authNewUser = userService.authenticate(uniqueEmail, "SecurePass@123");
        check(authNewUser != null && authNewUser.getEmail().equalsIgnoreCase(uniqueEmail), "Newly registered customer can authenticate");

        User wrongPassUser = userService.authenticate(uniqueEmail, "WrongPassword@123");
        check(wrongPassUser == null, "Incorrect password returns null authentication");

        User nonExistentUser = userService.authenticate("nonexistent_user_99@test.com", "SomePass@123");
        check(nonExistentUser == null, "Non-existent user returns null authentication");

        User demoAdmin = userService.authenticate("admin@findmystay.com", "Admin@123");
        check(demoAdmin != null && demoAdmin.isAdmin(), "Demo Admin authenticates with role ADMIN");

        User demoOwner = userService.authenticate("owner@findmystay.com", "Owner@123");
        check(demoOwner != null && demoOwner.isOwner(), "Demo Owner authenticates with role OWNER");

        User demoCustomer = userService.authenticate("customer@findmystay.com", "Customer@123");
        check(demoCustomer != null && !demoCustomer.isOwner() && !demoCustomer.isAdmin(), "Demo Customer authenticates with role CUSTOMER");

        // -------------------------------------------------------------
        // MODULE 3: Customer Module (Search, Details, Wishlist, Compare, Book)
        // -------------------------------------------------------------
        System.out.println("\n[3] Testing Customer Module...");
        List<Property> chennaiStays = propertyService.searchProperties("Chennai", null, null, null, null, null, null, null, null, null, "rating_desc");
        check(chennaiStays != null && !chennaiStays.isEmpty(), "Property search in Chennai returns results: " + (chennaiStays != null ? chennaiStays.size() : 0));

        Property stay1 = propertyService.getPropertyDetails(1);
        check(stay1 != null && stay1.getTitle() != null, "Property details loaded for stay #1 (" + (stay1 != null ? stay1.getTitle() : "none") + ")");

        // Wishlist
        wishlistService.addToWishlist(authNewUser.getId(), 1);
        boolean isWish = wishlistService.isWishlisted(authNewUser.getId(), 1);
        check(isWish, "Wishlist add and verification for stay #1");

        wishlistService.removeFromWishlist(authNewUser.getId(), 1);
        boolean notWish = !wishlistService.isWishlisted(authNewUser.getId(), 1);
        check(notWish, "Wishlist remove for stay #1");

        // Booking creation via service
        Date moveInDate = new Date(System.currentTimeMillis() + 86400000L);
        Booking newBooking = bookingService.createBooking(authNewUser.getId(), 1, "Double Sharing AC Room", moveInDate, 3, "9123456780", "Automated test booking");
        check(newBooking != null && newBooking.getId() > 0, "Booking creation via BookingService succeeds (Ref: " + (newBooking != null ? newBooking.getBookingRef() : "none") + ")");
        check(newBooking != null && newBooking.getTotalAmount() != null && newBooking.getTotalAmount().compareTo(BigDecimal.ZERO) > 0, "Booking price correctly calculated based on rate & duration");

        // Transactional Booking Approval & Duplicate Prevention
        boolean firstApprove = bookingService.updateStatus(newBooking.getId(), Booking.BookingStatus.APPROVED, 2, false);
        check(firstApprove, "Transactional booking approval succeeds for Owner #2");

        boolean dupApprove = bookingService.updateStatus(newBooking.getId(), Booking.BookingStatus.APPROVED, 2, false);
        check(!dupApprove, "Duplicate booking approval is correctly rejected (idempotent protection)");

        List<Booking> customerBookings = bookingService.getCustomerBookings(authNewUser.getId());
        check(customerBookings != null && !customerBookings.isEmpty(), "Customer booking history retrieval (count: " + (customerBookings != null ? customerBookings.size() : 0) + ")");

        // -------------------------------------------------------------
        // MODULE 4: Owner Module & Authorization
        // -------------------------------------------------------------
        System.out.println("\n[4] Testing Owner Module & Authorization Security...");
        List<Property> ownerProperties = propertyService.getPropertiesByOwner(demoOwner.getId());
        check(ownerProperties != null && !ownerProperties.isEmpty(), "Owner properties retrieval for Senthil Murugan (owner_id=" + demoOwner.getId() + ")");

        // Live availability update for own property (Property #1 belongs to Owner #2)
        boolean ownUpdate = propertyService.updateLiveAvailability(1, 5, demoOwner.getId());
        check(ownUpdate, "Owner successfully updates availability for their own property");

        // IDOR Attack Simulation: Owner #2 attempting to modify Owner #5's property (#3)
        Property prop3 = propertyService.getPropertyDetails(3);
        int foreignOwnerId = prop3.getOwnerId(); // Owner #5
        boolean crossOwnerUpdate = propertyService.updateLiveAvailability(3, 10, demoOwner.getId());
        check(!crossOwnerUpdate, "IDOR Prevention: Owner #2 cannot update availability of Owner #" + foreignOwnerId + "'s property");

        // -------------------------------------------------------------
        // MODULE 5: Admin Module
        // -------------------------------------------------------------
        System.out.println("\n[5] Testing Admin Module...");
        List<Property> allPropsAdmin = propertyService.getAllPropertiesForAdmin();
        check(allPropsAdmin != null && allPropsAdmin.size() >= 15, "Admin retrieves full property directory (" + (allPropsAdmin != null ? allPropsAdmin.size() : 0) + " properties)");

        int totalUsers = userService.getAllUsers().size();
        check(totalUsers >= 5, "Admin user directory retrieval (" + totalUsers + " registered users)");

        List<Complaint> complaints = complaintDAO.findAllComplaints();
        check(complaints != null, "Admin complaints overview (" + (complaints != null ? complaints.size() : 0) + " complaints)");

        // -------------------------------------------------------------
        // MODULE 6: Security & Redirects
        // -------------------------------------------------------------
        System.out.println("\n[6] Testing Security & Redirect Controls...");
        boolean openRedirectBlocked = !LoginServlet.isSafeInternalRedirect(null, "https://attacker-site.com", demoCustomer);
        check(openRedirectBlocked, "External redirect blocked: https://attacker-site.com");

        boolean protocolRelBlocked = !LoginServlet.isSafeInternalRedirect(null, "//attacker-site.com", demoCustomer);
        check(protocolRelBlocked, "Protocol-relative redirect blocked: //attacker-site.com");

        boolean roleEscalationBlocked = !LoginServlet.isSafeInternalRedirect(null, "/admin/dashboard", demoCustomer);
        check(roleEscalationBlocked, "Privilege escalation redirect blocked: Customer redirected to /admin/dashboard");

        // Clean up test user
        userService.deleteUser(authNewUser.getId());
        System.out.println("\n[Cleanup] Test user cleaned up.");

        System.out.println("\n=================================================");
        System.out.println("  SYSTEM TEST SUMMARY: " + passed + " PASSED, " + failed + " FAILED");
        System.out.println("=================================================");

        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void check(boolean condition, String description) {
        if (condition) {
            System.out.println("  [PASS] " + description);
            passed++;
        } else {
            System.err.println("  [FAIL] " + description);
            failed++;
        }
    }
}
