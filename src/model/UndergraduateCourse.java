/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author christian
 */
public class UndergraduateCourse implements Course {
    private final String code;
    private final String title;
    private final int creditPoints;
    
    public UndergraduateCourse(String code, String title, int creditPoints) {
        if(code == null || code.isBlank()) {
            throw new IllegalArgumentException("Code required ");
        }
        if(title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title required ");
        }
        if(creditPoints <= 0) throw new IllegalArgumentException("Positive Integer ");
        this.code = code.trim();
        this.title = title.trim();
        this.creditPoints = creditPoints;
    }
    
    @Override
    public String getCode() {
        return code;
    }
    
    @Override
    public String getTitle() {
        return title;
    }
    
    @Override
    public int getCreditPoints() {
        return creditPoints;
    }
    
    @Override
    public String toString() {
        return "[UnderGraduateProgramme]: " + code + " - " + title + " (" + creditPoints + ") "; 
    }
    
}
