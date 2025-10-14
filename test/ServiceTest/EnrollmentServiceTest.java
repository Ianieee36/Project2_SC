package ServiceTest;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */



import model.Student;
import model.Course;
import model.UndergraduateCourse;

import org.junit.Before;
import org.junit.Test;



import service.EnrollmentService;

import java.util.*;

import static org.junit.Assert.*;
import repo.CourseRepo;
import repo.EnrollmentRepo;
import repo.StudentRepo;




/**
 *
 * @author christian
 */

// Unit test for EnrollmentService using in-memory fake repos(no I/O).

public class EnrollmentServiceTest {
    
    private InMemStudentrepo students;
    private InMemCourserepo courses;
    private InMemEnrollmentrepo enrolls;
    private EnrollmentService svc;
    
    @Before
    public void setup() {
        students = new InMemStudentrepo();
        courses  = new InMemCourserepo();
        enrolls  = new InMemEnrollmentrepo(courses);
        svc      = new EnrollmentService(students, courses, enrolls);
        
        // Baseline data
        students.add(new Student("S1", "Ann", null));
        courses.add(new UndergraduateCourse("COMP101", "Intro", 15));
    }
    
    @Test
    public void enroll_success() {
        assertTrue(svc.validate("S1", "COMP101").isEmpty());
        svc.enroll("S1", "COMP101");
        assertEquals(1, svc.list("S1").size());
        assertEquals("COMP101", svc.list("S1").get(0).getCode());
    }
    
    @Test
    public void duplicate_rejected() {
        svc.enroll("S1", "COMP101");
        var errs = svc.validate("S1", "COMP101");
        assertTrue(errs.stream().anyMatch(m -> m.contains("Already enrolled")));
        assertThrows(IllegalArgumentException.class, () -> svc.enroll("S1", "COMP101"));
    }
    
    @Test
    public void credit_limit_blocks_when_exceeded() {
        // Add two more courses, enroll to hit the limit
        courses.add(new UndergraduateCourse("COMP102", "A", 15));
        courses.add(new UndergraduateCourse("COMP103", "B", 15));
        svc.enroll("S1", "COMP101");
        svc.enroll("S1", "COMP102");
        svc.setMaxCredits(30);
        var errs = svc.validate("S1", "COMP103");
        assertTrue(errs.stream().anyMatch(m -> m.contains("Credit limit exceeded")));
    }
    
    @Test
    public void  drop_removes_course() {
        svc.enroll("S1", "COMP101");
        svc.drop("S1", "COMP101");
        assertTrue(svc.list("S1").isEmpty());
    }
    
    @Test 
    public void limit_allows_when_equal() {
        svc.setMaxCredits(30);
        courses.add(new UndergraduateCourse("COMP102", "A", 15));
        svc.enroll("S1", "COMP101");
        var errs = svc.validate("S1", "COMP102");
        assertTrue(errs.toString(), errs.isEmpty());
    }
    
    @Test
    public void unknown_student_and_course_reported() {
        List<String> errs = svc.validate("NOPE", "NOCOURSE");
        assertTrue(errs.stream().anyMatch(m -> m.contains("Unknown student")));
    }
    
    @Test
    public void drop_non_enrolled_throws() {
        try {
            svc.drop("S1", "COMP102");
            fail("expected");
        } catch (IllegalArgumentException e) {
            
        }
    }
     
    // In-memory fake repos 
    
    static class InMemStudentrepo implements StudentRepo {
        final Map<String, Student> m = new HashMap<>();
        @Override public Student find(String id) {
            return m.get(id);
        }
        @Override public void add(Student s) {
            m.put(s.getId(), s);
        }
    }
    
    static class InMemCourserepo implements CourseRepo {
        final Map<String, Course> m = new HashMap<>();
        @Override public Course find(String code) {
            return m.get(code == null ? null : code.toUpperCase()); 
        }
        @Override 
        public void add(Course c) {
            m.put(c.getCode().toUpperCase(), c);
        }
        @Override
        public List<Course> search(String q) {
            return new ArrayList<>(m.values());
        }
    }
    
    static class InMemEnrollmentrepo implements EnrollmentRepo {
        final Map<String, Set<String>> data = new HashMap<>(); // sid set of course codes
        final CourseRepo courses;
        InMemEnrollmentrepo(CourseRepo courses) {
            this.courses = courses;
        }
        
        @Override
        public boolean isEnrolled(String sid, String code) {
            return data.getOrDefault(sid, Collections.emptySet()).contains(code.toUpperCase());
        }
        
        @Override
        public void add(String sid, String code) {
            data.computeIfAbsent(sid, k -> new HashSet<>()).add(code.toUpperCase());
            // I do not recalculate credits here; Enrollment service uses students.getTotalCredits()
            // in my CSV implementations. For this in-memory tests focuses on behavior, not credit math.)
        }
        
        @Override
        public void drop(String sid, String code) {
            data.getOrDefault(sid, Collections.emptySet()).remove(code.toUpperCase());
        }
        
        @Override
        public List<Course> listFor(String sid) {
            List<Course> out = new ArrayList<>();
            for(String code : data.getOrDefault(sid, Collections.emptySet())) {
                Course c = courses.find(code);
                if(c != null) out.add(c);
            }
            return out;
        }
    }
}
