# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

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

# Regenerate OpenAPI models from YAML specs
mvn generate-sources

# Run the application
mvn spring-boot:run
```

## Architecture

Fantasy tennis league management system built with Spring Boot + Kotlin, following domain-driven design with a contract-first API approach.

### Layers

- **`app/configuration/`** — Spring Boot configuration: services, repositories, DAOs, external clients, caches, security (OAuth2)
- **`controller/`** — REST controllers implementing OpenAPI-generated interfaces (6 APIs: Ranking, Player, Team, Tournament, Job, FantaTournament)
- **`domain/`** — Business logic, models (Tournament, FantaTeam, Player, TournamentResults), service interfaces, and repository interfaces
- **`infrastructure/`** — JDBC DAOs (MySQL via Spring Data JDBC), SQL repository implementations, and HTTP clients for external tennis data sources (ATP Tour, Tennis TV, Wimbledon, US Open, Australian Open, Roland Garros)

### Key Design Decisions

**Contract-first API**: All REST APIs are defined as OpenAPI YAML specs in `src/main/resources/openApi-*.yml`. The OpenAPI Generator Maven plugin generates Kotlin interfaces and model classes during `generate-sources`. Controllers implement these generated interfaces — do not modify generated code directly.

**Repository pattern**: Domain defines repository interfaces; `infrastructure/` provides SQL implementations. DAOs are the lowest-level database access layer; repositories compose DAOs and apply business logic.

**Caching**: Caffeine cache is configured for tournaments, players, fanta teams, and rankings. Cache beans are defined in configuration classes and injected into DAOs/repositories.

**Testing**: Unit tests use MockK for mocking. Integration tests (suffix `IT`) use Testcontainers with a MySQL container. WireMock is used for mocking external HTTP clients in tests.

### Technology Stack

- Kotlin 1.9, Spring Boot 3.2, Spring Data JDBC, Spring Security + OAuth2
- MySQL (production), H2 + Testcontainers/MySQL (tests)
- Caffeine cache, Apache HttpClient 5, GSON
- OpenAPI Generator 7.9 (kotlin-spring)
