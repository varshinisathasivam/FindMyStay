package com.findmystay.controller;

import com.findmystay.model.Amenity;
import com.findmystay.model.Property;
import com.findmystay.model.RoomType;
import com.findmystay.model.User;
import com.findmystay.service.PropertyService;
import com.findmystay.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for owner property creation.
 */
@WebServlet(name = "AddPropertyServlet", urlPatterns = {"/owner/property/add"})
public class AddPropertyServlet extends HttpServlet {

    private final PropertyService propertyService = new PropertyService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Amenity> allAmenities = propertyService.getAllAmenities();
        req.setAttribute("allAmenities", allAmenities);
        req.getRequestDispatcher("/WEB-INF/views/owner/add-property.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentOwner = (User) session.getAttribute("currentUser");

        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String typeStr = req.getParameter("propertyType");
        String genderStr = req.getParameter("genderPreference");
        String address = req.getParameter("address");
        String city = req.getParameter("city");
        String rentStr = req.getParameter("monthlyRent");
        String depositStr = req.getParameter("securityDeposit");
        String roomSharing = req.getParameter("roomSharing");
        String foodAvailability = req.getParameter("foodAvailability");
        String houseRules = req.getParameter("houseRules");
        String safetyInfo = req.getParameter("safetyInfo");
        String nearbyPlaces = req.getParameter("nearbyPlaces");
        String totalRoomsStr = req.getParameter("totalRooms");
        String availableRoomsStr = req.getParameter("availableRooms");
        String imageUrl = req.getParameter("featuredImage");

        if (!ValidationUtil.isNotEmpty(title) || !ValidationUtil.isNotEmpty(address) || !ValidationUtil.isNotEmpty(city)) {
            req.setAttribute("errorMessage", "Title, address, and city are required.");
            doGet(req, resp);
            return;
        }

        BigDecimal rent = BigDecimal.ZERO;
        BigDecimal deposit = BigDecimal.ZERO;
        try {
            rent = new BigDecimal(rentStr.trim());
            deposit = new BigDecimal(depositStr != null && !depositStr.isEmpty() ? depositStr.trim() : "0");
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Please provide a valid monthly rent amount.");
            doGet(req, resp);
            return;
        }

        int totalRooms = 10;
        int availableRooms = 3;
        try {
            if (totalRoomsStr != null) totalRooms = Integer.parseInt(totalRoomsStr);
            if (availableRoomsStr != null) availableRooms = Integer.parseInt(availableRoomsStr);
        } catch (Exception ignored) {}

        Property p = new Property();
        p.setOwnerId(currentOwner.getId());
        p.setTitle(title.trim());
        p.setDescription(description != null ? description.trim() : "");
        p.setPropertyType(Property.PropertyType.valueOf(typeStr != null ? typeStr : "PG"));
        p.setGenderPreference(Property.GenderPreference.valueOf(genderStr != null ? genderStr : "CO_LIVING"));
        p.setAddress(address.trim());
        p.setCity(city.trim());
        p.setMonthlyRent(rent);
        p.setSecurityDeposit(deposit);
        p.setRoomSharing(roomSharing != null ? roomSharing : "Single, Double");
        p.setFoodAvailability(foodAvailability != null ? foodAvailability : "3 Meals Included");
        p.setHouseRules(houseRules);
        p.setSafetyInfo(safetyInfo);
        p.setNearbyPlaces(nearbyPlaces);
        p.setTotalRooms(totalRooms);
        p.setAvailableRooms(availableRooms);
        p.setVerified(false); // Requires admin approval
        p.setActive(true);

        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            p.setFeaturedImage(imageUrl.trim());
        } else {
            p.setFeaturedImage("assets/images/stays/chennai-pg1.svg");
        }

        // Amenities
        String[] selectedAmenityIds = req.getParameterValues("amenityIds");
        if (selectedAmenityIds != null) {
            List<Amenity> amList = new ArrayList<>();
            for (String aId : selectedAmenityIds) {
                try {
                    amList.add(new Amenity(Integer.parseInt(aId), "", ""));
                } catch (Exception ignored) {}
            }
            p.setAmenities(amList);
        }

        // Default Room Type based on property
        List<RoomType> rts = new ArrayList<>();
        rts.add(new RoomType(0, "Standard Room", rent, availableRooms, p.isAcAvailable(), true));
        p.setRoomTypes(rts);

        boolean created = propertyService.addProperty(p);

        if (created) {
            resp.sendRedirect(req.getContextPath() + "/owner/dashboard?success=Property submitted successfully! It is pending admin verification.");
        } else {
            req.setAttribute("errorMessage", "Error saving property. Please check input values.");
            doGet(req, resp);
        }
    }
}

