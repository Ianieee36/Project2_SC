/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CUI_Version;



import service.EnrollmentService;

import model.Student;
import model.Course;

import java.util.List;
import repo.CourseRepo;
import repo.EnrollmentRepo;
import repo.StudentRepo;

/**
 *
 * @author christian
 */
public class repoServiceTest {
    public static void main(String[] args) {
        // Wiring (CSV backend via my CourseFiles auto-save/auto-load)
        
        CourseFiles filesSvc = new CourseFiles(); // this uses my ./data by default
        StudentRepo students = new CsvStudent(filesSvc);
        CourseRepo courses = new CsvCourse(filesSvc);
        EnrollmentRepo enrolls = new CsvEnrollment(filesSvc);
        EnrollmentService svc = new EnrollmentService(students, courses, enrolls);
        
        // I just used a unique test IDs for this testing.
        String sid = "TEST_001";
        String code = "COMP_TEST_101";
        
        try{
            // Ensure test course & student exists
            if(courses.find(code) == null) {
                courses.add(new model.UndergraduateCourse(code, "Test Intro", 15));
                System.out.println("[ADD COURSE] " + code);
            }
            else {
                System.out.println("[COURSE EXISTS] " + code);
            }
            
            if(students.find(sid) == null) {
                students.add(new Student(sid, "Test User", null));
                System.out.println("[ADD STUDENT" + sid);
            }
            else {
                System.out.println("[STUDENT EXISTS] " + sid);
            }
            
            // Valid + enroll
            List<String> errs = svc.validate(sid, code);
            if(!errs.isEmpty()) {
                System.out.println("[VALIDATE] cannot enroll: " + String.join("; ", errs));
            }
            else {
                svc.enroll(sid, code);
                System.out.println("[ENROLL] " + sid + " -> " + code);
            }
            
            // List after enroll
            System.out.println("[LIST AFTER ENROLL]");
            for(Course c : svc.list(sid)) {
                System.out.println(" - " + c.getCode() + " | " + c.getTitle() + " | " + c.getCreditPoints());
            }
            
            // Check duplicate validation
            errs = svc.validate(sid, code);
            System.out.println("[VALIDATE DUPLICATE] " + (errs.isEmpty() ? "[]" : String.join("; ", errs)));
            
            // Drop
            svc.drop(sid, code);
            System.out.println("[DROP] " + sid + " - " + code);
            
            // List after drop
            System.out.println("[LIST AFTER DROP]");
            for(Course c : svc.list(sid)) {
                System.out.println(" - " + c.getCode() + " | " + c.getTitle() + " | " + c.getCreditPoints());
            }
            
            System.out.println("[SERVICE TEST COMPLETED]");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[SERVICE TEST FAILED " + e.getMessage());
        }
        
    }
}
