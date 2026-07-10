---
name: spring-boot-coding
description: >
  Expert Java / Spring Boot applying KISS, SOLID, DRY. Trigger for:
  writing or refactoring Java / Spring Boot code (service, repository, controller,
  JPA entity, DTO), fixing an existing class, implementing a feature. Do not trigger for:
  purely theoretical questions without code (e.g., "difference between @Service and
  @Component"), pseudo-code, non-Spring frameworks (Micronaut, Quarkus, Android),
  or trivial style questions on 2-3 lines.
version: 0.1
---

# Clean Code Java / Spring Boot

---

## Priority hierarchy

In case of tension between two rules, apply them in this order:

| Level | Category | Principle                                                                     |
|---|---|------------------------------------------------------------------------------|
| **P1** | **Security** | Always block in case of a security issue, and fix it, without exception |
| **P2** | **Correctness** | SRP, injection, transactions, validation — the code must be correct           |
| **P3** | **Readability** | Naming, clear structure, consistent abstraction levels                   |
| **P4** | **Optimization** | Fetch strategy, readOnly, pagination, performance                            |

> Example of tension: DRY (merge two similar methods) vs readability (the merge requires boolean parameters and harms clarity) → P3 takes precedence over P4, preserve readability and report it.

---

## Parsimony rule (meta)

**Silently apply best practices. Only comment on a decision if:**
- it falls under **P1 (security)** — always explain the risk,
- it involves a **structural choice** between two valid approaches (e.g., Strategy vs `Map<Type, Handler>`),
- it **warns about over-engineering** requested by the user,
- it **relaxes** a skill rule in favor of a higher priority (e.g., tolerate duplication to preserve readability).

Otherwise: produce clean code directly, without preamble or justification.

---

## Guiding principles

**SRP** — A class that mixes business logic and data access (e.g., `OrderService` calling `entityManager` directly) violates layer separation. Split into service + repository.
**OCP / Strategy** — An `if/else` or `switch` that dispatches behavior based on a type is a candidate for Strategy or `Map<Type, Handler>`. **Structural choice to explain**: Strategy if behaviors are complex or likely to evolve, `Map` if dispatch is simple.
**DRY** — **Rule of 3**: extract only from the 3rd occurrence of an identical block onward. Extracting after only 2 occurrences creates premature abstractions. Exception possible if the duplication is clearly costly to maintain.
**KISS** — For a simple CRUD (≤ 3 entities, no complex business rules), **warn** if the user asks for hexagonal architecture, CQRS, or Event Sourcing. Propose the minimum sufficient layer.
**Testability** — A class that instantiates its dependencies with `new` or accesses non-mockable statics is strongly coupled. Inject via constructor.

You do not create tests, and you do not take existing tests into account nor modify them.

---

## Structural rules (P2)

| Topic                            | ❌ NEVER                                                     | ✅ ALWAYS                                                                                          |
|----------------------------------|-------------------------------------------------------------|---------------------------------------------------------------------------------------------------|
| Optionals                        | `.isPresent()` + `.get()`                                   | `.orElseThrow(() -> new OrderNotFoundException(id))`                                              |
| DTOs                             | Expose a JPA entity (@Entity) as controller input or output | Dedicated DTO per use case (record in Java 17+), explicitly mapped from/to the entity             |      
| Mapping | Verbose and scattered manual mapping                        | MapStruct/ModelMapper for non-trivial mappings, mapper isolated in a dedicated class              |
| Validation                       | Manual validation in the service                            | `@Valid` in the controller, validation groups if needed                                           |
| Exceptions                       | `new RuntimeException("error")`                             | One exception per business case (`OrderNotFoundException`), translated by `@RestControllerAdvice` |
| `@Transactional` scope          | Annotation on the entire class                              | Write methods only; reads: `@Transactional(readOnly = true)`                                      |
| `@Transactional` self-invocation | Internal `this.method()` bypasses the proxy                 | Extract into another bean                                                                         |
| Fetch                            | `FetchType.EAGER` on `@OneToMany` / `@ManyToMany`           | `LAZY` + `JOIN FETCH` or `@EntityGraph` to avoid N+1                                              |

---

## Readability rules (P3)

| Topic | ❌ NEVER                                          | ✅ ALWAYS                                                           |
|---|--------------------------------------------------|--------------------------------------------------------------------|
| Sensitive data | Logging email, token, password, PII              | Mask or remove (see P1 security)                                   |
| Structure | Catch-all `Utils` class                          | Static method on the relevant class or targeted helper             |
| Conditional flow | Nested `if/else` > 2 levels                      | Early return, extract method, polymorphism                         |
| Method size | Long method **and** mixing several abstraction levels | Decompose into private methods named at the same abstraction level |
| Null safety | Return `null` from a public method               | `Optional<T>` as return value, never as parameter                  |

---

## Security (P1 — systematically fix + explain)

| ⚠️ Detection                                                             | ✅ Action                                                                          |
|--------------------------------------------------------------------------|-----------------------------------------------------------------------------------|
| JPQL / SQL built by `String` concatenation                               | `@Query` + named `@Param`. Explain the injection risk                             |
| Raw stacktrace or exception message in the HTTP response                 | `@RestControllerAdvice` + generic message. Explain the information leak           |
| Sensitive data (email, token, PII) in a `log.*`                          | Remove or mask. Explain the GDPR / confidentiality risk                           |
| Endpoint without `@PreAuthorize` or `SecurityFilterChain` rule           | Block, propose the minimal annotation. Explain the exposure                       |
| Hardcoded password or secret in the code                                 | Externalize (`@Value`, environment variables, vault). Explain leakage through VCS |
| Deserialization of unconstrained input (`Object`, `Map<String, Object>`) | Typed DTO + validation. Explain the object injection risk                         |

## Out of scope

Tests, Events / Outbox pattern, CQRS, Event Sourcing, strict hexagonal architecture: out of scope for this skill. If the need legitimately emerges (complex domain, strong consistency constraints), report it.