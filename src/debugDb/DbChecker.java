package debugDb;

import java.sql.*;
import repo.db.DerbyManager;

public class DbChecker {
    public static void main(String[] args) throws Exception {
        try (Connection c = DerbyManager.get()) {
            System.out.println("[URL] " + c.getMetaData().getURL());
            System.out.println("[Schema] current=" + c.getSchema());

            // List all tables in AUT
            try (PreparedStatement ps = c.prepareStatement("""
                SELECT t.tablename
                  FROM sys.systables t
                  JOIN sys.sysschemas s ON s.schemaid=t.schemaid
                 WHERE s.schemaname=?
                 ORDER BY t.tablename
            """)) {
                ps.setString(1, "AUT");
                try (ResultSet rs = ps.executeQuery()) {
                    System.out.println("Tables in AUT:");
                    while (rs.next()) System.out.println(" - " + rs.getString(1));
                }
            }

            // Does AUT.COURSE exist?
            System.out.println("COURSE exists? " + tableExists(c, "AUT", "COURSE"));
            // Peek a few rows just to validate that my database stores data
            dumpCourses(c);
        }
    }

    static boolean tableExists(Connection c, String schema, String table) throws SQLException {
        try (ResultSet rs = c.getMetaData().getTables(
                null, schema.toUpperCase(), table.toUpperCase(), new String[]{"TABLE"})) {
            return rs.next();
        }
    }

    static void dumpCourses(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("""
                SELECT CODE, TITLE, CREDITS, LEVEL
                  FROM AUT.COURSE
                 ORDER BY CODE
                 FETCH FIRST 10 ROWS ONLY
        """);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("AUT.COURSE:");
            while (rs.next()) {
                System.out.printf(" %s | %s | %d | %s%n",
                        rs.getString(1), rs.getString(2), rs.getInt(3), rs.getString(4));
            }
        } catch (SQLException e) {
            if ("42X05".equals(e.getSQLState())) System.out.println("COURSE table not found.");
            else throw e;
        }
    }
}