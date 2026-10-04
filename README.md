# Leave Management API

Backend REST API for a leave/absence management system, built with Spring Boot. Handles leave requests, leave balances, approvals, calendar events, document uploads, notifications, and user/role management, with Microsoft OAuth2 login and JWT-secured endpoints.

## Tech Stack

- **Java 21** / **Spring Boot 3.2**
- **Spring Security** + **OAuth2 Client** (Microsoft login) + **JWT** (jjwt)
- **Spring Data JPA** + **PostgreSQL**
- **Spring Mail** (notifications) + **Thymeleaf** (email/error templates)
- **SpringDoc OpenAPI** (Swagger UI)
- **JUnit 5** + **Mockito** for testing

## Getting Started

### Prerequisites

- Java 21
- Maven
- PostgreSQL running locally (or accessible instance)
- A Microsoft Entra ID (Azure AD) app registration, for OAuth2 login

### Configuration

Copy the example config and fill in your own values:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Then set your database credentials and Microsoft OAuth2 client ID/secret/tenant ID in that file. It's git-ignored, so your secrets stay local.

### Run

```bash
./mvnw spring-boot:run
```

The app starts on `http://localhost:8083` by default (configurable via `server.port`).

### API Docs

Once running, Swagger UI is available at:

```
http://localhost:8083/swagger-ui.html
```

### Tests

```bash
./mvnw test
```

## Project Structure

```
src/main/java/com/africahr/leave/
├── config/       # Security and app configuration
├── controller/   # REST endpoints
├── dto/          # Request/response DTOs
├── model/        # JPA entities and enums
├── repository/   # Spring Data repositories
├── security/     # OAuth2 / JWT auth components
└── service/      # Business logic
```
