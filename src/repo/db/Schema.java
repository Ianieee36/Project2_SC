/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repo.db;

import java.sql.*;

/**
 *
 * @author christian
 */
public final class Schema {
    
    private Schema() {}
    
    public static synchronized void ensure() {
        
       // Ensure AUT schema exists and is current
        createSchemaIfNeeded("AUT");
        setSchema("AUT");
        
        create("CREATE TABLE STUDENT ("
                + "ID   VARCHAR(32)  PRIMARY KEY, "
                + "NAME VARCHAR(128) NOT NULL)");
        
        create("CREATE TABLE COURSE ("
                + "CODE    VARCHAR(16)  PRIMARY KEY, "
                + "TITLE   VARCHAR(160) NOT NULL, "
                + "CREDITS INT          NOT NULL, "
                + "LEVEL     CHAR(2)      NOT NULL)");
        
        create("CREATE TABLE ENROLLMENT (" +
               "SID   VARCHAR(32) NOT NULL, " + 
               "CCODE VARCHAR(16) NOT NULL, " +
               "PRIMARY KEY (SID, CCODE), " +
               "CONSTRAINT FK_ENR_STU FOREIGN KEY (SID)   REFERENCES STUDENT(ID), " +
               "CONSTRAINT FK_ENR_CRS FOREIGN KEY (CCODE) REFERENCES COURSE(CODE))"
               );
   
    }
    
    private static void createSchemaIfNeeded(String schema) {
        try(Statement st = DerbyManager.get().createStatement()) {
            st.executeUpdate("CREATE SCHEMA " + schema);
        } catch(SQLException e) {
            if(!"X0Y68".equals(e.getSQLState())) {
                throw new RuntimeException("Create schema failed", e);
            }
        }
    }
    
    private static void setSchema(String schema) {
        try(Statement st = DerbyManager.get().createStatement()) {
            st.executeUpdate("SET SCHEMA " + schema);
        } catch(SQLException e) {
            throw new RuntimeException("SET SCHEMA failed", e);
        }
    }
    
    private static void create(String ddl) {
        try(java.sql.Statement st = DerbyManager.get().createStatement()) {
            st.execute(ddl);
        } catch (java.sql.SQLException e) {
            // X0Y32 = "Table/View already exists" 
            String s = e.getSQLState();
            if("X0Y32".equals(s) || "X0Y68".equals(s)) {
                return;
            }
            throw new RuntimeException("DDL failed (SQLState " + s + "): " + ddl, e);
        }
    }
}



