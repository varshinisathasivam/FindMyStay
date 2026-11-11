package com.findmystay.dao;

import com.findmystay.model.Property;
import com.findmystay.model.Wishlist;
import com.findmystay.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Wishlist operations.
 */
public class WishlistDAO {

    public boolean addWishlist(int userId, int propertyId) {
        String sql = "INSERT IGNORE INTO wishlists (user_id, property_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, propertyId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeWishlist(int userId, int propertyId) {
        String sql = "DELETE FROM wishlists WHERE user_id = ? AND property_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, propertyId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isWishlisted(int userId, int propertyId) {
        String sql = "SELECT id FROM wishlists WHERE user_id = ? AND property_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, propertyId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Wishlist> findByUserId(int userId) {
        List<Wishlist> list = new ArrayList<>();
        PropertyDAO propDAO = new PropertyDAO();

        String sql = "SELECT id, user_id, property_id, created_at FROM wishlists WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Wishlist w = new Wishlist();
                    w.setId(rs.getInt("id"));
                    w.setUserId(rs.getInt("user_id"));
                    w.setPropertyId(rs.getInt("property_id"));
                    w.setCreatedAt(rs.getTimestamp("created_at"));

                    Property p = propDAO.findById(w.getPropertyId());
                    w.setProperty(p);
                    list.add(w);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countByUserId(int userId) {
        String sql = "SELECT COUNT(*) FROM wishlists WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

