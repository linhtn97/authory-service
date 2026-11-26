# AI Agent Operational Protocols & Development Governance

## 1. Core Operating Principles

As an AI Software Engineer, you must adhere to the highest standard of engineering discipline. Never make assumptions, never introduce unverified dependencies, and never submit unverified code.

---

## 2. The Five Golden AI Protocols

### Protocol 1: Read Before Write
- **Inspect First:** Before proposing or modifying code, read the target files, existing tests, interfaces, and configurations.
- **Understand Context:** Inspect the project build file (`build.gradle` or `pom.xml`) and version catalogs (`libs.versions.toml`) to verify what dependencies and starters are actually available. Never attempt to use libraries that are not declared.

### Protocol 2: Strict Implementation Sequence
When implementing or modifying features, always follow the Hexagonal dependency flow:
1. **Domain:** Define models, value objects, domain errors, and repository ports.
2. **Application:** Define inbound use case ports (`*UseCase`), outbound ports (`*Port`), application DTOs (`*Command`, `*Result`), and use case implementations (`*Service`).
3. **Infrastructure:** Implement outbound ports, JPA entities (`*Entity`), and repository adapters (`*RepositoryAdapter`).
4. **Representation:** Implement request/response DTOs, validation constraints, REST controller endpoints, consumers, and exception handlers.
5. **Testing:** Write Mockito unit tests for use cases and MockMvc tests for controller endpoints.

### Protocol 3: Surgical Edits & Minimal Diffs
- Modify only the lines of code necessary to accomplish the objective.
- Do not reformat unrelated code blocks.
- Preserve existing comments, variable naming patterns, and design structures unless explicitly instructed to refactor.

### Protocol 4: Mandatory Local Verification
No task is considered complete until automated checks pass locally.
Before concluding a task:
1. Run automated code formatting (`spotlessApply` or `spotless:apply`).
2. Run unit and integration tests (`test`).
3. Run full verification checks (`check` or `verify`).

### Protocol 5: Transparent Communication & Root Cause Explanation
- Clearly explain the architectural rationale behind changes.
- Document any schema modifications or configuration properties added.
- Highlight breaking changes, deprecations, or required environment configurations.

---

## 3. Git Commit Conventions

All commits must adhere to the Conventional Commits specification:
- `feat: <description>` &mdash; A new user-facing capability or use case.
- `fix: <description>` &mdash; A bug fix or defect correction.
- `refactor: <description>` &mdash; Code change that neither fixes a bug nor adds a feature.
- `test: <description>` &mdash; Adding or correcting unit/integration tests.
- `docs: <description>` &mdash; Documentation updates or architectural records.
- `chore: <description>` &mdash; Build configuration, dependency upgrades, or tooling.

### Formatting Rules
- Use imperative mood: `"feat: add presigned upload endpoint"` (NOT `"feat: added..."` or `"feat: adds..."`).
- Keep commit message header under 72 characters.
- Do not terminate summary with a period.
