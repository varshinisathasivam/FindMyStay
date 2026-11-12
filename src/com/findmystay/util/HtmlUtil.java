package com.findmystay.util;

/**
 * Utility for sanitizing and escaping HTML/XML content to prevent Cross-Site Scripting (XSS).
 */
public class HtmlUtil {

    /**
     * Escapes special characters for safe inclusion in HTML context.
     *
     * @param input Raw text
     * @return HTML-escaped string safe against XSS
     */
    public static String escape(String input) {
        if (input == null) return "";
        StringBuilder sb = new StringBuilder(input.length() + 16);
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '<':  sb.append("&lt;"); break;
                case '>':  sb.append("&gt;"); break;
                case '&':  sb.append("&amp;"); break;
                case '"':  sb.append("&quot;"); break;
                case '\'': sb.append("&#x27;"); break;
                case '/':  sb.append("&#x2F;"); break;
                default:   sb.append(c); break;
            }
        }
        return sb.toString();
    }
}
