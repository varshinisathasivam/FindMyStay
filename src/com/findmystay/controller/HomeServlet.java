package com.findmystay.controller;

import com.findmystay.model.Property;
import com.findmystay.service.PropertyService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Handles the application landing page.
 */
@WebServlet(name = "HomeServlet", urlPatterns = {"", "/home"})
public class HomeServlet extends HttpServlet {

    private final PropertyService propertyService = new PropertyService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Property> featuredStays = propertyService.getFeaturedProperties(6);
        List<String> cities = propertyService.getPopularCities();

        req.setAttribute("featuredStays", featuredStays);
        req.setAttribute("popularCities", cities);

        req.getRequestDispatcher("/WEB-INF/views/customer/home.jsp").forward(req, resp);
    }
}

