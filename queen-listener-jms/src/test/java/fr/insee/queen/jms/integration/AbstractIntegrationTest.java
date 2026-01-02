package fr.insee.queen.jms.integration;

import fr.insee.queen.JMSApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Base class for integration tests using Spring Docker Compose.
 * Uses compose.test.yml to provide PostgreSQL and ActiveMQ Artemis containers for testing.
 * Containers are automatically started and stopped by Spring Docker Compose.
 */
@SpringBootTest(classes = JMSApplication.class)
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Multimode configuration
        registry.add("feature.multimode.publisher.enabled", () -> "true");
        registry.add("feature.multimode.publisher.scheduler.interval", () -> "5000");
        registry.add("feature.multimode.publisher.scheduler.initialDelay", () -> "500");
        registry.add("feature.multimode.subscriber.enabled", () -> "true");
        registry.add("feature.multimode.topic", () -> "multimode_events_test");

        // Cross-environment communication configuration for inbox table
        registry.add("feature.cross-environment-communication.consumer", () -> "true");
        registry.add("feature.cross-environment-communication.emitter", () -> "true");
    }
}