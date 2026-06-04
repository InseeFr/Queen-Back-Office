# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build, run, test

Multi-module Maven project. Java 25 / Spring Boot 3.5.x (the pom is authoritative; the README's "Java 21" is outdated). Use the Maven wrapper (`./mvnw` on Unix, `mvnw.cmd` on Windows).

- Full build with unit + integration tests: `./mvnw clean install`
- Build skipping tests: `./mvnw clean install -DskipTests`
- Run the API locally: `./mvnw -pl queen-application spring-boot:run` (or `cd queen-application && ../mvnw spring-boot:run`)
- Run a single unit test: `./mvnw -pl queen-application -Dtest=CampaignServiceTest test`
- Run a single integration test: `./mvnw -pl queen-application -Dit.test=CampaignIT -DfailIfNoTests=false verify`
- Aggregated coverage report: `./mvnw -P coverage verify` → open `coverage-report/target/site/jacoco-aggregate/index.html`
- Liquibase diff: from `queen-infra-db`, `../mvnw liquibase:diff` (edit properties in its pom first)

Test conventions (configured in the parent pom):
- `*Test.java` → run by surefire as unit tests.
- `*IT.java` → run by failsafe as integration tests; these boot a Spring context and need PostgreSQL.
- Integration tests pick up `application-test.yml` (and variants `application-test-cipher.yml`, `application-test-demo.yml`) under `queen-application/src/test/resources`. The default test datasource expects Postgres on `localhost:5434`. `queen-application/compose.yml` defines `queen-db` / `queen-db-ciphered` / `keycloak` services for this — start with `docker compose --profile queen-db up -d` (or `--profile all`) from `queen-application/`. Env vars (`QUEEN_DB`, `QUEEN_DB_USER`, ...) come from `.env` files alongside the compose file.

Swagger UI when running locally: `http://localhost:8080/swagger-ui/index.html` (requires `feature.swagger.enabled=true`).

## Architecture

The codebase follows **hexagonal / ports-and-adapters** layered by Maven modules. Dependencies point inward toward `queen-domain`.

```
queen-application (Spring Boot app: REST controllers, DTOs, security, config)
        │
        ├── queen-domain-depositproof ──► queen-infra-depositproof   (PDF generation)
        ├── queen-domain-pilotage     ──► queen-infra-pilotage       (habilitation REST client)
        └── queen-domain ◄────────────── queen-infra-db              (JPA/Postgres)
queen-listener-jms (separate Spring Boot app reusing queen-domain + queen-infra-db)
```

Key rule: `queen-domain/pom.xml` has a `maven-enforcer-plugin` `bannedDependencies` rule that **forbids any `fr.insee.queen:*` dependency**. Domain is pure business logic — never add another Queen module to it. Per-domain `gateway/` packages define the ports (interfaces); `queen-infra-*` modules contain the adapters.

Inside `queen-domain` business code, the convention is:
- `model/` — domain types (records / POJOs, no JPA).
- `gateway/` — repository / external-port interfaces, implemented by infra modules.
- `service/` — orchestration. Pairs like `CampaignService` (impl) + `CampaignApiService` (interface exposed to the application layer) are common; honor that split when adding new services.

The application module is package-structured by bounded context (`campaign`, `interrogation`, `paradata`, `pilotage`, `depositproof`, `integration`, `interrogationtempzone`), each with `controller/`, `dto/`, and optional `component/` (DTO ↔ domain converters). The Spring Boot entry point is `fr.insee.queen.QueenApplication` (`scanBasePackages = "fr.insee.queen"`, with `@ConfigurationPropertiesScan`).

Two `queen-domain-registre` / `queen-infra-registre` directories exist but are **not** wired into the parent pom's `<modules>` — they only contain stale `target/` artifacts. Don't treat them as live modules.

### Cross-cutting configuration

- **Security**: dual config under `queen-application/src/main/java/fr/insee/queen/application/configuration/auth/`. `OidcSecurityConfiguration` is active when `feature.oidc.enabled=true` (JWT resource server, role mapping driven by `application.roles.*` and `feature.oidc.role-claim`); `NoAuthSecurityConfiguration` runs when OIDC is off and grants admin to all callers — intended for local dev only. `AuthorityPrivileges` / `AuthorityRoleEnum` define the role → privileges mapping consumed by `@PreAuthorize`.
- **Feature flags** in `application.yml` gate whole bounded contexts: `feature.oidc`, `feature.swagger`, `feature.pilotage`, `feature.cache`, `feature.comments`, `feature.interviewer-mode`, `feature.sensitive-data` (the last enables field-level encryption on persisted data; see `application-test-cipher.yml`). When adding endpoints or wiring, check whether they should be conditional on one of these.
- **Persistence**: PostgreSQL via Spring Data JPA. Schema is managed entirely by **Liquibase** (`queen-infra-db/src/main/resources/db/master.xml`, contexts `prod`); `spring.jpa.hibernate.ddl-auto=validate` — never let Hibernate auto-generate schema. Add migrations as new changesets under `db/changelog/`.
- **Caching**: Caffeine, configured in `queen-application/.../configuration/cache/CacheConfig.java`, gated by `feature.cache.enabled`.
- **External calls**: REST clients to the Pilotage API use the interceptors in `configuration/rest/` (`RestTemplateTokenInterceptor` forwards the current user's JWT).

### queen-listener-jms

Separate Spring Boot application (`JMSApplication`) that consumes from Artemis and reuses `queen-domain` + `queen-infra-db`. It has its own `Dockerfile` and `compose.yml`. Don't pull application-layer dependencies into it.

## Conventions and tooling

- **Commits**: pre-commit hook enforces **Conventional Commits** (`feat:`, `fix:`, `chore:`, `ci:`, ...). See `.pre-commit-config.yaml`. Pre-push hook runs `trivy fs --scanners vuln,secret --severity HIGH,CRITICAL`.
- **Lombok** is the project standard for boilerplate (`@Slf4j`, `@RequiredArgsConstructor`, `@Getter`, etc.); annotation processing is wired in the parent pom.
- **Sonar coverage exclusions**: `queen-domain` is excluded from coverage reporting on purpose — coverage is measured on the adapters and controllers that exercise it.
- **CI** (`.github/workflows/`): `create-snapshot.yml` / `create-rc.yml` / `create-release.yml` drive the release flow; `sonar.yml` runs SonarCloud analysis; `trivy.yml` runs vulnerability scans.
