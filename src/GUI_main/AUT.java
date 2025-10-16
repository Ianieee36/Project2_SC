/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI_main;

import javax.swing.SwingUtilities;

import files.CourseFiles;

// Repositories and CSV
import repo.StudentRepo;
import repo.CourseRepo;
import repo.EnrollmentRepo;
import repo.CsvStudent;
import repo.CsvCourse;
import repo.CsvEnrollment;

import service.EnrollmentService;
import UI.CourseSelectionView;
import Controller.CourseSelectionController;

/**
 *
 * @author christian
 */
public class AUT {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Backend wiring (CSV Storage via CourseFiles with auto-load/save)
            CourseFiles csv = new CourseFiles();
            
            StudentRepo sRepo = new CsvStudent(csv);
            CourseRepo cRepo = new CsvCourse(csv);
            EnrollmentRepo eRepo = new CsvEnrollment(csv);
            
            EnrollmentService svc = new EnrollmentService(sRepo, cRepo, eRepo);
            svc.setMaxCredits(60);
            
            // UI + Controller
            CourseSelectionView view = new CourseSelectionView();
            new CourseSelectionController(svc, sRepo, cRepo, view);
            view.setVisible(true);
        });
    }
}
