package com.findmystay.model;

import java.io.Serializable;

/**
 * Represents an amenity offered by a stay (e.g. Wi-Fi, Food, Power Backup).
 */
public class Amenity implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String iconClass;

    public Amenity() {}

    public Amenity(int id, String name, String iconClass) {
        this.id = id;
        this.name = name;
        this.iconClass = iconClass;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIconClass() { return iconClass; }
    public void setIconClass(String iconClass) { this.iconClass = iconClass; }
}

