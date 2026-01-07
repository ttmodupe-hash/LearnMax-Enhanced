package com.learnmax.virtuallab.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.learnmax.virtuallab.model.LabActivity;
import com.learnmax.virtuallab.model.LabExercise;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages virtual lab exercises and student activities
 * Supports offline operation and synchronization
 */
public class VirtualLabManager {
    
    private static final String EXERCISES_FILE = "lab_exercises.json";
    private static final String ACTIVITIES_FILE = "lab_activities.json";
    private static final String PENDING_SYNC_FILE = "pending_sync.json";
    
    private Map<String, LabExercise> exercises;
    private Map<String, LabActivity> activities;
    private List<LabActivity> pendingSync;
    private Gson gson;
    private boolean offlineMode;
    
    /**
     * Constructor
     */
    public VirtualLabManager() {
        this.exercises = new HashMap<>();
        this.activities = new HashMap<>();
        this.pendingSync = new ArrayList<>();
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        this.offlineMode = false;
        
        loadExercises();
        loadActivities();
        loadPendingSync();
    }
    
    // ==================== EXERCISE MANAGEMENT ====================
    
    /**
     * Add a new lab exercise
     */
    public boolean addExercise(LabExercise exercise) {
        if (exercise == null || exercises.containsKey(exercise.getId())) {
            return false;
        }
        
        exercises.put(exercise.getId(), exercise);
        saveExercises();
        return true;
    }
    
    /**
     * Get exercise by ID
     */
    public LabExercise getExercise(String exerciseId) {
        return exercises.get(exerciseId);
    }
    
    /**
     * Get all exercises
     */
    public List<LabExercise> getAllExercises() {
        return new ArrayList<>(exercises.values());
    }
    
    /**
     * Get exercises by subject
     */
    public List<LabExercise> getExercisesBySubject(String subject) {
        return exercises.values().stream()
                .filter(ex -> ex.getSubject().equalsIgnoreCase(subject))
                .collect(Collectors.toList());
    }
    
    /**
     * Get exercises by grade level
     */
    public List<LabExercise> getExercisesByGrade(String gradeLevel) {
        return exercises.values().stream()
                .filter(ex -> ex.getGradeLevel().equalsIgnoreCase(gradeLevel))
                .collect(Collectors.toList());
    }
    
    /**
     * Get exercises by difficulty
     */
    public List<LabExercise> getExercisesByDifficulty(LabExercise.DifficultyLevel difficulty) {
        return exercises.values().stream()
                .filter(ex -> ex.getDifficulty() == difficulty)
                .collect(Collectors.toList());
    }
    
    /**
     * Get offline-available exercises
     */
    public List<LabExercise> getOfflineExercises() {
        return exercises.values().stream()
                .filter(LabExercise::isOfflineAvailable)
                .collect(Collectors.toList());
    }
    
    // ==================== ACTIVITY TRACKING ====================
    
    /**
     * Start a new activity
     */
    public LabActivity startActivity(String studentId, String exerciseId) {
        if (!exercises.containsKey(exerciseId)) {
            throw new IllegalArgumentException("Exercise not found: " + exerciseId);
        }
        
        LabActivity activity = new LabActivity(studentId, exerciseId);
        activity.start();
        activity.setOffline(offlineMode);
        
        activities.put(activity.getActivityId(), activity);
        
        if (offlineMode) {
            pendingSync.add(activity);
        }
        
        saveActivities();
        savePendingSync();
        
        return activity;
    }
    
    /**
     * Update activity progress
     */
    public void updateActivity(String activityId, int minutesSpent) {
        LabActivity activity = activities.get(activityId);
        if (activity != null) {
            activity.updateProgress(minutesSpent);
            
            if (offlineMode && !pendingSync.contains(activity)) {
                pendingSync.add(activity);
            }
            
            saveActivities();
            savePendingSync();
        }
    }
    
    /**
     * Complete an activity
     */
    public void completeActivity(String activityId, double score, String aiFeedback) {
        LabActivity activity = activities.get(activityId);
        if (activity != null) {
            activity.complete(score);
            activity.addAiFeedback(aiFeedback);
            
            if (offlineMode && !pendingSync.contains(activity)) {
                pendingSync.add(activity);
            }
            
            saveActivities();
            savePendingSync();
        }
    }
    
    /**
     * Get activity by ID
     */
    public LabActivity getActivity(String activityId) {
        return activities.get(activityId);
    }
    
    /**
     * Get all activities for a student
     */
    public List<LabActivity> getStudentActivities(String studentId) {
        return activities.values().stream()
                .filter(act -> act.getStudentId().equals(studentId))
                .sorted(Comparator.comparing(LabActivity::getLastAccessTime).reversed())
                .collect(Collectors.toList());
    }
    
    /**
     * Get completed activities for a student
     */
    public List<LabActivity> getCompletedActivities(String studentId) {
        return activities.values().stream()
                .filter(act -> act.getStudentId().equals(studentId))
                .filter(LabActivity::isCompleted)
                .collect(Collectors.toList());
    }
    
    /**
     * Get in-progress activities for a student
     */
    public List<LabActivity> getInProgressActivities(String studentId) {
        return activities.values().stream()
                .filter(act -> act.getStudentId().equals(studentId))
                .filter(act -> act.getStatus() == LabActivity.ActivityStatus.IN_PROGRESS)
                .collect(Collectors.toList());
    }
    
    // ==================== PROGRESS ANALYTICS ====================
    
    /**
     * Get student's completion rate
     */
    public double getCompletionRate(String studentId) {
        List<LabActivity> studentActivities = getStudentActivities(studentId);
        if (studentActivities.isEmpty()) return 0.0;
        
        long completed = studentActivities.stream()
                .filter(LabActivity::isCompleted)
                .count();
        
        return (completed * 100.0) / studentActivities.size();
    }
    
    /**
     * Get student's average score
     */
    public double getAverageScore(String studentId) {
        List<LabActivity> completed = getCompletedActivities(studentId);
        if (completed.isEmpty()) return 0.0;
        
        return completed.stream()
                .mapToDouble(LabActivity::getScore)
                .average()
                .orElse(0.0);
    }
    
    /**
     * Get total time spent by student
     */
    public int getTotalTimeSpent(String studentId) {
        return getStudentActivities(studentId).stream()
                .mapToInt(LabActivity::getTimeSpentMinutes)
                .sum();
    }
    
    /**
     * Get mastery count
     */
    public int getMasteryCount(String studentId) {
        return (int) getStudentActivities(studentId).stream()
                .filter(LabActivity::isMastered)
                .count();
    }
    
    /**
     * Get subject-wise progress
     */
    public Map<String, Double> getSubjectProgress(String studentId) {
        Map<String, Double> progress = new HashMap<>();
        
        for (LabActivity activity : getStudentActivities(studentId)) {
            LabExercise exercise = exercises.get(activity.getExerciseId());
            if (exercise != null && activity.isCompleted()) {
                String subject = exercise.getSubject();
                progress.merge(subject, activity.getScore(), (old, score) -> (old + score) / 2);
            }
        }
        
        return progress;
    }
    
    /**
     * Generate student progress report
     */
    public String generateProgressReport(String studentId) {
        StringBuilder report = new StringBuilder();
        report.append("═══════════════════════════════════════════════════════════════\n");
        report.append("                 VIRTUAL LAB PROGRESS REPORT\n");
        report.append("═══════════════════════════════════════════════════════════════\n");
        report.append("Student ID: ").append(studentId).append("\n\n");
        
        List<LabActivity> activities = getStudentActivities(studentId);
        List<LabActivity> completed = getCompletedActivities(studentId);
        
        report.append("Overall Statistics:\n");
        report.append("  Total Activities: ").append(activities.size()).append("\n");
        report.append("  Completed: ").append(completed.size()).append("\n");
        report.append("  Completion Rate: ").append(String.format("%.1f%%", getCompletionRate(studentId))).append("\n");
        report.append("  Average Score: ").append(String.format("%.1f%%", getAverageScore(studentId))).append("\n");
        report.append("  Total Time: ").append(getTotalTimeSpent(studentId)).append(" minutes\n");
        report.append("  Mastered: ").append(getMasteryCount(studentId)).append(" exercises\n\n");
        
        report.append("Subject Progress:\n");
        Map<String, Double> subjectProgress = getSubjectProgress(studentId);
        for (Map.Entry<String, Double> entry : subjectProgress.entrySet()) {
            report.append("  ").append(entry.getKey()).append(": ")
                    .append(String.format("%.1f%%", entry.getValue())).append("\n");
        }
        
        report.append("\n");
        report.append("Recent Activities:\n");
        activities.stream()
                .limit(5)
                .forEach(act -> {
                    LabExercise ex = exercises.get(act.getExerciseId());
                    if (ex != null) {
                        report.append("  • ").append(ex.getTitle())
                                .append(" - ").append(act.getStatus().getDisplayName());
                        if (act.isCompleted()) {
                            report.append(" (").append(String.format("%.1f%%", act.getScore())).append(")");
                        }
                        report.append("\n");
                    }
                });
        
        report.append("\n═══════════════════════════════════════════════════════════════\n");
        
        return report.toString();
    }
    
    // ==================== OFFLINE SYNC ====================
    
    /**
     * Set offline mode
     */
    public void setOfflineMode(boolean offline) {
        this.offlineMode = offline;
    }
    
    /**
     * Check if in offline mode
     */
    public boolean isOfflineMode() {
        return offlineMode;
    }
    
    /**
     * Get pending sync count
     */
    public int getPendingSyncCount() {
        return pendingSync.size();
    }
    
    /**
     * Synchronize pending activities
     */
    public boolean synchronize() {
        if (pendingSync.isEmpty()) {
            return true;
        }
        
        try {
            // In production, this would sync with server
            // For now, just mark as synced
            for (LabActivity activity : pendingSync) {
                activity.markSynced();
            }
            
            pendingSync.clear();
            savePendingSync();
            saveActivities();
            
            return true;
        } catch (Exception e) {
            System.err.println("Sync failed: " + e.getMessage());
            return false;
        }
    }
    
    // ==================== DATA PERSISTENCE ====================
    
    /**
     * Save exercises to file
     */
    private void saveExercises() {
        try (Writer writer = new FileWriter(EXERCISES_FILE)) {
            gson.toJson(exercises, writer);
        } catch (IOException e) {
            System.err.println("Error saving exercises: " + e.getMessage());
        }
    }
    
    /**
     * Load exercises from file
     */
    private void loadExercises() {
        File file = new File(EXERCISES_FILE);
        if (!file.exists()) {
            System.out.println("No exercises file found. Starting with empty library.");
            return;
        }
        
        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, LabExercise>>(){}.getType();
            Map<String, LabExercise> loaded = gson.fromJson(reader, type);
            if (loaded != null) {
                exercises = loaded;
                System.out.println("✓ Loaded " + exercises.size() + " exercises");
            }
        } catch (IOException e) {
            System.err.println("Error loading exercises: " + e.getMessage());
        }
    }
    
    /**
     * Save activities to file
     */
    private void saveActivities() {
        try (Writer writer = new FileWriter(ACTIVITIES_FILE)) {
            gson.toJson(activities, writer);
        } catch (IOException e) {
            System.err.println("Error saving activities: " + e.getMessage());
        }
    }
    
    /**
     * Load activities from file
     */
    private void loadActivities() {
        File file = new File(ACTIVITIES_FILE);
        if (!file.exists()) {
            return;
        }
        
        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, LabActivity>>(){}.getType();
            Map<String, LabActivity> loaded = gson.fromJson(reader, type);
            if (loaded != null) {
                activities = loaded;
            }
        } catch (IOException e) {
            System.err.println("Error loading activities: " + e.getMessage());
        }
    }
    
    /**
     * Save pending sync queue
     */
    private void savePendingSync() {
        try (Writer writer = new FileWriter(PENDING_SYNC_FILE)) {
            gson.toJson(pendingSync, writer);
        } catch (IOException e) {
            System.err.println("Error saving pending sync: " + e.getMessage());
        }
    }
    
    /**
     * Load pending sync queue
     */
    private void loadPendingSync() {
        File file = new File(PENDING_SYNC_FILE);
        if (!file.exists()) {
            return;
        }
        
        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<List<LabActivity>>(){}.getType();
            List<LabActivity> loaded = gson.fromJson(reader, type);
            if (loaded != null) {
                pendingSync = loaded;
            }
        } catch (IOException e) {
            System.err.println("Error loading pending sync: " + e.getMessage());
        }
    }
    
    /**
     * Get exercise count
     */
    public int getExerciseCount() {
        return exercises.size();
    }
    
    /**
     * Get activity count
     */
    public int getActivityCount() {
        return activities.size();
    }
}
