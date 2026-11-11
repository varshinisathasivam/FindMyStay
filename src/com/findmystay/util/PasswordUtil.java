package com.findmystay.util;

/**
 * Utility for password hashing and verification using BCrypt.
 */
public class PasswordUtil {

    /**
     * Hashes a plain text password with a generated BCrypt salt.
     *
     * @param plainPassword Plain text password
     * @return BCrypt hashed string
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        String salt = BCrypt.gensalt(10);
        return BCrypt.hashpw(plainPassword, salt);
    }

    /**
     * Verifies a candidate plain text password against a stored BCrypt hash.
     *
     * @param plainPassword Candidate plain text
     * @param hashedPassword Stored BCrypt hash
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}

