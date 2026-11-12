package com.findmystay.controller;

import com.findmystay.model.User;
import com.findmystay.service.UserService;
import com.findmystay.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controller for user registration (Customer or Property Owner).
 */
@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        String roleStr = req.getParameter("role");

        // Server-side validation
        if (!ValidationUtil.isNotEmpty(fullName) || !ValidationUtil.isValidLength(fullName, 3, 100)) {
            req.setAttribute("errorMessage", "Please provide a valid full name (at least 3 characters).");
            preserveFormFields(req, fullName, email, phone, roleStr);
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            req.setAttribute("errorMessage", "Please provide a valid email address.");
            preserveFormFields(req, fullName, email, phone, roleStr);
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }

        if (!ValidationUtil.isValidPhone(phone)) {
            req.setAttribute("errorMessage", "Please provide a valid 10-digit Indian phone number starting with 6-9.");
            preserveFormFields(req, fullName, email, phone, roleStr);
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }

        if (!ValidationUtil.isValidPassword(password)) {
            req.setAttribute("errorMessage", "Password must be at least 6 characters long.");
            preserveFormFields(req, fullName, email, phone, roleStr);
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }

        if (!password.equals(confirmPassword)) {
            req.setAttribute("errorMessage", "Password and Confirm Password do not match.");
            preserveFormFields(req, fullName, email, phone, roleStr);
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }

        User.Role role = User.Role.CUSTOMER;
        if ("OWNER".equalsIgnoreCase(roleStr)) {
            role = User.Role.OWNER;
        }

        UserService.RegistrationResponse regResult = userService.registerUser(fullName, email, phone, password, role);

        if (regResult.isSuccess()) {
            resp.sendRedirect(req.getContextPath() + "/login?success=" + java.net.URLEncoder.encode(regResult.getMessage(), java.nio.charset.StandardCharsets.UTF_8));
        } else {
            req.setAttribute("errorMessage", regResult.getMessage());
            preserveFormFields(req, fullName, email, phone, roleStr);
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
        }
    }

    private void preserveFormFields(HttpServletRequest req, String name, String email, String phone, String role) {
        req.setAttribute("fullName", name);
        req.setAttribute("email", email);
        req.setAttribute("phone", phone);
        req.setAttribute("role", role);
    }
}

