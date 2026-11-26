---
name: database-migration
description: Procedures for planning, authoring, and verifying zero-downtime database schema migrations for PostgreSQL / MySQL in Spring Boot microservices.
---

# Database Migration Skill

Use this skill when designing, adding, or modifying relational database schemas and migration scripts (Flyway or Liquibase) in any Spring Boot microservice.

---

## 1. Migration File Creation Standards

1. **Location:** Migrations reside in:
   `src/main/resources/db/migration/` (for Flyway) or `src/main/resources/db/changelog/` (for Liquibase).
2. **Version Sequencing (Flyway):**
   - Check the existing files to find the current highest version (e.g. `V1`, `V2`, `V3`).
   - The next migration must increment sequentially: `V{next_number}__{descriptive_name}.sql`.
3. **Naming Syntax:**
   - `V{number}__{lowercase_description_with_underscores}.sql`
   - Notice the mandatory **double underscore** `__` between the version and the description.

---

## 2. Zero-Downtime Migration Rules

In production environments with running instances, schema changes must be backward-compatible with the currently running application version.

### 2.1 Adding Columns
- **Rule:** Never add a non-nullable column without a default value.
- **Allowed Pattern:**
  ```sql
  ALTER TABLE orders 
      ADD COLUMN IF NOT EXISTS notes VARCHAR(500) DEFAULT NULL;
  ```

### 2.2 Adding Tables
- Always use `IF NOT EXISTS`.
- Always declare primary keys and foreign key indexes explicitly:
  ```sql
  CREATE TABLE IF NOT EXISTS order_items (
      id UUID NOT NULL,
      order_id UUID NOT NULL,
      product_id UUID NOT NULL,
      quantity INTEGER NOT NULL,
      unit_price NUMERIC(15, 2) NOT NULL,
      created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
      CONSTRAINT pk_order_items PRIMARY KEY (id),
      CONSTRAINT fk_order_items_orders FOREIGN KEY (order_id) 
          REFERENCES orders(id) ON DELETE CASCADE
  );

  CREATE INDEX IF NOT EXISTS idx_order_items_order_id 
      ON order_items(order_id);
  ```

### 2.3 Creating Indexes
- Index all foreign keys and query predicate columns:
  ```sql
  CREATE INDEX IF NOT EXISTS idx_orders_status_created 
      ON orders(status, created_at);
  ```

### 2.4 Destructive Operations (Prohibited in Single Step)
- Renaming or dropping columns requires a multi-phase release:
  1. **Phase 1 (Expand):** Add new column, dual-write in application code.
  2. **Phase 2 (Migrate):** Backfill data from old column to new column.
  3. **Phase 3 (Switch):** Switch application reads to new column.
  4. **Phase 4 (Contract):** Drop old column in a later release.

---

## 3. Synchronizing JPA Entities & Repositories

After authoring the migration script:
1. **Update JPA Entity:**
   - Update `infrastructure/persistence/jpa/entity/*Entity.java`.
   - Add `@Column` mapping matching column type, nullability, and length.
2. **Update Entity Mapper:**
   - Update `infrastructure/persistence/mapper/*EntityMapper.java` to map new fields to/from the Domain Model.
3. **Update Domain Model:**
   - Update `domain/model/*` with corresponding getters, setters, or value objects.

---

## 4. Verification & Testing

1. **Verify Migration Execution:**
   Execute tests to trigger migration against the test database:
   ```bash
   # Gradle:
   ./gradlew test

   # Maven:
   ./mvnw test
   ```
2. **Verify Code Formatting:**
   ```bash
   # Gradle:
   ./gradlew spotlessApply

   # Maven:
   ./mvnw spotless:apply
   ```
3. **Rollback Script Preparation:**
   Document an inverse rollback SQL script in the pull request or task summary for operations teams in case a rollback is required.
