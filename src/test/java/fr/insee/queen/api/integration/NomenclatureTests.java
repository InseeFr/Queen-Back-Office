package fr.insee.queen.api.integration;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.queen.api.utils.JsonTestHelper;
import fr.insee.queen.api.dto.input.NomenclatureInputDto;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.hamcrest.Matchers.containsInAnyOrder;
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
class NomenclatureTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    void on_post_nomenclature_json_nomenclature_created() throws Exception {
        String nomenclatureName = "plop";
        String nomenclatureJsonFile = "db/dataset/nomenclature/public_regions-2019.json";
        on_get_nomenclature_when_nomenclature_not_exists_return_404(nomenclatureName);
        ArrayNode jsonNomenclature = JsonTestHelper.getResourceFileAsArrayNode(nomenclatureJsonFile);
        NomenclatureInputDto nomenclature = new NomenclatureInputDto(nomenclatureName, "label", jsonNomenclature);
        mockMvc.perform(post("/api/nomenclature")
                        .content(JsonTestHelper.getObjectAsJsonString(nomenclature))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        on_get_nomenclature_return_json_nomenclature(nomenclatureName, nomenclatureJsonFile);
    }

    @ParameterizedTest
    @CsvSource(value = {"plop,db/dataset/nomenclature/public_regions-2019.json"})
    @Order(2)
    void on_get_nomenclature_return_json_nomenclature(String nomenclatureName, String nomenclatureJsonFile) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/nomenclature/"+ nomenclatureName))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedResult = JsonTestHelper.getResourceFileAsString(nomenclatureJsonFile);
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.STRICT);
    }

    @Test
    @Order(3)
    void on_post_nomenclature_json_nomenclature_updated() throws Exception {
        String nomenclatureName = "plop";
        String nomenclatureJsonFile = "db/dataset/nomenclature/cities_2019_nomenclature.json";
        on_get_nomenclature_return_json_nomenclature(nomenclatureName, "db/dataset/nomenclature/public_regions-2019.json");
        ArrayNode jsonNomenclature = JsonTestHelper.getResourceFileAsArrayNode(nomenclatureJsonFile);
        NomenclatureInputDto nomenclature = new NomenclatureInputDto(nomenclatureName, "label", jsonNomenclature);
        mockMvc.perform(post("/api/nomenclature")
                        .content(JsonTestHelper.getObjectAsJsonString(nomenclature))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        on_get_nomenclature_return_json_nomenclature(nomenclatureName, nomenclatureJsonFile);
    }

    @Test
    @Order(4)
    void on_get_nomenclatures_return_all_nomenclatures() throws Exception {
        mockMvc.perform(get("/api/nomenclatures"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(7)))
                .andExpect(jsonPath("$[*]").value(containsInAnyOrder("plop", "cities2019", "regions2019", "L_DEPNAIS", "L_NATIONETR", "L_PAYSNAIS", "cog-communes")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"sqqghk"})
    void on_get_nomenclature_when_nomenclature_not_exists_return_404(String nomenclatureName) throws Exception {
        mockMvc.perform(get("/api/nomenclature/" + nomenclatureName))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void on_get_nomenclature_when_nomenclature_id_not_valid_return_400() throws Exception {
        mockMvc.perform(get("/api/nomenclature/s4-_"))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void on_get_required_nomenclatures_return_nomenclatures_for_campaign() throws Exception {
        mockMvc.perform(get("/api/campaign/SIMPSONS2020X00/required-nomenclatures"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[*]").value(containsInAnyOrder("cities2019", "regions2019")));
    }

    @Test
    void on_get_required_nomenclatures_return_nomenclatures_for_questionnaire() throws Exception {
        mockMvc.perform(get("/api/questionnaire/simpsons/required-nomenclatures"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[*]").value(containsInAnyOrder("cities2019", "regions2019")));
    }

    @Test
    void on_get_required_nomenclatures_when_campaign_not_exist_return_404() throws Exception {
        mockMvc.perform(get("/api/campaign/QSHL/required-nomenclatures"))
                .andExpect(status().isNotFound());
    }

    @Test
    void on_get_required_nomenclatures_when_campaign_id_invalid_return_400() throws Exception {
        mockMvc.perform(get("/api/campaign/_plop/required-nomenclatures"))
                .andExpect(status().isBadRequest());
    }
}
