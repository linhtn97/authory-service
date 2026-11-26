---
name: feature-development
description: End-to-end workflow for designing, implementing, and verifying new features in compliance with Hexagonal Architecture, DDD, and Spring Boot 3 standards.
---

# Feature Development Skill

Use this skill when developing a new capability, use case, or endpoint in any Spring Boot microservice. Follow this structured workflow to guarantee architectural purity, security, high concurrency, and complete test coverage.

---

## Step 1: Requirements & Architecture Alignment

1. **Identify the Core Aggregate Root:** Determine whether the feature operates on an existing domain aggregate (e.g. `Order`, `User`, `FileMetadata`) or introduces a new domain model.
2. **Determine Persistence & I/O Needs:** Will the feature interact with relational databases (PostgreSQL/MySQL), distributed caches/locks (Redis), messaging systems (Kafka/RabbitMQ), or external cloud services?
3. **Analyze Concurrency Hazards:**
   - Does this operation mutate state under high concurrency? &rarr; Apply distributed locks (`@DistributedLock`) and optimistic locking (`@Version`).
   - Is this an HTTP mutating endpoint (POST, PUT, PATCH)? &rarr; Require idempotency keys (`Idempotency-Key` header).
   - Does this need background scheduling across multi-instances? &rarr; Protect via ShedLock.

---

## Step 2: Domain Layer Implementation

1. **Domain Models & Value Objects:**
   - Location: `src/main/java/<base-package>/domain/model/` and `valueobject/`.
   - Ensure zero framework dependencies (pure Java).
   - Enforce business invariants inside model methods or value object constructors/records.
2. **Domain Errors & Events:**
   - Update `domain/error/` with domain error codes or declare new domain events in `domain/event/`.
3. **Repository Port:**
   - If new persistence queries are required, declare them on the domain repository interface in `domain/repository/`.

---

## Step 3: Application Layer Implementation

1. **DTOs:**
   - Input Command: `application/dto/command/*Command.java`.
   - Output Result: `application/dto/result/*Result.java`.
2. **Inbound Port (Use Case Interface):**
   - Create interface in `application/port/in/*UseCase.java` (e.g. `CreateOrderUseCase`).
3. **Outbound Port (Technical Contract):**
   - If external technical I/O is needed, declare the interface in `application/port/out/*Port.java`.
4. **Use Case Implementation Service:**
   - Location: `application/usecase/*Service.java`.
   - Annotate with `@Service`.
   - Annotate with `@Transactional(readOnly = true)` for queries or `@Transactional(isolation = Isolation.READ_COMMITTED)` for mutations.
   - Apply distributed locking if concurrent race hazards exist.
   - Utilize cache-aside (`getOrLoad`) or cache eviction where appropriate.
5. **Application Mapper:**
   - Add mapping methods between domain models and application DTOs in `application/mapper/`.

---

## Step 4: Infrastructure Layer Implementation

1. **Persistence Adapter:**
   - Implement repository interface methods in `infrastructure/persistence/*RepositoryAdapter.java`.
   - Add required queries to `infrastructure/persistence/jpa/repository/*JpaRepository.java`.
   - Map entities to/from domain models using entity mappers.
2. **External Adapters:**
   - Implement external integration clients in `infrastructure/adapter/` or message publishers in `infrastructure/messaging/`.
   - Sanitize all external inputs and normalize paths/URLs.

---

## Step 5: Representation Layer Implementation

1. **Request & Response DTOs:**
   - Create `representation/rest/dto/request/*Request.java` with Jakarta validation annotations (`@NotBlank`, `@NotNull`, `@Size`).
   - Create `representation/rest/dto/response/*Response.java`.
2. **REST Controller or Event Consumer:**
   - Add endpoint method in `representation/rest/controller/*Controller.java`.
   - Annotate with OpenAPI docs (`@Operation`, `@ApiResponses`).
   - For mutating actions, ensure idempotency support.
3. **REST Mapper:**
   - Map between HTTP Request/Response DTOs and Application Command/Result objects.

---

## Step 6: Testing & Quality Verification

1. **Unit Tests for Use Case:**
   - Create `src/test/java/.../application/usecase/*ServiceTest.java`.
   - Mock outbound ports with Mockito (`@ExtendWith(MockitoExtension.class)`).
   - Test both success scenarios and error paths.
2. **Controller MockMvc Tests:**
   - Add endpoint tests in `src/test/java/.../representation/rest/controller/*ControllerTest.java`.
   - Validate HTTP status codes, headers, and JSON responses.
3. **Automated Verification:**
   Execute the verification commands:
   ```bash
   # Gradle:
   ./gradlew spotlessApply && ./gradlew test && ./gradlew check

   # Maven:
   ./mvnw spotless:apply && ./mvnw test && ./mvnw verify
   ```
