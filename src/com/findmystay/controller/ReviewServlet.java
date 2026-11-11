package com.findmystay.controller;

import com.findmystay.model.User;
import com.findmystay.service.ReviewService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Controller for submitting customer reviews and ratings.
 */
@WebServlet(name = "ReviewServlet", urlPatterns = {"/review/add", "/admin/review/delete"})
public class ReviewServlet extends HttpServlet {

    private final ReviewService reviewService = new ReviewService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login?error=Please login to submit a review");
            return;
        }

        String uri = req.getRequestURI();

        if (uri.endsWith("/delete")) {
            if (!currentUser.isAdmin()) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            String revIdStr = req.getParameter("reviewId");
            if (revIdStr != null) {
                try {
                    reviewService.deleteReview(Integer.parseInt(revIdStr));
                } catch (Exception ignored) {}
            }
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard?tab=reviews&success=Review deleted");
            return;
        }

        // Add review
        String propIdStr = req.getParameter("propertyId");
        String ratingStr = req.getParameter("rating");
        String comment = req.getParameter("comment");

        if (propIdStr != null && ratingStr != null && comment != null) {
            try {
                int propertyId = Integer.parseInt(propIdStr);
                int rating = Integer.parseInt(ratingStr);
                reviewService.addReview(propertyId, currentUser.getId(), rating, comment);
                resp.sendRedirect(req.getContextPath() + "/property?id=" + propertyId + "&success=Thank you for sharing your review!");
                return;
            } catch (Exception ignored) {}
        }

        resp.sendRedirect(req.getContextPath() + "/stays");
    }
}

