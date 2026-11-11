package com.findmystay.controller;

import com.findmystay.model.User;
import com.findmystay.model.Wishlist;
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
 * Controller for managing customer wishlist operations.
 */
@WebServlet(name = "WishlistServlet", urlPatterns = {"/wishlist", "/wishlist/toggle", "/wishlist/remove"})
public class WishlistServlet extends HttpServlet {

    private final WishlistService wishlistService = new WishlistService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login?error=Please login to view your saved wishlist");
            return;
        }

        List<Wishlist> wishlist = wishlistService.getUserWishlist(currentUser.getId());
        req.setAttribute("wishlists", wishlist);
        req.getRequestDispatcher("/WEB-INF/views/customer/wishlist.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String propIdStr = req.getParameter("propertyId");
        String redirect = req.getParameter("redirect");

        if (propIdStr != null) {
            try {
                int propertyId = Integer.parseInt(propIdStr);
                String uri = req.getRequestURI();

                if (uri.endsWith("/remove")) {
                    wishlistService.removeFromWishlist(currentUser.getId(), propertyId);
                } else {
                    // Toggle
                    if (wishlistService.isWishlisted(currentUser.getId(), propertyId)) {
                        wishlistService.removeFromWishlist(currentUser.getId(), propertyId);
                    } else {
                        wishlistService.addToWishlist(currentUser.getId(), propertyId);
                    }
                }
            } catch (NumberFormatException ignored) {}
        }

        if (redirect != null && !redirect.trim().isEmpty()) {
            resp.sendRedirect(redirect);
        } else {
            resp.sendRedirect(req.getContextPath() + "/wishlist");
        }
    }
}

