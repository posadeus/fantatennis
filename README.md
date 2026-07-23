# FantaTennis

Fantasy tennis league management system. Users build fanta teams from real ATP players, register them in fanta tournaments spanning a range of real tournaments, and earn fanta points based on how their players actually perform on tour.

Real-world results are pulled from public tennis data sources (ATP Tour, Tennis TV, and the four Grand Slam sites), converted into fanta points, and persisted so teams can be ranked.

Built with Spring Boot 3.2 and Kotlin, following domain-driven design with a contract-first API.

## Requirements

- JDK 17
- Maven 3.9+
- MySQL 8 (local instance for the `dev` profile)
- Docker (only needed to run the integration tests, which use Testcontainers)

## Getting started

The application reads its profile-specific configuration from `config/`, which is gitignored because it holds datasource credentials and Google OAuth2 client secrets. You need to create it before the app will start:

```
config/
  application-dev.yml   # local MySQL datasource + OAuth2 client + app.security.admin-email
  application-prd.yml   # production datasource + OAuth2 client + app.security.admin-email
```

Each file needs `spring.datasource.*` (url, username, password, driver-class-name), `spring.security.oauth2.client.registration.google.*` (client-id, client-secret, scope), and `app.security.admin-email`. If you are pointing `dev` at a pre-existing schema that has tables but no `flyway_schema_history`, also set `spring.flyway.baseline-on-migrate: true` — Flyway otherwise refuses to migrate a non-empty schema it has no record of.

Then:

```bash
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The app serves under the context path `/fanta-tennis` (so locally: `http://localhost:8080/fanta-tennis`).

## Commands

```bash
# Build
mvn clean install

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=ClassName

# Run integration tests (classes ending in IT)
mvn test -Dtest="*IT"

# Regenerate OpenAPI models from the YAML specs
mvn generate-sources
```

## Architecture

The code follows a hexagonal layering, under `com.posadeus.fantatennis`:

- **`app/`** — `Application.kt` plus Spring configuration: service, repository, DAO, client and cache beans, and `SecurityConfig` (OAuth2 login via Google).
- **`controller/`** — REST controllers implementing the OpenAPI-generated interfaces.
- **`domain/`** — business logic: models (`Tournament`, `FantaTeam`, `FantaTournament`, `Ranking`, `TournamentResults`, …), service interfaces and implementations, repository interfaces, and domain exceptions.
- **`infrastructure/`** — JDBC DAOs (Spring Data JDBC over MySQL), SQL repository implementations, and HTTP clients for the external tennis data sources.

### Key design decisions

**Contract-first API.** Every REST API is defined as an OpenAPI spec in `src/main/resources/openApi-*.yml`. The OpenAPI Generator Maven plugin generates Kotlin interfaces and model classes into `target/generated-sources/openapi` during `generate-sources`; controllers implement those interfaces. Do not edit generated code — change the YAML spec and regenerate.

**Repository pattern.** The domain defines repository interfaces and `infrastructure/` provides the SQL implementations. DAOs are the lowest-level database access layer; repositories are thin wrappers that compose DAOs, translate DTOs into domain models, and handle errors. Composition logic (for example joining data from several DAOs) belongs in services, not repositories — keep domain logic out of the repository layer.

**Caching.** Caffeine caches sit in front of tournaments, players, fanta teams, fanta tournaments and player points. Every cache is configured declaratively under `caches.caffeine.*` in `application.yml` (expiry in minutes, plus a maximum size) and wired into the caching DAO/repository decorators.

**Database migrations.** Schema is owned by Flyway; migrations live in `src/main/resources/db/migration`. Add a new `V<n>__description.sql` rather than modifying an applied migration.

## APIs

All paths are relative to the `/fanta-tennis` context path.

| API | Endpoints                                                                                                 |
| --- |-----------------------------------------------------------------------------------------------------------|
| Ranking | `GET /players/ranking/{positions}`                                                                        |
| Player | `GET /player/{id}` (not implemented)                                                                      |
| Team | `GET /team/{teamId}`, `POST /team`, `POST /team/{teamId}/players/add`, `POST /team/{teamId}/players/swap` |
| Tournament | `GET /tournament/{tournamentId}`, `GET /tournaments/{year}`                                               |
| FantaTournament | `GET /fanta-tournament/{tournamentId}`, `POST /fanta-tournament`, `GET /fanta-tournaments`                |
| Job | `POST /job/points/{tournamentId}/{year}`, `POST /job/tournaments/{year}`                                  |

The Job API is the ingestion side: it imports the tournaments for a given year and recomputes players' fanta points for a tournament from the external sources.

## External data sources

Each client has its own base URI and timeouts under `client.*` in `application.yml`: ATP Tour, Tennis TV, Wimbledon, US Open, Australian Open, and Roland Garros.

## Security

Authentication is Google OAuth2 login. Note that `SecurityConfig` currently permits all requests — the `authenticated` rule is commented out pending the login flow being finished, so the API is effectively open.

## Testing

- Unit tests use JUnit 5, AssertJ, and MockK.
- Integration tests (suffix `IT`) run against a MySQL Testcontainer, so Docker must be running.
- External HTTP clients are stubbed with WireMock.

## Technology stack

Kotlin 2.1 (JVM target 17) · Spring Boot 3.2 · Spring Data JDBC · Spring Security + OAuth2 · MySQL 8 · Flyway · Caffeine · Apache HttpClient 5 · GSON · OpenAPI Generator 7.9 (`kotlin-spring`) · JUnit 5 / MockK / WireMock / Testcontainers

## License

Copyright © 2026 Marco Berti. All rights reserved.

This source is published for viewing and reference only. No permission is
granted to use, copy, modify, or distribute it, in whole or in part, without
prior written consent.
