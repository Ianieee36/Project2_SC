/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repo.derby;

import org.junit.After;
import org.junit.Before;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author christian
 */
public abstract class DerbyRepoTest {
    
    protected String memUrl;
    private String dbName;
    
    @Before
    public void setUp() throws Exception {
        dbName = "MEM_" + getClass().getSimpleName() + "_" + System.nanoTime();
        memUrl = "jdbc:derby:memory:" + dbName + ";create=true";
        
        System.setProperty("app.db.url", memUrl);
        repo.db.DerbyManager._test_reset();
        
        repo.db.Schema.ensure();
    }
    
    @After
    public void tearDown() throws Exception {
        
        repo.db.DerbyManager._test_reset();
        
        String dropUrl = "jdbc:derby:memory:" + dbName + ";drop=true";
        try{
            DriverManager.getConnection(dropUrl);
        } catch(SQLException e) {
            if(!"08006".equals(e.getSQLState()) && !"08003".equals(e.getSQLState()) && !"XJ004".equals(e.getSQLState())) 
                throw e;
        }
        System.clearProperty("app.db.url");
    }
    
}
