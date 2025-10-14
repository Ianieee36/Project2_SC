/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repo;

/**
 *
 * @author christian
 */
public final class CsvStudent implements StudentRepo {
    
    private final files.CourseFiles svc;
    
    public CsvStudent(files.CourseFiles svc) {
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
