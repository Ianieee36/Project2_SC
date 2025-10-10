/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package wrapperModel;

import files.CourseFiles;
import model.Course;
import model.Student;

import java.util.List;

/**
 *
 * @author christian
 */
public class csModel {
    
    private final CourseFiles svc;
    
    csModel(CourseFiles svc) {
        this.svc = svc;
    }
    
    public void addStudent(String id, String name) {
        svc.addStudent(new Student(id, name, null));
    }
    
    public void addCourse(Course c) {
        svc.addCourse(c);
    }
    
    public void enroll(String studentId, String courseCode) {
        svc.enrollingCourse(studentId, courseCode);
    }
    
    public void drop(String studentId, String courseCode) {
        svc.dropCourse(studentId, courseCode);
    }
    
    public List<Course> listStudentCourses(String studentId) {
        return svc.displayCourses(studentId);
    }
    
    
    
    
}
