# ============================================================
# Dockerfile — Build and run Spring Boot on Google Cloud Run
# Two-stage build: 1) Build JAR  2) Run JAR
# ============================================================

# Stage 1 — Build the JAR using Maven
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy pom.xml first (caches dependencies if code hasn't changed)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2 — Run the JAR (smaller image)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy built JAR from Stage 1
COPY --from=builder /app/target/*.jar app.jar

# Cloud Run uses port 8080
EXPOSE 8080

# Start the app
ENTRYPOINT ["java", "-jar", "app.jar"]
