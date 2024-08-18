# Stage 1: Build the application
FROM maven:3.9.8-eclipse-temurin-21-alpine AS build
WORKDIR /app
# Copy only pom.xml initially to leverage Docker cache
COPY ../pom.xml /app
# Install dependencies
RUN mvn dependency:go-offline
# Copy the rest of your project
COPY ../src /app/src
# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
# Copy only the built JAR from the build stage
COPY --from=build /app/target/georegistry-0.0.1-SNAPSHOT.jar /app/georegistry.jar
CMD ["java", "-jar", "/app/georegistry.jar"]