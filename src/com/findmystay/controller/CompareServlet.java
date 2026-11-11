package com.findmystay.controller;

import com.findmystay.model.Property;
import com.findmystay.model.PropertyComparison;
import com.findmystay.service.PropertyService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Controller for side-by-side comparison of up to 3 stays.
 */
@WebServlet(name = "CompareServlet", urlPatterns = {"/compare", "/compare/add", "/compare/remove", "/compare/clear"})
public class CompareServlet extends HttpServlet {

    private final PropertyService propertyService = new PropertyService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        PropertyComparison comparison = (PropertyComparison) session.getAttribute("comparison");
        if (comparison == null) {
            comparison = new PropertyComparison();
            session.setAttribute("comparison", comparison);
        }

        req.setAttribute("comparison", comparison);
        req.getRequestDispatcher("/WEB-INF/views/customer/compare.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        PropertyComparison comparison = (PropertyComparison) session.getAttribute("comparison");
        if (comparison == null) {
            comparison = new PropertyComparison();
            session.setAttribute("comparison", comparison);
        }

        String uri = req.getRequestURI();
        String idStr = req.getParameter("propertyId");
        String redirect = req.getParameter("redirect");

        if (uri.endsWith("/clear")) {
            comparison.getProperties().clear();
        } else if (idStr != null) {
            try {
                int propertyId = Integer.parseInt(idStr);
                if (uri.endsWith("/add")) {
                    Property p = propertyService.getPropertyDetails(propertyId);
                    if (p != null) {
                        comparison.addProperty(p);
                    }
                } else if (uri.endsWith("/remove")) {
                    comparison.removeProperty(propertyId);
                }
            } catch (NumberFormatException ignored) {}
        }

        if (redirect != null && !redirect.trim().isEmpty()) {
            resp.sendRedirect(redirect);
        } else {
            resp.sendRedirect(req.getContextPath() + "/compare");
        }
    }
}

