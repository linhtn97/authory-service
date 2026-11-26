---
name: refactoring
description: Safe, incremental code refactoring standards to eliminate code smells, decouple layers, modernize Java idioms, and optimize concurrency without altering external behavior.
---

# Refactoring Skill

Use this skill when restructuring existing code to improve maintainability, decouple layers, modernize Java idioms, or optimize concurrency in any Spring Boot microservice.

---

## 1. The Cardinal Rule of Refactoring

> **Refactoring alters the internal structure of code without modifying its observable external behavior.**

Never mix feature enhancements or API schema changes into a refactoring task.

---

## 2. Refactoring Pre-Flight Check

Before making any code changes:
1. **Verify Baseline Tests:**
   Execute the test suite and verify that 100% of existing tests are green:
   ```bash
   # Gradle:
   ./gradlew test

   # Maven:
   ./mvnw test
   ```
2. **Missing Test Coverage Warning:**
   If the code to be refactored lacks test coverage, write comprehensive unit tests covering existing behavior BEFORE initiating the refactor.

---

## 3. Standard Refactoring Targets

| Code Smell / Debt | Refactoring Target | Architecture Benefit |
| :--- | :--- | :--- |
| **Layer Leakage** | Move DB queries from Controller to Application Port & JPA Adapter. | Preserves Hexagonal boundary purity. |
| **Thread Pinning** | Replace `synchronized` blocks doing I/O with `ReentrantLock`. | Unblocks Project Loom virtual threads. |
| **ParallelStream Overhead**| Replace `parallelStream()` on small collections with sequential `.stream()`. | Eliminates ForkJoinPool context switching. |
| **Field Injection** | Replace `@Autowired private ...` with constructor injection. | Enables deterministic Mockito unit testing. |
| **Anemic Validation** | Move validation logic into Domain Model or Value Object constructors. | Centralizes domain invariant enforcement. |
| **Missing Cache Aside** | Wrap high-frequency repository lookups with cache-aside (`getOrLoad`). | Protects database from cache stampedes. |
| **Verbose Data Carriers** | Convert immutable DTOs/Value Objects to Java `record`. | Modernizes syntax, provides automatic immutability. |

---

## 4. Step-by-Step Refactoring Process

1. **Perform Micro-Steps:**
   Make one atomic refactoring change at a time (e.g. extract method &rarr; extract class &rarr; invert dependency).
2. **Re-run Tests Immediately:**
   After each micro-change, execute tests to guarantee no regression occurred:
   ```bash
   # Gradle:
   ./gradlew test

   # Maven:
   ./mvnw test
   ```
3. **Purge Dead Code:**
   Delete obsolete imports, deprecated helper methods, and unused private fields.
4. **Format & Verify:**
   Apply code formatting and run full verification:
   ```bash
   # Gradle:
   ./gradlew spotlessApply && ./gradlew check

   # Maven:
   ./mvnw spotless:apply && ./mvnw verify
   ```
