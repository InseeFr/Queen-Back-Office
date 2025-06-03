package fr.insee.queen.application.interrogation.integration;

import fr.insee.queen.application.configuration.FixedTimeConfiguration;
import fr.insee.queen.application.configuration.ScriptConstants;
import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;
import fr.insee.queen.application.utils.JsonTestHelper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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

import java.time.Clock;
import java.time.Instant;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(classes = {
        FixedTimeConfiguration.class
})
@AutoConfigureMockMvc
class StateDataIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Clock fixedClock;

    private final AuthenticatedUserTestHelper authenticatedUserTestHelper = new AuthenticatedUserTestHelper();

    @Test
    void on_get_state_data_return_state_data() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/interrogations/517046b6-bd88-47e0-838e-00d03461f592/state-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        String expectedResult = JsonTestHelper.getResourceFileAsString("interrogation/state_data.json");
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void on_get_state_data_when_su_not_exist_return_404() throws Exception {
        mockMvc.perform(get("/api/interrogations/plop/state-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_get_state_data_when_su_id_invalid_return_400() throws Exception {
        mockMvc.perform(get("/api/interrogations/pl!op/state-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_update_state_data_when_date_invalid_return_409() throws Exception {
        String interrogationId = "d98d28c2-1535-4fc8-a405-d6a554231bbc";
        String stateDataJson = """
            {
              "state": "EXTRACTED",
              "currentPage": "2.3#5"
            }
        """;
        mockMvc.perform(put("/api/interrogations/" + interrogationId + "/state-data")
                        .content(stateDataJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isConflict());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1111111119","1111111120"})
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_update_state_data_state_data_is_updated(String timestamp) throws Exception {
        String interrogationId = "d98d28c2-1535-4fc8-a405-d6a554231bbc";
        String stateDataJson = """
            {
              "state": "EXTRACTED",
              "currentPage": "2.3#5"
            }
        """;
        String stateDataJsonExpected = """
            {
              "state": "EXTRACTED",
              "date": """ + Instant.now(fixedClock).toEpochMilli() + "," +"""
              "currentPage": "2.3#5"
            }
        """;

        mockMvc.perform(put("/api/interrogations/" + interrogationId + "/state-data")
                        .content(stateDataJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/api/interrogations/" + interrogationId + "/state-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        JSONAssert.assertEquals(stateDataJsonExpected, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void on_retrieve_state_datas_by_interrogations_return_correct_values() throws Exception {
        String interrogationIds = """
                ["517046b6-bd88-47e0-838e-00d03461f592","d98d28c2-1535-4fc8-a405-d6a554231bbc","c8142dcc-c133-49aa-a969-bb9828190a2c","plop","plup"]""";
        MvcResult result = mockMvc.perform(post("/api/interrogations/state-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(interrogationIds)
                        .with(authentication(authenticatedUserTestHelper.getManagerUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        String expectedJson = """
                {
                  "interrogationOK": [
                    {
                      "id": "517046b6-bd88-47e0-838e-00d03461f592",
                      "stateData": {
                        "state": "EXTRACTED",
                        "date": 1111111111,
                        "currentPage": "2.3#5"
                      }
                    },
                    {
                      "id": "c8142dcc-c133-49aa-a969-bb9828190a2c",
                      "stateData": {
                        "state": "INIT",
                        "date": 1111111111,
                        "currentPage": "2.3#5"
                      }
                    }
                  ],
                  "interrogationNOK": [
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
    void when_user_update_interrogation_data_state_data_return_updated_state_data() throws Exception {
        String stateData = """
                {
                    "state": "EXTRACTED",
                    "date": 1111111111,
                    "currentPage": "2.3#5"
                }""";

        String interrogationDataStateData = String.format("""
            {
                "data": {
                    "COLLECTED": {
                    }
                },
                "stateData": %s
            }""", stateData);
        mockMvc.perform(patch("/api/interrogations/80dc2493-5258-44c5-8ec1-9c600d1df80b")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(interrogationDataStateData)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/api/interrogations/517046b6-bd88-47e0-838e-00d03461f592/state-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(interrogationDataStateData)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        JSONAssert.assertEquals(stateData, content, JSONCompareMode.STRICT);
    }

    @Test
    void when_user_update_interrogation_with_incorrect_state_data_return_400() throws Exception {
        String interrogationDataStateData = """
            {
                "data":{},
                    "stateData": {
                        "state": "EACTED",
                        "date": 1111111111,
                        "currentPage": "2.3#5"
                    }
                }""";
        mockMvc.perform(patch("/api/interrogations/517046b6-bd88-47e0-838e-00d03461f592")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(interrogationDataStateData)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_update_state_data_when_su_not_exist_return_404() throws Exception {
        String stateDataJson = JsonTestHelper.getResourceFileAsString("interrogation/state_data.json");
        mockMvc.perform(put("/api/interrogations/not-exist/state-data")
                        .content(stateDataJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_update_state_data_when_su_id_invalid_return_400() throws Exception {
        mockMvc.perform(put("/api/interrogations/invalid_identifier/state-data")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_update_state_data_when_state_data_not_valid_return_400() throws Exception {
        mockMvc.perform(put("/api/interrogations/d98d28c2-1535-4fc8-a405-d6a554231bbc/state-data")
                        .content("""
                                {
                                    "state": "PLOP",
                                    "date": 1111111111,
                                    "currentPage": "2.3#5"
                                }""")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_get_state_data_when_anonymous_return_401() throws Exception {
        mockMvc.perform(get("/api/interrogations/517046b6-bd88-47e0-838e-00d03461f592/state-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void on_update_state_data_when_anonymous_return_401() throws Exception {
        mockMvc.perform(put("/api/interrogations/517046b6-bd88-47e0-838e-00d03461f592/state-data")
                        .content("{}")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void on_post_state_data_when_anonymous_return_401() throws Exception {
        mockMvc.perform(post("/api/interrogations/state-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void on_retrieve_state_datas_when_anonymous_return_401() throws Exception {
        String interrogationIds = """
                ["517046b6-bd88-47e0-838e-00d03461f592","d98d28c2-1535-4fc8-a405-d6a554231bbc","c8142dcc-c133-49aa-a969-bb9828190a2c","plop","plup"]""";
        mockMvc.perform(post("/api/interrogations/state-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(interrogationIds)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void on_retrieve_state_datas_when_interrogationUser_return_403() throws Exception {
        String interrogationIds = """
                ["517046b6-bd88-47e0-838e-00d03461f592","d98d28c2-1535-4fc8-a405-d6a554231bbc","c8142dcc-c133-49aa-a969-bb9828190a2c","plop","plup"]""";
        mockMvc.perform(post("/api/interrogations/state-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(interrogationIds)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isForbidden());
    }
}
