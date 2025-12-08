package fr.insee.queen.jms.integration;

import fr.insee.queen.JMSApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Base class for integration tests using Spring Docker Compose.
 * Uses the compose.yml file from queen-application to provide PostgreSQL and ActiveMQ Artemis containers for testing.
 * Only starts services with the 'queen-listener-jms' profile (queen-db and activemq).
 */
@SpringBootTest(classes = JMSApplication.class)
public abstract class AbstractIntegrationTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Spring Docker Compose configuration (disabled for now - start containers manually)
        registry.add("spring.docker.compose.enabled", () -> "false");

        // PostgreSQL configuration (from compose.queen-db.yml)
        registry.add("spring.datasource.url", () -> "jdbc:postgresql://localhost:5434/queen");
        registry.add("spring.datasource.username", () -> "mypostgresuser2");
        registry.add("spring.datasource.password", () -> "mypostgrespassword2");

        // ActiveMQ Artemis configuration (from compose.activemq.yml)
        registry.add("spring.artemis.broker-url", () -> "tcp://localhost:61616");
        registry.add("spring.artemis.user", () -> "insee");
        registry.add("spring.artemis.password", () -> "lille");

        // JPA configuration
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.jpa.show-sql", () -> "false");

        // Liquibase configuration
        registry.add("spring.liquibase.enabled", () -> "true");
        registry.add("spring.liquibase.change-log", () -> "classpath:db/master.xml");

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