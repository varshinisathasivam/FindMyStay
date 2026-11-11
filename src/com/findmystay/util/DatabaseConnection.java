package com.findmystay.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages JDBC connections to MySQL database (findmystay_db).
 */
public class DatabaseConnection {

    private static String url = "jdbc:mysql://localhost:3306/findmystay_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
    private static String user = "root";
    private static String password = "#Varshini123";
    private static boolean driverLoaded = false;

    static {
        loadConfiguration();
        loadDriver();
    }

    private static void loadConfiguration() {
        try {
            // Check system properties or environment variables first
            String envHost = System.getenv("DB_HOST");
            String envPort = System.getenv("DB_PORT");
            String envDb   = System.getenv("DB_NAME");
            String envUser = System.getenv("DB_USER");
            String envPass = System.getenv("DB_PASSWORD");

            if (envHost != null && !envHost.isEmpty()) {
                String port = (envPort != null) ? envPort : "3306";
                String db = (envDb != null) ? envDb : "findmystay_db";
                url = "jdbc:mysql://" + envHost + ":" + port + "/" + db + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
            }
            if (envUser != null) user = envUser;
            if (envPass != null) password = envPass;

            // Also check optional classpath config file db.properties
            try (InputStream is = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (is != null) {
                    Properties props = new Properties();
                    props.load(is);
                    if (props.getProperty("db.url") != null) url = props.getProperty("db.url");
                    if (props.getProperty("db.user") != null) user = props.getProperty("db.user");
                    if (props.getProperty("db.password") != null) password = props.getProperty("db.password");
                }
            }
        } catch (Exception ignored) {
        }
    }

    private static void loadDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            driverLoaded = true;
        } catch (ClassNotFoundException e) {
            try {
                // Fallback for older drivers
                Class.forName("com.mysql.jdbc.Driver");
                driverLoaded = true;
            } catch (ClassNotFoundException ex) {
                System.err.println("[FindMyStay] Warning: MySQL JDBC Driver not found in classpath. " + ex.getMessage());
            }
        }
    }

    /**
     * Obtains a new database connection.
     *
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        if (!driverLoaded) {
            loadDriver();
        }
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Safely closes an AutoCloseable resource (Connection, Statement, ResultSet).
     */
    public static void close(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Tests whether the database connection is currently healthy.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    public static String getUrl() { return url; }
    public static String getUser() { return user; }
}

