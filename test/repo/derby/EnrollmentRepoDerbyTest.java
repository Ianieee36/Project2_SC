/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repo.derby;


import model.UndergraduateCourse;
import model.Student;

import repo.db.StudentRepoDerby;
import repo.db.CourseRepoDerby;
import repo.db.EnrollmentRepoDerby;

import org.junit.Before;
import org.junit.Test;

import java.util.stream.Collectors;

import static org.junit.Assert.*;





/**
 *
 * @author christian
 */
public class EnrollmentRepoDerbyTest extends DerbyRepoTest {
    
    @Test
    public void enroll_isEnrolled_then_drop() {
        var sRepo = new StudentRepoDerby();
        var cRepo = new CourseRepoDerby();
        var eRepo = new EnrollmentRepoDerby();
        
        // arrange
        sRepo.add(new Student("S1", "Test Student", null));
        cRepo.add(new UndergraduateCourse("COMP101", "Intro", 15));
        
        assertFalse(eRepo.isEnrolled("S1", "COMP101"));
        
        eRepo.add("S1", "COMP101");
        
        assertTrue(eRepo.isEnrolled("S1", "COMP101"));
        
        assertEquals(1, eRepo.listFor("S1").size());
        
        eRepo.drop("S1", "COMP101");
        
        assertFalse(eRepo.isEnrolled("S1", "COMP101"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void duplicate_enroll_is_rejected() {
        var sRepo = new StudentRepoDerby();
        var cRepo = new CourseRepoDerby();
        var eRepo = new EnrollmentRepoDerby();
        
        sRepo.add(new Student("S2", "Duplicate Student", null));
        cRepo.add(new UndergraduateCourse("COMP102", "DSA", 15));
        
        eRepo.add("S2", "COMP102");
        eRepo.add("S2", "COMP102");
    }
}
