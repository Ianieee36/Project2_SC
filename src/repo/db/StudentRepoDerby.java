/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repo.db;

import model.Student;
import repo.StudentRepo;
import java.sql.*;

/**
 *
 * @author christian
 */
public class StudentRepoDerby implements StudentRepo {
    
    public StudentRepoDerby() { 
        Schema.ensure();
    }
    
    @Override
    public Student find(String id) {
        if (id == null) return null;
        try(PreparedStatement ps = DerbyManager.get().prepareStatement(
            "SELECT ID, NAME FROM STUDENT WHERE ID=?")) {
            ps.setString(1, id);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) return new Student(rs.getString(1), rs.getString(2), null);
                return null;
            }
        } catch(SQLException e) { throw new RuntimeException(e); }
    } 
    
    @Override
    public void add(Student s) {
        try(PreparedStatement ps = DerbyManager.get().prepareStatement(
            "INSERT INTO STUDENT(ID, NAME) VALUES(?,?)")) {
            ps.setString(1, s.getId());
            ps.setString(2, s.getName());
            ps.executeUpdate();
        } catch(SQLException e) { 
            if("23505".equals(e.getSQLState()))
                throw new IllegalArgumentException("Student ID already exists: " + s.getId());
            throw new RuntimeException(e);
        }
    }   
}
