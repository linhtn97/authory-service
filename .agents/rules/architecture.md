# Architectural Standards & Governance

## 1. Core Architectural Paradigm

All services follow **Hexagonal Architecture (Ports and Adapters)** combined with **Domain-Driven Design (DDD)**. The primary objective is to isolate the pure business domain from frameworks, persistence technologies, third-party APIs, and transport protocols.

```
       [ Inbound Adapters ]              [ Pure Core ]              [ Outbound Adapters ]
    ┌─────────────────────────┐     ┌──────────────────────┐     ┌────────────────────────┐
    │ REST Controllers        │ ──> │ Inbound Ports (UC)   │     │ JPA Repository Adapter │ ──> Relational DB
    │ (representation/rest)   │     │ (application/port/in)│     │ (infrastructure/       │     (PostgreSQL/MySQL)
    ├─────────────────────────┤     └──────────┬───────────┘     │  persistence/jpa)      │
    │ Message Consumers       │                │                 └────────────────────────┘
    │ (representation/        │                ▼
    │  consumer)              │     ┌──────────────────────┐     ┌────────────────────────┐
    └─────────────────────────┘     │ Domain Core          │     │ External Service/Cloud │ ──> 3rd Party APIs /
                                    │ (domain/model, vo)   │     │ (infrastructure/       │     Storage (S3, etc.)
                                    └──────────┬───────────┘     │  adapter)              │
                                               │                 └────────────────────────┘
                                               ▼
                                    ┌──────────────────────┐     ┌────────────────────────┐
                                    │ Outbound Ports       │ <── │ Event Publisher Adapter│ ──> Kafka / RabbitMQ
                                    │ (application/port/out│     │ (infrastructure/       │
                                    │  domain/repository)  │     │  messaging)            │
                                    └──────────────────────┘     └────────────────────────┘
```

---

## 2. Layer Isolation Boundaries & Dependency Rules

### 2.1 Domain Layer (`<base-package>.domain`)
- **Purity Principle:** The domain layer must be pure Java with zero framework coupling.
- **Allowed Dependencies:** Standard Java runtime library (`java.*`) and minimal Lombok (`@Getter`, `@Builder`, `@EqualsAndHashCode`).
- **Strict Prohibitions:**
  - **NO** Spring framework annotations (`@Component`, `@Service`, `@Autowired`, `@Value`).
  - **NO** Jakarta Persistence or Hibernate annotations (`@Entity`, `@Table`, `@Column`, `@Id`).
  - **NO** Web/HTTP annotations or classes (`@RestController`, `HttpServletRequest`, `HttpStatus`).
  - **NO** Jackson JSON annotations or serialization logic.
- **Components:**
  - `model/`: Aggregate roots and entities encapsulating business invariants and state transitions.
  - `valueobject/`: Immutable value objects with self-validation in constructors/records.
  - `repository/`: Repository interfaces declaring pure business queries (e.g. `OrderRepository`, `UserRepository`).
  - `error/`: Domain error codes and exception constants.
  - `event/`: Domain event declarations (e.g. `OrderPlacedEvent`).
  - `enumarate/`: Domain lifecycle enumerations.

### 2.2 Application Layer (`<base-package>.application`)
- **Orchestration Principle:** Orchestrates business operations by loading domain models, invoking domain logic, and persisting state through ports.
- **Allowed Dependencies:** Domain layer, minimal Spring annotations (`@Service`, `@Transactional`, `@Scheduled`), shared platform abstractions (`CacheService`, `DistributedLockManager`).
- **Strict Prohibitions:**
  - **NO** HTTP or Web abstractions (`HttpServletRequest`, `ResponseEntity`).
  - **NO** Direct references to JPA entities (`*Entity`) or Spring Data repositories (`*JpaRepository`).
  - **NO** Direct cloud/messaging client libraries. All external interactions must pass through Outbound Ports.
- **Components:**
  - `port/in/`: Inbound port interfaces defining use cases (`*UseCase`).
  - `port/out/`: Outbound port interfaces defining technical capabilities (`*Port`).
  - `usecase/`: Concrete implementations of inbound ports (`*Service`).
  - `dto/command/`: Use case input records/DTOs (`*Command`).
  - `dto/result/`: Use case output records/DTOs (`*Result`).
  - `mapper/`: Pure mappers between domain models and application DTOs.
  - `job/`: Clustered scheduled tasks protected by ShedLock.

### 2.3 Representation Layer (`<base-package>.representation`)
- **Inbound Delivery Principle:** Adapts external communication channels (HTTP, Message Queues, WebSockets) into application commands and formats results into responses.
- **Allowed Dependencies:** Application layer inbound ports (`*UseCase`) and DTOs, Spring WebMVC, SpringDoc OpenAPI, messaging listener annotations.
- **Strict Prohibitions:**
  - **NO** direct calls to domain repository interfaces or infrastructure adapters.
  - **NO** direct manipulation of domain entities or JPA entities.
  - **NO** business logic in controllers or consumers. Controllers only validate input, delegate to use cases, and format output.
- **Components:**
  - `rest/controller/`: Spring REST controllers (`*Controller`).
  - `rest/dto/request/`: HTTP request payloads with Jakarta validation (`@Valid`, `@NotBlank`).
  - `rest/dto/response/`: Standardized JSON responses (`*Response`).
  - `rest/mapper/`: Mapping between HTTP DTOs and Application Commands/Results.
  - `rest/handler/`: Centralized HTTP exception handling (`GlobalApiExceptionHandler`).
  - `consumer/`: Kafka/RabbitMQ message listeners translating payloads into Application Commands.

### 2.4 Infrastructure Layer (`<base-package>.infrastructure`)
- **Outbound Adapter Principle:** Implements technical interfaces defined in Domain (repositories) and Application (outbound ports).
- **Allowed Dependencies:** Application ports, Domain repositories, Spring Data JPA, Hibernate, JDBC drivers, Cloud SDKs, Redis/Redisson, Kafka/RabbitMQ.
- **Strict Prohibitions:**
  - **NO** business rule decisions.
  - **NO** bypass of domain repository interfaces by exposing JPA entities to the application layer.
- **Components:**
  - `adapter/`: External service and cloud adapters implementing outbound ports.
  - `messaging/`: Message publishers and event dispatchers implementing outbound ports.
  - `persistence/jpa/entity/`: Database entities with `@Table`, `@Column`, `@Version`.
  - `persistence/jpa/repository/`: Spring Data JPA interfaces (`*JpaRepository`).
  - `persistence/adapter/`: Adapter classes implementing domain repositories (`*RepositoryAdapter`).
  - `persistence/mapper/`: Conversion between JPA entities and domain models.

---

## 3. Strict Naming Conventions

| Artifact | Location | Suffix / Pattern | Example |
| :--- | :--- | :--- | :--- |
| **Domain Model** | `domain/model/` | Noun (PascalCase) | `Order`, `FileMetadata` |
| **Value Object** | `domain/valueobject/` | Immutable Record/Class | `Money`, `StorageLocation` |
| **Domain Repository** | `domain/repository/` | `*Repository` | `OrderRepository`, `UserRepository` |
| **Inbound Port** | `application/port/in/` | `*UseCase` | `CreateOrderUseCase`, `FileUploadUseCase` |
| **Use Case Impl** | `application/usecase/` | `*Service` | `CreateOrderService`, `FileUploadService` |
| **Outbound Port** | `application/port/out/` | `*Port` | `NotificationPort`, `FileStorageServicePort` |
| **Application Input** | `application/dto/command/` | `*Command` | `CreateOrderCommand` |
| **Application Output** | `application/dto/result/` | `*Result` | `OrderGetResult` |
| **REST Controller** | `representation/rest/controller/` | `*Controller` | `OrderController`, `FileController` |
| **HTTP Request** | `representation/rest/dto/request/` | `*Request` | `CreateOrderRequest` |
| **HTTP Response** | `representation/rest/dto/response/` | `*Response` | `CreateOrderResponse` |
| **JPA Entity** | `infrastructure/persistence/jpa/entity/` | `*Entity` | `OrderEntity`, `FileMetadataEntity` |
| **Spring Data Repo** | `infrastructure/persistence/jpa/repository/` | `*JpaRepository` | `OrderJpaRepository` |
| **Persistence Adapter**| `infrastructure/persistence/` | `*RepositoryAdapter`| `OrderRepositoryAdapter` |
| **External Adapter** | `infrastructure/adapter/` | `*Adapter` | `StripePaymentAdapter`, `S3StorageAdapter` |

---

## 4. Cross-Cutting Architectural Patterns

### 4.1 Distributed Locking Pattern
- When modifying state that spans concurrent transactions or distributed nodes, use distributed locking (e.g. `@DistributedLock(key = "...")` via Redisson/Redis).
- Always set explicit `waitTime` and `leaseTime` to prevent deadlocks.

### 4.2 Idempotency Pattern
- Every state-mutating HTTP endpoint (POST, PUT, PATCH) must support idempotency checking (e.g. `Idempotency-Key` header with `@Idempotent`).
- Duplicated requests in-flight return `409 Conflict`; already completed requests replay the cached response.

### 4.3 Distributed Stampede-Protected Caching
- High-frequency read queries must use cache-aside with double-checked locking (`getOrLoad`) to prevent cache stampedes (dogpiling).
- Mutating operations (`update`, `delete`) must explicitly evict the corresponding cache key.

---

## 5. Architectural Anti-Patterns (Forbidden)

1. **Leaking JPA Entities:** Never return a `*Entity` from a repository or use case. Always map to/from domain models.
2. **Controller-to-Database Shortcut:** Never inject a `JpaRepository` or domain repository into a controller.
3. **Anemic Domain vs God Service:** Keep validation and invariant rules inside the Domain Model or Value Object, not scattered across procedural services.
4. **Bi-directional Domain Coupling:** Domain must never import classes from Application, Representation, or Infrastructure.
5. **Synchronous Distributed Transactions:** Avoid 2PC. Use idempotent compensation, optimistic locking, and clean status transitions.
