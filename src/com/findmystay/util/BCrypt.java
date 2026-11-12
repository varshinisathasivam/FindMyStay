package com.findmystay.util;

/**
 * Standard BCrypt password hashing adapter using the industry-standard
 * org.mindrot.jbcrypt.BCrypt library (compatible with Java 21 LTS).
 * Produces standard OpenBSD $2a$ Blowfish BCrypt hashes with genuine
 * salt generation and key expansion.
 */
public class BCrypt {

    /**
     * Generate a salt for use with the BCrypt.hashpw() method.
     *
     * @param log_rounds The log2 of the number of rounds of hashing to apply (4 to 31)
     * @return An encoded salt value
     */
    public static String gensalt(int log_rounds) {
        return org.mindrot.jbcrypt.BCrypt.gensalt(log_rounds);
    }

    /**
     * Generate a salt with default complexity (10 rounds).
     *
     * @return An encoded salt value
     */
    public static String gensalt() {
        return org.mindrot.jbcrypt.BCrypt.gensalt(10);
    }

    /**
     * Hash a password using the BCrypt algorithm.
     *
     * @param password The password to hash
     * @param salt The salt to hash with
     * @return The hashed password
     */
    public static String hashpw(String password, String salt) {
        if (password == null || salt == null) {
            throw new IllegalArgumentException("Password and salt cannot be null");
        }
        return org.mindrot.jbcrypt.BCrypt.hashpw(password, salt);
    }

    /**
     * Check that a plaintext password matches a previously hashed one.
     *
     * @param plaintext The plaintext password to verify
     * @param hashed The stored BCrypt hash to verify against
     * @return true if the passwords match, false otherwise
     */
    public static boolean checkpw(String plaintext, String hashed) {
        if (plaintext == null || hashed == null || hashed.length() < 28) {
            return false;
        }
        try {
            return org.mindrot.jbcrypt.BCrypt.checkpw(plaintext, hashed);
        } catch (Exception e) {
            return false;
        }
    }
}
