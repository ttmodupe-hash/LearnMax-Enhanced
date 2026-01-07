FROM openjdk:11-jre-slim

WORKDIR /app

COPY target/student-performance-tracker-2.0.0-ENHANCED-jar-with-dependencies.jar /app/learnmax.jar

CMD ["java", "-jar", "learnmax.jar"]
