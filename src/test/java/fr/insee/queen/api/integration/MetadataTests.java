package fr.insee.queen.api.integration;

import fr.insee.queen.api.JsonHelper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
@AutoConfigureMockMvc
class MetadataTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void when_empty_or_no_metadata_by_questionnaire_return_empty_json_metadata() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/questionnaire/simpsonsV2/metadata"))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        JSONAssert.assertEquals("{}", content, JSONCompareMode.STRICT);
    }

    @Test
    void on_get_metadata_by_questionnaire_retrieve_correct_metadata() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/questionnaire/LOG2021X11Web/metadata"))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedResult = JsonHelper.getResourceFileAsString("db/dataset/logement/metadata/metadata.json");
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.STRICT);
    }

    @Test
    void on_get_metadata_by_questionnaire_when_incorrect_identifier_questionnaire_format_id_return_400() throws Exception {
        mockMvc.perform(get("/api/questionnaire/insert into plop%s/metadata"))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void on_get_metadata_by_questionnaire_when_non_existing_questionnaire_format_id_return_404() throws Exception {
        mockMvc.perform(get("/api/questionnaire/QSJFDG12345/metadata"))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void when_empty_or_no_metadata_by_campaign_return_empty_json_metadata() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/campaign/SIMPSONS2020X00/metadata"))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        JSONAssert.assertEquals("{}", content, JSONCompareMode.STRICT);
    }

    @Test
    void on_get_metadata_by_campaign_retrieve_correct_metadata() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/campaign/LOG2021X11Web/metadata"))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedResult = JsonHelper.getResourceFileAsString("db/dataset/logement/metadata/metadata.json");
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.STRICT);
    }

    @Test
    void on_get_metadata_by_campaign_when_incorrect_identifier_campaign_format_id_return_400() throws Exception {
        mockMvc.perform(get("/api/campaign/insert into plop%s/metadata"))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void on_get_metadata_by_campaign_when_non_existing_campaign_format_id_return_404() throws Exception {
        mockMvc.perform(get("/api/campaign/QSJFDG12345/metadata"))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }
}