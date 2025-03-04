package fr.insee.queen.application.surveyunit.integration;

import fr.insee.queen.application.configuration.ContainerConfiguration;
import fr.insee.queen.application.configuration.ScriptConstants;
import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;
import fr.insee.queen.application.utils.JsonTestHelper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StateDataTests extends ContainerConfiguration {
    @Autowired
    private MockMvc mockMvc;

    private final AuthenticatedUserTestHelper authenticatedUserTestHelper = new AuthenticatedUserTestHelper();

    @Test
    void on_get_state_data_return_state_data() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/survey-unit/11/state-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        String expectedResult = JsonTestHelper.getResourceFileAsString("surveyunit/state_data.json");
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void on_get_state_data_when_su_not_exist_return_404() throws Exception {
        mockMvc.perform(get("/api/survey-unit/plop/state-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_get_state_data_when_su_id_invalid_return_400() throws Exception {
        mockMvc.perform(get("/api/survey-unit/pl!op/state-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_update_state_data_when_date_invalid_return_409() throws Exception {
        String surveyUnitId = "12";
        String stateDataJson = """
            {
              "state": "EXTRACTED",
              "date": 1111111110,
              "currentPage": "2.3#5"
            }
        """;
        mockMvc.perform(put("/api/survey-unit/" + surveyUnitId + "/state-data")
                        .content(stateDataJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isConflict());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1111111119","1111111120"})
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_update_state_data_state_data_is_updated(String timestamp) throws Exception {
        String surveyUnitId = "12";
        String stateDataJson = """
            {
              "state": "EXTRACTED",
              "date":""" + timestamp + """
              ,"currentPage": "2.3#5"
            }
        """;

        mockMvc.perform(put("/api/survey-unit/" + surveyUnitId + "/state-data")
                        .content(stateDataJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/api/survey-unit/" + surveyUnitId + "/state-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        JSONAssert.assertEquals(stateDataJson, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void on_retrieve_state_datas_by_survey_units_return_correct_values() throws Exception {
        String surveyUnitIds = """
                ["11","12","13","plop","plup"]""";
        MvcResult result = mockMvc.perform(post("/api/survey-units/state-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surveyUnitIds)
                        .with(authentication(authenticatedUserTestHelper.getManagerUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        String expectedJson = """
                {
                  "surveyUnitOK": [
                    {
                      "id": "11",
                      "stateData": {
                        "state": "EXTRACTED",
                        "date": 1111111111,
                        "currentPage": "2.3#5"
                      }
                    },
                    {
                      "id": "13",
                      "stateData": {
                        "state": "INIT",
                        "date": 1111111111,
                        "currentPage": "2.3#5"
                      }
                    }
                  ],
                  "surveyUnitNOK": [
                    {
                      "id": "plop"
                    },
                    {
                      "id": "plup"
                    }
                  ]
                }""";
        JSONAssert.assertNotEquals(expectedJson, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void when_user_update_surveyunit_data_state_data_return_updated_state_data() throws Exception {
        String stateData = """
                {
                    "state": "EXTRACTED",
                    "date": 1111111111,
                    "currentPage": "2.3#5"
                }""";

        String surveyUnitDataStateData = String.format("""
            {
                "data": {
                    "COLLECTED": {
                    }
                },
                "stateData": %s
            }""", stateData);
        mockMvc.perform(patch("/api/survey-unit/su-test-diff-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surveyUnitDataStateData)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/api/survey-unit/11/state-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surveyUnitDataStateData)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        JSONAssert.assertEquals(stateData, content, JSONCompareMode.STRICT);
    }

    @Test
    void when_user_update_surveyunit_with_incorrect_state_data_return_400() throws Exception {
        String surveyUnitDataStateData = """
            {
                "data":{},
                    "stateData": {
                        "state": "EACTED",
                        "date": 1111111111,
                        "currentPage": "2.3#5"
                    }
                }""";
        mockMvc.perform(patch("/api/survey-unit/11")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surveyUnitDataStateData)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_update_state_data_when_su_not_exist_return_404() throws Exception {
        String stateDataJson = JsonTestHelper.getResourceFileAsString("surveyunit/state_data.json");
        mockMvc.perform(put("/api/survey-unit/not-exist/state-data")
                        .content(stateDataJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_update_state_data_when_su_id_invalid_return_400() throws Exception {
        mockMvc.perform(put("/api/survey-unit/invalid_identifier/state-data")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_update_state_data_when_state_data_not_valid_return_400() throws Exception {
        mockMvc.perform(put("/api/survey-unit/12/state-data")
                        .content("""
                                {
                                    "state": "PLOP",
                                    "date": 1111111111,
                                    "currentPage": "2.3#5"
                                }""")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_get_state_data_when_anonymous_return_401() throws Exception {
        mockMvc.perform(get("/api/survey-unit/11/state-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void on_update_state_data_when_anonymous_return_401() throws Exception {
        mockMvc.perform(put("/api/survey-unit/11/state-data")
                        .content("{}")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void on_post_state_data_when_anonymous_return_401() throws Exception {
        mockMvc.perform(post("/api/survey-units/state-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void on_retrieve_state_datas_when_anonymous_return_401() throws Exception {
        String surveyUnitIds = """
                ["11","12","13","plop","plup"]""";
        mockMvc.perform(post("/api/survey-units/state-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surveyUnitIds)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void on_retrieve_state_datas_when_surveyUnitUser_return_403() throws Exception {
        String surveyUnitIds = """
                ["11","12","13","plop","plup"]""";
        mockMvc.perform(post("/api/survey-units/state-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surveyUnitIds)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isForbidden());
    }
}
