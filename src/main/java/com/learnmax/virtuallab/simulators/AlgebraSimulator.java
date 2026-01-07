package com.learnmax.virtuallab.simulators;

import com.learnmax.virtuallab.model.LabExercise;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Interactive Algebra Simulator for solving equations step-by-step
 * CAPS Aligned: Grade 8-12 Mathematics
 */
public class AlgebraSimulator extends JFrame {
    
    private LabExercise exercise;
    private JTextArea problemArea;
    private JTextField answerField;
    private JTextArea stepsArea;
    private JButton checkButton, hintButton, nextButton, showStepsButton;
    private JLabel feedbackLabel, scoreLabel;
    private JProgressBar progressBar;
    
    private List<AlgebraProblem> problems;
    private int currentProblemIndex;
    private int correctAnswers;
    private int hintsUsed;
    private long startTime;
    
    /**
     * Algebra problem structure
     */
    private static class AlgebraProblem {
        String equation;
        String solution;
        List<String> steps;
        String hint;
        int difficulty;
        
        AlgebraProblem(String equation, String solution, List<String> steps, String hint, int difficulty) {
            this.equation = equation;
            this.solution = solution;
            this.steps = steps;
            this.hint = hint;
            this.difficulty = difficulty;
        }
    }
    
    /**
     * Constructor
     */
    public AlgebraSimulator(LabExercise exercise) {
        this.exercise = exercise;
        this.currentProblemIndex = 0;
        this.correctAnswers = 0;
        this.hintsUsed = 0;
        this.startTime = System.currentTimeMillis();
        
        initializeProblems();
        initializeUI();
        loadProblem();
    }
    
    /**
     * Initialize algebra problems based on difficulty
     */
    private void initializeProblems() {
        problems = new ArrayList<>();
        
        // Beginner level problems
        problems.add(new AlgebraProblem(
            "Solve for x: 2x + 5 = 13",
            "4",
            Arrays.asList(
                "Step 1: Subtract 5 from both sides: 2x = 8",
                "Step 2: Divide both sides by 2: x = 4"
            ),
            "Try subtracting 5 from both sides first",
            1
        ));
        
        problems.add(new AlgebraProblem(
            "Solve for x: 3x - 7 = 11",
            "6",
            Arrays.asList(
                "Step 1: Add 7 to both sides: 3x = 18",
                "Step 2: Divide both sides by 3: x = 6"
            ),
            "What operation is the opposite of subtraction?",
            1
        ));
        
        problems.add(new AlgebraProblem(
            "Solve for x: 5x + 2 = 3x + 10",
            "4",
            Arrays.asList(
                "Step 1: Subtract 3x from both sides: 2x + 2 = 10",
                "Step 2: Subtract 2 from both sides: 2x = 8",
                "Step 3: Divide both sides by 2: x = 4"
            ),
            "Move all x terms to one side first",
            2
        ));
        
        // Intermediate level problems
        problems.add(new AlgebraProblem(
            "Solve for x: 2(x + 3) = 14",
            "4",
            Arrays.asList(
                "Step 1: Expand brackets: 2x + 6 = 14",
                "Step 2: Subtract 6 from both sides: 2x = 8",
                "Step 3: Divide both sides by 2: x = 4"
            ),
            "Remember to distribute the 2 to both terms inside the brackets",
            2
        ));
        
        problems.add(new AlgebraProblem(
            "Solve for x: (x/2) + 3 = 7",
            "8",
            Arrays.asList(
                "Step 1: Subtract 3 from both sides: x/2 = 4",
                "Step 2: Multiply both sides by 2: x = 8"
            ),
            "What's the opposite operation of division?",
            2
        ));
        
        // Advanced level problems
        problems.add(new AlgebraProblem(
            "Solve for x: 3(2x - 1) + 4 = 2(x + 5)",
            "3",
            Arrays.asList(
                "Step 1: Expand brackets: 6x - 3 + 4 = 2x + 10",
                "Step 2: Simplify: 6x + 1 = 2x + 10",
                "Step 3: Subtract 2x from both sides: 4x + 1 = 10",
                "Step 4: Subtract 1 from both sides: 4x = 9",
                "Step 5: Divide both sides by 4: x = 2.25"
            ),
            "Expand both sides first, then collect like terms",
            3
        ));
        
        Collections.shuffle(problems);
    }
    
    /**
     * Initialize user interface
     */
    private void initializeUI() {
        setTitle("Algebra Simulator - " + exercise.getTitle());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Ubuntu motivation quote
        JLabel quoteLabel = new JLabel("\"Ubuntu: I am because we are\" - Learn together, grow together");
        quoteLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        quoteLabel.setHorizontalAlignment(SwingConstants.CENTER);
        quoteLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(quoteLabel, BorderLayout.NORTH);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Problem display
        JPanel problemPanel = new JPanel(new BorderLayout(5, 5));
        problemPanel.setBorder(BorderFactory.createTitledBorder("Problem"));
        
        problemArea = new JTextArea(3, 40);
        problemArea.setEditable(false);
        problemArea.setFont(new Font("Arial", Font.BOLD, 18));
        problemArea.setLineWrap(true);
        problemArea.setWrapStyleWord(true);
        problemPanel.add(new JScrollPane(problemArea), BorderLayout.CENTER);
        
        mainPanel.add(problemPanel, BorderLayout.NORTH);
        
        // Answer input
        JPanel answerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        answerPanel.add(new JLabel("Your Answer: x = "));
        answerField = new JTextField(15);
        answerField.setFont(new Font("Arial", Font.PLAIN, 16));
        answerPanel.add(answerField);
        
        checkButton = new JButton("Check Answer");
        checkButton.setBackground(new Color(52, 152, 219));
        checkButton.setForeground(Color.WHITE);
        checkButton.addActionListener(e -> checkAnswer());
        answerPanel.add(checkButton);
        
        hintButton = new JButton("Get Hint");
        hintButton.setBackground(new Color(241, 196, 15));
        hintButton.addActionListener(e -> showHint());
        answerPanel.add(hintButton);
        
        showStepsButton = new JButton("Show Steps");
        showStepsButton.setBackground(new Color(155, 89, 182));
        showStepsButton.setForeground(Color.WHITE);
        showStepsButton.addActionListener(e -> showSteps());
        answerPanel.add(showStepsButton);
        
        mainPanel.add(answerPanel, BorderLayout.CENTER);
        
        // Steps display
        JPanel stepsPanel = new JPanel(new BorderLayout());
        stepsPanel.setBorder(BorderFactory.createTitledBorder("Solution Steps"));
        
        stepsArea = new JTextArea(8, 40);
        stepsArea.setEditable(false);
        stepsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        stepsArea.setLineWrap(true);
        stepsArea.setWrapStyleWord(true);
        stepsPanel.add(new JScrollPane(stepsArea), BorderLayout.CENTER);
        
        mainPanel.add(stepsPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Bottom panel with feedback and progress
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        feedbackLabel = new JLabel(" ");
        feedbackLabel.setFont(new Font("Arial", Font.BOLD, 14));
        feedbackLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bottomPanel.add(feedbackLabel, BorderLayout.NORTH);
        
        JPanel progressPanel = new JPanel(new FlowLayout());
        scoreLabel = new JLabel("Score: 0/" + problems.size());
        progressPanel.add(scoreLabel);
        
        progressBar = new JProgressBar(0, problems.size());
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressPanel.add(progressBar);
        
        nextButton = new JButton("Next Problem");
        nextButton.setBackground(new Color(46, 204, 113));
        nextButton.setForeground(Color.WHITE);
        nextButton.setEnabled(false);
        nextButton.addActionListener(e -> nextProblem());
        progressPanel.add(nextButton);
        
        bottomPanel.add(progressPanel, BorderLayout.CENTER);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Enter key to check answer
        answerField.addActionListener(e -> checkAnswer());
    }
    
    /**
     * Load current problem
     */
    private void loadProblem() {
        if (currentProblemIndex >= problems.size()) {
            showFinalResults();
            return;
        }
        
        AlgebraProblem problem = problems.get(currentProblemIndex);
        problemArea.setText(problem.equation);
        answerField.setText("");
        stepsArea.setText("");
        feedbackLabel.setText(" ");
        feedbackLabel.setForeground(Color.BLACK);
        
        checkButton.setEnabled(true);
        hintButton.setEnabled(true);
        showStepsButton.setEnabled(true);
        nextButton.setEnabled(false);
        
        answerField.requestFocus();
    }
    
    /**
     * Check student's answer
     */
    private void checkAnswer() {
        String userAnswer = answerField.getText().trim();
        if (userAnswer.isEmpty()) {
            feedbackLabel.setText("Please enter an answer");
            feedbackLabel.setForeground(Color.ORANGE);
            return;
        }
        
        AlgebraProblem problem = problems.get(currentProblemIndex);
        
        try {
            // Normalize answers (handle decimals and fractions)
            double userValue = Double.parseDouble(userAnswer);
            double correctValue = Double.parseDouble(problem.solution);
            
            if (Math.abs(userValue - correctValue) < 0.01) {
                correctAnswers++;
                feedbackLabel.setText("✓ Correct! Well done!");
                feedbackLabel.setForeground(new Color(46, 204, 113));
                showSteps();
                progressBar.setValue(currentProblemIndex + 1);
                scoreLabel.setText("Score: " + correctAnswers + "/" + problems.size());
                
                checkButton.setEnabled(false);
                hintButton.setEnabled(false);
                showStepsButton.setEnabled(false);
                nextButton.setEnabled(true);
            } else {
                feedbackLabel.setText("✗ Not quite right. Try again or use a hint!");
                feedbackLabel.setForeground(new Color(231, 76, 60));
            }
        } catch (NumberFormatException e) {
            feedbackLabel.setText("Please enter a valid number");
            feedbackLabel.setForeground(Color.ORANGE);
        }
    }
    
    /**
     * Show hint
     */
    private void showHint() {
        AlgebraProblem problem = problems.get(currentProblemIndex);
        hintsUsed++;
        JOptionPane.showMessageDialog(this,
            "💡 Hint: " + problem.hint,
            "AI Tutor Hint",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Show solution steps
     */
    private void showSteps() {
        AlgebraProblem problem = problems.get(currentProblemIndex);
        StringBuilder steps = new StringBuilder();
        steps.append("Solution:\n\n");
        for (String step : problem.steps) {
            steps.append(step).append("\n");
        }
        steps.append("\nFinal Answer: x = ").append(problem.solution);
        stepsArea.setText(steps.toString());
    }
    
    /**
     * Move to next problem
     */
    private void nextProblem() {
        currentProblemIndex++;
        loadProblem();
    }
    
    /**
     * Show final results
     */
    private void showFinalResults() {
        long timeSpent = (System.currentTimeMillis() - startTime) / 60000; // minutes
        double percentage = (correctAnswers * 100.0) / problems.size();
        
        String performance;
        if (percentage >= 90) {
            performance = "Excellent! You've mastered algebra!";
        } else if (percentage >= 75) {
            performance = "Great job! Keep practicing!";
        } else if (percentage >= 50) {
            performance = "Good effort! Review the steps and try again.";
        } else {
            performance = "Keep learning! Practice makes perfect.";
        }
        
        String message = String.format(
            "Algebra Simulator Complete!\n\n" +
            "Score: %d/%d (%.1f%%)\n" +
            "Time Spent: %d minutes\n" +
            "Hints Used: %d\n\n" +
            "%s\n\n" +
            "Ubuntu wisdom: \"A person is a person through other persons.\"\n" +
            "Your learning journey helps others learn too!",
            correctAnswers, problems.size(), percentage, timeSpent, hintsUsed, performance
        );
        
        JOptionPane.showMessageDialog(this, message, "Results", JOptionPane.INFORMATION_MESSAGE);
        
        // Return score for tracking
        dispose();
    }
    
    /**
     * Get final score
     */
    public double getScore() {
        return (correctAnswers * 100.0) / problems.size();
    }
    
    /**
     * Get time spent in minutes
     */
    public int getTimeSpentMinutes() {
        return (int) ((System.currentTimeMillis() - startTime) / 60000);
    }
    
    /**
     * Main method for standalone testing
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LabExercise exercise = new LabExercise(
                "MATH_ALG_001",
                "Linear Equations Practice",
                "Mathematics",
                "Grade 9",
                LabExercise.ExerciseType.SIMULATION
            );
            exercise.setDescription("Practice solving linear equations step-by-step");
            exercise.addLearningObjective("Solve linear equations with one variable");
            exercise.addLearningObjective("Apply inverse operations correctly");
            exercise.setCapsReference("CAPS Grade 9 Mathematics: Equations and Inequalities");
            
            AlgebraSimulator simulator = new AlgebraSimulator(exercise);
            simulator.setVisible(true);
        });
    }
}
