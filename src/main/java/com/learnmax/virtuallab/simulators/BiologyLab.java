package com.learnmax.virtuallab.simulators;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Advanced Biology Lab Simulator
 * 
 * Provides real, interactive simulations for:
 * 1. Cell Biology - Cell structure and organelles
 * 2. Genetics - Punnett squares and inheritance
 * 3. Ecosystem Dynamics - Population and food chains
 * 4. Human Body Systems - Circulatory, respiratory, digestive
 * 5. Photosynthesis - Light and dark reactions
 * 
 * CAPS Aligned: Grade 10-12 Life Sciences
 * Ubuntu Philosophy: "Understanding life connects us all"
 */
public class BiologyLab extends JFrame {
    
    private JTabbedPane tabbedPane;
    private JTextArea outputArea;
    private JPanel cellPanel;
    private JPanel geneticsPanel;
    private JPanel ecosystemPanel;
    
    // Genetics simulation
    private String parent1Allele1 = "B";
    private String parent1Allele2 = "b";
    private String parent2Allele1 = "B";
    private String parent2Allele2 = "b";
    
    // Ecosystem simulation
    private int preyPopulation = 100;
    private int predatorPopulation = 20;
    private int plantPopulation = 500;
    private List<int[]> populationHistory = new ArrayList<>();
    
    // Cell organelles
    private Map<String, String> organelles = new LinkedHashMap<>();
    
    public BiologyLab() {
        setTitle("LearnMax Biology Lab - Life Sciences Simulator");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initializeOrganelles();
        initializeUI();
    }
    
    private void initializeOrganelles() {
        organelles.put("Nucleus", "Control center of the cell, contains DNA and controls all cell activities");
        organelles.put("Mitochondria", "Powerhouse of the cell, produces ATP through cellular respiration");
        organelles.put("Ribosome", "Protein synthesis factory, translates mRNA into proteins");
        organelles.put("Endoplasmic Reticulum", "Transport network, rough ER has ribosomes, smooth ER synthesizes lipids");
        organelles.put("Golgi Apparatus", "Packaging and shipping center, modifies and packages proteins");
        organelles.put("Lysosome", "Digestive system, breaks down waste and cellular debris");
        organelles.put("Cell Membrane", "Protective barrier, controls what enters and exits the cell");
        organelles.put("Chloroplast", "Photosynthesis factory (plant cells only), converts light to energy");
        organelles.put("Vacuole", "Storage tank, stores water, nutrients, and waste");
        organelles.put("Cytoplasm", "Cell's interior fluid, suspends organelles and enables chemical reactions");
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(46, 125, 50));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        
        JLabel titleLabel = new JLabel("  🧬 Biology Lab - Life Sciences Simulator");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JLabel ubuntuLabel = new JLabel("\"Understanding life connects us all\"  ");
        ubuntuLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        ubuntuLabel.setForeground(Color.WHITE);
        headerPanel.add(ubuntuLabel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Tabbed pane for different experiments
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Add tabs
        tabbedPane.addTab("🔬 Cell Biology", createCellBiologyPanel());
        tabbedPane.addTab("🧬 Genetics", createGeneticsPanel());
        tabbedPane.addTab("🌿 Ecosystem", createEcosystemPanel());
        tabbedPane.addTab("❤️ Human Body", createHumanBodyPanel());
        tabbedPane.addTab("☀️ Photosynthesis", createPhotosynthesisPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Output area
        outputArea = new JTextArea(8, 50);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setBackground(new Color(240, 248, 255));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lab Results & Observations"));
        add(scrollPane, BorderLayout.SOUTH);
        
        appendOutput("Welcome to the Biology Lab!");
        appendOutput("Select an experiment tab above to begin your exploration of life sciences.");
        appendOutput("Ubuntu: \"Understanding life connects us all\" - When we understand biology, we understand ourselves and our connection to all living things.");
    }
    
    private JPanel createCellBiologyPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Instructions
        JLabel instructions = new JLabel("<html><b>Cell Biology:</b> Click on organelles to learn their functions. Explore both plant and animal cells.</html>");
        panel.add(instructions, BorderLayout.NORTH);
        
        // Cell type selector
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> cellTypeCombo = new JComboBox<>(new String[]{"Animal Cell", "Plant Cell"});
        controlPanel.add(new JLabel("Cell Type: "));
        controlPanel.add(cellTypeCombo);
        
        JButton exploreBtn = new JButton("Explore Cell");
        controlPanel.add(exploreBtn);
        
        panel.add(controlPanel, BorderLayout.NORTH);
        
        // Organelle list
        JPanel organellePanel = new JPanel(new GridLayout(5, 2, 10, 10));
        organellePanel.setBorder(BorderFactory.createTitledBorder("Cell Organelles"));
        
        for (String organelle : organelles.keySet()) {
            JButton btn = new JButton(organelle);
            btn.setFont(new Font("Arial", Font.PLAIN, 12));
            btn.addActionListener(e -> exploreOrganelle(organelle));
            organellePanel.add(btn);
        }
        
        panel.add(organellePanel, BorderLayout.CENTER);
        
        // Cell comparison button
        JButton compareBtn = new JButton("Compare Plant vs Animal Cells");
        compareBtn.addActionListener(e -> compareCells());
        panel.add(compareBtn, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void exploreOrganelle(String organelle) {
        String description = organelles.get(organelle);
        appendOutput("\n--- " + organelle + " ---");
        appendOutput("Function: " + description);
        
        // Add AI-powered insights
        switch (organelle) {
            case "Nucleus":
                appendOutput("💡 AI Insight: The nucleus is like the brain of a cell. It contains chromosomes made of DNA, which carry all genetic instructions.");
                appendOutput("📊 Fun Fact: Human cells have 46 chromosomes (23 pairs)!");
                break;
            case "Mitochondria":
                appendOutput("💡 AI Insight: Mitochondria have their own DNA! Scientists believe they were once independent bacteria that formed a symbiotic relationship with cells.");
                appendOutput("📊 Energy Production: C₆H₁₂O₆ + 6O₂ → 6CO₂ + 6H₂O + 36-38 ATP");
                break;
            case "Chloroplast":
                appendOutput("💡 AI Insight: Chloroplasts contain chlorophyll, which gives plants their green color and captures light energy.");
                appendOutput("📊 Photosynthesis: 6CO₂ + 6H₂O + Light → C₆H₁₂O₆ + 6O₂");
                break;
            default:
                appendOutput("💡 AI Insight: Each organelle works together like a team. Ubuntu teaches us that we are stronger together!");
                break;
        }
    }
    
    private void compareCells() {
        appendOutput("\n=== PLANT CELL vs ANIMAL CELL ===");
        appendOutput("┌─────────────────┬─────────────┬─────────────┐");
        appendOutput("│ Feature         │ Plant Cell  │ Animal Cell │");
        appendOutput("├─────────────────┼─────────────┼─────────────┤");
        appendOutput("│ Cell Wall       │ ✓ Present   │ ✗ Absent    │");
        appendOutput("│ Chloroplast     │ ✓ Present   │ ✗ Absent    │");
        appendOutput("│ Large Vacuole   │ ✓ Present   │ ✗ Small/None│");
        appendOutput("│ Centrioles      │ ✗ Absent    │ ✓ Present   │");
        appendOutput("│ Shape           │ Fixed/Rigid │ Irregular   │");
        appendOutput("│ Lysosomes       │ Rare        │ ✓ Present   │");
        appendOutput("└─────────────────┴─────────────┴─────────────┘");
        appendOutput("\n🌍 Ubuntu Connection: Just as plant and animal cells have different structures but work together in ecosystems, humans have different strengths but thrive together in communities.");
    }
    
    private JPanel createGeneticsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Instructions
        JLabel instructions = new JLabel("<html><b>Genetics:</b> Explore inheritance patterns using Punnett squares. Select alleles for each parent.</html>");
        panel.add(instructions, BorderLayout.NORTH);
        
        // Parent selection
        JPanel parentPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        parentPanel.setBorder(BorderFactory.createTitledBorder("Parent Alleles"));
        
        String[] alleles = {"B (Dominant)", "b (Recessive)"};
        
        JComboBox<String> p1a1 = new JComboBox<>(alleles);
        JComboBox<String> p1a2 = new JComboBox<>(alleles);
        p1a2.setSelectedIndex(1);
        JComboBox<String> p2a1 = new JComboBox<>(alleles);
        JComboBox<String> p2a2 = new JComboBox<>(alleles);
        p2a2.setSelectedIndex(1);
        
        parentPanel.add(new JLabel("Parent 1 Allele 1:"));
        parentPanel.add(p1a1);
        parentPanel.add(new JLabel("Parent 1 Allele 2:"));
        parentPanel.add(p1a2);
        parentPanel.add(new JLabel("Parent 2 Allele 1:"));
        parentPanel.add(p2a1);
        parentPanel.add(new JLabel("Parent 2 Allele 2:"));
        parentPanel.add(p2a2);
        
        panel.add(parentPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton crossBtn = new JButton("Perform Genetic Cross");
        crossBtn.addActionListener(e -> {
            parent1Allele1 = p1a1.getSelectedItem().toString().substring(0, 1);
            parent1Allele2 = p1a2.getSelectedItem().toString().substring(0, 1);
            parent2Allele1 = p2a1.getSelectedItem().toString().substring(0, 1);
            parent2Allele2 = p2a2.getSelectedItem().toString().substring(0, 1);
            performGeneticCross();
        });
        buttonPanel.add(crossBtn);
        
        JButton inheritanceBtn = new JButton("Learn Inheritance Patterns");
        inheritanceBtn.addActionListener(e -> explainInheritance());
        buttonPanel.add(inheritanceBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void performGeneticCross() {
        appendOutput("\n=== PUNNETT SQUARE ===");
        appendOutput("Parent 1: " + parent1Allele1 + parent1Allele2);
        appendOutput("Parent 2: " + parent2Allele1 + parent2Allele2);
        
        String[][] punnett = new String[2][2];
        punnett[0][0] = parent1Allele1 + parent2Allele1;
        punnett[0][1] = parent1Allele1 + parent2Allele2;
        punnett[1][0] = parent1Allele2 + parent2Allele1;
        punnett[1][1] = parent1Allele2 + parent2Allele2;
        
        appendOutput("\n┌─────┬─────┬─────┐");
        appendOutput("│     │  " + parent2Allele1 + "  │  " + parent2Allele2 + "  │");
        appendOutput("├─────┼─────┼─────┤");
        appendOutput("│  " + parent1Allele1 + "  │ " + sortAlleles(punnett[0][0]) + "  │ " + sortAlleles(punnett[0][1]) + "  │");
        appendOutput("├─────┼─────┼─────┤");
        appendOutput("│  " + parent1Allele2 + "  │ " + sortAlleles(punnett[1][0]) + "  │ " + sortAlleles(punnett[1][1]) + "  │");
        appendOutput("└─────┴─────┴─────┘");
        
        // Calculate ratios
        int dominant = 0, heterozygous = 0, recessive = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                String genotype = sortAlleles(punnett[i][j]);
                if (genotype.equals("BB")) dominant++;
                else if (genotype.equals("Bb")) heterozygous++;
                else recessive++;
            }
        }
        
        appendOutput("\n📊 Genotype Ratios:");
        appendOutput("  BB (Homozygous Dominant): " + dominant + "/4 = " + (dominant * 25) + "%");
        appendOutput("  Bb (Heterozygous): " + heterozygous + "/4 = " + (heterozygous * 25) + "%");
        appendOutput("  bb (Homozygous Recessive): " + recessive + "/4 = " + (recessive * 25) + "%");
        
        int phenotypeDominant = dominant + heterozygous;
        appendOutput("\n📊 Phenotype Ratios:");
        appendOutput("  Dominant Trait: " + phenotypeDominant + "/4 = " + (phenotypeDominant * 25) + "%");
        appendOutput("  Recessive Trait: " + recessive + "/4 = " + (recessive * 25) + "%");
        
        appendOutput("\n💡 AI Insight: This is Mendelian inheritance! Gregor Mendel discovered these patterns using pea plants in the 1860s.");
    }
    
    private String sortAlleles(String genotype) {
        char[] chars = genotype.toCharArray();
        if (chars[0] > chars[1]) {
            char temp = chars[0];
            chars[0] = chars[1];
            chars[1] = temp;
        }
        return new String(chars);
    }
    
    private void explainInheritance() {
        appendOutput("\n=== INHERITANCE PATTERNS ===");
        appendOutput("\n1. COMPLETE DOMINANCE");
        appendOutput("   - One allele completely masks the other");
        appendOutput("   - Example: Brown eyes (B) dominant over blue (b)");
        appendOutput("\n2. INCOMPLETE DOMINANCE");
        appendOutput("   - Neither allele is completely dominant");
        appendOutput("   - Heterozygote shows intermediate phenotype");
        appendOutput("   - Example: Red + White flowers = Pink flowers");
        appendOutput("\n3. CODOMINANCE");
        appendOutput("   - Both alleles are fully expressed");
        appendOutput("   - Example: AB blood type (both A and B antigens present)");
        appendOutput("\n4. SEX-LINKED INHERITANCE");
        appendOutput("   - Genes located on sex chromosomes (X or Y)");
        appendOutput("   - Example: Color blindness, hemophilia");
        appendOutput("\n🌍 Ubuntu: Just as we inherit traits from our parents, we also inherit culture, values, and wisdom from our communities.");
    }
    
    private JPanel createEcosystemPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Instructions
        JLabel instructions = new JLabel("<html><b>Ecosystem Dynamics:</b> Simulate predator-prey relationships and observe population changes over time.</html>");
        panel.add(instructions, BorderLayout.NORTH);
        
        // Population controls
        JPanel controlPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Initial Populations"));
        
        JSpinner plantSpinner = new JSpinner(new SpinnerNumberModel(500, 100, 1000, 50));
        JSpinner preySpinner = new JSpinner(new SpinnerNumberModel(100, 10, 500, 10));
        JSpinner predatorSpinner = new JSpinner(new SpinnerNumberModel(20, 5, 100, 5));
        
        controlPanel.add(new JLabel("🌿 Plants:"));
        controlPanel.add(plantSpinner);
        controlPanel.add(new JLabel("🐰 Prey (Herbivores):"));
        controlPanel.add(preySpinner);
        controlPanel.add(new JLabel("🦁 Predators:"));
        controlPanel.add(predatorSpinner);
        
        panel.add(controlPanel, BorderLayout.CENTER);
        
        // Simulation buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton simulateBtn = new JButton("Run 10-Year Simulation");
        simulateBtn.addActionListener(e -> {
            plantPopulation = (Integer) plantSpinner.getValue();
            preyPopulation = (Integer) preySpinner.getValue();
            predatorPopulation = (Integer) predatorSpinner.getValue();
            runEcosystemSimulation();
        });
        buttonPanel.add(simulateBtn);
        
        JButton foodChainBtn = new JButton("Show Food Chain");
        foodChainBtn.addActionListener(e -> showFoodChain());
        buttonPanel.add(foodChainBtn);
        
        JButton biodiversityBtn = new JButton("Biodiversity Importance");
        biodiversityBtn.addActionListener(e -> explainBiodiversity());
        buttonPanel.add(biodiversityBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void runEcosystemSimulation() {
        appendOutput("\n=== ECOSYSTEM SIMULATION (10 Years) ===");
        appendOutput("Initial: Plants=" + plantPopulation + ", Prey=" + preyPopulation + ", Predators=" + predatorPopulation);
        appendOutput("\nYear | Plants | Prey | Predators | Status");
        appendOutput("-----|--------|------|-----------|--------");
        
        populationHistory.clear();
        int plants = plantPopulation;
        int prey = preyPopulation;
        int predators = predatorPopulation;
        
        for (int year = 1; year <= 10; year++) {
            // Lotka-Volterra inspired dynamics
            double plantGrowth = 0.3;
            double preyBirthRate = 0.5;
            double preyDeathRate = 0.02;
            double predatorBirthRate = 0.01;
            double predatorDeathRate = 0.3;
            
            // Calculate changes
            int newPlants = (int) (plants * plantGrowth) - (int) (prey * 0.5);
            int newPrey = (int) (prey * preyBirthRate * (plants / 500.0)) - (int) (prey * preyDeathRate * predators);
            int newPredators = (int) (predators * predatorBirthRate * prey) - (int) (predators * predatorDeathRate);
            
            plants = Math.max(50, Math.min(1000, plants + newPlants));
            prey = Math.max(5, Math.min(500, prey + newPrey));
            predators = Math.max(2, Math.min(100, predators + newPredators));
            
            populationHistory.add(new int[]{plants, prey, predators});
            
            String status = getEcosystemStatus(plants, prey, predators);
            appendOutput(String.format("%4d | %6d | %4d | %9d | %s", year, plants, prey, predators, status));
        }
        
        appendOutput("\n💡 AI Insight: This demonstrates the predator-prey cycle. When prey is abundant, predators thrive and increase. As predators increase, prey decreases, which then causes predator decline.");
        appendOutput("🌍 Ubuntu: In nature, all species are interconnected. The health of one affects all others - just like in human communities.");
    }
    
    private String getEcosystemStatus(int plants, int prey, int predators) {
        if (plants < 100) return "⚠️ Plant Crisis";
        if (prey < 20) return "⚠️ Prey Decline";
        if (predators < 5) return "⚠️ Predator Decline";
        if (plants > 800 && prey > 200 && predators > 40) return "🌟 Thriving";
        return "✓ Stable";
    }
    
    private void showFoodChain() {
        appendOutput("\n=== FOOD CHAIN / FOOD WEB ===");
        appendOutput("\n                    ☀️ SUN (Energy Source)");
        appendOutput("                        │");
        appendOutput("                        ▼");
        appendOutput("                    🌿 PRODUCERS");
        appendOutput("              (Plants, Algae, Phytoplankton)");
        appendOutput("                        │");
        appendOutput("                        ▼");
        appendOutput("               🐰 PRIMARY CONSUMERS");
        appendOutput("              (Herbivores: Rabbits, Deer)");
        appendOutput("                        │");
        appendOutput("                        ▼");
        appendOutput("              🦊 SECONDARY CONSUMERS");
        appendOutput("              (Carnivores: Foxes, Snakes)");
        appendOutput("                        │");
        appendOutput("                        ▼");
        appendOutput("               🦁 TERTIARY CONSUMERS");
        appendOutput("              (Top Predators: Lions, Eagles)");
        appendOutput("                        │");
        appendOutput("                        ▼");
        appendOutput("               🦠 DECOMPOSERS");
        appendOutput("              (Bacteria, Fungi, Worms)");
        appendOutput("                        │");
        appendOutput("                        ▼");
        appendOutput("               🌍 NUTRIENTS RECYCLED");
        appendOutput("\n📊 Energy Transfer: Only 10% of energy transfers between trophic levels!");
    }
    
    private void explainBiodiversity() {
        appendOutput("\n=== BIODIVERSITY IMPORTANCE ===");
        appendOutput("\n🌍 What is Biodiversity?");
        appendOutput("   The variety of life on Earth - all species, genes, and ecosystems.");
        appendOutput("\n📊 South Africa's Biodiversity:");
        appendOutput("   - 3rd most biodiverse country in the world");
        appendOutput("   - 8 biomes (Fynbos, Savanna, Grassland, etc.)");
        appendOutput("   - 20,000+ plant species (10% of world's plants)");
        appendOutput("   - 800+ bird species");
        appendOutput("   - 300+ mammal species");
        appendOutput("\n🌿 Why Biodiversity Matters:");
        appendOutput("   1. Ecosystem Services (clean air, water, pollination)");
        appendOutput("   2. Medicine (many drugs come from plants)");
        appendOutput("   3. Food Security (genetic diversity in crops)");
        appendOutput("   4. Climate Regulation (forests absorb CO₂)");
        appendOutput("   5. Cultural Value (connection to nature)");
        appendOutput("\n⚠️ Threats to Biodiversity:");
        appendOutput("   - Habitat destruction");
        appendOutput("   - Climate change");
        appendOutput("   - Pollution");
        appendOutput("   - Invasive species");
        appendOutput("   - Overexploitation");
        appendOutput("\n🌍 Ubuntu: \"Umuntu ngumuntu ngabantu\" - We are connected to all life. Protecting biodiversity protects ourselves.");
    }
    
    private JPanel createHumanBodyPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Instructions
        JLabel instructions = new JLabel("<html><b>Human Body Systems:</b> Explore how different organ systems work together to keep you alive.</html>");
        panel.add(instructions, BorderLayout.NORTH);
        
        // System buttons
        JPanel systemPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        systemPanel.setBorder(BorderFactory.createTitledBorder("Organ Systems"));
        
        String[][] systems = {
            {"❤️ Circulatory System", "circulatory"},
            {"🫁 Respiratory System", "respiratory"},
            {"🍽️ Digestive System", "digestive"},
            {"🧠 Nervous System", "nervous"},
            {"💪 Muscular System", "muscular"},
            {"🦴 Skeletal System", "skeletal"}
        };
        
        for (String[] system : systems) {
            JButton btn = new JButton(system[0]);
            btn.addActionListener(e -> exploreBodySystem(system[1]));
            systemPanel.add(btn);
        }
        
        panel.add(systemPanel, BorderLayout.CENTER);
        
        // Homeostasis button
        JButton homeostasisBtn = new JButton("Learn About Homeostasis");
        homeostasisBtn.addActionListener(e -> explainHomeostasis());
        panel.add(homeostasisBtn, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void exploreBodySystem(String system) {
        switch (system) {
            case "circulatory":
                appendOutput("\n=== ❤️ CIRCULATORY SYSTEM ===");
                appendOutput("\n🔴 Components:");
                appendOutput("   - Heart (4 chambers: 2 atria, 2 ventricles)");
                appendOutput("   - Blood vessels (arteries, veins, capillaries)");
                appendOutput("   - Blood (red cells, white cells, platelets, plasma)");
                appendOutput("\n📊 Amazing Facts:");
                appendOutput("   - Heart beats ~100,000 times per day");
                appendOutput("   - Blood travels 19,000 km through vessels");
                appendOutput("   - 5 liters of blood in average adult");
                appendOutput("\n🔄 Blood Flow Path:");
                appendOutput("   Body → Right Atrium → Right Ventricle → Lungs");
                appendOutput("   Lungs → Left Atrium → Left Ventricle → Body");
                break;
                
            case "respiratory":
                appendOutput("\n=== 🫁 RESPIRATORY SYSTEM ===");
                appendOutput("\n🔵 Components:");
                appendOutput("   - Nose, Pharynx, Larynx, Trachea");
                appendOutput("   - Bronchi, Bronchioles, Alveoli");
                appendOutput("   - Lungs, Diaphragm");
                appendOutput("\n📊 Amazing Facts:");
                appendOutput("   - 12-20 breaths per minute at rest");
                appendOutput("   - 300 million alveoli in each lung");
                appendOutput("   - Surface area of alveoli = tennis court!");
                appendOutput("\n🔄 Gas Exchange:");
                appendOutput("   O₂ (air) → Alveoli → Blood → Cells");
                appendOutput("   CO₂ (cells) → Blood → Alveoli → Exhaled");
                break;
                
            case "digestive":
                appendOutput("\n=== 🍽️ DIGESTIVE SYSTEM ===");
                appendOutput("\n🟢 Components:");
                appendOutput("   - Mouth, Esophagus, Stomach");
                appendOutput("   - Small Intestine, Large Intestine");
                appendOutput("   - Liver, Pancreas, Gallbladder");
                appendOutput("\n📊 Amazing Facts:");
                appendOutput("   - Digestive tract is 9 meters long!");
                appendOutput("   - Stomach acid (pH 1.5-3.5) can dissolve metal");
                appendOutput("   - Small intestine has 300 m² surface area");
                appendOutput("\n🔄 Digestion Journey:");
                appendOutput("   Mouth (mechanical) → Stomach (chemical)");
                appendOutput("   → Small Intestine (absorption) → Large Intestine (water)");
                break;
                
            case "nervous":
                appendOutput("\n=== 🧠 NERVOUS SYSTEM ===");
                appendOutput("\n🟣 Components:");
                appendOutput("   - Brain (cerebrum, cerebellum, brainstem)");
                appendOutput("   - Spinal Cord");
                appendOutput("   - Peripheral Nerves");
                appendOutput("\n📊 Amazing Facts:");
                appendOutput("   - 86 billion neurons in the brain");
                appendOutput("   - Nerve signals travel at 120 m/s");
                appendOutput("   - Brain uses 20% of body's energy");
                appendOutput("\n🔄 Signal Pathway:");
                appendOutput("   Stimulus → Receptor → Sensory Neuron → Brain");
                appendOutput("   Brain → Motor Neuron → Effector → Response");
                break;
                
            case "muscular":
                appendOutput("\n=== 💪 MUSCULAR SYSTEM ===");
                appendOutput("\n🟠 Types of Muscle:");
                appendOutput("   - Skeletal (voluntary, striated)");
                appendOutput("   - Cardiac (involuntary, striated)");
                appendOutput("   - Smooth (involuntary, non-striated)");
                appendOutput("\n📊 Amazing Facts:");
                appendOutput("   - 600+ muscles in the human body");
                appendOutput("   - Muscles make up 40% of body weight");
                appendOutput("   - Tongue is the strongest muscle (relative to size)");
                break;
                
            case "skeletal":
                appendOutput("\n=== 🦴 SKELETAL SYSTEM ===");
                appendOutput("\n⚪ Components:");
                appendOutput("   - 206 bones in adult body");
                appendOutput("   - Joints, Cartilage, Ligaments");
                appendOutput("\n📊 Amazing Facts:");
                appendOutput("   - Babies have 270 bones (some fuse)");
                appendOutput("   - Femur is the longest bone");
                appendOutput("   - Bones are 5x stronger than steel (weight for weight)");
                appendOutput("\n🔄 Functions:");
                appendOutput("   - Support, Protection, Movement");
                appendOutput("   - Blood cell production (bone marrow)");
                appendOutput("   - Mineral storage (calcium, phosphorus)");
                break;
        }
        
        appendOutput("\n🌍 Ubuntu: Our bodies are complex systems where every part works together - just like a community where everyone has a role to play.");
    }
    
    private void explainHomeostasis() {
        appendOutput("\n=== HOMEOSTASIS ===");
        appendOutput("\n📖 Definition:");
        appendOutput("   The maintenance of a stable internal environment despite external changes.");
        appendOutput("\n🔄 Negative Feedback Loop:");
        appendOutput("   Stimulus → Receptor → Control Center → Effector → Response");
        appendOutput("   Response reduces the original stimulus (returns to normal)");
        appendOutput("\n📊 Examples of Homeostasis:");
        appendOutput("\n1. TEMPERATURE REGULATION (37°C)");
        appendOutput("   Too Hot: Sweating, Vasodilation, Reduced metabolism");
        appendOutput("   Too Cold: Shivering, Vasoconstriction, Increased metabolism");
        appendOutput("\n2. BLOOD GLUCOSE (4-6 mmol/L)");
        appendOutput("   Too High: Insulin released → Glucose stored as glycogen");
        appendOutput("   Too Low: Glucagon released → Glycogen converted to glucose");
        appendOutput("\n3. WATER BALANCE (Osmoregulation)");
        appendOutput("   Too Much Water: Dilute urine, less ADH");
        appendOutput("   Too Little Water: Concentrated urine, more ADH");
        appendOutput("\n💡 AI Insight: Homeostasis is like a thermostat - it constantly monitors and adjusts to maintain balance.");
        appendOutput("🌍 Ubuntu: Just as our bodies maintain balance, communities thrive when there is harmony and mutual support.");
    }
    
    private JPanel createPhotosynthesisPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Instructions
        JLabel instructions = new JLabel("<html><b>Photosynthesis:</b> Explore how plants convert light energy into chemical energy.</html>");
        panel.add(instructions, BorderLayout.NORTH);
        
        // Simulation controls
        JPanel controlPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Photosynthesis Factors"));
        
        JSlider lightSlider = new JSlider(0, 100, 50);
        JSlider co2Slider = new JSlider(0, 100, 50);
        JSlider tempSlider = new JSlider(0, 50, 25);
        
        controlPanel.add(new JLabel("☀️ Light Intensity:"));
        controlPanel.add(lightSlider);
        controlPanel.add(new JLabel("💨 CO₂ Concentration:"));
        controlPanel.add(co2Slider);
        controlPanel.add(new JLabel("🌡️ Temperature (°C):"));
        controlPanel.add(tempSlider);
        
        panel.add(controlPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton simulateBtn = new JButton("Calculate Photosynthesis Rate");
        simulateBtn.addActionListener(e -> {
            int light = lightSlider.getValue();
            int co2 = co2Slider.getValue();
            int temp = tempSlider.getValue();
            simulatePhotosynthesis(light, co2, temp);
        });
        buttonPanel.add(simulateBtn);
        
        JButton processBtn = new JButton("Show Process Details");
        processBtn.addActionListener(e -> showPhotosynthesisProcess());
        buttonPanel.add(processBtn);
        
        JButton respirationBtn = new JButton("Compare with Respiration");
        respirationBtn.addActionListener(e -> compareWithRespiration());
        buttonPanel.add(respirationBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void simulatePhotosynthesis(int light, int co2, int temp) {
        appendOutput("\n=== PHOTOSYNTHESIS SIMULATION ===");
        appendOutput("Conditions: Light=" + light + "%, CO₂=" + co2 + "%, Temp=" + temp + "°C");
        
        // Calculate rate based on limiting factors
        double lightFactor = light / 100.0;
        double co2Factor = co2 / 100.0;
        double tempFactor = 1.0;
        
        // Temperature optimum around 25-30°C
        if (temp < 10) tempFactor = temp / 10.0;
        else if (temp > 40) tempFactor = Math.max(0, (50 - temp) / 10.0);
        else if (temp >= 25 && temp <= 30) tempFactor = 1.0;
        else tempFactor = 0.8;
        
        // Rate is limited by the lowest factor (Law of Limiting Factors)
        double rate = Math.min(Math.min(lightFactor, co2Factor), tempFactor) * 100;
        String limitingFactor = "";
        
        if (lightFactor <= co2Factor && lightFactor <= tempFactor) limitingFactor = "Light";
        else if (co2Factor <= lightFactor && co2Factor <= tempFactor) limitingFactor = "CO₂";
        else limitingFactor = "Temperature";
        
        appendOutput("\n📊 Results:");
        appendOutput("   Photosynthesis Rate: " + String.format("%.1f", rate) + "%");
        appendOutput("   Limiting Factor: " + limitingFactor);
        appendOutput("   Glucose Produced: " + String.format("%.2f", rate * 0.1) + " mg/hour");
        appendOutput("   O₂ Released: " + String.format("%.2f", rate * 0.15) + " ml/hour");
        
        // Recommendations
        appendOutput("\n💡 AI Recommendations:");
        if (light < 50) appendOutput("   - Increase light intensity for better results");
        if (co2 < 50) appendOutput("   - Increase CO₂ concentration for better results");
        if (temp < 20 || temp > 35) appendOutput("   - Adjust temperature to 25-30°C for optimal rate");
        
        appendOutput("\n🌍 Ubuntu: Plants provide oxygen for all life. When we protect plants, we protect ourselves and future generations.");
    }
    
    private void showPhotosynthesisProcess() {
        appendOutput("\n=== PHOTOSYNTHESIS PROCESS ===");
        appendOutput("\n📝 Overall Equation:");
        appendOutput("   6CO₂ + 6H₂O + Light Energy → C₆H₁₂O₆ + 6O₂");
        appendOutput("\n☀️ LIGHT-DEPENDENT REACTIONS (Thylakoid):");
        appendOutput("   1. Light absorbed by chlorophyll");
        appendOutput("   2. Water split (photolysis): 2H₂O → 4H⁺ + 4e⁻ + O₂");
        appendOutput("   3. ATP and NADPH produced");
        appendOutput("   4. O₂ released as byproduct");
        appendOutput("\n🌙 LIGHT-INDEPENDENT REACTIONS (Stroma):");
        appendOutput("   Also called Calvin Cycle or Dark Reactions");
        appendOutput("   1. CO₂ fixation by RuBisCO enzyme");
        appendOutput("   2. CO₂ + RuBP → 2 × 3-PGA");
        appendOutput("   3. ATP and NADPH used to reduce 3-PGA");
        appendOutput("   4. G3P produced → Glucose synthesized");
        appendOutput("\n📊 Energy Conversion:");
        appendOutput("   Light Energy → Chemical Energy (stored in glucose)");
        appendOutput("   Efficiency: ~1-2% of light energy converted");
    }
    
    private void compareWithRespiration() {
        appendOutput("\n=== PHOTOSYNTHESIS vs CELLULAR RESPIRATION ===");
        appendOutput("\n┌─────────────────┬─────────────────────┬─────────────────────┐");
        appendOutput("│ Feature         │ Photosynthesis      │ Cellular Respiration│");
        appendOutput("├─────────────────┼─────────────────────┼─────────────────────┤");
        appendOutput("│ Location        │ Chloroplast         │ Mitochondria        │");
        appendOutput("│ Reactants       │ CO₂ + H₂O + Light   │ C₆H₁₂O₆ + O₂        │");
        appendOutput("│ Products        │ C₆H₁₂O₆ + O₂        │ CO₂ + H₂O + ATP     │");
        appendOutput("│ Energy          │ Stored (anabolic)   │ Released (catabolic)│");
        appendOutput("│ Organisms       │ Plants, algae       │ All living cells    │");
        appendOutput("│ Time            │ Day (needs light)   │ Day and night       │");
        appendOutput("│ Purpose         │ Make food           │ Release energy      │");
        appendOutput("└─────────────────┴─────────────────────┴─────────────────────┘");
        appendOutput("\n💡 Key Insight: These processes are complementary!");
        appendOutput("   Photosynthesis: 6CO₂ + 6H₂O → C₆H₁₂O₆ + 6O₂");
        appendOutput("   Respiration:    C₆H₁₂O₆ + 6O₂ → 6CO₂ + 6H₂O + ATP");
        appendOutput("\n🌍 Ubuntu: Plants and animals depend on each other - a perfect example of interconnection in nature.");
    }
    
    private void appendOutput(String text) {
        outputArea.append(text + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BiologyLab lab = new BiologyLab();
            lab.setVisible(true);
        });
    }
}
