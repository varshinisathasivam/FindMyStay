package com.findmystay.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Represents a customer review and rating for a property.
 */
public class Review implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int propertyId;
    private String propertyTitle;
    private int userId;
    private String userName;
    private int rating;
    private String comment;
    private Timestamp createdAt;

    public Review() {}

    public Review(int propertyId, int userId, int rating, String comment) {
        this.propertyId = propertyId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPropertyId() { return propertyId; }
    public void setPropertyId(int propertyId) { this.propertyId = propertyId; }

    public String getPropertyTitle() { return propertyTitle; }
    public void setPropertyTitle(String propertyTitle) { this.propertyTitle = propertyTitle; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}

