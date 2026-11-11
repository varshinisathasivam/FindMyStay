package com.findmystay.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents a property booking request by a customer.
 */
public class Booking implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum BookingStatus {
        PENDING, APPROVED, REJECTED, CANCELLED
    }

    private int id;
    private String bookingRef;
    private int customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    private int propertyId;
    private String propertyTitle;
    private String propertyCity;
    private String propertyAddress;
    private int ownerId;

    private String roomType;
    private Date moveInDate;
    private int durationMonths;
    private String contactNumber;
    private String message;
    private BookingStatus status;
    private BigDecimal totalAmount;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Booking() {
        this.status = BookingStatus.PENDING;
        this.durationMonths = 1;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBookingRef() { return bookingRef; }
    public void setBookingRef(String bookingRef) { this.bookingRef = bookingRef; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public int getPropertyId() { return propertyId; }
    public void setPropertyId(int propertyId) { this.propertyId = propertyId; }

    public String getPropertyTitle() { return propertyTitle; }
    public void setPropertyTitle(String propertyTitle) { this.propertyTitle = propertyTitle; }

    public String getPropertyCity() { return propertyCity; }
    public void setPropertyCity(String propertyCity) { this.propertyCity = propertyCity; }

    public String getPropertyAddress() { return propertyAddress; }
    public void setPropertyAddress(String propertyAddress) { this.propertyAddress = propertyAddress; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public Date getMoveInDate() { return moveInDate; }
    public void setMoveInDate(Date moveInDate) { this.moveInDate = moveInDate; }

    public int getDurationMonths() { return durationMonths; }
    public void setDurationMonths(int durationMonths) { this.durationMonths = durationMonths; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getStatusBadgeClass() {
        if (status == null) return "badge-neutral";
        switch (status) {
            case APPROVED: return "badge-success";
            case PENDING: return "badge-warning";
            case REJECTED: return "badge-danger";
            case CANCELLED: return "badge-secondary";
            default: return "badge-neutral";
        }
    }
}

