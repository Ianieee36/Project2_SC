/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package repo.derby;

import model.PostgraduateCourse;
import model.UndergraduateCourse;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author christian
 */
public class CourseRepoDerbyTest extends DerbyRepoTest {
    
    @Test 
    public void add_and_find_courses_with_levels() {
        var repo = new repo.db.CourseRepoDerby();
        
        repo.add(new UndergraduateCourse("COMP101", "Intro to CS", 15));
        repo.add(new PostgraduateCourse("COMP801", "Advance Topics", 30));
        
        var ug = repo.find("COMP101");
        assertNotNull(ug);
        assertTrue(ug instanceof UndergraduateCourse);
        assertEquals(15, ug.getCreditPoints());
        
        var pg = repo.find("COMP801");
        assertNotNull(pg);
        assertTrue(pg instanceof PostgraduateCourse);
        assertEquals(30, pg.getCreditPoints());
    }
    
    @Test
    public void duplicate_code_rejected() {
        var repo = new repo.db.CourseRepoDerby();
        repo.add(new model.UndergraduateCourse("ENSE600", "Soft. Construction", 15));
        
        try{
            repo.add(new model.UndergraduateCourse("ENSE600", "Something Else", 15));
            fail("Expected duplicate code to be rejected");
        } catch(IllegalArgumentException ex) {
            var cause = ex.getCause();
            if(cause instanceof java.sql.SQLException) {
                assertEquals("23505", ((java.sql.SQLException) cause).getSQLState());
            }
        } 
    }
       
}
