---
name: spring-boot-unit-testing
description: >
  Expert in Spring Boot unit testing using JUnit 5, Mockito and AssertJ.
  Trigger for: creating, reviewing, refactoring or improving unit tests,
  validating testing practices, detecting test anti-patterns, improving
  readability, isolation and maintainability of tests. Do not trigger for:
  integration tests, end-to-end tests, performance tests, load tests,
  Spring Boot application code implementation without test concerns.
version: 0.2
---

# Clean Unit Testing with Spring Boot

---

## Priority hierarchy

In case of tension between two rules, apply them in this order:

| Level | Category | Principle |
|---|---|---|
| **P1** | **Correctness** | Tests must verify business behavior, not implementation details |
| **P2** | **Reliability** | Tests must be deterministic, isolated and trustworthy |
| **P3** | **Readability** | Tests must clearly communicate intent |
| **P4** | **Maintainability** | Minimize duplication while preserving clarity |

> Example of tension: extracting a helper that hides the data **relevant to the
> test** removes duplication but obscures intent → P3 takes precedence over P4.
> Conversely, a well-named fixture builder (`anOrderWith(...)`) serves both P3
> and P4 and should be preferred over verbose inline setup.

---

## Parsimony rule (meta)

**Silently apply best practices. Only comment on a decision if:**
- it impacts test reliability,
- it requires a structural testing choice,
- it detects an anti-pattern,
- it relaxes a skill rule in favor of a higher priority.

Otherwise: produce clean tests directly, without justification.

---

## Guiding principles

**Given-When-Then pattern** — Every test must clearly separate Given, When and
Then sections (equivalent to Arrange-Act-Assert). Use one vocabulary
consistently within a test.

**Display names** — Use `@DisplayName` to describe the expected business
behavior in plain English.

**Behavior over implementation** — Verify observable behavior, not internal
method calls, unless collaboration verification is the purpose of the test.

**Determinism** — Tests must produce the same result regardless of execution
order, machine, timezone or current date.

**Isolation** — A unit test must test a single unit. External systems,
databases, APIs and frameworks must be mocked or replaced with test doubles.

**Readability** — A test should explain the business rule being verified.

**Parameterized tests** — Use parameterized tests when multiple inputs verify
the same behavior.

---

## Structural rules (P1)

| Topic | ❌ NEVER | ✅ ALWAYS |
|---------|---------|-----------|
| Assertions | Multiple unrelated assertions in one test | Verify a single business behavior |
| Mocking | Mock every dependency by default | Mock only external collaborators |
| Exception testing | try/catch assertions | `assertThatThrownBy()` or `assertThrows()` |
| Test data | Magic values scattered in tests | Explicit and meaningful test data |
| Verification | Verify implementation details | Verify observable outcomes |
| Parameterized tests | Duplicate tests for input variations | Use `@ParameterizedTest` |
| Time handling | Depend on current system time | Inject `Clock` or fixed time source |
| Randomness | Use uncontrolled random values | Use deterministic fixtures |

---

## Readability rules (P3)

| Topic | ❌ NEVER | ✅ ALWAYS |
|---------|---------|-----------|
| Naming | `test1()`, `shouldWork()` | `should_return_order_when_id_exists()` |
| Display name | Missing or technical descriptions | Use `@DisplayName` with business language |
| Structure | Mixed Given/When/Then | Clear Given-When-Then sections |
| Assertions | Generic JUnit assertions everywhere | Fluent AssertJ assertions |
| Test size | Large scenario with multiple concerns | One behavior per test |
| Comments | Explain obvious code | Use expressive names instead |

---

## Mockito rules (P1)

| Topic | ❌ NEVER | ✅ ALWAYS |
|---------|---------|-----------|
| Redundant verification | `verify()` on a stub already constrained by `when/thenReturn` | Let the stub assert the interaction; verify only unstubbed side effects |
| Argument matching | Blanket `any()` that hides argument bugs | Match exact expected arguments; use `ArgumentCaptor` when needed |
| Unnecessary stubs | Stubs unused by the test path (triggers `UnnecessaryStubbingException`) | Keep only the stubs the scenario exercises |
| Over-verification | Asserting every collaborator call | Verify only the interactions that carry business meaning |
| Strictness | Relaxing strictness to silence warnings | Keep `MockitoExtension` strict; fix the root cause |

---

## `@Nested` usage

Use `@Nested` to group tests that share a **business context or precondition**
(e.g. "when the order is already paid"), not to group by method name. Each
nested class gets a `@DisplayName` describing that context. Avoid nesting deeper
than two levels — it hurts readability more than it helps.

---

## Anti-pattern detection

| ⚠️ Detection | ✅ Action |
|-------------|----------|
| Sleep (`Thread.sleep`) in tests | Replace with deterministic synchronization |
| Assertions on implementation details | Assert business outcomes |
| Excessive mocking | Reduce mocks to true collaborators |
| Redundant `verify()` after stubbing | Remove; the stub already constrains the call |
| Unused stubs / over-broad `any()` | Tighten or remove |
| Shared mutable state between tests | Create independent fixtures |
| Hidden test setup | Make setup explicit |
| Multiple behaviors in one test | Split into focused tests |
| Copy-pasted tests | Consider parameterized tests |
| Random or time-dependent failures | Make execution deterministic |

---

## Preferred stack

- JUnit 5
- AssertJ
- Mockito
- `@ParameterizedTest`
- `@Nested` for scenario grouping
- `@ExtendWith(MockitoExtension.class)`

## Out of scope

Integration tests, Spring context tests (`@SpringBootTest`), contract tests,
performance tests, load tests, end-to-end tests and UI tests are out of scope
for this skill.
