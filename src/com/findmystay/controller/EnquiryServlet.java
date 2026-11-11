package com.findmystay.controller;

import com.findmystay.dao.EnquiryDAO;
import com.findmystay.model.Enquiry;
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
 * Controller for sending and managing property inquiries.
 */
@WebServlet(name = "EnquiryServlet", urlPatterns = {"/enquiry/submit", "/owner/enquiry/status"})
public class EnquiryServlet extends HttpServlet {

    private final EnquiryDAO enquiryDAO = new EnquiryDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();

        if (uri.endsWith("/status")) {
            HttpSession session = req.getSession(false);
            User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
            if (currentUser == null || (!currentUser.isOwner() && !currentUser.isAdmin())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            String idStr = req.getParameter("enquiryId");
            String statusStr = req.getParameter("status");
            if (idStr != null && statusStr != null) {
                try {
                    enquiryDAO.updateStatus(Integer.parseInt(idStr), Enquiry.EnquiryStatus.valueOf(statusStr));
                } catch (Exception ignored) {}
            }
            resp.sendRedirect(req.getContextPath() + "/owner/dashboard?tab=enquiries&success=Enquiry marked " + statusStr);
            return;
        }

        // Customer submission
        String propIdStr = req.getParameter("propertyId");
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        String message = req.getParameter("message");

        if (propIdStr == null || !ValidationUtil.isNotEmpty(name) || !ValidationUtil.isValidEmail(email)) {
            resp.sendRedirect(req.getContextPath() + "/property?id=" + propIdStr + "&error=Please complete all required enquiry fields");
            return;
        }

        try {
            int propId = Integer.parseInt(propIdStr);
            HttpSession session = req.getSession(false);
            User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

            Enquiry enq = new Enquiry();
            enq.setPropertyId(propId);
            if (currentUser != null) enq.setUserId(currentUser.getId());
            enq.setSenderName(name);
            enq.setSenderEmail(email);
            enq.setSenderPhone(phone != null ? phone : "");
            enq.setMessage(message != null ? message : "I would like to enquire about this stay.");

            enquiryDAO.createEnquiry(enq);
            resp.sendRedirect(req.getContextPath() + "/property?id=" + propId + "&success=Enquiry sent to owner successfully! They will contact you shortly.");

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/stays");
        }
    }
}

