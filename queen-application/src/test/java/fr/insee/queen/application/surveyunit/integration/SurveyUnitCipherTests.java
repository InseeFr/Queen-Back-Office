package fr.insee.queen.application.surveyunit.integration;

import fr.insee.queen.application.configuration.CipheredContainerConfiguration;
import fr.insee.queen.application.configuration.ScriptConstants;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

class SurveyUnitCipherTests extends CipheredContainerConfiguration {

    private final SurveyUnitCommonAssertions surveyUnitTests;

    public SurveyUnitCipherTests(@Autowired MockMvc mockMvc) {
        this.surveyUnitTests = new SurveyUnitCommonAssertions(mockMvc);
    }

    @Test
    void on_get_survey_units_by_campaign_return_survey_units() throws Exception {
        surveyUnitTests.on_get_survey_units_by_campaign_return_survey_units();
    }

    @Test
    @Sql(value = ScriptConstants.REINIT_CIPHERED_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_create_survey_unit_then_survey_unit_is_saved() throws Exception {
        surveyUnitTests.on_create_survey_unit_then_survey_unit_is_saved();
    }

    @Test
    @Sql(value = ScriptConstants.REINIT_CIPHERED_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_update_survey_unit_then_survey_unit_is_saved() throws Exception {
        surveyUnitTests.on_update_survey_unit_then_survey_unit_is_saved();
    }

    @Test
    @Sql(value = ScriptConstants.REINIT_CIPHERED_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_update_with_put_survey_unit_then_survey_unit_is_saved() throws Exception {
        surveyUnitTests.on_update_with_put_survey_unit_then_survey_unit_is_saved();
    }

    @Test
    @Sql(value = ScriptConstants.REINIT_CIPHERED_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_create_survey_unit_without_statedata_then_survey_unit_is_saved() throws Exception {
        surveyUnitTests.on_create_survey_unit_without_statedata_then_survey_unit_is_saved();
    }

    @Test
    @Sql(value = ScriptConstants.REINIT_CIPHERED_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_delete_survey_unit_process_deletion() throws Exception {
        surveyUnitTests.on_delete_survey_unit_process_deletion();
    }

    @Test
    void when_get_survey_units_for_interviewers_return_survey_units() throws Exception {
        surveyUnitTests.when_get_survey_units_for_interviewers_return_survey_units();
    }
}