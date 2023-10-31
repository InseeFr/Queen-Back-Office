package fr.insee.queen.api.controller;

import fr.insee.queen.api.controller.campaign.CampaignController;
import fr.insee.queen.api.dto.campaign.CampaignSummaryDto;
import fr.insee.queen.api.service.dummy.CampaignFakeService;
import fr.insee.queen.api.service.exception.CampaignDeletionException;
import fr.insee.queen.api.service.dummy.PilotageFakeService;
import fr.insee.queen.api.service.dummy.QuestionnaireModelFakeService;
import fr.insee.queen.api.utils.AuthenticatedUserTestHelper;
import fr.insee.queen.api.utils.dummy.AuthenticationFakeHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CampaignControllerTest {

    private CampaignController campaignController;
    private PilotageFakeService pilotageService;
    private CampaignFakeService campaignService;
    private QuestionnaireModelFakeService questionnaireService;
    private AuthenticatedUserTestHelper authenticatedUserTestHelper;
    private AuthenticationFakeHelper authenticationHelper;

    @BeforeEach
    public void init() {
        authenticatedUserTestHelper = new AuthenticatedUserTestHelper();
        authenticationHelper = new AuthenticationFakeHelper();
        campaignService = new CampaignFakeService();
        questionnaireService = new QuestionnaireModelFakeService();
        pilotageService = new PilotageFakeService();
    }

    @Test
    @DisplayName("On deletion, when force is true, deletion is done")
    void testDeletion() {
        campaignController = new CampaignController(authenticationHelper, "false", campaignService, questionnaireService, pilotageService);
        campaignController.deleteCampaignById(true, "11", authenticatedUserTestHelper.getAuthenticatedUser());
        assertThat(campaignService.deleted()).isTrue();
    }

    @Test
    @DisplayName("On deletion, when integration override is true, deletion is done")
    void testDeletion_01() {
        campaignController = new CampaignController(authenticationHelper, "true", campaignService, questionnaireService, pilotageService);
        campaignController.deleteCampaignById(false, "11", authenticatedUserTestHelper.getAuthenticatedUser());
        assertThat(campaignService.deleted()).isTrue();
    }

    @Test
    @DisplayName("On deletion, when campaign is closed, deletion is done")
    void testDeletion_02() {
        campaignController = new CampaignController(authenticationHelper, "false", campaignService, questionnaireService, pilotageService);
        campaignController.deleteCampaignById(false, "11", authenticatedUserTestHelper.getAuthenticatedUser());
        assertThat(campaignService.deleted()).isTrue();
    }

    @Test
    @DisplayName("On deletion, when campaign is opened, deletion is aborted")
    void testDeletionException() {
        pilotageService.isCampaignClosed(false);
        campaignController = new CampaignController(authenticationHelper, "false", campaignService, questionnaireService, pilotageService);
        assertThatThrownBy(() -> campaignController.deleteCampaignById(false, "11", authenticatedUserTestHelper.getAuthenticatedUser()))
                .isInstanceOf(CampaignDeletionException.class);
    }

    @Test
    @DisplayName("On retrieving interviewer campaigns, when integration override is true, all campaigns are retrieved")
    void testGetInterviewerCampaigns() {
        campaignController = new CampaignController(authenticationHelper, "true", campaignService, questionnaireService, pilotageService);
        List<CampaignSummaryDto> campaigns = campaignController.getInterviewerCampaignList(authenticatedUserTestHelper.getAuthenticatedUser());
        assertThat(campaignService.allCampaignsRetrieved()).isTrue();
        assertThat(campaigns).hasSize(2);
        assertThat(campaigns.get(0).id()).isEqualTo(CampaignFakeService.CAMPAIGN1_ID);
    }

    @Test
    @DisplayName("On retrieving interviewer campaigns, when integration override is false, all interviewer campaigns are retrieved")
    void testGetInterviewerCampaigns01() {
        campaignController = new CampaignController(authenticationHelper, "false", campaignService, questionnaireService, pilotageService);
        List<CampaignSummaryDto> campaigns = campaignController.getInterviewerCampaignList(authenticatedUserTestHelper.getAuthenticatedUser());
        assertThat(pilotageService.wentThroughInterviewerCampaigns()).isTrue();
        assertThat(campaigns).hasSize(2);
        assertThat(campaigns.get(0).id()).isEqualTo(PilotageFakeService.CAMPAIGN1_ID);
    }
}
