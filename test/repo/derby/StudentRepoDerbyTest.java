/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package repo.derby;

import model.Student;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author christian
 */
public class StudentRepoDerbyTest extends DerbyRepoTest {
    
    @Test
    public void add_and_find_student() {
        var repo = new repo.db.StudentRepoDerby();
        
        repo.add(new Student("S100", "Ana Smith", null));
        
        var found = repo.find("S100");
        assertNotNull(found);
        assertEquals("S100", found.getId());
        assertEquals("Ana Smith", found.getName());
    }
    
    @Test
    public void duplicate_id_rejected() {
        var repo = new repo.db.StudentRepoDerby();
        repo.add(new Student("S200", "Ben Lee", null));
        
        try{
            repo.add(new Student("S200", "Ben Lee 2", null));
            fail("Expected duplicate key to be rejected");
        } catch (IllegalArgumentException ex) {
             
            var cause = ex.getCause();
            if(cause instanceof java.sql.SQLException) {
                assertEquals("23505", ((java.sql.SQLException) cause).getSQLState());
            }
        }
    }
    
    
}
