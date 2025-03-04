package fr.insee.queen.application.campaign.integration;

import fr.insee.queen.application.configuration.ContainerConfiguration;
import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;
import fr.insee.queen.application.utils.JsonTestHelper;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class MetadataTests extends ContainerConfiguration {
    @Autowired
    private MockMvc mockMvc;

    private final AuthenticatedUserTestHelper authenticatedUserTestHelper = new AuthenticatedUserTestHelper();


    @Test
    void when_empty_or_no_metadata_by_questionnaire_return_empty_json_metadata() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/questionnaire/simpsonsV2/metadata")
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser())))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        JSONAssert.assertEquals("{}", content, JSONCompareMode.STRICT);
    }

    @Test
    void on_get_metadata_by_questionnaire_retrieve_correct_metadata() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/questionnaire/LOG2021X11Web/metadata")
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser())))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedResult = JsonTestHelper.getResourceFileAsString("campaign/metadata/metadata.json");
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.STRICT);
    }

    @Test
    void on_get_metadata_by_questionnaire_when_incorrect_identifier_questionnaire_format_id_return_400() throws Exception {
        mockMvc.perform(get("/api/questionnaire/insert into plopés/metadata")
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser())))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void on_get_metadata_by_questionnaire_when_non_existing_questionnaire_format_id_return_404() throws Exception {
        mockMvc.perform(get("/api/questionnaire/QSJFDG12345/metadata")
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser())))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void when_empty_or_no_metadata_by_campaign_return_empty_json_metadata() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/campaign/SIMPSONS2020X00/metadata")
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser())))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        JSONAssert.assertEquals("{}", content, JSONCompareMode.STRICT);
    }

    @Test
    void on_get_metadata_by_campaign_retrieve_correct_metadata() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/campaign/LOG2021X11Web/metadata")
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser())))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedResult = JsonTestHelper.getResourceFileAsString("campaign/metadata/metadata.json");
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.STRICT);
    }

    @Test
    void on_get_metadata_by_campaign_when_incorrect_identifier_campaign_format_id_return_400() throws Exception {
        mockMvc.perform(get("/api/campaign/insert into plopés/metadata")
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser())))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void on_get_metadata_by_campaign_when_non_existing_campaign_format_id_return_404() throws Exception {
        mockMvc.perform(get("/api/campaign/QSJFDG12345/metadata")
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser())))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void on_get_metadata_when_anonymous_access_return_401() throws Exception {
        mockMvc.perform(get("/api/questionnaire/LOG2021X11Web/metadata")
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser())))
                .andExpect(status().isUnauthorized());
    }
}
