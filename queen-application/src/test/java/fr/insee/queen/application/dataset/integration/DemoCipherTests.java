package fr.insee.queen.application.dataset.integration;

import fr.insee.queen.application.configuration.CipheredContainerConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@ActiveProfiles("test-demo")
@SpringBootTest
@Testcontainers
class DemoCipherTests extends CipheredContainerConfiguration {
    @Test
    void contextLoads() {
        // used to check spring boot loaded correctly
    }
}
