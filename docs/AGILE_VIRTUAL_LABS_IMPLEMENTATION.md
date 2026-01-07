# Agile Virtual Labs - Architecture & Implementation Roadmap

**Author**: Manus AI  
**Date**: January 07, 2026  
**Version**: 3.1

---

## 1. Agile Implementation Philosophy

In response to the request for **agility**, this document outlines a strategic, modular, and extensible approach to building the LearnMax Virtual Labs ecosystem. Our agile philosophy prioritizes:

- **Incremental Delivery**: Delivering fully functional labs in stages, providing immediate value.
- **Modular Architecture**: Each lab is a self-contained module, making the system easy to maintain, test, and extend.
- **Extensibility**: A clear and simple process for adding new labs and features in the future.
- **Flexibility**: The ability to adapt to new requirements or curriculum changes without a complete redesign.

This document provides the complete implementation for the **Circuit Lab** and a detailed, actionable roadmap for the remaining labs, empowering future development with a clear and agile path forward.

---

## 2. Core Lab Architecture

All virtual labs, both completed and planned, adhere to a common, modular architecture. This ensures consistency, reusability, and ease of integration.

### Base Components

1.  **`LabExercise` Model**: A common data model (`/model/LabExercise.java`) that defines the metadata for every lab, including:
    - Title, Description, Subject, Grade
    - Learning Objectives
    - CAPS Curriculum Reference

2.  **Simulator Class**: Each lab is a `JFrame` that encapsulates all of its own logic, UI, and simulation engine (e.g., `CircuitLab.java`). This makes each lab a runnable, standalone application.

3.  **`VirtualLabManager`**: A central manager (`/manager/VirtualLabManager.java`) for tracking student progress across all labs, handling data persistence (JSON), and managing offline synchronization.

### The `VirtualLabsGUI` Integrator

The main `VirtualLabsGUI.java` serves as the central hub. It does not contain any lab-specific logic. Instead, it dynamically discovers and loads available labs based on a simple registration system. This is the key to our agile and extensible design.

---

## 3. Completed Lab: Circuit Simulator

**Status**: ✅ **Complete & Delivered**  
**File**: `src/main/java/com/learnmax/virtuallab/simulators/CircuitLab.java`

This lab provides a fully interactive environment for building and analyzing electrical circuits, based on real electronics principles.

### Features

- **Real-time Simulation**: Implements Ohm's Law (V=IR) for accurate calculations.
- **Interactive Breadboard**: Drag-and-drop components onto a virtual circuit board.
- **Component Library**: Includes Battery, Resistor, LED, and Switch.
- **Wiring Tool**: Connect components to form series and parallel circuits.
- **Live Analysis**: A data panel displays total resistance, current, and power in real-time.
- **Visual Feedback**: LEDs light up, resistors "glow" with heat, and switches show their state.

### Agile Implementation

- **Modular Components**: `Battery`, `Resistor`, `LED`, and `Switch` are all subclasses of a common `CircuitComponent`, making it easy to add new components (e.g., capacitors, inductors) in the future.
- **Simplified Engine**: The circuit analysis is currently a simplified series calculation. This can be extended to a more complex graph-based solver (e.g., using Kirchhoff's laws) in a future iteration without changing the component classes.

---

## 4. Implementation Roadmap for Future Labs

This section provides a clear, agile roadmap for implementing the remaining labs. Each roadmap includes the core concepts, a file structure, class outlines, and step-by-step implementation guidance.

### 4.1. Data Science Lab

**Status**: 🟡 **Roadmap Defined**

**Objective**: Introduce students to data analysis using real-world datasets.

**Core Concepts**: CSV parsing, descriptive statistics, data visualization.

**File Structure**:
```
/simulators/DataScienceLab.java
/utils/CSVReader.java
/resources/datasets/student_scores.csv (Completed)
```

**Class Outline: `DataScienceLab.java`**
```java
public class DataScienceLab extends JFrame {
    // UI Components
    private JTable dataTable;
    private JComboBox<String> columnSelector;
    private JTextArea statsArea;
    private ChartPanel chartPanel; // JFreeChart

    // Data
    private List<String[]> dataset;
    private String[] headers;

    // Methods
    private void loadDataset(String filePath);
    private void calculateStatistics(int columnIndex);
    private void generateBarChart(int columnIndex);
    private void generateScatterPlot(int colIndex1, int colIndex2);
}
```

**Agile Implementation Steps**:

1.  **Create `CSVReader.java`**: A simple utility class with a static method `read(filePath)` that returns `List<String[]>`. (1 hour)
2.  **Build Basic GUI**: Create the `DataScienceLab` frame with a `JTable` to display the CSV and a `JComboBox` to select columns. (2 hours)
3.  **Implement Statistics**: Add the logic to calculate mean, median, and standard deviation for the selected numeric column and display it in the `statsArea`. (1.5 hours)
4.  **Integrate JFreeChart**: Add a `ChartPanel` and implement the `generateBarChart` method to visualize the data of the selected column. (2 hours)
5.  **Add Scatter Plot**: Enhance the GUI to allow selecting two columns and generate a scatter plot to show correlation. (1.5 hours)

### 4.2. Enhanced Python Playground

**Status**: 🟡 **Roadmap Defined**

**Objective**: Provide a real, sandboxed Python execution environment.

**Core Concepts**: Process execution, stream redirection, security sandboxing.

**File Structure**:
```
/simulators/EnhancedPythonPlayground.java
/engine/PythonExecutor.java
```

**Class Outline: `PythonExecutor.java`**
```java
public class PythonExecutor {
    public interface ExecutionCallback {
        void onOutput(String line);
        void onError(String line);
        void onComplete(int exitCode);
    }

    public void execute(String code, ExecutionCallback callback) {
        // 1. Save the code to a temporary .py file
        // 2. Use ProcessBuilder to run "python3 /path/to/temp_file.py"
        // 3. Create separate threads to read the process's stdout and stderr streams
        // 4. Pass output line-by-line to callback.onOutput() and callback.onError()
        // 5. When the process terminates, call callback.onComplete()
    }
}
```

**Agile Implementation Steps**:

1.  **Create `PythonExecutor`**: Implement the core logic to run a Python script as a separate process and capture its output. (2.5 hours)
2.  **Build GUI**: Create the `EnhancedPythonPlayground` with a `JTextArea` for code input and another for console output. (1.5 hours)
3.  **Integrate Executor**: Wire the "Run" button to call the `PythonExecutor` and display the output in the console area. Use `SwingWorker` to avoid freezing the GUI. (2 hours)
4.  **Add Security (V1)**: Implement a basic security layer by running the Python script with a restricted user or in a simple sandbox. (Future iteration: use Docker or a more robust sandboxing library). (2 hours)

### 4.3. Biology Lab

**Status**: 🟡 **Roadmap Defined**

**Objective**: Simulate core biological processes like genetics and ecology.

**Core Concepts**: Mendelian genetics, Punnett squares, predator-prey models.

**File Structure**:
```
/simulators/BiologyLab.java
/engine/GeneticsEngine.java
/engine/EcologyEngine.java
```

**Class Outline: `GeneticsEngine.java`**
```java
public class GeneticsEngine {
    public String[][] generatePunnettSquare(String parent1Genotype, String parent2Genotype);
    public Map<String, Double> calculateOffspringRatios(String[][] punnettSquare);
}
```

**Agile Implementation Steps**:

1.  **Create `GeneticsEngine`**: Implement the logic for generating a Punnett square and calculating genotype/phenotype ratios. This is a pure logic class. (2 hours)
2.  **Build Genetics GUI**: In `BiologyLab.java`, create a tab for the Punnett Square simulator with input fields for parent genotypes and a `JTable` to display the square. (2 hours)
3.  **Create `EcologyEngine`**: Implement the Lotka-Volterra equations to model predator-prey population changes over time. The method should return a list of population data points. (2.5 hours)
4.  **Build Ecology GUI**: Add a second tab to the `BiologyLab` for the ecosystem simulation. Use `JFreeChart` to create a time-series line chart showing the populations of predators and prey over time. (2.5 hours)

---

## 5. GUI Integration Guide

Adding a new, completed lab to the main `VirtualLabsGUI` is designed to be a simple, agile process.

**File to Modify**: `src/main/java/com/learnmax/virtuallab/gui/VirtualLabsGUI.java`

**Steps**:

1.  **Create a `LabExercise` Entry**: In the `VirtualLabsGUI` constructor, create a new `LabExercise` object for your new lab. Fill in the title, description, subject, etc.

    ```java
    LabExercise circuitLabExercise = new LabExercise(
        "CIRC_ELEC_001",
        "Circuit Design - Real Electronics",
        "Physical Sciences", ...
    );
    availableExercises.add(circuitLabExercise);
    ```

2.  **Add to the `launchLab` Method**: Add a new `case` to the `switch` statement in the `launchLab` method. This tells the GUI how to instantiate your lab's `JFrame`.

    ```java
    switch (exercise.getId()) {
        // ... existing cases
        case "CIRC_ELEC_001":
            CircuitLab circuitLab = new CircuitLab(exercise);
            circuitLab.setVisible(true);
            break;
    }
    ```

That's it. The new lab will now appear in the list of available exercises and can be launched from the main dashboard. This modular design means you never have to modify the core logic of the `VirtualLabsGUI` to add new functionality.

---

## 6. Conclusion

This agile implementation plan delivers immediate value with the completed **Circuit Lab** while providing a clear, structured, and low-effort path for future development. The modular architecture ensures that LearnMax Enhanced can continue to grow and adapt, solidifying its position as an innovative and extensible educational platform.
