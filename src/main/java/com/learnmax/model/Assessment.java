package com.learnmax.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single assessment/test score for a subject
 * Includes timestamp tracking for temporal analysis
 */
public class Assessment {
    private String subject;
    private int score;
    private LocalDateTime dateRecorded;
    private String assessmentType; // "Test", "Quiz", "Assignment", "Exam"
    private int weight; // For weighted average calculations (default 100)
    
    public Assessment(String subject, int score, String assessmentType) {
        this.subject = subject;
        this.score = score;
        this.dateRecorded = LocalDateTime.now();
        this.assessmentType = assessmentType;
        this.weight = 100;
    }
    
    public Assessment(String subject, int score, String assessmentType, int weight) {
        this(subject, score, assessmentType);
        this.weight = weight;
    }
    
    // Getters
    public String getSubject() {
        return subject;
    }
    
    public int getScore() {
        return score;
    }
    
    public LocalDateTime getDateRecorded() {
        return dateRecorded;
    }
    
    public String getAssessmentType() {
        return assessmentType;
    }
    
    public int getWeight() {
        return weight;
    }
    
    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dateRecorded.format(formatter);
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s: %d/100 (Weight: %d%%) [%s]", 
                           subject, assessmentType, score, weight, getFormattedDate());
    }
}
