package fr.insee.queen.api.integration;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.api.JsonHelper;
import fr.insee.queen.api.dto.input.QuestionnaireModelInputDto;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

import java.util.Set;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QuestionnaireTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void on_get_questionnaires_by_campaign_return_questionnaires() throws Exception {
        mockMvc.perform(get("/api/campaign/SIMPSONS2020X00/questionnaires")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)));
    }

    @Test
    void on_get_questionnaires_by_campaign_when_campaign_not_exist_return_404() throws Exception {
        mockMvc.perform(get("/api/campaign/not-exist/questionnaires")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_get_questionnaires_by_campaign_when_campaign_identifier_invalid_return_400() throws Exception {
        mockMvc.perform(get("/api/campaign/invalid_identifier/questionnaires")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_get_questionnaire_ids_by_campaign_return_questionnaires() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/campaign/SIMPSONS2020X00/questionnaire-id")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedResult = """
            [
                {"questionnaireId": "simpsons"},
                {"questionnaireId": "simpsonsV2"}
            ]""";
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void on_get_questionnaire_ids_by_campaign_when_campaign_not_exist_return_404() throws Exception {
        mockMvc.perform(get("/api/campaign/not-exist/questionnaire-id")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_get_questionnaire_ids_by_campaign_when_campaign_identifier_invalid_return_400() throws Exception {
        mockMvc.perform(get("/api/campaign/invalid_identifier/questionnaire-id")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_get_questionnaire_when_identifier_invalid_return_400() throws Exception {
        mockMvc.perform(get("/api/questionnaire/invalid_identifier")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_get_questionnaire_when_not_exist_return_404() throws Exception {
        mockMvc.perform(get("/api/questionnaire/not-exist")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @CsvSource("simpsons,db/dataset/simpsons.json")
    void on_get_questionnaire_return_correct_questionnaire(String questionnaireId, String questionnaireFile) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/questionnaire/" + questionnaireId)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        String expectedResult = "{ \"value\": " + JsonHelper.getResourceFileAsString(questionnaireFile) + "}";
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @ParameterizedTest
    @CsvSource("questionnaire-creation-test,db/dataset/simpsons.json")
    void on_create_questionnaire_check_questionnaire_created(String questionnaireId, String questionnaireFile) throws Exception {
        ObjectNode questionnaireJson = JsonHelper.getResourceFileAsObjectNode(questionnaireFile);
        Set<String> nomenclatures = Set.of("cities2019", "regions2019");
        QuestionnaireModelInputDto questionnaire = new QuestionnaireModelInputDto(questionnaireId, "label questionnaire", questionnaireJson, nomenclatures);
        mockMvc.perform(post("/api/questionnaire-models")
                        .content(JsonHelper.getObjectAsJsonString(questionnaire))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(get("/api/questionnaire/" + questionnaireId)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        String expectedResult = "{ \"value\": " + JsonHelper.getResourceFileAsString(questionnaireFile) + "}";
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void on_get_list_questionnaire_for_each_surveyunit_in_request_return_questionnaire_list() throws Exception {
        String surveyUnitIdsInput = """
                [
                  "11",
                  "12",
                  "20",
                  "456789",
                  "123456"
                ]
                """;
        MvcResult result = mockMvc.perform(post("/api/survey-units/questionnaire-model-id")
                        .content(surveyUnitIdsInput)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedResult = """
        {
          "surveyUnitOK": [
            {"id": "11", "questionnaireId": "simpsons"},
            {"id": "12", "questionnaireId": "simpsons"},
            {"id": "20", "questionnaireId": "VQS2021X00"}
          ],
          "surveyUnitNOK": [
            {"id": "456789"},
            {"id": "123456"}
          ]
        }
        """;
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.NON_EXTENSIBLE);
    }
}
