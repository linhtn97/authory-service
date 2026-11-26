# Spring Boot 3 & Framework Standards

## 1. Dependency Injection & Bean Configuration

### 1.1 Mandatory Constructor Injection
- **Field injection (`@Autowired private ...`) is strictly FORBIDDEN.**
- Use explicit constructor injection or `@RequiredArgsConstructor` (with `final` fields) for all components.
- For optional beans (e.g. a cache service or metrics collector that may be absent in lightweight test fixtures), use `@Autowired(required = false)` in constructor overloads:
  ```java
  @Service
  public class OrderGetService implements OrderGetUseCase {
      private final OrderRepository orderRepository;
      private final CacheService cacheService;

      public OrderGetService(OrderRepository orderRepository) {
          this(orderRepository, null);
      }

      @Autowired
      public OrderGetService(
              OrderRepository orderRepository,
              @Autowired(required = false) CacheService cacheService) {
          this.orderRepository = orderRepository;
          this.cacheService = cacheService;
      }
  }
  ```

### 1.2 Stereotype Annotations
- Use `@RestController` on representation controllers in `representation/rest/controller/`.
- Use `@Service` on application use case implementations in `application/usecase/`.
- Use `@Component` on infrastructure adapters, aspects, and technical utility beans.
- Use `@Configuration` only in the `config/` package. Keep configuration classes focused, modular, and single-purpose (e.g. `RedisConfig`, `ShedLockConfig`, `SwaggerConfig`).

---

## 2. Transaction Management Standards

### 2.1 Placement of `@Transactional`
- **Application Services Own Transactions:** Place `@Transactional` exclusively on methods of use case implementations (`application/usecase/*Service`).
- **Never place `@Transactional` on REST Controllers:** Controllers must not participate in database transactions.
- **Never rely solely on Spring Data repository transactions:** Single repository methods have default transactional behavior, but multi-step domain workflows require explicit boundary definition.

### 2.2 Read vs Write Transactions
- **Queries:** Mark all read-only use cases with `@Transactional(readOnly = true)`. This avoids Hibernate dirty-checking overhead and routes queries to read replicas if configured:
  ```java
  @Override
  @Transactional(readOnly = true)
  public OrderGetResult execute(UUID id) { ... }
  ```
- **Mutations:** Use explicit isolation level for state updates:
  ```java
  @Override
  @Transactional(isolation = Isolation.READ_COMMITTED)
  public OrderUpdateResult execute(UUID id, OrderUpdateCommand command) { ... }
  ```

### 2.3 Decoupling Heavy I/O from Database Transactions
- **CRITICAL:** Do NOT hold a database transaction open while performing long-running network or disk I/O (such as calling a slow third-party API, streaming large files, or sending remote emails).
- **Anti-Pattern:**
  ```java
  @Transactional // DANGER: DB connection held for the entire 10s remote API call!
  public OrderResult execute(OrderCommand command) {
      paymentGatewayClient.charge(...); // Blocks 10s
      repository.save(order);
  }
  ```
- **Recommended Pattern:**
  1. Initialize state in DB (`status = PENDING`) via short transaction.
  2. Execute external network call outside the DB transaction.
  3. Update final state in DB (`status = CONFIRMED`) via a second short transaction.

---

## 3. Distributed Locking Standards

For operations with concurrent race hazards across service instances:
- Apply distributed locks (e.g. `@DistributedLock(key = "...")` via Redisson or Redis).
- Keys must use SpEL expressions targeting unique resource identifiers:
  ```java
  @DistributedLock(key = "'order:' + #id", waitTime = 5, leaseTime = 30)
  public OrderUpdateResult execute(UUID id, OrderUpdateCommand command) { ... }
  ```
- Ensure key prefixes are configurable via application properties.
- If a lock cannot be acquired within `waitTime`, throw a specific lock exception mapping to HTTP 409 Conflict.

---

## 4. Configuration Properties (`@ConfigurationProperties`)

- Bind structured configurations to type-safe properties classes prefixed logically (e.g. `<service>.storage.*`, `platform.cache.*`).
- Enable configuration validation where applicable (`@Validated`, `@NotNull`, `@Min`).
- Support environment variable overrides with sensible local defaults in `application.yaml`:
  ```yaml
  app:
    client:
      timeout: ${CLIENT_TIMEOUT:5000ms}
      base-url: ${CLIENT_BASE_URL:http://localhost:8080}
  ```

---

## 5. Input Validation & API Documentation

- **Request DTOs:** Always annotate incoming request DTOs with Jakarta validation constraints:
  ```java
  @Getter
  @Setter
  public class CreateOrderRequest {
      @NotBlank(message = "Order title is required")
      @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
      private String title;
  }
  ```
- **Controller Endpoints:** Must annotate parameter with `@Valid` or `@Validated`:
  ```java
  @PostMapping
  public ResponseEntity<OrderResponse> create(
          @Valid @RequestBody CreateOrderRequest request)
  ```
- **OpenAPI / Swagger:** Provide `@Operation(summary = "...")` and `@ApiResponses` documenting success and expected error codes (`400`, `404`, `409`, `500`).
