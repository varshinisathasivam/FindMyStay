package com.findmystay.dao;

import com.findmystay.model.Complaint;
import com.findmystay.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for complaints submitted to administrators.
 */
public class ComplaintDAO {

    public boolean createComplaint(Complaint complaint) {
        String sql = "INSERT INTO complaints (user_id, property_id, subject, description, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, complaint.getUserId());
            if (complaint.getPropertyId() != null) ps.setInt(2, complaint.getPropertyId());
            else ps.setNull(2, Types.INTEGER);
            ps.setString(3, complaint.getSubject());
            ps.setString(4, complaint.getDescription());
            ps.setString(5, complaint.getStatus().name());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        complaint.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Complaint> findAllComplaints() {
        List<Complaint> list = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name as user_name, u.email as user_email, p.title as property_title " +
                     "FROM complaints c " +
                     "JOIN users u ON c.user_id = u.id " +
                     "LEFT JOIN properties p ON c.property_id = p.id " +
                     "ORDER BY c.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToComplaint(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Complaint> findByUserId(int userId) {
        List<Complaint> list = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name as user_name, u.email as user_email, p.title as property_title " +
                     "FROM complaints c " +
                     "JOIN users u ON c.user_id = u.id " +
                     "LEFT JOIN properties p ON c.property_id = p.id " +
                     "WHERE c.user_id = ? " +
                     "ORDER BY c.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToComplaint(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateStatus(int complaintId, Complaint.ComplaintStatus status) {
        String sql = "UPDATE complaints SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setInt(2, complaintId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Complaint mapRowToComplaint(ResultSet rs) throws SQLException {
        Complaint c = new Complaint();
        c.setId(rs.getInt("id"));
        c.setUserId(rs.getInt("user_id"));
        c.setUserName(rs.getString("user_name"));
        c.setUserEmail(rs.getString("user_email"));
        int pid = rs.getInt("property_id");
        if (!rs.wasNull()) {
            c.setPropertyId(pid);
            c.setPropertyTitle(rs.getString("property_title"));
        }
        c.setSubject(rs.getString("subject"));
        c.setDescription(rs.getString("description"));
        c.setStatus(Complaint.ComplaintStatus.valueOf(rs.getString("status")));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        return c;
    }
}

