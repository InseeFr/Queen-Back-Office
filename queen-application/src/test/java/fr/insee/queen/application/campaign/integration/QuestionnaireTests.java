package fr.insee.queen.application.campaign.integration;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.campaign.dto.input.QuestionnaireModelCreationData;
import fr.insee.queen.application.configuration.ContainerConfiguration;
import fr.insee.queen.application.configuration.ScriptConstants;
import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;
import fr.insee.queen.application.utils.JsonTestHelper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class QuestionnaireTests extends ContainerConfiguration {
    @Autowired
    private MockMvc mockMvc;

    private final AuthenticatedUserTestHelper authenticatedUserTestHelper = new AuthenticatedUserTestHelper();

    @Test
    void on_get_questionnaires_by_campaign_return_questionnaires() throws Exception {
        mockMvc.perform(get("/api/campaign/SIMPSONS2020X00/questionnaires")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getManagerUser()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)));
    }

    @Test
    void on_get_questionnaires_by_campaign_when_campaign_not_exist_return_404() throws Exception {
        mockMvc.perform(get("/api/campaign/not-exist/questionnaires")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getManagerUser()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_get_questionnaires_by_campaign_when_campaign_identifier_invalid_return_400() throws Exception {
        mockMvc.perform(get("/api/campaign/invalidéidentifier/questionnaires")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getManagerUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_get_questionnaire_ids_by_campaign_return_questionnaires() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/campaign/SIMPSONS2020X00/questionnaire-id")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getManagerUser()))
                )
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedResult = """
                [
                    {"questionnaireId": "simpsons"},
                    {"questionnaireId": "simpsonsV2"}
                ]""";
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void on_get_questionnaire_ids_by_campaign_when_campaign_not_exist_return_404() throws Exception {
        mockMvc.perform(get("/api/campaign/not-exist/questionnaire-id")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNonAdminUser()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_get_questionnaire_ids_by_campaign_when_campaign_identifier_invalid_return_400() throws Exception {
        mockMvc.perform(get("/api/campaign/invalidéidentifier/questionnaire-id")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_get_questionnaire_when_identifier_invalid_return_400() throws Exception {
        mockMvc.perform(get("/api/questionnaire/invalidéidentifier")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_get_questionnaire_when_not_exist_return_404() throws Exception {
        mockMvc.perform(get("/api/questionnaire/not-exist")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_get_questionnaire_return_correct_questionnaire() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/questionnaire/simpsons")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        String expectedResult = "{ \"value\": " + JsonTestHelper.getResourceFileAsString("questionnaire/simpsons.json") + "}";
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @ParameterizedTest
    @CsvSource("questionnaire-creation-test,questionnaire/simpsons.json")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_create_questionnaire_check_questionnaire_created(String questionnaireId, String questionnaireFile) throws Exception {
        ObjectNode questionnaireJson = JsonTestHelper.getResourceFileAsObjectNode(questionnaireFile);
        Set<String> nomenclatures = Set.of("cities2019", "regions2019");
        QuestionnaireModelCreationData questionnaire = new QuestionnaireModelCreationData(questionnaireId, "label questionnaire", questionnaireJson, nomenclatures);
        mockMvc.perform(post("/api/questionnaire-models")
                        .content(JsonTestHelper.getObjectAsJsonString(questionnaire))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(get("/api/questionnaire/" + questionnaireId)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        String expectedResult = "{ \"value\": " + JsonTestHelper.getResourceFileAsString(questionnaireFile) + "}";
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void on_get_list_questionnaire_for_each_surveyunit_in_request_return_questionnaire_list() throws Exception {
        String surveyUnitIdsInput = """
                [
                  "11",
                  "12",
                  "20",
                  "456789",
                  "123456"
                ]
                """;
        MvcResult result = mockMvc.perform(post("/api/survey-units/questionnaire-model-id")
                        .content(surveyUnitIdsInput)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getManagerUser()))
                )
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedResult = """
                {
                  "surveyUnitOK": [
                    {"id": "11", "questionnaireId": "simpsons"},
                    {"id": "12", "questionnaireId": "simpsons"},
                    {"id": "20", "questionnaireId": "VQS2021X00"}
                  ],
                  "surveyUnitNOK": [
                    {"id": "456789"},
                    {"id": "123456"}
                  ]
                }
                """;
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void on_get_list_questionnaire_for_each_surveyunit_when_anonymous_return_401() throws Exception {
        mockMvc.perform(post("/api/survey-units/questionnaire-model-id")
                        .content("[]")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/campaign/1/questionnaires",
            "/api/campaign/1/questionnaire-id"})
    void on_get_questionnaires_when_surveyUnitUser_return_403(String url) throws Exception {
        mockMvc.perform(get(url)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void on_find_questionnaire_model_ids_when_surveyUnitUser_return_403() throws Exception {
        mockMvc.perform(post("/api/survey-units/questionnaire-model-id")
                        .content("[]")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/campaign/1/questionnaires",
            "/api/questionnaire/1",
            "/api/campaign/1/questionnaire-id",
            "/api/survey-units/questionnaire-model-id"
    })
    void on_get_questionnaires_when_anonymous_return_401(String url) throws Exception {
        mockMvc.perform(get(url)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void on_create_questionnaires_when_anonymous_return_401() throws Exception {
        QuestionnaireModelCreationData questionnaire = new QuestionnaireModelCreationData("1", "label questionnaire", JsonNodeFactory.instance.objectNode(), Set.of("cities2019"));
        mockMvc.perform(post("/api/questionnaire-models")
                        .content(JsonTestHelper.getObjectAsJsonString(questionnaire))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void on_create_questionnaires_when_non_admin_return_403() throws Exception {
        QuestionnaireModelCreationData questionnaire = new QuestionnaireModelCreationData("1", "label questionnaire", JsonNodeFactory.instance.objectNode(), Set.of("cities2019"));
        mockMvc.perform(post("/api/questionnaire-models")
                        .content(JsonTestHelper.getObjectAsJsonString(questionnaire))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNonAdminUser()))
                )
                .andExpect(status().isForbidden());
    }
}
