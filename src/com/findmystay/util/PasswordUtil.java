package com.findmystay.util;

/**
 * Utility for password hashing and verification using BCrypt only.
 * Disallows plain-text fallback for security.
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
     * Verifies a candidate plain text password strictly using BCrypt.
     * Plain-text fallback is strictly prohibited.
     *
     * @param plainPassword Candidate plain text
     * @param hashedPassword Stored BCrypt hash
     * @return true if password matches BCrypt hash, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }

        // Strictly BCrypt verification only
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception ignored) {
            return false;
        }
    }
}
