package fr.insee.queen.api.integration;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.api.configuration.auth.AuthorityRoleEnum;
import fr.insee.queen.api.dto.input.CampaignInputDto;
import fr.insee.queen.api.dto.input.MetadataInputDto;
import fr.insee.queen.api.dto.input.QuestionnaireModelInputDto;
import fr.insee.queen.api.utils.AuthenticatedUserTestHelper;
import fr.insee.queen.api.utils.JsonTestHelper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CampaignTests {

    @Autowired
    private MockMvc mockMvc;

    private final AuthenticatedUserTestHelper authenticatedUserTestHelper = new AuthenticatedUserTestHelper();

    private final Authentication adminUser = authenticatedUserTestHelper.getAuthenticatedUser(
            AuthorityRoleEnum.ADMIN.name(),
            AuthorityRoleEnum.WEBCLIENT.name());

    private final Authentication nonAdminUser = authenticatedUserTestHelper.getAuthenticatedUser(
            AuthorityRoleEnum.REVIEWER.name(),
            AuthorityRoleEnum.REVIEWER_ALTERNATIVE.name(),
            AuthorityRoleEnum.INTERVIEWER.name());

    private final Authentication anonymousUser = authenticatedUserTestHelper.getNotAuthenticatedUser();

    @Test
    @Order(1)
    void on_get_campaigns_return_json_campaigns() throws Exception {
        mockMvc.perform(get("/api/admin/campaigns")
                        .with(authentication(adminUser))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == 'SIMPSONS2020X00')].questionnaireIds[*]").value(containsInAnyOrder("simpsons", "simpsonsV2")))
                .andExpect(jsonPath("$[?(@.id == 'VQS2021X00')].questionnaireIds[*]").value(containsInAnyOrder("VQS2021X00")))
                .andExpect(jsonPath("$[?(@.id == 'LOG2021X11Web')].questionnaireIds[*]").value(containsInAnyOrder("LOG2021X11Web")))
                .andExpect(jsonPath("$[?(@.id == 'LOG2021X11Tel')].questionnaireIds[*]").value(containsInAnyOrder("LOG2021X11Tel")));
    }

    @Test
    @Order(2)
    void on_create_campaigns_return_200() throws Exception {
        String questionnaireId = "questionnaire-for-campaign-creation";
        ObjectNode questionnaireJson = JsonTestHelper.getResourceFileAsObjectNode("db/dataset/simpsons.json");
        Set<String> nomenclatures = Set.of("cities2019", "regions2019");
        QuestionnaireModelInputDto questionnaire = new QuestionnaireModelInputDto(questionnaireId, "label questionnaire", questionnaireJson, nomenclatures);
        mockMvc.perform(post("/api/questionnaire-models")
                        .content(JsonTestHelper.getObjectAsJsonString(questionnaire))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(adminUser))

                )
                .andExpect(status().isCreated());

        String campaignName = "CAMPAIGN-12345";
        Set<String> questionnaireIds = Set.of(questionnaireId);

        MetadataInputDto metadata = new MetadataInputDto(JsonNodeFactory.instance.objectNode());
        CampaignInputDto campaign = new CampaignInputDto(campaignName, "label campaign", questionnaireIds, metadata);
        mockMvc.perform(post("/api/campaigns")
                        .content(JsonTestHelper.getObjectAsJsonString(campaign))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(adminUser))
                )
                .andExpect(status().isCreated());

        String expressionFilter = "$[?(@.id == '" + campaignName + "')].questionnaireIds[*]";
        mockMvc.perform(get("/api/admin/campaigns")
                    .with(authentication(adminUser))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath(expressionFilter).value(containsInAnyOrder(questionnaireIds.toArray())));
    }

    @Test
    @Order(3)
    void on_delete_campaign_process_deletion() throws Exception {
        String campaignName = "CAMPAIGN-12345";
        mockMvc.perform(delete("/api/campaign/" + campaignName)
                        .param("force", "true")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(adminUser))
                )
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/admin/campaigns")
                        .with(authentication(adminUser))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]").value(not(containsInAnyOrder(campaignName))));
    }

    @Test
    void on_create_campaign_when_campaign_already_exist_return_400() throws Exception {
        MetadataInputDto metadata = new MetadataInputDto(JsonNodeFactory.instance.objectNode());
        CampaignInputDto campaign = new CampaignInputDto("VQS2021X00", "label campaign", Set.of("simpsons", "simpsonsV2"), metadata);
        mockMvc.perform(post("/api/campaigns")
                        .content(JsonTestHelper.getObjectAsJsonString(campaign))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(adminUser))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_create_campaign_when_campaign_invalid_identifier_return_400() throws Exception {
        MetadataInputDto metadata = new MetadataInputDto(JsonNodeFactory.instance.objectNode());
        CampaignInputDto campaign = new CampaignInputDto("campaign_1234", "label campaign", Set.of("simpsons", "simpsonsV2"), metadata);
        mockMvc.perform(post("/api/campaigns")
                        .content(JsonTestHelper.getObjectAsJsonString(campaign))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(adminUser))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_create_campaign_when_questionnaire_not_exist_return_400() throws Exception {
        String campaignName = "CAMPAIGN-TEST";
        Set<String> questionnaireIds = Set.of("Hello", "Plip");

        MetadataInputDto metadata = new MetadataInputDto(JsonNodeFactory.instance.objectNode());
        CampaignInputDto campaign = new CampaignInputDto(campaignName, "label campaign", questionnaireIds, metadata);
        mockMvc.perform(post("/api/campaigns")
                        .content(JsonTestHelper.getObjectAsJsonString(campaign))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(adminUser))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_delete_campaign_when_campaign_invalid_identifier_return_400() throws Exception {
        mockMvc.perform(delete("/api/campaign/invalid_identifier")
                        .param("force", "true")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(adminUser))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_delete_campaign_when_campaign_not_exist_return_404() throws Exception {
        mockMvc.perform(delete("/api/campaign/non-existing-campaign")
                        .param("force", "true")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(adminUser))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_delete_campaign_when_user_not_authorized_return_403() throws Exception {
        mockMvc.perform(delete("/api/campaign/non-existing-campaign")
                        .param("force", "true")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(nonAdminUser))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void on_create_campaign_when_user_not_authorized_return_403() throws Exception {
        MetadataInputDto metadata = new MetadataInputDto(JsonNodeFactory.instance.objectNode());
        CampaignInputDto campaign = new CampaignInputDto("VQS2021X00", "label campaign", Set.of("simpsons", "simpsonsV2"), metadata);
        mockMvc.perform(post("/api/campaigns")
                        .content(JsonTestHelper.getObjectAsJsonString(campaign))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(nonAdminUser))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void on_get_campaigns_when_user_not_authorized_return_403() throws Exception {
        mockMvc.perform(get("/api/admin/campaigns")
                        .with(authentication(anonymousUser))
                )
                .andExpect(status().isUnauthorized());
    }
}
