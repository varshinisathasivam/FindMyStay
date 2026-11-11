package com.findmystay.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Represents a registered system user (Customer, Owner, or Admin).
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Role {
        CUSTOMER, OWNER, ADMIN
    }

    private int id;
    private String fullName;
    private String email;
    private String phone;
    private String passwordHash;
    private Role role;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public User() {}

    public User(String fullName, String email, String phone, String passwordHash, Role role) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public boolean isCustomer() { return Role.CUSTOMER.equals(this.role); }
    public boolean isOwner() { return Role.OWNER.equals(this.role); }
    public boolean isAdmin() { return Role.ADMIN.equals(this.role); }

    @Override
    public String toString() {
        return "User{id=" + id + ", fullName='" + fullName + "', email='" + email + "', role=" + role + "}";
    }
}

