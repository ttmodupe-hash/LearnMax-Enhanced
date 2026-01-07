package com.learnmax.manager;

import com.learnmax.model.Student;
import java.util.*;
import java.io.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

/**
 * Improved StudentManager with:
 * - Data persistence (JSON-based)
 * - Enhanced search capabilities
 * - Duplicate prevention
 * - Bulk operations
 */
public class StudentManager {
    private ArrayList<Student> students;
    private static final String DATA_FILE = "student_data.json";
    private Gson gson;
    
    public StudentManager() {
        students = new ArrayList<>();
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(java.time.LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        loadData();
    }
    
    /**
     * Add a student with duplicate checking
     */
    public boolean addStudent(Student student) {
        // Check for duplicate ID
        if (findStudentById(student.getId()) != null) {
            return false; // Student with this ID already exists
        }
        
        students.add(student);
        saveData();
        return true;
    }
    
    /**
     * Remove a student by ID
     */
    public boolean removeStudent(String id) {
        Student student = findStudentById(id);
        if (student != null) {
            students.remove(student);
            saveData();
            return true;
        }
        return false;
    }
    
    /**
     * Find student by exact ID
     */
    public Student findStudentById(String id) {
        if (id == null) return null;
        
        for (Student s : students) {
            if (s.getId().equals(id)) {
                return s;
            }
        }
        return null;
    }
    
    /**
     * Search students by name (partial match, case-insensitive)
     */
    public List<Student> searchByName(String nameQuery) {
        List<Student> results = new ArrayList<>();
        
        if (nameQuery == null || nameQuery.trim().isEmpty()) {
            return results;
        }
        
        String query = nameQuery.toLowerCase().trim();
        
        for (Student s : students) {
            if (s.getName().toLowerCase().contains(query)) {
                results.add(s);
            }
        }
        
        return results;
    }
    
    /**
     * Get students by grade level
     */
    public List<Student> getStudentsByGrade(String gradeLevel) {
        List<Student> results = new ArrayList<>();
        
        for (Student s : students) {
            if (s.getGradeLevel().equalsIgnoreCase(gradeLevel)) {
                results.add(s);
            }
        }
        
        return results;
    }
    
    /**
     * Get underperforming students (overall average < threshold)
     */
    public List<Student> getUnderperformingStudents(double threshold) {
        List<Student> results = new ArrayList<>();
        
        for (Student s : students) {
            double avg = s.calculateOverallAverage();
            if (avg >= 0 && avg < threshold) {
                results.add(s);
            }
        }
        
        // Sort by average (lowest first)
        results.sort(Comparator.comparingDouble(Student::calculateOverallAverage));
        
        return results;
    }
    
    /**
     * Get top performing students
     */
    public List<Student> getTopPerformers(int count) {
        List<Student> studentsWithScores = new ArrayList<>();
        
        // Filter students with assessment data
        for (Student s : students) {
            if (s.calculateOverallAverage() >= 0) {
                studentsWithScores.add(s);
            }
        }
        
        // Sort by average (highest first)
        studentsWithScores.sort((s1, s2) -> 
            Double.compare(s2.calculateOverallAverage(), s1.calculateOverallAverage()));
        
        // Return top N
        return studentsWithScores.subList(0, Math.min(count, studentsWithScores.size()));
    }
    
    /**
     * Get all students
     */
    public ArrayList<Student> getAllStudents() {
        return new ArrayList<>(students); // Return copy to prevent external modification
    }
    
    /**
     * Get total number of students
     */
    public int getStudentCount() {
        return students.size();
    }
    
    /**
     * Get class average across all students
     */
    public double getClassAverage() {
        if (students.isEmpty()) return -1;
        
        double total = 0;
        int count = 0;
        
        for (Student s : students) {
            double avg = s.calculateOverallAverage();
            if (avg >= 0) {
                total += avg;
                count++;
            }
        }
        
        return count > 0 ? total / count : -1;
    }
    
    /**
     * Generate class performance report
     */
    public String generateClassReport() {
        StringBuilder report = new StringBuilder();
        
        report.append("═══════════════════════════════════════════════════════════════\n");
        report.append("                    CLASS PERFORMANCE REPORT\n");
        report.append("═══════════════════════════════════════════════════════════════\n\n");
        
        report.append("Total Students: ").append(getStudentCount()).append("\n");
        
        double classAvg = getClassAverage();
        if (classAvg >= 0) {
            report.append("Class Average: ").append(String.format("%.2f%%", classAvg)).append("\n\n");
        } else {
            report.append("Class Average: No data available\n\n");
        }
        
        // Performance distribution
        int excellent = 0, good = 0, needsImprovement = 0, poor = 0, noData = 0;
        
        for (Student s : students) {
            double avg = s.calculateOverallAverage();
            if (avg < 0) {
                noData++;
            } else if (avg >= 90) {
                excellent++;
            } else if (avg >= 75) {
                good++;
            } else if (avg >= 50) {
                needsImprovement++;
            } else {
                poor++;
            }
        }
        
        report.append("Performance Distribution:\n");
        report.append("  Excellent (90-100):        ").append(excellent).append(" students\n");
        report.append("  Good (75-89):              ").append(good).append(" students\n");
        report.append("  Needs Improvement (50-74): ").append(needsImprovement).append(" students\n");
        report.append("  Requires Attention (<50):  ").append(poor).append(" students\n");
        report.append("  No Assessment Data:        ").append(noData).append(" students\n\n");
        
        // Top performers
        List<Student> topPerformers = getTopPerformers(5);
        if (!topPerformers.isEmpty()) {
            report.append("Top Performers:\n");
            for (int i = 0; i < topPerformers.size(); i++) {
                Student s = topPerformers.get(i);
                report.append(String.format("  %d. %s (ID: %s) - %.2f%%\n", 
                    i + 1, s.getName(), s.getId(), s.calculateOverallAverage()));
            }
            report.append("\n");
        }
        
        // Students needing support
        List<Student> underperforming = getUnderperformingStudents(50);
        if (!underperforming.isEmpty()) {
            report.append("Students Requiring Additional Support:\n");
            for (Student s : underperforming) {
                report.append(String.format("  • %s (ID: %s) - %.2f%%\n", 
                    s.getName(), s.getId(), s.calculateOverallAverage()));
            }
        }
        
        report.append("═══════════════════════════════════════════════════════════════\n");
        
        return report.toString();
    }
    
    /**
     * Save data to JSON file
     */
    public void saveData() {
        try (FileWriter writer = new FileWriter(DATA_FILE)) {
            gson.toJson(students, writer);
            System.out.println("✓ Data saved successfully to " + DATA_FILE);
        } catch (IOException e) {
            System.err.println("✗ Error saving data: " + e.getMessage());
        }
    }
    
    /**
     * Load data from JSON file
     */
    public void loadData() {
        File file = new File(DATA_FILE);
        
        if (!file.exists()) {
            System.out.println("No existing data file found. Starting with empty student list.");
            return;
        }
        
        try (FileReader reader = new FileReader(DATA_FILE)) {
            Type studentListType = new TypeToken<ArrayList<Student>>(){}.getType();
            ArrayList<Student> loadedStudents = gson.fromJson(reader, studentListType);
            
            if (loadedStudents != null) {
                students = loadedStudents;
                System.out.println("✓ Data loaded successfully: " + students.size() + " students");
            }
        } catch (IOException e) {
            System.err.println("✗ Error loading data: " + e.getMessage());
        }
    }
    
    /**
     * Export data to CSV format
     */
    public boolean exportToCSV(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Header
            writer.println("Student ID,Name,Grade Level,Subject,Latest Score,Average,Assessment Count");
            
            // Data
            for (Student s : students) {
                for (String subject : s.getSubjects()) {
                    writer.printf("%s,%s,%s,%s,%d,%.2f,%d\n",
                        s.getId(),
                        s.getName(),
                        s.getGradeLevel(),
                        subject,
                        s.getLatestScore(subject),
                        s.calculateSubjectAverage(subject),
                        s.getAssessmentsBySubject(subject).size()
                    );
                }
            }
            
            System.out.println("✓ Data exported to " + filename);
            return true;
        } catch (IOException e) {
            System.err.println("✗ Error exporting data: " + e.getMessage());
            return false;
        }
    }
}

/**
 * Custom adapter for LocalDateTime serialization/deserialization
 */
class LocalDateTimeAdapter implements JsonSerializer<java.time.LocalDateTime>, 
                                      JsonDeserializer<java.time.LocalDateTime> {
    
    @Override
    public JsonElement serialize(java.time.LocalDateTime dateTime, Type type, 
                                JsonSerializationContext context) {
        return new JsonPrimitive(dateTime.toString());
    }
    
    @Override
    public java.time.LocalDateTime deserialize(JsonElement json, Type type, 
                                               JsonDeserializationContext context) {
        return java.time.LocalDateTime.parse(json.getAsString());
    }
}
