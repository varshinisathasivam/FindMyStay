package com.findmystay.service;

import com.findmystay.dao.PropertyDAO;
import com.findmystay.model.Amenity;
import com.findmystay.model.Property;

import java.math.BigDecimal;
import java.util.List;

/**
 * Business service for property listings, search filters, and inventory.
 */
public class PropertyService {

    private final PropertyDAO propertyDAO;

    public PropertyService() {
        this.propertyDAO = new PropertyDAO();
    }

    public PropertyService(PropertyDAO propertyDAO) {
        this.propertyDAO = propertyDAO;
    }

    public boolean addProperty(Property property) {
        if (property == null || property.getMonthlyRent() == null) return false;
        return propertyDAO.createProperty(property);
    }

    public boolean updateProperty(Property property) {
        if (property == null || property.getId() <= 0) return false;
        return propertyDAO.updateProperty(property);
    }

    public boolean deleteProperty(int propertyId, int ownerId) {
        return propertyDAO.deleteProperty(propertyId, ownerId);
    }

    public Property getPropertyDetails(int id) {
        return propertyDAO.findById(id);
    }

    public List<Property> searchProperties(String city, String type, BigDecimal minRent, BigDecimal maxRent,
                                           String sharing, Boolean ac, Boolean food, Boolean wifi,
                                           Boolean bath, String gender, String sort) {
        return propertyDAO.searchProperties(city, type, minRent, maxRent, sharing, ac, food, wifi, bath, gender, sort);
    }

    public List<Property> getFeaturedProperties(int limit) {
        return propertyDAO.getFeaturedProperties(limit);
    }

    public List<Property> getBudgetSuggestions(BigDecimal targetBudget, int excludePropertyId) {
        return propertyDAO.getSmartBudgetSuggestions(targetBudget, excludePropertyId);
    }

    public List<Property> getPropertiesByOwner(int ownerId) {
        return propertyDAO.findByOwnerId(ownerId);
    }

    public List<Property> getAllPropertiesForAdmin() {
        return propertyDAO.getAllPropertiesForAdmin();
    }

    public boolean verifyProperty(int propertyId, boolean verified) {
        return propertyDAO.verifyProperty(propertyId, verified);
    }

    public boolean toggleActive(int propertyId, boolean active) {
        return propertyDAO.toggleActiveStatus(propertyId, active);
    }

    public boolean updateLiveAvailability(int propertyId, int count) {
        return propertyDAO.updateLiveAvailability(propertyId, count);
    }

    public boolean updateLiveAvailability(int propertyId, int count, int ownerId) {
        return propertyDAO.updateLiveAvailability(propertyId, count, ownerId);
    }

    public List<String> getPopularCities() {
        return propertyDAO.getPopularCities();
    }

    public List<Amenity> getAllAmenities() {
        return propertyDAO.getAllAmenities();
    }

    public int getTotalPropertiesCount() {
        return propertyDAO.countTotal();
    }

    public int getPendingVerificationCount() {
        return propertyDAO.countPendingVerification();
    }
}

