# Whydah-UserStateService

## Overview
Whydah-UserStateService (USS) is a service within the Whydah ecosystem that manages and tracks user state information. It provides functionality for monitoring user login statuses, handling old and deleted user accounts, and maintaining application state.

## Features
- **User Login Status Tracking**: Monitor and manage user login statuses over time
- **Old User Detection**: Identify and process user accounts that have been inactive for specified periods
- **Deleted User Management**: Maintain records of deleted user accounts
- **Notification System**: Send emails to inactive users via configurable templates
- **Application State Management**: Store and retrieve application-wide state information
- **RESTful API**: Expose user state operations through a well-defined API

## Technology Stack
- **Java 21**: Core programming language
- **Stingray Framework**: Base application framework from Cantara
- **Hibernate ORM**: Object-relational mapping for database operations
- **PostgreSQL/H2**: Database options for persistence
- **FlyWay**: Database migration tool
- **Resilience4j**: Circuit breaker and resilience patterns
- **Hazelcast**: Distributed caching and data grid
- **Logback & SLF4J**: Logging infrastructure
- **Swagger**: API documentation

## Architecture
The application follows a modular architecture with several key components:
- **Repository Layer**: Data access objects for persistence operations
- **Service Layer**: Business logic implementation
- **Resource Layer**: REST API endpoints
- **Entity Layer**: Data models representing the domain
- **Module Layer**: Specialized functional components

## Getting Started

### Prerequisites
- Java 21 JDK
- Maven 3.5+
- PostgreSQL (for production) or H2 (for development)

### Building the Project
```bash
mvn clean install
```

### Running the Service
```bash
java -jar target/UserStateService-1.11.6-SNAPSHOT.jar
```

### Configuration
Configure the application by modifying the following files:
- `src/main/resources/uss/application.properties`: Main configuration file
- `src/main/resources/uss/service-authorization.properties`: Security settings
- `src/main/resources/logback.xml`: Logging configuration

## API Documentation
When the service is running, Swagger documentation is available at:
```
http://localhost:8080/api/docs
```

## Database
The service supports two database engines:
- **H2**: Used for development and testing
- **PostgreSQL**: Recommended for production use

Database migrations are managed through Flyway and located in `src/main/resources/db/migration/`.

## Integration with Whydah
This service is a component of the larger Whydah Identity and Access Management platform. It requires proper configuration to connect to other Whydah components such as SecurityTokenService (STS) and UserIdentityBackend (UIB).

## Development

### Code Structure
- `entity`: Database entity classes
- `exception`: Application exception handling
- `model`: Data transfer objects
- `repository`: Data access layer
- `resource`: API endpoints
- `service`: Business logic
- `settings`: Configuration classes
- `util`: Utility classes

### Testing
Run the tests with:
```bash
mvn test
```

## License
This project is licensed under the Apache License, Version 2.0.

## Links
- [Whydah Wiki](https://wiki.cantara.no/display/iam/Whydah-UserStateService)
- [Cantara GitHub](https://github.com/cantara)
```

This README provides a comprehensive overview of the Whydah-UserStateService project. It includes:

1. A clear introduction to what the service does
2. Key features of the service
3. The technology stack used
4. Architectural overview
5. Instructions for getting started
6. Configuration information
7. API documentation references
8. Database information
9. Integration details with the broader Whydah ecosystem
10. Development information including code structure
11. Testing instructions
12. License information
13. Relevant links

Would you like me to make any adjustments to this README before finalizing it?