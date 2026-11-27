# Authory Service: Operations & Deployment Guide

## 1. Overview

This guide covers deployment, containerization, environment configuration, database management, observability, and operations for **Authory Service**.

---

## 2. Containerization (Docker)

### 2.1 Multi-Stage Dockerfile

The service includes an optimized multi-stage `Dockerfile` built on **Eclipse Temurin JDK 25 Alpine**:

```dockerfile
# Stage 1: Build & Package
FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /app
COPY gradle/ gradle/
COPY gradlew build.gradle settings.gradle ./
RUN ./gradlew dependencies --no-daemon || true
COPY src/ src/
RUN ./gradlew bootJar -x test --no-daemon

# Stage 2: Minimal Runtime
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser
COPY --from=builder /app/build/libs/authory-service-*.jar app.jar

EXPOSE 8600
ENV JAVA_OPTS="-XX:+UseZGC -XX:+ZGenerational -XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### 2.2 Docker Compose (`docker-compose.yml`)

To run the complete production-grade microservice infrastructure stack locally (PostgreSQL 16, Redis 7.4, Jaeger Tracing, and Authory Service):

```bash
docker-compose up -d
```

Services exposed:
- **Authory Service API:** `http://localhost:8600` (Swagger UI: `http://localhost:8600/swagger-ui.html`)
- **PostgreSQL 16:** `localhost:5432` (`authory`)
- **Redis 7.4:** `localhost:6379`
- **Jaeger Distributed Tracing UI:** `http://localhost:16686`


---

## 3. Configuration Management & Environment Variables

All settings in `src/main/resources/application.yaml` strictly adhere to the 12-factor app methodology:

| Environment Variable | Default Value | Description |
| :--- | :--- | :--- |
| `SERVER_PORT` | `8600` | HTTP port for REST APIs |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/authory` | JDBC connection URL |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | `postgres` | Database password |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `validate` | DDL generation strategy (`validate` in production) |
| `SPRING_THREADS_VIRTUAL_ENABLED` | `true` | Enables Java 25 Virtual Threads |
| `JWT_SECRET_KEY` | *(Externalized)* | HMAC-SHA256 Base64 secret key for signing access tokens |
| `JWT_ACCESS_EXPIRATION` | `3600000` | Access token lifespan in milliseconds (1 hour) |
| `JWT_REFRESH_EXPIRATION` | `604800000` | Refresh token lifespan in milliseconds (7 days) |

---

## 4. Database Schema & Flyway Migrations

The database lifecycle is fully governed by **Flyway Database Migrations**:

- **Location:** `classpath:db/migration`
- **Managed Schemas:** `public, auth, partner, organization, rbac, abac`
- **Migration Scripts:**
  - `V1__init_enterprise_schemas.sql`: 5 schemas, 25 tables, primary keys, foreign keys, unique constraints, and indexes.
  - `V2__seed_default_roles_and_admin.sql`: Default system roles (`SUPER_ADMIN`, `ADMIN`, `USER`), default system resources, and full permission grants.
  - `V3__enterprise_iam_features.sql`: Adds account lockout fields, MFA secrets, active device session metadata, and password reset token tracking.
  - `V4__add_optimistic_locking_version.sql`: Adds version column for JPA Optimistic Locking across all 27 tables.

### Connection Pool Configuration (HikariCP)
- **Pool Name:** `AuthoryHikariPool`
- **Maximum Pool Size:** `20`
- **Minimum Idle:** `5`
- **Connection Timeout:** `30000ms` (30s)
- **Leak Detection Threshold:** `5000ms` (Warns if a query holds a connection > 5 seconds)

---

## 5. Health Checks, Probes & Observability

### 5.1 Spring Boot Actuator Endpoints

- **Unified Health:** `GET http://localhost:8600/actuator/health`
- **Kubernetes Liveness Probe:** `GET http://localhost:8600/actuator/health/liveness`
- **Kubernetes Readiness Probe:** `GET http://localhost:8600/actuator/health/readiness`
- **Prometheus Metrics Scrape Target:** `GET http://localhost:8600/actuator/prometheus`
- **Application Info:** `GET http://localhost:8600/actuator/info`

### 5.2 Kubernetes Deployment Probes Configuration

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8600
  initialDelaySeconds: 15
  periodSeconds: 10
  timeoutSeconds: 3
  failureThreshold: 3

readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8600
  initialDelaySeconds: 20
  periodSeconds: 5
  timeoutSeconds: 3
  failureThreshold: 2
```

### 5.3 Graceful Shutdown
- **Mode:** `server.shutdown: graceful`
- **Phase Timeout:** `spring.lifecycle.timeout-per-shutdown-phase: 30s`
- When a `SIGTERM` signal is received, the HTTP listener stops accepting new connections and allows in-flight requests up to 30 seconds to finish processing before terminating.

### 5.4 Correlation ID & Distributed Tracing
- All incoming HTTP requests are intercepted by `TracingFilter` from `be-platform:tracing-core`.
- If an incoming request provides `X-Correlation-Id`, `X-Trace-ID`, or W3C `traceparent`, it is captured and bound to the thread via SLF4J MDC (`traceId`, `correlationId`).
- If no correlation header is provided, a new UUID is generated and returned in the HTTP response headers.

---

## 6. Rate Limiting & Account Security Policies

- Sensitive endpoints (`/api/v1/auth/login`, `/api/v1/auth/register`, `/api/v1/auth/mfa/verify`, `/api/v1/auth/forgot-password`, `/api/v1/auth/reset-password`) are protected by `RateLimitFilter` from `be-platform:ratelimit-redis`.
- Distributed sliding-window algorithm via Redis: **10 requests per minute** for login/register/mfa and **5 requests per minute** for password resets. Exceeding returns `HTTP 429 Too Many Requests` with standard rate limit response headers.
- Brute-force account lockout: 5 consecutive failed login attempts locks the credential for 15 minutes. Can be unlocked via `POST /api/v1/users/{id}/unlock`.
