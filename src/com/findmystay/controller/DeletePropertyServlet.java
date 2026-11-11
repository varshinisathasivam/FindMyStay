package com.findmystay.controller;

import com.findmystay.model.User;
import com.findmystay.service.PropertyService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Controller for deleting an owner's property listing.
 */
@WebServlet(name = "DeletePropertyServlet", urlPatterns = {"/owner/property/delete"})
public class DeletePropertyServlet extends HttpServlet {

    private final PropertyService propertyService = new PropertyService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentOwner = (User) session.getAttribute("currentUser");

        String idStr = req.getParameter("id");
        if (idStr != null) {
            try {
                int propertyId = Integer.parseInt(idStr);
                boolean deleted = propertyService.deleteProperty(propertyId, currentOwner.getId());
                if (deleted) {
                    resp.sendRedirect(req.getContextPath() + "/owner/dashboard?success=Property removed successfully.");
                    return;
                }
            } catch (Exception ignored) {}
        }
        resp.sendRedirect(req.getContextPath() + "/owner/dashboard?error=Could not delete property.");
    }
}

