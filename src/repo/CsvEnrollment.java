/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repo;

import files.CourseFiles;
import model.Student;
import model.Course;
import repo.Enrollment;

import java.util.List;
/**
 *
 * @author christian
 */
public final class CsvEnrollment implements Enrollment{
    
    private final CourseFiles svc;
    
    public CsvEnrollment(CourseFiles svc) {
        this.svc = svc;
    }
    
    @Override 
    public boolean isEnrolled(String sid, String code) {
        return svc.displayCourses(sid).stream().anyMatch(c -> c.getCode().equalsIgnoreCase(code));
    }
    
    @Override
    public void add(String sid, String code) {
        svc.enrollingCourse(sid, code);
    }
    
    @Override
    public void drop(String sid, String code) {
        svc.dropCourse(sid, code);
    }
    
    @Override public List<Course> listFor(String sid) {
        return svc.displayCourses(sid);
    }
}
