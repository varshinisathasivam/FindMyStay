package com.findmystay.service;

import com.findmystay.dao.UserDAO;
import com.findmystay.model.User;
import com.findmystay.util.PasswordUtil;
import com.findmystay.util.ValidationUtil;

import java.util.List;

/**
 * Service layer encapsulating user business logic and authentication.
 */
public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User authenticate(String email, String plainPassword) {
        if (!ValidationUtil.isNotEmpty(email) || !ValidationUtil.isNotEmpty(plainPassword)) {
            return null;
        }

        User user = userDAO.findByEmail(email.trim());
        if (user != null) {
            if (PasswordUtil.verifyPassword(plainPassword, user.getPasswordHash())) {
                return user;
            }
        }
        return null;
    }

    public static class RegistrationResponse {
        private final boolean success;
        private final String message;

        public RegistrationResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }

    public RegistrationResponse registerUser(String fullName, String email, String phone, String plainPassword, User.Role role) {
        if (!ValidationUtil.isNotEmpty(fullName) || !ValidationUtil.isValidLength(fullName, 3, 100)) {
            return new RegistrationResponse(false, "Please provide a valid full name (at least 3 characters).");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            return new RegistrationResponse(false, "Please provide a valid email address.");
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            return new RegistrationResponse(false, "Please provide a valid 10-digit Indian phone number starting with 6-9.");
        }
        if (!ValidationUtil.isValidPassword(plainPassword)) {
            return new RegistrationResponse(false, "Password must be at least 6 characters long.");
        }

        if (!com.findmystay.util.DatabaseConnection.testConnection()) {
            return new RegistrationResponse(false, "Database connection error! Please verify MySQL is running and credentials in src/db.properties are correct.");
        }

        if (userDAO.emailExists(email)) {
            return new RegistrationResponse(false, "An account with this email address already exists. Please login instead.");
        }

        // Prevent public self-registration as ADMIN
        if (User.Role.ADMIN.equals(role)) {
            role = User.Role.CUSTOMER;
        }

        String hashed = PasswordUtil.hashPassword(plainPassword);
        User user = new User(fullName.trim(), email.trim().toLowerCase(), phone != null ? phone.trim() : "", hashed, role);
        boolean created = userDAO.createUser(user);
        if (created) {
            return new RegistrationResponse(true, "Account created successfully! Please login.");
        } else {
            return new RegistrationResponse(false, "Unable to save user details to database. Please check MySQL server logs.");
        }
    }

    public boolean register(String fullName, String email, String phone, String plainPassword, User.Role role) {
        return registerUser(fullName, email, phone, plainPassword, role).isSuccess();
    }

    public boolean updateProfile(int userId, String fullName, String phone) {
        if (!ValidationUtil.isNotEmpty(fullName)) return false;
        User user = userDAO.findById(userId);
        if (user != null) {
            user.setFullName(fullName.trim());
            user.setPhone(phone != null ? phone.trim() : "");
            return userDAO.updateUser(user);
        }
        return false;
    }

    public boolean changePassword(int userId, String currentPassword, String newPassword) {
        if (!ValidationUtil.isValidPassword(newPassword)) return false;

        User user = userDAO.findById(userId);
        if (user != null && PasswordUtil.verifyPassword(currentPassword, user.getPasswordHash())) {
            String newHashed = PasswordUtil.hashPassword(newPassword);
            return userDAO.updatePassword(userId, newHashed);
        }
        return false;
    }

    public User getUserById(int userId) {
        return userDAO.findById(userId);
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public boolean deleteUser(int id) {
        return userDAO.deleteUser(id);
    }

    public int getCustomerCount() {
        return userDAO.countByRole(User.Role.CUSTOMER);
    }

    public int getOwnerCount() {
        return userDAO.countByRole(User.Role.OWNER);
    }
}

