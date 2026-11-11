package com.findmystay.service;

import com.findmystay.dao.BookingDAO;
import com.findmystay.dao.PropertyDAO;
import com.findmystay.model.Booking;
import com.findmystay.model.Property;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.sql.Date;
import java.time.Year;
import java.util.List;

/**
 * Business service for booking workflows, reference generation, and status updates.
 */
public class BookingService {

    private final BookingDAO bookingDAO;
    private final PropertyDAO propertyDAO;
    private static final SecureRandom RANDOM = new SecureRandom();

    public BookingService() {
        this.bookingDAO = new BookingDAO();
        this.propertyDAO = new PropertyDAO();
    }

    public BookingService(BookingDAO bookingDAO, PropertyDAO propertyDAO) {
        this.bookingDAO = bookingDAO;
        this.propertyDAO = propertyDAO;
    }

    public Booking createBooking(int customerId, int propertyId, String roomType,
                                 Date moveInDate, int durationMonths, String contactNumber, String message) {

        Property property = propertyDAO.findById(propertyId);
        if (property == null || !property.isActive()) {
            return null;
        }

        if (durationMonths <= 0) durationMonths = 1;
        BigDecimal monthlyRate = property.getMonthlyRent();
        BigDecimal total = monthlyRate.multiply(new BigDecimal(durationMonths));

        String ref = generateBookingReference();

        Booking booking = new Booking();
        booking.setBookingRef(ref);
        booking.setCustomerId(customerId);
        booking.setPropertyId(propertyId);
        booking.setRoomType(roomType != null ? roomType : "Standard Room");
        booking.setMoveInDate(moveInDate);
        booking.setDurationMonths(durationMonths);
        booking.setContactNumber(contactNumber);
        booking.setMessage(message);
        booking.setStatus(Booking.BookingStatus.PENDING);
        booking.setTotalAmount(total);

        boolean success = bookingDAO.createBooking(booking);
        return success ? booking : null;
    }

    public List<Booking> getCustomerBookings(int customerId) {
        return bookingDAO.findByCustomerId(customerId);
    }

    public List<Booking> getOwnerBookings(int ownerId) {
        return bookingDAO.findByOwnerId(ownerId);
    }

    public List<Booking> getAllBookings() {
        return bookingDAO.findAllBookings();
    }

    public boolean updateStatus(int bookingId, Booking.BookingStatus status, int ownerOrAdminId, boolean isAdmin) {
        Booking booking = bookingDAO.findById(bookingId);
        if (booking == null) return false;

        // Verify owner permissions if not admin
        if (!isAdmin && booking.getOwnerId() != ownerOrAdminId) {
            return false;
        }

        boolean updated = bookingDAO.updateStatus(bookingId, status);
        if (updated && status == Booking.BookingStatus.APPROVED) {
            // Decrement available room count if approved
            Property prop = propertyDAO.findById(booking.getPropertyId());
            if (prop != null && prop.getAvailableRooms() > 0) {
                propertyDAO.updateLiveAvailability(prop.getId(), prop.getAvailableRooms() - 1);
            }
        }
        return updated;
    }

    public boolean cancelBooking(int bookingId, int customerId) {
        return bookingDAO.cancelBooking(bookingId, customerId);
    }

    public int getTotalBookingsCount() {
        return bookingDAO.countTotal();
    }

    public int getCustomerPendingCount(int customerId) {
        return bookingDAO.countByCustomerAndStatus(customerId, Booking.BookingStatus.PENDING);
    }

    public int getCustomerApprovedCount(int customerId) {
        return bookingDAO.countByCustomerAndStatus(customerId, Booking.BookingStatus.APPROVED);
    }

    private String generateBookingReference() {
        int year = Year.now().getValue();
        int randomNum = 1000 + RANDOM.nextInt(9000);
        return "FMS-" + year + "-" + randomNum;
    }
}

