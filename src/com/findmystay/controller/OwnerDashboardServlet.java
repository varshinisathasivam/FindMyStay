package com.findmystay.controller;

import com.findmystay.dao.EnquiryDAO;
import com.findmystay.model.Booking;
import com.findmystay.model.Enquiry;
import com.findmystay.model.Property;
import com.findmystay.model.User;
import com.findmystay.service.BookingService;
import com.findmystay.service.PropertyService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller for the Property Owner Dashboard and operations.
 */
@WebServlet(name = "OwnerDashboardServlet", urlPatterns = {
    "/owner/dashboard",
    "/owner/properties",
    "/owner/bookings",
    "/owner/enquiries",
    "/owner/booking/status",
    "/owner/availability"
})
public class OwnerDashboardServlet extends HttpServlet {

    private final PropertyService propertyService = new PropertyService();
    private final BookingService bookingService = new BookingService();
    private final EnquiryDAO enquiryDAO = new EnquiryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentOwner = (User) session.getAttribute("currentUser");

        List<Property> properties = propertyService.getPropertiesByOwner(currentOwner.getId());
        List<Booking> bookings = bookingService.getOwnerBookings(currentOwner.getId());
        List<Enquiry> enquiries = enquiryDAO.findByOwnerId(currentOwner.getId());

        int pendingCount = 0;
        int approvedCount = 0;
        for (Booking b : bookings) {
            if (b.getStatus() == Booking.BookingStatus.PENDING) pendingCount++;
            else if (b.getStatus() == Booking.BookingStatus.APPROVED) approvedCount++;
        }

        req.setAttribute("properties", properties);
        req.setAttribute("bookings", bookings);
        req.setAttribute("enquiries", enquiries);
        req.setAttribute("totalProperties", properties.size());
        req.setAttribute("totalBookings", bookings.size());
        req.setAttribute("pendingBookings", pendingCount);
        req.setAttribute("approvedBookings", approvedCount);

        req.getRequestDispatcher("/WEB-INF/views/owner/dashboard.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentOwner = (User) session.getAttribute("currentUser");
        String uri = req.getRequestURI();

        if (uri.endsWith("/status")) {
            // Update booking status (APPROVE / REJECT) with transaction
            String bookingIdStr = req.getParameter("bookingId");
            String statusStr = req.getParameter("status");

            if (bookingIdStr != null && statusStr != null) {
                try {
                    int bookingId = Integer.parseInt(bookingIdStr);
                    Booking.BookingStatus status = Booking.BookingStatus.valueOf(statusStr.toUpperCase());
                    boolean updated = bookingService.updateStatus(bookingId, status, currentOwner.getId(), false);
                    if (updated) {
                        resp.sendRedirect(req.getContextPath() + "/owner/dashboard?tab=bookings&success=Booking status updated successfully");
                    } else {
                        resp.sendRedirect(req.getContextPath() + "/owner/dashboard?tab=bookings&error=Unable to update booking. Either rooms are full or status was already updated.");
                    }
                    return;
                } catch (Exception ignored) {}
            }
            resp.sendRedirect(req.getContextPath() + "/owner/dashboard?tab=bookings&error=Invalid booking request");
            return;

        } else if (uri.endsWith("/availability")) {
            // Update live room availability with backend authorization check
            if (currentOwner == null) {
                resp.sendRedirect(req.getContextPath() + "/login?error=Session expired. Please log in.");
                return;
            }

            String propertyIdStr = req.getParameter("propertyId");
            String countStr = req.getParameter("availableRooms");

            if (propertyIdStr != null && countStr != null) {
                try {
                    int propertyId = Integer.parseInt(propertyIdStr.trim());
                    int count = Integer.parseInt(countStr.trim());

                    // Validate property ownership
                    Property prop = propertyService.getPropertyDetails(propertyId);
                    if (prop == null || (prop.getOwnerId() != currentOwner.getId() && !currentOwner.isAdmin())) {
                        resp.sendRedirect(req.getContextPath() + "/owner/dashboard?error=Unauthorized: You can only update your own property.");
                        return;
                    }

                    boolean updated = currentOwner.isAdmin()
                            ? propertyService.updateLiveAvailability(propertyId, count)
                            : propertyService.updateLiveAvailability(propertyId, count, currentOwner.getId());

                    if (updated) {
                        resp.sendRedirect(req.getContextPath() + "/owner/dashboard?success=Room availability updated successfully");
                    } else {
                        resp.sendRedirect(req.getContextPath() + "/owner/dashboard?error=Failed to update room availability.");
                    }
                    return;
                } catch (NumberFormatException e) {
                    resp.sendRedirect(req.getContextPath() + "/owner/dashboard?error=Invalid property or room count format.");
                    return;
                }
            }
            resp.sendRedirect(req.getContextPath() + "/owner/dashboard");
        } else {
            resp.sendRedirect(req.getContextPath() + "/owner/dashboard");
        }
    }
}

