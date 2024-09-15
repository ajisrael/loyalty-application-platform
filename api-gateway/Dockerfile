# Base image
FROM openjdk:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the jar file
COPY target/ApiGateway-0.0.1-SNAPSHOT.jar api-gateway.jar

# Expose the port API Gateway runs on
EXPOSE 8082

# Command to run the application

ENTRYPOINT ["java", "-jar", "api-gateway.jar", "--spring.profiles.active=docker"]
