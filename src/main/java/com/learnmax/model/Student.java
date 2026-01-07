package com.learnmax.model;

import java.util.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Improved Student class with support for:
 * - Multiple assessments per subject
 * - Historical tracking
 * - Trend analysis
 * - Enhanced validation
 */
public class Student {
    private String id;
    private String name;
    private String gradeLevel;
    private LocalDateTime enrollmentDate;
    private ArrayList<Assessment> assessments; // All assessments across all subjects
    
    public Student(String id, String name) {
        this(id, name, "Not Specified");
    }
    
    public Student(String id, String name, String gradeLevel) {
        // Validation
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty");
        }
        
        this.id = id.trim();
        this.name = name.trim();
        this.gradeLevel = gradeLevel;
        this.enrollmentDate = LocalDateTime.now();
        this.assessments = new ArrayList<>();
    }
    
    /**
     * Add a new assessment for a subject
     */
    public void addAssessment(String subject, int score, String assessmentType) {
        // Validate score
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("Score must be between 0 and 100");
        }
        
        // Validate subject
        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject cannot be empty");
        }
        
        // Normalize subject name (capitalize first letter, lowercase rest)
        String normalizedSubject = normalizeSubjectName(subject);
        
        Assessment assessment = new Assessment(normalizedSubject, score, assessmentType);
        assessments.add(assessment);
    }
    
    /**
     * Normalize subject names to prevent duplicates (Math vs math vs MATH)
     */
    private String normalizeSubjectName(String subject) {
        subject = subject.trim();
        if (subject.isEmpty()) return subject;
        return subject.substring(0, 1).toUpperCase() + subject.substring(1).toLowerCase();
    }
    
    /**
     * Get all assessments for a specific subject
     */
    public List<Assessment> getAssessmentsBySubject(String subject) {
        String normalizedSubject = normalizeSubjectName(subject);
        List<Assessment> subjectAssessments = new ArrayList<>();
        
        for (Assessment assessment : assessments) {
            if (assessment.getSubject().equals(normalizedSubject)) {
                subjectAssessments.add(assessment);
            }
        }
        
        return subjectAssessments;
    }
    
    /**
     * Get unique list of subjects
     */
    public Set<String> getSubjects() {
        Set<String> subjects = new HashSet<>();
        for (Assessment assessment : assessments) {
            subjects.add(assessment.getSubject());
        }
        return subjects;
    }
    
    /**
     * Calculate average for a specific subject (all assessments)
     */
    public double calculateSubjectAverage(String subject) {
        List<Assessment> subjectAssessments = getAssessmentsBySubject(subject);
        
        if (subjectAssessments.isEmpty()) {
            return -1; // Return -1 to indicate no data (different from 0 score)
        }
        
        double totalWeightedScore = 0;
        int totalWeight = 0;
        
        for (Assessment assessment : subjectAssessments) {
            totalWeightedScore += assessment.getScore() * assessment.getWeight();
            totalWeight += assessment.getWeight();
        }
        
        return totalWeight > 0 ? totalWeightedScore / totalWeight : 0;
    }
    
    /**
     * Calculate overall average across all subjects
     */
    public double calculateOverallAverage() {
        if (assessments.isEmpty()) {
            return -1; // Return -1 to indicate no data
        }
        
        Set<String> subjects = getSubjects();
        double totalAverage = 0;
        
        for (String subject : subjects) {
            double subjectAvg = calculateSubjectAverage(subject);
            if (subjectAvg >= 0) {
                totalAverage += subjectAvg;
            }
        }
        
        return subjects.size() > 0 ? totalAverage / subjects.size() : -1;
    }
    
    /**
     * Get the most recent score for a subject (for display purposes)
     */
    public int getLatestScore(String subject) {
        List<Assessment> subjectAssessments = getAssessmentsBySubject(subject);
        
        if (subjectAssessments.isEmpty()) {
            return -1;
        }
        
        // Find most recent assessment
        Assessment latest = subjectAssessments.get(0);
        for (Assessment assessment : subjectAssessments) {
            if (assessment.getDateRecorded().isAfter(latest.getDateRecorded())) {
                latest = assessment;
            }
        }
        
        return latest.getScore();
    }
    
    /**
     * Generate performance feedback based on overall average
     */
    public String generateFeedback() {
        double avg = calculateOverallAverage();
        
        if (avg < 0) {
            return "No assessment data available yet.";
        }
        
        if (avg >= 90) {
            return "Excellent! Keep up the outstanding work!";
        } else if (avg >= 75) {
            return "Good Job! You're performing well.";
        } else if (avg >= 50) {
            return "Needs Improvement. Consider additional study time.";
        } else {
            return "Requires Attention. Please seek additional support.";
        }
    }
    
    /**
     * Generate detailed actionable feedback with study recommendations
     */
    public String generateDetailedFeedback() {
        StringBuilder feedback = new StringBuilder();
        double overallAvg = calculateOverallAverage();
        
        if (overallAvg < 0) {
            return "No assessment data available for analysis.";
        }
        
        feedback.append("Overall Performance: ").append(String.format("%.1f%%", overallAvg)).append("\n");
        feedback.append(generateFeedback()).append("\n\n");
        
        // Identify weak subjects
        List<String> weakSubjects = new ArrayList<>();
        List<String> strongSubjects = new ArrayList<>();
        
        for (String subject : getSubjects()) {
            double subjectAvg = calculateSubjectAverage(subject);
            if (subjectAvg < 60) {
                weakSubjects.add(subject + " (" + String.format("%.1f%%", subjectAvg) + ")");
            } else if (subjectAvg >= 80) {
                strongSubjects.add(subject + " (" + String.format("%.1f%%", subjectAvg) + ")");
            }
        }
        
        if (!weakSubjects.isEmpty()) {
            feedback.append("⚠ Focus Areas (Need Improvement):\n");
            for (String subject : weakSubjects) {
                feedback.append("  • ").append(subject).append("\n");
            }
            feedback.append("\n");
        }
        
        if (!strongSubjects.isEmpty()) {
            feedback.append("✓ Strong Areas:\n");
            for (String subject : strongSubjects) {
                feedback.append("  • ").append(subject).append("\n");
            }
            feedback.append("\n");
        }
        
        // Study time recommendations
        feedback.append("Recommended Study Time (per week):\n");
        for (String subject : getSubjects()) {
            double subjectAvg = calculateSubjectAverage(subject);
            int recommendedHours = calculateRecommendedStudyHours(subjectAvg);
            feedback.append("  • ").append(subject).append(": ")
                    .append(recommendedHours).append(" hours\n");
        }
        
        return feedback.toString();
    }
    
    /**
     * Calculate recommended study hours based on performance
     */
    private int calculateRecommendedStudyHours(double average) {
        if (average >= 90) return 2;  // Maintenance
        if (average >= 75) return 3;  // Slight improvement
        if (average >= 60) return 5;  // Moderate improvement needed
        if (average >= 50) return 7;  // Significant improvement needed
        return 10; // Intensive support needed
    }
    
    /**
     * Analyze performance trend for a subject
     * Returns: "improving", "declining", "stable", or "insufficient_data"
     */
    public String analyzeSubjectTrend(String subject) {
        List<Assessment> subjectAssessments = getAssessmentsBySubject(subject);
        
        if (subjectAssessments.size() < 2) {
            return "insufficient_data";
        }
        
        // Sort by date
        subjectAssessments.sort(Comparator.comparing(Assessment::getDateRecorded));
        
        // Compare first half average with second half average
        int midPoint = subjectAssessments.size() / 2;
        double firstHalfAvg = subjectAssessments.subList(0, midPoint).stream()
                .mapToInt(Assessment::getScore).average().orElse(0);
        double secondHalfAvg = subjectAssessments.subList(midPoint, subjectAssessments.size()).stream()
                .mapToInt(Assessment::getScore).average().orElse(0);
        
        double difference = secondHalfAvg - firstHalfAvg;
        
        if (difference > 5) return "improving";
        if (difference < -5) return "declining";
        return "stable";
    }
    
    /**
     * Get all assessments (for persistence and display)
     */
    public List<Assessment> getAllAssessments() {
        return new ArrayList<>(assessments);
    }
    
    /**
     * Get assessments from last N days
     */
    public List<Assessment> getRecentAssessments(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minus(days, ChronoUnit.DAYS);
        List<Assessment> recent = new ArrayList<>();
        
        for (Assessment assessment : assessments) {
            if (assessment.getDateRecorded().isAfter(cutoffDate)) {
                recent.add(assessment);
            }
        }
        
        return recent;
    }
    
    // Getters
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getGradeLevel() {
        return gradeLevel;
    }
    
    public LocalDateTime getEnrollmentDate() {
        return enrollmentDate;
    }
    
    public int getAssessmentCount() {
        return assessments.size();
    }
    
    /**
     * Get subject scores as HashMap for backward compatibility
     * Returns latest score for each subject
     */
    public HashMap<String, Integer> getSubjectScores() {
        HashMap<String, Integer> scores = new HashMap<>();
        for (String subject : getSubjects()) {
            int latestScore = getLatestScore(subject);
            if (latestScore >= 0) {
                scores.put(subject, latestScore);
            }
        }
        return scores;
    }
}
