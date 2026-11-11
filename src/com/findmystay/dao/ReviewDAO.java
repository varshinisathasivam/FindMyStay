package com.findmystay.dao;

import com.findmystay.model.Review;
import com.findmystay.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Reviews and Ratings.
 */
public class ReviewDAO {

    public boolean addReview(Review review) {
        String sql = "INSERT INTO reviews (property_id, user_id, rating, comment) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, review.getPropertyId());
            ps.setInt(2, review.getUserId());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getComment());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        review.setId(rs.getInt(1));
                    }
                }
                updatePropertyAverageRating(review.getPropertyId());
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Review> findByPropertyId(int propertyId) {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT r.*, u.full_name as user_name FROM reviews r " +
                     "JOIN users u ON r.user_id = u.id " +
                     "WHERE r.property_id = ? ORDER BY r.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, propertyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToReview(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Review> findAllReviews() {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT r.*, u.full_name as user_name, p.title as property_title " +
                     "FROM reviews r " +
                     "JOIN users u ON r.user_id = u.id " +
                     "JOIN properties p ON r.property_id = p.id " +
                     "ORDER BY r.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Review r = mapRowToReview(rs);
                r.setPropertyTitle(rs.getString("property_title"));
                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean deleteReview(int reviewId) {
        int propertyId = 0;
        String findSql = "SELECT property_id FROM reviews WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(findSql)) {
            ps.setInt(1, reviewId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) propertyId = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sql = "DELETE FROM reviews WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reviewId);
            boolean deleted = ps.executeUpdate() > 0;
            if (deleted && propertyId > 0) {
                updatePropertyAverageRating(propertyId);
            }
            return deleted;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void updatePropertyAverageRating(int propertyId) {
        String avgSql = "SELECT AVG(rating) FROM reviews WHERE property_id = ?";
        String updateSql = "UPDATE properties SET rating = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement psAvg = conn.prepareStatement(avgSql)) {

            psAvg.setInt(1, propertyId);
            double avg = 4.5;
            try (ResultSet rs = psAvg.executeQuery()) {
                if (rs.next() && rs.getObject(1) != null) {
                    avg = Math.round(rs.getDouble(1) * 10.0) / 10.0;
                }
            }

            try (PreparedStatement psUp = conn.prepareStatement(updateSql)) {
                psUp.setDouble(1, avg);
                psUp.setInt(2, propertyId);
                psUp.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Review mapRowToReview(ResultSet rs) throws SQLException {
        Review r = new Review();
        r.setId(rs.getInt("id"));
        r.setPropertyId(rs.getInt("property_id"));
        r.setUserId(rs.getInt("user_id"));
        r.setUserName(rs.getString("user_name"));
        r.setRating(rs.getInt("rating"));
        r.setComment(rs.getString("comment"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        return r;
    }
}

