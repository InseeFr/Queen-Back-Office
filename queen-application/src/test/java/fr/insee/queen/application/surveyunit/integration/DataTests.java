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

import static org.assertj.core.api.Assertions.assertThat;
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
    void updateCollectedData01() throws Exception {
        String surveyUnitId = "11";
        String collectedDataJson = """
            {
                "hey": "ho",
                "obj": {"plip": "plop"},
                "numb": 2,
                "READY": {
                     "EDITED": true,
                     "COLLECTED":false
                },
                "COMMENT": null
            }""";

        mockMvc.perform(patch("/api/survey-unit/" + surveyUnitId + "/data")
                        .content(collectedDataJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/api/survey-unit/" + surveyUnitId + "/data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        String expectedResult = """
            {
               "EXTERNAL":{
                  "LAST_BROADCAST":"12/07/1998"
               },
               "COLLECTED":{
                  "hey":"ho",
                  "obj":{
                     "plip":"plop"
                  },
                  "numb":2,
                  "READY":{
                     "EDITED":true,
                     "COLLECTED":false
                  },
                  "COMMENT": null,
                  "PRODUCER":{
                     "EDITED":null,
                     "FORCED":null,
                     "INPUTED":null,
                     "PREVIOUS":null,
                     "COLLECTED":"Matt Groening"
                  }
               }
            }
        """;
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.STRICT);
    }

    @Test
    @DisplayName("Given survey unit with no collected json data, when updating data then insert partial data as collected data")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void updateCollectedData02() throws Exception {
        String surveyUnitId = "21";
        String collectedDataJson = """
            {
                "COMMENT": null
            }""";

        mockMvc.perform(patch("/api/survey-unit/" + surveyUnitId + "/data")
                        .content(collectedDataJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/api/survey-unit/" + surveyUnitId + "/data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        String expectedResult = "{\"COLLECTED\":" + collectedDataJson + "}";
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.STRICT);
    }

    @Test
    @DisplayName("Given invalid survey unit id, when updating collected data then throw bad request")
    void updateCollectedDataError02() throws Exception {
        mockMvc.perform(patch("/api/survey-unit/invalid$identifier/data")
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
        mockMvc.perform(patch("/api/survey-unit/12/data")
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
        mockMvc.perform(patch("/api/survey-unit/12/data")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }
}
