/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import ui.CourseSelectionView;
import ui.CourseSelectionListener;

import service.EnrollmentService;
import repo.StudentRepo;
import repo.CourseRepo;
import model.Course;
import model.Student;
import model.PostgraduateCourse;

import java.util.List;



/**
 *
 * @author christian
 */
public class CourseSelectionController implements CourseSelectionListener {
    
    private final EnrollmentService svc;
    private final StudentRepo students;
    private final CourseRepo courses;
    private final CourseSelectionView view;
    
    public CourseSelectionController(EnrollmentService svc,
                                     StudentRepo students,
                                     CourseRepo courses,
                                     CourseSelectionView view) {
        this.svc = svc;
        this.students = students;
        this.courses = courses;
        this.view = view;
        
        // Registering this controller as my view's controller
        this.view.addListener(this);
    }
    
    @Override
    public void onAddStudent(String id, String name) {
        try {
            if(id == null || id.isBlank() || name == null || name.isBlank()) {
                throw new IllegalArgumentException("ID and name are Required");
            }
            students.add(new Student(id.trim(), name.trim(), null));
            view.showInfo("Added Student " + id.trim());
        } catch(Exception ex) {
            view.showError(ex.getMessage());
        }
    }
    
    @Override
    public void onAddCourse(String code, String title, int credits, String level) {
        try {
            if(code == null || code.isBlank() || title == null | title.isBlank()) {
                throw new IllegalArgumentException("Course code and title are required");
            }
            if(credits <= 0) {
                throw new IllegalArgumentException("Credits must be positive");
            }
            String lvl = level == null ? "" : level.trim().toUpperCase();
            Course c = switch(lvl) {
                case "UG" -> new model.UndergraduateCourse(code.trim(), title.trim(), credits);
                case "PG" -> new model.PostgraduateCourse(code.trim(), title.trim(), credits);
                default   -> throw new IllegalArgumentException("Level must be UG or PG.");
            };
            courses.add(c);
            view.showInfo("Added course " + code.trim().toUpperCase());
        } catch (Exception ex) {
            view.showError(ex.getMessage());
        }
    }
    
    @Override
    public void onEnroll(String studentId, String courseCode) {
        try {
            String sid = studentId == null ? "" : studentId.trim();
            String code = courseCode == null ? "" : courseCode.trim();
            
            List<String> errs = svc.validate(sid, code);
            if(!errs.isEmpty()) {
                view.showError(String.join("\n", errs));
                return;
            }
            svc.enroll(sid, code);
            view.showInfo("Enrolled " + sid + " in " + code.toUpperCase());
        } catch (Exception ex) {
            view.showError(ex.getMessage());
        }
    }
    
    @Override
    public void onDrop(String studentId, String courseCode) {
        try {
            String sid = studentId == null ? "" : studentId.trim();
            String code = courseCode == null ? "" : courseCode.trim();
            svc.drop(sid ,code);
            view.showInfo("Dropped " + sid + " from " + code.toUpperCase());
        } catch (Exception ex) {
            view.showError(ex.getMessage());
        }
    }
    
    @Override
    public void onListCourses(String studentId) {
        String sid = studentId == null ? "" : studentId.trim();
        if(sid.isEmpty()) {
            view.showError("Student ID required");
            return;
        }
        try{    
            List<Course> list = svc.list(sid);
            view.showStudentCourses(sid, list);
        } catch(IllegalArgumentException ex) {
            view.showError(ex.getMessage());
        } catch(Exception ex) {
            view.showError("Not found " + ex.getMessage());
        }
    }
    
    @Override
    public void onSearchCourses(String query, String progFilter) {
        try {
        List<Course> found = courses.search(query);
        if (!"All".equalsIgnoreCase(progFilter)) {
            found = found.stream()
                    .filter(c -> ("PG".equalsIgnoreCase(progFilter) && c instanceof PostgraduateCourse)
                              || ("UG".equalsIgnoreCase(progFilter) && !(c instanceof PostgraduateCourse)))
                    .toList();
            }
            view.showSearchResults(found);
        } catch (Exception ex) {
            view.showError(ex.getMessage());
        }
    }
 }
