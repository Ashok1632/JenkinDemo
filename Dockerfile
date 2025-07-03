# Build stage
FROM maven:3.8.6-openjdk-17-slim AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build
COPY src/ /app/src/
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar SpringDemo.jar

# Set the timezone (optional)
ENV TZ=Asia/Kolkata

# Expose the application port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "SpringDemo.jar"]
