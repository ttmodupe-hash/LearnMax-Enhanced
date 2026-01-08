package com.learnmax;

import com.learnmax.manager.StudentManager;
import com.learnmax.model.Student;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for StudentManager class
 */
class StudentManagerTest {
    
    private StudentManager manager;
    private static final String TEST_DATA_FILE = "test_student_data.json";
    
    @BeforeEach
    void setUp() {
        // Delete test data file if it exists
        File testFile = new File(TEST_DATA_FILE);
        if (testFile.exists()) {
            testFile.delete();
        }
        
        manager = new StudentManager(false); // Don't auto-load data for testing
    }
    
    @AfterEach
    void tearDown() {
        // Clean up test data file
        File testFile = new File(TEST_DATA_FILE);
        if (testFile.exists()) {
            testFile.delete();
        }
    }
    
    // ==================== ADD STUDENT TESTS ====================
    
    @Test
    @DisplayName("Should add student successfully")
    void testAddStudent() {
        Student student = new Student("S001", "John Doe", "Grade 10");
        assertTrue(manager.addStudent(student));
        assertEquals(1, manager.getStudentCount());
    }
    
    @Test
    @DisplayName("Should not add duplicate student")
    void testAddDuplicateStudent() {
        Student student1 = new Student("S001", "John Doe", "Grade 10");
        Student student2 = new Student("S001", "Jane Doe", "Grade 11");
        
        assertTrue(manager.addStudent(student1));
        assertFalse(manager.addStudent(student2));
        assertEquals(1, manager.getStudentCount());
    }
    
    @Test
    @DisplayName("Should add multiple students")
    void testAddMultipleStudents() {
        Student student1 = new Student("S001", "John Doe", "Grade 10");
        Student student2 = new Student("S002", "Jane Smith", "Grade 11");
        Student student3 = new Student("S003", "Bob Johnson", "Grade 10");
        
        manager.addStudent(student1);
        manager.addStudent(student2);
        manager.addStudent(student3);
        
        assertEquals(3, manager.getStudentCount());
    }
    
    // ==================== FIND STUDENT TESTS ====================
    
    @Test
    @DisplayName("Should find student by ID")
    void testFindStudentById() {
        Student student = new Student("S001", "John Doe", "Grade 10");
        manager.addStudent(student);
        
        Student found = manager.findStudentById("S001");
        assertNotNull(found);
        assertEquals("John Doe", found.getName());
    }
    
    @Test
    @DisplayName("Should return null for non-existent student")
    void testFindNonExistentStudent() {
        assertNull(manager.findStudentById("S999"));
    }
    
    @Test
    @DisplayName("Should return null for null ID")
    void testFindStudentWithNullId() {
        assertNull(manager.findStudentById(null));
    }
    
    // ==================== SEARCH TESTS ====================
    
    @Test
    @DisplayName("Should search students by name")
    void testSearchByName() {
        manager.addStudent(new Student("S001", "John Doe", "Grade 10"));
        manager.addStudent(new Student("S002", "Jane Doe", "Grade 11"));
        manager.addStudent(new Student("S003", "Bob Smith", "Grade 10"));
        
        List<Student> results = manager.searchByName("Doe");
        assertEquals(2, results.size());
    }
    
    @Test
    @DisplayName("Should search case-insensitively")
    void testSearchCaseInsensitive() {
        manager.addStudent(new Student("S001", "John Doe", "Grade 10"));
        
        List<Student> results = manager.searchByName("john");
        assertEquals(1, results.size());
    }
    
    @Test
    @DisplayName("Should return empty list for no matches")
    void testSearchNoMatches() {
        manager.addStudent(new Student("S001", "John Doe", "Grade 10"));
        
        List<Student> results = manager.searchByName("NonExistent");
        assertTrue(results.isEmpty());
    }
    
    @Test
    @DisplayName("Should return empty list for null query")
    void testSearchWithNullQuery() {
        List<Student> results = manager.searchByName(null);
        assertTrue(results.isEmpty());
    }
    
    // ==================== GRADE LEVEL TESTS ====================
    
    @Test
    @DisplayName("Should get students by grade level")
    void testGetStudentsByGrade() {
        manager.addStudent(new Student("S001", "John Doe", "Grade 10"));
        manager.addStudent(new Student("S002", "Jane Smith", "Grade 10"));
        manager.addStudent(new Student("S003", "Bob Johnson", "Grade 11"));
        
        List<Student> grade10 = manager.getStudentsByGrade("Grade 10");
        assertEquals(2, grade10.size());
    }
    
    @Test
    @DisplayName("Should search grade case-insensitively")
    void testGetStudentsByGradeCaseInsensitive() {
        manager.addStudent(new Student("S001", "John Doe", "Grade 10"));
        
        List<Student> results = manager.getStudentsByGrade("grade 10");
        assertEquals(1, results.size());
    }
    
    // ==================== UNDERPERFORMING STUDENTS TESTS ====================
    
    @Test
    @DisplayName("Should identify underperforming students")
    void testGetUnderperformingStudents() {
        Student student1 = new Student("S001", "John Doe", "Grade 10");
        student1.addAssessment("Mathematics", 45, "Test");
        
        Student student2 = new Student("S002", "Jane Smith", "Grade 10");
        student2.addAssessment("Mathematics", 85, "Test");
        
        Student student3 = new Student("S003", "Bob Johnson", "Grade 10");
        student3.addAssessment("Mathematics", 55, "Test");
        
        manager.addStudent(student1);
        manager.addStudent(student2);
        manager.addStudent(student3);
        
        List<Student> underperforming = manager.getUnderperformingStudents(60);
        assertEquals(2, underperforming.size());
    }
    
    @Test
    @DisplayName("Should sort underperforming students by average")
    void testUnderperformingStudentsSorted() {
        Student student1 = new Student("S001", "John Doe", "Grade 10");
        student1.addAssessment("Mathematics", 55, "Test");
        
        Student student2 = new Student("S002", "Jane Smith", "Grade 10");
        student2.addAssessment("Mathematics", 45, "Test");
        
        manager.addStudent(student1);
        manager.addStudent(student2);
        
        List<Student> underperforming = manager.getUnderperformingStudents(60);
        assertEquals("S002", underperforming.get(0).getId()); // Lowest first
    }
    
    @Test
    @DisplayName("Should exclude students with no data from underperforming list")
    void testUnderperformingExcludesNoData() {
        Student student1 = new Student("S001", "John Doe", "Grade 10");
        // No assessments
        
        Student student2 = new Student("S002", "Jane Smith", "Grade 10");
        student2.addAssessment("Mathematics", 45, "Test");
        
        manager.addStudent(student1);
        manager.addStudent(student2);
        
        List<Student> underperforming = manager.getUnderperformingStudents(60);
        assertEquals(1, underperforming.size());
        assertEquals("S002", underperforming.get(0).getId());
    }
    
    // ==================== TOP PERFORMERS TESTS ====================
    
    @Test
    @DisplayName("Should get top performers")
    void testGetTopPerformers() {
        Student student1 = new Student("S001", "John Doe", "Grade 10");
        student1.addAssessment("Mathematics", 95, "Test");
        
        Student student2 = new Student("S002", "Jane Smith", "Grade 10");
        student2.addAssessment("Mathematics", 85, "Test");
        
        Student student3 = new Student("S003", "Bob Johnson", "Grade 10");
        student3.addAssessment("Mathematics", 75, "Test");
        
        manager.addStudent(student1);
        manager.addStudent(student2);
        manager.addStudent(student3);
        
        List<Student> topPerformers = manager.getTopPerformers(2);
        assertEquals(2, topPerformers.size());
        assertEquals("S001", topPerformers.get(0).getId()); // Highest first
    }
    
    @Test
    @DisplayName("Should handle request for more top performers than available")
    void testGetTopPerformersExceedsCount() {
        Student student = new Student("S001", "John Doe", "Grade 10");
        student.addAssessment("Mathematics", 85, "Test");
        manager.addStudent(student);
        
        List<Student> topPerformers = manager.getTopPerformers(10);
        assertEquals(1, topPerformers.size());
    }
    
    @Test
    @DisplayName("Should exclude students with no data from top performers")
    void testTopPerformersExcludesNoData() {
        Student student1 = new Student("S001", "John Doe", "Grade 10");
        // No assessments
        
        Student student2 = new Student("S002", "Jane Smith", "Grade 10");
        student2.addAssessment("Mathematics", 85, "Test");
        
        manager.addStudent(student1);
        manager.addStudent(student2);
        
        List<Student> topPerformers = manager.getTopPerformers(5);
        assertEquals(1, topPerformers.size());
        assertEquals("S002", topPerformers.get(0).getId());
    }
    
    // ==================== CLASS AVERAGE TESTS ====================
    
    @Test
    @DisplayName("Should calculate class average")
    void testGetClassAverage() {
        Student student1 = new Student("S001", "John Doe", "Grade 10");
        student1.addAssessment("Mathematics", 80, "Test");
        
        Student student2 = new Student("S002", "Jane Smith", "Grade 10");
        student2.addAssessment("Mathematics", 90, "Test");
        
        manager.addStudent(student1);
        manager.addStudent(student2);
        
        double classAvg = manager.getClassAverage();
        assertEquals(85.0, classAvg, 0.01);
    }
    
    @Test
    @DisplayName("Should return -1 for class average with no students")
    void testGetClassAverageNoStudents() {
        assertEquals(-1, manager.getClassAverage());
    }
    
    @Test
    @DisplayName("Should exclude students with no data from class average")
    void testClassAverageExcludesNoData() {
        Student student1 = new Student("S001", "John Doe", "Grade 10");
        // No assessments
        
        Student student2 = new Student("S002", "Jane Smith", "Grade 10");
        student2.addAssessment("Mathematics", 80, "Test");
        
        manager.addStudent(student1);
        manager.addStudent(student2);
        
        double classAvg = manager.getClassAverage();
        assertEquals(80.0, classAvg, 0.01);
    }
    
    // ==================== REMOVE STUDENT TESTS ====================
    
    @Test
    @DisplayName("Should remove student successfully")
    void testRemoveStudent() {
        Student student = new Student("S001", "John Doe", "Grade 10");
        manager.addStudent(student);
        
        assertTrue(manager.removeStudent("S001"));
        assertEquals(0, manager.getStudentCount());
    }
    
    @Test
    @DisplayName("Should return false when removing non-existent student")
    void testRemoveNonExistentStudent() {
        assertFalse(manager.removeStudent("S999"));
    }
    
    // ==================== GET ALL STUDENTS TESTS ====================
    
    @Test
    @DisplayName("Should return all students")
    void testGetAllStudents() {
        manager.addStudent(new Student("S001", "John Doe", "Grade 10"));
        manager.addStudent(new Student("S002", "Jane Smith", "Grade 11"));
        
        List<Student> allStudents = manager.getAllStudents();
        assertEquals(2, allStudents.size());
    }
    
    @Test
    @DisplayName("Should return copy of student list")
    void testGetAllStudentsReturnsCopy() {
        manager.addStudent(new Student("S001", "John Doe", "Grade 10"));
        
        List<Student> allStudents = manager.getAllStudents();
        allStudents.clear();
        
        // Original list should not be affected
        assertEquals(1, manager.getStudentCount());
    }
    
    // ==================== CLASS REPORT TESTS ====================
    
    @Test
    @DisplayName("Should generate class report")
    void testGenerateClassReport() {
        Student student = new Student("S001", "John Doe", "Grade 10");
        student.addAssessment("Mathematics", 85, "Test");
        manager.addStudent(student);
        
        String report = manager.generateClassReport();
        assertNotNull(report);
        assertTrue(report.contains("CLASS PERFORMANCE REPORT"));
        assertTrue(report.contains("Total Students: 1"));
    }
    
    @Test
    @DisplayName("Should include performance distribution in report")
    void testClassReportIncludesDistribution() {
        Student student1 = new Student("S001", "John Doe", "Grade 10");
        student1.addAssessment("Mathematics", 95, "Test");
        
        Student student2 = new Student("S002", "Jane Smith", "Grade 10");
        student2.addAssessment("Mathematics", 80, "Test");
        
        manager.addStudent(student1);
        manager.addStudent(student2);
        
        String report = manager.generateClassReport();
        assertTrue(report.contains("Performance Distribution"));
        assertTrue(report.contains("Excellent"));
        assertTrue(report.contains("Good"));
    }
    
    // ==================== EXPORT TESTS ====================
    
    @Test
    @DisplayName("Should export to CSV successfully")
    void testExportToCSV() {
        Student student = new Student("S001", "John Doe", "Grade 10");
        student.addAssessment("Mathematics", 85, "Test");
        manager.addStudent(student);
        
        String filename = "test_export.csv";
        assertTrue(manager.exportToCSV(filename));
        
        File exportFile = new File(filename);
        assertTrue(exportFile.exists());
        exportFile.delete(); // Clean up
    }
    
    @Test
    @DisplayName("Should handle export with no students")
    void testExportEmptyData() {
        String filename = "test_export_empty.csv";
        assertTrue(manager.exportToCSV(filename));
        
        File exportFile = new File(filename);
        assertTrue(exportFile.exists());
        exportFile.delete(); // Clean up
    }
}
