package fr.insee.queen.application.dataset.integration;

import fr.insee.queen.application.configuration.ContainerConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@ActiveProfiles("test-demo")
@SpringBootTest
@Testcontainers
class DemoUnCipherTests  extends ContainerConfiguration {
    @Test
    void contextLoads() {
        // used to check spring boot loaded correctly
    }
}
