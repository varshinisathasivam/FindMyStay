package com.findmystay.test;

import com.findmystay.util.PasswordUtil;
import com.findmystay.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class HashUpdater {
    public static void main(String[] args) throws Exception {
        String customerHash = PasswordUtil.hashPassword("Customer@123");
        String ownerHash = PasswordUtil.hashPassword("Owner@123");
        String adminHash = PasswordUtil.hashPassword("Admin@123");

        System.out.println("CUSTOMER_HASH=" + customerHash);
        System.out.println("OWNER_HASH=" + ownerHash);
        System.out.println("ADMIN_HASH=" + adminHash);

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("UPDATE users SET password_hash = ? WHERE email = ?")) {
                ps.setString(1, customerHash);
                ps.setString(2, "customer@findmystay.com");
                ps.executeUpdate();

                ps.setString(1, customerHash);
                ps.setString(2, "priya@gmail.com");
                ps.executeUpdate();

                ps.setString(1, ownerHash);
                ps.setString(2, "owner@findmystay.com");
                ps.executeUpdate();

                ps.setString(1, ownerHash);
                ps.setString(2, "rajesh.owner@gmail.com");
                ps.executeUpdate();

                ps.setString(1, adminHash);
                ps.setString(2, "admin@findmystay.com");
                ps.executeUpdate();
            }
            System.out.println("DATABASE PASSWORDS UPDATED SUCCESSFULLY!");
        }
    }
}

