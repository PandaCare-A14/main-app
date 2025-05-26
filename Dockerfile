# Stage 1: Build the app using Gradle and JDK 17
FROM gradle:8.4.0-jdk17 AS builder

# Set the working directory inside the container
WORKDIR /home/gradle/project

# Copy everything to the container
COPY --chown=gradle:gradle . .

# Run Gradle build (skip tests to speed up build)
RUN gradle build -x test

# Stage 2: Run the app using a smaller base image
FROM openjdk:17-jdk-slim

# Set working directory for the app
WORKDIR /app

# Copy built JAR from the builder stage
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar

# Expose the default port
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
