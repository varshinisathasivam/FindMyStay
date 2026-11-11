package com.findmystay.dao;

import com.findmystay.model.Property;
import com.findmystay.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for tracking and retrieving recently viewed properties.
 */
public class RecentlyViewedDAO {

    public void recordView(int userId, int propertyId) {
        String sql = "INSERT INTO recently_viewed (user_id, property_id, viewed_at) " +
                     "VALUES (?, ?, CURRENT_TIMESTAMP) " +
                     "ON DUPLICATE KEY UPDATE viewed_at = CURRENT_TIMESTAMP";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, propertyId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Property> getRecentlyViewed(int userId, int limit) {
        List<Property> list = new ArrayList<>();
        PropertyDAO propDAO = new PropertyDAO();

        String sql = "SELECT property_id FROM recently_viewed WHERE user_id = ? ORDER BY viewed_at DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Property p = propDAO.findById(rs.getInt("property_id"));
                    if (p != null && p.isActive()) {
                        list.add(p);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}

