---
name: spring-boot-e2e-cucumber
description: >
  Expert Java / Spring Boot end-to-end integration tests driven by Cucumber.
  Trigger for: writing or refactoring black-box E2E tests where the app runs in-JVM
  (@SpringBootTest, RANDOM_PORT) and external services run in docker-compose — feature
  files, step definitions, Cucumber-Spring config, @Before/@After hooks, state reset,
  WireMock boundary stubs, RestTestClient assertions. Do not trigger for: unit tests,
  slice tests (@WebMvcTest, @DataJpaTest, @WebFluxTest), contract tests (Pact / Spring
  Cloud Contract), load / UI tests, theoretical questions without code, or production code.
version: 0.1
---

# E2E Integration Tests — Spring Boot 4.x + Cucumber

Setup assumed by this skill: Maven, Spring Boot 4.1 / Spring Framework 7 (Java 17+),
JUnit Platform, `cucumber-java` + `cucumber-spring` + `cucumber-junit-platform-engine`.
SUT booted in-JVM via `@SpringBootTest(webEnvironment = RANDOM_PORT)`; DB and other
services under docker-compose; external HTTP services stubbed with WireMock; production
code uses `RestClient`.

---

## Priority hierarchy

In case of tension between two rules, apply them in this order:

| Level | Category | Principle |
|---|---|---|
| **P1** | **Determinism & isolation** | A flaky or order-dependent test is worse than no test: it destroys trust and failures get ignored |
| **P2** | **Fidelity (black-box)** | Drive the system through its public boundary (HTTP), assert on observable effects (response, persisted state, emitted message). Never reach into internals |
| **P3** | **Business readability** | Declarative Gherkin in domain language, Given/When/Then discipline, reusable steps |
| **P4** | **Cost / speed** | One shared Spring context, one compose stack, no `@DirtiesContext`, controlled parallelism |

> Example of tension: fidelity (P2) wants the real external service; determinism (P1)
> forbids depending on a flaky third party → stub it at the boundary with WireMock.
> P1 wins: a deterministic stub beats a real-but-flaky dependency. Report it.

---

## Parsimony rule (meta)

**Silently apply best practices. Only comment on a decision if:**
- it falls under **P1 (determinism/isolation)** — always explain the flakiness risk,
- it involves a **structural choice** between two valid approaches (e.g., WireMock boundary stub vs `@MockitoBean` last resort),
- it **warns about over-engineering** (e.g., contract/CQRS machinery for a simple flow),
- it **relaxes** a skill rule in favor of a higher priority.

Otherwise: produce clean tests directly, without preamble or justification.

This skill writes integration tests. It does **not** modify production code to make a
test pass (that breaks P2 fidelity) and does not create the production feature itself.

---

## Guiding principles

**Black-box first** — Drive via the public HTTP boundary, observe via public effects
(HTTP response, persisted state, emitted event). No peeking at internals "to help" the test.
**One scenario = one business behavior** — Declarative, domain language, self-sufficient.
Not a script of technical steps.
**Determinism by construction** — Time, randomness, concurrency and ordering are controlled.
A test that "passes on retry" is broken, not flaky-but-fine.
**Isolation by reset, not by hope** — State is reset before each scenario, never reliant on
execution order or leftovers from a previous scenario.
**Single context** — One Spring context for the whole suite. Anything that fragments it
(bean mock, `@DirtiesContext`) costs startup time and must be justified.

---

## Structural rules (P2 — correctness & fidelity)

| Topic | ❌ NEVER | ✅ ALWAYS |
|---|---|---|
| Transaction rollback | `@Transactional` on the test/config class to "clean up" | `@Transactional(propagation = NOT_SUPPORTED)` + programmatic reset — the controller runs on a server thread, outside the test's transaction, so nothing rolls back |
| State reset | Rely on scenario order or an `@After` cleanup | `TRUNCATE ... RESTART IDENTITY CASCADE` in a global `@Before` hook, on the real `DataSource` |
| Mock external HTTP | `@MockitoBean` on the internal client/component | WireMock at the boundary: point the `RestClient` base URL to the stub, `resetAll()` per scenario |
| Bean mock (last resort) | Mock a bean "for convenience" (forks the context cache) | Reserve for non-HTTP collaborators with no stubbable boundary; assumed and reported |
| Removed annotations | `@MockBean` / `@SpyBean` (removed in Boot 4) | `@MockitoBean` / `@MockitoSpyBean` (`org.springframework.test.context.bean.override.mockito`) |
| Test HTTP client | `RestTestClient.bindToController` / `bindToApplicationContext` (bypasses filters, security) | `RestTestClient.bindToServer()` on the random port (full HTTP stack), or inject it via `@AutoConfigureRestTestClient` |
| Time | `Instant.now()` / `LocalDateTime.now()` left uncontrolled in the SUT | Injectable `Clock`, fixed in the test profile |
| Cucumber config | Several `@CucumberContextConfiguration` classes | A single config class, shared by all glue/step defs |
| Shared step state | Static fields / global variables | State held by a scenario-scoped bean, injected via DI |
| Test secrets | Real tokens/passwords hardcoded in features/fixtures | Fake values, isolated test profile |

---

## Readability rules (P3 — Gherkin)

| Topic | ❌ NEVER | ✅ ALWAYS |
|---|---|---|
| Language level | Imperative steps ("click", "send a POST to /x with body {...}") | Declarative business language ("when the customer confirms the order") |
| Technical detail | URLs, HTTP codes, JSON inside the `.feature` | Hidden in step definitions; the `.feature` stays business-facing |
| Common setup | Repeat the same Givens in every scenario | `Background` for the shared context of a `.feature` |
| Data | Magic values scattered across steps | `Scenario Outline` + `Examples`, or readable data tables |
| Step defs | Catch-all glue, fragile regex | Reusable steps, Cucumber expressions, minimal glue organized by domain |
| Assertions | `assertTrue(x.equals(y))`, asserting on a mock (`verify(...)`) | AssertJ on the observable (response, DB state); meaningful failure messages |
| Scenario coupling | A scenario that depends on state left by another | Each scenario self-sufficient (its Given sets up its full context) |

---

## Performance rules (P4)

| Topic | ❌ NEVER | ✅ ALWAYS |
|---|---|---|
| Context | `@DirtiesContext`, varying mock sets per test | One shared context for the whole suite (SF7 pauses inactive cached contexts) |
| Compose stack | Restart the stack per scenario | Started once for the suite; lifecycle owned outside the scenario |
| WireMock | Re-instantiate the server per scenario | One reused server, stubs reset per scenario |
| Selection | Run everything as one indistinct block | Cucumber tags (`@e2e`, `@slow`) to target / parallelize |

---

## Security (P1 — systematically fix + explain)

| ⚠️ Detection | ✅ Action |
|---|---|
| Security disabled in test (`permitAll`, filters off) to make a scenario pass | Test against the real security config — otherwise false negatives. Explain the production gap |
| Real secret/token hardcoded in a `.feature` or fixture | Replace with a fake value in an isolated profile. Explain leakage through VCS |
| Token / PII printed in test logs or WireMock verbose output | Mask or disable verbose logging. Explain the confidentiality risk |

---

## Reference skeleton (the non-obvious wiring)

```java
// Single Spring config for Cucumber — step defs share it via the glue path.
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional(propagation = Propagation.NOT_SUPPORTED) // no rollback over the HTTP boundary
@AutoConfigureRestTestClient                            // HTTP test clients are not auto-configured in Boot 4
@ActiveProfiles("e2e")
public class CucumberSpringConfig { }
```

```java
// Global hook: deterministic clean slate before every scenario, context untouched.
public class ResetHooks {

    private final JdbcTemplate jdbc;   // bound to the real (compose) DataSource

    public ResetHooks(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @Before(order = 0)
    public void reset() {
        truncateAllTables();        // TRUNCATE <tables> RESTART IDENTITY CASCADE (skip migration history)
        WireMock.resetAllRequests();// + reset stubs so each scenario stubs only what it needs
    }
}
```

```java
// Step def: black-box via the running server, AssertJ-style expectations.
public class OrderSteps {

    @Autowired RestTestClient client;  // injected against the random port

    @When("the customer confirms the order")
    public void confirmsOrder() {
        client.post().uri("/orders/{id}/confirm", scenario.orderId())
              .exchange()
              .expectStatus().isOk();
    }
}
```

Key points: external HTTP dependencies are reached through WireMock by pointing the
`RestClient` base URL (config property) at the WireMock instance; one WireMock server is
shared and only its stubs are reset per scenario, so the Spring context stays cached.
