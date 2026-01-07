package com.learnmax.virtuallab.simulators;

import com.learnmax.virtuallab.model.LabExercise;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Interactive Circuit Lab with real electronics behavior
 * CAPS Aligned: Grade 10-12 Physical Sciences - Electricity & Magnetism
 * 
 * Implements Ohm's Law (V=IR) and circuit analysis
 */
public class CircuitLab extends JFrame {
    
    private LabExercise exercise;
    private JPanel circuitPanel;
    private JPanel controlPanel;
    private JTextArea dataArea;
    private JLabel statusLabel;
    
    private List<CircuitComponent> components;
    private List<Wire> wires;
    private CircuitComponent selectedComponent;
    private Point dragStart;
    
    private boolean isPowered;
    private double voltage;
    private long startTime;
    private int score;
    
    /**
     * Circuit Component base class
     */
    abstract static class CircuitComponent {
        String id;
        String type;
        double x, y;
        double width, height;
        Color color;
        boolean powered;
        
        public CircuitComponent(String id, String type, double x, double y) {
            this.id = id;
            this.type = type;
            this.x = x;
            this.y = y;
            this.width = 60;
            this.height = 40;
            this.powered = false;
        }
        
        public abstract void draw(Graphics2D g2d);
        public abstract double getResistance();
        public abstract String getInfo();
        
        public boolean contains(Point p) {
            return p.x >= x && p.x <= x + width && p.y >= y && p.y <= y + height;
        }
    }
    
    /**
     * Battery component
     */
    static class Battery extends CircuitComponent {
        double voltage;
        
        public Battery(String id, double x, double y, double voltage) {
            super(id, "Battery", x, y);
            this.voltage = voltage;
            this.color = new Color(255, 215, 0);
        }
        
        @Override
        public void draw(Graphics2D g2d) {
            g2d.setColor(color);
            g2d.fillRect((int)x, (int)y, (int)width, (int)height);
            g2d.setColor(Color.BLACK);
            g2d.drawRect((int)x, (int)y, (int)width, (int)height);
            
            // Draw + and - symbols
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.drawString("+", (int)(x + width * 0.7), (int)(y + height * 0.6));
            g2d.drawString("-", (int)(x + width * 0.2), (int)(y + height * 0.6));
            
            // Label
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            g2d.drawString(voltage + "V", (int)x, (int)(y - 5));
        }
        
        @Override
        public double getResistance() {
            return 0; // Ideal battery has no resistance
        }
        
        @Override
        public String getInfo() {
            return String.format("Battery: %.1fV", voltage);
        }
    }
    
    /**
     * Resistor component
     */
    static class Resistor extends CircuitComponent {
        double resistance;
        double current;
        
        public Resistor(String id, double x, double y, double resistance) {
            super(id, "Resistor", x, y);
            this.resistance = resistance;
            this.color = new Color(139, 69, 19);
            this.current = 0;
        }
        
        @Override
        public void draw(Graphics2D g2d) {
            // Draw zigzag pattern for resistor
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(3));
            
            int startX = (int)x;
            int startY = (int)(y + height / 2);
            int endX = (int)(x + width);
            int segmentWidth = (int)width / 6;
            int amplitude = 10;
            
            for (int i = 0; i < 6; i++) {
                int x1 = startX + i * segmentWidth;
                int y1 = startY + (i % 2 == 0 ? -amplitude : amplitude);
                int x2 = startX + (i + 1) * segmentWidth;
                int y2 = startY + ((i + 1) % 2 == 0 ? -amplitude : amplitude);
                g2d.drawLine(x1, y1, x2, y2);
            }
            
            // Glow effect if powered
            if (powered && current > 0) {
                g2d.setColor(new Color(255, 100, 0, 100));
                g2d.fillRect((int)x, (int)y, (int)width, (int)height);
            }
            
            // Label
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            g2d.drawString(resistance + "Ω", (int)x, (int)(y - 5));
        }
        
        @Override
        public double getResistance() {
            return resistance;
        }
        
        @Override
        public String getInfo() {
            return String.format("Resistor: %.1fΩ, Current: %.3fA, Power: %.3fW", 
                                resistance, current, current * current * resistance);
        }
    }
    
    /**
     * LED component
     */
    static class LED extends CircuitComponent {
        double forwardVoltage;
        double current;
        Color ledColor;
        
        public LED(String id, double x, double y, Color ledColor) {
            super(id, "LED", x, y);
            this.width = 30;
            this.height = 30;
            this.forwardVoltage = 2.0; // Typical LED forward voltage
            this.ledColor = ledColor;
            this.color = Color.GRAY;
            this.current = 0;
        }
        
        @Override
        public void draw(Graphics2D g2d) {
            // Draw LED circle
            if (powered && current > 0.001) {
                // LED is lit
                g2d.setColor(ledColor);
                g2d.fillOval((int)x, (int)y, (int)width, (int)height);
                
                // Glow effect
                g2d.setColor(new Color(ledColor.getRed(), ledColor.getGreen(), 
                                      ledColor.getBlue(), 50));
                g2d.fillOval((int)(x - 10), (int)(y - 10), (int)(width + 20), (int)(height + 20));
            } else {
                // LED is off
                g2d.setColor(color);
                g2d.fillOval((int)x, (int)y, (int)width, (int)height);
            }
            
            g2d.setColor(Color.BLACK);
            g2d.drawOval((int)x, (int)y, (int)width, (int)height);
            
            // Label
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            g2d.drawString("LED", (int)x, (int)(y - 5));
        }
        
        @Override
        public double getResistance() {
            return 100; // Simplified resistance model
        }
        
        @Override
        public String getInfo() {
            return String.format("LED: %.1fV forward, Current: %.3fA, %s", 
                                forwardVoltage, current, powered && current > 0.001 ? "ON" : "OFF");
        }
    }
    
    /**
     * Switch component
     */
    static class Switch extends CircuitComponent {
        boolean closed;
        
        public Switch(String id, double x, double y) {
            super(id, "Switch", x, y);
            this.width = 50;
            this.height = 30;
            this.closed = false;
            this.color = new Color(192, 192, 192);
        }
        
        @Override
        public void draw(Graphics2D g2d) {
            g2d.setColor(color);
            g2d.fillRect((int)x, (int)y, (int)width, (int)height);
            g2d.setColor(Color.BLACK);
            g2d.drawRect((int)x, (int)y, (int)width, (int)height);
            
            // Draw switch state
            g2d.setStroke(new BasicStroke(3));
            if (closed) {
                g2d.setColor(new Color(0, 255, 0));
                g2d.drawLine((int)(x + 10), (int)(y + height / 2), 
                           (int)(x + width - 10), (int)(y + height / 2));
            } else {
                g2d.setColor(Color.RED);
                g2d.drawLine((int)(x + 10), (int)(y + height / 2), 
                           (int)(x + width / 2), (int)(y + 10));
            }
            
            // Label
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            g2d.drawString(closed ? "CLOSED" : "OPEN", (int)x, (int)(y - 5));
        }
        
        @Override
        public double getResistance() {
            return closed ? 0 : Double.POSITIVE_INFINITY;
        }
        
        @Override
        public String getInfo() {
            return String.format("Switch: %s", closed ? "CLOSED" : "OPEN");
        }
        
        public void toggle() {
            closed = !closed;
        }
    }
    
    /**
     * Wire connection
     */
    static class Wire {
        CircuitComponent from;
        CircuitComponent to;
        
        public Wire(CircuitComponent from, CircuitComponent to) {
            this.from = from;
            this.to = to;
        }
        
        public void draw(Graphics2D g2d) {
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            
            int x1 = (int)(from.x + from.width / 2);
            int y1 = (int)(from.y + from.height / 2);
            int x2 = (int)(to.x + to.width / 2);
            int y2 = (int)(to.y + to.height / 2);
            
            g2d.drawLine(x1, y1, x2, y2);
        }
    }
    
    /**
     * Constructor
     */
    public CircuitLab(LabExercise exercise) {
        this.exercise = exercise;
        this.components = new ArrayList<>();
        this.wires = new ArrayList<>();
        this.isPowered = false;
        this.voltage = 9.0;
        this.score = 0;
        this.startTime = System.currentTimeMillis();
        
        initializeUI();
        loadDefaultCircuit();
    }
    
    /**
     * Initialize UI
     */
    private void initializeUI() {
        setTitle("Circuit Lab - " + exercise.getTitle());
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 152, 0));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("⚡ Circuit Lab - Real Electronics Simulation");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JLabel quoteLabel = new JLabel("\"Ubuntu: Electricity connects us all\"");
        quoteLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        quoteLabel.setForeground(Color.WHITE);
        headerPanel.add(quoteLabel, BorderLayout.SOUTH);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Main split pane
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // Circuit canvas
        circuitPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                drawCircuit(g2d);
            }
        };
        circuitPanel.setBackground(Color.WHITE);
        circuitPanel.setPreferredSize(new Dimension(800, 600));
        circuitPanel.setBorder(BorderFactory.createTitledBorder("Circuit Board"));
        
        // Mouse listeners for drag and drop
        circuitPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseReleased(e);
            }
        });
        
        circuitPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(e);
            }
        });
        
        mainSplit.setLeftComponent(circuitPanel);
        
        // Right panel
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        
        // Control panel
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
        
        // Power button
        JButton powerButton = new JButton("⚡ Power ON/OFF");
        powerButton.setBackground(new Color(46, 204, 113));
        powerButton.setForeground(Color.WHITE);
        powerButton.addActionListener(e -> togglePower());
        controlPanel.add(powerButton);
        
        controlPanel.add(Box.createVerticalStrut(10));
        
        // Component buttons
        JPanel componentPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        componentPanel.setBorder(BorderFactory.createTitledBorder("Add Components"));
        
        JButton addBatteryBtn = new JButton("🔋 Battery");
        addBatteryBtn.addActionListener(e -> addComponent("battery"));
        componentPanel.add(addBatteryBtn);
        
        JButton addResistorBtn = new JButton("⚡ Resistor");
        addResistorBtn.addActionListener(e -> addComponent("resistor"));
        componentPanel.add(addResistorBtn);
        
        JButton addLEDBtn = new JButton("💡 LED");
        addLEDBtn.addActionListener(e -> addComponent("led"));
        componentPanel.add(addLEDBtn);
        
        JButton addSwitchBtn = new JButton("🔘 Switch");
        addSwitchBtn.addActionListener(e -> addComponent("switch"));
        componentPanel.add(addSwitchBtn);
        
        controlPanel.add(componentPanel);
        
        controlPanel.add(Box.createVerticalStrut(10));
        
        // Circuit presets
        JPanel presetPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        presetPanel.setBorder(BorderFactory.createTitledBorder("Circuit Presets"));
        
        JButton seriesBtn = new JButton("Series Circuit");
        seriesBtn.addActionListener(e -> loadSeriesCircuit());
        presetPanel.add(seriesBtn);
        
        JButton parallelBtn = new JButton("Parallel Circuit");
        parallelBtn.addActionListener(e -> loadParallelCircuit());
        presetPanel.add(parallelBtn);
        
        JButton clearBtn = new JButton("Clear All");
        clearBtn.addActionListener(e -> clearCircuit());
        presetPanel.add(clearBtn);
        
        controlPanel.add(presetPanel);
        
        rightPanel.add(controlPanel, BorderLayout.NORTH);
        
        // Data area
        dataArea = new JTextArea(15, 30);
        dataArea.setEditable(false);
        dataArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane dataScroll = new JScrollPane(dataArea);
        dataScroll.setBorder(BorderFactory.createTitledBorder("Circuit Analysis"));
        rightPanel.add(dataScroll, BorderLayout.CENTER);
        
        mainSplit.setRightComponent(rightPanel);
        mainSplit.setDividerLocation(800);
        
        add(mainSplit, BorderLayout.CENTER);
        
        // Status bar
        statusLabel = new JLabel(" Ready - Build your circuit");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    /**
     * Load default circuit
     */
    private void loadDefaultCircuit() {
        loadSeriesCircuit();
    }
    
    /**
     * Load series circuit
     */
    private void loadSeriesCircuit() {
        clearCircuit();
        
        Battery battery = new Battery("B1", 100, 250, 9.0);
        Resistor r1 = new Resistor("R1", 250, 250, 100);
        LED led = new LED("LED1", 400, 250, Color.RED);
        Switch sw = new Switch("SW1", 550, 250);
        
        components.add(battery);
        components.add(r1);
        components.add(led);
        components.add(sw);
        
        wires.add(new Wire(battery, r1));
        wires.add(new Wire(r1, led));
        wires.add(new Wire(led, sw));
        
        analyzeCircuit();
        circuitPanel.repaint();
        statusLabel.setText(" Loaded: Series Circuit");
    }
    
    /**
     * Load parallel circuit
     */
    private void loadParallelCircuit() {
        clearCircuit();
        
        Battery battery = new Battery("B1", 100, 300, 9.0);
        Resistor r1 = new Resistor("R1", 300, 200, 100);
        Resistor r2 = new Resistor("R2", 300, 350, 200);
        
        components.add(battery);
        components.add(r1);
        components.add(r2);
        
        wires.add(new Wire(battery, r1));
        wires.add(new Wire(battery, r2));
        
        analyzeCircuit();
        circuitPanel.repaint();
        statusLabel.setText(" Loaded: Parallel Circuit");
    }
    
    /**
     * Clear circuit
     */
    private void clearCircuit() {
        components.clear();
        wires.clear();
        dataArea.setText("");
        circuitPanel.repaint();
    }
    
    /**
     * Add component
     */
    private void addComponent(String type) {
        double x = 100 + Math.random() * 500;
        double y = 100 + Math.random() * 400;
        
        switch (type) {
            case "battery":
                components.add(new Battery("B" + components.size(), x, y, 9.0));
                break;
            case "resistor":
                components.add(new Resistor("R" + components.size(), x, y, 100));
                break;
            case "led":
                components.add(new LED("LED" + components.size(), x, y, Color.RED));
                break;
            case "switch":
                components.add(new Switch("SW" + components.size(), x, y));
                break;
        }
        
        circuitPanel.repaint();
        score += 5;
    }
    
    /**
     * Toggle power
     */
    private void togglePower() {
        isPowered = !isPowered;
        analyzeCircuit();
        circuitPanel.repaint();
        statusLabel.setText(isPowered ? " Circuit powered ON" : " Circuit powered OFF");
        score += 10;
    }
    
    /**
     * Analyze circuit using Ohm's Law
     */
    private void analyzeCircuit() {
        StringBuilder analysis = new StringBuilder();
        analysis.append("CIRCUIT ANALYSIS\n");
        analysis.append("═══════════════════════════════════\n\n");
        
        // Find battery
        Battery battery = null;
        for (CircuitComponent comp : components) {
            if (comp instanceof Battery) {
                battery = (Battery) comp;
                break;
            }
        }
        
        if (battery == null) {
            analysis.append("No battery found!\n");
            dataArea.setText(analysis.toString());
            return;
        }
        
        analysis.append(String.format("Power Source: %.1fV\n", battery.voltage));
        analysis.append(String.format("Status: %s\n\n", isPowered ? "POWERED" : "OFF"));
        
        if (!isPowered) {
            dataArea.setText(analysis.toString());
            return;
        }
        
        // Calculate total resistance (simplified series calculation)
        double totalResistance = 0;
        boolean circuitOpen = false;
        
        for (CircuitComponent comp : components) {
            if (comp instanceof Switch && !((Switch) comp).closed) {
                circuitOpen = true;
                break;
            }
            if (!(comp instanceof Battery)) {
                double r = comp.getResistance();
                if (Double.isInfinite(r)) {
                    circuitOpen = true;
                    break;
                }
                totalResistance += r;
            }
        }
        
        if (circuitOpen) {
            analysis.append("Circuit is OPEN (no current flow)\n");
            dataArea.setText(analysis.toString());
            return;
        }
        
        // Calculate current using Ohm's Law: I = V / R
        double current = totalResistance > 0 ? battery.voltage / totalResistance : 0;
        
        analysis.append(String.format("Total Resistance: %.2fΩ\n", totalResistance));
        analysis.append(String.format("Total Current: %.3fA\n\n", current));
        
        analysis.append("Component Analysis:\n");
        analysis.append("───────────────────────────────────\n");
        
        // Update components
        for (CircuitComponent comp : components) {
            comp.powered = isPowered;
            
            if (comp instanceof Resistor) {
                Resistor r = (Resistor) comp;
                r.current = current;
                double voltage = current * r.resistance;
                double power = current * voltage;
                analysis.append(String.format("%s: V=%.2fV, I=%.3fA, P=%.3fW\n", 
                                             r.id, voltage, current, power));
            } else if (comp instanceof LED) {
                LED led = (LED) comp;
                led.current = current;
                analysis.append(String.format("%s: %s, I=%.3fA\n", 
                                             led.id, current > 0.001 ? "ON" : "OFF", current));
            } else if (comp instanceof Switch) {
                Switch sw = (Switch) comp;
                analysis.append(String.format("%s: %s\n", sw.id, sw.closed ? "CLOSED" : "OPEN"));
            }
        }
        
        // Power calculation
        double totalPower = current * battery.voltage;
        analysis.append(String.format("\nTotal Power: %.3fW\n", totalPower));
        
        dataArea.setText(analysis.toString());
    }
    
    /**
     * Draw circuit
     */
    private void drawCircuit(Graphics2D g2d) {
        // Draw grid
        g2d.setColor(new Color(240, 240, 240));
        for (int i = 0; i < circuitPanel.getWidth(); i += 50) {
            g2d.drawLine(i, 0, i, circuitPanel.getHeight());
        }
        for (int i = 0; i < circuitPanel.getHeight(); i += 50) {
            g2d.drawLine(0, i, circuitPanel.getWidth(), i);
        }
        
        // Draw wires
        for (Wire wire : wires) {
            wire.draw(g2d);
        }
        
        // Draw components
        for (CircuitComponent comp : components) {
            comp.draw(g2d);
        }
    }
    
    /**
     * Mouse handlers
     */
    private void handleMousePressed(MouseEvent e) {
        for (CircuitComponent comp : components) {
            if (comp.contains(e.getPoint())) {
                // Check if it's a switch - toggle it
                if (comp instanceof Switch) {
                    ((Switch) comp).toggle();
                    analyzeCircuit();
                    circuitPanel.repaint();
                    score += 5;
                    return;
                }
                
                selectedComponent = comp;
                dragStart = e.getPoint();
                return;
            }
        }
    }
    
    private void handleMouseReleased(MouseEvent e) {
        selectedComponent = null;
        dragStart = null;
    }
    
    private void handleMouseDragged(MouseEvent e) {
        if (selectedComponent != null && dragStart != null) {
            double dx = e.getX() - dragStart.x;
            double dy = e.getY() - dragStart.y;
            
            selectedComponent.x += dx;
            selectedComponent.y += dy;
            
            dragStart = e.getPoint();
            circuitPanel.repaint();
        }
    }
    
    /**
     * Get score
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
     * Main method
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LabExercise exercise = new LabExercise(
                "CIRC_ELEC_001",
                "Circuit Design - Real Electronics",
                "Physical Sciences",
                "Grade 11",
                LabExercise.ExerciseType.EXPERIMENT
            );
            exercise.setDescription("Build and analyze real electrical circuits");
            exercise.addLearningObjective("Understand Ohm's Law (V=IR)");
            exercise.addLearningObjective("Analyze series and parallel circuits");
            exercise.addLearningObjective("Calculate current, voltage, and power");
            exercise.setCapsReference("CAPS Grade 11 Physical Sciences: Electricity & Magnetism");
            
            CircuitLab lab = new CircuitLab(exercise);
            lab.setVisible(true);
        });
    }
}
