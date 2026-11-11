package com.findmystay.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Represents a saved wishlist property item.
 */
public class Wishlist implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int userId;
    private int propertyId;
    private Timestamp createdAt;
    private Property property; // Populated for display

    public Wishlist() {}

    public Wishlist(int userId, int propertyId) {
        this.userId = userId;
        this.propertyId = propertyId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getPropertyId() { return propertyId; }
    public void setPropertyId(int propertyId) { this.propertyId = propertyId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Property getProperty() { return property; }
    public void setProperty(Property property) { this.property = property; }
}

