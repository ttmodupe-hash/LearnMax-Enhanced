package com.learnmax;

import com.learnmax.model.Student;
import com.learnmax.model.Assessment;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for Student class
 */
class StudentTest {
    
    private Student student;
    
    @BeforeEach
    void setUp() {
        student = new Student("S001", "John Doe", "Grade 10");
    }
    
    // ==================== CONSTRUCTOR TESTS ====================
    
    @Test
    @DisplayName("Should create student with valid data")
    void testValidStudentCreation() {
        assertNotNull(student);
        assertEquals("S001", student.getId());
        assertEquals("John Doe", student.getName());
        assertEquals("Grade 10", student.getGradeLevel());
    }
    
    @Test
    @DisplayName("Should throw exception for null ID")
    void testNullIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Student(null, "Jane Doe", "Grade 10");
        });
    }
    
    @Test
    @DisplayName("Should throw exception for empty ID")
    void testEmptyIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Student("", "Jane Doe", "Grade 10");
        });
    }
    
    @Test
    @DisplayName("Should throw exception for null name")
    void testNullNameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Student("S002", null, "Grade 10");
        });
    }
    
    @Test
    @DisplayName("Should throw exception for empty name")
    void testEmptyNameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Student("S002", "   ", "Grade 10");
        });
    }
    
    @Test
    @DisplayName("Should trim whitespace from ID and name")
    void testWhitespaceTrimming() {
        Student s = new Student("  S002  ", "  Jane Doe  ", "Grade 10");
        assertEquals("S002", s.getId());
        assertEquals("Jane Doe", s.getName());
    }
    
    // ==================== ASSESSMENT TESTS ====================
    
    @Test
    @DisplayName("Should add valid assessment")
    void testAddValidAssessment() {
        student.addAssessment("Mathematics", 85, "Test");
        assertEquals(1, student.getAssessmentCount());
    }
    
    @Test
    @DisplayName("Should add multiple assessments")
    void testAddMultipleAssessments() {
        student.addAssessment("Mathematics", 85, "Test");
        student.addAssessment("Science", 90, "Quiz");
        student.addAssessment("English", 78, "Assignment");
        
        assertEquals(3, student.getAssessmentCount());
        assertEquals(3, student.getSubjects().size());
    }
    
    @Test
    @DisplayName("Should throw exception for score below 0")
    void testScoreBelowZeroThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            student.addAssessment("Mathematics", -1, "Test");
        });
    }
    
    @Test
    @DisplayName("Should throw exception for score above 100")
    void testScoreAbove100ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            student.addAssessment("Mathematics", 101, "Test");
        });
    }
    
    @ParameterizedTest
    @ValueSource(ints = {0, 50, 100})
    @DisplayName("Should accept boundary scores")
    void testBoundaryScores(int score) {
        assertDoesNotThrow(() -> {
            student.addAssessment("Mathematics", score, "Test");
        });
    }
    
    @Test
    @DisplayName("Should throw exception for null subject")
    void testNullSubjectThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            student.addAssessment(null, 85, "Test");
        });
    }
    
    @Test
    @DisplayName("Should throw exception for empty subject")
    void testEmptySubjectThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            student.addAssessment("", 85, "Test");
        });
    }
    
    // ==================== SUBJECT NORMALIZATION TESTS ====================
    
    @Test
    @DisplayName("Should normalize subject names")
    void testSubjectNormalization() {
        student.addAssessment("mathematics", 85, "Test");
        student.addAssessment("MATHEMATICS", 90, "Quiz");
        student.addAssessment("Mathematics", 78, "Assignment");
        
        // All should be normalized to "Mathematics"
        assertEquals(1, student.getSubjects().size());
        assertTrue(student.getSubjects().contains("Mathematics"));
        assertEquals(3, student.getAssessmentsBySubject("Mathematics").size());
    }
    
    // ==================== AVERAGE CALCULATION TESTS ====================
    
    @Test
    @DisplayName("Should return -1 for overall average with no assessments")
    void testOverallAverageNoData() {
        assertEquals(-1, student.calculateOverallAverage());
    }
    
    @Test
    @DisplayName("Should calculate correct subject average")
    void testSubjectAverageCalculation() {
        student.addAssessment("Mathematics", 80, "Test");
        student.addAssessment("Mathematics", 90, "Quiz");
        student.addAssessment("Mathematics", 70, "Assignment");
        
        double expected = (80 + 90 + 70) / 3.0;
        assertEquals(expected, student.calculateSubjectAverage("Mathematics"), 0.01);
    }
    
    @Test
    @DisplayName("Should calculate correct overall average")
    void testOverallAverageCalculation() {
        student.addAssessment("Mathematics", 80, "Test");
        student.addAssessment("Science", 90, "Test");
        student.addAssessment("English", 70, "Test");
        
        double expected = (80 + 90 + 70) / 3.0;
        assertEquals(expected, student.calculateOverallAverage(), 0.01);
    }
    
    @Test
    @DisplayName("Should return -1 for subject average with no assessments")
    void testSubjectAverageNoData() {
        assertEquals(-1, student.calculateSubjectAverage("Mathematics"));
    }
    
    // ==================== LATEST SCORE TESTS ====================
    
    @Test
    @DisplayName("Should return latest score for subject")
    void testGetLatestScore() throws InterruptedException {
        student.addAssessment("Mathematics", 80, "Test");
        Thread.sleep(10); // Ensure different timestamps
        student.addAssessment("Mathematics", 90, "Quiz");
        Thread.sleep(10);
        student.addAssessment("Mathematics", 85, "Assignment");
        
        assertEquals(85, student.getLatestScore("Mathematics"));
    }
    
    @Test
    @DisplayName("Should return -1 for latest score with no assessments")
    void testLatestScoreNoData() {
        assertEquals(-1, student.getLatestScore("Mathematics"));
    }
    
    // ==================== FEEDBACK TESTS ====================
    
    @Test
    @DisplayName("Should generate 'No data' feedback for no assessments")
    void testFeedbackNoData() {
        String feedback = student.generateFeedback();
        assertTrue(feedback.contains("No assessment data"));
    }
    
    @Test
    @DisplayName("Should generate 'Excellent' feedback for 90+")
    void testFeedbackExcellent() {
        student.addAssessment("Mathematics", 95, "Test");
        String feedback = student.generateFeedback();
        assertTrue(feedback.contains("Excellent"));
    }
    
    @Test
    @DisplayName("Should generate 'Good Job' feedback for 75-89")
    void testFeedbackGood() {
        student.addAssessment("Mathematics", 80, "Test");
        String feedback = student.generateFeedback();
        assertTrue(feedback.contains("Good Job"));
    }
    
    @Test
    @DisplayName("Should generate 'Needs Improvement' feedback for 50-74")
    void testFeedbackNeedsImprovement() {
        student.addAssessment("Mathematics", 60, "Test");
        String feedback = student.generateFeedback();
        assertTrue(feedback.contains("Needs Improvement"));
    }
    
    @Test
    @DisplayName("Should generate 'Requires Attention' feedback for <50")
    void testFeedbackPoor() {
        student.addAssessment("Mathematics", 40, "Test");
        String feedback = student.generateFeedback();
        assertTrue(feedback.contains("Requires Attention"));
    }
    
    // ==================== DETAILED FEEDBACK TESTS ====================
    
    @Test
    @DisplayName("Should include study recommendations in detailed feedback")
    void testDetailedFeedbackIncludesRecommendations() {
        student.addAssessment("Mathematics", 60, "Test");
        String feedback = student.generateDetailedFeedback();
        
        assertTrue(feedback.contains("Recommended Study Time"));
        assertTrue(feedback.contains("hours"));
    }
    
    @Test
    @DisplayName("Should identify weak subjects in detailed feedback")
    void testDetailedFeedbackIdentifiesWeakSubjects() {
        student.addAssessment("Mathematics", 50, "Test");
        student.addAssessment("Science", 85, "Test");
        
        String feedback = student.generateDetailedFeedback();
        assertTrue(feedback.contains("Focus Areas"));
    }
    
    @Test
    @DisplayName("Should identify strong subjects in detailed feedback")
    void testDetailedFeedbackIdentifiesStrongSubjects() {
        student.addAssessment("Mathematics", 85, "Test");
        student.addAssessment("Science", 90, "Test");
        
        String feedback = student.generateDetailedFeedback();
        assertTrue(feedback.contains("Strong Areas"));
    }
    
    // ==================== TREND ANALYSIS TESTS ====================
    
    @Test
    @DisplayName("Should return 'insufficient_data' for less than 2 assessments")
    void testTrendInsufficientData() {
        student.addAssessment("Mathematics", 80, "Test");
        assertEquals("insufficient_data", student.analyzeSubjectTrend("Mathematics"));
    }
    
    @Test
    @DisplayName("Should detect improving trend")
    void testTrendImproving() throws InterruptedException {
        student.addAssessment("Mathematics", 60, "Test");
        Thread.sleep(10);
        student.addAssessment("Mathematics", 70, "Quiz");
        Thread.sleep(10);
        student.addAssessment("Mathematics", 80, "Assignment");
        Thread.sleep(10);
        student.addAssessment("Mathematics", 90, "Test");
        
        assertEquals("improving", student.analyzeSubjectTrend("Mathematics"));
    }
    
    @Test
    @DisplayName("Should detect declining trend")
    void testTrendDeclining() throws InterruptedException {
        student.addAssessment("Mathematics", 90, "Test");
        Thread.sleep(10);
        student.addAssessment("Mathematics", 80, "Quiz");
        Thread.sleep(10);
        student.addAssessment("Mathematics", 70, "Assignment");
        Thread.sleep(10);
        student.addAssessment("Mathematics", 60, "Test");
        
        assertEquals("declining", student.analyzeSubjectTrend("Mathematics"));
    }
    
    @Test
    @DisplayName("Should detect stable trend")
    void testTrendStable() throws InterruptedException {
        student.addAssessment("Mathematics", 80, "Test");
        Thread.sleep(10);
        student.addAssessment("Mathematics", 82, "Quiz");
        Thread.sleep(10);
        student.addAssessment("Mathematics", 79, "Assignment");
        Thread.sleep(10);
        student.addAssessment("Mathematics", 81, "Test");
        
        assertEquals("stable", student.analyzeSubjectTrend("Mathematics"));
    }
    
    // ==================== SUBJECTS TESTS ====================
    
    @Test
    @DisplayName("Should return empty set for no subjects")
    void testGetSubjectsEmpty() {
        assertTrue(student.getSubjects().isEmpty());
    }
    
    @Test
    @DisplayName("Should return correct subjects")
    void testGetSubjects() {
        student.addAssessment("Mathematics", 80, "Test");
        student.addAssessment("Science", 90, "Test");
        student.addAssessment("English", 70, "Test");
        
        Set<String> subjects = student.getSubjects();
        assertEquals(3, subjects.size());
        assertTrue(subjects.contains("Mathematics"));
        assertTrue(subjects.contains("Science"));
        assertTrue(subjects.contains("English"));
    }
    
    // ==================== RECENT ASSESSMENTS TESTS ====================
    
    @Test
    @DisplayName("Should return recent assessments within specified days")
    void testGetRecentAssessments() {
        student.addAssessment("Mathematics", 80, "Test");
        student.addAssessment("Science", 90, "Test");
        
        List<Assessment> recent = student.getRecentAssessments(7);
        assertEquals(2, recent.size());
    }
    
    @Test
    @DisplayName("Should return empty list for recent assessments when none exist")
    void testGetRecentAssessmentsEmpty() {
        List<Assessment> recent = student.getRecentAssessments(7);
        assertTrue(recent.isEmpty());
    }
    
    // ==================== BACKWARD COMPATIBILITY TESTS ====================
    
    @Test
    @DisplayName("Should provide backward compatible getSubjectScores")
    void testGetSubjectScoresBackwardCompatibility() {
        student.addAssessment("Mathematics", 80, "Test");
        student.addAssessment("Mathematics", 90, "Quiz");
        student.addAssessment("Science", 85, "Test");
        
        var scores = student.getSubjectScores();
        assertEquals(2, scores.size());
        assertEquals(90, scores.get("Mathematics")); // Latest score
        assertEquals(85, scores.get("Science"));
    }
}
