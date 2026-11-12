package com.findmystay.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * CSRF Protection Utility. Generates and validates cryptographic per-session tokens.
 */
public class CsrfUtil {

    public static final String CSRF_SESSION_ATTR = "csrfToken";
    public static final String CSRF_PARAM_NAME = "csrfToken";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Retrieves the current CSRF token from the session, or generates a new one.
     */
    public static String getToken(HttpSession session) {
        if (session == null) return "";
        synchronized (session) {
            String token = (String) session.getAttribute(CSRF_SESSION_ATTR);
            if (token == null || token.trim().isEmpty()) {
                byte[] bytes = new byte[24];
                RANDOM.nextBytes(bytes);
                token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
                session.setAttribute(CSRF_SESSION_ATTR, token);
            }
            return token;
        }
    }

    /**
     * Validates that the request parameter matches the session's CSRF token.
     */
    public static boolean isValid(HttpServletRequest request) {
        if (request == null) return false;
        HttpSession session = request.getSession(false);
        if (session == null) return false;

        String sessionToken = (String) session.getAttribute(CSRF_SESSION_ATTR);
        if (sessionToken == null || sessionToken.isEmpty()) return false;

        String reqToken = request.getParameter(CSRF_PARAM_NAME);
        if (reqToken == null) {
            reqToken = request.getHeader("X-CSRF-TOKEN");
        }

        if (reqToken == null) return false;

        return constantTimeEquals(sessionToken, reqToken.trim());
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
