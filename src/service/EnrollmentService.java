/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import model.Course;
import model.Student;

import java.util.ArrayList;
import java.util.List;
import repo.CourseRepo;
import repo.EnrollmentRepo;
import repo.StudentRepo;

/**
 *
 * @author christian
 */

public class EnrollmentService {
    
    private final StudentRepo students;
    private final CourseRepo courses;
    private final EnrollmentRepo enrollments;
    private int maxCredits = 60;
    
    public EnrollmentService(StudentRepo s, CourseRepo c, EnrollmentRepo e) {
        this.students = s;
        this.courses = c;
        this.enrollments = e;
    }
    
    public void setMaxCredits(int maxCredits) {
        this.maxCredits = maxCredits;
    }
    
    public List<String> validate(String sid, String code) {
        
        List<String> errs = new ArrayList<>();
        
        Student s = students.find(sid);
        if(s == null) errs.add("Unknown student: " + sid);
        
        Course  c = courses.find(code);
        if(c == null) errs.add("Unknown course: " + code);
        if(!errs.isEmpty()) return errs;
        
        if(enrollments.isEnrolled(sid, code)) {
            errs.add("Already enrolled in " + code);
            return errs;

        }
        
        int current = enrollments.listFor(sid).stream()
                .mapToInt(Course::getCreditPoints).sum();
        
        int projected = current + c.getCreditPoints();
        if(projected > maxCredits) {
            errs.add("Credit limit exceeded: " + projected + " > " + maxCredits);
        }
        return errs;
    }
    
    public void enroll(String sid, String code) {
        List<String> errs = validate(sid, code);
        if(!errs.isEmpty()) throw new IllegalArgumentException("Not enrolled in " + code);
        enrollments.add(sid, code);
    }
    
    public void drop(String sid, String code) {
        if(!enrollments.isEnrolled(sid, code)) 
            throw new IllegalArgumentException(String.join(",", code));
        enrollments.drop(sid, code);
    }
    
    public List<Course> list(String sid) {
        if(sid == null || sid.isBlank()) {
            throw new IllegalArgumentException("Student ID required");
        }
        sid = sid.trim();
        var s = students.find(sid);
        if(s == null) {
            throw new IllegalArgumentException("Student: " + sid + " not found");
        }
        return enrollments.listFor(sid);
    }
}
