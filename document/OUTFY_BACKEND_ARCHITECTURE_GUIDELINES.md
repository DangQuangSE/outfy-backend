# OUTFY Backend Architecture & Coding Guidelines

## 1. Purpose of This Document

This document defines the **backend architecture**, **coding rules**, and **implementation principles** for the OUTFY backend project.

Its purpose is to ensure that:

- developers write code consistently;
- AI coding assistants (such as Augment) follow the same architecture;
- the codebase remains easy to maintain, test, extend, and refactor;
- demo code can be reused later for a real production system.

This document must be treated as a **project rulebook**. Any generated code should follow it unless there is a clearly documented reason not to.

---

## 2. Architecture Decision

### 2.1 Chosen Architecture

The project will use:

**Modular Monolith + Package by Feature + Clean Architecture Lite**

This means:

- the system is implemented as **one Spring Boot application**;
- code is organized by **business feature/module**, not by global technical layers only;
- each module keeps a clean separation of concerns;
- external AI services, storage services, and third-party integrations are isolated behind gateway/client abstractions;
- the architecture is lightweight enough for fast MVP/demo development, while still scalable for future production growth.

### 2.2 Why This Architecture

This architecture is chosen because the system:

- has multiple clear business domains such as auth, users, body profile, clothing, try-on, recommendation, wardrobe, orders, and admin;
- must be built quickly for demo purposes;
- still needs to preserve backend code for later web/mobile/app expansion;
- will likely use mock AI services first, then replace them with real integrations later;
- should avoid the operational complexity of microservices in the early stage.

### 2.3 Architecture Goals

The architecture must support:

- fast delivery for MVP/demo;
- clean separation between business logic and infrastructure;
- easy replacement of mock services with real services;
- stable API design for future mobile app reuse;
- maintainable and readable code for both humans and AI tools.

---

## 3. High-Level Architectural Style

### 3.1 Monolith First

The backend will start as a **single deployable Spring Boot application**.

Do **not** split into microservices at the beginning.

Reasons:

- simpler deployment;
- simpler debugging;
- faster development;
- easier local setup;
- better fit for MVP/demo stage.

### 3.2 Modular Organization

Even though the application is one deployable unit, the source code must be divided into clearly separated modules.

Each module represents a business capability.

Examples:

- auth
- user
- bodyprofile
- clothing
- tryon
- recommendation
- wardrobe
- order
- admin

### 3.3 Clean Architecture Lite

Within each module, code should follow a simplified clean architecture approach:

- **controller**: handles HTTP requests/responses only;
- **service**: contains business logic and orchestration;
- **repository**: handles database access;
- **gateway/client**: communicates with external services;
- **dto**: request/response contracts;
- **entity**: persistence models;
- **mapper**: converts between entity and DTO when needed.

This is called “clean architecture lite” because we preserve the separation of concerns without overcomplicating the project with too many Maven modules too early.

---

## 4. Project Structure

## 4.1 Root Package

Base package example:

```text
com.outfy.outfy_backend
```

## 4.2 Recommended Folder Structure

```text
src/main/java/com/outfy/outfy_backend
├── common
│   ├── config
│   ├── exception
│   ├── response
│   ├── util
│   ├── constant
│   └── mapper
├── infrastructure
│   ├── security
│   ├── storage
│   ├── external
│   └── persistence
├── modules
│   ├── auth
│   ├── user
│   ├── bodyprofile
│   ├── clothing
│   ├── tryon
│   ├── recommendation
│   ├── wardrobe
│   ├── order
│   └── admin
└── OutfyBackendApplication.java
```

## 4.3 Internal Module Structure

Each module should follow a similar internal structure.

Example:

```text
modules/bodyprofile
├── controller
├── dto
│   ├── request
│   └── response
├── entity
├── mapper
├── repository
├── service
├── gateway
└── enums
```

Optional subfolders can be added if a module grows, such as:

- `validator`
- `specification`
- `facade`
- `scheduler`
- `event`

---

## 5. Layer Responsibilities

## 5.1 Controller Layer

Controllers are responsible for:

- receiving HTTP requests;
- validating request input through DTO annotations;
- calling service methods;
- returning standardized API responses.

Controllers must **not**:

- contain business logic;
- contain database logic;
- construct complex domain rules;
- call repositories directly unless there is an exceptional and documented reason.

## 5.2 Service Layer

Services are responsible for:

- business rules;
- use-case orchestration;
- transaction boundaries where appropriate;
- coordination between repositories and external gateways;
- maintaining domain behavior.

Services should be the primary place for business decisions.

## 5.3 Repository Layer

Repositories are responsible for:

- persistence access;
- querying and storing entities;
- encapsulating database operations.

Repositories must **not**:

- contain business workflow logic;
- call external services;
- return API responses directly.

## 5.4 Gateway / Client Layer

Gateways or clients are responsible for:

- communication with external AI services;
- calling cloud storage services;
- integrating with third-party APIs;
- hiding infrastructure details from business logic.

All external integrations must go through gateway/client abstractions.

## 5.5 DTO Layer

DTOs are responsible for:

- defining request contracts;
- defining response contracts;
- separating API shape from database shape.

Entities must not be exposed directly in public APIs unless there is a very strong reason.

## 5.6 Entity Layer

Entities represent database persistence models.

Entities should focus on persistence state and minimal lifecycle support.

Do not overload entity classes with controller concerns or API formatting logic.

---

## 6. Dependency Rules

The following dependency direction must be respected:

- controller -> service
- service -> repository
- service -> gateway/client
- repository -> entity
- mapper -> dto/entity

Avoid reverse dependencies.

### Forbidden examples

- repository calling service
- controller calling repository directly for regular business flows
- entity depending on controller or DTO
- service depending on concrete external implementation when an abstraction is more appropriate

---

## 7. Package-by-Feature Rule

The project must use **package by feature**, not only package by technical layer for the whole application.

### Preferred

```text
modules/bodyprofile/controller
modules/bodyprofile/service
modules/bodyprofile/repository
```

### Avoid

```text
controller/bodyprofile
controller/clothing
service/bodyprofile
service/clothing
repository/bodyprofile
repository/clothing
```

The preferred approach keeps each domain cohesive and easier for both developers and AI tools to understand.

---

## 8. SOLID Principles

All code should follow SOLID where practical.

## 8.1 Single Responsibility Principle

Each class should have one main reason to change.

Examples:

- a controller should handle HTTP interactions only;
- a mapper should only map;
- a service should handle business logic;
- a gateway should only deal with external integration details.

Avoid “god classes” that do everything.

## 8.2 Open/Closed Principle

Code should be open for extension but closed for unnecessary modification.

Use abstraction when future replacements are expected.

Example:

- define `TryOnGateway` interface;
- implement `MockTryOnGateway` first;
- replace later with `RealTryOnGateway` without rewriting service logic.

## 8.3 Liskov Substitution Principle

Subtypes must be safely replaceable for their abstractions.

If a service depends on `RecommendationGateway`, any implementation of that gateway should behave correctly according to the same contract.

## 8.4 Interface Segregation Principle

Prefer small, focused interfaces over large, bloated interfaces.

Do not create a single giant “AIService” interface that handles body generation, try-on, recommendation, clothing classification, and image storage all together.

Instead, split by responsibility.

## 8.5 Dependency Inversion Principle

Business logic should depend on abstractions, not low-level concrete details.

Examples:

- service depends on `BodyGenerationGateway`, not directly on a specific HTTP client class;
- service depends on repository interfaces provided by Spring Data, not database implementation details.

---

## 9. Other Core Design Principles

## 9.1 Separation of Concerns

Keep concerns separated clearly:

- API contracts
- business rules
- persistence
- third-party integration
- mapping
- validation

## 9.2 DRY (Don’t Repeat Yourself)

Avoid duplicating:

- validation logic;
- mapping logic;
- repeated response wrapper logic;
- repeated exception-handling patterns.

However, do not over-abstract too early.

## 9.3 KISS (Keep It Simple, Stupid)

Prefer simple, readable solutions.

Do not introduce design patterns unless they solve a real need.

## 9.4 YAGNI (You Aren’t Gonna Need It)

Do not implement speculative complexity.

If a feature is not needed now, do not add complicated abstractions for imaginary future cases.

Use architecture that is ready for growth, but do not overbuild.

## 9.5 Composition Over Inheritance

Prefer composing services and helpers over deep inheritance trees.

Inheritance should be used carefully and only when it expresses a real “is-a” relationship.

---

## 10. Rules for External AI / Mock Integration

This project will likely use mock implementations first for demo.

To support future replacement, follow these rules:

### 10.1 Use Gateway Interfaces

Example:

```java
public interface BodyGenerationGateway {
    BodyGenerationResult generate(BodyProfile profile);
}
```

### 10.2 Keep Mock Logic Out of Controllers

Controllers must not contain fake/demo logic.

Bad:

```java
if (demoMode) {
    return fakeResponse;
}
```

Good:

- controller calls service;
- service uses gateway;
- gateway implementation can be mock or real.

### 10.3 One Responsibility per Gateway

Examples:

- `BodyGenerationGateway`
- `TryOnGateway`
- `RecommendationGateway`
- `StorageGateway`

Do not merge unrelated responsibilities into one giant external service class.

### 10.4 Real Implementations Must Preserve the Contract

When replacing mock services with real integrations, the interface contract must stay stable.

---

## 11. API Design Rules

## 11.1 RESTful Style

Use RESTful endpoint naming conventions.

Examples:

- `POST /api/v1/body-profiles`
- `GET /api/v1/body-profiles/{id}`
- `POST /api/v1/tryons`
- `GET /api/v1/recommendations/{userId}`

## 11.2 Versioning

All public APIs should start with version prefix:

```text
/api/v1
```

## 11.3 DTO Only

Request and response payloads should use DTOs.

Do not expose entity objects directly.

## 11.4 Standardized Response Format

Use a common response structure when appropriate.

Suggested example:

```json
{
  "success": true,
  "message": "Body profile created successfully",
  "data": {
    "id": 1
  }
}
```

## 11.5 Validation

Use Jakarta Validation annotations on request DTOs.

Examples:

- `@NotNull`
- `@NotBlank`
- `@Positive`
- `@Email`
- `@Size`

Validation should happen at the boundary.

---

## 12. Error Handling Rules

## 12.1 Global Exception Handling

Use centralized exception handling with `@RestControllerAdvice`.

## 12.2 Custom Exceptions

Use meaningful custom exceptions for business failures.

Examples:

- `ResourceNotFoundException`
- `BusinessRuleViolationException`
- `InvalidTryOnRequestException`

## 12.3 Clear Error Messages

Error responses should be:

- readable;
- consistent;
- not overly technical for API consumers;
- still useful for debugging.

## 12.4 Never Leak Sensitive Internal Information

Do not expose stack traces or sensitive internal details in production responses.

---

## 13. Database & Persistence Rules

## 13.1 Entity Naming

Use clear entity names aligned with business meaning.

Examples:

- `User`
- `BodyProfile`
- `ClothingItem`
- `TryOnSession`
- `RecommendationResult`

## 13.2 Repository Naming

Repositories should end with `Repository`.

Examples:

- `BodyProfileRepository`
- `TryOnSessionRepository`

## 13.3 Transaction Management

Use `@Transactional` at service level where needed.

Do not scatter transactions randomly.

## 13.4 Avoid Business Logic in JPA Entities

Entities should not become giant business engines.

Keep them focused on persistence and simple lifecycle logic.

## 13.5 Prefer Explicit Queries When Needed

If query logic becomes complex, use explicit repository query methods or query specifications instead of writing unclear code.

---

## 14. Mapping Rules

## 14.1 Use Mappers

Use mapper classes or mapping tools to convert between:

- request DTO -> entity
- entity -> response DTO

## 14.2 Do Not Pollute Controllers With Mapping Logic

Simple mapping may be acceptable briefly, but mapping should generally live in mapper/helper classes for maintainability.

## 14.3 Keep Mappers Focused

A mapper should map. It should not fetch data from repositories or call external services.

---

## 15. Naming Conventions

## 15.1 Class Names

Use clear business-oriented names.

Good examples:

- `BodyProfileController`
- `BodyProfileService`
- `BodyProfileRepository`
- `CreateBodyProfileRequest`
- `BodyProfileResponse`
- `MockTryOnGateway`

## 15.2 Method Names

Method names should express intent clearly.

Good examples:

- `createBodyProfile`
- `getBodyProfileById`
- `generateAvatarPreview`
- `recommendOutfitsForUser`

Avoid vague names like:

- `process`
- `handle`
- `doTask`

unless context makes them truly clear.

## 15.3 Variable Names

Use descriptive names.

Avoid cryptic abbreviations unless industry-standard and obvious.

---

## 16. Code Style Rules

## 16.1 Readability First

Code should optimize for readability and maintainability.

## 16.2 Small Methods

Methods should generally be small and focused.

If a method is too long, split it.

## 16.3 Small Classes

Classes should not accumulate too many unrelated responsibilities.

## 16.4 Avoid Deep Nesting

Prefer guard clauses and clear flow over deeply nested code.

## 16.5 Avoid Magic Numbers and Magic Strings

Extract constants where appropriate.

## 16.6 Use Final Where Reasonable

Use `final` for method parameters and local variables when it improves clarity and immutability discipline.

## 16.7 Avoid Premature Optimization

Write correct and clear code first.

---

## 17. Logging Rules

## 17.1 Use Structured, Meaningful Logs

Log important business and technical events.

Examples:

- request accepted for try-on generation;
- external AI request failed;
- recommendation result created;
- storage upload succeeded.

## 17.2 Do Not Log Sensitive Data

Do not log:

- raw passwords;
- tokens;
- sensitive personal data;
- private image URLs if restricted.

## 17.3 Error Logs Must Be Useful

Log enough context to debug issues without leaking sensitive data.

---

## 18. Security Rules

## 18.1 Security Must Be Centralized

Authentication and authorization logic should be placed in dedicated security/config areas, not scattered throughout the system.

## 18.2 Never Hardcode Secrets

Do not hardcode:

- passwords;
- tokens;
- API keys;
- storage credentials.

Use environment variables or secure config.

## 18.3 Validate Input Strictly

All external input must be treated as untrusted.

## 18.4 Principle of Least Privilege

Give only required access to users, services, and integrations.

---

## 19. Testing Rules

## 19.1 Critical Business Logic Should Be Testable

Business logic should be written so it can be unit tested.

## 19.2 Prefer Testing Services Over Controllers First

Core behavior should be validated at service level.

## 19.3 Mock External Integrations in Tests

Do not depend on real external AI services in unit tests.

## 19.4 Separate Test Types

Use appropriate test categories:

- unit tests for business logic;
- integration tests for repository/API behavior when needed.

---

## 20. Rules for AI Coding Assistants

This section is written specifically for AI tools such as Augment.

### 20.1 Follow Existing Structure

Before generating code, inspect the existing project/module structure and continue using the same conventions.

### 20.2 Do Not Invent New Architecture Randomly

Do not introduce:

- unnecessary new layers;
- random utility dumping grounds;
- unrelated design patterns;
- alternate naming styles inconsistent with the project.

### 20.3 Generate Complete, Cohesive Units

When generating code for a feature, prefer generating a complete minimal slice:

- request DTO
- response DTO
- entity
- repository
- service
- controller
- mapper if needed

### 20.4 Keep Controllers Thin

Generated controllers must remain thin and delegate to services.

### 20.5 Preserve Abstractions for External Services

When code touches AI or third-party integrations, generate interfaces/gateways and separate implementations.

### 20.6 Use Consistent Naming

Match naming with current module and project standards.

### 20.7 Avoid Overengineering

Generate code that is clean, maintainable, and simple.

Do not introduce microservices, event buses, CQRS, or advanced patterns unless explicitly requested.

### 20.8 Use DTOs and Validation

Generated API endpoints should use DTOs and validation annotations.

### 20.9 Never Put Mock Logic in Controller

Mock/demo behavior must stay in services or gateway implementations.

### 20.10 Prefer Reusable Business Logic

Generated code should support future mobile app/backend reuse and not be tightly coupled to current demo UI.

---

## 21. Example Module Template

Example module structure for `bodyprofile`:

```text
modules/bodyprofile
├── controller
│   └── BodyProfileController.java
├── dto
│   ├── request
│   │   └── CreateBodyProfileRequest.java
│   └── response
│       └── BodyProfileResponse.java
├── entity
│   └── BodyProfile.java
├── mapper
│   └── BodyProfileMapper.java
├── repository
│   └── BodyProfileRepository.java
├── service
│   └── BodyProfileService.java
├── gateway
│   ├── BodyGenerationGateway.java
│   └── MockBodyGenerationGateway.java
└── enums
```

---

## 22. Non-Negotiable Rules

The following rules are mandatory:

1. Use package by feature.
2. Keep controller thin.
3. Keep business logic in service.
4. Do not expose entities directly in API.
5. Use DTOs for requests/responses.
6. Put external integrations behind gateway/client abstractions.
7. Keep mock/demo logic out of controllers.
8. Follow SOLID where practical.
9. Prefer readable and simple code.
10. Do not introduce unnecessary complexity.
11. Maintain stable API contracts.
12. Write code that can evolve from demo to production.

---

## 23. Final Guidance

This backend should be built with the mindset:

- **fast enough for demo**;
- **clean enough for long-term growth**;
- **simple enough for AI-assisted development**;
- **structured enough for future production scaling**.

When in doubt, choose the solution that best balances:

- clarity;
- maintainability;
- extensibility;
- delivery speed.

The preferred implementation style is:

**simple, modular, clean, and reusable.**

