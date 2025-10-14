/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package repo;

/**
 *
 * @author christian
 */
public interface StudentRepo {
    void add(model.Student s);
    model.Student find(String id);
}
