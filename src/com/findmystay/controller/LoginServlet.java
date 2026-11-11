package com.findmystay.controller;

import com.findmystay.model.User;
import com.findmystay.service.UserService;
import com.findmystay.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Controller for user authentication and session creation.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            User currentUser = (User) session.getAttribute("currentUser");
            redirectToDashboard(req, resp, currentUser);
            return;
        }

        String redirect = req.getParameter("redirect");
        String success = req.getParameter("success");
        String error = req.getParameter("error");

        req.setAttribute("redirect", redirect);
        req.setAttribute("successMessage", success);
        req.setAttribute("errorMessage", error);

        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String redirect = req.getParameter("redirect");

        if (!ValidationUtil.isNotEmpty(email) || !ValidationUtil.isNotEmpty(password)) {
            req.setAttribute("errorMessage", "Please provide both email and password.");
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        User user = userService.authenticate(email, password);

        if (user != null) {
            // Establish session
            HttpSession session = req.getSession(true);
            session.setAttribute("currentUser", user);
            session.setMaxInactiveInterval(60 * 60); // 1 hour session

            if (redirect != null && !redirect.trim().isEmpty() && !redirect.contains("login") && !redirect.contains("register")) {
                resp.sendRedirect(redirect);
            } else {
                redirectToDashboard(req, resp, user);
            }
        } else {
            req.setAttribute("errorMessage", "Invalid email or password. Please verify and try again.");
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        }
    }

    private void redirectToDashboard(HttpServletRequest req, HttpServletResponse resp, User user) throws IOException {
        switch (user.getRole()) {
            case ADMIN:
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
                break;
            case OWNER:
                resp.sendRedirect(req.getContextPath() + "/owner/dashboard");
                break;
            case CUSTOMER:
            default:
                resp.sendRedirect(req.getContextPath() + "/customer/dashboard");
                break;
        }
    }
}

