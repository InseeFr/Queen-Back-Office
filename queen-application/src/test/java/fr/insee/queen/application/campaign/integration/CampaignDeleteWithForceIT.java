package fr.insee.queen.application.campaign.integration;

import fr.insee.queen.application.configuration.ScriptConstants;
import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(properties = {
        "application.campaign.check-interrogations-on-delete=false"
})
@AutoConfigureMockMvc
class CampaignDeleteWithForceIT {

    @Autowired
    private MockMvc mockMvc;

    private final AuthenticatedUserTestHelper authenticatedUserTestHelper = new AuthenticatedUserTestHelper();

    @Test
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_delete_campaign_process_deletion() throws Exception {
        String campaignName = "LOG2021X11Web";
        mockMvc.perform(delete("/api/campaign/" + campaignName)
                        .param("force", "true")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/admin/campaigns")
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]").value(not(containsInAnyOrder(campaignName))));
    }

    @Test
    void on_delete_campaign_when_campaign_invalid_identifier_return_400() throws Exception {
        mockMvc.perform(delete("/api/campaign/invalid!identifier")
                        .param("force", "true")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_delete_campaign_when_campaign_not_exist_return_404() throws Exception {
        mockMvc.perform(delete("/api/campaign/non-existing-campaign")
                        .param("force", "true")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_delete_campaign_when_user_not_authorized_return_403() throws Exception {
        mockMvc.perform(delete("/api/campaign/non-existing-campaign")
                        .param("force", "true")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNonAdminUser()))
                )
                .andExpect(status().isForbidden());
    }
}
