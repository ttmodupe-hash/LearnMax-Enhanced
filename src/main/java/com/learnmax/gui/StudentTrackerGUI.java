package com.learnmax.gui;

import com.learnmax.manager.StudentManager;
import com.learnmax.model.Student;
import com.learnmax.model.Assessment;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Enhanced Student Tracker GUI with modern design and comprehensive features
 * Features:
 * - Multiple assessment tracking
 * - Trend visualization
 * - Search and filter capabilities
 * - Detailed feedback display
 * - Class performance dashboard
 */
public class StudentTrackerGUI extends JFrame {
    private StudentManager manager;
    
    // Input components
    private JTextField idField, nameField, gradeField, subjectField, scoreField;
    private JComboBox<String> assessmentTypeCombo;
    private JTextArea outputArea;
    
    // Search components
    private JTextField searchField;
    private JComboBox<String> searchTypeCombo;
    
    // Table for student list
    private JTable studentTable;
    private DefaultTableModel tableModel;
    
    // Tabbed pane for different views
    private JTabbedPane tabbedPane;
    
    // Colors for modern design
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color DARK_BG = new Color(44, 62, 80);
    private static final Color LIGHT_BG = new Color(236, 240, 241);

    public StudentTrackerGUI() {
        manager = new StudentManager();
        
        setTitle("LearnMax Enhanced - Student Performance Tracker 2.0");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create components
        createMenuBar();
        createInputPanel();
        createMainContentArea();
        createStatusBar();
        
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    /**
     * Create menu bar with file operations
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(DARK_BG);
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(Color.WHITE);
        
        JMenuItem exportItem = new JMenuItem("Export to CSV");
        exportItem.addActionListener(e -> exportData());
        fileMenu.add(exportItem);
        
        JMenuItem refreshItem = new JMenuItem("Refresh Data");
        refreshItem.addActionListener(e -> refreshStudentList());
        fileMenu.add(refreshItem);
        
        fileMenu.addSeparator();
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        // Reports menu
        JMenu reportsMenu = new JMenu("Reports");
        reportsMenu.setForeground(Color.WHITE);
        
        JMenuItem classReportItem = new JMenuItem("Class Performance Report");
        classReportItem.addActionListener(e -> showClassReport());
        reportsMenu.add(classReportItem);
        
        JMenuItem underperformingItem = new JMenuItem("Underperforming Students");
        underperformingItem.addActionListener(e -> showUnderperformingStudents());
        reportsMenu.add(underperformingItem);
        
        JMenuItem topPerformersItem = new JMenuItem("Top Performers");
        topPerformersItem.addActionListener(e -> showTopPerformers());
        reportsMenu.add(topPerformersItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setForeground(Color.WHITE);
        
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(reportsMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    /**
     * Create input panel for student data entry
     */
    private void createInputPanel() {
        JPanel mainInputPanel = new JPanel(new BorderLayout(10, 10));
        mainInputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainInputPanel.setBackground(LIGHT_BG);
        
        // Student Information Panel
        JPanel studentInfoPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        studentInfoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            "Student Information",
            0, 0, new Font("Arial", Font.BOLD, 14), PRIMARY_COLOR));
        studentInfoPanel.setBackground(Color.WHITE);
        
        studentInfoPanel.add(createLabel("Student ID:"));
        idField = createTextField();
        studentInfoPanel.add(idField);
        
        studentInfoPanel.add(createLabel("Name:"));
        nameField = createTextField();
        studentInfoPanel.add(nameField);
        
        studentInfoPanel.add(createLabel("Grade Level:"));
        gradeField = createTextField();
        gradeField.setToolTipText("e.g., Grade 10, Grade 11, etc.");
        studentInfoPanel.add(gradeField);
        
        // Assessment Information Panel
        JPanel assessmentInfoPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        assessmentInfoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(SUCCESS_COLOR, 2),
            "Assessment Information",
            0, 0, new Font("Arial", Font.BOLD, 14), SUCCESS_COLOR));
        assessmentInfoPanel.setBackground(Color.WHITE);
        
        assessmentInfoPanel.add(createLabel("Subject:"));
        subjectField = createTextField();
        assessmentInfoPanel.add(subjectField);
        
        assessmentInfoPanel.add(createLabel("Score (0-100):"));
        scoreField = createTextField();
        assessmentInfoPanel.add(scoreField);
        
        assessmentInfoPanel.add(createLabel("Assessment Type:"));
        String[] assessmentTypes = {"Test", "Quiz", "Assignment", "Exam", "Project"};
        assessmentTypeCombo = new JComboBox<>(assessmentTypes);
        assessmentTypeCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        assessmentInfoPanel.add(assessmentTypeCombo);
        
        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(LIGHT_BG);
        
        JButton addButton = createStyledButton("Add Student", SUCCESS_COLOR);
        addButton.addActionListener(e -> addStudent());
        buttonPanel.add(addButton);
        
        JButton addAssessmentButton = createStyledButton("Add Assessment", PRIMARY_COLOR);
        addAssessmentButton.addActionListener(e -> addAssessment());
        buttonPanel.add(addAssessmentButton);
        
        JButton viewButton = createStyledButton("View Student", new Color(155, 89, 182));
        viewButton.addActionListener(e -> viewStudent());
        buttonPanel.add(viewButton);
        
        JButton trendButton = createStyledButton("Show Trends", WARNING_COLOR);
        trendButton.addActionListener(e -> showTrends());
        buttonPanel.add(trendButton);
        
        JButton clearButton = createStyledButton("Clear Fields", new Color(149, 165, 166));
        clearButton.addActionListener(e -> clearFields());
        buttonPanel.add(clearButton);
        
        // Assemble input panel
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        topPanel.setBackground(LIGHT_BG);
        topPanel.add(studentInfoPanel);
        topPanel.add(assessmentInfoPanel);
        
        mainInputPanel.add(topPanel, BorderLayout.CENTER);
        mainInputPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainInputPanel, BorderLayout.NORTH);
    }
    
    /**
     * Create main content area with tabs
     */
    private void createMainContentArea() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Student List Tab
        tabbedPane.addTab("📋 Student List", createStudentListPanel());
        
        // Details Tab
        tabbedPane.addTab("📊 Student Details", createDetailsPanel());
        
        // Dashboard Tab
        tabbedPane.addTab("📈 Class Dashboard", createDashboardPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    /**
     * Create student list panel with search
     */
    private JPanel createStudentListPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(LIGHT_BG);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Students"));
        
        searchPanel.add(createLabel("Search by:"));
        String[] searchTypes = {"Name", "ID", "Grade"};
        searchTypeCombo = new JComboBox<>(searchTypes);
        searchPanel.add(searchTypeCombo);
        
        searchField = createTextField();
        searchField.setPreferredSize(new Dimension(200, 30));
        searchPanel.add(searchField);
        
        JButton searchButton = createStyledButton("Search", PRIMARY_COLOR);
        searchButton.addActionListener(e -> performSearch());
        searchPanel.add(searchButton);
        
        JButton showAllButton = createStyledButton("Show All", new Color(149, 165, 166));
        showAllButton.addActionListener(e -> refreshStudentList());
        searchPanel.add(showAllButton);
        
        // Table
        String[] columns = {"ID", "Name", "Grade", "Subjects", "Assessments", "Average", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        studentTable = new JTable(tableModel);
        studentTable.setFont(new Font("Arial", Font.PLAIN, 12));
        studentTable.setRowHeight(25);
        studentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        studentTable.getTableHeader().setBackground(PRIMARY_COLOR);
        studentTable.getTableHeader().setForeground(Color.WHITE);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Add double-click listener
        studentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = studentTable.getSelectedRow();
                    if (row >= 0) {
                        String id = (String) tableModel.getValueAt(row, 0);
                        idField.setText(id);
                        viewStudent();
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1));
        
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load initial data
        refreshStudentList();
        
        return panel;
    }
    
    /**
     * Create details panel for student information
     */
    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(LIGHT_BG);
        
        outputArea = new JTextArea(20, 60);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        outputArea.setBackground(Color.WHITE);
        outputArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create dashboard panel with class statistics
     */
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(LIGHT_BG);
        
        // Statistics panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        statsPanel.setBackground(LIGHT_BG);
        
        // Add statistics cards
        statsPanel.add(createStatCard("Total Students", String.valueOf(manager.getStudentCount()), PRIMARY_COLOR));
        
        double classAvg = manager.getClassAverage();
        String avgText = classAvg >= 0 ? String.format("%.1f%%", classAvg) : "N/A";
        statsPanel.add(createStatCard("Class Average", avgText, SUCCESS_COLOR));
        
        List<Student> underperforming = manager.getUnderperformingStudents(50);
        statsPanel.add(createStatCard("Need Support", String.valueOf(underperforming.size()), DANGER_COLOR));
        
        List<Student> topPerformers = manager.getTopPerformers(5);
        statsPanel.add(createStatCard("Top Performers", String.valueOf(topPerformers.size()), WARNING_COLOR));
        
        // Add more stats
        int excellent = 0, good = 0, needsImprovement = 0, poor = 0;
        for (Student s : manager.getAllStudents()) {
            double avg = s.calculateOverallAverage();
            if (avg >= 90) excellent++;
            else if (avg >= 75) good++;
            else if (avg >= 50) needsImprovement++;
            else if (avg >= 0) poor++;
        }
        
        statsPanel.add(createStatCard("Excellent (90+)", String.valueOf(excellent), SUCCESS_COLOR));
        statsPanel.add(createStatCard("Good (75-89)", String.valueOf(good), PRIMARY_COLOR));
        statsPanel.add(createStatCard("Needs Work (50-74)", String.valueOf(needsImprovement), WARNING_COLOR));
        statsPanel.add(createStatCard("At Risk (<50)", String.valueOf(poor), DANGER_COLOR));
        
        panel.add(statsPanel, BorderLayout.NORTH);
        
        // Chart panel
        JPanel chartPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        chartPanel.setBackground(LIGHT_BG);
        
        // Performance distribution pie chart
        chartPanel.add(createPerformanceDistributionChart());
        
        // Class average bar chart (placeholder)
        JPanel placeholderPanel = new JPanel(new BorderLayout());
        placeholderPanel.setBackground(Color.WHITE);
        placeholderPanel.setBorder(BorderFactory.createTitledBorder("Subject Performance"));
        JLabel placeholderLabel = new JLabel("Select a student to view subject performance", SwingConstants.CENTER);
        placeholderLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        placeholderLabel.setForeground(Color.GRAY);
        placeholderPanel.add(placeholderLabel, BorderLayout.CENTER);
        chartPanel.add(placeholderPanel);
        
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create status bar
     */
    private void createStatusBar() {
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBackground(DARK_BG);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel statusLabel = new JLabel("Ready | Students: " + manager.getStudentCount());
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusBar.add(statusLabel);
        
        add(statusBar, BorderLayout.SOUTH);
    }
    
    // Continued in next part...
    
    /**
     * Helper method to create styled labels
     */
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(DARK_BG);
        return label;
    }
    
    /**
     * Helper method to create styled text fields
     */
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return field;
    }
    
    /**
     * Helper method to create styled buttons
     */
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(140, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    /**
     * Helper method to create statistic cards
     */
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(color);
        
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
        valueLabel.setForeground(DARK_BG);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    // Action methods continue below...

    // ==================== ACTION METHODS ====================
    
    /**
     * Add a new student
     */
    private void addStudent() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String grade = gradeField.getText().trim();
        String subject = subjectField.getText().trim();
        String scoreText = scoreField.getText().trim();
        
        if (id.isEmpty() || name.isEmpty()) {
            showError("Student ID and Name are required!");
            return;
        }
        
        if (grade.isEmpty()) {
            grade = "Not Specified";
        }
        
        try {
            Student student = new Student(id, name, grade);
            
            // Add first assessment if provided
            if (!subject.isEmpty() && !scoreText.isEmpty()) {
                int score = Integer.parseInt(scoreText);
                String assessmentType = (String) assessmentTypeCombo.getSelectedItem();
                student.addAssessment(subject, score, assessmentType);
            }
            
            if (manager.addStudent(student)) {
                showSuccess("✓ Student added successfully!\n\n" + formatStudentInfo(student));
                refreshStudentList();
                clearFields();
            } else {
                showError("Student with ID " + id + " already exists!");
            }
        } catch (IllegalArgumentException e) {
            showError("Error: " + e.getMessage());
        }
    }
    
    /**
     * Add assessment to existing student
     */
    private void addAssessment() {
        String id = idField.getText().trim();
        String subject = subjectField.getText().trim();
        String scoreText = scoreField.getText().trim();
        
        if (id.isEmpty()) {
            showError("Please enter Student ID!");
            return;
        }
        
        if (subject.isEmpty() || scoreText.isEmpty()) {
            showError("Subject and Score are required!");
            return;
        }
        
        Student student = manager.findStudentById(id);
        if (student == null) {
            showError("Student not found with ID: " + id);
            return;
        }
        
        try {
            int score = Integer.parseInt(scoreText);
            String assessmentType = (String) assessmentTypeCombo.getSelectedItem();
            student.addAssessment(subject, score, assessmentType);
            manager.saveData();
            
            showSuccess("✓ Assessment added successfully!\n\n" + formatStudentInfo(student));
            refreshStudentList();
            scoreField.setText("");
            subjectField.setText("");
        } catch (IllegalArgumentException e) {
            showError("Error: " + e.getMessage());
        }
    }
    
    /**
     * View student details
     */
    private void viewStudent() {
        String id = idField.getText().trim();
        
        if (id.isEmpty()) {
            showError("Please enter Student ID!");
            return;
        }
        
        Student student = manager.findStudentById(id);
        if (student == null) {
            showError("Student not found with ID: " + id);
            return;
        }
        
        showInfo(formatStudentInfo(student));
        tabbedPane.setSelectedIndex(1); // Switch to details tab
    }
    
    /**
     * Show trends for a student
     */
    private void showTrends() {
        String id = idField.getText().trim();
        
        if (id.isEmpty()) {
            showError("Please enter Student ID!");
            return;
        }
        
        Student student = manager.findStudentById(id);
        if (student == null) {
            showError("Student not found with ID: " + id);
            return;
        }
        
        StringBuilder trends = new StringBuilder();
        trends.append("═══════════════════════════════════════════════════════════════\n");
        trends.append("                    PERFORMANCE TRENDS\n");
        trends.append("═══════════════════════════════════════════════════════════════\n\n");
        trends.append("Student: ").append(student.getName()).append(" (").append(student.getId()).append(")\n");
        trends.append("Grade: ").append(student.getGradeLevel()).append("\n\n");
        
        for (String subject : student.getSubjects()) {
            String trend = student.analyzeSubjectTrend(subject);
            String trendIcon = getTrendIcon(trend);
            double avg = student.calculateSubjectAverage(subject);
            
            trends.append(String.format("%-20s: %s %-15s (Avg: %.1f%%)\n", 
                subject, trendIcon, formatTrend(trend), avg));
        }
        
        trends.append("\n");
        trends.append(student.generateDetailedFeedback());
        trends.append("═══════════════════════════════════════════════════════════════\n");
        
        showInfo(trends.toString());
        tabbedPane.setSelectedIndex(1);
    }
    
    /**
     * Perform search based on selected criteria
     */
    private void performSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            refreshStudentList();
            return;
        }
        
        String searchType = (String) searchTypeCombo.getSelectedItem();
        tableModel.setRowCount(0);
        
        List<Student> results = new java.util.ArrayList<>();
        
        switch (searchType) {
            case "Name":
                results = manager.searchByName(query);
                break;
            case "ID":
                Student student = manager.findStudentById(query);
                if (student != null) results.add(student);
                break;
            case "Grade":
                results = manager.getStudentsByGrade(query);
                break;
        }
        
        for (Student s : results) {
            addStudentToTable(s);
        }
        
        if (results.isEmpty()) {
            showError("No students found matching: " + query);
        }
    }
    
    /**
     * Refresh student list table
     */
    private void refreshStudentList() {
        tableModel.setRowCount(0);
        for (Student student : manager.getAllStudents()) {
            addStudentToTable(student);
        }
    }
    
    /**
     * Add student row to table
     */
    private void addStudentToTable(Student student) {
        double avg = student.calculateOverallAverage();
        String avgText = avg >= 0 ? String.format("%.1f%%", avg) : "No Data";
        String status = getStatusText(avg);
        
        Object[] row = {
            student.getId(),
            student.getName(),
            student.getGradeLevel(),
            student.getSubjects().size(),
            student.getAssessmentCount(),
            avgText,
            status
        };
        tableModel.addRow(row);
    }
    
    /**
     * Show class performance report
     */
    private void showClassReport() {
        String report = manager.generateClassReport();
        showInfo(report);
        tabbedPane.setSelectedIndex(1);
    }
    
    /**
     * Show underperforming students
     */
    private void showUnderperformingStudents() {
        List<Student> underperforming = manager.getUnderperformingStudents(60);
        
        if (underperforming.isEmpty()) {
            showInfo("No underperforming students found (threshold: 60%)");
            return;
        }
        
        StringBuilder report = new StringBuilder();
        report.append("═══════════════════════════════════════════════════════════════\n");
        report.append("              STUDENTS REQUIRING SUPPORT (< 60%)\n");
        report.append("═══════════════════════════════════════════════════════════════\n\n");
        
        for (Student s : underperforming) {
            report.append(String.format("%-20s (ID: %-10s) - %.1f%%\n", 
                s.getName(), s.getId(), s.calculateOverallAverage()));
            report.append("  Grade: ").append(s.getGradeLevel()).append("\n");
            report.append("  Feedback: ").append(s.generateFeedback()).append("\n\n");
        }
        
        report.append("═══════════════════════════════════════════════════════════════\n");
        showInfo(report.toString());
        tabbedPane.setSelectedIndex(1);
    }
    
    /**
     * Show top performers
     */
    private void showTopPerformers() {
        List<Student> topPerformers = manager.getTopPerformers(10);
        
        if (topPerformers.isEmpty()) {
            showInfo("No students with assessment data found.");
            return;
        }
        
        StringBuilder report = new StringBuilder();
        report.append("═══════════════════════════════════════════════════════════════\n");
        report.append("                      TOP PERFORMERS\n");
        report.append("═══════════════════════════════════════════════════════════════\n\n");
        
        for (int i = 0; i < topPerformers.size(); i++) {
            Student s = topPerformers.get(i);
            report.append(String.format("%2d. %-20s (ID: %-10s) - %.1f%%\n", 
                i + 1, s.getName(), s.getId(), s.calculateOverallAverage()));
            report.append("    Grade: ").append(s.getGradeLevel()).append("\n\n");
        }
        
        report.append("═══════════════════════════════════════════════════════════════\n");
        showInfo(report.toString());
        tabbedPane.setSelectedIndex(1);
    }
    
    /**
     * Export data to CSV
     */
    private void exportData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export to CSV");
        fileChooser.setSelectedFile(new java.io.File("student_performance.csv"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getAbsolutePath();
            if (manager.exportToCSV(filename)) {
                JOptionPane.showMessageDialog(this, 
                    "Data exported successfully to:\n" + filename,
                    "Export Successful", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                showError("Failed to export data!");
            }
        }
    }
    
    /**
     * Show about dialog
     */
    private void showAboutDialog() {
        String message = "LearnMax Enhanced - Student Performance Tracker 2.0\n\n" +
                        "A comprehensive student performance tracking system with:\n" +
                        "• Multiple assessment tracking\n" +
                        "• Trend analysis\n" +
                        "• Personalized feedback\n" +
                        "• Class performance analytics\n\n" +
                        "© 2026 LearnMax Educational Systems";
        
        JOptionPane.showMessageDialog(this, message, "About LearnMax Enhanced", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Clear all input fields
     */
    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        gradeField.setText("");
        subjectField.setText("");
        scoreField.setText("");
        assessmentTypeCombo.setSelectedIndex(0);
    }
    
    /**
     * Create performance distribution pie chart
     */
    private ChartPanel createPerformanceDistributionChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        int excellent = 0, good = 0, needsImprovement = 0, poor = 0, noData = 0;
        
        for (Student s : manager.getAllStudents()) {
            double avg = s.calculateOverallAverage();
            if (avg < 0) noData++;
            else if (avg >= 90) excellent++;
            else if (avg >= 75) good++;
            else if (avg >= 50) needsImprovement++;
            else poor++;
        }
        
        if (excellent > 0) dataset.setValue("Excellent (90-100)", excellent);
        if (good > 0) dataset.setValue("Good (75-89)", good);
        if (needsImprovement > 0) dataset.setValue("Needs Improvement (50-74)", needsImprovement);
        if (poor > 0) dataset.setValue("At Risk (<50)", poor);
        if (noData > 0) dataset.setValue("No Data", noData);
        
        JFreeChart chart = ChartFactory.createPieChart(
            "Performance Distribution",
            dataset,
            true,
            true,
            false
        );
        
        return new ChartPanel(chart);
    }
    
    // ==================== HELPER METHODS ====================
    
    private void showInfo(String message) {
        outputArea.setText(message);
    }
    
    private void showSuccess(String message) {
        outputArea.setText(message);
        outputArea.setForeground(SUCCESS_COLOR.darker());
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private String formatStudentInfo(Student student) {
        StringBuilder info = new StringBuilder();
        info.append("═══════════════════════════════════════════════════════════════\n");
        info.append("                  STUDENT PERFORMANCE REPORT\n");
        info.append("═══════════════════════════════════════════════════════════════\n\n");
        
        info.append("Student ID: ").append(student.getId()).append("\n");
        info.append("Name: ").append(student.getName()).append("\n");
        info.append("Grade: ").append(student.getGradeLevel()).append("\n");
        info.append("Total Assessments: ").append(student.getAssessmentCount()).append("\n\n");
        
        if (student.getSubjects().isEmpty()) {
            info.append("No assessment data available yet.\n");
        } else {
            info.append("SUBJECT PERFORMANCE:\n");
            info.append("─────────────────────────────────────────────────────────────\n");
            
            for (String subject : student.getSubjects()) {
                double avg = student.calculateSubjectAverage(subject);
                int latest = student.getLatestScore(subject);
                String trend = student.analyzeSubjectTrend(subject);
                int count = student.getAssessmentsBySubject(subject).size();
                
                info.append(String.format("%-20s: %.1f%% (Latest: %d) %s [%d assessments]\n", 
                    subject, avg, latest, getTrendIcon(trend), count));
            }
            
            info.append("─────────────────────────────────────────────────────────────\n");
            info.append(String.format("Overall Average: %.1f%%\n", student.calculateOverallAverage()));
            info.append("Overall Feedback: ").append(student.generateFeedback()).append("\n\n");
            
            info.append(student.generateDetailedFeedback());
        }
        
        info.append("═══════════════════════════════════════════════════════════════\n");
        
        return info.toString();
    }
    
    private String getTrendIcon(String trend) {
        switch (trend) {
            case "improving": return "📈";
            case "declining": return "📉";
            case "stable": return "➡️";
            default: return "❓";
        }
    }
    
    private String formatTrend(String trend) {
        switch (trend) {
            case "improving": return "Improving";
            case "declining": return "Declining";
            case "stable": return "Stable";
            default: return "Insufficient Data";
        }
    }
    
    private String getStatusText(double avg) {
        if (avg < 0) return "No Data";
        if (avg >= 90) return "Excellent";
        if (avg >= 75) return "Good";
        if (avg >= 50) return "Needs Work";
        return "At Risk";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentTrackerGUI());
    }
}
