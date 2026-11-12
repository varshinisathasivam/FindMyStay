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

        if (!com.findmystay.util.DatabaseConnection.testConnection()) {
            req.setAttribute("errorMessage", "Database connection failed! Please verify MySQL is running on port 3306 and credentials in src/db.properties are correct.");
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        User user = userService.authenticate(email, password);

        if (user != null) {
            // Establish session and regenerate session ID to prevent fixation attacks
            HttpSession session = req.getSession(true);
            try {
                req.changeSessionId();
            } catch (Exception ignored) {}
            session.setAttribute("currentUser", user);
            session.setMaxInactiveInterval(60 * 60); // 1 hour session
            // Pre-seed CSRF token for authenticated session
            com.findmystay.util.CsrfUtil.getToken(session);

            if (isSafeInternalRedirect(req, redirect, user)) {
                String safeTarget = sanitizeRedirectPath(req, redirect);
                resp.sendRedirect(safeTarget);
            } else {
                redirectToDashboard(req, resp, user);
            }
        } else {
            req.setAttribute("errorMessage", "Invalid email or password. Please verify and try again.");
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        }
    }

    /**
     * Validates that the redirect URL is a safe internal relative application URL.
     * Prevents open redirect attacks, CRLF injection, and unauthorized cross-role access.
     */
    public static boolean isSafeInternalRedirect(HttpServletRequest req, String redirect, User user) {
        if (redirect == null || redirect.trim().isEmpty()) {
            return false;
        }

        String url = redirect.trim();

        // Disallow CRLF / Header injection characters
        if (url.indexOf('\r') != -1 || url.indexOf('\n') != -1) {
            return false;
        }

        // Disallow scheme / protocol / external URLs (http:, https:, javascript:, data:, etc.)
        int colonIdx = url.indexOf(':');
        int questionIdx = url.indexOf('?');
        if (colonIdx != -1 && (questionIdx == -1 || colonIdx < questionIdx)) {
            return false;
        }

        // Disallow protocol-relative URLs (//) and backslashes (\\)
        if (url.startsWith("//") || url.startsWith("\\\\") || url.contains("/\\") || url.contains("\\/")) {
            return false;
        }

        // Must start with '/' to be an internal relative path
        if (!url.startsWith("/")) {
            return false;
        }

        // Strip context path for path analysis if present
        String contextPath = (req != null) ? req.getContextPath() : "";
        String path = url;
        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }

        // Must still start with '/' after stripping context path
        if (!path.startsWith("/")) {
            return false;
        }

        // Extract path without query parameters for security check
        String cleanPath = (path.indexOf('?') != -1) ? path.substring(0, path.indexOf('?')) : path;

        // Disallow redirecting back to authentication pages
        if (cleanPath.equals("/login") || cleanPath.equals("/register") || cleanPath.equals("/logout")) {
            return false;
        }

        // Enforce role-based access on destination
        if (user != null) {
            if (cleanPath.startsWith("/admin/") || cleanPath.equals("/admin")) {
                return user.isAdmin();
            }

            if (cleanPath.startsWith("/owner/") || cleanPath.equals("/owner")) {
                return user.isOwner() || user.isAdmin();
            }
        }

        return true;
    }

    /**
     * Ensures internal redirect path includes contextPath correctly without duplicating.
     */
    private String sanitizeRedirectPath(HttpServletRequest req, String redirect) {
        String url = redirect.trim();
        String contextPath = req.getContextPath();
        if (contextPath != null && !contextPath.isEmpty()) {
            if (!url.startsWith(contextPath)) {
                return contextPath + url;
            }
        }
        return url;
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
