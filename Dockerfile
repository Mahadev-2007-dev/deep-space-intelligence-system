# ============================================================
# DSIS - Deep Space Intelligence System
# Multi-stage Docker build
# Stage 1: Build the Spring Boot JAR (with embedded React frontend)
# Stage 2: Minimal runtime image
# ============================================================

# --- Stage 1: Build ---
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml and download dependencies first (layer caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build JAR (skip tests for faster build)
RUN mvn clean package -DskipTests -B

# --- Stage 2: Runtime ---
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Create data directory for SQLite persistence
RUN mkdir -p /data && chmod 777 /data

# Copy the built JAR from the build stage
COPY --from=build /app/target/dsis-backend-1.0.0.jar app.jar

# Expose the application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/v1/health || exit 1

# Run the application
# SQLITE_DB_PATH and PORT are set by the deployment platform
ENV SQLITE_DB_PATH=/data/dsis.db
ENV PORT=8080

ENTRYPOINT ["java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-Xmx512m", \
  "-Xms256m", \
  "-jar", \
  "app.jar"]
