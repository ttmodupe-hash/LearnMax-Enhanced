# LearnMax Enhanced - Quick Start Guide

Get up and running with LearnMax Enhanced in 5 minutes!

## 🚀 Quick Installation

### Option 1: Run Pre-built JAR (Easiest)

1. **Download the JAR file** from the releases page or build it:
   ```bash
   git clone https://github.com/ttmodupe-hash/LearnMax-Enhanced.git
   cd LearnMax-Enhanced
   mvn clean package
   ```

2. **Run the application**:
   ```bash
   java -jar target/student-performance-tracker-2.0.0-ENHANCED-jar-with-dependencies.jar
   ```

3. **Done!** The application window will open.

### Option 2: Generate Sample Data First

1. **Generate 30 sample students** with realistic data:
   ```bash
   java -cp target/student-performance-tracker-2.0.0-ENHANCED-jar-with-dependencies.jar com.learnmax.util.SampleDataGenerator
   ```

2. **Run the GUI**:
   ```bash
   java -jar target/student-performance-tracker-2.0.0-ENHANCED-jar-with-dependencies.jar
   ```

3. **Explore!** You'll see 30 students with assessments already loaded.

## 📝 First Steps

### 1. Add Your First Student

1. Fill in the **Student Information** section:
   - Student ID: `S001`
   - Name: `John Doe`
   - Grade Level: `Grade 10`

2. Fill in the **Assessment Information** (optional):
   - Subject: `Mathematics`
   - Score: `85`
   - Assessment Type: `Test`

3. Click **"Add Student"** button

4. Check the **Student List** tab to see your new student!

### 2. Add More Assessments

1. Enter the Student ID: `S001`
2. Enter Subject: `Science`
3. Enter Score: `90`
4. Select Assessment Type: `Quiz`
5. Click **"Add Assessment"**

Repeat for multiple subjects and assessments!

### 3. View Student Details

1. Enter Student ID: `S001`
2. Click **"View Student"**
3. Switch to **"Student Details"** tab
4. See comprehensive report with:
   - All subjects and scores
   - Overall average
   - Performance feedback
   - Study recommendations

### 4. Analyze Trends

1. Enter Student ID (with multiple assessments)
2. Click **"Show Trends"**
3. View:
   - Improving/declining subjects
   - Performance trends
   - Personalized recommendations

### 5. Explore the Dashboard

1. Click on **"Class Dashboard"** tab
2. View:
   - Total students
   - Class average
   - Performance distribution
   - Statistics cards

## 🔍 Key Features to Try

### Search Students
- Go to **Student List** tab
- Select search type (Name, ID, or Grade)
- Enter search query
- Click **Search**

### Generate Reports
- Menu → **Reports** → **Class Performance Report**
- Menu → **Reports** → **Underperforming Students**
- Menu → **Reports** → **Top Performers**

### Export Data
- Menu → **File** → **Export to CSV**
- Choose save location
- Open in Excel or any spreadsheet app

## 💡 Pro Tips

1. **Double-click** a student in the Student List to quickly view their details

2. **Subject names are normalized** - "Math", "math", and "MATH" are treated as the same subject

3. **Data is automatically saved** - No need to manually save, it happens automatically!

4. **Scores must be 0-100** - The system validates all inputs

5. **Multiple assessments** - Add unlimited assessments per subject to track progress over time

## 📊 Understanding Feedback

### Performance Levels
- **Excellent (90-100%)**: Outstanding work!
- **Good (75-89%)**: Performing well
- **Needs Improvement (50-74%)**: Additional study time needed
- **At Risk (<50%)**: Requires immediate attention and support

### Trend Indicators
- **📈 Improving**: Recent scores are higher than earlier ones
- **📉 Declining**: Recent scores are lower than earlier ones
- **➡️ Stable**: Consistent performance
- **❓ Insufficient Data**: Need at least 2 assessments to analyze trends

### Study Time Recommendations
The system automatically calculates recommended study hours per week based on performance:
- **90%+**: 2 hours (maintenance)
- **75-89%**: 3 hours (slight improvement)
- **60-74%**: 5 hours (moderate improvement)
- **50-59%**: 7 hours (significant improvement)
- **<50%**: 10 hours (intensive support)

## 🐛 Troubleshooting

### Application won't start
- Ensure Java 11 or higher is installed: `java -version`
- Check that the JAR file exists in the target directory

### Can't add student
- Make sure Student ID is unique
- Check that all required fields are filled
- Verify scores are between 0 and 100

### Data not saving
- Check file permissions in the application directory
- Look for `student_data.json` file
- Check console for error messages

### GUI looks strange
- Try resizing the window
- Check your display scaling settings
- Restart the application

## 📚 Next Steps

1. **Read the full README**: [README.md](README.md)
2. **Check documentation**: [docs/](docs/)
3. **Explore the code**: [src/](src/)
4. **Run tests**: `mvn test`
5. **Contribute**: See [CONTRIBUTING.md](CONTRIBUTING.md) (if available)

## 🆘 Need Help?

- **GitHub Issues**: [Report a bug or request a feature](https://github.com/ttmodupe-hash/LearnMax-Enhanced/issues)
- **Documentation**: Check the [docs](docs/) folder
- **Source Code**: Browse the [repository](https://github.com/ttmodupe-hash/LearnMax-Enhanced)

## 🎉 You're Ready!

Start tracking student performance with confidence. The system will help you:
- Identify students who need support
- Track progress over time
- Generate actionable insights
- Make data-driven decisions

**Happy tracking!** 📊✨
