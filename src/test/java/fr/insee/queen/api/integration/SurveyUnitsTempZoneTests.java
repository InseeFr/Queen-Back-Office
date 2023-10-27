package fr.insee.queen.api.integration;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.*;
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
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SurveyUnitsTempZoneTests {

    @Autowired
    MockMvc mockMvc;

    @ParameterizedTest
    @ValueSource(strings = {"11","12"})
    @Order(1)
    void on_create_survey_unit_then_return_201(String surveyUnitId) throws Exception {
        // no control on questionnaire id ...
        String questionnaireId = "\"questionnaire-" + surveyUnitId + "\"";
        String tempZoneInput = """
                {
                  "data": {
                    "EXTERNAL": {
                      "ADR": "Rue des Plantes",
                      "NUMTH": "1"
                    }
                  },
                  "comment": {},
                  "personalization": [],
                  "questionnaireId":""" + questionnaireId + """
                }""";
        mockMvc.perform(post("/api/survey-unit/" + surveyUnitId + "/temp-zone")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tempZoneInput)
                )
                .andExpect(status().isCreated());
    }

    @Test
    @Order(2)
    void on_get_survey_units_return_survey_units() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/survey-units/temp-zone")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(not(is(emptyOrNullString()))))
                .andExpect(jsonPath("$[1].id").value(not(is(emptyOrNullString()))))
                .andExpect(jsonPath("$[0].date").value(not(is(emptyOrNullString()))))
                .andExpect(jsonPath("$[1].date").value(not(is(emptyOrNullString()))))
                .andReturn();
        String content = result.getResponse().getContentAsString();

        String expectedResult = """
        [
            {
              "surveyUnitId": "11",
              "userId":"GUEST",
              "surveyUnit": {
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
              "surveyUnitId": "12",
              "userId":"GUEST",
              "surveyUnit": {
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
}
