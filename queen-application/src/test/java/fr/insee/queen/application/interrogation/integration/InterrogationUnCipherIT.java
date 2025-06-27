package fr.insee.queen.application.interrogation.integration;

import fr.insee.queen.application.configuration.FixedTimeConfiguration;
import fr.insee.queen.application.configuration.ScriptConstants;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@ActiveProfiles("test")
@SpringBootTest(classes = {
        FixedTimeConfiguration.class
})@AutoConfigureMockMvc
/* Disable the "Add at least one assertion to this test case." (sic)
   The sonar rule is not smart enough to inspect common test class
 */
@SuppressWarnings("java:S2699")
class InterrogationUnCipherIT {
    private final InterrogationCommonAssertions interrogationTests;

    public InterrogationUnCipherIT(@Autowired MockMvc mockMvc) {
        this.interrogationTests = new InterrogationCommonAssertions(mockMvc);
    }

    @Test
    void on_get_interrogations_by_campaign_return_interrogations() throws Exception {
        interrogationTests.on_get_interrogations_by_campaign_return_interrogations();
    }

    @Test
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_create_interrogation_then_interrogation_is_saved() throws Exception {
        interrogationTests.on_create_interrogation_then_interrogation_is_saved();
    }

    @Test
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_update_interrogation_then_interrogation_is_saved() throws Exception {
        interrogationTests.on_update_interrogation_then_interrogation_is_saved();
    }

    @Test
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_update_with_put_interrogation_then_interrogation_is_saved() throws Exception {
        interrogationTests.on_update_with_put_interrogation_then_interrogation_is_saved();
    }

    @Test
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_create_interrogation_without_statedata_then_interrogation_is_saved() throws Exception {
        interrogationTests.on_create_interrogation_without_statedata_then_interrogation_is_saved();
    }

    @Test
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_delete_interrogation_process_deletion() throws Exception {
        interrogationTests.on_delete_interrogation_process_deletion();
    }

    @Test
    void when_get_interrogations_for_interviewers_return_interrogations() throws Exception {
        interrogationTests.when_get_interrogations_for_interviewers_return_interrogations();
    }
}
