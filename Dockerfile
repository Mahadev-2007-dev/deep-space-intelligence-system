# ============================================================
# DSIS - Deep Space Intelligence System
# Production Docker Build for Render.com
# Architecture: Single container serves React SPA + Spring Boot API
# ============================================================

# --- Stage 1: Build Spring Boot JAR ---
FROM maven:3.9.6-eclipse-temurin-17-focal AS build

WORKDIR /app

# Cache Maven dependencies (layer cache optimization)
COPY pom.xml .
RUN mvn dependency:go-offline -B -q

# Copy source (includes React dist already embedded in src/main/resources/static/)
COPY src ./src

# Build production JAR
RUN mvn clean package -DskipTests -B -q

# --- Stage 2: Minimal JRE Runtime ---
FROM eclipse-temurin:17-jre-focal

WORKDIR /app

# Install curl for health checks
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*

# Create persistent data directory for SQLite
RUN mkdir -p /data && chmod 777 /data

# Copy the fat JAR (includes OpenCV natives + React frontend)
COPY --from=build /app/target/dsis-backend-1.0.0.jar app.jar

# Expose port (Render sets $PORT env var)
EXPOSE 8080

# Health check for Render
HEALTHCHECK --interval=30s --timeout=15s --start-period=90s --retries=3 \
  CMD curl -f http://localhost:${PORT:-8080}/api/v1/health || exit 1

# Production JVM flags: fast startup, bounded memory for free tier (512MB RAM)
ENTRYPOINT ["java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=70.0", \
  "-XX:+UseG1GC", \
  "-jar", \
  "app.jar"]
