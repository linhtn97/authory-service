# Authory Service

Authory Service is a comprehensive microservice built with Spring Boot 4.0.0 and Java 25. It serves as a central authentication, authorization, and organizational structure mapping authority. The service embraces Domain-Driven Design (DDD) principles and a clean architectural model, offering modular features across various bounded contexts.

## Key Features

- **Authentication & Authorization**: Built utilizing Spring Security, JWT (jjwt), and a reactive/MVC hybrid setup.
- **Identity & User Management**: Complete management of Users, Customers, and Employees.
- **Organizational Structure Modeling**: Robust models to manage complex business structures including Companies, Centers, Branches, Divisions, Departments, and internal Groups.
- **Role-Based Access Control (RBAC) & Metadata**: In-depth control over Profiles, Roles, Permissions, Resources, and User Attributes.
- **Hexagonal Architecture**: Divided into distinct modular domains (`features` such as `auth`, `user`, `employee`, `customer`, `metadata`) utilizing `application`, `domain`, `infrastructure`, and `representation` layers to separate concerns effectively.
- **Shared Kernel**: A robust collection of common architectural tools, including abstract CRUD services, unified API error responses, specification builders for dynamic querying, and a custom `DomainException` hierarchy.

## Tech Stack

* **Language**: Java 25
* **Framework**: Spring Boot 4.0.0
* **Data Access**: Spring Data JPA with PostgreSQL
* **Security**: Spring Security & JWT 
* **Validation**: Spring Boot Starter Validation
* **API Paradigm**: Spring WebMVC & WebFlux
* **Object Mapping**: MapStruct (1.6.3)
* **Build Tool**: Gradle with Spotless Plugin for consistent code formatting (Google Java Format)
* **Boilerplate Reduction**: Lombok

## Getting Started

### Prerequisites

* Java 25
* Gradle (or use the provided `gradlew` wrapper)
* PostgreSQL Database

### Configuration

Ensure your `application.yaml` is configured with the correct PostgreSQL database credentials and JWT secret properties. 

### Running Locally

To start the application locally, you can run the following command from the root directory:

```bash
./gradlew bootRun
```

### Formatting Code

The project enforces code formatting with Spotless. If your build fails due to a formatting error, run:

```bash
./gradlew spotlessApply
```

### Testing

To run the unit and integration test suites:

```bash
./gradlew test
```

## Project Structure (Highlights)

```text
src/main/java/com/linhtn/authory_service
├── features/
│   ├── auth/          # Authentication & Token Management
│   ├── customer/      # Customer-specific domains
│   ├── employee/      # Employee-specific domains
│   ├── metadata/      # Org structure, Roles, Permissions, Attributes
│   └── user/          # Core User Entities & Lifecycle
├── infrastructure/    # App-wide configurations (e.g., SecurityConfig)
└── shared_kernel/     # Reusable components, Base Services, Error Handling
```
