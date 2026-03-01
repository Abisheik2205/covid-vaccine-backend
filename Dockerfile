# =================================================================
# DOCKERFILE
# A Dockerfile is a recipe for building a "container" —
# a self-contained box that has everything needed to run your app.
#
# ANALOGY:
# Think of a container like a shipping container.
# Inside it is: Java, your compiled code, all dependencies.
# It runs identically on your PC, Render's servers, anywhere.
#
# TWO-STAGE BUILD EXPLAINED:
# Stage 1 (build): Use Maven + JDK to compile your Java code
# Stage 2 (run):   Use a smaller JRE image just to run the jar
# Result: smaller final image = faster startup on Render
# =================================================================

# =================================================================
# STAGE 1 — BUILD
# Maven image already has Java 17 + Maven installed
# =================================================================
FROM maven:3.9.4-eclipse-temurin-17 AS build

# Set the working directory inside the container
# All subsequent commands run from this directory
WORKDIR /app

# Copy pom.xml FIRST, then download dependencies
# WHY SEPARATELY? Docker caches each step.
# If only your .java files change (not pom.xml),
# Docker reuses the cached dependency download — much faster!
COPY pom.xml .
RUN mvn dependency:go-offline -B
# -B = batch mode (no interactive prompts)

# Now copy your source code
COPY src ./src

# Build the project into a fat JAR, skipping tests for speed
RUN mvn clean package -DskipTests
# Output: target/covid-vaccine-analysis-0.0.1-SNAPSHOT.jar


# =================================================================
# STAGE 2 — RUN
# Use a smaller image (JRE = Java Runtime, no compiler needed)
# eclipse-temurin:17-jre-alpine is ~200MB vs ~600MB for the JDK
# =================================================================
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Copy ONLY the compiled jar from Stage 1
# We don't need Maven, source code, or JDK anymore
COPY --from=build /app/target/*.jar app.jar

# Tell Docker this container listens on port 8080
# (Render reads this and routes traffic to it)
EXPOSE 8080

# The command that runs when the container starts
# java -jar app.jar = run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
