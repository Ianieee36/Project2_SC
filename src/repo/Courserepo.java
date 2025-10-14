/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package repo;

import model.Course;
import java.util.List;

/**
 *
 * @author christian
 */
public interface CourseRepo {
    void add(model.Course c);
    model.Course find(String code);
    List<Course> search(String query);
    
}
