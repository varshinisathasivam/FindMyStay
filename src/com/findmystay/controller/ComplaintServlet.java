package com.findmystay.controller;

import com.findmystay.dao.ComplaintDAO;
import com.findmystay.model.Complaint;
import com.findmystay.model.User;
import com.findmystay.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Controller for submitting and resolving complaints.
 */
@WebServlet(name = "ComplaintServlet", urlPatterns = {"/complaint/submit", "/admin/complaint/status"})
public class ComplaintServlet extends HttpServlet {

    private final ComplaintDAO complaintDAO = new ComplaintDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login?error=Please login to submit a complaint");
            return;
        }

        String uri = req.getRequestURI();

        if (uri.endsWith("/status")) {
            if (!currentUser.isAdmin()) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            String idStr = req.getParameter("complaintId");
            String statusStr = req.getParameter("status");
            if (idStr != null && statusStr != null) {
                try {
                    complaintDAO.updateStatus(Integer.parseInt(idStr), Complaint.ComplaintStatus.valueOf(statusStr));
                } catch (Exception ignored) {}
            }
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard?tab=complaints&success=Complaint updated");
            return;
        }

        String subject = req.getParameter("subject");
        String description = req.getParameter("description");
        String propIdStr = req.getParameter("propertyId");

        if (!ValidationUtil.isNotEmpty(subject) || !ValidationUtil.isNotEmpty(description)) {
            resp.sendRedirect(req.getContextPath() + "/customer/dashboard?tab=complaints&error=Subject and details required");
            return;
        }

        Complaint c = new Complaint();
        c.setUserId(currentUser.getId());
        if (propIdStr != null && !propIdStr.isEmpty()) {
            try {
                c.setPropertyId(Integer.parseInt(propIdStr));
            } catch (Exception ignored) {}
        }
        c.setSubject(subject);
        c.setDescription(description);

        complaintDAO.createComplaint(c);
        resp.sendRedirect(req.getContextPath() + "/customer/dashboard?tab=complaints&success=Complaint filed successfully. Admin team will investigate.");
    }
}

