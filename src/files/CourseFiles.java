package files;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import model.Course;
import model.Student;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets; // NEW
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths; // NEW
import java.nio.file.StandardCopyOption; // NEW

import java.util.*;
      
/**
 * 
 *
 * @author christian
 */
public class CourseFiles {
    
    private final Map<String, Student> students = new HashMap<>();
    private final Map<String, Course> courses = new HashMap<>();
    
    // === UPDATE: autosave config ===
    private final Path dataDir;                         // where CSV files live
    private final boolean autoSave;                     
    private final Object ioLock = new Object();         // simple write guard
    
    public CourseFiles() {
        this(Paths.get("data"), true);
    }
    
    public CourseFiles(Path dataDir, boolean autoSave) {
        this.dataDir = Objects.requireNonNull(dataDir, "dataDir");
        this.autoSave = autoSave;
        ensureDir();
        // Auto-load existing files (if any)
        try {
            loadFiles(this.dataDir);
            System.out.println();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    
    private void ensureDir() {
        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            throw new RuntimeException("Cannot create data directory: " + dataDir, e);
        }
    }
    
    // NEW: persist current state to disk (atomic, best-effort)
    private void persist() {
        if(!autoSave) return;
        synchronized(ioLock) {
            try {
                saveFiles(this.dataDir);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
    
    public void addStudent(Student s) {
        Objects.requireNonNull(s, "student");
        if (students.containsKey(s.getId())) {
            throw new IllegalArgumentException("Student ID already exists: " + s.getId());
        }
        students.put(s.getId(), s);
        persist();
    }
    
    public void addCourse(Course c) {
        Objects.requireNonNull(c, "course"); 
        String key = normalizedCode(c.getCode());
        if(courses.containsKey(key)) {
            throw new IllegalArgumentException("Course Code already exists: " + c.getCode());
        }
        courses.put(key, c);
        persist();
    }
    
    public void enrollingCourse(String studentId, String courseCode) {
        Student s = requireStudent(studentId);
        Course c = requireCourse(courseCode);
        
        if (s.hasCourse(c.getCode())) {
            throw new IllegalArgumentException("Student already enrolled " + c.getCode());
        }
        s.enrollCourses(c);
        persist();
    }
    
    public void dropCourse(String studentId, String courseCode) {
        Student s = requireStudent(studentId);
        String code = normalizedCode(courseCode);
        
        // This makes the students mutate its own list
        boolean removed = s.dropCoursesByCode(courseCode);
        if(!removed) {
            throw new IllegalArgumentException("Student not enrolled in " + courseCode);
        }
        persist(); // keep autosave
    }
    
    public List<Course> displayCourses(String studentId) {
        Student s = requireStudent(studentId);
        return s.getEnrolledCourses();
    }
    
    public Collection<Student> allStudents() {
        return Collections.unmodifiableCollection(students.values());
    }
    
    public Collection<Course> allCourses() {
        return Collections.unmodifiableCollection(courses.values());
    }
    
    
    /* 
    I used an ai tool to help me with this method 
    due to my lack of experience in I/O files i generated 
    part of this method.
    */
    public void saveFiles(Path folder) throws IOException { 
        Objects.requireNonNull(folder, "folder");
        Files.createDirectories(folder);
        
        writeAtomic(folder.resolve("students.csv"), bw -> {
            for(Student s : students.values()) {
                bw.write(escape(s.getId()) + "," + escape(s.getName()));
                bw.newLine();
            }
        });
        
        writeAtomic(folder.resolve("courses.csv"), bw -> {
            for(Course c : courses.values()) {
                String type = (c instanceof model.PostgraduateCourse) ? "PG" : "UG";
                bw.write(escape(c.getCode()) + "," + escape(c.getTitle()) + "," + c.getCreditPoints() + "," + type);
                bw.newLine();
            }
        });
        
        writeAtomic(folder.resolve("enrollment.csv"), bw -> {
            for (Student s : students.values()) {
                for(Course c : s.getEnrolledCourses()) {
                    bw.write(escape(s.getId()) + "," + escape(c.getCode()));
                    bw.newLine();
                }
            }
        });
    }
    
    public void loadFiles(Path folder) throws IOException {
        Objects.requireNonNull(folder, "folder");
        students.clear();
        courses.clear();
        
        Path loadStudents = folder.resolve("students.csv");
        if(Files.exists(loadStudents)) {
            try(BufferedReader br = Files.newBufferedReader(loadStudents, StandardCharsets.UTF_8)) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = splitCsv(line, 2);
                    String id = unescape(parts[0]);
                    String name = unescape(parts[1]);
                    students.put(id, new Student(id, name, null));
                }
            }
        }
        
        Path loadCourses = folder.resolve("courses.csv");
        if(Files.exists(loadCourses)) {
            try(BufferedReader br = Files.newBufferedReader(loadCourses, StandardCharsets.UTF_8)) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = splitCsv(line, 4);
                    if (parts.length < 3) continue;
                    
                    String code = unescape(parts[0]);
                    String title = unescape(parts[1]);
                    int credits = Integer.parseInt(parts[2]);
                    String type = parts[3].trim().toUpperCase();
                    
                    Course course;
                    switch (type) {
                            case "UG" -> course = new model.UndergraduateCourse(code, title, credits);
                            case "PG" -> course = new model.PostgraduateCourse(code, title, credits);
                            default   -> throw new IllegalArgumentException("Unknown course type: " + type + " for code " + code);
                    }
                    courses.put(normalizedCode(code), course);    
                }
            }
        }
        
        Path loadEnrollments = folder.resolve("enrollment.csv");
        if(Files.exists(loadEnrollments)) {
            try(BufferedReader br = Files.newBufferedReader(loadEnrollments, StandardCharsets.UTF_8)) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = splitCsv(line, 2);
                    String studentId = unescape(parts[0]);
                    String courseCode = unescape(parts[1]);
                    
                    Student s = students.get(studentId);
                    Course c = courses.get(normalizedCode(courseCode));
                    
                    if(s != null && c != null && !s.hasCourse(c.getCode())) {
                        s.enrollCourses(c);
                    }
                }
            }
        }
    }
    
    // ============ HELPERS ============
    
    private Student requireStudent(String studentId) {
        Student s = students.get(studentId);
        if(s == null) {
            throw new IllegalArgumentException("ID Required" + studentId );
        }
        return s;
    }
    
    private Course requireCourse(String courseCode) {
        Course c = courses.get(normalizedCode(courseCode));
        if(c == null) {
            throw new IllegalArgumentException("Course Code Required" + courseCode);
        }
        return c;
    }
    
    private static String normalizedCode(String code) {
        return Objects.requireNonNull(code, "courseCode").trim().toUpperCase();
    }
    
    private static String escape(String s) {
        if(s == null) {
            return "";
        }
        boolean needsQuotes = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String out = s.replace("\"", "\"\"");
        return needsQuotes ? "\"" + out + "\"" : out;
    }
    
    private static String unescape(String s) {
        s = s.trim();
        if(s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
            s = s.substring(1, s.length() - 1).replace("\"\"", "\"");
        } 
        return s;
    }
    
    /*
    I asked for help from an AI tool in this method
    
    */
    private static String[] splitCsv(String line, int expectedParts) {
        
        List<String> parts = new ArrayList<>(expectedParts);
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if(inQuotes) {
                if(ch == '"') {
                    if(i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        cur.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    cur.append(ch);
                }
            } else {
                if(ch == ',') {
                    parts.add(cur.toString());
                    cur.setLength(0);
                } else if (ch == '"') {
                    inQuotes = true;
                } else {
                    cur.append(ch);
                }
            }
        }
        parts.add(cur.toString());
        if(parts.size() != expectedParts) {
            throw new IllegalArgumentException("Malformed CSV line: " + line);
        }
        return parts.toArray(new String[0]);
    }
    
    // NEW: atomic write helper 
    
    private static void writeAtomic(Path target, WriterConsumer writer) throws IOException {
        Path tmp = target.resolveSibling(target.getFileName() + ".tmp");
        try(BufferedWriter out = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
            writer.accept(out);
        }
        try {
            Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (java.nio.file.AtomicMoveNotSupportedException e) {
            Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            try {
                Files.deleteIfExists(tmp);
            } catch (Exception ignored) {}
        }
    }
 }
