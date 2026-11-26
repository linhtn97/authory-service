# Database & Persistence Standards

## 1. Schema & Multi-Tenancy Design

- **Dedicated Service Schema:** In PostgreSQL or MySQL, each microservice operates within its own dedicated database schema (e.g. `order_service`, `file_storage`, `user_management`) to ensure bounded context isolation.
- **Explicit Schema Declaration:** Every JPA entity must declare its table and schema explicitly:
  ```java
  @Entity
  @Table(name = "orders", schema = "${service.database.schema:public}")
  public class OrderEntity { ... }
  ```
- **Timezone:** All timestamps stored in the database must be in UTC (`TIMESTAMP WITH TIME ZONE` or UTC epoch milliseconds).

---

## 2. Versioned Database Migrations (Flyway / Liquibase)

Database schema changes are managed exclusively through migration tools (Flyway or Liquibase). Manual DDL execution in shared or production environments is strictly forbidden.

### 2.1 File Naming & Versioning Rules (Flyway Standard)
- Migrations reside in: `src/main/resources/db/migration/`
- Pattern: `V{version}__{description}.sql`
  - Exactly two underscores between version and description.
  - Description must be lowercase with words separated by underscores.
  - Examples: `V1__init_schema.sql`, `V2__create_orders_table.sql`, `V3__add_version_column.sql`.

### 2.2 Golden Rules for Migration Scripts
1. **Migrations Are Immutable:** Never modify or delete a migration that has been merged or applied in any shared environment. Always create a new sequential version to apply updates.
2. **Schema Setting:** Begin every migration script by setting the target schema:
   ```sql
   SET search_path TO ${schema_name}, public;
   ```
3. **Idempotent DDL:** Use `IF NOT EXISTS` / `IF EXISTS` where supported:
   ```sql
   CREATE TABLE IF NOT EXISTS orders ( ... );
   CREATE INDEX IF NOT EXISTS idx_orders_status ON orders (status);
   ```
4. **Zero-Downtime Safe DDL:**
   - When adding a column to an existing table, allow `NULL` or provide a default value to avoid breaking running application instances.
   - When creating indexes on large tables, evaluate non-blocking index creation (e.g. `CREATE INDEX CONCURRENTLY` in PostgreSQL outside a transaction block).
5. **Expand and Contract (No Destructive Single-Step Operations):** Never `DROP TABLE` or `DROP COLUMN` in the same deployment where application code is being updated. Deprecate first, deploy code that stops referencing it, and drop in a subsequent migration.

---

## 3. JPA Entity Architecture & Optimistic Locking

### 3.1 Entity Structure & Constraints
- JPA Entities are strictly quarantined in `infrastructure/persistence/jpa/entity/`.
- Entities must NEVER be exposed to Application use cases or REST controllers.
- Use surrogate UUIDs or standard BIGINT IDs for aggregate roots:
  ```java
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;
  ```

### 3.2 Mandatory Optimistic Locking (`@Version`)
All mutable aggregate root entities must include an optimistic locking version token to prevent concurrent lost updates:
```java
@Version
@Column(name = "version", nullable = false)
private Long version;
```
- When concurrent updates collide, Hibernate throws `ObjectOptimisticLockingFailureException`.
- The global exception handler must intercept this exception and return **`409 Conflict`** with an error code like `OPTIMISTIC_LOCK_CONFLICT`.

### 3.3 Lombok on Entities
- **ALLOWED:** `@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`.
- **FORBIDDEN:** `@Data`, `@EqualsAndHashCode`. (Generating `hashCode()` on lazy associations causes unexpected database queries or `LazyInitializationException`).

---

## 4. Multi-Instance Scheduler Locking (ShedLock)

For scheduled background tasks running across multiple replicas:
- Jobs must use **ShedLock** backed by a shared JDBC table (`shedlock`):
  ```sql
  CREATE TABLE IF NOT EXISTS shedlock (
      name VARCHAR(64) NOT NULL,
      lock_until TIMESTAMP WITH TIME ZONE NOT NULL,
      locked_at TIMESTAMP WITH TIME ZONE NOT NULL,
      locked_by VARCHAR(255) NOT NULL,
      PRIMARY KEY (name)
  );
  ```
- Annotated job method:
  ```java
  @Scheduled(cron = "0 0 2 * * *")
  @SchedulerLock(
      name = "DailyMaintenanceJob_cleanup",
      lockAtLeastFor = "5m",
      lockAtMostFor = "30m"
  )
  public void runMaintenance() { ... }
  ```
- `usingDbTime()` must be enabled on the lock provider to avoid server clock skew issues.

---

## 5. Connection Pooling & Performance (HikariCP)

Standard connection pool configuration in `application.yaml`:
```yaml
spring:
  datasource:
    hikari:
      maximumPoolSize: 20
      minimumIdle: 5
      idleTimeout: 600000     # 10 minutes
      connectionTimeout: 30000 # 30 seconds
      maxLifetime: 1800000    # 30 minutes
```
- Index all foreign key columns and query filter predicates.
- Keep database transactions short to return connections to the pool rapidly.
- Guard against N+1 queries using fetch joins (`JOIN FETCH`) or entity graphs (`@EntityGraph`).
