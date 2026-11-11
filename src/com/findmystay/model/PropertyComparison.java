package com.findmystay.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Model helper for side-by-side comparison of up to 3 properties.
 */
public class PropertyComparison implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Property> properties = new ArrayList<>();

    public PropertyComparison() {}

    public PropertyComparison(List<Property> properties) {
        this.properties = properties;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public void addProperty(Property p) {
        if (properties.size() < 3 && !contains(p.getId())) {
            properties.add(p);
        }
    }

    public void removeProperty(int propertyId) {
        properties.removeIf(p -> p.getId() == propertyId);
    }

    public boolean contains(int propertyId) {
        for (Property p : properties) {
            if (p.getId() == propertyId) return true;
        }
        return false;
    }

    public int getCount() {
        return properties.size();
    }
}

