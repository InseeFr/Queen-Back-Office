package fr.insee.queen.application.surveyunit.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.queen.application.configuration.ContainerConfiguration;
import fr.insee.queen.application.surveyunit.dto.input.StateDataTypeInput;
import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SurveyUnitTests extends ContainerConfiguration {
    @Autowired
    private MockMvc mockMvc;

    private final AuthenticatedUserTestHelper authenticatedUserTestHelper = new AuthenticatedUserTestHelper();

    @Test
    void on_get_survey_unit_ids_return_ids() throws Exception {
        mockMvc.perform(get("/api/survey-units")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(16)));
    }

    @Test
    @DisplayName("Should return survey units with states")
    void on_get_survey_units_return_survey_units() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String expectedFirstResult = """
        {
                "id":"11",
                "questionnaireId":"simpsons",
                "campaignId":"SIMPSONS2020X00",
                "stateData":{
                    "state":"EXTRACTED",
                    "date":1111111111,
                    "currentPage":"2.3#5"
                }
        }""";
        mockMvc.perform(get("/api/admin/campaign/SIMPSONS2020X00/survey-units")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("state", StateDataTypeInput.EXTRACTED.name())
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contents.size()", is(3)))
                // extract and check first element
                .andExpect(result -> {
                    String responseContent = result.getResponse().getContentAsString();
                    JsonNode rootNode = mapper.readTree(responseContent);
                    JsonNode firstContent = rootNode.path("contents").get(0);
                    JsonNode expectedNode = mapper.readTree(expectedFirstResult);
                    assertThat(firstContent).isEqualTo(expectedNode);
                });
    }

    @Test
    void on_get_survey_units_by_campaign_when_campaign_not_exist_return_404() throws Exception {
        mockMvc.perform(get("/api/campaign/not-exist/survey-units")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_get_survey_units_by_campaign_when_campaign_identifier_invalid_return_400() throws Exception {
        mockMvc.perform(get("/api/campaign/invalid!identifier/survey-units")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_create_survey_unit_when_campaign_not_exist_return_404() throws Exception {
        mockMvc.perform(post("/api/campaign/not-exist/survey-unit")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SurveyUnitCommonAssertions.SURVEYUNIT_DATA)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_create_survey_unit_when_campaign_not_linked_to_questionnaire_return_400() throws Exception {
        String suData = """
                {
                    "id":"test-surveyunit2",
                    "personalization":[{"name":"whoAnswers33","value":"MrDupond"},{"name":"whoAnswers2","value":""}],
                    "data":{"EXTERNAL":{"LAST_BROADCAST":"12/07/1998"}},"comment":{"COMMENT":"acomment"},
                    "questionnaireId":"simpsons"
                }""";
        mockMvc.perform(post("/api/campaign/VQS2021X00/survey-unit")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(suData)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_create_survey_unit_when_campaign_identifier_invalid_return_400() throws Exception {
        mockMvc.perform(post("/api/campaign/invalid!identifier/survey-unit")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SurveyUnitCommonAssertions.SURVEYUNIT_DATA)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_get_survey_unit_when_identifier_invalid_return_400() throws Exception {
        mockMvc.perform(get("/api/survey-unit/invalidéidentifier")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_get_survey_unit_when_not_exist_return_404() throws Exception {
        mockMvc.perform(get("/api/survey-unit/not-exist")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_delete_survey_unit_when_identifier_invalid_return_400() throws Exception {
        mockMvc.perform(delete("/api/survey-unit/invalid!identifier")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_delete_survey_unit_when_not_exist_return_404() throws Exception {
        mockMvc.perform(delete("/api/survey-unit/not-exist")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_get_deposit_proof_return_200() throws Exception {
        mockMvc.perform(get("/api/survey-unit/11/deposit-proof")
                        .accept(MediaType.APPLICATION_PDF)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isOk());
    }


    @Test
    void on_get_survey_unit_metadata_return_survey_unit_metadata() throws Exception {
        String surveyUnitId = "LOG2021X11Web-01";
        MvcResult result = mockMvc.perform(get("/api/survey-unit/" + surveyUnitId + "/metadata")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedResult = """
        {
            "context":"household",
            "personalization":[],
            "label":"Enquête logement pour la recette technique",
            "objectives":"Cette enquête permet de connaître votre logement mais surtout nos applis",
            "variables":[
                {"name":"Enq_CaractereObligatoire","value":true},
                {"name":"Enq_NumeroVisa","value":"2021A054EC"},
                {"name":"Enq_MinistereTutelle","value":"de l'Économie, des Finances et de la Relance"},
                {"name":"Enq_ParutionJo","value":true},{"name":"Enq_DateParutionJo","value":"23/11/2020"},
                {"name":"Enq_RespOperationnel","value":"L’Institut national de la statistique et des études économiques (Insee)"},
                {"name":"Enq_RespTraitement","value":"l'Insee"},{"name":"Enq_AnneeVisa","value":"2021"},
                {"name":"Loi_statistique","value":"https://www.legifrance.gouv.fr/affichTexte.do?cidTexte=JORFTEXT000000888573"},
                {"name":"Loi_rgpd","value":"https://eur-lex.europa.eu/legal-content/FR/TXT/?uri=CELEX%3A32016R0679"},
                {"name":"Loi_informatique","value":"https://www.legifrance.gouv.fr/affichTexte.do?cidTexte=JORFTEXT000000886460"}
            ],
            "logos": {
                "main": {
                    "url": "https://insee.fr/logo1.png",
                    "label": "logo1"
                },
                "secondaries": [
                    {
                        "url": "https://insee.fr/logo2.png",
                        "label": "logo2"
                    },
                    {
                        "url": "https://insee.fr/logo3.png",
                        "label": "logo3"
                    }
                ]
            }
        }""";
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void on_get_survey_unit_metadata_when_invalid_metadata_return_404() throws Exception {
        String surveyUnitId = "11";
        mockMvc.perform(get("/api/survey-unit/" + surveyUnitId + "/metadata")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_get_deposit_proof_when_not_exist_return_404() throws Exception {
        mockMvc.perform(get("/api/survey-unit/not-exist/deposit-proof")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void when_authenticated_non_admin_user_access_admin_endpoints_return_403() throws Exception {
        mockMvc.perform(get("/api/survey-units")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNonAdminUser()))
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/campaign/VQS2021X00/survey-unit")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SurveyUnitCommonAssertions.SURVEYUNIT_DATA)
                        .with(authentication(authenticatedUserTestHelper.getNonAdminUser()))
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/survey-unit/11")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNonAdminUser()))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void when_user_update_surveyunit_with_incorrect_identifier_return_400() throws Exception {
        String surveyUnitDataStateData = """
            {
                "data":""" + SurveyUnitCommonAssertions.SURVEYUNIT_DATA + ", " +
                """
                    "stateData": {
                        "state": "EXTRACTED",
                        "date": 1111111111,
                        "currentPage": "2.3#5"
                    }
                }""";
        mockMvc.perform(patch("/api/survey-unit/invalid!identifier")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surveyUnitDataStateData)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void when_non_interviewer_get_surveyunits_return_403() throws Exception {
        mockMvc.perform(get("/api/survey-units/interviewer")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNonInterviewerUser()))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void when_anonymous_user_access_authenticated_endpoints_return_401() throws Exception {
        List<String> getEndPoints = List.of(
                "/api/survey-units",
                "/api/survey-unit/11",
                "/api/survey-unit/11/deposit-proof",
                "/api/survey-unit/11/metadata",
                "/api/campaign/VQS2021X00/survey-units",
                "/api/survey-units/interviewer");
        for (String getEndPoint : getEndPoints) {
            mockMvc.perform(get(getEndPoint)
                            .accept(MediaType.APPLICATION_JSON)
                            .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                    )
                    .andExpect(status().isUnauthorized());
        }

        mockMvc.perform(post("/api/campaign/VQS2021X00/survey-unit")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SurveyUnitCommonAssertions.SURVEYUNIT_DATA)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());

        mockMvc.perform(put("/api/survey-unit/11")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SurveyUnitCommonAssertions.SURVEYUNIT_DATA)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());

        mockMvc.perform(patch("/api/survey-unit/11")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SurveyUnitCommonAssertions.SURVEYUNIT_DATA)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/api/survey-unit/11")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }
}
