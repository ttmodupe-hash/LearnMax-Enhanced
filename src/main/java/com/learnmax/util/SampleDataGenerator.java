package com.learnmax.util;

import com.learnmax.manager.StudentManager;
import com.learnmax.model.Student;

import java.util.Random;

/**
 * Utility class to generate sample data for demonstration purposes
 */
public class SampleDataGenerator {
    
    private static final String[] FIRST_NAMES = {
        "John", "Jane", "Michael", "Sarah", "David", "Emma", "James", "Olivia",
        "Robert", "Sophia", "William", "Isabella", "Joseph", "Mia", "Thomas",
        "Charlotte", "Daniel", "Amelia", "Matthew", "Harper", "Christopher",
        "Evelyn", "Andrew", "Abigail", "Joshua", "Emily", "Ryan", "Elizabeth"
    };
    
    private static final String[] LAST_NAMES = {
        "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller",
        "Davis", "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez",
        "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin"
    };
    
    private static final String[] SUBJECTS = {
        "Mathematics", "Science", "English", "History", "Geography",
        "Life Sciences", "Physical Sciences", "Accounting", "Economics"
    };
    
    private static final String[] ASSESSMENT_TYPES = {
        "Test", "Quiz", "Assignment", "Exam", "Project"
    };
    
    private static final String[] GRADES = {
        "Grade 8", "Grade 9", "Grade 10", "Grade 11", "Grade 12"
    };
    
    private static final Random random = new Random();
    
    /**
     * Generate sample students with assessments
     */
    public static void generateSampleData(StudentManager manager, int studentCount) {
        System.out.println("Generating " + studentCount + " sample students...");
        
        for (int i = 1; i <= studentCount; i++) {
            String id = String.format("S%03d", i);
            String name = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)] + " " +
                         LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            String grade = GRADES[random.nextInt(GRADES.length)];
            
            Student student = new Student(id, name, grade);
            
            // Add 3-7 subjects per student
            int subjectCount = 3 + random.nextInt(5);
            String[] selectedSubjects = selectRandomSubjects(subjectCount);
            
            for (String subject : selectedSubjects) {
                // Add 2-5 assessments per subject
                int assessmentCount = 2 + random.nextInt(4);
                
                // Generate assessments with realistic progression
                int baseScore = 40 + random.nextInt(40); // Base score 40-80
                
                for (int j = 0; j < assessmentCount; j++) {
                    // Add some variation and slight improvement trend
                    int variation = -10 + random.nextInt(21); // -10 to +10
                    int improvement = j * 2; // Slight improvement over time
                    int score = Math.max(0, Math.min(100, baseScore + variation + improvement));
                    
                    String assessmentType = ASSESSMENT_TYPES[random.nextInt(ASSESSMENT_TYPES.length)];
                    
                    try {
                        student.addAssessment(subject, score, assessmentType);
                        // Small delay to ensure different timestamps
                        Thread.sleep(1);
                    } catch (Exception e) {
                        System.err.println("Error adding assessment: " + e.getMessage());
                    }
                }
            }
            
            manager.addStudent(student);
            
            if (i % 10 == 0) {
                System.out.println("  Generated " + i + " students...");
            }
        }
        
        System.out.println("✓ Sample data generation complete!");
        System.out.println("  Total students: " + manager.getStudentCount());
        System.out.println("  Class average: " + String.format("%.2f%%", manager.getClassAverage()));
    }
    
    /**
     * Select random subjects without duplicates
     */
    private static String[] selectRandomSubjects(int count) {
        String[] selected = new String[count];
        boolean[] used = new boolean[SUBJECTS.length];
        
        for (int i = 0; i < count; i++) {
            int index;
            do {
                index = random.nextInt(SUBJECTS.length);
            } while (used[index]);
            
            used[index] = true;
            selected[i] = SUBJECTS[index];
        }
        
        return selected;
    }
    
    /**
     * Main method for standalone execution
     */
    public static void main(String[] args) {
        StudentManager manager = new StudentManager();
        
        // Generate 30 sample students
        generateSampleData(manager, 30);
        
        // Print summary
        System.out.println("\n" + manager.generateClassReport());
        
        System.out.println("\nSample data has been saved to student_data.json");
        System.out.println("You can now run the GUI application to view the data.");
    }
}
