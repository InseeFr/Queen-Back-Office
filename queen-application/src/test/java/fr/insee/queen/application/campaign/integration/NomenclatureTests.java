package fr.insee.queen.application.campaign.integration;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import fr.insee.queen.application.campaign.dto.input.NomenclatureCreationData;
import fr.insee.queen.application.configuration.ScriptConstants;
import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;
import fr.insee.queen.application.utils.JsonTestHelper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration
@AutoConfigureEmbeddedDatabase
@AutoConfigureMockMvc
class NomenclatureTests {
    @Autowired
    private MockMvc mockMvc;

    private final AuthenticatedUserTestHelper authenticatedUserTestHelper = new AuthenticatedUserTestHelper();

    @Test
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_post_nomenclature_json_nomenclature_created() throws Exception {
        String nomenclatureName = "plop";
        String nomenclatureJsonFile = "nomenclature/regions-2019.json";
        on_get_nomenclature_when_nomenclature_not_exists_return_404(nomenclatureName);
        ArrayNode jsonNomenclature = JsonTestHelper.getResourceFileAsArrayNode(nomenclatureJsonFile);
        NomenclatureCreationData nomenclature = new NomenclatureCreationData(nomenclatureName, "label", jsonNomenclature);
        mockMvc.perform(post("/api/nomenclature")
                        .content(JsonTestHelper.getObjectAsJsonString(nomenclature))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isOk());
        on_get_nomenclature_return_json_nomenclature(nomenclatureName, nomenclatureJsonFile);
    }

    @ParameterizedTest
    @CsvSource(value = {"regions2019,nomenclature/regions-2019.json"})
    void on_get_nomenclature_return_json_nomenclature(String nomenclatureName, String nomenclatureJsonFile) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/nomenclature/" + nomenclatureName)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser())))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedResult = JsonTestHelper.getResourceFileAsString(nomenclatureJsonFile);
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.STRICT);
    }

    @Test
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_post_nomenclature_json_nomenclature_updated() throws Exception {
        String nomenclatureName = "regions2019";
        String nomenclatureJsonFile = "nomenclature/regions-2019-update.json";
        ArrayNode jsonNomenclature = JsonTestHelper.getResourceFileAsArrayNode(nomenclatureJsonFile);
        NomenclatureCreationData nomenclature = new NomenclatureCreationData(nomenclatureName, "label", jsonNomenclature);
        mockMvc.perform(post("/api/nomenclature")
                        .content(JsonTestHelper.getObjectAsJsonString(nomenclature))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isOk());
        on_get_nomenclature_return_json_nomenclature(nomenclatureName, nomenclatureJsonFile);
    }

    @Test
    void on_get_nomenclatures_return_all_nomenclatures() throws Exception {
        mockMvc.perform(get("/api/nomenclatures")
                        .with(authentication(authenticatedUserTestHelper.getManagerUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(6)))
                .andExpect(jsonPath("$[*]").value(containsInAnyOrder("cities2019", "regions2019", "L_DEPNAIS", "L_NATIONETR", "L_PAYSNAIS", "cog-communes")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"sqqghk"})
    void on_get_nomenclature_when_nomenclature_not_exists_return_404(String nomenclatureName) throws Exception {
        mockMvc.perform(get("/api/nomenclature/" + nomenclatureName)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser())))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void on_get_nomenclature_when_nomenclature_id_not_valid_return_400() throws Exception {
        mockMvc.perform(get("/api/nomenclature/s4-%")
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser())))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void on_get_required_nomenclatures_return_nomenclatures_for_campaign() throws Exception {
        mockMvc.perform(get("/api/campaign/SIMPSONS2020X00/required-nomenclatures")
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[*]").value(containsInAnyOrder("cities2019", "regions2019")));
    }

    @Test
    void on_get_required_nomenclatures_return_nomenclatures_for_questionnaire() throws Exception {
        mockMvc.perform(get("/api/questionnaire/simpsons/required-nomenclatures")
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[*]").value(containsInAnyOrder("cities2019", "regions2019")));
    }

    @Test
    void on_get_required_nomenclatures_when_campaign_not_exist_return_404() throws Exception {
        mockMvc.perform(get("/api/campaign/QSHL/required-nomenclatures")
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser())))
                .andExpect(status().isNotFound());
    }

    @Test
    void on_get_required_nomenclatures_when_campaign_id_invalid_return_400() throws Exception {
        mockMvc.perform(get("/api/campaign/éplop/required-nomenclatures")
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser())))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/campaign/1/required-nomenclatures",
            "/api/questionnaire/simpsons/required-nomenclatures",
            "/api/nomenclature/1",
            "/api/nomenclatures"
    })
    void on_get_nomenclatures_when_anonymous_return_401(String url) throws Exception {
        mockMvc.perform(get(url)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser())))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void on_post_nomenclature_when_anonymous_return_401() throws Exception {
        NomenclatureCreationData nomenclature = new NomenclatureCreationData("nomenclature", "label", JsonNodeFactory.instance.arrayNode());
        mockMvc.perform(post("/api/nomenclature")
                        .content(JsonTestHelper.getObjectAsJsonString(nomenclature))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void on_post_nomenclature_when_non_admin_return_403() throws Exception {
        NomenclatureCreationData nomenclature = new NomenclatureCreationData("nomenclature", "label", JsonNodeFactory.instance.arrayNode());
        mockMvc.perform(post("/api/nomenclature")
                        .content(JsonTestHelper.getObjectAsJsonString(nomenclature))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getManagerUser()))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void on_get_nomenclatures_when_surveyUnitUser_return_403() throws Exception {
        mockMvc.perform(get("/api/nomenclatures")
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser())))
                .andExpect(status().isForbidden());
    }
}
