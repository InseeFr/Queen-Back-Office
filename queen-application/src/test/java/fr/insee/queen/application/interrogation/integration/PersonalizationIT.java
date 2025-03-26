package fr.insee.queen.application.interrogation.integration;

import fr.insee.queen.application.configuration.ScriptConstants;
import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;
import fr.insee.queen.application.utils.JsonTestHelper;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class PersonalizationIT {
    @Autowired
    private MockMvc mockMvc;

    private final AuthenticatedUserTestHelper authenticatedUserTestHelper = new AuthenticatedUserTestHelper();

    @Test
    void on_get_personalization_return_personalization() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/interrogation/517046b6-bd88-47e0-838e-00d03461f592/personalization")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        String expectedResult = JsonTestHelper.getResourceFileAsString("interrogation/personalization.json");
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void on_get_personalization_when_su_not_exist_return_404() throws Exception {
        mockMvc.perform(get("/api/interrogation/plop/personalization")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_get_personalization_when_su_id_invalid_return_400() throws Exception {
        mockMvc.perform(get("/api/interrogation/pl!op/personalization")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_update_personalization_personalization_is_updated() throws Exception {
        String interrogationId = "d98d28c2-1535-4fc8-a405-d6a554231bbc";
        String personalizationJson = JsonTestHelper.getResourceFileAsString("interrogation/personalization.json");
        MvcResult result = mockMvc.perform(get("/api/interrogation/" + interrogationId + "/personalization")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        JSONAssert.assertNotEquals(personalizationJson, content, JSONCompareMode.NON_EXTENSIBLE);

        mockMvc.perform(put("/api/interrogation/" + interrogationId + "/personalization")
                        .content(personalizationJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isOk());

        result = mockMvc.perform(get("/api/interrogation/" + interrogationId + "/personalization")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
        JSONAssert.assertEquals(personalizationJson, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void on_update_personalization_when_su_not_exist_return_404() throws Exception {
        mockMvc.perform(put("/api/interrogation/not-exist/personalization")
                        .content("[]")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_update_personalization_when_su_id_invalid_return_400() throws Exception {
        mockMvc.perform(put("/api/interrogation/invalid!identifier/personalization")
                        .content("[]")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_update_personalization_when_personalization_not_json_array_node_return_400() throws Exception {
        mockMvc.perform(put("/api/interrogation/d98d28c2-1535-4fc8-a405-d6a554231bbc/personalization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_get_data_when_anonymous_user_return_401() throws Exception {
        mockMvc.perform(get("/api/interrogation/d98d28c2-1535-4fc8-a405-d6a554231bbc/personalization")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void on_update_data_when_anonymous_user_return_401() throws Exception {
        mockMvc.perform(put("/api/interrogation/d98d28c2-1535-4fc8-a405-d6a554231bbc/personalization")
                        .content("[]")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void on_update_data_when_nonAdmin_user_return_403() throws Exception {
        mockMvc.perform(put("/api/interrogation/d98d28c2-1535-4fc8-a405-d6a554231bbc/personalization")
                        .content("[]")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNonAdminUser()))
                )
                .andExpect(status().isForbidden());
    }
}
