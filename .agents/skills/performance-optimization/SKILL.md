---
name: performance-optimization
description: Systematic procedures for diagnosing and resolving JPA N+1 queries, Project Loom thread pinning, distributed cache stampedes, and database connection pool bottlenecks.
---

# Performance Optimization Skill

Use this skill when diagnosing slow endpoints, high CPU/memory usage, database connection starvation, or degraded latency in any Spring Boot microservice.

---

## Step 1: JPA & Database Query Optimization

### 1.1 Eliminate N+1 Select Queries
- **The Symptom:** Loading 1 parent entity triggers $N$ separate SQL queries to load its child associations.
- **Diagnostic:** Enable SQL logging in development:
  ```yaml
  spring:
    jpa:
      properties:
        hibernate:
          generate_statistics: true
  ```
- **Correction:** Use `JOIN FETCH` in JPQL or declare `@EntityGraph`:
  ```java
  // CORRECT: Executes exactly 1 SQL query with join
  @Query("SELECT o FROM OrderEntity o JOIN FETCH o.items WHERE o.id = :id")
  Optional<OrderEntity> findByIdWithItems(@Param("id") UUID id);
  ```

### 1.2 Prevent Unbounded Queries
- **Rule:** Never invoke `repository.findAll()` on non-static, growing tables.
- **Correction:** Always enforce `Pageable` limits:
  ```java
  Page<OrderEntity> findByStatus(OrderStatus status, Pageable pageable);
  ```

### 1.3 Leverage Read-Only Transactions
- For all query-only use cases, ensure `@Transactional(readOnly = true)` is present.
- **Performance Benefit:** Disables Hibernate session dirty-checking, reduces memory consumption, and routes queries to database read replicas.

### 1.4 Enable JDBC Batch Inserts & Updates
For bulk operations, configure JDBC batching in `application.yaml`:
```yaml
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 50
        order_inserts: true
        order_updates: true
```

---

## Step 2: Project Loom & Virtual Thread Pinning Diagnosis

### 2.1 Trace Pinned Virtual Threads
When virtual threads execute blocking I/O inside a `synchronized` block or method, they "pin" the underlying OS carrier thread, degrading concurrency.
- **Diagnostic JVM Flag:** Launch the application with:
  ```bash
  -Djdk.tracePinnedThreads=full
  ```
- Any pinned thread stack trace printed in console must be refactored.

### 2.2 Refactor Pinning to `ReentrantLock`
- **Before (Pinned):**
  ```java
  public synchronized byte[] fetchRemoteFile(String url) {
      return httpClient.send(...); // PINS CARRIER THREAD!
  }
  ```
- **After (Unpinned):**
  ```java
  private final ReentrantLock lock = new ReentrantLock();

  public byte[] fetchRemoteFile(String url) {
      lock.lock();
      try {
          return httpClient.send(...); // Unmounts safely
      } finally {
          lock.unlock();
      }
  }
  ```

### 2.3 Eliminate `parallelStream()` for Small Collections
- `parallelStream()` utilizes `ForkJoinPool.commonPool()` and incurs scheduling overhead.
- Under virtual threads, standard sequential `.stream()` or unrolled loops execute significantly faster for in-memory collections.

---

## Step 3: Distributed Caching & Stampede Prevention

### 3.1 Distributed Cache-Aside (`getOrLoad`)
Avoid manual cache check-then-set logic, which allows multiple concurrent threads to query the database simultaneously when a cache key expires (Cache Stampede / Dogpiling).
- **Correction:** Use `getOrLoad` with double-checked distributed locking:
  ```java
  return cacheService.getOrLoad(
      CacheKey.of("orders", id.toString()),
      OrderGetResult.class,
      () -> loadFromDatabase(id)
  );
  ```

### 3.2 Cache Avalanche Prevention (TTL Jitter)
- When thousands of cache keys share the exact same TTL (e.g. 10 minutes), they expire simultaneously, creating massive database spikes.
- **Correction:** Configure a jitter ratio (e.g. &plusmn;10% random variation on TTL) in cache properties.

### 3.3 Cache Penetration Defense (Negative Caching)
- When clients repeatedly query non-existent IDs, queries bypass cache and hit the database.
- **Correction:** Store null results with a short TTL (e.g. 60 seconds).

---

## Step 4: Connection Pool & Memory Tuning

### 4.1 HikariCP Connection Pool Sizing
Avoid oversized connection pools that cause database disk thrashing and context switching.
- **Empirical Sizing Formula:**
  $$\text{Pool Size} = (\text{CPU Cores} \times 2) + \text{Effective Spindle / SSD Count}$$
- Typical microservice pool size: 10 to 20 connections.
- Ensure database transactions are kept as short as possible.

### 4.2 Stream Large Payloads Instead of Heap Buffering
- Never load multi-gigabyte or multi-megabyte files into byte arrays (`byte[]`) in memory.
- Use streaming interfaces (`InputStream`, `StreamingResponseBody`, or chunked transfers) to maintain a flat memory profile under high concurrent traffic.

---

## Step 5: Verification & Benchmark Checklist

1. **Re-run Automated Tests:**
   ```bash
   # Gradle:
   ./gradlew test && ./gradlew check

   # Maven:
   ./mvnw test && ./mvnw verify
   ```
2. **Benchmark Comparison:** Compare response latency percentiles (P95, P99) and database query counts before and after the optimization.
3. **Format Code:**
   Ensure changes conform to code style:
   ```bash
   ./gradlew spotlessApply
   ```
