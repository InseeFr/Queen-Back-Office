package fr.insee.queen.jms.integration;

import fr.insee.queen.JMSApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Base class for integration tests using Testcontainers.
 * Provides PostgreSQL and ActiveMQ Artemis containers for testing.
 */
@SpringBootTest(classes = JMSApplication.class)
@Testcontainers
public abstract class AbstractIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:14.15")
    )
            .withDatabaseName("queen")
            .withUsername("test")
            .withPassword("test");

    @Container
    static GenericContainer<?> artemisContainer = new GenericContainer<>(
            DockerImageName.parse("apache/activemq-artemis:2.32.0-alpine")
    )
            .withExposedPorts(61616, 8161)
            .withEnv("ARTEMIS_USER", "test")
            .withEnv("ARTEMIS_PASSWORD", "test")
            .waitingFor(Wait.forLogMessage(".*AMQ221007.*", 1));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Disable Docker Compose for integration tests (we use Testcontainers instead)
        registry.add("spring.docker.compose.enabled", () -> "false");

        // PostgreSQL configuration
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);

        // JPA configuration
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.jpa.show-sql", () -> "false");

        // Liquibase configuration
        registry.add("spring.liquibase.enabled", () -> "true");
        registry.add("spring.liquibase.change-log", () -> "classpath:db/master.xml");

        // ActiveMQ Artemis configuration
        registry.add("spring.artemis.broker-url",
                () -> "tcp://" + artemisContainer.getHost() + ":" + artemisContainer.getMappedPort(61616));
        registry.add("spring.artemis.user", () -> "test");
        registry.add("spring.artemis.password", () -> "test");

        // Broker configuration
        registry.add("broker.name", () -> "artemis");
        registry.add("broker.queue.interrogation.request", () -> "interrogation_request");
        registry.add("broker.queue.interrogation.response", () -> "interrogation_response");

        // Multimode configuration
        registry.add("feature.multimode.publisher.enabled", () -> "true");
        registry.add("feature.multimode.publisher.scheduler.interval", () -> "5000"); // 5 seconds for faster testing
        registry.add("feature.multimode.publisher.scheduler.initialDelay", () -> "500"); // Start quickly in tests
        registry.add("feature.multimode.subscriber.enabled", () -> "true");
        registry.add("feature.multimode.topic", () -> "multimode_events_test");

        // Cross-environment communication configuration for inbox table
        registry.add("feature.cross-environment-communication.consumer", () -> "true");
        registry.add("feature.cross-environment-communication.emitter", () -> "true");
    }
}