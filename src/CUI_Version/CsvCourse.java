/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CUI_Version;

import CUI_Version.CourseFiles;
import model.Course;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import repo.CourseRepo;

/**
 *
 * @author christian
 */
public final class CsvCourse implements CourseRepo {
    
    private final CourseFiles svc;
    
    public CsvCourse(CourseFiles svc) {
        this.svc = svc;
    }
    
    @Override
    public void add(Course c) {
        svc.addCourse(c);
    }
    
    @Override 
    public Course find(String code) {
        if(code == null) return null;
        String target = code.trim().toUpperCase();
        for(Course c : svc.allCourses()) {
            if(c.getCode().trim().toUpperCase().equals(target)) {
                return c;
            }
        }
        return null;
    }
    
    @Override
    public List<Course> search(String query) {
        String q = (query == null ? "" : query.trim().toUpperCase());
        List<Course> out = new ArrayList<>();
        for(Course c : svc.allCourses()) {
            if(c.getCode().toUpperCase().contains(q) ||
                    c.getTitle().toUpperCase().contains(q)) {
                out.add(c);
            }
        }
        out.sort(Comparator.comparing(c -> c.getCode().toUpperCase()));
        return out;
    }
}
