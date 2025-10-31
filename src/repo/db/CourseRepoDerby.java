/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repo.db;

import model.Course;
import model.UndergraduateCourse;
import model.PostgraduateCourse;
import repo.CourseRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author christian
 */
public class CourseRepoDerby implements CourseRepo {
    
    public CourseRepoDerby() {
        Schema.ensure();
    }
    
    private static Course map(String code, String title, int credits, String lvl) {
        return "PG".equalsIgnoreCase(lvl)
            ? new PostgraduateCourse(code, title, credits)
            : new UndergraduateCourse(code, title, credits);
    }
    
    @Override
    public Course find(String code) {
        if(code == null) return null;
        try(PreparedStatement ps = DerbyManager.get().prepareStatement(
            "SELECT CODE, TITLE, CREDITS, LEVEL FROM COURSE WHERE CODE=?")) {
            ps.setString(1, code.toUpperCase());
            try(ResultSet rs = ps.executeQuery()) {
                if(!rs.next()) return null;
                return map(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getString(4));
            }
        } catch(SQLException e) { throw new RuntimeException(e); }
    }
    
    @Override
    public void add(Course c) {
        String lvl = (c instanceof PostgraduateCourse) ? "PG" : "UG";
        try(PreparedStatement ps = DerbyManager.get().prepareStatement(
            "INSERT INTO COURSE(CODE, TITLE, CREDITS, LEVEL) VALUES(?,?,?,?)")) {
            ps.setString(1, c.getCode().toUpperCase());
            ps.setString(2, c.getTitle());
            ps.setInt(3, c.getCreditPoints());
            ps.setString(4, lvl);
            ps.executeUpdate();
        } catch (SQLException e) {
            if("23505".equals(e.getSQLState()))
                throw new IllegalArgumentException("Course code already exists: " + c.getCode());
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public List<Course> search(String query) {
        String like = "%" + (query == null ? "" : query.trim().toUpperCase()) + "%";
        String sql = """
                     SELECT CODE, TITLE, CREDITS, LEVEL
                     FROM COURSE
                     WHERE UPPER(CODE) LIKE ? OR UPPER(TITLE) LIKE ?
                     ORDER BY CODE
                     """;
        List<Course> out = new ArrayList<>();
        try(PreparedStatement ps = DerbyManager.get().prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    out.add(map(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getString(4)));
                }
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
}
