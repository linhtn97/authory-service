# syntax=docker/dockerfile:1.4

# ==============================================================================
# Stage 1: Build stage with Eclipse Temurin JDK 25
# ==============================================================================
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /workspace

# Build arguments for Nexus repository
ARG NEXUS_URL=http://host.docker.internal:8081/repository/maven-public/
ARG NEXUS_USERNAME=admin
ARG NEXUS_PASSWORD=admin123
ENV NEXUS_URL=${NEXUS_URL}
ENV NEXUS_USERNAME=${NEXUS_USERNAME}
ENV NEXUS_PASSWORD=${NEXUS_PASSWORD}

# Install dos2unix to ensure gradlew line endings are LF inside Linux container
RUN apt-get update && apt-get install -y --no-install-recommends dos2unix \
    && rm -rf /var/lib/apt/lists/*

# Copy Gradle wrapper and configuration files first to optimize Docker layer cache
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle settings.gradle gradle.properties* ./

# Ensure gradlew has executable permissions and unix line endings
RUN dos2unix gradlew && chmod +x gradlew

# Download Gradle dependencies layer (cached unless build.gradle/settings.gradle change)
RUN ./gradlew dependencies --no-daemon || true

# Copy project source code
COPY src/ src/

# Build Spring Boot executable fat JAR (skip tests for fast reproducible container builds)
RUN ./gradlew bootJar -x test --no-daemon

# ==============================================================================
# Stage 2: Runtime Image with Eclipse Temurin JRE 25 (Minimal footprint)
# ==============================================================================
FROM eclipse-temurin:25-jre
WORKDIR /app

# Install curl for healthcheck probing
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Create a non-root system user and group for security
RUN groupadd -r spring && useradd -r -g spring spring \
    && mkdir -p /app/logs && chown -R spring:spring /app

USER spring:spring

# Copy built executable fat JAR from builder stage
COPY --from=builder --chown=spring:spring /workspace/build/libs/authory-service-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose microservice HTTP port
EXPOSE 8600

# Container JVM performance flags tailored for Java 25 & Project Loom Virtual Threads
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseZGC -Djava.security.egd=file:/dev/./urandom"

# Health check probing Spring Boot Actuator health endpoint
HEALTHCHECK --interval=15s --timeout=5s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8600/actuator/health || exit 1

# Launch Spring Boot application
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
