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
    public static final String INTERROGATION_DATA = """
            {
                "id":"aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa01",
                "surveyUnitId": "test-survey-unit-id-55",
                "personalization":[{"name":"whoAnswers33","value":"MrDupond"},{"name":"whoAnswers2","value":""}],
                "data":{"EXTERNAL":{"LAST_BROADCAST":"12/07/1998"}},"comment":{"COMMENT":"acomment"},
                "stateData":{"state":"EXTRACTED","currentPage":"2.3#5"},
                "questionnaireId":"VQS2021X00"
            }""";

    public static final String SURVEYUNIT_DATA_SAVED = """
            {
                "id":"aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa01",
                "surveyUnitId": "test-survey-unit-id-55",
                "personalization":[{"name":"whoAnswers33","value":"MrDupond"},{"name":"whoAnswers2","value":""}],
                "data":{"EXTERNAL":{"LAST_BROADCAST":"12/07/1998"}},"comment":{"COMMENT":"acomment"},
                "stateData":{"state":"EXTRACTED","date":1747395350727,"currentPage":"2.3#5"},
                "questionnaireId":"VQS2021X00"
            }""";
    private final MockMvc mockMvc;
    private final AuthenticatedUserTestHelper authenticatedUserTestHelper = new AuthenticatedUserTestHelper();

    void on_get_interrogations_by_campaign_return_interrogations() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/campaign/SIMPSONS2020X00/interrogations")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNonAdminUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        String expectedResult = """
                [
                    {"id":"517046b6-bd88-47e0-838e-00d03461f592","questionnaireId":"simpsons"},
                    {"id":"d98d28c2-1535-4fc8-a405-d6a554231bbc","questionnaireId":"simpsons"},
                    {"id":"c8142dcc-c133-49aa-a969-bb9828190a2c","questionnaireId":"simpsonsV2"},
                    {"id":"45c78a3e-f3b6-4d69-bd58-d2ca749dd7cd","questionnaireId":"simpsonsV2"}
                ]""";
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    void on_create_interrogation_then_interrogation_is_saved() throws Exception {
        String interrogationDataCreated = """
            {
                "id":"aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa01",
                "personalization":[{"name":"whoAnswers33","value":"MrDupond"},{"name":"whoAnswers2","value":""}],
                "data":{"EXTERNAL":{"LAST_BROADCAST":"12/07/1998"}},"comment":{"COMMENT":"acomment"},
                "stateData":{"state":"EXTRACTED","date":1111111111,"currentPage":"2.3#5"},
                "questionnaireId":"VQS2021X00"
            }""";

        mockMvc.perform(post("/api/campaign/VQS2021X00/interrogation")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INTERROGATION_DATA)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isCreated());

        on_get_interrogation_return_interrogation("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa01", interrogationDataCreated);
    }

    void on_update_interrogation_then_interrogation_is_saved() throws Exception {
        String interrogationDataUpdated = """
                {
                    "id":"517046b6-bd88-47e0-838e-00d03461f592",
                    "surveyUnitId": "survey-unit-id12",
                    "personalization":[{"name":"whoAnswers33","value":"MrDupond"},{"name":"whoAnswers2","value":""}],
                    "data":{"EXTERNAL":{"LAST_BROADCAST":"12/07/1998"}},"comment":{"COMMENT":"COMMENT UPDATED"},
                    "stateData":{"state":"EXTRACTED","currentPage":"2.3#5"},
                    "questionnaireId":"simpsonsV2"
                }""";



        String interrogationDataResponseExpected = """
                {
                    "id":"517046b6-bd88-47e0-838e-00d03461f592",
                    "personalization":[{"name":"whoAnswers33","value":"MrDupond"},{"name":"whoAnswers2","value":""}],
                    "data":{"EXTERNAL":{"LAST_BROADCAST":"12/07/1998"}},"comment":{"COMMENT":"COMMENT UPDATED"},
                    "stateData":{"state":"EXTRACTED","date":1747395350727,"currentPage":"2.3#5"},
                    "questionnaireId":"simpsonsV2"
                }""";
        mockMvc.perform(post("/api/campaign/LOG2021X11Tel/interrogation")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(interrogationDataUpdated)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isOk());

        on_get_interrogation_return_interrogation("517046b6-bd88-47e0-838e-00d03461f592", interrogationDataResponseExpected);
    }

    void on_update_with_put_interrogation_then_interrogation_is_saved() throws Exception {
        String interrogationDataUpdated = """
                {
                    "id":"517046b6-bd88-47e0-838e-00d03461f592",
                    "personalization":[{"name":"whoAnswers33","value":"MrDupond"},{"name":"whoAnswers2","value":""}],
                    "data":{"EXTERNAL":{"LAST_BROADCAST":"12/07/1998"}},"comment":{"COMMENT":"COMMENT UPDATED 2"},
                    "stateData":{"state":"EXTRACTED","date":1111111111,"currentPage":"2.3#5"},
                    "questionnaireId":"simpsons"
                }""";
        mockMvc.perform(put("/api/interrogation/517046b6-bd88-47e0-838e-00d03461f592")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(interrogationDataUpdated)
                        .with(authentication(authenticatedUserTestHelper.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER)))
                )
                .andExpect(status().isOk());

        on_get_interrogation_return_interrogation("517046b6-bd88-47e0-838e-00d03461f592", interrogationDataUpdated);
    }

    void on_create_interrogation_without_statedata_then_interrogation_is_saved() throws Exception {
        String interrogationDataWithoutState = """
                {
                    "id":"aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa01",
                    "surveyUnitId": "survey-unit-id",
                    "personalization":[{"name":"whoAnswers33","value":"MrDupond"},{"name":"whoAnswers2","value":""}],
                    "data":{"EXTERNAL":{"LAST_BROADCAST":"12/07/1998"}},"comment":{"COMMENT":"acomment"},
                    "questionnaireId":"VQS2021X00"
                }""";
        String interrogationDataResponseWithoutState = """
                {
                    "id":"aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa01",
                    "personalization":[{"name":"whoAnswers33","value":"MrDupond"},{"name":"whoAnswers2","value":""}],
                    "data":{"EXTERNAL":{"LAST_BROADCAST":"12/07/1998"}},"comment":{"COMMENT":"acomment"},
                    "questionnaireId":"VQS2021X00"
                }""";
        mockMvc.perform(post("/api/campaign/VQS2021X00/interrogation")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(interrogationDataWithoutState)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isCreated());
        on_get_interrogation_return_interrogation("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa01", interrogationDataResponseWithoutState);
    }

    void on_delete_interrogation_process_deletion() throws Exception {
        String interrogationId = "517046b6-bd88-47e0-838e-00d03461f592";
        mockMvc.perform(delete("/api/interrogation/" + interrogationId)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/interrogation/" + interrogationId)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isNotFound());
    }

    void when_get_interrogations_for_interviewers_return_interrogations() throws Exception {
        mockMvc.perform(get("/api/interrogations/interviewer")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(16)))
                .andExpect(jsonPath("$[0].id").value("31e0537d-01ab-4402-9a59-d001d3ba00fd"))
                .andExpect(jsonPath("$[0].questionnaireId").value("LOG2021X11Tel"))
                .andExpect(jsonPath("$[0].personalization.size()", is(0)))
                .andExpect(jsonPath("$[0].data").value(is(not(emptyOrNullString()))))
                .andExpect(jsonPath("$[0].stateData.state").value(StateDataType.INIT.name()))
                .andExpect(jsonPath("$[0].stateData.date").value(900000000))
                .andExpect(jsonPath("$[0].stateData.currentPage").value("1"));
    }

    private void on_get_interrogation_return_interrogation(String interrogationId, String expectedResult) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/interrogation/" + interrogationId)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.NON_EXTENSIBLE);
    }
}