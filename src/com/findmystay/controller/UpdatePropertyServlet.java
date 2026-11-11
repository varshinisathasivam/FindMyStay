package com.findmystay.controller;

import com.findmystay.model.Amenity;
import com.findmystay.model.Property;
import com.findmystay.model.User;
import com.findmystay.service.PropertyService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controller for editing existing property listings.
 */
@WebServlet(name = "UpdatePropertyServlet", urlPatterns = {"/owner/property/edit"})
public class UpdatePropertyServlet extends HttpServlet {

    private final PropertyService propertyService = new PropertyService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentOwner = (User) session.getAttribute("currentUser");

        String idStr = req.getParameter("id");
        if (idStr == null) {
            resp.sendRedirect(req.getContextPath() + "/owner/dashboard");
            return;
        }

        try {
            int propertyId = Integer.parseInt(idStr);
            Property p = propertyService.getPropertyDetails(propertyId);

            // Ownership check
            if (p == null || (p.getOwnerId() != currentOwner.getId() && !currentOwner.isAdmin())) {
                resp.sendRedirect(req.getContextPath() + "/owner/dashboard?error=Unauthorized property access");
                return;
            }

            List<Amenity> allAmenities = propertyService.getAllAmenities();

            req.setAttribute("property", p);
            req.setAttribute("allAmenities", allAmenities);
            req.getRequestDispatcher("/WEB-INF/views/owner/edit-property.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/owner/dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentOwner = (User) session.getAttribute("currentUser");

        String idStr = req.getParameter("id");
        if (idStr == null) {
            resp.sendRedirect(req.getContextPath() + "/owner/dashboard");
            return;
        }

        int propertyId = Integer.parseInt(idStr);
        Property existing = propertyService.getPropertyDetails(propertyId);

        if (existing == null || (existing.getOwnerId() != currentOwner.getId() && !currentOwner.isAdmin())) {
            resp.sendRedirect(req.getContextPath() + "/owner/dashboard?error=Unauthorized property modification");
            return;
        }

        existing.setTitle(req.getParameter("title"));
        existing.setDescription(req.getParameter("description"));
        existing.setPropertyType(Property.PropertyType.valueOf(req.getParameter("propertyType")));
        existing.setGenderPreference(Property.GenderPreference.valueOf(req.getParameter("genderPreference")));
        existing.setAddress(req.getParameter("address"));
        existing.setCity(req.getParameter("city"));
        existing.setMonthlyRent(new BigDecimal(req.getParameter("monthlyRent")));
        existing.setSecurityDeposit(new BigDecimal(req.getParameter("securityDeposit")));
        existing.setRoomSharing(req.getParameter("roomSharing"));
        existing.setFoodAvailability(req.getParameter("foodAvailability"));
        existing.setHouseRules(req.getParameter("houseRules"));
        existing.setSafetyInfo(req.getParameter("safetyInfo"));
        existing.setNearbyPlaces(req.getParameter("nearbyPlaces"));
        existing.setTotalRooms(Integer.parseInt(req.getParameter("totalRooms")));
        existing.setAvailableRooms(Integer.parseInt(req.getParameter("availableRooms")));

        String image = req.getParameter("featuredImage");
        if (image != null && !image.trim().isEmpty()) {
            existing.setFeaturedImage(image.trim());
        }

        boolean updated = propertyService.updateProperty(existing);

        if (updated) {
            resp.sendRedirect(req.getContextPath() + "/owner/dashboard?success=Property updated successfully!");
        } else {
            resp.sendRedirect(req.getContextPath() + "/owner/property/edit?id=" + propertyId + "&error=Failed to update property.");
        }
    }
}

