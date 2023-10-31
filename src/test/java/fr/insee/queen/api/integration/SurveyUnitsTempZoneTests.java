package fr.insee.queen.api.integration;

import fr.insee.queen.api.configuration.auth.AuthorityRoleEnum;
import fr.insee.queen.api.utils.AuthenticatedUserTestHelper;
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
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
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

    private final AuthenticatedUserTestHelper authenticatedUserTestHelper = new AuthenticatedUserTestHelper();

    private final Authentication adminUser = authenticatedUserTestHelper.getAuthenticatedUser(
            AuthorityRoleEnum.ADMIN,
            AuthorityRoleEnum.WEBCLIENT);

    private final Authentication nonAdminUser = authenticatedUserTestHelper.getAuthenticatedUser(
            AuthorityRoleEnum.REVIEWER,
            AuthorityRoleEnum.REVIEWER_ALTERNATIVE,
            AuthorityRoleEnum.INTERVIEWER);

    private final Authentication anonymousUser = authenticatedUserTestHelper.getNotAuthenticatedUser();

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
                        .with(authentication(adminUser))
                )
                .andExpect(status().isCreated());
    }

    @Test
    @Order(2)
    void on_get_survey_units_return_survey_units() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/survey-units/temp-zone")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(nonAdminUser))
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
              "userId": "dupont-identifier",
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
              "userId":"dupont-identifier",
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

    @Test
    void on_create_survey_unit_when_not_interviewer_then_return_403() throws Exception {
        // no control on questionnaire id ...
        String tempZoneInput = "{}";
        mockMvc.perform(post("/api/survey-unit/11/temp-zone")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tempZoneInput)
                        .with(authentication(authenticatedUserTestHelper.getAuthenticatedUser(AuthorityRoleEnum.REVIEWER_ALTERNATIVE, AuthorityRoleEnum.REVIEWER)))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void on_create_survey_unit_when_anonymous_user_then_return_401() throws Exception {
        // no control on questionnaire id ...
        String tempZoneInput = "{}";
        mockMvc.perform(post("/api/survey-unit/11/temp-zone")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tempZoneInput)
                        .with(authentication(anonymousUser))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void on_get_survey_units_when_anonymous_user_then_return_401() throws Exception {
        // no control on questionnaire id ...
        mockMvc.perform(get("/api/survey-units/temp-zone")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(anonymousUser))
                )
                .andExpect(status().isUnauthorized());
    }
}
