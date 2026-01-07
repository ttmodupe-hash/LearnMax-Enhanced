package com.learnmax.virtuallab.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a virtual lab exercise
 */
public class LabExercise {
    
    public enum ExerciseType {
        SIMULATION,      // Interactive simulation
        QUIZ,            // Knowledge check
        PROBLEM_SOLVING, // Step-by-step problem
        EXPERIMENT,      // Virtual experiment
        CODING,          // Programming exercise
        INTERACTIVE      // General interactive activity
    }
    
    public enum DifficultyLevel {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED
    }
    
    private String id;
    private String title;
    private String description;
    private String subject;
    private String gradeLevel;
    private ExerciseType type;
    private DifficultyLevel difficulty;
    private List<String> learningObjectives;
    private int estimatedMinutes;
    private List<String> prerequisites;
    private String capsReference;  // CAPS curriculum reference
    private boolean offlineAvailable;
    
    /**
     * Constructor
     */
    public LabExercise(String id, String title, String subject, String gradeLevel, ExerciseType type) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Exercise ID cannot be null or empty");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject cannot be null or empty");
        }
        
        this.id = id.trim();
        this.title = title.trim();
        this.subject = subject.trim();
        this.gradeLevel = gradeLevel != null ? gradeLevel.trim() : "";
        this.type = type != null ? type : ExerciseType.INTERACTIVE;
        this.difficulty = DifficultyLevel.INTERMEDIATE;
        this.learningObjectives = new ArrayList<>();
        this.prerequisites = new ArrayList<>();
        this.estimatedMinutes = 15;
        this.offlineAvailable = true;
        this.description = "";
        this.capsReference = "";
    }
    
    // Getters and Setters
    
    public String getId() {
        return id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title.trim();
        }
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description != null ? description.trim() : "";
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        if (subject != null && !subject.trim().isEmpty()) {
            this.subject = subject.trim();
        }
    }
    
    public String getGradeLevel() {
        return gradeLevel;
    }
    
    public void setGradeLevel(String gradeLevel) {
        this.gradeLevel = gradeLevel != null ? gradeLevel.trim() : "";
    }
    
    public ExerciseType getType() {
        return type;
    }
    
    public void setType(ExerciseType type) {
        this.type = type != null ? type : ExerciseType.INTERACTIVE;
    }
    
    public DifficultyLevel getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(DifficultyLevel difficulty) {
        this.difficulty = difficulty != null ? difficulty : DifficultyLevel.INTERMEDIATE;
    }
    
    public List<String> getLearningObjectives() {
        return new ArrayList<>(learningObjectives);
    }
    
    public void addLearningObjective(String objective) {
        if (objective != null && !objective.trim().isEmpty()) {
            learningObjectives.add(objective.trim());
        }
    }
    
    public void setLearningObjectives(List<String> objectives) {
        this.learningObjectives = objectives != null ? new ArrayList<>(objectives) : new ArrayList<>();
    }
    
    public int getEstimatedMinutes() {
        return estimatedMinutes;
    }
    
    public void setEstimatedMinutes(int minutes) {
        this.estimatedMinutes = Math.max(1, minutes);
    }
    
    public List<String> getPrerequisites() {
        return new ArrayList<>(prerequisites);
    }
    
    public void addPrerequisite(String prerequisite) {
        if (prerequisite != null && !prerequisite.trim().isEmpty()) {
            prerequisites.add(prerequisite.trim());
        }
    }
    
    public void setPrerequisites(List<String> prerequisites) {
        this.prerequisites = prerequisites != null ? new ArrayList<>(prerequisites) : new ArrayList<>();
    }
    
    public String getCapsReference() {
        return capsReference;
    }
    
    public void setCapsReference(String capsReference) {
        this.capsReference = capsReference != null ? capsReference.trim() : "";
    }
    
    public boolean isOfflineAvailable() {
        return offlineAvailable;
    }
    
    public void setOfflineAvailable(boolean offlineAvailable) {
        this.offlineAvailable = offlineAvailable;
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s (%s, %s)", id, title, subject, difficulty);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LabExercise other = (LabExercise) obj;
        return id.equals(other.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
