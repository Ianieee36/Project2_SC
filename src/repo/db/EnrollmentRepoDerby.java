/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repo.db;

import model.Course;
import model.UndergraduateCourse;
import model.PostgraduateCourse;
import repo.EnrollmentRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author christian
 */
public class EnrollmentRepoDerby implements EnrollmentRepo {

    // Qualify tables (safer than relying on default schema)
    private static final String T_ENR = "AUT.ENROLLMENT";
    private static final String T_CRS = "AUT.COURSE";

    private static final String SQL_IS_ENROLLED =
            "SELECT 1 FROM " + T_ENR + " WHERE SID=? AND CCODE=?";

    private static final String SQL_ENROLL =
            "INSERT INTO " + T_ENR + " (SID, CCODE) VALUES (?, ?)";

    private static final String SQL_DROP =
            "DELETE FROM " + T_ENR + " WHERE SID=? AND CCODE=?";

    private static final String SQL_LIST_FOR =
            "SELECT c.CODE, c.TITLE, c.CREDITS, c.LEVEL " +
            "FROM " + T_ENR + " e " +
            "JOIN " + T_CRS + " c ON c.CODE = e.CCODE " +
            "WHERE e.SID = ? " +
            "ORDER BY c.CODE";

    public EnrollmentRepoDerby() {
        Schema.ensure(); // ok to keep; it’s a no-op after first run
    }
    
    @Override
    public boolean isEnrolled(String sid, String code) {
        try (PreparedStatement ps = DerbyManager.get().prepareStatement(SQL_IS_ENROLLED)) {
            ps.setString(1, sid.trim());
            ps.setString(2, normalize(code));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void add(String sid, String code) {
        try (PreparedStatement ps = DerbyManager.get().prepareStatement(SQL_ENROLL)) {
            ps.setString(1, sid.trim());
            ps.setString(2, normalize(code));
            ps.executeUpdate();
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) { // duplicate PK (SID, CCODE)
                throw new IllegalArgumentException("Already enrolled");
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public void drop(String sid, String code) {
        try (PreparedStatement ps = DerbyManager.get().prepareStatement(SQL_DROP)) {
            ps.setString(1, sid.trim());
            ps.setString(2, normalize(code));
            int n = ps.executeUpdate();
            if (n == 0) throw new IllegalArgumentException("Not enrolled in " + code);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Course> listFor(String sid) {
        List<Course> out = new ArrayList<>();
        try (PreparedStatement ps = DerbyManager.get().prepareStatement(SQL_LIST_FOR)) {
            ps.setString(1, sid.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String code   = rs.getString(1);
                    String title  = rs.getString(2);
                    int credits   = rs.getInt(3);
                    String lvl    = rs.getString("LEVEL"); // "UG"/"PG"
                    Course c = "PG".equalsIgnoreCase(lvl)
                            ? new PostgraduateCourse(code, title, credits)
                            : new UndergraduateCourse(code, title, credits);
                    
                    out.add(c);
                }
            }            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    private static String normalize(String code) {
        return code == null ? null : code.trim().toUpperCase();
    }
}