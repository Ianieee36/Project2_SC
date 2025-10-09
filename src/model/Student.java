/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Objects;

/**
 *
 * @author christian
 */
public final class Student {
    
    private final String id;
    private final String name;
    private final List<Course> enrolledCourses;
    
    public Student(String id, String name, List<Course> enrolledCourses) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Error: Invalid ID ");
        } 
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Error: Invalid Name ");
        } 
        this.id = id.trim();
        this.name = name.trim();
        this.enrolledCourses = new ArrayList<>();
    }
    
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    
    public List<Course> getEnrolledCourses() {
        return Collections.unmodifiableList(enrolledCourses);
    }
    
    public boolean enrollCourses(Course course) {
        Objects.requireNonNull(course, "course");
        if (hasCourse(course.getCode())) {
            return false;
        }
        return enrolledCourses.add(course);
    }
    
    public boolean dropCourses(Course course) {
        Objects.requireNonNull(course, "course");
        return dropCoursesByCode(course.getCode());
        
    }
    
    public boolean dropCoursesByCode(String courseCode) {
        if(courseCode == null || courseCode.isBlank()) {
            return false;
        } 
        return enrolledCourses.removeIf(x -> x.getCode().equalsIgnoreCase(courseCode));
    }
    
    public boolean hasCourse(String courseCode) {
        if (courseCode == null || courseCode.isBlank()) {
            return false;
        }
        for (Course x : enrolledCourses) {
            if (x.getCode().equalsIgnoreCase(courseCode)) {
                return true;
            }
        }
        
        return false;
    }
       
    public int getTotalCredits() {
        int sum = 0;
        for (Course x : enrolledCourses) {
            sum += x.getCreditPoints();
        }
        return sum;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Student other)) {
            return false;
        }
        return id.equalsIgnoreCase(other.id);
    }
    
    @Override
    public int hashCode() {
        return id.toLowerCase().hashCode();
    }
    
    /**
    @Override
    public String toString() {
        return String.format(
                "Student Course Information:%nID: %s, Student Name: %s, Courses: %d",
                id, name, enrolledCourses.size()
        );
    }
    */

}