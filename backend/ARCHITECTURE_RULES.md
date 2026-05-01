# Senior Care Backend — Architecture Instructions

## Goal

Rebuild the backend as a modular monolith in Kotlin/Ktor.

The first reference module is `user`. Do not implement other modules until the `user` module architecture is stable.

---

## Stack

- Kotlin 2.1.20 JVM
- Java 21
- Ktor 3.1.3
- kotlinx.serialization
- Arrow 2.1.2
- Arrow Fx Coroutines
- Exposed 0.61.0
- PostgreSQL
- HikariCP
- Flyway
- Kotest
- Testcontainers
- Logback
- OpenAPI Ktor

---

## Architecture

Use a modular monolith with strict layer boundaries.

Dependency direction:

```text
domain <- application <- web/infrastructure <- app
```

Layer responsibilities:

```text
domain:
- pure Kotlin
- entities
- value objects
- domain repository ports
- domain errors
- domain policies
- entity factories
- invariants

application:
- use cases
- commands
- queries
- application DTOs/results
- orchestration
- transaction boundaries
- input validation
- use-case-specific ports

infrastructure:
- technical details
- persistence
- Exposed tables
- SQL repository implementations
- persistence mappers

web:
- Ktor routing
- HTTP request/response handling
- HTTP <-> application mapping
- use case invocation
- HTTP error mapping

contract:
- public API DTOs
- request DTOs
- response DTOs
- error response DTOs
- serialization annotations

app:
- bootstrap
- configuration
- manual dependency wiring
- plugin registration
- route registration

shared:
- shared kernel
- web utilities
- persistence utilities
- security abstractions
- validation primitives
- logging primitives
```

---

## Hard Restrictions

### Domain

Domain must not know:

```text
- Ktor
- Exposed
- kotlinx.serialization
- configuration
- HTTP DTOs
- contract DTOs
- infrastructure
- web
- app
```

Domain is pure Kotlin.

### Application

Application must not know:

```text
- Ktor
- Exposed
- HTTP responses
- HTTP status codes
- contract DTOs
- web layer
- infrastructure implementation details
```

Application returns application-level results, for example:

```text
CreatedUser
UserView
UserListItem
```

Application must not return HTTP DTOs like:

```text
UserResponse
UserListResponse
ErrorResponse
```

### Infrastructure

Infrastructure must not contain business logic.

It may contain:

```text
- Exposed table definitions
- SQL repository implementations
- persistence mappers
- database error translation
```

### Web

Web must not contain business logic.

It may contain:

```text
- routing
- request parsing
- request validation related to HTTP shape
- Request -> Command mapping
- application result -> Contract Response mapping
- application error -> HTTP response mapping
```

### App

App performs manual dependency composition.

No:

```text
- DI frameworks
- service locator
- global singletons
```

---

## Target Project Structure

```text
seniorcare/
└── src/
    ├── main/
    │   └── kotlin/
    │       └── pl/
    │           └── bratosz/
    │               └── seniorcarebackend/
    │                   ├── app/
    │                   │   ├── Application.kt
    │                   │   ├── bootstrap/
    │                   │   │   ├── Routing.kt
    │                   │   │   ├── Plugins.kt
    │                   │   │   └── Modules.kt
    │                   │   └── config/
    │                   │       ├── Env.kt
    │                   │       ├── DatabaseConfig.kt
    │                   │       ├── SecurityConfig.kt
    │                   │       └── SerializationConfig.kt
    │                   │
    │                   ├── shared/
    │                   │   ├── kernel/
    │                   │   │   ├── Result.kt
    │                   │   │   ├── AppError.kt
    │                   │   │   ├── Page.kt
    │                   │   │   ├── Sort.kt
    │                   │   │   └── ClockProvider.kt
    │                   │   ├── logging/
    │                   │   │   ├── Logger.kt
    │                   │   │   └── CorrelationId.kt
    │                   │   ├── security/
    │                   │   │   ├── CurrentUser.kt
    │                   │   │   ├── AuthContext.kt
    │                   │   │   └── Permissions.kt
    │                   │   ├── validation/
    │                   │   │   ├── ValidationError.kt
    │                   │   │   └── Validator.kt
    │                   │   ├── web/
    │                   │   │   ├── ApiResponse.kt
    │                   │   │   ├── ErrorResponse.kt
    │                   │   │   ├── ExceptionMappers.kt
    │                   │   │   └── RequestContext.kt
    │                   │   └── persistence/
    │                   │       ├── TransactionManager.kt
    │                   │       ├── BaseTable.kt
    │                   │       └── DbExtensions.kt
    │                   │
    │                   └── modules/
    │                       ├── user/
    │                       │   ├── domain/
    │                       │   │   ├── User.kt
    │                       │   │   ├── UserId.kt
    │                       │   │   ├── UserEmail.kt
    │                       │   │   ├── UserStatus.kt
    │                       │   │   ├── UserRepository.kt
    │                       │   │   ├── UserErrors.kt
    │                       │   │   └── UserPolicy.kt
    │                       │   │
    │                       │   ├── application/
    │                       │   │   ├── commands/
    │                       │   │   │   ├── CreateUserCommand.kt
    │                       │   │   │   ├── UpdateUserCommand.kt
    │                       │   │   │   └── DeleteUserCommand.kt
    │                       │   │   ├── queries/
    │                       │   │   │   ├── GetUserQuery.kt
    │                       │   │   │   └── ListUsersQuery.kt
    │                       │   │   ├── dto/
    │                       │   │   │   ├── CreatedUser.kt
    │                       │   │   │   ├── UserView.kt
    │                       │   │   │   └── UserListItem.kt
    │                       │   │   ├── handlers/
    │                       │   │   │   ├── CreateUserHandler.kt
    │                       │   │   │   ├── UpdateUserHandler.kt
    │                       │   │   │   ├── DeleteUserHandler.kt
    │                       │   │   │   ├── GetUserHandler.kt
    │                       │   │   │   └── ListUsersHandler.kt
    │                       │   │   ├── errors/
    │                       │   │   │   └── CreateUserError.kt
    │                       │   │   ├── mappers/
    │                       │   │   │   └── UserApplicationMapper.kt
    │                       │   │   └── ports/
    │                       │   │       └── UserIdGenerator.kt
    │                       │   │
    │                       │   ├── infrastructure/
    │                       │   │   └── persistence/
    │                       │   │       ├── UserTable.kt
    │                       │   │       ├── SqlUserRepository.kt
    │                       │   │       └── UserPersistenceMapper.kt
    │                       │   │
    │                       │   ├── web/
    │                       │   │   ├── UserRoutes.kt
    │                       │   │   ├── UserHttpMapper.kt
    │                       │   │   └── UserErrorMapper.kt
    │                       │   │
    │                       │   └── contract/
    │                       │       ├── CreateUserRequest.kt
    │                       │       ├── UpdateUserRequest.kt
    │                       │       ├── UserResponse.kt
    │                       │       ├── UserListResponse.kt
    │                       │       └── UserErrorResponse.kt
    │                       │
    │                       ├── product/
    │                       ├── warehouse/
    │                       ├── stock/
    │                       └── shipment/
    │
    └── test/
        └── kotlin/
            └── pl/
                └── bratosz/
                    └── seniorcarebackend/
                        ├── architecture/
                        │   └── ArchitectureRulesTest.kt
                        ├── modules/
                        │   └── user/
                        │       ├── domain/
                        │       ├── application/
                        │       ├── infrastructure/
                        │       └── web/
                        └── shared/
```

---

## Package Naming

Packages must match directories.

Examples:

```text
pl.bratosz.seniorcarebackend.modules.user.domain
pl.bratosz.seniorcarebackend.modules.user.application
pl.bratosz.seniorcarebackend.modules.user.application.commands
pl.bratosz.seniorcarebackend.modules.user.application.queries
pl.bratosz.seniorcarebackend.modules.user.application.dto
pl.bratosz.seniorcarebackend.modules.user.application.handlers
pl.bratosz.seniorcarebackend.modules.user.application.errors
pl.bratosz.seniorcarebackend.modules.user.application.mappers
pl.bratosz.seniorcarebackend.modules.user.application.ports
pl.bratosz.seniorcarebackend.modules.user.infrastructure.persistence
pl.bratosz.seniorcarebackend.modules.user.web
pl.bratosz.seniorcarebackend.modules.user.contract
pl.bratosz.seniorcarebackend.shared.kernel
pl.bratosz.seniorcarebackend.shared.persistence
pl.bratosz.seniorcarebackend.app.bootstrap
```

---

## Contract Rules

`contract` belongs to the HTTP boundary.

It contains public API DTOs only:

```text
- requests
- responses
- error responses
```

Allowed dependencies:

```text
contract -> kotlinx.serialization
```

Forbidden dependencies:

```text
contract -> domain
contract -> application
contract -> infrastructure
contract -> app
```

Application must never return contract DTOs.

Correct flow:

```text
CreateUserRequest -> CreateUserCommand -> CreatedUser -> UserResponse
```

---

## Application Ports

Use `application/ports` for technical or orchestration dependencies that belong to a use case but are not domain concepts.

Examples:

```text
UserIdGenerator
PasswordHasher
EmailSender
DomainEventPublisher
```

Domain repository ports stay in domain when they represent business access to aggregates.

Example:

```text
domain/UserRepository.kt
```

---

## Shared Rules

Keep `shared` minimal.

Allowed shared concepts:

```text
- AppError
- Page
- Sort
- ClockProvider
- TransactionManager
- RequestContext
- CorrelationId
- generic validation primitives
- generic persistence helpers
```

Do not put module-specific logic in `shared`.

Forbidden examples:

```text
- UserValidation
- UserMapper
- UserPermissions
- UserErrorCodes
- User-specific policies
```

---

## Context Parameters

Context parameters may be used for explicit dependencies.

Use narrow context dependencies.

Good:

```kotlin
context(tx: TransactionManager, clock: ClockProvider)
suspend fun handle(command: CreateUserCommand): Either<CreateUserError, CreatedUser>
```

Avoid broad context objects.

Bad:

```kotlin
context(ctx: UserModuleContext)
suspend fun handle(command: CreateUserCommand): Either<CreateUserError, CreatedUser>
```

Do not turn context parameters into a service locator.

If context parameters are used, configure the required Kotlin compiler flags explicitly.

---

## Transaction Rules

Transaction boundary belongs to the application use case.

Repositories must not open transactions.

Preferred style:

```kotlin
tx.transaction {
    either {
        // use case logic
    }
}
```

Rules:

```text
- business errors are represented by Either
- business errors must not be thrown as exceptions
- rollback happens for technical exceptions
- repository does not manage transaction lifecycle
- use case owns transaction boundary
```

Transaction manager contract:

```kotlin
interface TransactionManager {
    suspend fun <A> transaction(block: suspend () -> A): A
}
```

---

## Error Handling

Use typed errors with Arrow `Either`.

No exceptions for business logic.

### Domain Errors

Domain errors represent domain rules only.

Example:

```kotlin
sealed interface UserError {
    data object InvalidEmail : UserError
    data object InvalidName : UserError
    data object CannotActivateDeletedUser : UserError
}
```

### Application Errors

Application errors may aggregate:

```text
- domain errors
- conflicts
- persistence errors
- safe technical errors
```

Example:

```kotlin
sealed interface CreateUserError : AppError {
    data class Domain(val error: UserError) : CreateUserError
    data class Conflict(val code: String, val message: String) : CreateUserError
    data class Persistence(val code: String, val message: String) : CreateUserError
}
```

### Web Error Mapping

Web maps application errors to HTTP responses.

Example:

```text
CreateUserError.Domain -> 400
CreateUserError.Conflict -> 409
CreateUserError.Persistence -> 500 safe response
```

---

## Validation Rules

Use three validation levels.

### Web / Contract Validation

Responsible for:

```text
- invalid JSON
- missing fields
- malformed request shape
- HTTP-specific parsing
```

### Application Validation

Responsible for:

```text
- command-level validation
- use-case-specific input rules
- cross-field checks
```

### Domain Validation

Responsible for:

```text
- invariants
- value object construction rules
- aggregate consistency
```

Example:

```text
Blank email from HTTP request:
web/application validation

Invalid email value object:
domain error

Email already exists:
application conflict error
```

---

## Mapping Rules

Use explicit mapping at boundaries.

```text
Request -> Command:
web

Command -> Domain/Application:
application

DB row -> Domain:
infrastructure persistence mapper

Domain/Application result -> Contract Response:
web

Application error -> HTTP response:
web
```

Never leak DTOs across the wrong boundary.

---

## Persistence Rules

Repository implements the port from domain.

Exposed exists only in:

```text
modules/user/infrastructure/persistence
shared/persistence
```

Repository responsibilities:

```text
- execute SQL queries
- map rows to domain
- map persistence failures to safe persistence errors if needed
```

Repository must not:

```text
- open transactions
- contain business rules
- return HTTP DTOs
- depend on Ktor
```

---

## OpenAPI Rules

OpenAPI belongs to the HTTP boundary.

Allowed places:

```text
web
contract
app plugin/bootstrap configuration
```

Forbidden places:

```text
domain
application
infrastructure persistence
```

Contract DTOs define the public API shape.

Application remains independent from OpenAPI.

---

## Architecture Tests

Add architecture tests to enforce dependency rules.

Minimum rules:

```text
domain must not depend on:
- io.ktor.*
- org.jetbrains.exposed.*
- kotlinx.serialization.*
- modules.*.web.*
- modules.*.contract.*
- modules.*.infrastructure.*
- app.*

application must not depend on:
- io.ktor.*
- org.jetbrains.exposed.*
- modules.*.web.*
- modules.*.contract.*
- modules.*.infrastructure.*
- app.*

infrastructure must not depend on:
- modules.*.web.*
- modules.*.contract.*

web must not depend on:
- org.jetbrains.exposed.*
```

Architecture tests belong in:

```text
src/test/kotlin/pl/bratosz/seniorcarebackend/architecture/ArchitectureRulesTest.kt
```

---

## Manual Wiring

Manual dependency composition happens in:

```text
app/bootstrap/Modules.kt
```

App creates:

```text
- repositories
- transaction manager
- clock
- use case handlers
- module route dependencies
```

App registers:

```text
- plugins
- routing
- module routes
```

Do not use:

```text
- DI frameworks
- service locator
- global mutable singletons
```

---

## Tests

### Domain Tests

Use pure unit tests.

Test:

```text
- value objects
- entity factories
- invariants
- policies
```

No database, Ktor, or Testcontainers.

### Application Tests

Use:

```text
- fake repository
- fake transaction manager
- fake clock
- fake application ports
```

Test:

```text
- use case orchestration
- transaction boundary
- typed errors
- validation
```

### Infrastructure Tests

Use:

```text
- Testcontainers
- PostgreSQL
- Flyway
- real Exposed repository
```

Test:

```text
- SQL repository behavior
- persistence mapping
- database constraints
```

### Web/API Tests

Use Ktor test engine.

Test:

```text
- routes
- request mapping
- response mapping
- error mapping
- status codes
```

---

## Working Mode

Work one step per answer.

For each step:

```text
1. Show the target file structure for the step.
2. Show the exact files and code.
3. Do not jump to another module.
4. Wait for confirmation before the next step.
```

Do not provide:

```text
- alternatives
- unnecessary theory
- unrelated refactoring
- implementation for other modules
```

Start with:

```text
modules/user
```

First step:

```text
Build the user module skeleton.
```
