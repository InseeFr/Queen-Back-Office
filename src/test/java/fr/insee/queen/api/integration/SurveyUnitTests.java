package fr.insee.queen.api.integration;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SurveyUnitTests {
    @Autowired
    private MockMvc mockMvc;

    private final String surveyUnitData = """
                {
                    "id":"test-surveyunit",
                    "personalization":[{"name":"whoAnswers33","value":"MrDupond"},{"name":"whoAnswers2","value":""}],
                    "data":{"EXTERNAL":{"LAST_BROADCAST":"12/07/1998"}},"comment":{"COMMENT":"acomment"},
                    "stateData":{"state":"EXTRACTED","date":1111111111,"currentPage":"2.3#5"},
                    "questionnaireId":"VQS2021X00"
                }""";

    // tests on list before tests on create/update
    @Test
    @Order(1)
    void on_get_survey_units_by_campaign_return_survey_units() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/campaign/SIMPSONS2020X00/survey-units")
                        .accept(MediaType.APPLICATION_JSON)
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

    @Test
    @Order(2)
    void on_get_survey_unit_ids_return_ids() throws Exception {
        mockMvc.perform(get("/api/survey-units")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(14)));
    }
    @Test
    @Order(3)
    void on_create_survey_unit_then_survey_unit_is_saved() throws Exception {
        mockMvc.perform(post("/api/campaign/VQS2021X00/survey-unit")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surveyUnitData)
                )
                .andExpect(status().isCreated());

        on_get_survey_unit_return_survey_unit("test-surveyunit", surveyUnitData);
    }

    @Test
    @Order(4)
    void on_create_survey_unit_without_statedata_then_survey_unit_is_saved() throws Exception {
        String surveyUnitDataWithoutState = """
                {
                    "id":"test-surveyunit2",
                    "personalization":[{"name":"whoAnswers33","value":"MrDupond"},{"name":"whoAnswers2","value":""}],
                    "data":{"EXTERNAL":{"LAST_BROADCAST":"12/07/1998"}},"comment":{"COMMENT":"acomment"},
                    "questionnaireId":"VQS2021X00"
                }""";
        String surveyUnitDataResponseWithoutState = """
                {
                    "id":"test-surveyunit2",
                    "personalization":[{"name":"whoAnswers33","value":"MrDupond"},{"name":"whoAnswers2","value":""}],
                    "data":{"EXTERNAL":{"LAST_BROADCAST":"12/07/1998"}},"comment":{"COMMENT":"acomment"},
                    "stateData": {"state": null,"date": null,"currentPage": null},
                    "questionnaireId":"VQS2021X00"
                }""";
        mockMvc.perform(post("/api/campaign/VQS2021X00/survey-unit")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surveyUnitDataWithoutState)
                )
                .andExpect(status().isCreated());
        on_get_survey_unit_return_survey_unit("test-surveyunit2", surveyUnitDataResponseWithoutState);
    }

    private void on_get_survey_unit_return_survey_unit(String surveyUnitId, String expectedResult) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/survey-unit/" + surveyUnitId)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @ParameterizedTest
    @ValueSource(strings = {"test-surveyunit", "test-surveyunit2"})
    @Order(5)
    void on_delete_survey_unit_process_deletion(String surveyUnitId) throws Exception {
        mockMvc.perform(delete("/api/survey-unit/" + surveyUnitId)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/survey-unit/" + surveyUnitId)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_get_survey_units_by_campaign_when_campaign_not_exist_return_404() throws Exception {
        mockMvc.perform(get("/api/campaign/not-exist/survey-units")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_get_survey_units_by_campaign_when_campaign_identifier_invalid_return_400() throws Exception {
        mockMvc.perform(get("/api/campaign/invalid_identifier/survey-units")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_create_survey_unit_when_campaign_not_exist_return_404() throws Exception {
        mockMvc.perform(post("/api/campaign/not-exist/survey-unit")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surveyUnitData)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_create_survey_unit_when_campaign_identifier_invalid_return_400() throws Exception {
        mockMvc.perform(post("/api/campaign/invalid_identifier/survey-unit")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surveyUnitData)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_get_survey_unit_when_identifier_invalid_return_400() throws Exception {
        mockMvc.perform(get("/api/survey-unit/invalid_identifier")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_get_survey_unit_when_not_exist_return_404() throws Exception {
        mockMvc.perform(get("/api/survey-unit/not-exist")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_delete_survey_unit_when_identifier_invalid_return_400() throws Exception {
        mockMvc.perform(delete("/api/survey-unit/invalid_identifier")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_delete_survey_unit_when_not_exist_return_404() throws Exception {
        mockMvc.perform(delete("/api/survey-unit/not-exist")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_get_deposit_proof_return_200() throws Exception {
        mockMvc.perform(get("/api/survey-unit/11/deposit-proof")
                        .accept(MediaType.APPLICATION_PDF)
                )
                .andExpect(status().isOk());
    }

    @Test
    void on_get_deposit_proof_when_not_exist_return_404() throws Exception {
        mockMvc.perform(get("/api/survey-unit/not-exist/deposit-proof")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }
}
