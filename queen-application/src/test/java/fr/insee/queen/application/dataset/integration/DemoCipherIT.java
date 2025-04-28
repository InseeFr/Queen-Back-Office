package fr.insee.queen.application.dataset.integration;

import fr.insee.queen.application.configuration.ScriptConstants;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_CLASS;

@ActiveProfiles({"test-cipher","test-demo"})
@SpringBootTest
@Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_CLASS)
class DemoCipherIT {
    @Test
    void contextLoads() {
        // used to check spring boot loaded correctly
    }
}
