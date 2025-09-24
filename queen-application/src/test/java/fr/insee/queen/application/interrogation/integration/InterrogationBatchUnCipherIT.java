package fr.insee.queen.application.interrogation.integration;

import fr.insee.queen.application.configuration.ScriptConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class InterrogationBatchUnCipherIT {

    private final InterrogationBatchCommonAssertions interrogationTests;

    public InterrogationBatchUnCipherIT(@Autowired MockMvc mockMvc, @Autowired JdbcTemplate jdbcTemplate) {
        this.interrogationTests = new InterrogationBatchCommonAssertions(mockMvc, jdbcTemplate);
    }

    @Test
    @DisplayName("Should create/update/delete interrogation with batch")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void e2e_batch_flow() throws Exception {
        boolean isCiphered = false;
        interrogationTests.shouldCreateUpdateDeleteInterrogations(isCiphered);
    }
}