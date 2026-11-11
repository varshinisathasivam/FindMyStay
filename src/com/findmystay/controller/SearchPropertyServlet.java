package com.findmystay.controller;

import com.findmystay.model.Property;
import com.findmystay.service.PropertyService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controller for searching, filtering, and sorting accommodations.
 */
@WebServlet(name = "SearchPropertyServlet", urlPatterns = {"/search", "/stays"})
public class SearchPropertyServlet extends HttpServlet {

    private final PropertyService propertyService = new PropertyService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String city = req.getParameter("city");
        String type = req.getParameter("type");
        String gender = req.getParameter("gender");
        String sharing = req.getParameter("sharing");
        String minRentStr = req.getParameter("minRent");
        String maxRentStr = req.getParameter("maxRent");
        String acStr = req.getParameter("ac");
        String foodStr = req.getParameter("food");
        String wifiStr = req.getParameter("wifi");
        String bathStr = req.getParameter("bath");
        String sort = req.getParameter("sort");

        BigDecimal minRent = null;
        BigDecimal maxRent = null;

        try {
            if (minRentStr != null && !minRentStr.trim().isEmpty()) {
                minRent = new BigDecimal(minRentStr.trim());
            }
        } catch (Exception ignored) {}

        try {
            if (maxRentStr != null && !maxRentStr.trim().isEmpty()) {
                maxRent = new BigDecimal(maxRentStr.trim());
            }
        } catch (Exception ignored) {}

        Boolean ac = (acStr != null && !acStr.isEmpty()) ? Boolean.valueOf(acStr) : null;
        Boolean food = (foodStr != null && !foodStr.isEmpty()) ? Boolean.valueOf(foodStr) : null;
        Boolean wifi = (wifiStr != null && !wifiStr.isEmpty()) ? Boolean.valueOf(wifiStr) : null;
        Boolean bath = (bathStr != null && !bathStr.isEmpty()) ? Boolean.valueOf(bathStr) : null;

        List<Property> results = propertyService.searchProperties(
            city, type, minRent, maxRent, sharing, ac, food, wifi, bath, gender, sort
        );

        List<String> cities = propertyService.getPopularCities();

        req.setAttribute("properties", results);
        req.setAttribute("popularCities", cities);
        req.setAttribute("resultCount", results.size());

        // Echo back filters for UI selection state
        req.setAttribute("selectedCity", city);
        req.setAttribute("selectedType", type);
        req.setAttribute("selectedGender", gender);
        req.setAttribute("selectedSharing", sharing);
        req.setAttribute("selectedMinRent", minRentStr);
        req.setAttribute("selectedMaxRent", maxRentStr);
        req.setAttribute("selectedAc", ac);
        req.setAttribute("selectedFood", food);
        req.setAttribute("selectedWifi", wifi);
        req.setAttribute("selectedBath", bath);
        req.setAttribute("selectedSort", sort);

        req.getRequestDispatcher("/WEB-INF/views/customer/search.jsp").forward(req, resp);
    }
}

