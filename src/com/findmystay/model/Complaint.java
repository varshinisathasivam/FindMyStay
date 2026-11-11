package com.findmystay.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Represents a customer or owner complaint submitted to the platform.
 */
public class Complaint implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum ComplaintStatus {
        OPEN, IN_PROGRESS, RESOLVED
    }

    private int id;
    private int userId;
    private String userName;
    private String userEmail;
    private Integer propertyId;
    private String propertyTitle;
    private String subject;
    private String description;
    private ComplaintStatus status;
    private Timestamp createdAt;

    public Complaint() {
        this.status = ComplaintStatus.OPEN;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public Integer getPropertyId() { return propertyId; }
    public void setPropertyId(Integer propertyId) { this.propertyId = propertyId; }

    public String getPropertyTitle() { return propertyTitle; }
    public void setPropertyTitle(String propertyTitle) { this.propertyTitle = propertyTitle; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ComplaintStatus getStatus() { return status; }
    public void setStatus(ComplaintStatus status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}

