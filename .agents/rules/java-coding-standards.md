# Java & Concurrency Coding Standards

## 1. Modern Java (Java 21 / 25) Language Idioms

The architecture targets modern Java toolchains (Java 21 LTS, Java 25). Code must be clean, expressive, type-safe, and leverage contemporary language features.

### 1.1 Records for Immutable Data Carriers
- Use Java `record` for DTOs, Value Objects, query projections, and domain events:
  ```java
  public record Money(BigDecimal amount, Currency currency) {
      public Money {
          Objects.requireNonNull(amount, "amount must not be null");
          Objects.requireNonNull(currency, "currency must not be null");
          if (amount.compareTo(BigDecimal.ZERO) < 0) {
              throw new IllegalArgumentException("amount cannot be negative");
          }
      }
  }
  ```
- Do not use records for JPA Entities (Hibernate requires mutable getters/setters and proxyable non-final classes).

### 1.2 Pattern Matching & Switch Expressions
- Prefer pattern matching for `instanceof`:
  ```java
  if (exception instanceof ApplicationException appEx) {
      return handleApplicationException(appEx, request);
  }
  ```
- Use exhaustive switch expressions over chained `if-else`:
  ```java
  BigDecimal discount = switch (customerTier) {
      case PLATINUM -> new BigDecimal("0.20");
      case GOLD -> new BigDecimal("0.10");
      case SILVER, BRONZE -> BigDecimal.ZERO;
  };
  ```

### 1.3 Collection Factories & Immutability
- Prefer `List.of()`, `Set.of()`, and `Map.of()` for immutable collections.
- Never return `null` for a collection or map. Return `Collections.emptyList()`, `List.of()`, or `Optional.empty()`.
- Use `.toList()` directly on Stream instead of `.collect(Collectors.toList())`.

---

## 2. Project Loom & Virtual Threads Governance

When virtual threads are enabled (`spring.threads.virtual.enabled: true`), the JVM spawns a lightweight virtual thread for incoming requests and asynchronous tasks.

### 2.1 The Golden Rules of Virtual Threads
1. **Prefer Synchronous Blocking I/O over Reactive Complexity:**
   - Write simple, readable, synchronous code. When a virtual thread executes blocking I/O (e.g. database query, network HTTP call, file access), the JVM unmounts the virtual thread from its carrier thread without blocking OS kernel threads.
2. **Avoid Thread Pinning (`synchronized` with I/O):**
   - **NEVER** perform blocking I/O inside a `synchronized` block or method. Doing so "pins" the virtual thread to its underlying OS carrier thread, degrading concurrency across the application.
   - If mutual exclusion across I/O is required, use `java.util.concurrent.locks.ReentrantLock`:
     ```java
     // CORRECT: Does not pin carrier thread
     private final ReentrantLock lock = new ReentrantLock();
     public void processResource() {
         lock.lock();
         try {
             externalClient.call(...);
         } finally {
             lock.unlock();
         }
     }
     ```
3. **Do NOT Use `parallelStream()` for Small Collections:**
   - `parallelStream()` submits tasks to the shared `ForkJoinPool.commonPool()`, which bypasses virtual thread pooling and introduces thread scheduling overhead. Use standard sequential `.stream()` or loops.
4. **Avoid Heavy `ThreadLocal` State:**
   - Because virtual threads are lightweight and short-lived, millions can be instantiated. Avoid binding heavy objects to `ThreadLocal` to prevent massive memory footprint. Use scoped values where possible.

---

## 3. Formatting & Code Style Standards

The project strictly enforces automated formatting (e.g. Google Java Format AOSP via Spotless). Non-compliant code will fail CI/CD build checks.

### 3.1 Strict Import Ordering
Imports must follow this exact order with a single blank line between groups:
1. `java.*`
2. `javax.*`
3. `jakarta.*`
4. `org.*`
5. `com.*`
6. All other packages (e.g. `lombok.*`, third-party SDKs)
7. Static imports (`static ...`) at the very end

### 3.2 Formatting Automation Commands
```bash
# Gradle (Unix / macOS):
./gradlew spotlessApply

# Gradle (Windows):
cmd.exe /c "gradlew.bat spotlessApply"

# Maven (Unix / macOS):
./mvnw spotless:apply

# Maven (Windows):
cmd.exe /c "mvnw.cmd spotless:apply"
```

---

## 4. Lombok Usage Guidelines

Lombok reduces boilerplate, but must be used with architectural discipline:

| Annotation | Usage Rules |
| :--- | :--- |
| `@Getter` / `@Setter` | Allowed on DTOs, Entities, and Models. Prefer immutable fields wherever possible. |
| `@Builder` | Recommended for complex domain models, commands, results, and DTOs. Always accompany with `@NoArgsConstructor` + `@AllArgsConstructor` when used with Jackson or JPA. |
| `@Slf4j` | The standard and mandatory way to declare loggers in all components. |
| `@Data` | **FORBIDDEN** on JPA Entities. Generates broken `equals()` and `hashCode()` that interfere with Hibernate identity proxies and lazy collections. |
| `@ToString` | Use `exclude = {"sensitiveField"}` on any security-relevant fields (passwords, tokens, secret keys). |

---

## 5. Null Safety & Optional Rules

- **Parameters:** Do NOT accept `Optional<T>` as a method parameter. Use method overloading or explicit null checks with descriptive error messages (`Objects.requireNonNull(val, "val must not be null")`).
- **Return Values:** Use `Optional<T>` only for query methods where the result can legitimately be absent:
  ```java
  public Optional<Order> findById(UUID id);
  ```
- **Fields:** Never use `Optional<T>` as a class field or within serializable DTOs.
- **Collections:** Return empty collections instead of `Optional<List<T>>`.

---

## 6. Exception Handling Standards

1. **Hierarchy:** All custom business exceptions must extend a shared domain or application exception (e.g. `DomainException` or `ApplicationException`).
2. **Standard Error Codes:** Always supply an error code and message key:
   ```java
   throw new ApplicationException("ORDER_NOT_FOUND", "error.order.not_found");
   ```
3. **No Empty Catch Blocks:** Never catch and swallow an exception. If an operation is non-critical, log a descriptive warning:
   ```java
   try {
       notificationPort.notifyUser(userId, event);
   } catch (Exception e) {
       log.warn("Non-critical notification failed for user {}: {}", userId, e.getMessage());
   }
   ```
4. **Preserve Stack Traces:** When re-throwing exceptions, always pass the root cause:
   ```java
   throw new ApplicationException("PROCESSING_FAILED", "Failed to process payload", cause);
   ```
