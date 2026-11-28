# Authory Service: Developer Guidelines & Engineering Standards

## 1. Prerequisites & Environment Setup

To build and run **Authory Service**, ensure the following development tools are installed:

- **Java Development Kit (JDK):** Version **25** (Oracle OpenJDK or Eclipse Temurin).
- **Build Tool:** Gradle (Use the included `./gradlew` wrapper configured to Gradle **9.4.1**).
- **Database:** PostgreSQL **15+** running locally or via Docker (`localhost:5432`).
- **IDE:** IntelliJ IDEA 2025+, Eclipse, or VS Code with Java Extension Pack. Enable Lombok and Annotation Processing.

---

## 2. Essential Gradle Commands

| Task | Command | Description |
| :--- | :--- | :--- |
| **Compile Code** | `./gradlew compileJava --no-daemon` | Compiles main source files and runs MapStruct annotation processors |
| **Apply Code Format** | `./gradlew spotlessApply --no-daemon` | Automatically formats all Java and Gradle files to Google Java Format |
| **Check Code Format** | `./gradlew spotlessCheck --no-daemon` | Validates formatting without modifying files (CI Quality Gate) |
| **Run Unit Tests** | `./gradlew test --tests *UserCreateServiceTest* --no-daemon` | Executes fast, isolated unit tests without requiring a live database |
| **Full Build & Test** | `./gradlew build --no-daemon` | Executes full compilation, spotless check, and test suite |
| **Run Application** | `./gradlew bootRun --no-daemon` | Starts the Spring Boot application on port `8600` |

> [!TIP]
> Always execute `./gradlew spotlessApply --no-daemon` before pushing code or creating a pull request. The CI pipeline will reject any unformatted code via `spotlessCheck`.

---

## 3. Code Style & Standards

Authory Service strictly enforces **Google Java Format** using the Spotless Gradle plugin.

### Key Rules:
- **Indent:** 4 spaces (Google Java Format style for Java files).
- **Line Length:** 100 characters maximum.
- **Imports:** Alphabetical ordering, no wildcard imports (`import java.util.*` is prohibited).
- **Lombok Usage:** Use `@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor` on Domain and Entity models. Avoid `@Data` on JPA Entities to prevent circular `equals`/`hashCode` and performance regressions.

---

## 4. MapStruct Best Practices

All data mappers use **MapStruct 1.6.3** with Spring component model integration.

### Audit Mapper Configuration:
Domain mappers inherit standard audit field mappings via `AuditMapperConfig`:

```java
@Mapper(componentModel = "spring", config = AuditMapperConfig.class)
public abstract class RoleMapper implements BaseMapper<Role, RoleDto> {}
```

### Entity to Domain Mapping:
When creating persistence mappers in `infrastructure/persistence/mapper/`, use clean static mapper classes for optimal performance and zero runtime reflection:

```java
public final class UserEntityMapper {
    private UserEntityMapper() {}

    public static User toDomain(UserEntity entity) {
        if (entity == null) return null;
        return User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .build();
    }

    public static UserEntity toEntity(User domain) {
        if (domain == null) return null;
        UserEntity entity = new UserEntity();
        entity.setId(domain.getId());
        entity.setUsername(domain.getUsername());
        entity.setEmail(domain.getEmail());
        return entity;
    }
}
```

---

## 5. Testing Strategy

### 5.1 Use Case Unit Testing (Fast & Isolated)
Because domain repository ports are pure interfaces, Application Use Cases must be unit tested with **Mockito** in complete isolation without starting Spring or connecting to a database:

```java
@ExtendWith(MockitoExtension.class)
class UserCreateServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private I18nHelper i18nHelper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserCreateService userCreateService;

    @Test
    void execute_WhenValidCommand_ReturnsUserResult() {
        // Arrange
        UserCreateCommand command = UserCreateCommand.builder()
                .username("john.doe")
                .email("john@example.com")
                .build();

        when(userRepository.create(any(User.class)))
                .thenAnswer(inv -> {
                    User u = inv.getArgument(0);
                    u.setId(UUID.randomUUID());
                    return u;
                });

        // Act
        UserResult result = userCreateService.execute(command);

        // Assert
        assertNotNull(result);
        assertEquals("john.doe", result.getUsername());
        verify(userRepository).create(any(User.class));
        verify(eventPublisher).publishEvent(any(UserCreatedEvent.class));
    }
}
```

### 5.2 Test Suite Architecture & Coverage Matrix

The unit test suite consists of **30 test classes** and **69 unit test cases** executing via JUnit 5 and Mockito in pure isolation without requiring an active database or network:

| Test Class | Target Component | Coverage Focus |
| :--- | :--- | :--- |
| `UserAuthenticationServiceTest` | `UserAuthenticationService` | Register via `UserCreateUseCase`, password hashing, standard login via `UserGetUseCase`, MFA login, refresh token, logout, account lockout checks, API key and client credential resolution |
| `DashboardGetMetricsServiceTest` | `DashboardGetMetricsService` | Aggregates system metrics exclusively across module ports (`UserGetUseCase`, `RoleGetUseCase`, `TokenUseCase`, etc.) |
| `UserCredentialServiceTest` | `UserCredentialService` | Credential validation, 5-strike account lockout defense, unlock, MFA enable/disable, password updates |
| `TokenServiceTest` | `TokenService` | Token generation, JTI validation, revocation, and session cleanup |
| `UserCreateServiceTest` | `UserCreateService` | User validation, unique constraints, and `UserCreatedEvent` publishing |
| `UserGetServiceTest` | `UserGetService` | Single user retrieval, username lookup, optional findById, count, countNewUsersToday |
| `UserUpdateServiceTest` | `UserUpdateService` | User field modifications and `UserUpdatedEvent` publishing |
| `UserDeleteServiceTest` | `UserDeleteService` | User deletion and `UserDeletedEvent` publishing |
| `UserUnlockServiceTest` | `UserUnlockService` | Administrative account unlock delegation |
| `UserGetPageServiceTest` | `UserGetPageService` | Pagination and sorting response transformation |
| `RoleCreateServiceTest` | `RoleCreateService` | Role creation and `RoleCreatedEvent` publishing |
| `RoleGetServiceTest` | `RoleGetService` | Single role lookup, count, and not found handling |
| `RoleUpdateServiceTest` | `RoleUpdateService` | Role updating and `RoleUpdatedEvent` publishing |
| `RoleDeleteServiceTest` | `RoleDeleteService` | Role deletion and `RoleDeletedEvent` publishing |
| `UserAuthoryServiceTest` | `UserAuthoryService` | Aggregating user roles, groups, resources, and permission merging |
| `PolicyEvaluationServiceTest` | `PolicyEvaluationService` | ABAC dynamic PDP evaluation via `UserGetUseCase` port and SpEL policy decisions |
| `AttributeCreateServiceTest` | `AttributeCreateService` | ABAC attribute creation |
| `AttributeGetServiceTest` | `AttributeGetService` | ABAC attribute retrieval |
| `CompanyCreateServiceTest` | `CompanyCreateService` | Organization company creation |
| `CompanyGetServiceTest` | `CompanyGetService` | Organization company retrieval |
| `SystemActivityGetRecentServiceTest` | `SystemActivityGetRecentService` | Recent activity querying and mapping |
| `JwtAuthorizationFilterTest` | `JwtAuthorizationFilter` | Stateless JWT parsing, Bearer token extraction, and SecurityContext population |
| `TotpUtilTest` | `TotpUtil` | RFC 6238 TOTP key generation and 6-digit code verification |
| `GlobalApiExceptionHandlerConcurrencyTest` | `GlobalApiExceptionHandler` | HTTP 409 Conflict mapping for optimistic locking and race conditions |

### 5.3 Persistence Adapter Testing
To test `*RepositoryAdapter` classes, use `@DataJpaTest` with Testcontainers or an H2 / test PostgreSQL instance to verify custom JPA queries and database constraints.

---

## 6. Git Workflow & Commit Standards

- **Branch Naming:**
  - `feature/<feature-name>` (e.g. `feature/user-avatar-upload`)
  - `fix/<bug-description>` (e.g. `fix/refresh-token-expiration`)
  - `refactor/<module-name>` (e.g. `refactor/rbac-hexagonal`)
- **Commit Messages:** Follow Conventional Commits:
  - `feat(user): add user delete use case and soft-delete logic`
  - `fix(auth): correct token refresh expiration check`
  - `refactor(rbac): decouple role and permission entities into domain models`
  - `docs: update API integration guidelines and architecture manual`

