package com.findmystay.controller;

import com.findmystay.dao.ComplaintDAO;
import com.findmystay.model.*;
import com.findmystay.service.BookingService;
import com.findmystay.service.PropertyService;
import com.findmystay.service.ReviewService;
import com.findmystay.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller for comprehensive administrative functions.
 */
@WebServlet(name = "AdminServlet", urlPatterns = {
    "/admin/dashboard",
    "/admin/verify",
    "/admin/property/toggle",
    "/admin/booking/status",
    "/admin/user/delete"
})
public class AdminServlet extends HttpServlet {

    private final UserService userService = new UserService();
    private final PropertyService propertyService = new PropertyService();
    private final BookingService bookingService = new BookingService();
    private final ReviewService reviewService = new ReviewService();
    private final ComplaintDAO complaintDAO = new ComplaintDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null || !currentUser.isAdmin()) {
            resp.sendRedirect(req.getContextPath() + "/login?error=Admin authorization required");
            return;
        }

        // Metrics
        int totalCustomers = userService.getCustomerCount();
        int totalOwners = userService.getOwnerCount();
        int totalProperties = propertyService.getTotalPropertiesCount();
        int pendingVerification = propertyService.getPendingVerificationCount();
        int totalBookings = bookingService.getTotalBookingsCount();

        // Data collections
        List<User> allUsers = userService.getAllUsers();
        List<Property> allProperties = propertyService.getAllPropertiesForAdmin();
        List<Booking> allBookings = bookingService.getAllBookings();
        List<Review> allReviews = reviewService.getAllReviews();
        List<Complaint> allComplaints = complaintDAO.findAllComplaints();

        req.setAttribute("totalCustomers", totalCustomers);
        req.setAttribute("totalOwners", totalOwners);
        req.setAttribute("totalProperties", totalProperties);
        req.setAttribute("pendingVerification", pendingVerification);
        req.setAttribute("totalBookings", totalBookings);

        req.setAttribute("allUsers", allUsers);
        req.setAttribute("allProperties", allProperties);
        req.setAttribute("allBookings", allBookings);
        req.setAttribute("allReviews", allReviews);
        req.setAttribute("allComplaints", allComplaints);

        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null || !currentUser.isAdmin()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String uri = req.getRequestURI();

        if (uri.endsWith("/verify")) {
            String propIdStr = req.getParameter("propertyId");
            String verifiedStr = req.getParameter("verified");
            if (propIdStr != null && verifiedStr != null) {
                try {
                    propertyService.verifyProperty(Integer.parseInt(propIdStr), Boolean.parseBoolean(verifiedStr));
                } catch (Exception ignored) {}
            }
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard?tab=properties&success=Verification updated");

        } else if (uri.endsWith("/property/toggle")) {
            String propIdStr = req.getParameter("propertyId");
            String activeStr = req.getParameter("active");
            if (propIdStr != null && activeStr != null) {
                try {
                    propertyService.toggleActive(Integer.parseInt(propIdStr), Boolean.parseBoolean(activeStr));
                } catch (Exception ignored) {}
            }
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard?tab=properties&success=Listing status updated");

        } else if (uri.endsWith("/booking/status")) {
            String bIdStr = req.getParameter("bookingId");
            String statusStr = req.getParameter("status");
            if (bIdStr != null && statusStr != null) {
                try {
                    boolean updated = bookingService.updateStatus(Integer.parseInt(bIdStr), Booking.BookingStatus.valueOf(statusStr), currentUser.getId(), true);
                    if (updated) {
                        resp.sendRedirect(req.getContextPath() + "/admin/dashboard?tab=bookings&success=Booking status updated successfully");
                    } else {
                        resp.sendRedirect(req.getContextPath() + "/admin/dashboard?tab=bookings&error=Unable to update booking status");
                    }
                    return;
                } catch (Exception ignored) {}
            }
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard?tab=bookings&error=Invalid booking request");
            return;

        } else if (uri.endsWith("/user/delete")) {
            String uIdStr = req.getParameter("userId");
            if (uIdStr != null) {
                try {
                    userService.deleteUser(Integer.parseInt(uIdStr));
                } catch (Exception ignored) {}
            }
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard?tab=users&success=User deleted");

        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
        }
    }
}

