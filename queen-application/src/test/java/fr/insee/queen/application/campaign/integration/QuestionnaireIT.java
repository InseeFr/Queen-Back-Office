package fr.insee.queen.application.campaign.integration;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.campaign.dto.input.QuestionnaireModelCreationData;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
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

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class QuestionnaireIT {
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
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_get_questionnaire_when_not_exist_return_404() throws Exception {
        mockMvc.perform(get("/api/questionnaire/not-exist")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_get_questionnaire_return_correct_questionnaire() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/questionnaire/simpsons")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
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
    void on_get_list_questionnaire_for_each_interrogation_in_request_return_questionnaire_list() throws Exception {
        String interrogationIdsInput = """
                [
                  "517046b6-bd88-47e0-838e-00d03461f592",
                  "d98d28c2-1535-4fc8-a405-d6a554231bbc",
                  "89f4df89-8e83-444f-8f43-6d964c3339d8",
                  "456789",
                  "123456"
                ]
                """;
        MvcResult result = mockMvc.perform(post("/api/interrogations/questionnaire-model-id")
                        .content(interrogationIdsInput)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getManagerUser()))
                )
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedResult = """
                {
                  "interrogationOK": [
                    {"id": "517046b6-bd88-47e0-838e-00d03461f592", "questionnaireId": "simpsons"},
                    {"id": "d98d28c2-1535-4fc8-a405-d6a554231bbc", "questionnaireId": "simpsons"},
                    {"id": "89f4df89-8e83-444f-8f43-6d964c3339d8", "questionnaireId": "VQS2021X00"}
                  ],
                  "interrogationNOK": [
                    {"id": "456789"},
                    {"id": "123456"}
                  ]
                }
                """;
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void on_get_list_questionnaire_for_each_interrogation_when_anonymous_return_401() throws Exception {
        mockMvc.perform(post("/api/interrogations/questionnaire-model-id")
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
    void on_get_questionnaires_when_interrogationUser_return_403(String url) throws Exception {
        mockMvc.perform(get(url)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void on_find_questionnaire_model_ids_when_interrogationUser_return_403() throws Exception {
        mockMvc.perform(post("/api/interrogations/questionnaire-model-id")
                        .content("[]")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/campaign/1/questionnaires",
            "/api/questionnaire/1",
            "/api/campaign/1/questionnaire-id",
            "/api/interrogations/questionnaire-model-id"
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
