# Use OpenJDK 21 as the base image
FROM openjdk:21-jdk-slim as build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper and pom.xml file
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline

# Copy the source code
COPY src src

# Build the application
RUN ./mvnw package -DskipTests

# Use OpenJDK 21 for running the application
FROM openjdk:21-jdk-slim

# Copy the built artifact from the build stage
COPY --from=build /app/target/*.jar app.jar

# Command to run the application
EXPOSE 8000
ENTRYPOINT ["java","-jar","/app.jar"]
