# syntax=docker/dockerfile:1.4

# ==============================================================================
# Stage 1: Build the application with Gradle & Eclipse Temurin JDK 25
# ==============================================================================
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app

# Optional build arguments for Nexus maven repository access during docker build
ARG NEXUS_URL=http://host.docker.internal:8081/repository/maven-public/
ARG NEXUS_USERNAME=admin
ARG NEXUS_PASSWORD=admin123
ENV NEXUS_URL=${NEXUS_URL} \
    NEXUS_USERNAME=${NEXUS_USERNAME} \
    NEXUS_PASSWORD=${NEXUS_PASSWORD}

# Copy gradle wrapper and build metadata first to leverage Docker layer caching
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Make wrapper script executable
RUN chmod +x gradlew

# Download dependencies (cached unless build/gradle files change)
RUN ./gradlew dependencies --no-daemon

# Copy application source code
COPY src src

# Build executable Spring Boot fat JAR, skipping tests (tests verified during CI)
RUN ./gradlew bootJar -x test --no-daemon

# Extract Spring Boot layers for optimized Docker layer caching and faster startup
RUN java -Djarmode=tools -jar build/libs/*.jar extract --layers --destination build/extracted

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

# Copy extracted application layers from builder stage
COPY --from=builder --chown=spring:spring /app/build/extracted/dependencies/ ./
COPY --from=builder --chown=spring:spring /app/build/extracted/spring-boot-loader/ ./
COPY --from=builder --chown=spring:spring /app/build/extracted/snapshot-dependencies/ ./
COPY --from=builder --chown=spring:spring /app/build/extracted/application/ ./

# Expose microservice HTTP port
EXPOSE 8600

# Container JVM performance flags tailored for Java 25 & Project Loom Virtual Threads
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseZGC -Djava.security.egd=file:/dev/./urandom"

# Health check probing Spring Boot Actuator health endpoint
HEALTHCHECK --interval=15s --timeout=5s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8600/actuator/health || exit 1

# Launch Spring Boot application
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/*.jar"]
