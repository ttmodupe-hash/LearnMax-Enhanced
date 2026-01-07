package com.learnmax.virtuallab.simulators;

import com.learnmax.virtuallab.model.LabExercise;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

/**
 * Interactive Chemistry Lab with real molecular structures and reactions
 * CAPS Aligned: Grade 10-12 Physical Sciences - Chemical Systems
 */
public class ChemistryLab extends JFrame {
    
    private LabExercise exercise;
    private JPanel moleculePanel;
    private JPanel controlPanel;
    private JTextArea dataArea;
    private JLabel statusLabel;
    
    private javax.swing.Timer animationTimer;
    private String currentExperiment;
    private boolean isRunning;
    
    private List<Atom> atoms;
    private List<Bond> bonds;
    private Molecule currentMolecule;
    
    private long startTime;
    private int score;
    private int correctAnswers;
    
    /**
     * Atom class representing chemical elements
     */
    static class Atom {
        String symbol;
        String name;
        int atomicNumber;
        double x, y;
        double vx, vy;
        Color color;
        int valence;
        List<Atom> bondedTo;
        
        public Atom(String symbol, String name, int atomicNumber, int valence, Color color) {
            this.symbol = symbol;
            this.name = name;
            this.atomicNumber = atomicNumber;
            this.valence = valence;
            this.color = color;
            this.bondedTo = new ArrayList<>();
            this.x = Math.random() * 600 + 50;
            this.y = Math.random() * 400 + 50;
            this.vx = (Math.random() - 0.5) * 2;
            this.vy = (Math.random() - 0.5) * 2;
        }
        
        public void update(double dt, int width, int height) {
            x += vx * dt;
            y += vy * dt;
            
            // Bounce off walls
            if (x < 20 || x > width - 20) vx *= -1;
            if (y < 20 || y > height - 20) vy *= -1;
            
            // Keep in bounds
            x = Math.max(20, Math.min(width - 20, x));
            y = Math.max(20, Math.min(height - 20, y));
        }
        
        public boolean canBond() {
            return bondedTo.size() < valence;
        }
        
        public void bondTo(Atom other) {
            if (canBond() && other.canBond() && !bondedTo.contains(other)) {
                bondedTo.add(other);
                other.bondedTo.add(this);
            }
        }
    }
    
    /**
     * Bond class representing chemical bonds
     */
    static class Bond {
        Atom atom1;
        Atom atom2;
        int bondOrder; // 1=single, 2=double, 3=triple
        
        public Bond(Atom atom1, Atom atom2, int bondOrder) {
            this.atom1 = atom1;
            this.atom2 = atom2;
            this.bondOrder = bondOrder;
        }
    }
    
    /**
     * Molecule class representing chemical compounds
     */
    static class Molecule {
        String formula;
        String name;
        List<Atom> atoms;
        List<Bond> bonds;
        String structure;
        
        public Molecule(String formula, String name, String structure) {
            this.formula = formula;
            this.name = name;
            this.structure = structure;
            this.atoms = new ArrayList<>();
            this.bonds = new ArrayList<>();
        }
        
        public void addAtom(Atom atom) {
            atoms.add(atom);
        }
        
        public void addBond(Atom a1, Atom a2, int order) {
            bonds.add(new Bond(a1, a2, order));
            a1.bondTo(a2);
        }
    }
    
    /**
     * Periodic table data
     */
    private static final Map<String, Atom> PERIODIC_TABLE = new HashMap<>();
    static {
        PERIODIC_TABLE.put("H", new Atom("H", "Hydrogen", 1, 1, new Color(255, 255, 255)));
        PERIODIC_TABLE.put("C", new Atom("C", "Carbon", 6, 4, new Color(50, 50, 50)));
        PERIODIC_TABLE.put("N", new Atom("N", "Nitrogen", 7, 3, new Color(0, 0, 255)));
        PERIODIC_TABLE.put("O", new Atom("O", "Oxygen", 8, 2, new Color(255, 0, 0)));
        PERIODIC_TABLE.put("S", new Atom("S", "Sulfur", 16, 2, new Color(255, 255, 0)));
        PERIODIC_TABLE.put("P", new Atom("P", "Phosphorus", 15, 3, new Color(255, 165, 0)));
        PERIODIC_TABLE.put("Cl", new Atom("Cl", "Chlorine", 17, 1, new Color(0, 255, 0)));
        PERIODIC_TABLE.put("Na", new Atom("Na", "Sodium", 11, 1, new Color(171, 92, 242)));
        PERIODIC_TABLE.put("Ca", new Atom("Ca", "Calcium", 20, 2, new Color(61, 255, 0)));
        PERIODIC_TABLE.put("Fe", new Atom("Fe", "Iron", 26, 3, new Color(224, 102, 51)));
    }
    
    /**
     * Constructor
     */
    public ChemistryLab(LabExercise exercise) {
        this.exercise = exercise;
        this.isRunning = false;
        this.score = 0;
        this.correctAnswers = 0;
        this.startTime = System.currentTimeMillis();
        this.atoms = new ArrayList<>();
        this.bonds = new ArrayList<>();
        
        initializeUI();
        loadExperiment("water");
    }
    
    /**
     * Initialize user interface
     */
    private void initializeUI() {
        setTitle("Chemistry Lab - " + exercise.getTitle());
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Header with Ubuntu quote
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(155, 89, 182));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("⚗️ Chemistry Lab - Molecular Structures & Reactions");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JLabel quoteLabel = new JLabel("\"Ubuntu: Chemistry shows us that everything is connected\"");
        quoteLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        quoteLabel.setForeground(Color.WHITE);
        headerPanel.add(quoteLabel, BorderLayout.SOUTH);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Main split pane
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // Left: Molecule canvas
        moleculePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                drawMolecule(g2d);
            }
        };
        moleculePanel.setBackground(Color.WHITE);
        moleculePanel.setPreferredSize(new Dimension(800, 600));
        moleculePanel.setBorder(BorderFactory.createTitledBorder("Molecular Visualization"));
        
        mainSplit.setLeftComponent(moleculePanel);
        
        // Right: Controls and data
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        
        // Control panel
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Experiments"));
        
        // Experiment selector
        JPanel expPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        expPanel.add(new JLabel("Select Molecule:"));
        JComboBox<String> expCombo = new JComboBox<>(new String[]{
            "Water (H₂O)",
            "Methane (CH₄)",
            "Ammonia (NH₃)",
            "Carbon Dioxide (CO₂)",
            "Glucose (C₆H₁₂O₆)",
            "Ethanol (C₂H₅OH)",
            "Sulfuric Acid (H₂SO₄)",
            "Sodium Chloride (NaCl)"
        });
        expCombo.addActionListener(e -> {
            String selected = (String) expCombo.getSelectedItem();
            if (selected.contains("Water")) loadExperiment("water");
            else if (selected.contains("Methane")) loadExperiment("methane");
            else if (selected.contains("Ammonia")) loadExperiment("ammonia");
            else if (selected.contains("Carbon Dioxide")) loadExperiment("co2");
            else if (selected.contains("Glucose")) loadExperiment("glucose");
            else if (selected.contains("Ethanol")) loadExperiment("ethanol");
            else if (selected.contains("Sulfuric Acid")) loadExperiment("h2so4");
            else if (selected.contains("Sodium Chloride")) loadExperiment("nacl");
        });
        expPanel.add(expCombo);
        controlPanel.add(expPanel);
        
        controlPanel.add(Box.createVerticalStrut(10));
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton animateButton = new JButton("▶ Animate");
        animateButton.setBackground(new Color(46, 204, 113));
        animateButton.setForeground(Color.WHITE);
        animateButton.addActionListener(e -> startAnimation());
        buttonPanel.add(animateButton);
        
        JButton stopButton = new JButton("⏸ Stop");
        stopButton.setBackground(new Color(231, 76, 60));
        stopButton.setForeground(Color.WHITE);
        stopButton.addActionListener(e -> stopAnimation());
        buttonPanel.add(stopButton);
        
        JButton quizButton = new JButton("📝 Quiz Me");
        quizButton.setBackground(new Color(52, 152, 219));
        quizButton.setForeground(Color.WHITE);
        quizButton.addActionListener(e -> startQuiz());
        buttonPanel.add(quizButton);
        
        controlPanel.add(buttonPanel);
        
        // Periodic table section
        JPanel periodicPanel = new JPanel(new GridLayout(0, 3, 5, 5));
        periodicPanel.setBorder(BorderFactory.createTitledBorder("Elements"));
        
        for (String symbol : PERIODIC_TABLE.keySet()) {
            Atom atom = PERIODIC_TABLE.get(symbol);
            JButton btn = new JButton(symbol);
            btn.setBackground(atom.color);
            btn.setToolTipText(atom.name);
            btn.addActionListener(e -> showElementInfo(symbol));
            periodicPanel.add(btn);
        }
        
        controlPanel.add(periodicPanel);
        
        rightPanel.add(controlPanel, BorderLayout.NORTH);
        
        // Data display
        dataArea = new JTextArea(15, 30);
        dataArea.setEditable(false);
        dataArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane dataScroll = new JScrollPane(dataArea);
        dataScroll.setBorder(BorderFactory.createTitledBorder("Molecular Data"));
        rightPanel.add(dataScroll, BorderLayout.CENTER);
        
        mainSplit.setRightComponent(rightPanel);
        mainSplit.setDividerLocation(800);
        
        add(mainSplit, BorderLayout.CENTER);
        
        // Status bar
        statusLabel = new JLabel(" Ready - Select a molecule to explore");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(statusLabel, BorderLayout.SOUTH);
        
        // Animation timer (30 FPS)
        animationTimer = new javax.swing.Timer(33, e -> updateAnimation());
    }
    
    /**
     * Load experiment
     */
    private void loadExperiment(String experiment) {
        currentExperiment = experiment;
        stopAnimation();
        atoms.clear();
        bonds.clear();
        
        switch (experiment) {
            case "water":
                createWaterMolecule();
                break;
            case "methane":
                createMethaneMolecule();
                break;
            case "ammonia":
                createAmmoniaMolecule();
                break;
            case "co2":
                createCO2Molecule();
                break;
            case "glucose":
                createGlucoseMolecule();
                break;
            case "ethanol":
                createEthanolMolecule();
                break;
            case "h2so4":
                createSulfuricAcidMolecule();
                break;
            case "nacl":
                createNaClMolecule();
                break;
        }
        
        updateMolecularData();
        moleculePanel.repaint();
        statusLabel.setText(" Loaded: " + currentMolecule.name);
    }
    
    /**
     * Create Water (H2O) molecule
     */
    private void createWaterMolecule() {
        currentMolecule = new Molecule("H₂O", "Water", "Bent");
        
        // Create atoms
        Atom o = createAtom("O", 400, 300);
        Atom h1 = createAtom("H", 350, 250);
        Atom h2 = createAtom("H", 450, 250);
        
        atoms.add(o);
        atoms.add(h1);
        atoms.add(h2);
        
        // Create bonds
        bonds.add(new Bond(o, h1, 1));
        bonds.add(new Bond(o, h2, 1));
        
        currentMolecule.atoms = atoms;
        currentMolecule.bonds = bonds;
    }
    
    /**
     * Create Methane (CH4) molecule
     */
    private void createMethaneMolecule() {
        currentMolecule = new Molecule("CH₄", "Methane", "Tetrahedral");
        
        // Create atoms
        Atom c = createAtom("C", 400, 300);
        Atom h1 = createAtom("H", 350, 250);
        Atom h2 = createAtom("H", 450, 250);
        Atom h3 = createAtom("H", 350, 350);
        Atom h4 = createAtom("H", 450, 350);
        
        atoms.add(c);
        atoms.add(h1);
        atoms.add(h2);
        atoms.add(h3);
        atoms.add(h4);
        
        // Create bonds
        bonds.add(new Bond(c, h1, 1));
        bonds.add(new Bond(c, h2, 1));
        bonds.add(new Bond(c, h3, 1));
        bonds.add(new Bond(c, h4, 1));
        
        currentMolecule.atoms = atoms;
        currentMolecule.bonds = bonds;
    }
    
    /**
     * Create Ammonia (NH3) molecule
     */
    private void createAmmoniaMolecule() {
        currentMolecule = new Molecule("NH₃", "Ammonia", "Trigonal Pyramidal");
        
        // Create atoms
        Atom n = createAtom("N", 400, 300);
        Atom h1 = createAtom("H", 350, 250);
        Atom h2 = createAtom("H", 450, 250);
        Atom h3 = createAtom("H", 400, 350);
        
        atoms.add(n);
        atoms.add(h1);
        atoms.add(h2);
        atoms.add(h3);
        
        // Create bonds
        bonds.add(new Bond(n, h1, 1));
        bonds.add(new Bond(n, h2, 1));
        bonds.add(new Bond(n, h3, 1));
        
        currentMolecule.atoms = atoms;
        currentMolecule.bonds = bonds;
    }
    
    /**
     * Create Carbon Dioxide (CO2) molecule
     */
    private void createCO2Molecule() {
        currentMolecule = new Molecule("CO₂", "Carbon Dioxide", "Linear");
        
        // Create atoms
        Atom c = createAtom("C", 400, 300);
        Atom o1 = createAtom("O", 300, 300);
        Atom o2 = createAtom("O", 500, 300);
        
        atoms.add(c);
        atoms.add(o1);
        atoms.add(o2);
        
        // Create double bonds
        bonds.add(new Bond(c, o1, 2));
        bonds.add(new Bond(c, o2, 2));
        
        currentMolecule.atoms = atoms;
        currentMolecule.bonds = bonds;
    }
    
    /**
     * Create Glucose (C6H12O6) molecule
     */
    private void createGlucoseMolecule() {
        currentMolecule = new Molecule("C₆H₁₂O₆", "Glucose", "Ring");
        
        // Create carbon ring
        double centerX = 400;
        double centerY = 300;
        double radius = 80;
        
        for (int i = 0; i < 6; i++) {
            double angle = i * Math.PI / 3;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            
            Atom c = createAtom(i < 5 ? "C" : "O", x, y);
            atoms.add(c);
            
            // Add hydrogen
            double hx = x + 30 * Math.cos(angle);
            double hy = y + 30 * Math.sin(angle);
            Atom h = createAtom("H", hx, hy);
            atoms.add(h);
            bonds.add(new Bond(c, h, 1));
        }
        
        // Create ring bonds
        for (int i = 0; i < 6; i++) {
            Atom a1 = atoms.get(i * 2);
            Atom a2 = atoms.get(((i + 1) % 6) * 2);
            bonds.add(new Bond(a1, a2, 1));
        }
        
        currentMolecule.atoms = atoms;
        currentMolecule.bonds = bonds;
    }
    
    /**
     * Create Ethanol (C2H5OH) molecule
     */
    private void createEthanolMolecule() {
        currentMolecule = new Molecule("C₂H₅OH", "Ethanol", "Chain");
        
        // Create atoms
        Atom c1 = createAtom("C", 350, 300);
        Atom c2 = createAtom("C", 450, 300);
        Atom o = createAtom("O", 520, 300);
        Atom h = createAtom("H", 580, 300);
        
        atoms.add(c1);
        atoms.add(c2);
        atoms.add(o);
        atoms.add(h);
        
        // Add hydrogens to carbons
        atoms.add(createAtom("H", 320, 250));
        atoms.add(createAtom("H", 320, 350));
        atoms.add(createAtom("H", 380, 320));
        atoms.add(createAtom("H", 480, 250));
        atoms.add(createAtom("H", 480, 350));
        
        // Create bonds
        bonds.add(new Bond(c1, c2, 1));
        bonds.add(new Bond(c2, o, 1));
        bonds.add(new Bond(o, h, 1));
        
        // Hydrogen bonds
        for (int i = 4; i < atoms.size(); i++) {
            if (i < 7) bonds.add(new Bond(c1, atoms.get(i), 1));
            else bonds.add(new Bond(c2, atoms.get(i), 1));
        }
        
        currentMolecule.atoms = atoms;
        currentMolecule.bonds = bonds;
    }
    
    /**
     * Create Sulfuric Acid (H2SO4) molecule
     */
    private void createSulfuricAcidMolecule() {
        currentMolecule = new Molecule("H₂SO₄", "Sulfuric Acid", "Tetrahedral");
        
        // Create atoms
        Atom s = createAtom("S", 400, 300);
        Atom o1 = createAtom("O", 350, 250);
        Atom o2 = createAtom("O", 450, 250);
        Atom o3 = createAtom("O", 350, 350);
        Atom o4 = createAtom("O", 450, 350);
        Atom h1 = createAtom("H", 320, 230);
        Atom h2 = createAtom("H", 320, 370);
        
        atoms.add(s);
        atoms.add(o1);
        atoms.add(o2);
        atoms.add(o3);
        atoms.add(o4);
        atoms.add(h1);
        atoms.add(h2);
        
        // Create bonds
        bonds.add(new Bond(s, o1, 2));
        bonds.add(new Bond(s, o2, 2));
        bonds.add(new Bond(s, o3, 1));
        bonds.add(new Bond(s, o4, 1));
        bonds.add(new Bond(o3, h1, 1));
        bonds.add(new Bond(o4, h2, 1));
        
        currentMolecule.atoms = atoms;
        currentMolecule.bonds = bonds;
    }
    
    /**
     * Create Sodium Chloride (NaCl) ionic compound
     */
    private void createNaClMolecule() {
        currentMolecule = new Molecule("NaCl", "Sodium Chloride", "Ionic");
        
        // Create atoms
        Atom na = createAtom("Na", 350, 300);
        Atom cl = createAtom("Cl", 450, 300);
        
        atoms.add(na);
        atoms.add(cl);
        
        // Ionic bond (represented as single bond)
        bonds.add(new Bond(na, cl, 1));
        
        currentMolecule.atoms = atoms;
        currentMolecule.bonds = bonds;
    }
    
    /**
     * Create atom from periodic table
     */
    private Atom createAtom(String symbol, double x, double y) {
        Atom template = PERIODIC_TABLE.get(symbol);
        Atom atom = new Atom(template.symbol, template.name, template.atomicNumber, 
                            template.valence, template.color);
        atom.x = x;
        atom.y = y;
        atom.vx = 0;
        atom.vy = 0;
        return atom;
    }
    
    /**
     * Start animation
     */
    private void startAnimation() {
        isRunning = true;
        // Give atoms random velocities
        for (Atom atom : atoms) {
            atom.vx = (Math.random() - 0.5) * 50;
            atom.vy = (Math.random() - 0.5) * 50;
        }
        animationTimer.start();
        statusLabel.setText(" Animating molecular motion...");
    }
    
    /**
     * Stop animation
     */
    private void stopAnimation() {
        isRunning = false;
        animationTimer.stop();
        // Stop all atoms
        for (Atom atom : atoms) {
            atom.vx = 0;
            atom.vy = 0;
        }
        statusLabel.setText(" Animation stopped");
    }
    
    /**
     * Update animation
     */
    private void updateAnimation() {
        double dt = 0.5;
        int width = moleculePanel.getWidth();
        int height = moleculePanel.getHeight();
        
        // Update atom positions
        for (Atom atom : atoms) {
            atom.update(dt, width, height);
        }
        
        // Apply bond constraints (keep bonded atoms together)
        for (Bond bond : bonds) {
            double dx = bond.atom2.x - bond.atom1.x;
            double dy = bond.atom2.y - bond.atom1.y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            double targetDistance = 50 * bond.bondOrder;
            
            if (distance > 0) {
                double diff = (distance - targetDistance) / distance;
                double offsetX = dx * diff * 0.5;
                double offsetY = dy * diff * 0.5;
                
                bond.atom1.x += offsetX;
                bond.atom1.y += offsetY;
                bond.atom2.x -= offsetX;
                bond.atom2.y -= offsetY;
            }
        }
        
        moleculePanel.repaint();
    }
    
    /**
     * Draw molecule
     */
    private void drawMolecule(Graphics2D g2d) {
        // Draw bonds first
        g2d.setStroke(new BasicStroke(3));
        for (Bond bond : bonds) {
            g2d.setColor(Color.DARK_GRAY);
            
            int x1 = (int)bond.atom1.x;
            int y1 = (int)bond.atom1.y;
            int x2 = (int)bond.atom2.x;
            int y2 = (int)bond.atom2.y;
            
            if (bond.bondOrder == 1) {
                // Single bond
                g2d.drawLine(x1, y1, x2, y2);
            } else if (bond.bondOrder == 2) {
                // Double bond
                double angle = Math.atan2(y2 - y1, x2 - x1);
                double perpAngle = angle + Math.PI / 2;
                int offset = 3;
                
                int ox = (int)(offset * Math.cos(perpAngle));
                int oy = (int)(offset * Math.sin(perpAngle));
                
                g2d.drawLine(x1 + ox, y1 + oy, x2 + ox, y2 + oy);
                g2d.drawLine(x1 - ox, y1 - oy, x2 - ox, y2 - oy);
            } else if (bond.bondOrder == 3) {
                // Triple bond
                g2d.drawLine(x1, y1, x2, y2);
                double angle = Math.atan2(y2 - y1, x2 - x1);
                double perpAngle = angle + Math.PI / 2;
                int offset = 5;
                
                int ox = (int)(offset * Math.cos(perpAngle));
                int oy = (int)(offset * Math.sin(perpAngle));
                
                g2d.drawLine(x1 + ox, y1 + oy, x2 + ox, y2 + oy);
                g2d.drawLine(x1 - ox, y1 - oy, x2 - ox, y2 - oy);
            }
        }
        
        // Draw atoms
        for (Atom atom : atoms) {
            int x = (int)atom.x;
            int y = (int)atom.y;
            int radius = 20;
            
            // Draw atom circle
            g2d.setColor(atom.color);
            g2d.fillOval(x - radius, y - radius, radius * 2, radius * 2);
            
            // Draw border
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(x - radius, y - radius, radius * 2, radius * 2);
            
            // Draw symbol
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(atom.symbol);
            int textHeight = fm.getHeight();
            
            g2d.setColor(atom.color.getRed() + atom.color.getGreen() + atom.color.getBlue() > 400 ? 
                        Color.BLACK : Color.WHITE);
            g2d.drawString(atom.symbol, x - textWidth / 2, y + textHeight / 4);
        }
    }
    
    /**
     * Update molecular data display
     */
    private void updateMolecularData() {
        StringBuilder data = new StringBuilder();
        data.append("MOLECULAR ANALYSIS\n");
        data.append("═══════════════════════════════════\n\n");
        data.append(String.format("Name: %s\n", currentMolecule.name));
        data.append(String.format("Formula: %s\n", currentMolecule.formula));
        data.append(String.format("Structure: %s\n\n", currentMolecule.structure));
        
        data.append("Composition:\n");
        Map<String, Integer> composition = new HashMap<>();
        for (Atom atom : atoms) {
            composition.put(atom.symbol, composition.getOrDefault(atom.symbol, 0) + 1);
        }
        for (Map.Entry<String, Integer> entry : composition.entrySet()) {
            Atom atom = PERIODIC_TABLE.get(entry.getKey());
            data.append(String.format("  %s (%s): %d atom(s)\n", 
                entry.getKey(), atom.name, entry.getValue()));
        }
        
        data.append(String.format("\nBonds: %d\n", bonds.size()));
        int single = 0, doubleBond = 0, triple = 0;
        for (Bond bond : bonds) {
            if (bond.bondOrder == 1) single++;
            else if (bond.bondOrder == 2) doubleBond++;
            else if (bond.bondOrder == 3) triple++;
        }
        if (single > 0) data.append(String.format("  Single: %d\n", single));
        if (doubleBond > 0) data.append(String.format("  Double: %d\n", doubleBond));
        if (triple > 0) data.append(String.format("  Triple: %d\n", triple));
        
        // Calculate molar mass
        double molarMass = 0;
        Map<String, Double> atomicMasses = new HashMap<>();
        atomicMasses.put("H", 1.008);
        atomicMasses.put("C", 12.011);
        atomicMasses.put("N", 14.007);
        atomicMasses.put("O", 15.999);
        atomicMasses.put("S", 32.065);
        atomicMasses.put("P", 30.974);
        atomicMasses.put("Cl", 35.453);
        atomicMasses.put("Na", 22.990);
        atomicMasses.put("Ca", 40.078);
        atomicMasses.put("Fe", 55.845);
        
        for (Atom atom : atoms) {
            molarMass += atomicMasses.getOrDefault(atom.symbol, 0.0);
        }
        
        data.append(String.format("\nMolar Mass: %.2f g/mol\n", molarMass));
        
        dataArea.setText(data.toString());
    }
    
    /**
     * Show element information
     */
    private void showElementInfo(String symbol) {
        Atom atom = PERIODIC_TABLE.get(symbol);
        String info = String.format(
            "Element: %s (%s)\n" +
            "Atomic Number: %d\n" +
            "Valence: %d\n\n" +
            "This element can form up to %d bond(s).",
            atom.name, atom.symbol, atom.atomicNumber, atom.valence, atom.valence
        );
        JOptionPane.showMessageDialog(this, info, "Element Information", 
                                     JOptionPane.INFORMATION_MESSAGE);
        score += 5;
    }
    
    /**
     * Start quiz
     */
    private void startQuiz() {
        String[] questions = {
            "What is the molecular formula?",
            "How many atoms are in this molecule?",
            "What is the molecular structure?",
            "How many bonds are there?"
        };
        
        String[] answers = {
            currentMolecule.formula,
            String.valueOf(atoms.size()),
            currentMolecule.structure,
            String.valueOf(bonds.size())
        };
        
        int correct = 0;
        for (int i = 0; i < questions.length; i++) {
            String answer = JOptionPane.showInputDialog(this, questions[i]);
            if (answer != null && answer.trim().equalsIgnoreCase(answers[i].trim())) {
                correct++;
                score += 10;
            }
        }
        
        correctAnswers += correct;
        String feedback = String.format("You got %d out of %d correct!\n\n", correct, questions.length);
        if (correct == questions.length) {
            feedback += "Perfect! You've mastered this molecule! 🎉\n";
            feedback += "Ubuntu wisdom: Your knowledge grows stronger when shared with others.";
        } else if (correct >= questions.length / 2) {
            feedback += "Good job! Keep practicing to achieve mastery.\n";
            feedback += "Ubuntu wisdom: Every mistake is a step toward understanding.";
        } else {
            feedback += "Keep learning! Review the molecular data and try again.\n";
            feedback += "Ubuntu wisdom: We learn together, we grow together.";
        }
        
        JOptionPane.showMessageDialog(this, feedback, "Quiz Results", 
                                     JOptionPane.INFORMATION_MESSAGE);
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
                "CHEM_MOL_001",
                "Molecular Structures - Interactive Exploration",
                "Physical Sciences",
                "Grade 11",
                LabExercise.ExerciseType.EXPERIMENT
            );
            exercise.setDescription("Explore molecular structures and chemical bonds");
            exercise.addLearningObjective("Understand molecular geometry");
            exercise.addLearningObjective("Identify chemical bonds");
            exercise.addLearningObjective("Calculate molar mass");
            exercise.setCapsReference("CAPS Grade 11 Physical Sciences: Chemical Systems");
            
            ChemistryLab lab = new ChemistryLab(exercise);
            lab.setVisible(true);
        });
    }
}
