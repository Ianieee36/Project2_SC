/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package CUI_main;

import model.Course;
import model.Student;
import files.CourseFiles;

import java.util.List;
import java.util.Scanner;

/**
 *
 * @author christian
 */
public class AUTCourseSelectionSystem {

    private static final Scanner in = new Scanner(System.in);
    // Auto-loads from ./data and auto-saves after each change
    private static final CourseFiles svc = new CourseFiles();
    
    public static void main(String[] args) {
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
        svc.addStudent(new Student(id, name, null));
        System.out.println(name + " Your Student ID is: " + id);
    }
    
    public static void addCourse() {
        String code = readNonBlank("Course code: ");
        String title = readNonBlank("Course title: ");
        int credits = readPositiveInt("Credit Points: ");
        String level = readNonBlank("Level (UG/PG): ").toUpperCase();
        
        Course c;
        switch (level) {
            case "PG": c = new model.PostgraduateCourse(code, title, credits); break;
            case "UG": c = new model.UndergraduateCourse(code, title, credits); break;
            default:       throw new IllegalArgumentException("Invalid: choose either UG/PG");
        }
        
        svc.addCourse(c);
        System.out.println("Added course: " + code.toUpperCase() + " [" + level + "]");
    }
    
    private static void enroll() {
        String sid = readNonBlank("Student ID: ");
        String code = readNonBlank("Course code: ");
        svc.enrollingCourse(sid, code);
        System.out.println("You've been successfully been enrolled in " + code);
    }
    
    private static void drop() {
        String sid = readNonBlank("Student ID: ");
        String code = readNonBlank("Course code: ");
        svc.dropCourse(sid, code);
        System.out.println("Student is officially been dropped.");
    }
    
    private static void listStudentCourses() {
        String sid = readNonBlank("Student ID: ");
        List<Course> courses = svc.displayCourses(sid);
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
