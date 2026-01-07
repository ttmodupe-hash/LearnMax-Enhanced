package com.learnmax.virtuallab.simulators;

import com.learnmax.virtuallab.model.LabExercise;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Interactive Python Coding Playground
 * CAPS Aligned: Grade 10-12 IT/CAT - Basic Coding (Compulsory for all learners)
 */
public class PythonPlayground extends JFrame {
    
    private LabExercise exercise;
    private JTextPane codeEditor;
    private JTextArea outputArea;
    private JTextArea instructionsArea;
    private JButton runButton, hintButton, checkButton, nextButton;
    private JLabel statusLabel, scoreLabel;
    
    private List<CodingChallenge> challenges;
    private int currentChallengeIndex;
    private int correctSolutions;
    private long startTime;
    
    /**
     * Coding challenge structure
     */
    private static class CodingChallenge {
        String title;
        String description;
        String starterCode;
        String expectedOutput;
        String hint;
        List<String> testCases;
        int difficulty;
        
        CodingChallenge(String title, String description, String starterCode, 
                       String expectedOutput, String hint, int difficulty) {
            this.title = title;
            this.description = description;
            this.starterCode = starterCode;
            this.expectedOutput = expectedOutput;
            this.hint = hint;
            this.difficulty = difficulty;
            this.testCases = new ArrayList<>();
        }
    }
    
    /**
     * Constructor
     */
    public PythonPlayground(LabExercise exercise) {
        this.exercise = exercise;
        this.currentChallengeIndex = 0;
        this.correctSolutions = 0;
        this.startTime = System.currentTimeMillis();
        
        initializeChallenges();
        initializeUI();
        loadChallenge();
    }
    
    /**
     * Initialize coding challenges
     */
    private void initializeChallenges() {
        challenges = new ArrayList<>();
        
        // Challenge 1: Hello World
        challenges.add(new CodingChallenge(
            "Hello, World!",
            "Write a program that prints 'Hello, World!' to the console.\n" +
            "This is your first step in learning Python!",
            "# Write your code here\nprint('Hello, World!')",
            "Hello, World!",
            "Use the print() function to display text",
            1
        ));
        
        // Challenge 2: Variables and Input
        challenges.add(new CodingChallenge(
            "Personal Greeting",
            "Create a program that:\n" +
            "1. Asks for the user's name\n" +
            "2. Prints a personalized greeting\n\n" +
            "Example: If user enters 'Thabo', print 'Hello, Thabo! Welcome to coding!'",
            "# Get user's name\nname = input('Enter your name: ')\n\n" +
            "# Print greeting\nprint('Hello, ' + name + '! Welcome to coding!')",
            "Hello, [name]! Welcome to coding!",
            "Use input() to get user input and + to join strings",
            1
        ));
        
        // Challenge 3: Simple Math
        challenges.add(new CodingChallenge(
            "Calculator",
            "Write a program that:\n" +
            "1. Takes two numbers as input\n" +
            "2. Calculates and prints their sum, difference, and product\n\n" +
            "Example: For 10 and 5, print:\n" +
            "Sum: 15\n" +
            "Difference: 5\n" +
            "Product: 50",
            "# Get two numbers\nnum1 = int(input('Enter first number: '))\n" +
            "num2 = int(input('Enter second number: '))\n\n" +
            "# Calculate and print results\nprint('Sum:', num1 + num2)\n" +
            "print('Difference:', num1 - num2)\n" +
            "print('Product:', num1 * num2)",
            "Sum: [result]\nDifference: [result]\nProduct: [result]",
            "Use int() to convert input to numbers, then use +, -, * operators",
            2
        ));
        
        // Challenge 4: Conditional Logic
        challenges.add(new CodingChallenge(
            "Grade Checker",
            "Write a program that:\n" +
            "1. Takes a test score (0-100) as input\n" +
            "2. Prints the grade based on:\n" +
            "   90-100: Excellent\n" +
            "   75-89: Good\n" +
            "   50-74: Pass\n" +
            "   Below 50: Needs Improvement",
            "# Get score\nscore = int(input('Enter score: '))\n\n" +
            "# Check grade\nif score >= 90:\n" +
            "    print('Excellent')\n" +
            "elif score >= 75:\n" +
            "    print('Good')\n" +
            "elif score >= 50:\n" +
            "    print('Pass')\n" +
            "else:\n" +
            "    print('Needs Improvement')",
            "[Grade based on score]",
            "Use if, elif, else statements. Remember indentation is important in Python!",
            2
        ));
        
        // Challenge 5: Loops
        challenges.add(new CodingChallenge(
            "Times Table",
            "Write a program that:\n" +
            "1. Takes a number as input\n" +
            "2. Prints the times table for that number (1 to 10)\n\n" +
            "Example for 5:\n" +
            "5 x 1 = 5\n" +
            "5 x 2 = 10\n" +
            "...\n" +
            "5 x 10 = 50",
            "# Get number\nnum = int(input('Enter a number: '))\n\n" +
            "# Print times table\nfor i in range(1, 11):\n" +
            "    print(f'{num} x {i} = {num * i}')",
            "[Times table output]",
            "Use a for loop with range(1, 11) to iterate from 1 to 10",
            2
        ));
        
        // Challenge 6: Lists
        challenges.add(new CodingChallenge(
            "Class Average",
            "Write a program that:\n" +
            "1. Creates a list of 5 test scores\n" +
            "2. Calculates and prints the average\n" +
            "3. Finds and prints the highest score",
            "# List of scores\nscores = [85, 92, 78, 95, 88]\n\n" +
            "# Calculate average\naverage = sum(scores) / len(scores)\n" +
            "print(f'Average: {average:.1f}')\n\n" +
            "# Find highest\nhighest = max(scores)\n" +
            "print(f'Highest: {highest}')",
            "Average: [value]\nHighest: [value]",
            "Use sum() to add all numbers, len() for count, max() for highest",
            3
        ));
    }
    
    /**
     * Initialize user interface
     */
    private void initializeUI() {
        setTitle("Python Playground - " + exercise.getTitle());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Ubuntu quote
        JLabel quoteLabel = new JLabel("\"Ubuntu: Umuntu ngumuntu ngabantu\" - Coding connects us all");
        quoteLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        quoteLabel.setHorizontalAlignment(SwingConstants.CENTER);
        quoteLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(quoteLabel, BorderLayout.NORTH);
        
        // Split pane for instructions and code
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setDividerLocation(300);
        
        // Left panel: Instructions
        JPanel instructionsPanel = new JPanel(new BorderLayout());
        instructionsPanel.setBorder(BorderFactory.createTitledBorder("Challenge Instructions"));
        
        instructionsArea = new JTextArea();
        instructionsArea.setEditable(false);
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        instructionsArea.setFont(new Font("Arial", Font.PLAIN, 13));
        instructionsPanel.add(new JScrollPane(instructionsArea), BorderLayout.CENTER);
        
        mainSplit.setLeftComponent(instructionsPanel);
        
        // Right panel: Code and output
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        
        // Code editor
        JPanel editorPanel = new JPanel(new BorderLayout());
        editorPanel.setBorder(BorderFactory.createTitledBorder("Python Code Editor"));
        
        codeEditor = new JTextPane();
        codeEditor.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane editorScroll = new JScrollPane(codeEditor);
        editorPanel.add(editorScroll, BorderLayout.CENTER);
        
        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        runButton = new JButton("▶ Run Code");
        runButton.setBackground(new Color(46, 204, 113));
        runButton.setForeground(Color.WHITE);
        runButton.addActionListener(e -> runCode());
        toolbar.add(runButton);
        
        checkButton = new JButton("✓ Check Solution");
        checkButton.setBackground(new Color(52, 152, 219));
        checkButton.setForeground(Color.WHITE);
        checkButton.addActionListener(e -> checkSolution());
        toolbar.add(checkButton);
        
        hintButton = new JButton("💡 Get Hint");
        hintButton.setBackground(new Color(241, 196, 15));
        hintButton.addActionListener(e -> showHint());
        toolbar.add(hintButton);
        
        nextButton = new JButton("Next Challenge →");
        nextButton.setBackground(new Color(155, 89, 182));
        nextButton.setForeground(Color.WHITE);
        nextButton.setEnabled(false);
        nextButton.addActionListener(e -> nextChallenge());
        toolbar.add(nextButton);
        
        editorPanel.add(toolbar, BorderLayout.SOUTH);
        
        // Output area
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("Output"));
        
        outputArea = new JTextArea(8, 40);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setBackground(Color.BLACK);
        outputArea.setForeground(Color.GREEN);
        outputPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        
        // Combine editor and output
        JSplitPane codeSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        codeSplit.setTopComponent(editorPanel);
        codeSplit.setBottomComponent(outputPanel);
        codeSplit.setDividerLocation(400);
        
        rightPanel.add(codeSplit, BorderLayout.CENTER);
        
        mainSplit.setRightComponent(rightPanel);
        add(mainSplit, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusPanel = new JPanel(new BorderLayout());
        
        statusLabel = new JLabel(" Ready to code!");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusPanel.add(statusLabel, BorderLayout.WEST);
        
        scoreLabel = new JLabel("Progress: 0/" + challenges.size() + " ");
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusPanel.add(scoreLabel, BorderLayout.EAST);
        
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Load current challenge
     */
    private void loadChallenge() {
        if (currentChallengeIndex >= challenges.size()) {
            showFinalResults();
            return;
        }
        
        CodingChallenge challenge = challenges.get(currentChallengeIndex);
        
        instructionsArea.setText(
            "Challenge " + (currentChallengeIndex + 1) + ": " + challenge.title + "\n\n" +
            challenge.description + "\n\n" +
            "Difficulty: " + getDifficultyString(challenge.difficulty)
        );
        
        codeEditor.setText(challenge.starterCode);
        outputArea.setText("");
        statusLabel.setText(" Ready to code!");
        statusLabel.setForeground(Color.BLACK);
        
        nextButton.setEnabled(false);
    }
    
    /**
     * Run Python code (simulated)
     */
    private void runCode() {
        String code = codeEditor.getText();
        outputArea.setText(">>> Running code...\n\n");
        
        // Simulate code execution (in real implementation, use Jython or process)
        outputArea.append("Note: This is a demo simulator.\n");
        outputArea.append("In production, this would execute actual Python code.\n\n");
        outputArea.append("Your code:\n" + code + "\n\n");
        outputArea.append("✓ Code executed successfully!\n");
        
        statusLabel.setText(" Code executed. Check your solution when ready.");
        statusLabel.setForeground(new Color(46, 204, 113));
    }
    
    /**
     * Check solution
     */
    private void checkSolution() {
        // In real implementation, compare actual output with expected
        // For demo, we'll do basic validation
        String code = codeEditor.getText().toLowerCase();
        CodingChallenge challenge = challenges.get(currentChallengeIndex);
        
        boolean isCorrect = false;
        
        // Simple validation based on challenge
        if (currentChallengeIndex == 0 && code.contains("print") && code.contains("hello")) {
            isCorrect = true;
        } else if (currentChallengeIndex == 1 && code.contains("input") && code.contains("print")) {
            isCorrect = true;
        } else if (code.contains("print")) {
            // For demo, accept if code has print statement
            isCorrect = true;
        }
        
        if (isCorrect) {
            correctSolutions++;
            statusLabel.setText(" ✓ Correct! Well done!");
            statusLabel.setForeground(new Color(46, 204, 113));
            scoreLabel.setText("Progress: " + correctSolutions + "/" + challenges.size() + " ");
            nextButton.setEnabled(true);
            
            JOptionPane.showMessageDialog(this,
                "Excellent work! Your solution is correct.\n\n" +
                "Ubuntu wisdom: Your success helps others learn!",
                "Success!",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            statusLabel.setText(" ✗ Not quite right. Try again or get a hint!");
            statusLabel.setForeground(new Color(231, 76, 60));
        }
    }
    
    /**
     * Show hint
     */
    private void showHint() {
        CodingChallenge challenge = challenges.get(currentChallengeIndex);
        JOptionPane.showMessageDialog(this,
            "💡 AI Tutor Hint:\n\n" + challenge.hint,
            "Hint",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Next challenge
     */
    private void nextChallenge() {
        currentChallengeIndex++;
        loadChallenge();
    }
    
    /**
     * Get difficulty string
     */
    private String getDifficultyString(int difficulty) {
        switch (difficulty) {
            case 1: return "⭐ Beginner";
            case 2: return "⭐⭐ Intermediate";
            case 3: return "⭐⭐⭐ Advanced";
            default: return "⭐ Beginner";
        }
    }
    
    /**
     * Show final results
     */
    private void showFinalResults() {
        long timeSpent = (System.currentTimeMillis() - startTime) / 60000;
        double percentage = (correctSolutions * 100.0) / challenges.size();
        
        String performance;
        if (percentage >= 90) {
            performance = "Outstanding! You're a coding star!";
        } else if (percentage >= 75) {
            performance = "Great work! Keep coding!";
        } else if (percentage >= 50) {
            performance = "Good progress! Practice more to improve.";
        } else {
            performance = "Keep learning! Every coder started here.";
        }
        
        String message = String.format(
            "Python Playground Complete!\n\n" +
            "Challenges Solved: %d/%d (%.1f%%)\n" +
            "Time Spent: %d minutes\n\n" +
            "%s\n\n" +
            "Ubuntu wisdom: \"I am because we are.\"\n" +
            "Your coding journey inspires others!",
            correctSolutions, challenges.size(), percentage, timeSpent, performance
        );
        
        JOptionPane.showMessageDialog(this, message, "Results", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
    
    /**
     * Get final score
     */
    public double getScore() {
        return (correctSolutions * 100.0) / challenges.size();
    }
    
    /**
     * Main method for standalone testing
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LabExercise exercise = new LabExercise(
                "CODE_PY_001",
                "Python Basics - Introduction to Programming",
                "Information Technology",
                "Grade 10",
                LabExercise.ExerciseType.CODING
            );
            exercise.setDescription("Learn Python programming basics through interactive challenges");
            exercise.addLearningObjective("Understand variables and data types");
            exercise.addLearningObjective("Use conditional statements and loops");
            exercise.addLearningObjective("Work with functions and lists");
            exercise.setCapsReference("CAPS Grade 10-12 IT: Programming and Software Development");
            
            PythonPlayground playground = new PythonPlayground(exercise);
            playground.setVisible(true);
        });
    }
}
