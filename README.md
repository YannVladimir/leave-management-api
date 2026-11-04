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

Then set your database credentials, a JWT signing secret (`app.jwt.secret`, generate one with
`openssl rand -base64 32`), and your Microsoft OAuth2 client ID/secret/tenant ID in that file.
It's git-ignored, so your secrets stay local.

> **Security note:** earlier versions of this repo had a hardcoded JWT signing secret baked
> directly into `JwtService.java` and committed to git history. It has been moved to the
> `app.jwt.secret` config property (no default - the app now fails fast at startup if it isn't
> set), but the old hardcoded value is still visible in this repo's git history. If this project
> is ever deployed with that old key, rotate it immediately - anyone who has seen the history
> can forge valid JWTs with it.

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

All tests are plain JUnit 5 + Mockito unit tests (no database or OAuth2 provider required to run
them). CI (`.github/workflows/ci.yml`) runs `mvn test` on every push/PR against `main`.

## Authorization model

Roles are `USER`, `MANAGER`, and `ADMIN` (see `UserRole`). Access control is enforced with
`@PreAuthorize` on controller methods (method security is enabled via `@EnableMethodSecurity` in
`SecurityConfig`), with a path-based `authenticated()` catch-all in the security filter chain for
everything else. A few examples:

- Approving/rejecting a leave request or viewing a team's requests requires `MANAGER` -
  `LeaveRequestController`. The service layer additionally checks that the caller is the actual
  manager of the employee who filed the request.
- Viewing or adjusting another user's leave balance, triggering monthly accrual, or year-end
  carry-forward requires `ADMIN`/`MANAGER` - `LeaveBalanceController`.
- Holiday management and user role management require `ADMIN`.

(An earlier version of `SecurityConfig` restricted roles using path patterns like
`/api/leave/approve` and `/api/admin/**` that didn't match any real controller route - meaning
those sensitive endpoints had no role restriction at all and were reachable by any authenticated
user. That's been fixed; see the commit history for details.)

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

## Known limitations

- `POST /api/leave/balance/adjust` adjusts the *authenticated caller's own* balance rather than
  taking a target user id. It's restricted to `ADMIN` to prevent self-service balance inflation,
  but as a result only an admin adjusting their own balance can use it as-is. A proper "HR
  corrects an employee's balance" flow would need this endpoint reworked to accept a target user.

## License

MIT - see [LICENSE](LICENSE).
