package com.findmystay.dao;

import com.findmystay.model.Enquiry;
import com.findmystay.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for customer inquiries sent to property owners.
 */
public class EnquiryDAO {

    public boolean createEnquiry(Enquiry enquiry) {
        String sql = "INSERT INTO enquiries (property_id, user_id, sender_name, sender_email, sender_phone, message, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, enquiry.getPropertyId());
            if (enquiry.getUserId() != null) ps.setInt(2, enquiry.getUserId());
            else ps.setNull(2, Types.INTEGER);
            ps.setString(3, enquiry.getSenderName());
            ps.setString(4, enquiry.getSenderEmail());
            ps.setString(5, enquiry.getSenderPhone());
            ps.setString(6, enquiry.getMessage());
            ps.setString(7, enquiry.getStatus().name());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        enquiry.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Enquiry> findByOwnerId(int ownerId) {
        List<Enquiry> list = new ArrayList<>();
        String sql = "SELECT e.*, p.title as property_title " +
                     "FROM enquiries e " +
                     "JOIN properties p ON e.property_id = p.id " +
                     "WHERE p.owner_id = ? " +
                     "ORDER BY e.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToEnquiry(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateStatus(int enquiryId, Enquiry.EnquiryStatus status) {
        String sql = "UPDATE enquiries SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setInt(2, enquiryId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Enquiry mapRowToEnquiry(ResultSet rs) throws SQLException {
        Enquiry e = new Enquiry();
        e.setId(rs.getInt("id"));
        e.setPropertyId(rs.getInt("property_id"));
        e.setPropertyTitle(rs.getString("property_title"));
        int uid = rs.getInt("user_id");
        if (!rs.wasNull()) e.setUserId(uid);
        e.setSenderName(rs.getString("sender_name"));
        e.setSenderEmail(rs.getString("sender_email"));
        e.setSenderPhone(rs.getString("sender_phone"));
        e.setMessage(rs.getString("message"));
        e.setStatus(Enquiry.EnquiryStatus.valueOf(rs.getString("status")));
        e.setCreatedAt(rs.getTimestamp("created_at"));
        return e;
    }
}

