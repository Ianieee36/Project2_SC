/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repo.db;

import java.sql.*;

/**
 *
 * @author christian
 */
public final class DerbyManager {
    private static final String EMBEDDED_URL =
        "jdbc:derby:" + System.getProperty("user.home") + "/Project2_CSS/aut_DB;create=true";
    private static final String CLIENT_URL =
        "jdbc:derby://localhost:1527/aut_DB;create=true";

    private static volatile Connection CONN;

    private DerbyManager() {}

    public static Connection get() {
        if(CONN != null) return CONN;
        
        synchronized (DerbyManager.class) {
            if(CONN != null) return CONN;
            
            String override = System.getProperty("app.db.url");
            if(override != null && !override.isBlank()) {
                try {
                    CONN = DriverManager.getConnection(override);
                    CONN.setAutoCommit(true);
                    System.out.println("[DB] override url -> " + override);
                    return CONN;
                } catch (SQLException first) {
                    try {
                        CONN = DriverManager.getConnection(override, "aut", "aut");
                        CONN.setAutoCommit(true);
                        System.out.println("[DB] override url (with user) -> " + override);
                        return CONN;
                    } catch (SQLException second) {
                        throw new RuntimeException("Could not connect to override DB: " + override, second);
                    }
                }
            }
            
            try {
                CONN = DriverManager.getConnection(EMBEDDED_URL, "aut", "aut");
                CONN.setAutoCommit(true);
                System.out.println("[DB] Embedded mode -> " + EMBEDDED_URL);
                return CONN;
            } catch (SQLException e) {
                try {
                    CONN = DriverManager.getConnection(CLIENT_URL, "aut", "aut");
                    CONN.setAutoCommit(true);
                    System.out.println("[DB] Client mode -> " + CLIENT_URL);
                    return CONN;
                } catch (SQLException e2) {
                    throw new RuntimeException("Could not obtain a Derby connection", e2);
                }
            }
        }
    }
    
    // TEST ONLY: allow resetting the cached connection between test classes
    public static void _test_reset() {
        synchronized (DerbyManager.class) {
            if(CONN != null) {
                try{ CONN.close(); } catch (SQLException ignore) {}
                CONN = null;
            }
        }
    }
}
    
    