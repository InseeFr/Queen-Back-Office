package fr.insee.queen.application.pilotage.controller;

import fr.insee.queen.application.campaign.service.dummy.CampaignFakeService;
import fr.insee.queen.application.interrogation.service.dummy.InterrogationFakeService;
import fr.insee.queen.domain.campaign.model.CampaignSummary;
import fr.insee.queen.domain.pilotage.model.PilotageCampaign;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NoPilotageComponentTest {
    private InterrogationFakeService interrogationService;
    private CampaignFakeService campaignService;
    private NoPilotageComponent pilotageComponent;

    @BeforeEach
    void init() {
        interrogationService = new InterrogationFakeService();
        campaignService = new CampaignFakeService();
    }

    @Test
    @DisplayName("On check habilitations check interrogation existence")
    void testCheckHabilitations01() {
        pilotageComponent = new NoPilotageComponent(interrogationService, campaignService);
        pilotageComponent.checkHabilitations("11", PilotageRole.INTERVIEWER);
        assertThat(interrogationService.isCheckInterrogationExist()).isTrue();
    }

    @Test
    @DisplayName("When checking if campaign is closed, always return true")
    void testIsClosed() {
        pilotageComponent = new NoPilotageComponent(interrogationService, campaignService);
        boolean isClosed = pilotageComponent.isClosed("campaign-id");
        assertThat(isClosed).isTrue();
    }

    @Test
    @DisplayName("When retrieving interrogations by campaign, return all interrogations from campaign")
    void testGetSUByCampaign() {
        pilotageComponent = new NoPilotageComponent(interrogationService, campaignService);
        List<InterrogationSummary> interrogations = pilotageComponent.getInterrogationsByCampaign("campaign-id");
        assertThat(interrogations).isEqualTo(interrogationService.getInterrogationSummaries());
    }

    @Test
    @DisplayName("When retrieving campaigns for interviewer, return all campaigns")
    void testGetInterviewerCampaigns() {
        pilotageComponent = new NoPilotageComponent(interrogationService, campaignService);
        List<PilotageCampaign> campaignSummaries = pilotageComponent.getInterviewerCampaigns();
        for(CampaignSummary campaign : CampaignFakeService.CAMPAIGN_SUMMARY_LIST) {
            assertThat(campaignSummaries).contains(new PilotageCampaign(campaign.getId(), campaign.getQuestionnaireIds().stream().toList()));
        }

    }
}
