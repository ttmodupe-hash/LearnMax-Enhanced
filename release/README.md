# LearnMax Enhanced - Student Performance Tracker 2.0

A comprehensive student performance tracking system with modern design, trend analysis, and personalized feedback capabilities.

![Version](https://img.shields.io/badge/version-2.0.0-blue.svg)
![Java](https://img.shields.io/badge/java-11-orange.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)

## 🎯 Overview

LearnMax Enhanced is a complete rewrite of the student performance tracking system with significant improvements in functionality, design, and educational value. It provides educators with powerful tools to track student progress, identify underperforming students, and generate actionable insights.

## ✨ Key Features

### 📊 **Comprehensive Performance Tracking**
- Multiple assessments per subject with timestamps
- Historical data tracking and trend analysis
- Support for different assessment types (Test, Quiz, Assignment, Exam, Project)
- Weighted scoring for accurate averages

### 📈 **Trend Analysis**
- Automatic detection of improving, declining, or stable performance
- Subject-specific trend visualization
- Performance distribution charts
- Class-level analytics dashboard

### 🎓 **Personalized Feedback**
- Actionable feedback with specific recommendations
- Personalized study time suggestions based on performance
- Identification of focus areas and strong subjects
- Performance level categorization (Excellent, Good, Needs Improvement, At Risk)

### 🔍 **Advanced Search & Filtering**
- Search students by name, ID, or grade level
- Filter underperforming students by threshold
- Identify top performers
- Class performance reports

### 💾 **Data Persistence**
- JSON-based data storage
- Automatic save/load functionality
- CSV export for external analysis
- Data integrity validation

### 🎨 **Modern User Interface**
- Clean, professional design with color-coded status indicators
- Tabbed interface for organized navigation
- Real-time statistics dashboard
- Interactive data tables with sorting

## 🚀 Getting Started

### Prerequisites

- Java Development Kit (JDK) 11 or higher
- Maven 3.6 or higher
- Git (for cloning the repository)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/ttmodupe-hash/LearnMax-Enhanced.git
   cd LearnMax-Enhanced
   ```

2. **Build the project**
   ```bash
   mvn clean package
   ```

3. **Run the application**
   ```bash
   java -jar target/student-performance-tracker-2.0.0-ENHANCED-jar-with-dependencies.jar
   ```

   Or simply:
   ```bash
   mvn exec:java -Dexec.mainClass="com.learnmax.gui.StudentTrackerGUI"
   ```

## 📖 Usage Guide

### Adding a Student

1. Enter Student ID, Name, and Grade Level in the input fields
2. Optionally add first assessment (Subject, Score, Assessment Type)
3. Click "Add Student" button
4. Student will appear in the Student List tab

### Adding Assessments

1. Enter the Student ID of an existing student
2. Enter Subject name and Score (0-100)
3. Select Assessment Type from dropdown
4. Click "Add Assessment" button
5. Assessment is automatically saved with timestamp

### Viewing Student Details

1. Enter Student ID in the input field
2. Click "View Student" button
3. Detailed report appears in the Student Details tab
4. Shows all subjects, averages, trends, and personalized feedback

### Analyzing Trends

1. Enter Student ID
2. Click "Show Trends" button
3. View performance trends for each subject
4. See improvement recommendations and study time suggestions

### Searching Students

1. Navigate to Student List tab
2. Select search type (Name, ID, or Grade)
3. Enter search query
4. Click "Search" button
5. Results appear in the table

### Generating Reports

- **Class Performance Report**: Menu → Reports → Class Performance Report
- **Underperforming Students**: Menu → Reports → Underperforming Students
- **Top Performers**: Menu → Reports → Top Performers

### Exporting Data

1. Menu → File → Export to CSV
2. Choose save location
3. CSV file includes all student data with assessment details

## 🏗️ Project Structure

```
LearnMax-Enhanced/
├── src/
│   ├── main/
│   │   ├── java/com/learnmax/
│   │   │   ├── model/
│   │   │   │   ├── Assessment.java      # Assessment data model
│   │   │   │   └── Student.java         # Student data model
│   │   │   ├── manager/
│   │   │   │   └── StudentManager.java  # Data management & persistence
│   │   │   └── gui/
│   │   │       └── StudentTrackerGUI.java # Main GUI application
│   │   └── resources/
│   └── test/
│       └── java/com/learnmax/
│           ├── StudentTest.java          # Student class unit tests
│           └── StudentManagerTest.java   # Manager class unit tests
├── docs/
│   ├── performance_tracker_issues.md     # Detailed issue analysis
│   ├── executive_summary.md              # Executive summary
│   └── README_FIXES.md                   # Documentation of fixes
├── pom.xml                               # Maven configuration
└── README.md                             # This file
```

## 🧪 Testing

The project includes comprehensive unit tests covering all major functionality.

### Run all tests
```bash
mvn test
```

### Run specific test class
```bash
mvn test -Dtest=StudentTest
mvn test -Dtest=StudentManagerTest
```

### Test Coverage

- **Student Class**: 40+ test cases
  - Constructor validation
  - Assessment management
  - Average calculations
  - Trend analysis
  - Feedback generation
  - Subject normalization

- **StudentManager Class**: 30+ test cases
  - Student CRUD operations
  - Search functionality
  - Data persistence
  - Report generation
  - Export functionality

## 📊 Key Improvements Over Original

| Feature | Original | Enhanced |
|---------|----------|----------|
| **Data Persistence** | ❌ None (data lost on close) | ✅ JSON file storage |
| **Assessments per Subject** | ❌ 1 only | ✅ Unlimited with timestamps |
| **Historical Tracking** | ❌ None | ✅ Full history with dates |
| **Trend Analysis** | ❌ None | ✅ Improving/declining detection |
| **Feedback** | ⚠️ Generic | ✅ Actionable with study hours |
| **Search** | ⚠️ ID only | ✅ Name, grade, performance |
| **Input Validation** | ⚠️ Basic | ✅ Comprehensive |
| **Class Reports** | ❌ None | ✅ Full analytics |
| **Export** | ❌ None | ✅ CSV export |
| **Duplicate Prevention** | ❌ None | ✅ ID checking |

## 🎯 Educational Requirements Met

✅ **Historical tracking** with timestamps  
✅ **Trend analysis** (improving/declining)  
✅ **Personalized study recommendations**  
✅ **Underperformer identification**  
✅ **Time-based reporting** (monthly, weekly)  
✅ **Data persistence**  
✅ **Class-level analytics**  

## 🔮 Future Enhancements

### Short-term (1-3 months)
- [ ] Migrate to database (SQLite/MySQL)
- [ ] Add user authentication system
- [ ] Implement attendance tracking
- [ ] Create web-based interface
- [ ] Mobile app development

### Long-term (6-12 months)
- [ ] AI integration for predictive analytics
- [ ] Parent/guardian portal
- [ ] Real-time dashboards
- [ ] POPIA compliance features
- [ ] Integration with Department of Basic Education systems

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Development Team** - Initial work and enhancements

## 🙏 Acknowledgments

- Original LearnMax project by AMK488
- JFreeChart library for visualization
- Google Gson for JSON serialization
- JUnit 5 for testing framework

## 📞 Support

For support, please open an issue in the GitHub repository or contact the development team.

## 📚 Documentation

- [Detailed Issue Analysis](docs/performance_tracker_issues.md)
- [Executive Summary](docs/executive_summary.md)
- [Fix Documentation](docs/README_FIXES.md)

---

**Made with ❤️ for educators and students**
