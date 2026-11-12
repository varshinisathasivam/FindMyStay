package com.findmystay.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages JDBC connections to MySQL database (findmystay_db).
 * Database credentials are read dynamically from db.properties configuration file
 * or environment variables, avoiding hard-coded credentials in Java source code.
 */
public class DatabaseConnection {

    private static String url;
    private static String user;
    private static String password;
    private static boolean driverLoaded = false;

    static {
        loadConfiguration();
        loadDriver();
    }

    private static void loadConfiguration() {
        // Safe defaults without embedded sensitive passwords
        String defaultUrl = "jdbc:mysql://localhost:3306/findmystay_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
        String defaultUser = "root";
        String defaultPass = "";

        Properties props = new Properties();

        // 1. First, check working directory or known relative locations for active local development configuration
        String[] candidatePaths = {
            "src/db.properties",
            "db.properties",
            "../src/db.properties",
            "build/classes/db.properties",
            "src/webapp/WEB-INF/classes/db.properties"
        };
        boolean fileFound = false;
        for (String p : candidatePaths) {
            File f = new File(p);
            if (f.exists() && f.isFile()) {
                try (InputStream fis = new FileInputStream(f)) {
                    props.load(fis);
                    fileFound = true;
                    // If this file provides a non-empty password, prioritize it
                    String pwd = props.getProperty("db.password");
                    if (pwd != null && !pwd.trim().isEmpty()) {
                        break;
                    }
                } catch (Exception ignored) {
                }
            }
        }

        // 2. ClassLoader fallback if not loaded from file or password is empty
        if (!fileFound || props.getProperty("db.password") == null || props.getProperty("db.password").trim().isEmpty()) {
            try (InputStream is = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (is != null) {
                    Properties clProps = new Properties();
                    clProps.load(is);
                    for (String key : clProps.stringPropertyNames()) {
                        if (!props.containsKey(key) || props.getProperty(key).trim().isEmpty()) {
                            props.setProperty(key, clProps.getProperty(key));
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }

        url = props.getProperty("db.url", defaultUrl);
        user = props.getProperty("db.user", defaultUser);
        password = props.getProperty("db.password", defaultPass);

        if (url != null) url = url.trim();
        if (user != null) user = user.trim();

        // 3. System Properties and Environment Variables have top priority
        String envUrl  = System.getenv("DB_URL");
        String envHost = System.getenv("DB_HOST");
        String envPort = System.getenv("DB_PORT");
        String envDb   = System.getenv("DB_NAME");
        String envUser = System.getenv("DB_USER");
        String envPass = System.getenv("DB_PASSWORD");

        if (System.getProperty("db.url") != null && !System.getProperty("db.url").trim().isEmpty()) {
            url = System.getProperty("db.url").trim();
        } else if (envUrl != null && !envUrl.trim().isEmpty()) {
            url = envUrl.trim();
        } else if (envHost != null && !envHost.trim().isEmpty()) {
            String port = (envPort != null && !envPort.trim().isEmpty()) ? envPort.trim() : "3306";
            String db = (envDb != null && !envDb.trim().isEmpty()) ? envDb.trim() : "findmystay_db";
            url = "jdbc:mysql://" + envHost.trim() + ":" + port + "/" + db + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
        }

        if (System.getProperty("db.user") != null && !System.getProperty("db.user").trim().isEmpty()) {
            user = System.getProperty("db.user").trim();
        } else if (envUser != null && !envUser.trim().isEmpty()) {
            user = envUser.trim();
        }

        if (System.getProperty("db.password") != null) {
            password = System.getProperty("db.password");
        } else if (envPass != null) {
            password = envPass;
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
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException initialEx) {
            // Re-read configuration in case db.properties was updated or saved
            loadConfiguration();
            return DriverManager.getConnection(url, user, password);
        }
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
        } catch (SQLException e) {
            return false;
        }
    }

    public static String getUrl() {
        return url;
    }

    public static String getUser() {
        return user;
    }
}
