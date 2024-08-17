# Use the official Amazon Corretto image as a parent image
FROM amazoncorretto:21

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from your host machine into the container
COPY target/georegistry-0.0.1-SNAPSHOT.jar /app/georegistry.jar

# Command to run the application
CMD ["java", "-jar", "/app/georegistry.jar"]
