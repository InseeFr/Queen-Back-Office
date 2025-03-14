package fr.insee.queen.application.dataset.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"test","test-demo"})
@SpringBootTest
class DemoUnCipherIT {
    @Test
    void contextLoads() {
        // used to check spring boot loaded correctly
    }
}
