/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ui;

/**
 *
 * @author christian
 */
public interface CourseSelectionListener {
    void onAddStudent(String id, String name);
    void onAddCourse(String code, String title, int credits, String level);
    void onEnroll(String studentId, String courseCode);
    void onDrop(String studentId, String courseCode);
    void onListCourses(String studentId);
    void onSearchCourses(String query, String progFilter);
}
