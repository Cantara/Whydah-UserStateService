# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Whydah-UserStateService (USS) is a service within the Whydah Identity and Access Management ecosystem. It tracks user login statuses, identifies inactive users, manages deleted user records, and sends notifications to inactive users. The service integrates with other Whydah components (SecurityTokenService/STS and UserAdminService/UAS).

## Build and Run Commands

### Build
```bash
mvn clean install
```

### Run Tests
```bash
mvn test
```

### Run Single Test
```bash
mvn test -Dtest=APIServiceTest
```

### Run the Service
```bash
java -jar target/UserStateService-<version>-SNAPSHOT.jar
```

The service runs on port 8777 by default (configurable in `application.properties`).

## Architecture

### Framework
Built on **Stingray** (Cantara's application framework). The main entry point is `MainApplication` which extends `AbstractStingrayApplication`. Bootstrap happens via:
1. `MainApplication.main()` → `initApp()`
2. `ApplicationFactory` creates the application instance
3. Configuration loaded from classpath and filesystem properties files
4. `doInit()` registers all components (services, repositories, resources)
5. `FlywayService` runs database migrations before `APIService` initialization

### Layered Architecture

**Entity Layer** (`entity/`): JPA entities representing domain models
- `LoginUserStatusEntity` - tracks user login timestamps
- `OldUserEntity` - users marked as inactive
- `DeletedUserEntity` - historical records of deleted users
- `AppStateEntity` - application-wide state and statistics

**Repository Layer** (`repository/`): Data access via Hibernate
- Extends base classes `Repository` or `CRUDRepository` from `util/repository/`
- All repositories provide standard CRUD operations
- Uses HikariCP connection pooling

**Service Layer** (`service/`):
- `APIService` (abstract) / `APIServiceImpl` - main business logic coordinator
- Contains references to all repositories and modules
- Has `testMode` flag to disable scheduled tasks during testing

**Module Layer** (`service/module/`): Specialized functional components
- `ImportUserModule` - periodically fetches users from UAS (every 24 hours)
- `DetectOldUserModule` - identifies users who haven't logged in for the configured threshold (runs daily)
- `NotifyOldUserModule` - sends email notifications to inactive users (runs daily)
- `RemoveOldUserModule` - removes users marked as old after grace period (runs daily)
- `WhydahClientModule` - HTTP client for communicating with Whydah services (STS/UAS)

**Resource Layer** (`resource/`): REST endpoints
- `APIResource` - `/api` endpoints for external integrations (requires access token)
  - `POST /{token}/update` - receive login updates from STS
  - `DELETE /{token}/delete/{uid}` - receive user deletion notifications from UAS
- `UIResource` - `/ui` endpoints for dashboards/reports

### Scheduled Tasks
Three scheduled executors run in production (disabled when `testMode = true`):
1. Import users from UAS every 24 hours
2. Detect old users + send notifications daily
3. Check and remove old users daily

### Database
- **Development**: H2 in-memory database (default config)
- **Production**: PostgreSQL
- **Migrations**: Flyway (migrations in `src/main/resources/db/migration/`)
  - Separate folders for H2 and PostgreSQL-specific migrations
- **ORM**: Hibernate 6.x with HikariCP connection pooling

### Configuration Files
- `src/main/resources/uss/application.properties` - main config (ports, thresholds, Whydah credentials, DB settings)
- `src/main/resources/uss/service-authorization.properties` - security settings
- `src/main/resources/hibernate.properties` - Hibernate/JPA config
- `src/main/resources/logback.xml` - logging configuration

### Email System
Uses FreeMarker templates for email notifications. Email sending happens through UAS (no direct SMTP configuration needed).

### Key Settings (`AppSettings`)
- `THRESHOLD_FOR_DETECTING_OLD_USERS_IN_MONTH` - months of inactivity before marking user as old (default: 23)
- `RECENT_LOGON_PERIOD_IN_DAY` - defines "recent" login window (default: 7)
- `ACCESS_TOKEN` - required for API authentication

## Testing

Tests use JUnit 5. The test setup:
- Calls `MainApplication.initApp()` to bootstrap the full application
- Sets `testMode = true` to disable scheduled tasks
- Uses H2 in-memory database
- Cleans repositories with `deleteAll()` in `@BeforeEach`

To run tests with specific Hibernate settings, the test can override Hibernate properties loaded during application initialization.

## Whydah Integration

This service is part of a larger ecosystem:
- **STS (SecurityTokenService)**: Manages authentication tokens, sends login events to USS
- **UAS (UserAdminService)**: Central user repository, USS fetches users from here and sends deletion requests back

USS acts as a "state tracker" that monitors user activity across the Whydah platform and can trigger actions in UAS (like user deletion).

## Technology Stack
- Java 21
- Maven (shade plugin creates uber-jar)
- Stingray Framework (Cantara)
- Hibernate 6.6.x + HikariCP
- Flyway 10.x
- JAX-RS (Jersey) with Swagger/OpenAPI
- Hazelcast (distributed caching)
- Resilience4j (circuit breakers)
- Lombok (code generation)
- FreeMarker (email templates)

## Common Patterns

### Dependency Injection
Stingray uses a custom DI approach via `init()` methods in `MainApplication.doInit()`. Components are registered and retrieved using `get(Class)`.

### Repository Pattern
All repositories extend `Repository` or `CRUDRepository` base classes. Common operations:
- `insert(entity)` / `update(entity)` / `delete(entity)`
- `findById(id)` returns `Optional<Entity>`
- `deleteAll()` for test cleanup
- Custom query methods like `getNumberOfRecentLogins(LocalDateTime since)`

### Module Pattern
Business logic is organized into modules that can be independently tested. Each module receives `APIService` reference in constructor to access repositories.

## Tracking Learnings and Issues

**IMPORTANT**: As you work in this codebase, document your discoveries below. This creates institutional knowledge for future sessions.

### Instructions for Future Claude Instances

When you discover something important while working in this codebase, add it to the appropriate section below:
- **Gotchas/Issues**: Bugs, edge cases, non-obvious behaviors, or problems you encountered
- **Solutions/Workarounds**: How you fixed issues or worked around limitations
- **Architectural Insights**: Important discoveries about how the system works that aren't obvious from reading individual files
- **Dependencies/Integration Points**: How this service interacts with other Whydah components or external systems

Keep entries concise but informative. Include file references when relevant.

---

### Known Gotchas and Issues

**Lombok Annotation Processing (Fixed)**
- **Issue**: Lombok `@Data` annotations on entity classes weren't generating getters/setters, causing compilation errors
- **Root Cause**: Maven compiler plugin wasn't configured to use Lombok as an annotation processor
- **Fixed In**: pom.xml:456-462

**Hibernate Validator + jboss-logging Version Conflict (Fixed)**
- **Issue**: `NoSuchMethodError` at runtime during tests: `org.jboss.logging.Logger.getMessageLogger(MethodHandles$Lookup...)`
- **Root Cause**: Hibernate Validator 9.1.0 requires jboss-logging 3.6.0+, but resteasy-jaxrs was bringing in 3.4.1.Final
- **Fixed In**: pom.xml:367-372 (explicit dependency on jboss-logging 3.6.1.Final)

### Solutions and Workarounds

**Enabling Lombok Annotation Processing**
- Added `<annotationProcessorPaths>` configuration to maven-compiler-plugin
- Explicitly includes Lombok 1.18.42 as annotation processor
- See pom.xml:456-462

**Resolving Dependency Version Conflicts**
- When you see `NoSuchMethodError` at runtime, check for transitive dependency version conflicts
- Use `mvn dependency:tree -Dincludes=groupId:artifactId` to trace dependency sources
- Add explicit dependency with newer version to override transitive ones

### Architectural Insights

*(Document important discoveries here)*

### Integration Points and Dependencies

*(Document integration details here)*
