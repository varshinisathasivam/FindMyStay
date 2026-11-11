package com.findmystay.server;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;

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

        // Resolve the webapp directory (works from project root)
        String webappDir = resolveWebappDir();
        System.out.println("  Webapp dir  : " + webappDir);
        System.out.println("  Port        : " + PORT);
        System.out.println("  URL         : http://localhost:" + PORT + "/");
        System.out.println("----------------------------------------------");

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(PORT);

        // Required to trigger connector creation
        tomcat.getConnector();

        // Set a writable base directory for Tomcat temp files
        File baseDir = new File(System.getProperty("java.io.tmpdir"), "findmystay-tomcat");
        baseDir.mkdirs();
        tomcat.setBaseDir(baseDir.getAbsolutePath());

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
        tomcat.start();
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

        // Automatically launch the default browser
        try {
            if (java.awt.Desktop.isDesktopSupported() && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                java.awt.Desktop.getDesktop().browse(new java.net.URI("http://localhost:" + PORT + "/"));
            }
        } catch (Exception ignored) {}

        // Keep the server running
        tomcat.getServer().await();
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

