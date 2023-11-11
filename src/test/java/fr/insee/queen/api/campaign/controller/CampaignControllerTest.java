package fr.insee.queen.api.campaign.controller;

import fr.insee.queen.api.campaign.controller.dto.output.CampaignSummaryDto;
import fr.insee.queen.api.campaign.service.exception.CampaignDeletionException;
import fr.insee.queen.api.campaign.service.dummy.CampaignFakeService;
import fr.insee.queen.api.pilotage.service.dummy.PilotageFakeService;
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
    private AuthenticatedUserTestHelper authenticatedUserTestHelper;
    private AuthenticationFakeHelper authenticationHelper;

    @BeforeEach
    public void init() {
        authenticatedUserTestHelper = new AuthenticatedUserTestHelper();
        authenticationHelper = new AuthenticationFakeHelper();
        campaignService = new CampaignFakeService();
        pilotageService = new PilotageFakeService();
    }

    @Test
    @DisplayName("On deletion, when force is true, deletion is done")
    void testDeletion() {
        campaignController = new CampaignController(authenticationHelper, "false", campaignService, pilotageService);
        campaignController.deleteCampaignById(true, "11", authenticatedUserTestHelper.getAuthenticatedUser());
        assertThat(campaignService.deleted()).isTrue();
    }

    @Test
    @DisplayName("On deletion, when integration override is true, deletion is done")
    void testDeletion_01() {
        campaignController = new CampaignController(authenticationHelper, "true", campaignService, pilotageService);
        campaignController.deleteCampaignById(false, "11", authenticatedUserTestHelper.getAuthenticatedUser());
        assertThat(campaignService.deleted()).isTrue();
    }

    @Test
    @DisplayName("On deletion, when campaign is closed, deletion is done")
    void testDeletion_02() {
        campaignController = new CampaignController(authenticationHelper, "false", campaignService, pilotageService);
        campaignController.deleteCampaignById(false, "11", authenticatedUserTestHelper.getAuthenticatedUser());
        assertThat(campaignService.deleted()).isTrue();
    }

    @Test
    @DisplayName("On deletion, when campaign is opened, deletion is aborted")
    void testDeletionException() {
        pilotageService.isCampaignClosed(false);
        campaignController = new CampaignController(authenticationHelper, "false", campaignService, pilotageService);
        assertThatThrownBy(() -> campaignController.deleteCampaignById(false, "11", authenticatedUserTestHelper.getAuthenticatedUser()))
                .isInstanceOf(CampaignDeletionException.class);
    }

    @Test
    @DisplayName("On retrieving interviewer campaigns, when integration override is true, all campaigns are retrieved")
    void testGetInterviewerCampaigns() {
        campaignController = new CampaignController(authenticationHelper, "true", campaignService, pilotageService);
        List<CampaignSummaryDto> campaigns = campaignController.getInterviewerCampaignList(authenticatedUserTestHelper.getAuthenticatedUser());
        assertThat(campaignService.allCampaignsRetrieved()).isTrue();
        assertThat(campaigns).hasSize(2);
        assertThat(campaigns.get(0).id()).isEqualTo(CampaignFakeService.CAMPAIGN1_ID);
    }

    @Test
    @DisplayName("On retrieving interviewer campaigns, when integration override is false, all interviewer campaigns are retrieved")
    void testGetInterviewerCampaigns01() {
        campaignController = new CampaignController(authenticationHelper, "false", campaignService, pilotageService);
        List<CampaignSummaryDto> campaigns = campaignController.getInterviewerCampaignList(authenticatedUserTestHelper.getAuthenticatedUser());
        assertThat(pilotageService.wentThroughInterviewerCampaigns()).isTrue();
        assertThat(campaigns).hasSize(2);
        assertThat(campaigns.get(0).id()).isEqualTo(PilotageFakeService.CAMPAIGN1_ID);
    }
}
