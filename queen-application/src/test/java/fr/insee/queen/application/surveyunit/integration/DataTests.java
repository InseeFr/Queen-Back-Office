package fr.insee.queen.application.surveyunit.integration;

import fr.insee.queen.application.configuration.ScriptConstants;
import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;
import fr.insee.queen.application.utils.JsonTestHelper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration
@AutoConfigureEmbeddedDatabase
@AutoConfigureMockMvc
class DataTests {
    @Autowired
    private MockMvc mockMvc;

    private final AuthenticatedUserTestHelper authenticatedUserTestHelper = new AuthenticatedUserTestHelper();

    @Test
    void on_get_data_return_data() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/survey-unit/11/data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        String expectedResult = JsonTestHelper.getResourceFileAsString("surveyunit/data.json");
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void on_get_data_when_su_not_exist_return_404() throws Exception {
        mockMvc.perform(get("/api/survey-unit/plop/data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_get_data_when_su_id_invalid_return_400() throws Exception {
        mockMvc.perform(get("/api/survey-unit/plop$/data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_update_data_data_is_updated() throws Exception {
        String surveyUnitId = "12";
        String dataJson = JsonTestHelper.getResourceFileAsString("surveyunit/data.json");
        MvcResult result = mockMvc.perform(get("/api/survey-unit/" + surveyUnitId + "/data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        JSONAssert.assertNotEquals(dataJson, content, JSONCompareMode.NON_EXTENSIBLE);

        mockMvc.perform(put("/api/survey-unit/" + surveyUnitId + "/data")
                        .content(dataJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isOk());

        result = mockMvc.perform(get("/api/survey-unit/" + surveyUnitId + "/data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
        JSONAssert.assertEquals(dataJson, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void on_update_data_when_su_not_exist_return_404() throws Exception {
        mockMvc.perform(put("/api/survey-unit/not-exist/data")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_update_data_when_su_id_invalid_return_400() throws Exception {
        mockMvc.perform(put("/api/survey-unit/invalid$identifier/data")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_update_data_when_data_not_json_object_node_return_400() throws Exception {
        mockMvc.perform(put("/api/survey-unit/12/data")
                        .content("[]")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_get_data_when_anonymous_user_return_401() throws Exception {
        mockMvc.perform(get("/api/survey-unit/pl_op/data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void on_update_data_when_anonymous_user_return_401() throws Exception {
        mockMvc.perform(put("/api/survey-unit/12/data")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Given survey unit data with collected data, when inserting partial collected data, then merge collected datas")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void updateCollectedData02() throws Exception {
        String surveyUnitId = "su-test-diff-data";
        String externalData = """
           "EXTERNAL": {"LAST_BROADCAST": "12/07/1998"}""";
        String stateData = """
                {
                    "state": "EXTRACTED",
                    "date": 1111111111,
                    "currentPage": "2.3#5"
                }""";

        String newCollectedVar = """
                "DTA": {
                            "EDITED": null,
                            "FORCED": null,
                            "INPUTED": null,
                            "PREVIOUS": null,
                            "COLLECTED": "updated"
                      }
                """;

        String collectedVarToUpdate = """
                "READY": {
                            "EDITED": null,
                            "FORCED": null,
                            "INPUTED": null,
                            "PREVIOUS": null,
                            "COLLECTED": "plop"
                      }
                """;
        String collectedVarNotTouched = """
                "PRODUCER": {
                    "EDITED": null,
                    "FORCED": null,
                    "INPUTED": null,
                    "PREVIOUS": null,
                    "COLLECTED": "Matt Groening"
                }
                """;
        String surveyUnitDataStateData = String.format("""
            {
                "data": {
                      %s,
                      %s
                },
                "stateData": %s
            }""", collectedVarToUpdate, newCollectedVar, stateData);

        // check it works when already collected data
        mockMvc.perform(patch("/api/survey-unit/" + surveyUnitId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surveyUnitDataStateData)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/api/survey-unit/" + surveyUnitId + "/data")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        String expectedResult = String.format("""
                {
                    %s,
                    "COLLECTED": {
                        %s,
                        %s,
                        %s,
                    }
                }""", externalData, collectedVarNotTouched, collectedVarToUpdate, newCollectedVar);
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.STRICT);
    }

    @Test
    @DisplayName("Given survey unit with no collected json data, when updating data then insert partial data as collected data")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void updateCollectedData01() throws Exception {
        String surveyUnitId = "su-test-diff-without-collected-data";
        String externalData = """
           "EXTERNAL": {"LAST_BROADCAST": "12/07/1998"}""";
        String stateData = """
                {
                    "state": "EXTRACTED",
                    "date": 1111111111,
                    "currentPage": "2.3#5"
                }""";

        String collectedVar1 = """
                "DAG": {
                            "EDITED": null,
                            "FORCED": null,
                            "INPUTED": null,
                            "PREVIOUS": null,
                            "COLLECTED": "3"
                      }
                """;

        String collectedVar2 = """
                "DTA": {
                            "EDITED": null,
                            "FORCED": null,
                            "INPUTED": null,
                            "PREVIOUS": null,
                            "COLLECTED": "4"
                      }
                """;

        String surveyUnitDataStateData = String.format("""
            {
                "data": {
                      %s,
                      %s
                },
                "stateData": %s
            }""", collectedVar1, collectedVar2, stateData);

        mockMvc.perform(patch("/api/survey-unit/" + surveyUnitId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surveyUnitDataStateData)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/api/survey-unit/" + surveyUnitId + "/data")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        String expectedResult = String.format("""
                {
                    %s,
                    "COLLECTED": {
                        %s,
                        %s
                    }
                }""", externalData, collectedVar1, collectedVar2);
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.STRICT);
    }

    @Test
    @DisplayName("Given invalid survey unit id, when updating collected data then throw bad request")
    void updateCollectedDataError02() throws Exception {
        mockMvc.perform(patch("/api/survey-unit/invalid$identifier")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Given invalid json collected input data, when updating collected data then throw bad request")
    void updateCollectedDataError03() throws Exception {
        mockMvc.perform(patch("/api/survey-unit/12")
                        .content("[]")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Given an anoymous user, when updating collected data then return unauthenticated error")
    void updateCollectedDataError04() throws Exception {
        mockMvc.perform(patch("/api/survey-unit/12")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }
}
