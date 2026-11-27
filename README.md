# Authory Service

[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.org/projects/jdk/25/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Architecture](https://img.shields.io/badge/Architecture-Hexagonal%20%7C%20Clean%20%7C%20DDD-blue.svg)](#architecture-overview)
[![License](https://img.shields.io/badge/License-Apache%202.0-lightgrey.svg)](LICENSE)

**Authory Service** is a centralized, high-performance Identity and Access Management (IAM), Multi-Factor Authentication (MFA/2FA), Dynamic Attribute-Based Access Control (ABAC PDP), Role-Based Access Control (RBAC), and Organizational Structure authority microservice.

Built with **Spring Boot 4.1.2** and **Java 25**, it adheres strictly to **Hexagonal Architecture (Ports and Adapters)** and **Domain-Driven Design (DDD)**. All business domains are completely insulated from transport protocols, database engines, and external frameworks.

---

## 📚 Documentation Hub

Detailed engineering documentation is maintained in the [`docs/`](docs/) directory:

| Document | Description |
| :--- | :--- |
| 🏛️ **[System Architecture](docs/ARCHITECTURE.md)** | Full Hexagonal & DDD architecture guide, layer boundaries, Mermaid diagrams, and workflows |
| 📐 **[AI Architecture Rules](docs/ai_architecture_rules.md)** | Mandatory directory layouts, layer rules, naming conventions, and coding workflows |
| 🔌 **[API Guidelines & Integration](docs/API_GUIDELINES.md)** | REST contract standards, CQRS payloads, endpoints catalog, and cURL examples |
| 🏢 **[Enterprise Design](docs/ENTERPRISE_DESIGN.md)** | Hybrid RBAC + ABAC security model, multi-tenancy, partner integration, and performance design |
| 🛠️ **[Developer Guidelines](docs/DEVELOPMENT_GUIDELINES.md)** | Environment setup, Gradle commands, Spotless Google Java Format, and testing standards |
| 🚀 **[Operations Guide](docs/OPERATIONS_GUIDE.md)** | Docker containerization, Flyway migrations, Actuator probes, HikariCP, and runbooks |

---

## 🌟 Key Capabilities

- **Pure Domain Core:** Business rules, domain models, and aggregates are 100% free of Spring, JPA, and database annotations.
- **Hexagonal Architecture:** Communication between layers is strictly mediated through Inbound Ports (Use Cases) and Outbound Ports (Repositories).
- **Strict CQRS Segregation:** Granular `*CreateUseCase`, `*GetUseCase`, `*GetPageUseCase`, `*UpdateUseCase`, `*DeleteUseCase` with dedicated Commands, Results, Requests, and Responses.
- **Native MFA / 2FA Engine (RFC 6238 TOTP):** Native TOTP implementation (Google / Microsoft Authenticator) with QR code enrollment and two-step verification using temporary scoped tokens.
- **Brute-Force Defense & Account Lockout:** Automatically locks credentials for 15 minutes after 5 consecutive failed login attempts, with an admin unlock API.
- **Active Device Session Management:** Tracks client IP, User-Agent, and device names across refresh tokens with APIs to view and terminate sessions remotely.
- **Dynamic ABAC Policy Decision Point (PDP):** Policy engine evaluating subject attributes, resource attributes, environmental context, and dynamic SpEL expressions.
- **Optimistic Locking & Concurrency Defense:** Centralized `@Version` tracking across all entities and 27 database tables. Automatic translation of concurrent mutation conflicts to `HTTP 409 Conflict`.
- **Automated Cache Invalidation:** Multi-level caching with `@Cacheable` and automated `@CacheEvict` when user roles, permissions, or groups mutate.
- **Enterprise Database Migrations (Flyway):** Automated version-controlled DDL migrations across 5 schemas (`auth`, `partner`, `organization`, `rbac`, `abac`).
- **Distributed Tracing & Rate Limiting:** Platform `tracing-core` (`TracingFilter`, `X-Correlation-Id`, W3C `traceparent` MDC logging) and `ratelimit-redis` sliding-window rate limiting on all sensitive authentication routes.
- **High Concurrency with Java 25:** Native support for Virtual Threads (`spring.threads.virtual.enabled: true`) enabling high-throughput I/O.
- **Enterprise Observability:** Spring Boot Actuator with Prometheus metrics (`/actuator/prometheus`) and Kubernetes readiness/liveness health probes.

---

## 🏛️ Architecture Overview

Each business capability resides within its own bounded context inside `modules/<feature>/`:

```text
src/main/java/com/linhtn/authory_service/modules/
├── user/               # User lifecycle, accounts, profile, and account unlock
├── auth/               # JWT authentication, MFA/2FA, password reset, active device sessions
├── partner/            # B2B tenant management and partner isolation
├── rbac/               # Roles, Permissions, Resources, Groups, and junction graphs
├── abac/               # Dynamic ABAC Policy Decision Point (PDP) and user attributes
├── organization/       # Company, Center, Branch, Division, Department, Position
├── activity/           # System activity tracking via asynchronous domain event listeners
└── dashboard/          # Aggregated system metrics and overview
```

---

## 💻 Tech Stack

- **Runtime & Language:** Java 25 (OpenJDK / Temurin)
- **Framework:** Spring Boot 4.1.2
- **Database Migrations:** Flyway 11 / 12
- **Data Access:** Spring Data JPA with Hibernate, PostgreSQL
- **Security:** Spring Security, JJWT (JSON Web Token), BCrypt, Native TOTP (RFC 6238)
- **Caching:** Spring Cache with automated `@CacheEvict`
- **Observability:** Spring Boot Actuator, Micrometer Prometheus
- **Concurrency:** Java 25 Virtual Threads (`@Async`)
- **Code Quality & Formatting:** Spotless Plugin (Google Java Format)
- **API Documentation:** Springdoc OpenAPI 3.0 / Swagger UI

---

## 🚀 Quick Start

### 1. Prerequisites
- **JDK 25** installed (`java -version`)
- **Docker** and **Docker Compose** (for running PostgreSQL)

### 2. Start PostgreSQL
Run PostgreSQL with the required schemas:

```bash
docker compose up -d postgres
```

### 3. Build & Run Application
Start the microservice using the Gradle wrapper:

```bash
./gradlew bootRun --no-daemon
```

The application will start on **`http://localhost:8600`**.

### 4. Interactive API Documentation
Open your browser and navigate to:
- **Swagger UI:** [http://localhost:8600/swagger-ui.html](http://localhost:8600/swagger-ui.html)
- **OpenAPI JSON:** [http://localhost:8600/v3/api-docs](http://localhost:8600/v3/api-docs)

### 5. Production Observability Endpoints
- **Health Check:** [http://localhost:8600/actuator/health](http://localhost:8600/actuator/health)
- **Kubernetes Liveness:** [http://localhost:8600/actuator/health/liveness](http://localhost:8600/actuator/health/liveness)
- **Kubernetes Readiness:** [http://localhost:8600/actuator/health/readiness](http://localhost:8600/actuator/health/readiness)
- **Prometheus Metrics:** [http://localhost:8600/actuator/prometheus](http://localhost:8600/actuator/prometheus)

---

## 🧪 Testing & Code Formatting

### Execute Unit Tests
```bash
./gradlew test --no-daemon
```

### Code Formatting with Spotless
```bash
# Automatically format code
./gradlew spotlessApply --no-daemon

# Verify formatting without changes (CI quality check)
./gradlew spotlessCheck --no-daemon
```

---

## 📄 License
This project is licensed under the Apache 2.0 License.
