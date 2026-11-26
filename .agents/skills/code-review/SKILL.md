---
name: code-review
description: Systematic code review procedure to audit code changes for Hexagonal architecture integrity, Project Loom virtual thread compatibility, distributed concurrency, security vulnerabilities, and testing standards.
---

# Code Review Skill

Use this skill when reviewing code changes, pull requests, or diffs in any Spring Boot microservice. Perform a multi-angle inspection against the following criteria.

---

## 1. Architectural & Layer Boundary Inspection

- [ ] **Domain Layer Purity:** Does any file in `<base-package>.domain` import Spring (`org.springframework.*`), JPA (`jakarta.persistence.*`), or Web (`org.springframework.web.*`)? *Any occurrence is an immediate blocker.*
- [ ] **No Controller-to-Repository Shortcuts:** Does any Controller inject a `*Repository` or `*JpaRepository` directly? Controllers must exclusively communicate through Inbound Ports (`*UseCase`).
- [ ] **No Leaked JPA Entities:** Are `*Entity` classes returned by Use Cases or exposed in Controller responses? Entities must be mapped to Domain Models or Application Results.
- [ ] **Port & Adapter Inversion:** Are outbound dependencies defined as interfaces in `application/port/out` or `domain/repository` and implemented in `infrastructure/`?

---

## 2. Concurrency, Virtual Threads & Locking Inspection

- [ ] **Virtual Thread Pinning Check:** Is there any `synchronized` block or method executing blocking I/O (network calls, file I/O, database queries)? Must be replaced with `ReentrantLock`.
- [ ] **ForkJoinPool Anti-Pattern:** Is `parallelStream()` used on small in-memory collections? Must use sequential `.stream()`.
- [ ] **Distributed Lock Key Precision:** Does distributed locking use a specific SpEL expression with resource IDs (e.g. `'resource:' + #id`), rather than static broad locks?
- [ ] **Optimistic Locking:** Does the aggregate root entity have `@Version` to guard against lost updates during concurrent edits?
- [ ] **Multi-Instance Scheduler Lock:** Are all `@Scheduled` background tasks protected by a distributed lock (e.g. `@SchedulerLock`)?

---

## 3. Security & Vulnerability Defense Inspection

- [ ] **SQL Injection Defense:** Are all database queries parameterized via Spring Data JPA or named parameters? No raw string concatenation.
- [ ] **Path Traversal Defense:** Does any storage adapter resolve file paths from user inputs without canonical path normalization and a root containment check (`resolved.startsWith(rootPath)`)?
- [ ] **Secret Exposure:** Are any access keys, passwords, or tokens hardcoded in code or configuration files?
- [ ] **Input Validation:** Are all request DTO parameters annotated with `@Valid` and appropriate Jakarta constraints (`@NotBlank`, `@NotNull`, `@Size`)?

---

## 4. Transaction Management & Resource Cleanliness

- [ ] **No Long I/O inside Transactions:** Are long-running network or disk calls executed inside a `@Transactional` boundary? Database transactions must be kept minimal to avoid connection pool starvation.
- [ ] **Read-Only Transactions:** Are query methods explicitly annotated with `@Transactional(readOnly = true)`?
- [ ] **Cache Eviction Consistency:** Are cache entries evicted when entities are updated or deleted?

---

## 5. Testing & Verification Checklist

- [ ] **Unit Test Coverage:** Does every new or altered Use Case have a corresponding Mockito unit test testing both happy and error paths?
- [ ] **Lenient Stubbing Check:** Are Mockito mocks properly configured with `lenient()` if declared in `@BeforeEach` but not used in every test method?
- [ ] **Spotless & Build Compliance:** Run the build verification command:
  ```bash
  # Gradle:
  ./gradlew spotlessCheck && ./gradlew check

  # Maven:
  ./mvnw spotless:check && ./mvnw verify
  ```
