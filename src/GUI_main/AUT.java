/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI_main;

import javax.swing.SwingUtilities;

// Repositories and CSV
import repo.StudentRepo;
import repo.CourseRepo;
import repo.EnrollmentRepo;

// Repositories for DB
import repo.db.*;

//

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
            Schema.ensure();
            
            StudentRepo sRepo = new StudentRepoDerby();
            CourseRepo cRepo = new CourseRepoDerby();
            EnrollmentRepo eRepo = new EnrollmentRepoDerby();
            
            EnrollmentService svc = new EnrollmentService(sRepo, cRepo, eRepo);
            svc.setMaxCredits(60);
            
            // UI + Controller
            CourseSelectionView view = new CourseSelectionView();
            new CourseSelectionController(svc, sRepo, cRepo, view);
            view.setVisible(true);
        });
    }
}
