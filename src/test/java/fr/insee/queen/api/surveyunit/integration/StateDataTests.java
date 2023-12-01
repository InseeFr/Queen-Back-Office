package fr.insee.queen.api.surveyunit.integration;

import fr.insee.queen.api.configuration.auth.AuthorityRoleEnum;
import fr.insee.queen.api.utils.AuthenticatedUserTestHelper;
import fr.insee.queen.api.utils.JsonTestHelper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StateDataTests {
    @Autowired
    private MockMvc mockMvc;

    private final AuthenticatedUserTestHelper authenticatedUserTestHelper = new AuthenticatedUserTestHelper();

    private final Authentication nonAdminUser = authenticatedUserTestHelper.getAuthenticatedUser(
            AuthorityRoleEnum.REVIEWER,
            AuthorityRoleEnum.REVIEWER_ALTERNATIVE,
            AuthorityRoleEnum.INTERVIEWER);

    private final Authentication anonymousUser = authenticatedUserTestHelper.getNotAuthenticatedUser();

    @Test
    void on_get_state_data_return_state_data() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/survey-unit/11/state-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(nonAdminUser))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        String expectedResult = JsonTestHelper.getResourceFileAsString("db/dataset/state_data.json");
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void on_get_state_data_when_su_not_exist_return_404() throws Exception {
        mockMvc.perform(get("/api/survey-unit/plop/state-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(nonAdminUser))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_get_state_data_when_su_id_invalid_return_400() throws Exception {
        mockMvc.perform(get("/api/survey-unit/pl!op/state-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(nonAdminUser))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_update_state_data_state_data_is_updated() throws Exception {
        String surveyUnitId = "12";
        String stateDataJson = JsonTestHelper.getResourceFileAsString("db/dataset/state_data.json");
        MvcResult result = mockMvc.perform(get("/api/survey-unit/" + surveyUnitId + "/state-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(nonAdminUser))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        JSONAssert.assertNotEquals(stateDataJson, content, JSONCompareMode.NON_EXTENSIBLE);

        mockMvc.perform(put("/api/survey-unit/" + surveyUnitId + "/state-data")
                        .content(stateDataJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(nonAdminUser))
                )
                .andExpect(status().isOk());

        result = mockMvc.perform(get("/api/survey-unit/" + surveyUnitId + "/state-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(nonAdminUser))
                )
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
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
                        .with(authentication(nonAdminUser))
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
    void on_update_state_data_when_su_not_exist_return_404() throws Exception {
        String stateDataJson = JsonTestHelper.getResourceFileAsString("db/dataset/state_data.json");
        mockMvc.perform(put("/api/survey-unit/not-exist/state-data")
                        .content(stateDataJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(nonAdminUser))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_update_state_data_when_su_id_invalid_return_400() throws Exception {
        mockMvc.perform(put("/api/survey-unit/invalid_identifier/state-data")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(nonAdminUser))
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
                        .with(authentication(nonAdminUser))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_get_state_data_when_anonymous_return_401() throws Exception {
        mockMvc.perform(get("/api/survey-unit/11/state-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(anonymousUser))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void on_update_state_data_when_anonymous_return_401() throws Exception {
        mockMvc.perform(put("/api/survey-unit/11/state-data")
                        .content("{}")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(anonymousUser))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void on_post_state_data_when_anonymous_return_401() throws Exception {
        mockMvc.perform(post("/api/survey-units/state-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(anonymousUser))
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
                        .with(authentication(anonymousUser))
                )
                .andExpect(status().isUnauthorized());
    }
}
