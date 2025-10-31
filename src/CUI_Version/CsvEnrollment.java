/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CUI_Version;

import CUI_Version.CourseFiles;
import model.Course;

import java.util.List;
import repo.EnrollmentRepo;

/**
 *
 * @author christian
 */
public final class CsvEnrollment implements EnrollmentRepo{
    
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
    public void drop(String studentId, String courseCode) {
        svc.dropCourse(studentId, courseCode);
    }
    
    @Override public List<Course> listFor(String sid) {
        return svc.displayCourses(sid);
    }
}
