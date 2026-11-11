package com.findmystay.util;

import java.util.regex.Pattern;

/**
 * Server-side input validation and sanitization utility.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[6-9]\\d{9}$"
    );

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        String clean = phone.replaceAll("[\\s-]", "");
        return PHONE_PATTERN.matcher(clean).matches();
    }

    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean isValidLength(String str, int min, int max) {
        if (str == null) return false;
        int len = str.trim().length();
        return len >= min && len <= max;
    }

    public static boolean isValidPassword(String password) {
        // At least 6 characters
        return password != null && password.length() >= 6;
    }

    public static boolean isValidPositiveDecimal(String val) {
        if (val == null) return false;
        try {
            double d = Double.parseDouble(val.trim());
            return d >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String sanitize(String input) {
        if (input == null) return null;
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#x27;");
    }
}

