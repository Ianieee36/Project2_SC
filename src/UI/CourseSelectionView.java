/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UI;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.border.EmptyBorder;
import model.Course;

/**
 *
 * @author christian
 */
public class CourseSelectionView extends JFrame {
    
    private final List<CourseSelectionListener> listeners = new ArrayList<>();
    
    // ImageIcon
    private ImageIcon loadIcon(String cpPath, int w, int h) {
        var url = getClass().getResource(cpPath);
        if(url == null) return null;
        var img = new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
    
    
    // Students Tab
    private final JTextField tfSid = new JTextField(12);
    private final JTextField tfName = new JTextField(18);
    private final JButton btnAddStudent = new JButton("Add Student");
    
    // Courses Tab
    private final JTextField tfCode = new JTextField(10);
    private final JTextField tfTitle = new JTextField(18);
    private final JSpinner spCredits = new JSpinner(new SpinnerNumberModel(15, 1, 15, 1));
    private final JComboBox<String> cbLevel = new JComboBox<>(new String[]{"UG", "PG"});
    private final JButton btnAddCourse = new JButton("Add Course"); 
    
    // Enroll/Drop Tab
    private final JTextField tfESid = new JTextField(12);
    private final JTextField tfECid = new JTextField(12);    
    private final JButton btnEnroll = new JButton("Enroll");
    private final JButton btnDrop = new JButton("Drop");
    
    // Student Courses Tab
    private final JTextField tfListSid = new JTextField(12);
    private final JButton btnList = new JButton("List Course");
    private final JTextArea taCourses = new JTextArea(12, 50);
    
    // Search Tab
    private final JTextField tfQuery = new JTextField(20);
    private final JButton btnSearch = new JButton("Search");
    private final JTable tblResults = new JTable(new DefaultTableModel(
            new Object[]{"Code", "Title", "Credits"}, 0));
    
    
    
    public CourseSelectionView() {
        super("Auckland University of Technology");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JTabbedPane tabs = new JTabbedPane();
        ImageIcon homeTabIcon = loadIcon("/icons/aut.png", 20, 20);
        tabs.addTab("Home", homeTabIcon, buildHomePanel(), "w");
        tabs.setBackground(new Color(243,112,33));
        tabs.addTab("Students", buildStudentsPanel());
        tabs.addTab("Courses", buildCoursesPanel());
        tabs.addTab("Enroll / Drop", buildEnrollPanel());
        tabs.addTab("Student Courses", buildListPanel());
        tabs.addTab("Search Courses", buildSearchPanel());
        setContentPane(tabs);
        tabs.setSelectedIndex(0);
        
        // wiring of buttons to Listeners
        
        btnAddStudent.addActionListener(e ->
                listeners.forEach(l -> l.onAddStudent(
                        tfSid.getText().trim(), tfName.getText().trim())));
        
        btnAddCourse.addActionListener(e ->
                listeners.forEach(l -> l.onAddCourse(
                        tfCode.getText().trim(),
                        tfTitle.getText().trim(),
                        ((Number) spCredits.getValue()).intValue(),
                        String.valueOf(cbLevel.getSelectedItem()))));
        
        btnEnroll.addActionListener(e ->
                listeners.forEach(l -> l.onEnroll(
                        tfESid.getText().trim(), tfECid.getText().trim())));
                
        btnDrop.addActionListener(e ->
                listeners.forEach(l -> l.onDrop(
                        tfESid.getText().trim(), tfECid.getText().trim())));

        btnList.addActionListener(e ->
                listeners.forEach(l -> l.onListCourses(tfListSid.getText().trim())));
        
        btnSearch.addActionListener(e -> 
                listeners.forEach(l -> l.onSearchCourses(tfQuery.getText().trim())));
        
        taCourses.setEditable(false);
        tblResults.setFillsViewportHeight(true);
            
    }
    
    public void addListener(CourseSelectionListener l) {
            listeners.add(l);
        }
    
    // View feedback helpers
    
    public void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public void showStudentCourses(String sid, List<Course> courses) {
        StringBuilder sb = new StringBuilder(sid).append(" Enrolled Courses").append(":\n");
        if(courses == null || courses.isEmpty()) sb.append("(none)\n");
        else {
            for(Course c : courses) {
                sb.append(" - ").append(c.getCode())
                  .append(" | ").append(c.getTitle())
                  .append(" | ").append(c.getCreditPoints()).append(" pts\n");
            }
        }
        taCourses.setText(sb.toString());
    }
    
    public void showSearchResults(List<Course> courses) {
        DefaultTableModel m = (DefaultTableModel) tblResults.getModel();
        m.setRowCount(0);
        if(courses != null) {
            for(Course c : courses) {
                m.addRow(new Object[]{c.getCode(), c.getTitle(), c.getCreditPoints()});
            }
        }
    }
    
    // Panels
    
    private JComponent buildHomePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(0, 0, 0, 0));
//        p.setBackground(new Color(243,112,33));
        
        
        ImageIcon aut = loadIcon("/icons/aut.png", 128, 128);
        JLabel wc = new JLabel(     
             "<html><div style='text-align:center'>"
           + "<h1> Welcome to Auckland University of Technology</h1>"
                     + "</div></html>", 
                aut, JLabel.LEFT
        );
        
        wc.setIconTextGap(10);
        wc.setVerticalAlignment(JLabel.TOP);
        wc.setHorizontalAlignment(JLabel.CENTER);
        
        var url = getClass().getResource("/icons/ktw.jpg");
        
        ImageIcon banner = null;
        if(url != null) {
            var img = new ImageIcon(url).getImage().getScaledInstance(1200, 500, Image.SCALE_SMOOTH);
            banner = new ImageIcon(img);
        }
        JLabel bn = new JLabel(banner); 
        bn.setHorizontalAlignment(SwingConstants.CENTER);
        if(banner != null) {
            bn.setIcon(banner);
        } else {
            bn.setText("Banner not found (check path /icons/ktw.jpg)");
            bn.setForeground(Color.RED);
        }
        bn.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(wc);
        center.add(bn);
        
        
        p.add(center, BorderLayout.CENTER);
        
        return p;
        
    }
    private JPanel buildStudentsPanel() {
        JPanel s = new JPanel(new GridBagLayout());
        GridBagConstraints g = gbc();
        g.gridx=0; g.gridy=0; s.add(new JLabel("Student ID:"), g);
        g.gridx=1; s.add(tfSid, g);
        g.gridx=0; g.gridy=1; s.add(new JLabel("Student Name:"), g);
        g.gridx=1; s.add(tfName, g);
        g.gridx=1; g.gridy=2; s.add(btnAddStudent, g);
        return s;
        
    }
    
    private JPanel buildCoursesPanel() {
        JPanel c = new JPanel(new GridBagLayout());
        GridBagConstraints g = gbc();
        g.gridx=0; g.gridy=0; c.add(new JLabel("Course Code:"), g);
        g.gridx=1; c.add(tfCode, g);
        g.gridx=0; g.gridy=1; c.add(new JLabel("Course Title:"), g);
        g.gridx=1; c.add(tfTitle, g);
        g.gridx=0; g.gridy=2; c.add(new JLabel("Credits:"), g);
        g.gridx=1; c.add(spCredits, g);
        g.gridx=0; g.gridy=3; c.add(new JLabel("Level:"), g);
        g.gridx=1; c.add(cbLevel, g);
        g.gridx=1; g.gridy=4; c.add(btnAddCourse, g);
        return c;
        
    }
    
    private JPanel buildEnrollPanel() {
        JPanel e = new JPanel(new GridBagLayout());
        GridBagConstraints g = gbc();
        g.gridx=0; g.gridy=0; e.add(new JLabel("Student ID:"), g);
        g.gridx=1; e.add(tfESid, g);
        g.gridx=0; g.gridy=1; e.add(new JLabel("Course Code:"), g);
        g.gridx=1; e.add(tfECid, g);
        g.gridx=1; g.gridy=2; e.add(btnEnroll, g);
        g.gridy=3; e.add(btnDrop, g);
        return e;
        
    }
    
    private JPanel buildListPanel() {
        JPanel l = new JPanel(new BorderLayout(8, 8));
        JPanel top = new JPanel();  
        top.add(new JLabel("Student ID:"));
        top.add(tfListSid);
        top.add(btnList);
        l.add(top, BorderLayout.NORTH);
        l.add(new JScrollPane(taCourses), BorderLayout.CENTER);
        return l;  
    }
    
    private JPanel buildSearchPanel() {
        JPanel s = new JPanel(new BorderLayout(8, 8));
        JPanel top = new JPanel();  
        top.add(new JLabel("Course:"));
        top.add(tfQuery);
        top.add(btnSearch);
        s.add(top, BorderLayout.NORTH);
        s.add(new JScrollPane(tblResults), BorderLayout.CENTER);
        return s; 
    }
    
    
    
    
    private static GridBagConstraints gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6,6,6,6);
        g.anchor = GridBagConstraints.WEST;
        return g;
    }
}
