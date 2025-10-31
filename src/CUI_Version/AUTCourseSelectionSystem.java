/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package CUI_Version;

import model.Course;
import model.Student;

import repo.StudentRepo;
import repo.CourseRepo;
import repo.EnrollmentRepo;


import service.EnrollmentService;

import java.util.List;
import java.util.Scanner;

/**
 *
 * @author christian
 */
public class AUTCourseSelectionSystem {

    private static final Scanner in = new Scanner(System.in);
    
    // Repos + Service available to menu handlers
    private static StudentRepo studentRepo;
    private static CourseRepo courseRepo;
    private static EnrollmentService svc;
    
    public static void main(String[] args) {
        
        // I Wired CSV backend; CourseFiles that handles autosave/autoload
        CourseFiles filesSvc = new CourseFiles();
        
        studentRepo = new CsvStudent(filesSvc);
        courseRepo = new CsvCourse(filesSvc);
        EnrollmentRepo enrollRepo = new CsvEnrollment(filesSvc);
        svc = new EnrollmentService(studentRepo, courseRepo, enrollRepo);
        svc.setMaxCredits(60);
        
        System.out.println("=== AUCKLAND UNIVERSITY OF TECHNOLOGY ===");
        while (true) {
            try {
                printMenu();
                int choice = readInt("Enter choice: ");
                switch (choice) {
                    case 1 -> addStudent();
                    case 2 -> addCourse();
                    case 3 -> enroll();
                    case 4 -> drop();
                    case 5 -> listStudentCourses();
                    case 6 -> {System.out.println("AUCKLAND UNIVERSITY OF TECHNOLOGY "); return; }
                    default -> System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
          System.out.println();  
        } 
    }
    
    private static void addStudent() {
        String id = readNonBlank("Student ID: ");
        String name = readNonBlank("Student Name: ");
        studentRepo.add(new Student(id, name, null));
        System.out.println(name + " Your Student ID is: " + id);
    }
    
    public static void addCourse() {
        String code = readNonBlank("Course code: ");
        String title = readNonBlank("Course title: ");
        int credits = readPositiveInt("Credit Points: ");
        String level = readNonBlank("Level (UG/PG): ").toUpperCase();
        
        Course c = switch (level) {
            case "PG" -> new model.PostgraduateCourse(code, title, credits);
            case "UG" -> new model.UndergraduateCourse(code, title, credits);
            default   -> throw new IllegalArgumentException("Invalid: choose either UG/PG");
        };
        
        courseRepo.add(c);
        System.out.println("Added course: " + code.toUpperCase() + " [" + level + "]");
    }
    
    private static void enroll() {
        String sid = readNonBlank("Student ID: ");
        String code = readNonBlank("Course code: ");
        var errs = svc.validate(sid, code);
        if(!errs.isEmpty()) {
            System.out.println("Cannot enroll: " + String.join("; ", errs));
            return;
        }
        svc.enroll(sid, code);
        System.out.println("Enrolled in " + code.toUpperCase());
    }
    
    private static void drop() {
        String sid = readNonBlank("Student ID: ");
        String code = readNonBlank("Course code: ");
        svc.drop(sid, code);
        System.out.println("Student officially drop from " + code.toUpperCase());
    }
    
    private static void listStudentCourses() {
        String sid = readNonBlank("Student ID: ");
        List<Course> courses = svc.list(sid);
        if(courses.isEmpty()) {
            System.out.println("No courses enrolled.");
            return;
        }
        
        System.out.println("Courses for " + sid + ":");
        for(Course c : courses) {
            System.out.println(" - " + c.getCode() + " | " + c.getTitle() + " | " + c.getCreditPoints() + " pts");
        }
    } 
    
    private static void printMenu() {
        System.out.println("""
                           
            -----AUT - ENROLLMENT - SYSTEM-----
                           
            1) Add a Student
            2) Add a Course
            3) Enroll 
            4) Drop a Course
            5) Student Course Information
            6) Exit   
                           
            """);
    }
    
    // small input helpers
    
    private static String readNonBlank(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = in.nextLine();
            if (s != null && !s.isBlank()) {
                return s.trim();
            }
            System.out.println("Input Required.");
        }
    }
    
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(in.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter an integer.");
            }
        }
    }
    
    private static int readPositiveInt(String prompt) {
        while (true) {
            int v = readInt(prompt);
            if (v > 0) {
                return v;
            }
            System.out.println("Invalid input.");
        }
    }
}
