package com.findmystay.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Represents a property enquiry sent by a user/visitor to an owner.
 */
public class Enquiry implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum EnquiryStatus {
        NEW, CONTACTED, CLOSED
    }

    private int id;
    private int propertyId;
    private String propertyTitle;
    private Integer userId;
    private String senderName;
    private String senderEmail;
    private String senderPhone;
    private String message;
    private EnquiryStatus status;
    private Timestamp createdAt;

    public Enquiry() {
        this.status = EnquiryStatus.NEW;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPropertyId() { return propertyId; }
    public void setPropertyId(int propertyId) { this.propertyId = propertyId; }

    public String getPropertyTitle() { return propertyTitle; }
    public void setPropertyTitle(String propertyTitle) { this.propertyTitle = propertyTitle; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderEmail() { return senderEmail; }
    public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }

    public String getSenderPhone() { return senderPhone; }
    public void setSenderPhone(String senderPhone) { this.senderPhone = senderPhone; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public EnquiryStatus getStatus() { return status; }
    public void setStatus(EnquiryStatus status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}

