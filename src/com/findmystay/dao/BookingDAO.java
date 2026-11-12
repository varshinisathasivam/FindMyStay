package com.findmystay.dao;

import com.findmystay.model.Booking;
import com.findmystay.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Booking operations.
 */
public class BookingDAO {

    public boolean createBooking(Booking booking) {
        String sql = "INSERT INTO bookings (booking_ref, customer_id, property_id, room_type, move_in_date, " +
                     "duration_months, contact_number, message, status, total_amount) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, booking.getBookingRef());
            ps.setInt(2, booking.getCustomerId());
            ps.setInt(3, booking.getPropertyId());
            ps.setString(4, booking.getRoomType());
            ps.setDate(5, booking.getMoveInDate());
            ps.setInt(6, booking.getDurationMonths());
            ps.setString(7, booking.getContactNumber());
            ps.setString(8, booking.getMessage());
            ps.setString(9, booking.getStatus().name());
            ps.setBigDecimal(10, booking.getTotalAmount());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        booking.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Booking findById(int id) {
        String sql = "SELECT b.*, u.full_name as customer_name, u.email as customer_email, u.phone as customer_phone, " +
                     "p.title as property_title, p.city as property_city, p.address as property_address, p.owner_id " +
                     "FROM bookings b " +
                     "JOIN users u ON b.customer_id = u.id " +
                     "JOIN properties p ON b.property_id = p.id " +
                     "WHERE b.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToBooking(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Booking> findByCustomerId(int customerId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, u.full_name as customer_name, u.email as customer_email, u.phone as customer_phone, " +
                     "p.title as property_title, p.city as property_city, p.address as property_address, p.owner_id " +
                     "FROM bookings b " +
                     "JOIN users u ON b.customer_id = u.id " +
                     "JOIN properties p ON b.property_id = p.id " +
                     "WHERE b.customer_id = ? " +
                     "ORDER BY b.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToBooking(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Booking> findByOwnerId(int ownerId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, u.full_name as customer_name, u.email as customer_email, u.phone as customer_phone, " +
                     "p.title as property_title, p.city as property_city, p.address as property_address, p.owner_id " +
                     "FROM bookings b " +
                     "JOIN users u ON b.customer_id = u.id " +
                     "JOIN properties p ON b.property_id = p.id " +
                     "WHERE p.owner_id = ? " +
                     "ORDER BY b.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToBooking(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Booking> findAllBookings() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, u.full_name as customer_name, u.email as customer_email, u.phone as customer_phone, " +
                     "p.title as property_title, p.city as property_city, p.address as property_address, p.owner_id " +
                     "FROM bookings b " +
                     "JOIN users u ON b.customer_id = u.id " +
                     "JOIN properties p ON b.property_id = p.id " +
                     "ORDER BY b.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToBooking(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateStatus(int bookingId, Booking.BookingStatus status) {
        return updateStatusWithTransaction(bookingId, status, 0, true);
    }

    /**
     * Atomically updates booking status with row-level locking (FOR UPDATE) and proper
     * room availability decrements/restorations within a single ACID transaction.
     * Prevents duplicate approvals, race conditions, and double decrements.
     */
    public boolean updateStatusWithTransaction(int bookingId, Booking.BookingStatus targetStatus, int ownerOrAdminId, boolean isAdmin) {
        String selectSql = "SELECT b.id, b.status, b.property_id, p.owner_id, p.available_rooms, p.total_rooms " +
                           "FROM bookings b JOIN properties p ON b.property_id = p.id " +
                           "WHERE b.id = ? FOR UPDATE";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psSelect = conn.prepareStatement(selectSql)) {
                psSelect.setInt(1, bookingId);
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }

                    Booking.BookingStatus currentStatus = Booking.BookingStatus.valueOf(rs.getString("status"));
                    int propertyId = rs.getInt("property_id");
                    int propertyOwnerId = rs.getInt("owner_id");
                    int availableRooms = rs.getInt("available_rooms");

                    // Check ownership authorization if not admin
                    if (!isAdmin && propertyOwnerId != ownerOrAdminId) {
                        conn.rollback();
                        return false;
                    }

                    // Prevent duplicate approvals or idempotent no-op updates
                    if (currentStatus == targetStatus) {
                        conn.rollback();
                        return false;
                    }

                    // Transition validation:
                    if (targetStatus == Booking.BookingStatus.APPROVED) {
                        if (currentStatus != Booking.BookingStatus.PENDING) {
                            conn.rollback();
                            return false;
                        }
                        if (availableRooms <= 0) {
                            conn.rollback();
                            return false; // No rooms left!
                        }

                        // Decrement room availability atomically
                        String decrSql = "UPDATE properties SET available_rooms = available_rooms - 1 WHERE id = ? AND available_rooms > 0";
                        try (PreparedStatement psDecr = conn.prepareStatement(decrSql)) {
                            psDecr.setInt(1, propertyId);
                            int decrRows = psDecr.executeUpdate();
                            if (decrRows <= 0) {
                                conn.rollback();
                                return false;
                            }
                        }
                    } else if ((targetStatus == Booking.BookingStatus.CANCELLED || targetStatus == Booking.BookingStatus.REJECTED)
                               && currentStatus == Booking.BookingStatus.APPROVED) {
                        // Restore previously decremented room availability
                        String incrSql = "UPDATE properties SET available_rooms = LEAST(total_rooms, available_rooms + 1) WHERE id = ?";
                        try (PreparedStatement psIncr = conn.prepareStatement(incrSql)) {
                            psIncr.setInt(1, propertyId);
                            psIncr.executeUpdate();
                        }
                    } else if (currentStatus != Booking.BookingStatus.PENDING) {
                        // If current status is already CANCELLED or REJECTED, cannot change
                        conn.rollback();
                        return false;
                    }

                    // Update the booking status
                    String updateSql = "UPDATE bookings SET status = ? WHERE id = ?";
                    try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                        psUpdate.setString(1, targetStatus.name());
                        psUpdate.setInt(2, bookingId);
                        int affected = psUpdate.executeUpdate();
                        if (affected > 0) {
                            conn.commit();
                            return true;
                        } else {
                            conn.rollback();
                            return false;
                        }
                    }
                }
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean cancelBooking(int bookingId, int customerId) {
        String sql = "UPDATE bookings SET status = 'CANCELLED' WHERE id = ? AND customer_id = ? AND status = 'PENDING'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookingId);
            ps.setInt(2, customerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int countTotal() {
        String sql = "SELECT COUNT(*) FROM bookings";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countByCustomerAndStatus(int customerId, Booking.BookingStatus status) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE customer_id = ? " +
                     (status != null ? "AND status = ?" : "");
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            if (status != null) ps.setString(2, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Booking mapRowToBooking(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setId(rs.getInt("id"));
        b.setBookingRef(rs.getString("booking_ref"));
        b.setCustomerId(rs.getInt("customer_id"));
        b.setCustomerName(rs.getString("customer_name"));
        b.setCustomerEmail(rs.getString("customer_email"));
        b.setCustomerPhone(rs.getString("customer_phone"));
        b.setPropertyId(rs.getInt("property_id"));
        b.setPropertyTitle(rs.getString("property_title"));
        b.setPropertyCity(rs.getString("property_city"));
        b.setPropertyAddress(rs.getString("property_address"));
        b.setOwnerId(rs.getInt("owner_id"));
        b.setRoomType(rs.getString("room_type"));
        b.setMoveInDate(rs.getDate("move_in_date"));
        b.setDurationMonths(rs.getInt("duration_months"));
        b.setContactNumber(rs.getString("contact_number"));
        b.setMessage(rs.getString("message"));
        b.setStatus(Booking.BookingStatus.valueOf(rs.getString("status")));
        b.setTotalAmount(rs.getBigDecimal("total_amount"));
        b.setCreatedAt(rs.getTimestamp("created_at"));
        b.setUpdatedAt(rs.getTimestamp("updated_at"));
        return b;
    }
}

