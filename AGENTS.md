# Authory Service API

**Enterprise Identity, Authentication & Access Governance | Hexagonal Architecture & DDD**

A high-performance IAM and fine-grained authorization microservice built with **Spring Boot 4.x** and **Java 25**, integrated with enterprise shared components from **`be-platform`**. Engineered for high throughput, stateless JWT issuance and rotation, RFC 6238 TOTP Multi-Factor Authentication (MFA), dynamic ABAC Policy Decision Point (PDP) evaluation, multi-tenant B2B partner governance, and Project Loom virtual thread concurrency.

---

## 🚀 Fast Track Links

- **Interactive API (Swagger UI):** `http://localhost:8600/swagger-ui.html`
- **OpenAPI Schema (JSON):** `http://localhost:8600/v3/api-docs`
- **Application Server Port:** `8600`
- **Distributed Tracing (Jaeger):** `http://localhost:16686`
- **Metrics & Dashboards (Grafana):** `http://localhost:3000`

---

## 🏗️ Core Capabilities

- **Enterprise Hexagonal Architecture & DDD:** Strict isolation across Domain, Application, Infrastructure, and Representation layers.
- **Authentication & Token Lifecycle:** Stateless JWT authentication, refresh token rotation, active device session tracking, and brute-force account lockout protection.
- **RFC 6238 Multi-Factor Authentication (MFA):** Native TOTP secret provisioning, QR code generation, and multi-window time-drift verification.
- **Fine-Grained Role-Based Access Control (RBAC):** Users, Groups, Roles, Resources, and Permissions with many-to-many associations and authority cache eviction.
- **Attribute-Based Access Control (ABAC):** Dynamic Policy Decision Point (PDP) engine evaluating subject, resource, action, and contextual environment attributes.
- **B2B Multi-Tenant Partner Governance:** Dedicated partner tenant isolation, hashed API keys (`Client-Id`, `Client-Key`), and isolated partner authority scopes.
- **Organizational Structure:** Enterprise multi-level hierarchy (Company, Branch, Center, Division, Department, Position).
- **Extreme Concurrency:** Fully tuned for **Project Loom (Virtual Threads)** on Java 25 with lock-free token bucket rate limiting.
- **Enterprise Platform Foundation:** Standardized on `be-platform` shared modules for uniform pagination (`PagingRequest`, `PagingResponse`), error responses (`ApiErrorResponse`), i18n (`MessageResolver`), auditable entities, and auto-configured OpenAPI 3 contracts.

---

## 📁 Comprehensive Documentation Map

| Document | Target Audience | Primary Focus |
| :--- | :--- | :--- |
| [**API_GUIDELINES.md**](./docs/API_GUIDELINES.md) | Frontend & API Consumers | REST endpoints, request/response formats, pagination, and error codes. |
| [**DEVELOPMENT_GUIDELINES.md**](./docs/DEVELOPMENT_GUIDELINES.md) | Backend Engineers | Feature workflow (Domain → App → Infra → Repr), Loom rules, code style. |
| [**ARCHITECTURE.md**](./docs/ARCHITECTURE.md) | Architects & Developers | Hexagonal boundaries, Ports & Adapters interaction diagrams, domain models. |
| [**ENTERPRISE_DESIGN.md**](./docs/ENTERPRISE_DESIGN.md) | Tech Leads & Architects | System design, ABAC evaluation engine, security filter chain, token lifecycle. |
| [**OPERATIONS_GUIDE.md**](./docs/OPERATIONS_GUIDE.md) | SREs & DevOps | Monitoring metrics, Jaeger distributed tracing, Flyway database migrations, runbooks. |

---

## 🛠️ Tech Stack & Platform Libraries

- **Runtime:** Java 25 (Project Loom Virtual Threads enabled: `spring.threads.virtual.enabled: true`)
- **Framework:** Spring Boot 4.1.2
- **Security & Crypto:** Spring Security 7.x, JJWT 0.12.6, BCrypt, TOTP (RFC 6238)
- **Enterprise Platform (`be-platform`):**
  - `shared-kernel`: Unified domain/application exceptions, pagination models (`PagingRequest`, `PagingResponse`), and API error responses (`ApiErrorResponse`).
  - `web-core`: Global i18n message resolution (`MessageResolver`), accept-header locale negotiation, trace/IP utilities.
  - `data-core` & `data-jpa`: Base auditable JPA entity abstractions (`BaseUuidEntity`, `BaseEntity`), `GenericSpecification`.
  - `cache-core` & `cache-redis`: Distributed cache abstractions and Redisson coordination.
  - `openapi-core`: Auto-configured OpenAPI 3 / Swagger documentation and error response schemas.
  - `idempotency-core`: Mutation replay protection via `Idempotency-Key` header.
- **Database:** PostgreSQL 16 (Schema migrations via Flyway, schemas: `public`, `auth`, `partner`, `organization`, `rbac`, `abac`)
- **Code Standards:** Spotless (Google Java Format AOSP)

---

## ⚡ Quick Start

### 1. Launch Infrastructure via Docker Compose
```bash
docker-compose up -d
```
Spins up PostgreSQL, Redis, Jaeger, and Grafana.

### 2. Verify Code Formatting
```bash
./gradlew spotlessCheck
```
Or auto-format:
```bash
./gradlew spotlessApply
```

### 3. Run the Microservice
```bash
./gradlew bootRun
```
The service will start on HTTP port **`8600`**.

---

## 🧪 Quick API Verification

### 1. User Authentication (Login)
```bash
curl -X POST http://localhost:8600/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "Password123!"
  }'
```

### 2. Query Paginated Users
```bash
curl -X POST http://localhost:8600/api/v1/users/get-page \
  -H "Authorization: Bearer <access_token>" \
  -H "Content-Type: application/json" \
  -d '{ "page": 1, "pageSize": 10 }'
```

### 3. Evaluate ABAC Policy
```bash
curl -X POST http://localhost:8600/api/v1/abac/evaluate \
  -H "Authorization: Bearer <access_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "subject": "usr-123",
    "resource": "document-456",
    "action": "READ",
    "environment": { "ip": "10.0.0.1", "time": "14:30" }
  }'
```

### 4. Register B2B Partner
```bash
curl -X POST http://localhost:8600/api/v1/partners \
  -H "Authorization: Bearer <access_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "partner_fintech",
    "name": "Fintech Solutions Ltd",
    "email": "contact@fintech.example.com",
    "phone": "+1234567890",
    "address": "123 Financial District"
  }'
```
