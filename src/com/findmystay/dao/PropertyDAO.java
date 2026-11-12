package com.findmystay.dao;

import com.findmystay.model.Amenity;
import com.findmystay.model.Property;
import com.findmystay.model.RoomType;
import com.findmystay.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Properties, Room Types, and Amenities.
 */
public class PropertyDAO {

    public boolean createProperty(Property property) {
        String sql = "INSERT INTO properties (owner_id, title, description, property_type, gender_preference, " +
                     "address, city, monthly_rent, security_deposit, room_sharing, food_availability, " +
                     "house_rules, safety_info, nearby_places, is_verified, is_active, total_rooms, " +
                     "available_rooms, rating, featured_image) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, property.getOwnerId());
            ps.setString(2, property.getTitle());
            ps.setString(3, property.getDescription());
            ps.setString(4, property.getPropertyType().name());
            ps.setString(5, property.getGenderPreference().name());
            ps.setString(6, property.getAddress());
            ps.setString(7, property.getCity());
            ps.setBigDecimal(8, property.getMonthlyRent());
            ps.setBigDecimal(9, property.getSecurityDeposit());
            ps.setString(10, property.getRoomSharing());
            ps.setString(11, property.getFoodAvailability());
            ps.setString(12, property.getHouseRules());
            ps.setString(13, property.getSafetyInfo());
            ps.setString(14, property.getNearbyPlaces());
            ps.setBoolean(15, property.isVerified());
            ps.setBoolean(16, property.isActive());
            ps.setInt(17, property.getTotalRooms());
            ps.setInt(18, property.getAvailableRooms());
            ps.setDouble(19, property.getRating());
            ps.setString(20, property.getFeaturedImage() != null ? property.getFeaturedImage() : "assets/images/default-stay.svg");

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        property.setId(rs.getInt(1));
                    }
                }
                // Save amenities if selected
                if (property.getAmenities() != null && !property.getAmenities().isEmpty()) {
                    savePropertyAmenities(conn, property.getId(), property.getAmenities());
                }
                // Save room types if specified
                if (property.getRoomTypes() != null && !property.getRoomTypes().isEmpty()) {
                    savePropertyRoomTypes(conn, property.getId(), property.getRoomTypes());
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateProperty(Property property) {
        String sql = "UPDATE properties SET title = ?, description = ?, property_type = ?, gender_preference = ?, " +
                     "address = ?, city = ?, monthly_rent = ?, security_deposit = ?, room_sharing = ?, " +
                     "food_availability = ?, house_rules = ?, safety_info = ?, nearby_places = ?, " +
                     "total_rooms = ?, available_rooms = ?, featured_image = ? " +
                     "WHERE id = ? AND owner_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, property.getTitle());
            ps.setString(2, property.getDescription());
            ps.setString(3, property.getPropertyType().name());
            ps.setString(4, property.getGenderPreference().name());
            ps.setString(5, property.getAddress());
            ps.setString(6, property.getCity());
            ps.setBigDecimal(7, property.getMonthlyRent());
            ps.setBigDecimal(8, property.getSecurityDeposit());
            ps.setString(9, property.getRoomSharing());
            ps.setString(10, property.getFoodAvailability());
            ps.setString(11, property.getHouseRules());
            ps.setString(12, property.getSafetyInfo());
            ps.setString(13, property.getNearbyPlaces());
            ps.setInt(14, property.getTotalRooms());
            ps.setInt(15, property.getAvailableRooms());
            ps.setString(16, property.getFeaturedImage());
            ps.setInt(17, property.getId());
            ps.setInt(18, property.getOwnerId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteProperty(int propertyId, int ownerId) {
        String sql = "DELETE FROM properties WHERE id = ? AND owner_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, propertyId);
            ps.setInt(2, ownerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Property findById(int id) {
        String sql = "SELECT p.*, u.full_name as owner_name, u.phone as owner_phone, u.email as owner_email " +
                     "FROM properties p " +
                     "JOIN users u ON p.owner_id = u.id " +
                     "WHERE p.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Property p = mapRowToProperty(rs);
                    loadPropertyRelations(conn, p);
                    return p;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Property> searchProperties(String city, String type, BigDecimal minRent, BigDecimal maxRent,
                                           String sharing, Boolean ac, Boolean food, Boolean wifi,
                                           Boolean bath, String gender, String sort) {
        List<Property> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT DISTINCT p.*, u.full_name as owner_name, u.phone as owner_phone, u.email as owner_email " +
            "FROM properties p " +
            "JOIN users u ON p.owner_id = u.id " +
            "LEFT JOIN property_amenities pa ON p.id = pa.property_id " +
            "LEFT JOIN amenities a ON pa.amenity_id = a.id " +
            "WHERE p.is_active = TRUE "
        );

        List<Object> params = new ArrayList<>();

        if (city != null && !city.trim().isEmpty() && !city.equalsIgnoreCase("all")) {
            sql.append(" AND LOWER(p.city) LIKE LOWER(?) ");
            params.add("%" + city.trim() + "%");
        }

        if (type != null && !type.trim().isEmpty() && !type.equalsIgnoreCase("all")) {
            sql.append(" AND p.property_type = ? ");
            params.add(type.toUpperCase());
        }

        if (gender != null && !gender.trim().isEmpty() && !gender.equalsIgnoreCase("all")) {
            sql.append(" AND p.gender_preference = ? ");
            params.add(gender.toUpperCase());
        }

        if (minRent != null && minRent.compareTo(BigDecimal.ZERO) > 0) {
            sql.append(" AND p.monthly_rent >= ? ");
            params.add(minRent);
        }

        if (maxRent != null && maxRent.compareTo(BigDecimal.ZERO) > 0) {
            sql.append(" AND p.monthly_rent <= ? ");
            params.add(maxRent);
        }

        if (sharing != null && !sharing.trim().isEmpty() && !sharing.equalsIgnoreCase("all")) {
            sql.append(" AND LOWER(p.room_sharing) LIKE LOWER(?) ");
            params.add("%" + sharing.trim() + "%");
        }

        if (food != null && food) {
            sql.append(" AND LOWER(p.food_availability) NOT LIKE '%not%' AND LOWER(p.food_availability) NOT LIKE '%none%' ");
        }

        if (sort != null) {
            switch (sort) {
                case "price_asc":
                    sql.append(" ORDER BY p.monthly_rent ASC ");
                    break;
                case "price_desc":
                    sql.append(" ORDER BY p.monthly_rent DESC ");
                    break;
                case "rating_desc":
                    sql.append(" ORDER BY p.rating DESC, p.monthly_rent ASC ");
                    break;
                default:
                    sql.append(" ORDER BY p.is_verified DESC, p.created_at DESC ");
                    break;
            }
        } else {
            sql.append(" ORDER BY p.is_verified DESC, p.created_at DESC ");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Property p = mapRowToProperty(rs);
                    loadPropertyRelations(conn, p);

                    // Memory filter for amenities if requested
                    if (ac != null && ac && !p.isAcAvailable()) continue;
                    if (wifi != null && wifi && !p.hasWifi()) continue;
                    if (bath != null && bath && !p.hasAmenity("Bathroom")) continue;

                    list.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Property> getFeaturedProperties(int limit) {
        List<Property> list = new ArrayList<>();
        String sql = "SELECT p.*, u.full_name as owner_name, u.phone as owner_phone, u.email as owner_email " +
                     "FROM properties p " +
                     "JOIN users u ON p.owner_id = u.id " +
                     "WHERE p.is_active = TRUE AND p.is_verified = TRUE " +
                     "ORDER BY p.rating DESC, p.id ASC LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Property p = mapRowToProperty(rs);
                    loadPropertyRelations(conn, p);
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Property> getSmartBudgetSuggestions(BigDecimal targetBudget, int excludePropertyId) {
        List<Property> list = new ArrayList<>();
        if (targetBudget == null || targetBudget.compareTo(BigDecimal.ZERO) <= 0) return list;

        BigDecimal min = targetBudget.multiply(new BigDecimal("0.80"));
        BigDecimal max = targetBudget.multiply(new BigDecimal("1.20"));

        String sql = "SELECT p.*, u.full_name as owner_name, u.phone as owner_phone, u.email as owner_email " +
                     "FROM properties p " +
                     "JOIN users u ON p.owner_id = u.id " +
                     "WHERE p.is_active = TRUE AND p.id != ? AND p.monthly_rent BETWEEN ? AND ? " +
                     "ORDER BY p.rating DESC LIMIT 3";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, excludePropertyId);
            ps.setBigDecimal(2, min);
            ps.setBigDecimal(3, max);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Property p = mapRowToProperty(rs);
                    loadPropertyRelations(conn, p);
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Property> findByOwnerId(int ownerId) {
        List<Property> list = new ArrayList<>();
        String sql = "SELECT p.*, u.full_name as owner_name, u.phone as owner_phone, u.email as owner_email " +
                     "FROM properties p " +
                     "JOIN users u ON p.owner_id = u.id " +
                     "WHERE p.owner_id = ? " +
                     "ORDER BY p.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Property p = mapRowToProperty(rs);
                    loadPropertyRelations(conn, p);
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Property> getAllPropertiesForAdmin() {
        List<Property> list = new ArrayList<>();
        String sql = "SELECT p.*, u.full_name as owner_name, u.phone as owner_phone, u.email as owner_email " +
                     "FROM properties p " +
                     "JOIN users u ON p.owner_id = u.id " +
                     "ORDER BY p.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Property p = mapRowToProperty(rs);
                loadPropertyRelations(conn, p);
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean verifyProperty(int propertyId, boolean verified) {
        String sql = "UPDATE properties SET is_verified = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, verified);
            ps.setInt(2, propertyId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean toggleActiveStatus(int propertyId, boolean active) {
        String sql = "UPDATE properties SET is_active = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, active);
            ps.setInt(2, propertyId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateLiveAvailability(int propertyId, int availableRooms) {
        String sql = "UPDATE properties SET available_rooms = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Math.max(0, availableRooms));
            ps.setInt(2, propertyId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateLiveAvailability(int propertyId, int availableRooms, int ownerId) {
        String sql = "UPDATE properties SET available_rooms = ? WHERE id = ? AND owner_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Math.max(0, availableRooms));
            ps.setInt(2, propertyId);
            ps.setInt(3, ownerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int countTotal() {
        String sql = "SELECT COUNT(*) FROM properties";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countPendingVerification() {
        String sql = "SELECT COUNT(*) FROM properties WHERE is_verified = FALSE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<String> getPopularCities() {
        List<String> cities = new ArrayList<>();
        String sql = "SELECT DISTINCT city, COUNT(*) as cnt FROM properties WHERE is_active = TRUE GROUP BY city ORDER BY cnt DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cities.add(rs.getString("city"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cities;
    }

    public List<Amenity> getAllAmenities() {
        List<Amenity> list = new ArrayList<>();
        String sql = "SELECT * FROM amenities ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Amenity(rs.getInt("id"), rs.getString("name"), rs.getString("icon_class")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private void savePropertyAmenities(Connection conn, int propertyId, List<Amenity> amenities) throws SQLException {
        String delSql = "DELETE FROM property_amenities WHERE property_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(delSql)) {
            ps.setInt(1, propertyId);
            ps.executeUpdate();
        }

        String insSql = "INSERT INTO property_amenities (property_id, amenity_id) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insSql)) {
            for (Amenity a : amenities) {
                ps.setInt(1, propertyId);
                ps.setInt(2, a.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void savePropertyRoomTypes(Connection conn, int propertyId, List<RoomType> roomTypes) throws SQLException {
        String delSql = "DELETE FROM room_types WHERE property_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(delSql)) {
            ps.setInt(1, propertyId);
            ps.executeUpdate();
        }

        String insSql = "INSERT INTO room_types (property_id, name, rent, available_count, has_ac, has_attached_bath) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insSql)) {
            for (RoomType rt : roomTypes) {
                ps.setInt(1, propertyId);
                ps.setString(2, rt.getName());
                ps.setBigDecimal(3, rt.getRent());
                ps.setInt(4, rt.getAvailableCount());
                ps.setBoolean(5, rt.isHasAc());
                ps.setBoolean(6, rt.isHasAttachedBath());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void loadPropertyRelations(Connection conn, Property p) {
        // Load Amenities
        String aSql = "SELECT a.* FROM amenities a " +
                      "JOIN property_amenities pa ON a.id = pa.amenity_id " +
                      "WHERE pa.property_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(aSql)) {
            ps.setInt(1, p.getId());
            try (ResultSet rs = ps.executeQuery()) {
                List<Amenity> ams = new ArrayList<>();
                while (rs.next()) {
                    ams.add(new Amenity(rs.getInt("id"), rs.getString("name"), rs.getString("icon_class")));
                }
                p.setAmenities(ams);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Load Room Types
        String rtSql = "SELECT * FROM room_types WHERE property_id = ? ORDER BY rent ASC";
        try (PreparedStatement ps = conn.prepareStatement(rtSql)) {
            ps.setInt(1, p.getId());
            try (ResultSet rs = ps.executeQuery()) {
                List<RoomType> rts = new ArrayList<>();
                while (rs.next()) {
                    RoomType rt = new RoomType();
                    rt.setId(rs.getInt("id"));
                    rt.setPropertyId(p.getId());
                    rt.setName(rs.getString("name"));
                    rt.setRent(rs.getBigDecimal("rent"));
                    rt.setAvailableCount(rs.getInt("available_count"));
                    rt.setHasAc(rs.getBoolean("has_ac"));
                    rt.setHasAttachedBath(rs.getBoolean("has_attached_bath"));
                    rts.add(rt);
                }
                p.setRoomTypes(rts);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Load Images
        String imgSql = "SELECT image_url FROM property_images WHERE property_id = ? ORDER BY is_primary DESC, id ASC";
        try (PreparedStatement ps = conn.prepareStatement(imgSql)) {
            ps.setInt(1, p.getId());
            try (ResultSet rs = ps.executeQuery()) {
                List<String> imgs = new ArrayList<>();
                while (rs.next()) {
                    imgs.add(rs.getString("image_url"));
                }
                if (imgs.isEmpty() && p.getFeaturedImage() != null) {
                    imgs.add(p.getFeaturedImage());
                }
                p.setImageUrls(imgs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Property mapRowToProperty(ResultSet rs) throws SQLException {
        Property p = new Property();
        p.setId(rs.getInt("id"));
        p.setOwnerId(rs.getInt("owner_id"));
        p.setOwnerName(rs.getString("owner_name"));
        p.setOwnerPhone(rs.getString("owner_phone"));
        p.setOwnerEmail(rs.getString("owner_email"));
        p.setTitle(rs.getString("title"));
        p.setDescription(rs.getString("description"));
        p.setPropertyType(Property.PropertyType.valueOf(rs.getString("property_type")));
        p.setGenderPreference(Property.GenderPreference.valueOf(rs.getString("gender_preference")));
        p.setAddress(rs.getString("address"));
        p.setCity(rs.getString("city"));
        p.setMonthlyRent(rs.getBigDecimal("monthly_rent"));
        p.setSecurityDeposit(rs.getBigDecimal("security_deposit"));
        p.setRoomSharing(rs.getString("room_sharing"));
        p.setFoodAvailability(rs.getString("food_availability"));
        p.setHouseRules(rs.getString("house_rules"));
        p.setSafetyInfo(rs.getString("safety_info"));
        p.setNearbyPlaces(rs.getString("nearby_places"));
        p.setVerified(rs.getBoolean("is_verified"));
        p.setActive(rs.getBoolean("is_active"));
        p.setTotalRooms(rs.getInt("total_rooms"));
        p.setAvailableRooms(rs.getInt("available_rooms"));
        p.setRating(rs.getDouble("rating"));
        p.setFeaturedImage(rs.getString("featured_image"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setUpdatedAt(rs.getTimestamp("updated_at"));
        return p;
    }
}

