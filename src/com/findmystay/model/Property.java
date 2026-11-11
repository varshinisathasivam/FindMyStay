package com.findmystay.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a PG, Hostel, or Room property listing.
 */
public class Property implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum PropertyType {
        PG, HOSTEL, ROOM, FLAT
    }

    public enum GenderPreference {
        BOYS, GIRLS, CO_LIVING
    }

    private int id;
    private int ownerId;
    private String ownerName;
    private String ownerPhone;
    private String ownerEmail;

    private String title;
    private String description;
    private PropertyType propertyType;
    private GenderPreference genderPreference;
    private String address;
    private String city;
    private BigDecimal monthlyRent;
    private BigDecimal securityDeposit;
    private String roomSharing;
    private String foodAvailability;
    private String houseRules;
    private String safetyInfo;
    private String nearbyPlaces;
    private boolean isVerified;
    private boolean isActive;
    private int totalRooms;
    private int availableRooms;
    private double rating;
    private String featuredImage;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Associated collections
    private List<RoomType> roomTypes = new ArrayList<>();
    private List<Amenity> amenities = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();

    public Property() {
        this.isVerified = false;
        this.isActive = true;
        this.rating = 4.5;
        this.featuredImage = "assets/images/default-stay.svg";
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getOwnerPhone() { return ownerPhone; }
    public void setOwnerPhone(String ownerPhone) { this.ownerPhone = ownerPhone; }

    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public PropertyType getPropertyType() { return propertyType; }
    public void setPropertyType(PropertyType propertyType) { this.propertyType = propertyType; }

    public GenderPreference getGenderPreference() { return genderPreference; }
    public void setGenderPreference(GenderPreference genderPreference) { this.genderPreference = genderPreference; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public BigDecimal getMonthlyRent() { return monthlyRent; }
    public void setMonthlyRent(BigDecimal monthlyRent) { this.monthlyRent = monthlyRent; }

    public BigDecimal getSecurityDeposit() { return securityDeposit; }
    public void setSecurityDeposit(BigDecimal securityDeposit) { this.securityDeposit = securityDeposit; }

    public String getRoomSharing() { return roomSharing; }
    public void setRoomSharing(String roomSharing) { this.roomSharing = roomSharing; }

    public String getFoodAvailability() { return foodAvailability; }
    public void setFoodAvailability(String foodAvailability) { this.foodAvailability = foodAvailability; }

    public String getHouseRules() { return houseRules; }
    public void setHouseRules(String houseRules) { this.houseRules = houseRules; }

    public String getSafetyInfo() { return safetyInfo; }
    public void setSafetyInfo(String safetyInfo) { this.safetyInfo = safetyInfo; }

    public String getNearbyPlaces() { return nearbyPlaces; }
    public void setNearbyPlaces(String nearbyPlaces) { this.nearbyPlaces = nearbyPlaces; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public int getTotalRooms() { return totalRooms; }
    public void setTotalRooms(int totalRooms) { this.totalRooms = totalRooms; }

    public int getAvailableRooms() { return availableRooms; }
    public void setAvailableRooms(int availableRooms) { this.availableRooms = availableRooms; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getFeaturedImage() { return featuredImage; }
    public void setFeaturedImage(String featuredImage) { this.featuredImage = featuredImage; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public List<RoomType> getRoomTypes() { return roomTypes; }
    public void setRoomTypes(List<RoomType> roomTypes) { this.roomTypes = roomTypes; }

    public List<Amenity> getAmenities() { return amenities; }
    public void setAmenities(List<Amenity> amenities) { this.amenities = amenities; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    // Helpers
    public boolean hasAmenity(String name) {
        if (amenities == null || name == null) return false;
        for (Amenity a : amenities) {
            if (a.getName().toLowerCase().contains(name.toLowerCase())) return true;
        }
        return false;
    }

    public boolean isAcAvailable() {
        if (hasAmenity("AC") || hasAmenity("Air Conditioning")) return true;
        for (RoomType rt : roomTypes) {
            if (rt.isHasAc()) return true;
        }
        return false;
    }

    public boolean hasWifi() {
        return hasAmenity("Wi-Fi") || hasAmenity("Wifi") || hasAmenity("Internet");
    }

    public String getGenderBadgeClass() {
        if (genderPreference == null) return "badge-neutral";
        switch (genderPreference) {
            case BOYS: return "badge-boys";
            case GIRLS: return "badge-girls";
            case CO_LIVING: default: return "badge-coliving";
        }
    }
}

