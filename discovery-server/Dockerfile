# Base image
FROM openjdk:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the jar file
COPY target/DiscoveryServer-0.0.1-SNAPSHOT.jar discovery-server.jar

# Expose the port Eureka Server runs on
EXPOSE 8761

# Command to run the application
ENTRYPOINT ["java", "-jar", "discovery-server.jar", "--spring.profiles.active=docker"]
