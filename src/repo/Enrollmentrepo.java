/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package repo;

/**
 *
 * @author christian
 */
public interface EnrollmentRepo {
    boolean isEnrolled(String sid, String code);
    void add(String sid, String code);
    void drop(String sid, String code);
    java.util.List<model.Course> listFor(String sid);
}
