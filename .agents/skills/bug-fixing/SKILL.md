---
name: bug-fixing
description: Systematic defect analysis, test-driven bug reproduction, root cause isolation, minimal surgical correction, and regression verification.
---

# Bug Fixing Skill

Use this skill when diagnosing, reproducing, and fixing defects or unexpected behaviors in any Spring Boot backend microservice.

---

## Step 1: Defect Triage & Error Characterization

1. **Collect Observability Data:**
   - Gather exact error codes, HTTP status codes, exception class names, and stack traces.
   - Inspect distributed trace logs with `traceId` and `spanId`.
2. **Isolate the Affected Architectural Layer:**
   - **Representation:** Request binding error, missing validation, wrong HTTP status mapping in global exception handler, deserialization error.
   - **Application:** Use case orchestration failure, missing `@Transactional`, lock timeout, cache staleness.
   - **Domain:** Model invariant violation, invalid state transition.
   - **Infrastructure:** Client network timeout, SQL constraint violation, migration discrepancy, persistence mapper bug.
   - **Concurrency:** Optimistic lock collision (`ObjectOptimisticLockingFailureException`), distributed lock acquisition failure.

---

## Step 2: Author a Reproducing Regression Test

**Mandatory Rule:** Before writing fix code, write an automated test that reproduces the bug and demonstrates the failure.

1. **Locate Target Test Class:**
   - For use case defects &rarr; `src/test/java/.../application/usecase/*ServiceTest.java`.
   - For HTTP/REST/mapping defects &rarr; `src/test/java/.../representation/rest/controller/*ControllerTest.java`.
   - For exception handler defects &rarr; `src/test/java/.../representation/rest/handler/*ExceptionHandlerTest.java`.
2. **Construct Test Case:**
   - Create a test method naming the defect condition:
     ```java
     @Test
     @DisplayName("Should throw ConcurrencyConflict when concurrent update version mismatches")
     void shouldHandleOptimisticLockCollision() { ... }
     ```
3. **Execute and Confirm Failure:**
   Run the test to confirm it fails for the expected reason:
   ```bash
   # Gradle:
   ./gradlew test --tests *NameOfFailingTest*

   # Maven:
   ./mvnw test -Dtest=*NameOfFailingTest*
   ```

---

## Step 3: Implement Surgical Fix

1. **Minimal Surface Area:**
   - Fix the root cause in the correct architectural layer.
   - Do NOT introduce workarounds in the Controller if the defect resides in the Domain Model or Repository Adapter.
2. **Preserve Behavioral Contracts:**
   - Avoid changing public API method signatures unless part of the defect correction.
3. **Handle Edge Cases:**
   - Check null safety, empty collections, zero values, and edge conditions.

---

## Step 4: Verification & Regression Prevention

1. **Verify the Failing Test Passes:**
   ```bash
   # Gradle:
   ./gradlew test --tests *NameOfFailingTest*

   # Maven:
   ./mvnw test -Dtest=*NameOfFailingTest*
   ```
2. **Format Code:**
   ```bash
   # Gradle:
   ./gradlew spotlessApply

   # Maven:
   ./mvnw spotless:apply
   ```
3. **Run Entire Test Suite:**
   Ensure the fix did not break any other tests:
   ```bash
   # Gradle:
   ./gradlew check

   # Maven:
   ./mvnw verify
   ```

---

## Step 5: Document Fix & Root Cause

Provide a concise summary explaining:
- **Root Cause:** What caused the failure.
- **Surgical Change:** What code was adjusted.
- **Regression Defense:** Which automated test now prevents this regression.
