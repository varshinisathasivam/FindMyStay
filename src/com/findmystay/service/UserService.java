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

    public boolean register(String fullName, String email, String phone, String plainPassword, User.Role role) {
        if (!ValidationUtil.isNotEmpty(fullName) ||
            !ValidationUtil.isValidEmail(email) ||
            !ValidationUtil.isValidPassword(plainPassword)) {
            return false;
        }

        if (userDAO.emailExists(email)) {
            return false;
        }

        // Prevent public self-registration as ADMIN
        if (User.Role.ADMIN.equals(role)) {
            role = User.Role.CUSTOMER;
        }

        String hashed = PasswordUtil.hashPassword(plainPassword);
        User user = new User(fullName.trim(), email.trim().toLowerCase(), phone != null ? phone.trim() : "", hashed, role);
        return userDAO.createUser(user);
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

