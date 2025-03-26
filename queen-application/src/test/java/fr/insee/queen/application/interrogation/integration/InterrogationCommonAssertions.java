package fr.insee.queen.application.interrogation.integration;

import fr.insee.queen.application.configuration.auth.AuthorityRoleEnum;
import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;
import fr.insee.queen.domain.interrogation.model.StateDataType;
import lombok.RequiredArgsConstructor;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
class InterrogationCommonAssertions {
    public static final String SURVEYUNIT_DATA = """
            {
                "id":"test-interrogation",
                "personalization":[{"name":"whoAnswers33","value":"MrDupond"},{"name":"whoAnswers2","value":""}],
                "data":{"EXTERNAL":{"LAST_BROADCAST":"12/07/1998"}},"comment":{"COMMENT":"acomment"},
                "stateData":{"state":"EXTRACTED","date":1111111111,"currentPage":"2.3#5"},
                "questionnaireId":"VQS2021X00"
            }""";
    private final MockMvc mockMvc;
    private final AuthenticatedUserTestHelper authenticatedUserTestHelper = new AuthenticatedUserTestHelper();

    void on_get_interrogations_by_campaign_return_interrogations() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/campaign/SIMPSONS2020X00/survey-units")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNonAdminUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        String expectedResult = """
                [
                    {"id":"11","questionnaireId":"simpsons"},
                    {"id":"12","questionnaireId":"simpsons"},
                    {"id":"13","questionnaireId":"simpsonsV2"},
                    {"id":"14","questionnaireId":"simpsonsV2"}
                ]""";
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    void on_create_interrogation_then_interrogation_is_saved() throws Exception {
        mockMvc.perform(post("/api/campaign/VQS2021X00/survey-unit")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SURVEYUNIT_DATA)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isCreated());

        on_get_interrogation_return_interrogation("test-interrogation", SURVEYUNIT_DATA);
    }

    void on_update_interrogation_then_interrogation_is_saved() throws Exception {
        String interrogationDataUpdated = """
                {
                    "id":"11",
                    "personalization":[{"name":"whoAnswers33","value":"MrDupond"},{"name":"whoAnswers2","value":""}],
                    "data":{"EXTERNAL":{"LAST_BROADCAST":"12/07/1998"}},"comment":{"COMMENT":"COMMENT UPDATED"},
                    "stateData":{"state":"EXTRACTED","date":1111111111,"currentPage":"2.3#5"},
                    "questionnaireId":"simpsonsV2"
                }""";
        mockMvc.perform(post("/api/campaign/LOG2021X11Tel/survey-unit")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(interrogationDataUpdated)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isOk());

        on_get_interrogation_return_interrogation("11", interrogationDataUpdated);
    }

    void on_update_with_put_interrogation_then_interrogation_is_saved() throws Exception {
        String interrogationDataUpdated = """
                {
                    "id":"11",
                    "personalization":[{"name":"whoAnswers33","value":"MrDupond"},{"name":"whoAnswers2","value":""}],
                    "data":{"EXTERNAL":{"LAST_BROADCAST":"12/07/1998"}},"comment":{"COMMENT":"COMMENT UPDATED 2"},
                    "stateData":{"state":"EXTRACTED","date":1111111111,"currentPage":"2.3#5"},
                    "questionnaireId":"simpsons"
                }""";
        mockMvc.perform(put("/api/survey-unit/11")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(interrogationDataUpdated)
                        .with(authentication(authenticatedUserTestHelper.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER)))
                )
                .andExpect(status().isOk());

        on_get_interrogation_return_interrogation("11", interrogationDataUpdated);
    }

    void on_create_interrogation_without_statedata_then_interrogation_is_saved() throws Exception {
        String interrogationDataWithoutState = """
                {
                    "id":"test-interrogation2",
                    "personalization":[{"name":"whoAnswers33","value":"MrDupond"},{"name":"whoAnswers2","value":""}],
                    "data":{"EXTERNAL":{"LAST_BROADCAST":"12/07/1998"}},"comment":{"COMMENT":"acomment"},
                    "questionnaireId":"VQS2021X00"
                }""";
        String interrogationDataResponseWithoutState = """
                {
                    "id":"test-interrogation2",
                    "personalization":[{"name":"whoAnswers33","value":"MrDupond"},{"name":"whoAnswers2","value":""}],
                    "data":{"EXTERNAL":{"LAST_BROADCAST":"12/07/1998"}},"comment":{"COMMENT":"acomment"},
                    "questionnaireId":"VQS2021X00"
                }""";
        mockMvc.perform(post("/api/campaign/VQS2021X00/survey-unit")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(interrogationDataWithoutState)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isCreated());
        on_get_interrogation_return_interrogation("test-interrogation2", interrogationDataResponseWithoutState);
    }

    void on_delete_interrogation_process_deletion() throws Exception {
        String interrogationId = "11";
        mockMvc.perform(delete("/api/survey-unit/" + interrogationId)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/survey-unit/" + interrogationId)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isNotFound());
    }

    void when_get_interrogations_for_interviewers_return_interrogations() throws Exception {
        mockMvc.perform(get("/api/survey-units/interviewer")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(16)))
                .andExpect(jsonPath("$[0].id").value("11"))
                .andExpect(jsonPath("$[0].questionnaireId").value("simpsons"))
                .andExpect(jsonPath("$[0].personalization.size()", is(2)))
                .andExpect(jsonPath("$[0].data").value(is(not(emptyOrNullString()))))
                .andExpect(jsonPath("$[0].stateData.state").value(StateDataType.EXTRACTED.name()))
                .andExpect(jsonPath("$[0].stateData.date").value(1111111111))
                .andExpect(jsonPath("$[0].stateData.currentPage").value("2.3#5"));
    }

    private void on_get_interrogation_return_interrogation(String interrogationId, String expectedResult) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/survey-unit/" + interrogationId)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.NON_EXTENSIBLE);
    }
}