/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CUI_Version;

import repo.StudentRepo;

/**
 *
 * @author christian
 */
public final class CsvStudent implements StudentRepo {
    
    private final CUI_Version.CourseFiles svc;
    
    public CsvStudent(CUI_Version.CourseFiles svc) {
        this.svc = svc;
    }
    
    @Override
    public void add(model.Student s) {
        svc.addStudent(s);
    }
    
    @Override
    public model.Student find(String id) {
        return svc.allStudents().stream().filter(st->st.getId().equals(id)).findFirst().orElse(null);
    }
}
