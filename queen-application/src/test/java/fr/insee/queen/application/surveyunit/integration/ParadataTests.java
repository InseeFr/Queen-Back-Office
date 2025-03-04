package fr.insee.queen.application.surveyunit.integration;

import fr.insee.queen.application.configuration.ContainerConfiguration;
import fr.insee.queen.application.configuration.ScriptConstants;
import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class ParadataTests extends ContainerConfiguration {
    @Autowired
    private MockMvc mockMvc;

    private final AuthenticatedUserTestHelper authenticatedUserTestHelper = new AuthenticatedUserTestHelper();

    @Test
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_create_paradata_return_created() throws Exception {
        String paradataInput = """
                {
                    "idSU": "11",
                    "events": [
                        {
                            "page": "83",
                                "type": "click",
                                "idSession": "80c78148-bbja-4909-bc10-j9154dc1198f",
                                "timestamp": 1694009481932,
                                "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:102.0) Gecko/20100101 Firefox/102.0",
                                "idSurveyUnit": "CDVO00008",
                                "idOrchestrator": "orchestrator-collect",
                                "idQuestionnaire": "simpsons",
                                "idParadataObject": "next-button-orchestrator-collect",
                                "typeParadataObject": "orchestrator"
                        }
                    ]
                }""";
        mockMvc.perform(post("/api/paradata")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(paradataInput)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"\"idSU\": \"invalid-unit\",", "", "\"idSU\": {},"})
    void on_create_paradata_when_survey_unit_invalid_return_404(String surveyUnitJson) throws Exception {
        String paradataInput = """
                {
                """ + surveyUnitJson + """  
                    "events": [
                        {
                            "page": "83",
                                "type": "click",
                                "idSession": "80c78148-bbja-4909-bc10-j9154dc1198f",
                                "timestamp": 1694009481932,
                                "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:102.0) Gecko/20100101 Firefox/102.0",
                                "idSurveyUnit": "CDVO00008",
                                "idOrchestrator": "orchestrator-collect",
                                "idQuestionnaire": "simpsons",
                                "idParadataObject": "next-button-orchestrator-collect",
                                "typeParadataObject": "orchestrator"
                        }
                    ]
                }""";
        mockMvc.perform(post("/api/paradata")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(paradataInput)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_create_paradata_when_anonymous_user_return_401() throws Exception {
        String paradataInput = """
                {
                    "idSU": "11",
                    "events": [
                        {}
                    ]
                }""";
        mockMvc.perform(post("/api/paradata")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                        .content(paradataInput)
                )
                .andExpect(status().isUnauthorized());
    }
}
