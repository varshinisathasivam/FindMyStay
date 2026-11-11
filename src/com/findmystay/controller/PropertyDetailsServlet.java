package com.findmystay.controller;

import com.findmystay.dao.RecentlyViewedDAO;
import com.findmystay.model.Property;
import com.findmystay.model.Review;
import com.findmystay.model.User;
import com.findmystay.service.PropertyService;
import com.findmystay.service.ReviewService;
import com.findmystay.service.WishlistService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller for viewing comprehensive accommodation details.
 */
@WebServlet(name = "PropertyDetailsServlet", urlPatterns = {"/property"})
public class PropertyDetailsServlet extends HttpServlet {

    private final PropertyService propertyService = new PropertyService();
    private final ReviewService reviewService = new ReviewService();
    private final WishlistService wishlistService = new WishlistService();
    private final RecentlyViewedDAO recentlyViewedDAO = new RecentlyViewedDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/stays");
            return;
        }

        try {
            int propertyId = Integer.parseInt(idParam.trim());
            Property property = propertyService.getPropertyDetails(propertyId);

            if (property == null || !property.isActive()) {
                resp.sendRedirect(req.getContextPath() + "/stays?error=Property not found or unavailable");
                return;
            }

            // Reviews
            List<Review> reviews = reviewService.getPropertyReviews(propertyId);

            // Smart Budget Suggestions
            List<Property> budgetSuggestions = propertyService.getBudgetSuggestions(property.getMonthlyRent(), propertyId);

            // Logged-in user context (Wishlist check & Recently Viewed recording)
            HttpSession session = req.getSession(false);
            boolean isWishlisted = false;
            if (session != null) {
                User currentUser = (User) session.getAttribute("currentUser");
                if (currentUser != null) {
                    isWishlisted = wishlistService.isWishlisted(currentUser.getId(), propertyId);
                    recentlyViewedDAO.recordView(currentUser.getId(), propertyId);
                }
            }

            req.setAttribute("property", property);
            req.setAttribute("reviews", reviews);
            req.setAttribute("budgetSuggestions", budgetSuggestions);
            req.setAttribute("isWishlisted", isWishlisted);

            req.getRequestDispatcher("/WEB-INF/views/customer/property-details.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/stays");
        }
    }
}

