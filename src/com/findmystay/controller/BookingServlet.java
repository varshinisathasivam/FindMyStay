package com.findmystay.controller;

import com.findmystay.model.Booking;
import com.findmystay.model.Property;
import com.findmystay.model.User;
import com.findmystay.service.BookingService;
import com.findmystay.service.PropertyService;
import com.findmystay.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Date;

/**
 * Controller for booking requests and cancellations.
 */
@WebServlet(name = "BookingServlet", urlPatterns = {"/book", "/customer/booking/cancel"})
public class BookingServlet extends HttpServlet {

    private final BookingService bookingService = new BookingService();
    private final PropertyService propertyService = new PropertyService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            String propId = req.getParameter("propertyId");
            resp.sendRedirect(req.getContextPath() + "/login?redirect=" + req.getContextPath() + "/book?propertyId=" + propId + "&error=Please login to book a stay");
            return;
        }

        String propIdStr = req.getParameter("propertyId");
        if (propIdStr == null) {
            resp.sendRedirect(req.getContextPath() + "/stays");
            return;
        }

        try {
            int propId = Integer.parseInt(propIdStr);
            Property property = propertyService.getPropertyDetails(propId);
            if (property == null || !property.isActive()) {
                resp.sendRedirect(req.getContextPath() + "/stays?error=Property unavailable");
                return;
            }

            req.setAttribute("property", property);
            req.setAttribute("user", currentUser);
            req.getRequestDispatcher("/WEB-INF/views/customer/book.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/stays");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String uri = req.getRequestURI();

        if (uri.endsWith("/cancel")) {
            String bookingIdStr = req.getParameter("bookingId");
            if (bookingIdStr != null) {
                try {
                    int bookingId = Integer.parseInt(bookingIdStr);
                    bookingService.cancelBooking(bookingId, currentUser.getId());
                } catch (Exception ignored) {}
            }
            resp.sendRedirect(req.getContextPath() + "/customer/dashboard?tab=bookings&success=Booking cancelled successfully");
            return;
        }

        // Create new booking
        String propIdStr = req.getParameter("propertyId");
        String roomType = req.getParameter("roomType");
        String moveInDateStr = req.getParameter("moveInDate");
        String durationStr = req.getParameter("durationMonths");
        String phone = req.getParameter("contactNumber");
        String message = req.getParameter("message");

        if (propIdStr == null || moveInDateStr == null || !ValidationUtil.isValidPhone(phone)) {
            resp.sendRedirect(req.getContextPath() + "/book?propertyId=" + propIdStr + "&error=Invalid contact phone or missing fields");
            return;
        }

        try {
            int propId = Integer.parseInt(propIdStr);
            Date moveInDate = Date.valueOf(moveInDateStr.trim());
            int duration = Integer.parseInt(durationStr != null ? durationStr.trim() : "1");

            Booking booking = bookingService.createBooking(
                currentUser.getId(), propId, roomType, moveInDate, duration, phone, message
            );

            if (booking != null) {
                resp.sendRedirect(req.getContextPath() + "/customer/dashboard?tab=bookings&success=Booking request submitted! Reference: " + booking.getBookingRef());
            } else {
                resp.sendRedirect(req.getContextPath() + "/book?propertyId=" + propIdStr + "&error=Failed to submit booking request");
            }
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/stays?error=Invalid booking parameters");
        }
    }
}

