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

        // Room availability check
        if (property.getAvailableRooms() <= 0) {
            return null;
        }

        // Validate booking dates: move-in date cannot be in the past
        if (moveInDate == null) {
            return null;
        }
        long now = System.currentTimeMillis() - (24 * 60 * 60 * 1000L); // 1-day tolerance for timezone
        if (moveInDate.getTime() < now) {
            return null;
        }

        if (durationMonths <= 0) durationMonths = 1;
        if (durationMonths > 36) durationMonths = 36;

        // Calculate booking prices using the correct selected room type and applicable rate
        BigDecimal monthlyRate = property.getMonthlyRent();
        if (roomType != null && property.getRoomTypes() != null) {
            for (com.findmystay.model.RoomType rt : property.getRoomTypes()) {
                if (rt.getName() != null && rt.getName().equalsIgnoreCase(roomType.trim())) {
                    if (rt.getRent() != null && rt.getRent().compareTo(BigDecimal.ZERO) > 0) {
                        monthlyRate = rt.getRent();
                    }
                    break;
                }
            }
        }
        BigDecimal total = monthlyRate.multiply(new BigDecimal(durationMonths));

        String ref = generateBookingReference();

        Booking booking = new Booking();
        booking.setBookingRef(ref);
        booking.setCustomerId(customerId);
        booking.setPropertyId(propertyId);
        booking.setRoomType(roomType != null && !roomType.trim().isEmpty() ? roomType.trim() : "Standard Room");
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
        return bookingDAO.updateStatusWithTransaction(bookingId, status, ownerOrAdminId, isAdmin);
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

