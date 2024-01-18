package fr.insee.queen.application.surveyunit.integration;

import fr.insee.queen.application.configuration.ScriptConstants;
import fr.insee.queen.application.configuration.auth.AuthorityRoleEnum;
import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;
import fr.insee.queen.domain.surveyunit.model.StateDataType;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration
@AutoConfigureEmbeddedDatabase
@AutoConfigureMockMvc
class SurveyUnitTests {
    @Autowired
    private MockMvc mockMvc;

    private final AuthenticatedUserTestHelper authenticatedUserTestHelper = new AuthenticatedUserTestHelper();

    private final Authentication adminUser = authenticatedUserTestHelper.getAuthenticatedUser(
            AuthorityRoleEnum.ADMIN,
            AuthorityRoleEnum.WEBCLIENT);

    private final Authentication nonAdminUser = authenticatedUserTestHelper.getAuthenticatedUser(
            AuthorityRoleEnum.REVIEWER,
            AuthorityRoleEnum.REVIEWER_ALTERNATIVE,
            AuthorityRoleEnum.INTERVIEWER);

    private final Authentication anonymousUser = authenticatedUserTestHelper.getNotAuthenticatedUser();

    private final String surveyUnitData = """
            {
                "id":"test-surveyunit",
                "personalization":[{"name":"whoAnswers33","value":"MrDupond"},{"name":"whoAnswers2","value":""}],
                "data":{"EXTERNAL":{"LAST_BROADCAST":"12/07/1998"}},"comment":{"COMMENT":"acomment"},
                "stateData":{"state":"EXTRACTED","date":1111111111,"currentPage":"2.3#5"},
                "questionnaireId":"VQS2021X00"
            }""";

    // tests on list before tests on create/update
    @Test
    void on_get_survey_units_by_campaign_return_survey_units() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/campaign/SIMPSONS2020X00/survey-units")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(nonAdminUser))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        String expectedResult = """
                [
                    {"id":"11","questionnaireId":"simpsons"},
                    {"id":"12","questionnaireId":"simpsons"},
                    {"id":"13","questionnaireId":"simpsonsV2"},
                    {"id":"14","questionnaireId":"simpsonsV2"}
                ]""";
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void on_get_survey_unit_ids_return_ids() throws Exception {
        mockMvc.perform(get("/api/survey-units")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(adminUser))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(14)));
    }

    @Test
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_create_survey_unit_then_survey_unit_is_saved() throws Exception {
        mockMvc.perform(post("/api/campaign/VQS2021X00/survey-unit")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surveyUnitData)
                        .with(authentication(adminUser))
                )
                .andExpect(status().isCreated());

        on_get_survey_unit_return_survey_unit("test-surveyunit", surveyUnitData);
    }

    @Test
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_update_survey_unit_then_survey_unit_is_saved() throws Exception {
        String surveyUnitDataUpdated = """
                {
                    "id":"11",
                    "personalization":[{"name":"whoAnswers33","value":"MrDupond"},{"name":"whoAnswers2","value":""}],
                    "data":{"EXTERNAL":{"LAST_BROADCAST":"12/07/1998"}},"comment":{"COMMENT":"COMMENT UPDATED"},
                    "stateData":{"state":"EXTRACTED","date":1111111111,"currentPage":"2.3#5"},
                    "questionnaireId":"simpsons"
                }""";
        mockMvc.perform(post("/api/campaign/simpsons/survey-unit")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surveyUnitDataUpdated)
                        .with(authentication(adminUser))
                )
                .andExpect(status().isOk());

        on_get_survey_unit_return_survey_unit("11", surveyUnitDataUpdated);
    }

    @Test
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_update_with_put_survey_unit_then_survey_unit_is_saved() throws Exception {
        String surveyUnitDataUpdated = """
                {
                    "id":"11",
                    "personalization":[{"name":"whoAnswers33","value":"MrDupond"},{"name":"whoAnswers2","value":""}],
                    "data":{"EXTERNAL":{"LAST_BROADCAST":"12/07/1998"}},"comment":{"COMMENT":"COMMENT UPDATED 2"},
                    "stateData":{"state":"EXTRACTED","date":1111111111,"currentPage":"2.3#5"},
                    "questionnaireId":"simpsons"
                }""";
        mockMvc.perform(put("/api/survey-unit/11")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surveyUnitDataUpdated)
                        .with(authentication(nonAdminUser))
                )
                .andExpect(status().isOk());

        on_get_survey_unit_return_survey_unit("11", surveyUnitDataUpdated);
    }

    @Test
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_create_survey_unit_without_statedata_then_survey_unit_is_saved() throws Exception {
        String surveyUnitDataWithoutState = """
                {
                    "id":"test-surveyunit2",
                    "personalization":[{"name":"whoAnswers33","value":"MrDupond"},{"name":"whoAnswers2","value":""}],
                    "data":{"EXTERNAL":{"LAST_BROADCAST":"12/07/1998"}},"comment":{"COMMENT":"acomment"},
                    "questionnaireId":"VQS2021X00"
                }""";
        String surveyUnitDataResponseWithoutState = """
                {
                    "id":"test-surveyunit2",
                    "personalization":[{"name":"whoAnswers33","value":"MrDupond"},{"name":"whoAnswers2","value":""}],
                    "data":{"EXTERNAL":{"LAST_BROADCAST":"12/07/1998"}},"comment":{"COMMENT":"acomment"},
                    "questionnaireId":"VQS2021X00"
                }""";
        mockMvc.perform(post("/api/campaign/VQS2021X00/survey-unit")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surveyUnitDataWithoutState)
                        .with(authentication(adminUser))
                )
                .andExpect(status().isCreated());
        on_get_survey_unit_return_survey_unit("test-surveyunit2", surveyUnitDataResponseWithoutState);
    }

    private void on_get_survey_unit_return_survey_unit(String surveyUnitId, String expectedResult) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/survey-unit/" + surveyUnitId)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(adminUser))
                )
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_delete_survey_unit_process_deletion() throws Exception {
        String surveyUnitId = "11";
        mockMvc.perform(delete("/api/survey-unit/" + surveyUnitId)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(adminUser))
                )
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/survey-unit/" + surveyUnitId)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(adminUser))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_get_survey_units_by_campaign_when_campaign_not_exist_return_404() throws Exception {
        mockMvc.perform(get("/api/campaign/not-exist/survey-units")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(adminUser))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_get_survey_units_by_campaign_when_campaign_identifier_invalid_return_400() throws Exception {
        mockMvc.perform(get("/api/campaign/invalid!identifier/survey-units")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(adminUser))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_create_survey_unit_when_campaign_not_exist_return_404() throws Exception {
        mockMvc.perform(post("/api/campaign/not-exist/survey-unit")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surveyUnitData)
                        .with(authentication(adminUser))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_create_survey_unit_when_campaign_identifier_invalid_return_400() throws Exception {
        mockMvc.perform(post("/api/campaign/invalid!identifier/survey-unit")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surveyUnitData)
                        .with(authentication(adminUser))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_get_survey_unit_when_identifier_invalid_return_400() throws Exception {
        mockMvc.perform(get("/api/survey-unit/invalid%identifier")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(adminUser))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_get_survey_unit_when_not_exist_return_404() throws Exception {
        mockMvc.perform(get("/api/survey-unit/not-exist")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(adminUser))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_delete_survey_unit_when_identifier_invalid_return_400() throws Exception {
        mockMvc.perform(delete("/api/survey-unit/invalid!identifier")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(adminUser))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_delete_survey_unit_when_not_exist_return_404() throws Exception {
        mockMvc.perform(delete("/api/survey-unit/not-exist")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(adminUser))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_get_deposit_proof_return_200() throws Exception {
        mockMvc.perform(get("/api/survey-unit/11/deposit-proof")
                        .accept(MediaType.APPLICATION_PDF)
                        .with(authentication(nonAdminUser))
                )
                .andExpect(status().isOk());
    }

    @Test
    void on_get_deposit_proof_when_not_exist_return_404() throws Exception {
        mockMvc.perform(get("/api/survey-unit/not-exist/deposit-proof")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(nonAdminUser))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void when_authenticated_non_admin_user_access_admin_endpoints_return_403() throws Exception {
        mockMvc.perform(get("/api/survey-units")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(nonAdminUser))
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/campaign/VQS2021X00/survey-unit")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surveyUnitData)
                        .with(authentication(nonAdminUser))
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/survey-unit/11")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(nonAdminUser))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void when_non_interviewer_update_surveyunit_return_403() throws Exception {
        mockMvc.perform(put("/api/survey-unit/11")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surveyUnitData)
                        .with(authentication(authenticatedUserTestHelper.getAuthenticatedUser(AuthorityRoleEnum.REVIEWER, AuthorityRoleEnum.REVIEWER_ALTERNATIVE)))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void when_non_interviewer_get_surveyunits_return_403() throws Exception {
        mockMvc.perform(get("/api/survey-units/interviewer")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAuthenticatedUser(AuthorityRoleEnum.REVIEWER, AuthorityRoleEnum.REVIEWER_ALTERNATIVE)))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void when_get_survey_units_for_interviewers_return_survey_units() throws Exception {
        mockMvc.perform(get("/api/survey-units/interviewer")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(14)))
                .andExpect(jsonPath("$[0].id").value("11"))
                .andExpect(jsonPath("$[0].questionnaireId").value("simpsons"))
                .andExpect(jsonPath("$[0].personalization.size()", is(2)))
                .andExpect(jsonPath("$[0].data").value(is(not(emptyOrNullString()))))
                .andExpect(jsonPath("$[0].stateData.state").value(StateDataType.EXTRACTED.name()))
                .andExpect(jsonPath("$[0].stateData.date").value(1111111111))
                .andExpect(jsonPath("$[0].stateData.currentPage").value("2.3#5"));
    }

    @Test
    void when_anonymous_user_access_authenticated_endpoints_return_401() throws Exception {
        List<String> getEdnPoints = List.of(
                "/api/survey-units",
                "/api/survey-unit/11",
                "/api/survey-unit/11/deposit-proof",
                "/api/campaign/VQS2021X00/survey-units",
                "/api/survey-units/interviewer");
        for (String getEndPoint : getEdnPoints) {
            mockMvc.perform(get(getEndPoint)
                            .accept(MediaType.APPLICATION_JSON)
                            .with(authentication(anonymousUser))
                    )
                    .andExpect(status().isUnauthorized());
        }

        mockMvc.perform(post("/api/campaign/VQS2021X00/survey-unit")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surveyUnitData)
                        .with(authentication(anonymousUser))
                )
                .andExpect(status().isUnauthorized());

        mockMvc.perform(put("/api/survey-unit/11")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surveyUnitData)
                        .with(authentication(anonymousUser))
                )
                .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/api/survey-unit/11")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(anonymousUser))
                )
                .andExpect(status().isUnauthorized());
    }
}
