package com.learnmax.virtuallab.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a student's activity in a virtual lab exercise
 */
public class LabActivity {
    
    public enum ActivityStatus {
        NOT_STARTED("Not Started"),
        IN_PROGRESS("In Progress"),
        COMPLETED("Completed"),
        MASTERED("Mastered");
        
        private final String displayName;
        
        ActivityStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    private String activityId;
    private String studentId;
    private String exerciseId;
    private ActivityStatus status;
    private LocalDateTime startTime;
    private LocalDateTime lastAccessTime;
    private LocalDateTime completionTime;
    private int timeSpentMinutes;
    private int attemptsCount;
    private double score;  // 0-100
    private String aiFeedback;
    private boolean isOffline;
    private boolean needsSync;
    
    /**
     * Constructor for new activity
     */
    public LabActivity(String studentId, String exerciseId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be null or empty");
        }
        if (exerciseId == null || exerciseId.trim().isEmpty()) {
            throw new IllegalArgumentException("Exercise ID cannot be null or empty");
        }
        
        this.activityId = generateActivityId(studentId, exerciseId);
        this.studentId = studentId.trim();
        this.exerciseId = exerciseId.trim();
        this.status = ActivityStatus.NOT_STARTED;
        this.startTime = LocalDateTime.now();
        this.lastAccessTime = LocalDateTime.now();
        this.timeSpentMinutes = 0;
        this.attemptsCount = 0;
        this.score = -1;  // -1 indicates not yet scored
        this.aiFeedback = "";
        this.isOffline = false;
        this.needsSync = false;
    }
    
    /**
     * Generate unique activity ID
     */
    private String generateActivityId(String studentId, String exerciseId) {
        return studentId + "_" + exerciseId + "_" + System.currentTimeMillis();
    }
    
    /**
     * Start the activity
     */
    public void start() {
        if (status == ActivityStatus.NOT_STARTED) {
            status = ActivityStatus.IN_PROGRESS;
            startTime = LocalDateTime.now();
            lastAccessTime = LocalDateTime.now();
            attemptsCount++;
            needsSync = true;
        }
    }
    
    /**
     * Update progress
     */
    public void updateProgress(int minutesSpent) {
        this.lastAccessTime = LocalDateTime.now();
        this.timeSpentMinutes += minutesSpent;
        if (status == ActivityStatus.NOT_STARTED) {
            status = ActivityStatus.IN_PROGRESS;
        }
        needsSync = true;
    }
    
    /**
     * Complete the activity
     */
    public void complete(double score) {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("Score must be between 0 and 100");
        }
        
        this.score = score;
        this.completionTime = LocalDateTime.now();
        this.lastAccessTime = LocalDateTime.now();
        
        // Determine status based on score
        if (score >= 90) {
            this.status = ActivityStatus.MASTERED;
        } else {
            this.status = ActivityStatus.COMPLETED;
        }
        
        needsSync = true;
    }
    
    /**
     * Retry the activity
     */
    public void retry() {
        this.attemptsCount++;
        this.status = ActivityStatus.IN_PROGRESS;
        this.lastAccessTime = LocalDateTime.now();
        needsSync = true;
    }
    
    /**
     * Add AI feedback
     */
    public void addAiFeedback(String feedback) {
        this.aiFeedback = feedback != null ? feedback.trim() : "";
        needsSync = true;
    }
    
    /**
     * Check if activity is completed
     */
    public boolean isCompleted() {
        return status == ActivityStatus.COMPLETED || status == ActivityStatus.MASTERED;
    }
    
    /**
     * Check if mastered
     */
    public boolean isMastered() {
        return status == ActivityStatus.MASTERED;
    }
    
    /**
     * Get formatted start time
     */
    public String getFormattedStartTime() {
        if (startTime == null) return "N/A";
        return startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    
    /**
     * Get formatted completion time
     */
    public String getFormattedCompletionTime() {
        if (completionTime == null) return "Not completed";
        return completionTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    
    // Getters and Setters
    
    public String getActivityId() {
        return activityId;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public String getExerciseId() {
        return exerciseId;
    }
    
    public ActivityStatus getStatus() {
        return status;
    }
    
    public void setStatus(ActivityStatus status) {
        this.status = status != null ? status : ActivityStatus.NOT_STARTED;
        needsSync = true;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getLastAccessTime() {
        return lastAccessTime;
    }
    
    public void setLastAccessTime(LocalDateTime lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }
    
    public LocalDateTime getCompletionTime() {
        return completionTime;
    }
    
    public void setCompletionTime(LocalDateTime completionTime) {
        this.completionTime = completionTime;
    }
    
    public int getTimeSpentMinutes() {
        return timeSpentMinutes;
    }
    
    public void setTimeSpentMinutes(int timeSpentMinutes) {
        this.timeSpentMinutes = Math.max(0, timeSpentMinutes);
    }
    
    public int getAttemptsCount() {
        return attemptsCount;
    }
    
    public void setAttemptsCount(int attemptsCount) {
        this.attemptsCount = Math.max(0, attemptsCount);
    }
    
    public double getScore() {
        return score;
    }
    
    public void setScore(double score) {
        if (score >= 0 && score <= 100) {
            this.score = score;
            needsSync = true;
        }
    }
    
    public String getAiFeedback() {
        return aiFeedback;
    }
    
    public void setAiFeedback(String aiFeedback) {
        this.aiFeedback = aiFeedback != null ? aiFeedback.trim() : "";
    }
    
    public boolean isOffline() {
        return isOffline;
    }
    
    public void setOffline(boolean offline) {
        this.isOffline = offline;
    }
    
    public boolean needsSync() {
        return needsSync;
    }
    
    public void markSynced() {
        this.needsSync = false;
    }
    
    @Override
    public String toString() {
        return String.format("Activity[%s] Student:%s Exercise:%s Status:%s Score:%.1f%%",
                activityId, studentId, exerciseId, status.getDisplayName(), score);
    }
}
