package fr.insee.queen.application.interrogation.integration;

import fr.insee.queen.application.configuration.ScriptConstants;
import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;

import org.junit.jupiter.api.Test;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class InterrogationsTempZoneIT {

    @Autowired
    MockMvc mockMvc;

    private final AuthenticatedUserTestHelper authenticatedUserTestHelper = new AuthenticatedUserTestHelper();

    @Test
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_create_interrogation_then_interrogation_created() throws Exception {
        String interrogationId = "538d89c2-1047-48f7-8c16-02e9f41a8093";
        // no control on questionnaire id ...
        String tempZoneInput = """
                {
                  "data": {
                    "EXTERNAL": {
                      "ADR": "Rue des Plantes",
                      "NUMTH": "1"
                    }
                  },
                  "comment": {"plip": "plop"},
                  "personalization": [{"name": "name", "value": "value"}],
                  "questionnaireId":"questionnaire-11",
                  "stateData": {
                    "state": "EXTRACTED",
                    "date": 123456789,
                    "currentPage": "2.3"
                  }
                }""";
        mockMvc.perform(post("/api/interrogation/" + interrogationId + "/temp-zone")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tempZoneInput)
                        .with(authentication(authenticatedUserTestHelper.getManagerUser()))
                )
                .andExpect(status().isCreated());

        String expressionFilter = "$[?(@.interrogationId == '" + interrogationId + "')]";
        MvcResult result = mockMvc.perform(get("/api/interrogations/temp-zone")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getManagerUser()))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(3)))
                .andExpect(jsonPath(expressionFilter + ".id", is(not(empty()))))
                .andExpect(jsonPath(expressionFilter + ".date", is(not(empty()))))
                .andExpect(jsonPath(expressionFilter + ".userId").value("dupont-identifier"))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedResult = """
        [
            {},
            {},
            {
                "interrogation":""" + tempZoneInput + """
            }
        ]""";
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.LENIENT);
    }

    @Test
    void on_get_interrogations_return_interrogations() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/interrogations/temp-zone")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getManagerUser()))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();

        String expectedResult = """
                [
                    {
                      "id":"42858b14-2a0c-4d17-afd0-f50a0f9a8dd5",
                      "interrogationId": "517046b6-bd88-47e0-838e-00d03461f592",
                      "userId": "user-id",
                      "date":900000000,
                      "interrogation": {
                          "data": {
                            "EXTERNAL": {
                              "ADR": "Rue des Plantes",
                              "NUMTH": "1"
                            }
                          },
                          "comment": {},
                          "personalization": [],
                          "questionnaireId": "questionnaire-11"
                      }
                    },
                    {
                      "id":"6fcbbd84-3464-4290-b8fc-cdf0082ee339",
                      "interrogationId": "d98d28c2-1535-4fc8-a405-d6a554231bbc",
                      "userId":"user-id",
                      "date":900000000,
                      "interrogation": {
                          "data": {
                            "EXTERNAL": {
                              "ADR": "Rue des Plantes",
                              "NUMTH": "1"
                            }
                          },
                          "comment": {},
                          "personalization": [],
                          "questionnaireId": "questionnaire-12"
                      }
                    }
                ]""";
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.LENIENT);
    }

    @Test
    void on_create_interrogation_when_non_interviewer_then_return_403() throws Exception {
        // no control on questionnaire id ...
        String tempZoneInput = "{}";
        mockMvc.perform(post("/api/interrogation/517046b6-bd88-47e0-838e-00d03461f592/temp-zone")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tempZoneInput)
                        .with(authentication(authenticatedUserTestHelper.getNonInterviewerUser()))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void on_create_interrogation_when_anonymous_user_then_return_401() throws Exception {
        // no control on questionnaire id ...
        String tempZoneInput = "{}";
        mockMvc.perform(post("/api/interrogation/517046b6-bd88-47e0-838e-00d03461f592/temp-zone")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tempZoneInput)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void on_get_interrogations_when_anonymous_user_then_return_401() throws Exception {
        // no control on questionnaire id ...
        mockMvc.perform(get("/api/interrogations/temp-zone")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void on_get_interrogations_when_interrogationUser_then_return_403() throws Exception {
        // no control on questionnaire id ...
        mockMvc.perform(get("/api/interrogations/temp-zone")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isForbidden());
    }
}
