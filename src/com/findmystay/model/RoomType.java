package com.findmystay.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Represents a room category within a property (e.g. Single AC, Double Non-AC).
 */
public class RoomType implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int propertyId;
    private String name;
    private BigDecimal rent;
    private int availableCount;
    private boolean hasAc;
    private boolean hasAttachedBath;

    public RoomType() {}

    public RoomType(int propertyId, String name, BigDecimal rent, int availableCount, boolean hasAc, boolean hasAttachedBath) {
        this.propertyId = propertyId;
        this.name = name;
        this.rent = rent;
        this.availableCount = availableCount;
        this.hasAc = hasAc;
        this.hasAttachedBath = hasAttachedBath;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPropertyId() { return propertyId; }
    public void setPropertyId(int propertyId) { this.propertyId = propertyId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getRent() { return rent; }
    public void setRent(BigDecimal rent) { this.rent = rent; }

    public int getAvailableCount() { return availableCount; }
    public void setAvailableCount(int availableCount) { this.availableCount = availableCount; }

    public boolean isHasAc() { return hasAc; }
    public void setHasAc(boolean hasAc) { this.hasAc = hasAc; }

    public boolean isHasAttachedBath() { return hasAttachedBath; }
    public void setHasAttachedBath(boolean hasAttachedBath) { this.hasAttachedBath = hasAttachedBath; }
}

