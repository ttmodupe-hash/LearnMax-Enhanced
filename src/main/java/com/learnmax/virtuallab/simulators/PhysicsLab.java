package com.learnmax.virtuallab.simulators;

import com.learnmax.virtuallab.engine.PhysicsEngine;
import com.learnmax.virtuallab.engine.PhysicsEngine.*;
import com.learnmax.virtuallab.model.LabExercise;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Interactive Physics Lab with real mechanics simulations
 * CAPS Aligned: Grade 10-12 Physical Sciences - Mechanics
 */
public class PhysicsLab extends JFrame {
    
    private LabExercise exercise;
    private JPanel simulationPanel;
    private JPanel controlPanel;
    private JTextArea dataArea;
    private JLabel statusLabel;
    
    private Timer animationTimer;
    private String currentExperiment;
    private boolean isRunning;
    
    // Projectile motion experiment
    private ProjectileMotion projectile;
    private List<Point2D.Double> trajectoryPoints;
    private double projectileTime;
    
    // Pendulum experiment
    private Pendulum pendulum;
    private Point2D.Double pendulumPivot;
    
    // Collision experiment
    private List<PhysicsObject> objects;
    
    // Spring experiment
    private PhysicsEngine.Spring spring;
    
    private long startTime;
    private int score;
    
    /**
     * Constructor
     */
    public PhysicsLab(LabExercise exercise) {
        this.exercise = exercise;
        this.isRunning = false;
        this.score = 0;
        this.startTime = System.currentTimeMillis();
        
        initializeUI();
        loadExperiment("projectile");
    }
    
    /**
     * Initialize user interface
     */
    private void initializeUI() {
        setTitle("Physics Lab - " + exercise.getTitle());
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Header with Ubuntu quote
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("🔬 Physics Lab - Real Mechanics Simulations");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JLabel quoteLabel = new JLabel("\"Ubuntu: Understanding physics connects us to the universe\"");
        quoteLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        quoteLabel.setForeground(Color.WHITE);
        headerPanel.add(quoteLabel, BorderLayout.SOUTH);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Main split pane
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // Left: Simulation canvas
        simulationPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                drawSimulation(g2d);
            }
        };
        simulationPanel.setBackground(Color.WHITE);
        simulationPanel.setPreferredSize(new Dimension(800, 600));
        simulationPanel.setBorder(BorderFactory.createTitledBorder("Simulation"));
        
        mainSplit.setLeftComponent(simulationPanel);
        
        // Right: Controls and data
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        
        // Control panel
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
        
        // Experiment selector
        JPanel expPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        expPanel.add(new JLabel("Experiment:"));
        JComboBox<String> expCombo = new JComboBox<>(new String[]{
            "Projectile Motion",
            "Pendulum",
            "Collisions",
            "Spring System",
            "Inclined Plane"
        });
        expCombo.addActionListener(e -> {
            String selected = (String) expCombo.getSelectedItem();
            switch (selected) {
                case "Projectile Motion": loadExperiment("projectile"); break;
                case "Pendulum": loadExperiment("pendulum"); break;
                case "Collisions": loadExperiment("collision"); break;
                case "Spring System": loadExperiment("spring"); break;
                case "Inclined Plane": loadExperiment("incline"); break;
            }
        });
        expPanel.add(expCombo);
        controlPanel.add(expPanel);
        
        controlPanel.add(Box.createVerticalStrut(10));
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton startButton = new JButton("▶ Start");
        startButton.setBackground(new Color(46, 204, 113));
        startButton.setForeground(Color.WHITE);
        startButton.addActionListener(e -> startSimulation());
        buttonPanel.add(startButton);
        
        JButton stopButton = new JButton("⏸ Stop");
        stopButton.setBackground(new Color(231, 76, 60));
        stopButton.setForeground(Color.WHITE);
        stopButton.addActionListener(e -> stopSimulation());
        buttonPanel.add(stopButton);
        
        JButton resetButton = new JButton("↻ Reset");
        resetButton.addActionListener(e -> resetSimulation());
        buttonPanel.add(resetButton);
        
        controlPanel.add(buttonPanel);
        
        rightPanel.add(controlPanel, BorderLayout.NORTH);
        
        // Data display
        dataArea = new JTextArea(20, 30);
        dataArea.setEditable(false);
        dataArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane dataScroll = new JScrollPane(dataArea);
        dataScroll.setBorder(BorderFactory.createTitledBorder("Real-Time Data"));
        rightPanel.add(dataScroll, BorderLayout.CENTER);
        
        mainSplit.setRightComponent(rightPanel);
        mainSplit.setDividerLocation(800);
        
        add(mainSplit, BorderLayout.CENTER);
        
        // Status bar
        statusLabel = new JLabel(" Ready - Select an experiment and click Start");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(statusLabel, BorderLayout.SOUTH);
        
        // Animation timer (60 FPS)
        animationTimer = new Timer(16, e -> updateSimulation());
    }
    
    /**
     * Load experiment
     */
    private void loadExperiment(String experiment) {
        currentExperiment = experiment;
        stopSimulation();
        
        switch (experiment) {
            case "projectile":
                setupProjectileMotion();
                break;
            case "pendulum":
                setupPendulum();
                break;
            case "collision":
                setupCollision();
                break;
            case "spring":
                setupSpring();
                break;
            case "incline":
                setupInclinedPlane();
                break;
        }
        
        simulationPanel.repaint();
        statusLabel.setText(" Loaded: " + experiment + " experiment");
    }
    
    /**
     * Setup projectile motion experiment
     */
    private void setupProjectileMotion() {
        controlPanel.removeAll();
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2, 5, 5));
        
        JTextField velocityField = new JTextField("20");
        JTextField angleField = new JTextField("45");
        JTextField heightField = new JTextField("0");
        
        panel.add(new JLabel("Initial Velocity (m/s):"));
        panel.add(velocityField);
        panel.add(new JLabel("Launch Angle (°):"));
        panel.add(angleField);
        panel.add(new JLabel("Initial Height (m):"));
        panel.add(heightField);
        
        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(e -> {
            try {
                double v = Double.parseDouble(velocityField.getText());
                double angle = Double.parseDouble(angleField.getText());
                double h = Double.parseDouble(heightField.getText());
                
                projectile = new ProjectileMotion(v, angle, h);
                trajectoryPoints = projectile.getTrajectory(100);
                projectileTime = 0;
                
                updateProjectileData();
                simulationPanel.repaint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers");
            }
        });
        panel.add(applyButton);
        
        controlPanel.add(panel);
        controlPanel.revalidate();
        controlPanel.repaint();
        
        // Initialize with default values
        projectile = new ProjectileMotion(20, 45, 0);
        trajectoryPoints = projectile.getTrajectory(100);
        projectileTime = 0;
        updateProjectileData();
    }
    
    /**
     * Setup pendulum experiment
     */
    private void setupPendulum() {
        controlPanel.removeAll();
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2, 5, 5));
        
        JTextField lengthField = new JTextField("2.0");
        JTextField angleField = new JTextField("30");
        
        panel.add(new JLabel("Length (m):"));
        panel.add(lengthField);
        panel.add(new JLabel("Initial Angle (°):"));
        panel.add(angleField);
        
        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(e -> {
            try {
                double length = Double.parseDouble(lengthField.getText());
                double angle = Double.parseDouble(angleField.getText());
                
                pendulum = new Pendulum(length, angle);
                pendulumPivot = new Point2D.Double(400, 100);
                
                updatePendulumData();
                simulationPanel.repaint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers");
            }
        });
        panel.add(applyButton);
        
        controlPanel.add(panel);
        controlPanel.revalidate();
        controlPanel.repaint();
        
        // Initialize
        pendulum = new Pendulum(2.0, 30);
        pendulumPivot = new Point2D.Double(400, 100);
        updatePendulumData();
    }
    
    /**
     * Setup collision experiment
     */
    private void setupCollision() {
        objects = new ArrayList<>();
        
        // Create two objects
        PhysicsObject obj1 = new PhysicsObject(1.0, 200, 300);
        obj1.setVelocity(50, 0);
        
        PhysicsObject obj2 = new PhysicsObject(2.0, 600, 300);
        obj2.setVelocity(-30, 0);
        
        objects.add(obj1);
        objects.add(obj2);
        
        updateCollisionData();
    }
    
    /**
     * Setup spring experiment
     */
    private void setupSpring() {
        objects = new ArrayList<>();
        
        PhysicsObject obj1 = new PhysicsObject(1.0, 300, 300);
        obj1.setFixed(true);
        
        PhysicsObject obj2 = new PhysicsObject(0.5, 500, 300);
        
        objects.add(obj1);
        objects.add(obj2);
        
        spring = new PhysicsEngine.Spring(obj1, obj2, 100, 10);
        
        updateSpringData();
    }
    
    /**
     * Setup inclined plane experiment
     */
    private void setupInclinedPlane() {
        controlPanel.removeAll();
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2, 5, 5));
        
        JTextField angleField = new JTextField("30");
        JTextField massField = new JTextField("5");
        JTextField frictionField = new JTextField("0.2");
        
        panel.add(new JLabel("Angle (°):"));
        panel.add(angleField);
        panel.add(new JLabel("Mass (kg):"));
        panel.add(massField);
        panel.add(new JLabel("Friction:"));
        panel.add(frictionField);
        
        JButton calculateButton = new JButton("Calculate");
        calculateButton.addActionListener(e -> {
            try {
                double angle = Double.parseDouble(angleField.getText());
                double mass = Double.parseDouble(massField.getText());
                double friction = Double.parseDouble(frictionField.getText());
                
                InclinedPlane plane = new InclinedPlane(angle, mass, friction);
                
                StringBuilder data = new StringBuilder();
                data.append("INCLINED PLANE ANALYSIS\n");
                data.append("═══════════════════════════════════\n\n");
                data.append(String.format("Angle: %.1f°\n", angle));
                data.append(String.format("Mass: %.1f kg\n", mass));
                data.append(String.format("Friction coefficient: %.2f\n\n", friction));
                data.append("Forces:\n");
                data.append(String.format("  Normal Force: %.2f N\n", plane.getNormalForce()));
                data.append(String.format("  Friction Force: %.2f N\n", plane.getFrictionForce()));
                data.append(String.format("\nAcceleration: %.2f m/s²\n", plane.getAcceleration()));
                
                if (plane.getAcceleration() > 0) {
                    data.append("\n✓ Object will slide down\n");
                } else {
                    data.append("\n✗ Object will remain stationary\n");
                }
                
                dataArea.setText(data.toString());
                score += 10;
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers");
            }
        });
        panel.add(calculateButton);
        
        controlPanel.add(panel);
        controlPanel.revalidate();
        controlPanel.repaint();
    }
    
    /**
     * Start simulation
     */
    private void startSimulation() {
        isRunning = true;
        animationTimer.start();
        statusLabel.setText(" Running simulation...");
    }
    
    /**
     * Stop simulation
     */
    private void stopSimulation() {
        isRunning = false;
        animationTimer.stop();
        statusLabel.setText(" Simulation stopped");
    }
    
    /**
     * Reset simulation
     */
    private void resetSimulation() {
        stopSimulation();
        loadExperiment(currentExperiment);
        statusLabel.setText(" Simulation reset");
    }
    
    /**
     * Update simulation (called by timer)
     */
    private void updateSimulation() {
        double dt = 0.016; // 16ms = ~60 FPS
        
        switch (currentExperiment) {
            case "projectile":
                projectileTime += dt;
                if (projectileTime > projectile.getTimeOfFlight()) {
                    stopSimulation();
                    score += 20;
                }
                updateProjectileData();
                break;
                
            case "pendulum":
                pendulum.update(dt);
                updatePendulumData();
                break;
                
            case "collision":
                for (PhysicsObject obj : objects) {
                    obj.applyForce(0, 0); // No external forces
                    obj.update(dt);
                }
                // Check collisions
                if (objects.size() >= 2) {
                    if (CollisionHandler.checkCircleCollision(objects.get(0), 20, objects.get(1), 20)) {
                        CollisionHandler.resolveCollision(objects.get(0), objects.get(1));
                        score += 15;
                    }
                }
                updateCollisionData();
                break;
                
            case "spring":
                spring.update();
                for (PhysicsObject obj : objects) {
                    if (!obj.equals(objects.get(0))) { // Don't update fixed object
                        obj.applyForce(0, PhysicsEngine.GRAVITY * obj.getMass());
                    }
                    obj.update(dt);
                }
                updateSpringData();
                break;
        }
        
        simulationPanel.repaint();
    }
    
    /**
     * Draw simulation
     */
    private void drawSimulation(Graphics2D g2d) {
        int width = simulationPanel.getWidth();
        int height = simulationPanel.getHeight();
        
        // Draw grid
        g2d.setColor(new Color(230, 230, 230));
        for (int i = 0; i < width; i += 50) {
            g2d.drawLine(i, 0, i, height);
        }
        for (int i = 0; i < height; i += 50) {
            g2d.drawLine(0, i, width, i);
        }
        
        switch (currentExperiment) {
            case "projectile":
                drawProjectileMotion(g2d);
                break;
            case "pendulum":
                drawPendulum(g2d);
                break;
            case "collision":
                drawCollision(g2d);
                break;
            case "spring":
                drawSpring(g2d);
                break;
            case "incline":
                drawInclinedPlane(g2d);
                break;
        }
    }
    
    /**
     * Draw projectile motion
     */
    private void drawProjectileMotion(Graphics2D g2d) {
        // Draw trajectory
        g2d.setColor(new Color(52, 152, 219, 100));
        g2d.setStroke(new BasicStroke(2));
        
        double scale = 10; // pixels per meter
        int groundY = simulationPanel.getHeight() - 50;
        
        for (int i = 0; i < trajectoryPoints.size() - 1; i++) {
            Point2D.Double p1 = trajectoryPoints.get(i);
            Point2D.Double p2 = trajectoryPoints.get(i + 1);
            
            int x1 = 50 + (int)(p1.x * scale);
            int y1 = groundY - (int)(p1.y * scale);
            int x2 = 50 + (int)(p2.x * scale);
            int y2 = groundY - (int)(p2.y * scale);
            
            g2d.drawLine(x1, y1, x2, y2);
        }
        
        // Draw projectile at current time
        if (isRunning && projectileTime <= projectile.getTimeOfFlight()) {
            Point2D.Double pos = projectile.getPositionAtTime(projectileTime);
            int x = 50 + (int)(pos.x * scale);
            int y = groundY - (int)(pos.y * scale);
            
            g2d.setColor(new Color(231, 76, 60));
            g2d.fillOval(x - 10, y - 10, 20, 20);
        }
        
        // Draw ground
        g2d.setColor(new Color(46, 204, 113));
        g2d.fillRect(0, groundY, simulationPanel.getWidth(), 50);
    }
    
    /**
     * Draw pendulum
     */
    private void drawPendulum(Graphics2D g2d) {
        Point2D.Double bobPos = pendulum.getBobPosition(pendulumPivot);
        
        double scale = 100; // pixels per meter
        int bobX = (int)(bobPos.x * scale);
        int bobY = (int)(bobPos.y * scale);
        int pivotX = (int)(pendulumPivot.x);
        int pivotY = (int)(pendulumPivot.y);
        
        // Draw string
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(pivotX, pivotY, bobX, bobY);
        
        // Draw pivot
        g2d.setColor(Color.GRAY);
        g2d.fillRect(pivotX - 5, pivotY - 5, 10, 10);
        
        // Draw bob
        g2d.setColor(new Color(231, 76, 60));
        g2d.fillOval(bobX - 15, bobY - 15, 30, 30);
    }
    
    /**
     * Draw collision
     */
    private void drawCollision(Graphics2D g2d) {
        for (int i = 0; i < objects.size(); i++) {
            PhysicsObject obj = objects.get(i);
            int x = (int)obj.getPosition().x;
            int y = (int)obj.getPosition().y;
            int radius = (int)(obj.getMass() * 20);
            
            Color color = i == 0 ? new Color(52, 152, 219) : new Color(231, 76, 60);
            g2d.setColor(color);
            g2d.fillOval(x - radius, y - radius, radius * 2, radius * 2);
            
            // Draw velocity vector
            g2d.setColor(Color.BLACK);
            Point2D.Double vel = obj.getVelocity();
            g2d.drawLine(x, y, x + (int)vel.x, y + (int)vel.y);
        }
    }
    
    /**
     * Draw spring
     */
    private void drawSpring(Graphics2D g2d) {
        if (objects.size() < 2) return;
        
        PhysicsObject obj1 = objects.get(0);
        PhysicsObject obj2 = objects.get(1);
        
        int x1 = (int)obj1.getPosition().x;
        int y1 = (int)obj1.getPosition().y;
        int x2 = (int)obj2.getPosition().x;
        int y2 = (int)obj2.getPosition().y;
        
        // Draw spring (zigzag line)
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        
        int segments = 10;
        int amplitude = 10;
        for (int i = 0; i < segments; i++) {
            double t1 = i / (double)segments;
            double t2 = (i + 1) / (double)segments;
            
            int px1 = (int)(x1 + (x2 - x1) * t1);
            int py1 = (int)(y1 + (y2 - y1) * t1) + (i % 2 == 0 ? amplitude : -amplitude);
            int px2 = (int)(x1 + (x2 - x1) * t2);
            int py2 = (int)(y1 + (y2 - y1) * t2) + ((i + 1) % 2 == 0 ? amplitude : -amplitude);
            
            g2d.drawLine(px1, py1, px2, py2);
        }
        
        // Draw objects
        g2d.setColor(Color.GRAY);
        g2d.fillRect(x1 - 15, y1 - 15, 30, 30);
        
        g2d.setColor(new Color(231, 76, 60));
        g2d.fillOval(x2 - 15, y2 - 15, 30, 30);
    }
    
    /**
     * Draw inclined plane
     */
    private void drawInclinedPlane(Graphics2D g2d) {
        // Draw plane
        g2d.setColor(new Color(155, 89, 182));
        int[] xPoints = {100, 600, 600};
        int[] yPoints = {500, 500, 300};
        g2d.fillPolygon(xPoints, yPoints, 3);
        
        // Draw object on plane
        g2d.setColor(new Color(231, 76, 60));
        g2d.fillRect(300, 380, 40, 40);
    }
    
    /**
     * Update data displays
     */
    private void updateProjectileData() {
        StringBuilder data = new StringBuilder();
        data.append("PROJECTILE MOTION ANALYSIS\n");
        data.append("═══════════════════════════════════\n\n");
        data.append(String.format("Initial Velocity: %.2f m/s\n", projectile.getRange() > 0 ? 20.0 : 0));
        data.append(String.format("Launch Angle: %.1f°\n", 45.0));
        data.append(String.format("\nCalculated Values:\n"));
        data.append(String.format("  Max Height: %.2f m\n", projectile.getMaxHeight()));
        data.append(String.format("  Range: %.2f m\n", projectile.getRange()));
        data.append(String.format("  Time of Flight: %.2f s\n", projectile.getTimeOfFlight()));
        
        if (isRunning) {
            Point2D.Double pos = projectile.getPositionAtTime(projectileTime);
            data.append(String.format("\nCurrent State (t=%.2fs):\n", projectileTime));
            data.append(String.format("  Position: (%.2f, %.2f) m\n", pos.x, pos.y));
        }
        
        dataArea.setText(data.toString());
    }
    
    private void updatePendulumData() {
        StringBuilder data = new StringBuilder();
        data.append("PENDULUM ANALYSIS\n");
        data.append("═══════════════════════════════════\n\n");
        data.append(String.format("Length: %.2f m\n", 2.0));
        data.append(String.format("Period: %.2f s\n", pendulum.getPeriod()));
        data.append(String.format("\nCurrent State:\n"));
        data.append(String.format("  Angle: %.2f°\n", pendulum.getAngleDegrees()));
        
        dataArea.setText(data.toString());
    }
    
    private void updateCollisionData() {
        StringBuilder data = new StringBuilder();
        data.append("COLLISION ANALYSIS\n");
        data.append("═══════════════════════════════════\n\n");
        
        for (int i = 0; i < objects.size(); i++) {
            PhysicsObject obj = objects.get(i);
            data.append(String.format("Object %d:\n", i + 1));
            data.append(String.format("  Mass: %.2f kg\n", obj.getMass()));
            data.append(String.format("  Velocity: (%.2f, %.2f) m/s\n", 
                obj.getVelocity().x, obj.getVelocity().y));
            data.append(String.format("  KE: %.2f J\n", obj.getKineticEnergy()));
            data.append("\n");
        }
        
        dataArea.setText(data.toString());
    }
    
    private void updateSpringData() {
        StringBuilder data = new StringBuilder();
        data.append("SPRING SYSTEM ANALYSIS\n");
        data.append("═══════════════════════════════════\n\n");
        data.append("Hooke's Law: F = -kx\n\n");
        
        if (objects.size() >= 2) {
            PhysicsObject obj = objects.get(1);
            data.append(String.format("Mass: %.2f kg\n", obj.getMass()));
            data.append(String.format("Position: %.2f m\n", obj.getPosition().x / 100));
            data.append(String.format("Velocity: %.2f m/s\n", obj.getVelocity().x));
        }
        
        dataArea.setText(data.toString());
    }
    
    /**
     * Get final score
     */
    public double getScore() {
        return Math.min(100, score);
    }
    
    /**
     * Get time spent
     */
    public int getTimeSpentMinutes() {
        return (int)((System.currentTimeMillis() - startTime) / 60000);
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LabExercise exercise = new LabExercise(
                "PHYS_MECH_001",
                "Mechanics - Interactive Experiments",
                "Physical Sciences",
                "Grade 11",
                LabExercise.ExerciseType.EXPERIMENT
            );
            exercise.setDescription("Explore real physics through interactive simulations");
            exercise.addLearningObjective("Understand projectile motion");
            exercise.addLearningObjective("Analyze pendulum behavior");
            exercise.addLearningObjective("Study elastic collisions");
            exercise.setCapsReference("CAPS Grade 11 Physical Sciences: Mechanics");
            
            PhysicsLab lab = new PhysicsLab(exercise);
            lab.setVisible(true);
        });
    }
}
