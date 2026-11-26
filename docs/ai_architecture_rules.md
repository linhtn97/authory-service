# Hexagonal Architecture & Domain-Driven Design (DDD) Rules

This document outlines the architectural rules, directory structures, naming conventions, and coding constraints for **Authory Service**. All developers and AI assistants must strictly adhere to these rules when creating, refactoring, or reviewing code.

---

## 1. Core Architectural Philosophy

Authory Service is designed as a **Modular Monolith** using **Hexagonal Architecture (Ports and Adapters)** and **Domain-Driven Design (DDD)**.

1. **Separation of Concerns:** Core business rules reside exclusively in the Domain layer, completely insulated from Spring, JPA/Hibernate, HTTP web frameworks, and third-party libraries.
2. **Inward Dependency Rule:** Dependencies strictly point inward toward the Domain layer:
   ```
   Representation (Inbound Adapters) -> Application (Use Cases) -> Domain (Core Models & Ports) <- Infrastructure (Outbound Persistence Adapters)
   ```
3. **Ports and Adapters:**
   - **Inbound Ports:** Interfaces defining application use cases (`application/port/in`).
   - **Inbound Adapters:** Web controllers, consumers, or listeners invoking use cases (`representation/rest/controller`).
   - **Outbound Ports:** Domain repository interfaces defining data access contracts (`domain/repository`).
   - **Outbound Adapters:** Spring Data JPA repositories, external HTTP clients, and database persistence logic (`infrastructure/persistence`).

---

## 2. Layer Constraints & Responsibilities

### 2.1 Domain Layer (`modules/<feature>/domain/`)
- **Purity Guarantee:** The purest layer in the application.
- **Allowed:** Pure Java classes, records, enums, Lombok (`@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`).
- **STRICTLY PROHIBITED:**
  - `jakarta.persistence.*` (`@Entity`, `@Table`, `@Id`, `@Column`, `@ManyToOne`, etc.)
  - `org.hibernate.*`
  - `org.springframework.*`
  - Spring Data interfaces (`JpaRepository`, `JpaSpecificationExecutor`)
- **Typical Components:**
  - `domain/model/`: Pure business models (e.g. `User`, `Role`, `Partner`).
  - `domain/repository/`: Outbound repository ports (e.g. `UserRepository`, `RoleRepository`).
  - `domain/event/`: Domain lifecycle events (e.g. `UserCreateEvent`, `RoleCreateEvent`).

### 2.2 Application Layer (`modules/<feature>/application/`)
- **Responsibility:** Implements and coordinates application use cases and business workflows.
- **Allowed:** Domain layer models, Spring `@Service`, `@Transactional`, standard Java utilities.
- **STRICTLY PROHIBITED:**
  - REST Controllers, HTTP Request/Response objects.
  - Direct JPA Entities, SQL queries, or database drivers.
- **Typical Components:**
  - `application/port/in/`: Use case interfaces (e.g. `UserCreateUseCase`, `UserGetUseCase`).
  - `application/usecase/`: Services implementing inbound ports (e.g. `UserCreateService`).
  - `application/dto/command/`: Action commands from callers (e.g. `UserCreateCommand`).
  - `application/dto/result/`: Results returned from use cases (e.g. `UserResult`).
  - `application/mapper/`: Maps between Application DTOs and Domain Models.

### 2.3 Representation Layer (`modules/<feature>/representation/`)
- **Responsibility:** Inbound web adapters exposing RESTful HTTP APIs.
- **Allowed:** Spring WebMVC (`@RestController`, `@RequestMapping`), Jakarta Validation (`@Valid`), Springdoc OpenAPI (`@Operation`, `@Tag`), Application Inbound Ports (`*UseCase`), Application DTOs.
- **STRICTLY PROHIBITED:**
  - Direct access to Database Repositories or JPA Entities.
  - Business invariant validation (business rules belong in Domain/Application).
- **Typical Components:**
  - `representation/rest/controller/`: REST API Controllers (e.g. `UserController`).
  - `representation/rest/dto/request/`: HTTP JSON request bodies (e.g. `UserCreateRequest`).
  - `representation/rest/dto/response/`: HTTP JSON response payloads (e.g. `UserResponse`).
  - `representation/rest/mapper/`: Maps REST DTOs to Application Commands and Results to Responses.

### 2.4 Infrastructure Layer (`modules/<feature>/infrastructure/`)
- **Responsibility:** Outbound technical adapters providing persistence, message delivery, and external integrations.
- **Allowed:** Spring Data JPA, Hibernate, PostgreSQL driver, database entities, connection pools.
- **STRICTLY PROHIBITED:**
  - Core business decisions or domain workflow orchestration.
- **Typical Components:**
  - `infrastructure/persistence/jpa/entity/`: Database ORM entities (e.g. `UserEntity`).
  - `infrastructure/persistence/jpa/repository/`: Spring Data JPA interfaces (e.g. `UserJpaRepository`).
  - `infrastructure/persistence/mapper/`: Bidirectional mappers between JPA Entities and Domain Models (e.g. `UserEntityMapper`).
  - `infrastructure/persistence/`: Adapters implementing domain repository ports (e.g. `UserRepositoryAdapter`).

---

## 3. Mandatory Directory Structure

When creating a new bounded context or adding features to an existing module, adhere to the standard directory layout:

```text
src/main/java/com/linhtn/authory_service/modules/<feature>/
├── application/
│   ├── dto/
│   │   ├── command/                 # Action DTOs (e.g., FeatureCreateCommand.java)
│   │   └── result/                  # Result DTOs (e.g., FeatureResult.java)
│   ├── mapper/                      # Application DTO <-> Domain Model mappers
│   ├── port/
│   │   ├── in/                      # Inbound Use Case ports (e.g., FeatureCreateUseCase.java)
│   │   └── out/                     # Optional application-specific outbound ports
│   └── usecase/                     # Use Case implementations (e.g., FeatureCreateService.java)
├── domain/
│   ├── event/                       # Domain events (e.g., FeatureCreatedEvent.java)
│   ├── model/                       # Pure Domain POJOs (e.g., Feature.java)
│   └── repository/                  # Outbound Repository Ports (e.g., FeatureRepository.java)
├── infrastructure/
│   └── persistence/
│       ├── jpa/
│       │   ├── entity/              # JPA ORM Entities (e.g., FeatureEntity.java)
│       │   └── repository/          # Spring Data JPA interfaces (e.g., FeatureJpaRepository.java)
│       ├── mapper/                  # Entity <-> Domain mappers (e.g., FeatureEntityMapper.java)
│       └── FeatureRepositoryAdapter.java # Implements FeatureRepository port
└── representation/
    └── rest/
        ├── controller/              # REST Controllers (e.g., FeatureController.java)
        ├── dto/
        │   ├── request/             # REST Request payloads (e.g., FeatureCreateRequest.java)
        │   └── response/            # REST Response payloads (e.g., FeatureResponse.java)
        └── mapper/                  # Maps REST DTOs <-> Application Commands/Results
```

---

## 4. Naming Conventions

| Component | Suffix / Pattern | Example |
| :--- | :--- | :--- |
| **Domain Model** | Bare entity name | `User`, `Role`, `Partner` |
| **Domain Repository Port** | `*Repository` | `UserRepository`, `RoleRepository` |
| **Domain Event** | `*<Action>Event` | `UserCreateEvent`, `RoleDeleteEvent` |
| **Inbound Port (Use Case)** | `*<Action>UseCase` | `UserCreateUseCase`, `UserGetPageUseCase` |
| **Use Case Service** | `*<Action>Service` | `UserCreateService`, `UserGetPageService` |
| **Application Command** | `*<Action>Command` | `UserCreateCommand`, `UserUpdateCommand` |
| **Application Result** | `*Result` | `UserResult`, `PartnerResult` |
| **JPA Entity** | `*Entity` | `UserEntity`, `RoleEntity` |
| **Spring Data Repository** | `*JpaRepository` | `UserJpaRepository`, `RoleJpaRepository` |
| **Entity Mapper** | `*EntityMapper` | `UserEntityMapper`, `RoleEntityMapper` |
| **Repository Adapter** | `*RepositoryAdapter` | `UserRepositoryAdapter`, `RoleRepositoryAdapter` |
| **REST Controller** | `*Controller` | `UserController`, `RoleController` |
| **REST Request DTO** | `*<Action>Request` | `UserCreateRequest`, `UserUpdateRequest` |
| **REST Response DTO** | `*Response` | `UserResponse`, `RoleResponse` |

---

## 5. Coding Workflow for Feature Implementation

When developing a new feature (e.g. `Create Position`):

1. **Step 1: Domain Core**
   - Create the domain model `Position` in `modules/organization/domain/model/Position.java`.
   - Define the outbound port `PositionRepository` in `modules/organization/domain/repository/PositionRepository.java`.
2. **Step 2: Infrastructure Persistence Adapter**
   - Create JPA entity `PositionEntity` in `modules/organization/infrastructure/persistence/jpa/entity/PositionEntity.java`.
   - Create Spring Data JPA repository `PositionJpaRepository`.
   - Create bidirectional mapper `PositionEntityMapper`.
   - Implement `PositionRepositoryAdapter` implementing `PositionRepository`.
3. **Step 3: Application Use Case**
   - Define `PositionCreateCommand` and `PositionResult`.
   - Define inbound port `PositionCreateUseCase`.
   - Implement use case in `PositionCreateService`.
4. **Step 4: Representation REST Adapter**
   - Create `PositionCreateRequest` and `PositionResponse`.
   - Create `PositionRestMapper` translating between REST DTOs and Application DTOs.
   - Expose endpoint in `PositionController` (`POST /api/v1/positions`).
5. **Step 5: Code Formatting & Verification**
   - Format with `./gradlew spotlessApply --no-daemon`.
   - Verify compilation and style with `./gradlew spotlessCheck --no-daemon`.
   - Write unit tests mocking the repository port and run `./gradlew test`.

