package com.learnmax.virtuallab.gui;

import com.learnmax.virtuallab.manager.VirtualLabManager;
import com.learnmax.virtuallab.model.LabActivity;
import com.learnmax.virtuallab.model.LabExercise;
import com.learnmax.virtuallab.simulators.AlgebraSimulator;
import com.learnmax.virtuallab.simulators.PythonPlayground;
import com.learnmax.virtuallab.simulators.PhysicsLab;
import com.learnmax.virtuallab.simulators.ChemistryLab;
import com.learnmax.virtuallab.simulators.CircuitLab;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Main GUI for Virtual Labs
 * Integrated with Student Performance Tracker
 */
public class VirtualLabsGUI extends JFrame {
    
    private VirtualLabManager labManager;
    private String currentStudentId;
    
    // UI Components
    private JTabbedPane tabbedPane;
    private JTable exercisesTable;
    private DefaultTableModel exercisesTableModel;
    private JTable activitiesTable;
    private DefaultTableModel activitiesTableModel;
    private JTextArea progressArea;
    private JLabel statusLabel, syncLabel;
    private JButton launchButton, syncButton, refreshButton;
    private JComboBox<String> subjectFilter, gradeFilter;
    
    // Colors
    private static final Color PRIMARY = new Color(52, 152, 219);
    private static final Color SUCCESS = new Color(46, 204, 113);
    private static final Color WARNING = new Color(241, 196, 15);
    private static final Color DANGER = new Color(231, 76, 60);
    
    /**
     * Constructor
     */
    public VirtualLabsGUI(String studentId) {
        this.currentStudentId = studentId;
        this.labManager = new VirtualLabManager();
        
        initializeSampleExercises();
        initializeUI();
        loadData();
    }
    
    /**
     * Initialize sample exercises
     */
    private void initializeSampleExercises() {
        // Mathematics exercises
        LabExercise algebra = new LabExercise(
            "MATH_ALG_001",
            "Linear Equations Practice",
            "Mathematics",
            "Grade 9",
            LabExercise.ExerciseType.SIMULATION
        );
        algebra.setDescription("Practice solving linear equations step-by-step");
        algebra.addLearningObjective("Solve linear equations with one variable");
        algebra.addLearningObjective("Apply inverse operations correctly");
        algebra.setDifficulty(LabExercise.DifficultyLevel.BEGINNER);
        algebra.setEstimatedMinutes(20);
        algebra.setCapsReference("CAPS Grade 9 Mathematics: Equations and Inequalities");
        labManager.addExercise(algebra);
        
        // Coding exercises
        LabExercise python = new LabExercise(
            "CODE_PY_001",
            "Python Basics - Introduction",
            "Information Technology",
            "Grade 10",
            LabExercise.ExerciseType.CODING
        );
        python.setDescription("Learn Python programming through interactive challenges");
        python.addLearningObjective("Understand variables and data types");
        python.addLearningObjective("Use conditional statements and loops");
        python.setDifficulty(LabExercise.DifficultyLevel.BEGINNER);
        python.setEstimatedMinutes(30);
        python.setCapsReference("CAPS Grade 10-12 IT: Programming and Software Development");
        labManager.addExercise(python);
        
        // Physics Lab
        LabExercise physics = new LabExercise(
            "PHYS_MECH_001",
            "Physics Lab - Real Mechanics",
            "Physical Sciences",
            "Grade 11",
            LabExercise.ExerciseType.EXPERIMENT
        );
        physics.setDescription("Explore real physics with projectile motion, pendulum, collisions, springs, and inclined planes");
        physics.addLearningObjective("Understand Newtonian mechanics");
        physics.addLearningObjective("Apply conservation laws");
        physics.addLearningObjective("Analyze force and motion");
        physics.setDifficulty(LabExercise.DifficultyLevel.INTERMEDIATE);
        physics.setEstimatedMinutes(40);
        physics.setCapsReference("CAPS Grade 11-12 Physical Sciences: Mechanics");
        labManager.addExercise(physics);
        
        // Chemistry Lab
        LabExercise chemistry = new LabExercise(
            "CHEM_MOL_001",
            "Chemistry Lab - Molecular Structures",
            "Physical Sciences",
            "Grade 10",
            LabExercise.ExerciseType.EXPERIMENT
        );
        chemistry.setDescription("Explore molecular structures, chemical bonds, and molar mass calculations");
        chemistry.addLearningObjective("Understand molecular geometry");
        chemistry.addLearningObjective("Identify chemical bonds");
        chemistry.addLearningObjective("Calculate molar mass");
        chemistry.setDifficulty(LabExercise.DifficultyLevel.INTERMEDIATE);
        chemistry.setEstimatedMinutes(35);
        chemistry.setCapsReference("CAPS Grade 10-12 Physical Sciences: Chemical Systems");
        labManager.addExercise(chemistry);
        
        // Circuit Lab
        LabExercise circuit = new LabExercise(
            "CIRC_ELEC_001",
            "Circuit Lab - Real Electronics",
            "Physical Sciences",
            "Grade 11",
            LabExercise.ExerciseType.EXPERIMENT
        );
        circuit.setDescription("Build and analyze electrical circuits using Ohm's Law");
        circuit.addLearningObjective("Understand Ohm's Law (V=IR)");
        circuit.addLearningObjective("Analyze series and parallel circuits");
        circuit.addLearningObjective("Calculate current, voltage, and power");
        circuit.setDifficulty(LabExercise.DifficultyLevel.INTERMEDIATE);
        circuit.setEstimatedMinutes(30);
        circuit.setCapsReference("CAPS Grade 11 Physical Sciences: Electricity & Magnetism");
        labManager.addExercise(circuit);
    }
    
    /**
     * Initialize user interface
     */
    private void initializeUI() {
        setTitle("Virtual Labs - LearnMax Enhanced");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Header with Ubuntu quote
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("🔬 Virtual Labs - Hands-On Learning");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JLabel quoteLabel = new JLabel("\"Ubuntu: Umuntu ngumuntu ngabantu\" - Learn by doing, together");
        quoteLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        quoteLabel.setForeground(Color.WHITE);
        headerPanel.add(quoteLabel, BorderLayout.SOUTH);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Main tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Tab 1: Available Exercises
        tabbedPane.addTab("📚 Available Exercises", createExercisesPanel());
        
        // Tab 2: My Activities
        tabbedPane.addTab("📊 My Activities", createActivitiesPanel());
        
        // Tab 3: Progress Report
        tabbedPane.addTab("📈 Progress Report", createProgressPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        statusLabel = new JLabel(" Ready");
        statusPanel.add(statusLabel, BorderLayout.WEST);
        
        JPanel rightStatus = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        syncLabel = new JLabel("🔄 Synced");
        syncLabel.setForeground(SUCCESS);
        rightStatus.add(syncLabel);
        
        syncButton = new JButton("Sync Now");
        syncButton.setBackground(PRIMARY);
        syncButton.setForeground(Color.WHITE);
        syncButton.addActionListener(e -> synchronize());
        rightStatus.add(syncButton);
        
        statusPanel.add(rightStatus, BorderLayout.EAST);
        
        add(statusPanel, BorderLayout.SOUTH);
        
        updateSyncStatus();
    }
    
    /**
     * Create exercises panel
     */
    private JPanel createExercisesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Filters
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        filterPanel.add(new JLabel("Subject:"));
        subjectFilter = new JComboBox<>(new String[]{"All", "Mathematics", "Information Technology", "Science"});
        subjectFilter.addActionListener(e -> filterExercises());
        filterPanel.add(subjectFilter);
        
        filterPanel.add(new JLabel("Grade:"));
        gradeFilter = new JComboBox<>(new String[]{"All", "Grade 8", "Grade 9", "Grade 10", "Grade 11", "Grade 12"});
        gradeFilter.addActionListener(e -> filterExercises());
        filterPanel.add(gradeFilter);
        
        refreshButton = new JButton("🔄 Refresh");
        refreshButton.addActionListener(e -> loadExercises());
        filterPanel.add(refreshButton);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        
        // Exercises table
        String[] columns = {"ID", "Title", "Subject", "Grade", "Type", "Difficulty", "Time (min)", "Status"};
        exercisesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        exercisesTable = new JTable(exercisesTableModel);
        exercisesTable.setRowHeight(25);
        exercisesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(exercisesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        launchButton = new JButton("🚀 Launch Exercise");
        launchButton.setFont(new Font("Arial", Font.BOLD, 14));
        launchButton.setBackground(SUCCESS);
        launchButton.setForeground(Color.WHITE);
        launchButton.setPreferredSize(new Dimension(180, 40));
        launchButton.addActionListener(e -> launchExercise());
        buttonPanel.add(launchButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Create activities panel
     */
    private JPanel createActivitiesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Activities table
        String[] columns = {"Exercise", "Status", "Score", "Time Spent", "Last Access", "Feedback"};
        activitiesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        activitiesTable = new JTable(activitiesTableModel);
        activitiesTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(activitiesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create progress panel
     */
    private JPanel createProgressPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        progressArea = new JTextArea();
        progressArea.setEditable(false);
        progressArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(progressArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JButton generateButton = new JButton("📊 Generate Report");
        generateButton.setBackground(PRIMARY);
        generateButton.setForeground(Color.WHITE);
        generateButton.addActionListener(e -> generateReport());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(generateButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Load all data
     */
    private void loadData() {
        loadExercises();
        loadActivities();
        generateReport();
    }
    
    /**
     * Load exercises into table
     */
    private void loadExercises() {
        exercisesTableModel.setRowCount(0);
        
        List<LabExercise> exercises = labManager.getAllExercises();
        
        for (LabExercise exercise : exercises) {
            String status = hasCompleted(exercise.getId()) ? "✓ Completed" : "Available";
            
            exercisesTableModel.addRow(new Object[]{
                exercise.getId(),
                exercise.getTitle(),
                exercise.getSubject(),
                exercise.getGradeLevel(),
                exercise.getType(),
                exercise.getDifficulty(),
                exercise.getEstimatedMinutes(),
                status
            });
        }
        
        statusLabel.setText(" Loaded " + exercises.size() + " exercises");
    }
    
    /**
     * Filter exercises
     */
    private void filterExercises() {
        String subject = (String) subjectFilter.getSelectedItem();
        String grade = (String) gradeFilter.getSelectedItem();
        
        exercisesTableModel.setRowCount(0);
        
        List<LabExercise> exercises = labManager.getAllExercises();
        
        for (LabExercise exercise : exercises) {
            boolean matchSubject = subject.equals("All") || exercise.getSubject().equals(subject);
            boolean matchGrade = grade.equals("All") || exercise.getGradeLevel().equals(grade);
            
            if (matchSubject && matchGrade) {
                String status = hasCompleted(exercise.getId()) ? "✓ Completed" : "Available";
                
                exercisesTableModel.addRow(new Object[]{
                    exercise.getId(),
                    exercise.getTitle(),
                    exercise.getSubject(),
                    exercise.getGradeLevel(),
                    exercise.getType(),
                    exercise.getDifficulty(),
                    exercise.getEstimatedMinutes(),
                    status
                });
            }
        }
    }
    
    /**
     * Check if exercise is completed
     */
    private boolean hasCompleted(String exerciseId) {
        return labManager.getStudentActivities(currentStudentId).stream()
                .anyMatch(act -> act.getExerciseId().equals(exerciseId) && act.isCompleted());
    }
    
    /**
     * Load activities into table
     */
    private void loadActivities() {
        activitiesTableModel.setRowCount(0);
        
        List<LabActivity> activities = labManager.getStudentActivities(currentStudentId);
        
        for (LabActivity activity : activities) {
            LabExercise exercise = labManager.getExercise(activity.getExerciseId());
            String exerciseTitle = exercise != null ? exercise.getTitle() : activity.getExerciseId();
            
            String score = activity.getScore() >= 0 ? String.format("%.1f%%", activity.getScore()) : "N/A";
            String feedback = activity.getAiFeedback().isEmpty() ? "None" : 
                             activity.getAiFeedback().substring(0, Math.min(50, activity.getAiFeedback().length())) + "...";
            
            activitiesTableModel.addRow(new Object[]{
                exerciseTitle,
                activity.getStatus().getDisplayName(),
                score,
                activity.getTimeSpentMinutes() + " min",
                activity.getFormattedStartTime(),
                feedback
            });
        }
    }
    
    /**
     * Launch selected exercise
     */
    private void launchExercise() {
        int selectedRow = exercisesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an exercise to launch", 
                                        "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String exerciseId = (String) exercisesTableModel.getValueAt(selectedRow, 0);
        LabExercise exercise = labManager.getExercise(exerciseId);
        
        if (exercise == null) {
            JOptionPane.showMessageDialog(this, "Exercise not found", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Start activity tracking
        LabActivity activity = labManager.startActivity(currentStudentId, exerciseId);
        
        // Launch appropriate simulator
        if (exerciseId.startsWith("MATH_ALG")) {
            AlgebraSimulator simulator = new AlgebraSimulator(exercise);
            simulator.setVisible(true);
            
            // When simulator closes, update activity
            simulator.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    double score = simulator.getScore();
                    int timeSpent = simulator.getTimeSpentMinutes();
                    String feedback = generateAIFeedback(score);
                    
                    labManager.completeActivity(activity.getActivityId(), score, feedback);
                    loadActivities();
                    loadExercises();
                    generateReport();
                }
            });
        } else if (exerciseId.startsWith("CODE_PY")) {
            PythonPlayground playground = new PythonPlayground(exercise);
            playground.setVisible(true);
            
            playground.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    double score = playground.getScore();
                    String feedback = generateAIFeedback(score);
                    
                    labManager.completeActivity(activity.getActivityId(), score, feedback);
                    loadActivities();
                    loadExercises();
                    generateReport();
                }
            });
        } else if (exerciseId.startsWith("PHYS_")) {
            PhysicsLab physicsLab = new PhysicsLab(exercise);
            physicsLab.setVisible(true);
            
            physicsLab.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    double score = 85.0; // Physics lab auto-scores based on completion
                    String feedback = generateAIFeedback(score);
                    
                    labManager.completeActivity(activity.getActivityId(), score, feedback);
                    loadActivities();
                    loadExercises();
                    generateReport();
                }
            });
        } else if (exerciseId.startsWith("CHEM_")) {
            ChemistryLab chemLab = new ChemistryLab(exercise);
            chemLab.setVisible(true);
            
            chemLab.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    double score = 85.0; // Chemistry lab auto-scores based on completion
                    String feedback = generateAIFeedback(score);
                    
                    labManager.completeActivity(activity.getActivityId(), score, feedback);
                    loadActivities();
                    loadExercises();
                    generateReport();
                }
            });
        } else if (exerciseId.startsWith("CIRC_")) {
            CircuitLab circuitLab = new CircuitLab(exercise);
            circuitLab.setVisible(true);
            
            circuitLab.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    double score = 85.0; // Circuit lab auto-scores based on completion
                    String feedback = generateAIFeedback(score);
                    
                    labManager.completeActivity(activity.getActivityId(), score, feedback);
                    loadActivities();
                    loadExercises();
                    generateReport();
                }
            });
        } else {
            JOptionPane.showMessageDialog(this, 
                "This simulator is not yet implemented.\nComing soon!", 
                "Under Development", JOptionPane.INFORMATION_MESSAGE);
        }
        
        statusLabel.setText(" Launched: " + exercise.getTitle());
    }
    
    /**
     * Generate AI feedback based on score
     */
    private String generateAIFeedback(double score) {
        if (score >= 90) {
            return "Excellent work! You've demonstrated mastery of this concept. " +
                   "Ubuntu wisdom: Your success inspires others to learn!";
        } else if (score >= 75) {
            return "Great job! You're doing well. Review the areas where you made mistakes " +
                   "and try again to achieve mastery.";
        } else if (score >= 50) {
            return "Good effort! You're making progress. Spend more time practicing this topic " +
                   "and use the hints when needed. Remember: persistence leads to mastery!";
        } else {
            return "Keep learning! This topic needs more practice. Don't give up - " +
                   "every expert was once a beginner. Review the steps carefully and try again.";
        }
    }
    
    /**
     * Generate progress report
     */
    private void generateReport() {
        String report = labManager.generateProgressReport(currentStudentId);
        progressArea.setText(report);
    }
    
    /**
     * Synchronize data
     */
    private void synchronize() {
        syncLabel.setText("🔄 Syncing...");
        syncLabel.setForeground(WARNING);
        
        boolean success = labManager.synchronize();
        
        if (success) {
            syncLabel.setText("✓ Synced");
            syncLabel.setForeground(SUCCESS);
            statusLabel.setText(" Synchronization complete");
        } else {
            syncLabel.setText("✗ Sync Failed");
            syncLabel.setForeground(DANGER);
            statusLabel.setText(" Synchronization failed");
        }
        
        updateSyncStatus();
    }
    
    /**
     * Update sync status
     */
    private void updateSyncStatus() {
        int pending = labManager.getPendingSyncCount();
        if (pending > 0) {
            syncLabel.setText("⚠ " + pending + " pending");
            syncLabel.setForeground(WARNING);
        }
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VirtualLabsGUI gui = new VirtualLabsGUI("S001");
            gui.setVisible(true);
        });
    }
}
