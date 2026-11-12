package com.findmystay.server;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.net.ServerSocket;
import java.net.URI;

/**
 * EmbeddedServer – launches FindMyStay inside an embedded Tomcat 10 instance.
 * No separate Tomcat installation required.
 *
 * Usage:  java -cp "build\classes;lib\*" com.findmystay.server.EmbeddedServer
 *
 * Then open: http://localhost:8080/
 */
public class EmbeddedServer {

    private static final int PORT = 8080;

    public static void main(String[] args) throws LifecycleException {

        System.out.println("==============================================");
        System.out.println("  FindMyStay – Embedded Server Starting...  ");
        System.out.println("==============================================");

        // Pre-check port availability before attempting to bind
        if (!isPortAvailable(PORT)) {
            System.err.println("==============================================================");
            System.err.println("  [ERROR] Port " + PORT + " is already in use by another process!");
            System.err.println("  Please stop any running server or release port " + PORT + ".");
            System.err.println("==============================================================");
            System.exit(1);
        }

        // Resolve the webapp directory (works from project root)
        String webappDir = resolveWebappDir();
        System.out.println("  Webapp dir  : " + webappDir);
        System.out.println("  Port        : " + PORT);
        System.out.println("  URL         : http://localhost:" + PORT + "/");
        System.out.println("----------------------------------------------");

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(PORT);

        // Set a writable base directory for Tomcat temp files
        File baseDir = new File(System.getProperty("java.io.tmpdir"), "findmystay-tomcat");
        baseDir.mkdirs();
        tomcat.setBaseDir(baseDir.getAbsolutePath());

        // Initialize connector
        Connector connector = tomcat.getConnector();
        if (connector == null) {
            System.err.println("[ERROR] Failed to create Tomcat connector for port " + PORT);
            System.exit(1);
        }

        // Add webapp context at root "/"
        Context ctx = tomcat.addWebapp("", new File(webappDir).getAbsolutePath());

        // Add compiled .class files so servlets are found
        StandardRoot resources = new StandardRoot(ctx);
        File classesDir = new File("build/classes");
        if (classesDir.exists()) {
            resources.addPreResources(
                new DirResourceSet(resources, "/WEB-INF/classes",
                    classesDir.getAbsolutePath(), "/")
            );
            System.out.println("  Classes dir : " + classesDir.getAbsolutePath());
        } else {
            System.err.println("  WARNING: build/classes not found – run compile.bat first!");
        }
        ctx.setResources(resources);

        // Start Tomcat
        try {
            tomcat.start();
        } catch (LifecycleException le) {
            System.err.println("==============================================================");
            System.err.println("  [ERROR] Failed to start Tomcat server: " + le.getMessage());
            System.err.println("==============================================================");
            try {
                tomcat.stop();
                tomcat.destroy();
            } catch (Exception ignored) {}
            System.exit(1);
        }

        // Verify connector initialization succeeded before reporting server started
        if (connector.getState() == LifecycleState.FAILED || !connector.getState().isAvailable()) {
            System.err.println("==============================================================");
            System.err.println("  [ERROR] Tomcat connector initialization failed on port " + PORT + "!");
            System.err.println("  Connector state: " + connector.getState());
            System.err.println("==============================================================");
            try {
                tomcat.stop();
                tomcat.destroy();
            } catch (Exception ignored) {}
            System.exit(1);
        }

        System.out.println("==============================================");
        System.out.println("  Server started! Open your browser:        ");
        System.out.println("  http://localhost:" + PORT + "/             ");
        System.out.println("  (Tip: Hold Ctrl + Click the link above)    ");
        System.out.println("                                              ");
        System.out.println("  Demo Accounts:                             ");
        System.out.println("   Customer : customer@findmystay.com        ");
        System.out.println("             / Customer@123                  ");
        System.out.println("   Owner    : owner@findmystay.com           ");
        System.out.println("             / Owner@123                     ");
        System.out.println("   Admin    : admin@findmystay.com           ");
        System.out.println("             / Admin@123                     ");
        System.out.println("                                              ");
        System.out.println("  Press Ctrl+C to stop.                      ");
        System.out.println("==============================================");

        // Automatically launch the default browser safely (headless & non-desktop safe)
        try {
            if (!GraphicsEnvironment.isHeadless()
                    && Desktop.isDesktopSupported()
                    && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI("http://localhost:" + PORT + "/"));
            }
        } catch (Throwable t) {
            // Safely ignored in headless / non-desktop environments
        }

        // Keep the server running
        tomcat.getServer().await();
    }

    private static boolean isPortAvailable(int port) {
        try (ServerSocket ss = new ServerSocket(port)) {
            ss.setReuseAddress(true);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static String resolveWebappDir() {
        // Try relative path first (running from project root)
        File rel = new File("src/webapp");
        if (rel.exists()) return rel.getAbsolutePath();

        // Fallback: same directory as the JAR
        File jar = new File(EmbeddedServer.class.getProtectionDomain()
            .getCodeSource().getLocation().getPath());
        File parent = jar.getParentFile(); // build/dist or build/classes
        // Go up to project root
        File root = parent.getParentFile().getParentFile();
        File webapp = new File(root, "src/webapp");
        if (webapp.exists()) return webapp.getAbsolutePath();

        // Last resort – current directory
        return "src/webapp";
    }
}
