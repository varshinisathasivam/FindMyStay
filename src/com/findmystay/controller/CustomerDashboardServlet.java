package com.findmystay.controller;

import com.findmystay.dao.RecentlyViewedDAO;
import com.findmystay.model.Booking;
import com.findmystay.model.Property;
import com.findmystay.model.User;
import com.findmystay.model.Wishlist;
import com.findmystay.service.BookingService;
import com.findmystay.service.UserService;
import com.findmystay.service.WishlistService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller for the Customer Dashboard and personal portal.
 */
@WebServlet(name = "CustomerDashboardServlet", urlPatterns = {"/customer/dashboard", "/customer/profile"})
public class CustomerDashboardServlet extends HttpServlet {

    private final UserService userService = new UserService();
    private final BookingService bookingService = new BookingService();
    private final WishlistService wishlistService = new WishlistService();
    private final RecentlyViewedDAO recentlyViewedDAO = new RecentlyViewedDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");

        // Fresh user details
        User freshUser = userService.getUserById(currentUser.getId());
        session.setAttribute("currentUser", freshUser);

        List<Booking> bookings = bookingService.getCustomerBookings(currentUser.getId());
        List<Wishlist> wishlists = wishlistService.getUserWishlist(currentUser.getId());
        List<Property> recentlyViewed = recentlyViewedDAO.getRecentlyViewed(currentUser.getId(), 4);

        int totalBookings = bookings.size();
        int pendingBookings = bookingService.getCustomerPendingCount(currentUser.getId());
        int approvedBookings = bookingService.getCustomerApprovedCount(currentUser.getId());
        int wishlistCount = wishlists.size();

        req.setAttribute("user", freshUser);
        req.setAttribute("bookings", bookings);
        req.setAttribute("wishlists", wishlists);
        req.setAttribute("recentlyViewed", recentlyViewed);
        req.setAttribute("totalBookings", totalBookings);
        req.setAttribute("pendingBookings", pendingBookings);
        req.setAttribute("approvedBookings", approvedBookings);
        req.setAttribute("wishlistCount", wishlistCount);

        req.getRequestDispatcher("/WEB-INF/views/customer/dashboard.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");
        String action = req.getParameter("action");

        if ("updateProfile".equals(action)) {
            String fullName = req.getParameter("fullName");
            String phone = req.getParameter("phone");

            boolean updated = userService.updateProfile(currentUser.getId(), fullName, phone);
            if (updated) {
                resp.sendRedirect(req.getContextPath() + "/customer/dashboard?success=Profile updated successfully!");
            } else {
                resp.sendRedirect(req.getContextPath() + "/customer/dashboard?error=Failed to update profile.");
            }
        } else if ("changePassword".equals(action)) {
            String currentPass = req.getParameter("currentPassword");
            String newPass = req.getParameter("newPassword");
            String confirmPass = req.getParameter("confirmPassword");

            if (!newPass.equals(confirmPass)) {
                resp.sendRedirect(req.getContextPath() + "/customer/dashboard?error=New passwords do not match.");
                return;
            }

            boolean changed = userService.changePassword(currentUser.getId(), currentPass, newPass);
            if (changed) {
                resp.sendRedirect(req.getContextPath() + "/customer/dashboard?success=Password changed successfully!");
            } else {
                resp.sendRedirect(req.getContextPath() + "/customer/dashboard?error=Current password incorrect or invalid.");
            }
        } else {
            resp.sendRedirect(req.getContextPath() + "/customer/dashboard");
        }
    }
}

