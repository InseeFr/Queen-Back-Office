package fr.insee.queen.api.utils;

import fr.insee.queen.api.campaign.service.dummy.CampaignFakeService;
import fr.insee.queen.api.campaign.service.model.CampaignSummary;
import fr.insee.queen.api.pilotage.controller.NoPilotageComponent;
import fr.insee.queen.api.pilotage.service.PilotageRole;
import fr.insee.queen.api.pilotage.service.model.PilotageCampaign;
import fr.insee.queen.api.surveyunit.service.dummy.SurveyUnitFakeService;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnitSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NoPilotageComponentTest {
    private SurveyUnitFakeService surveyUnitService;
    private CampaignFakeService campaignService;
    private NoPilotageComponent pilotageComponent;

    @BeforeEach
    void init() {
        surveyUnitService = new SurveyUnitFakeService();
        campaignService = new CampaignFakeService();
    }

    @Test
    @DisplayName("On check habilitations check survey unit existence")
    void testCheckHabilitations01() {
        pilotageComponent = new NoPilotageComponent(surveyUnitService, campaignService);
        pilotageComponent.checkHabilitations("11", PilotageRole.INTERVIEWER);
        assertThat(surveyUnitService.checkSurveyUnitExist()).isTrue();
    }

    @Test
    @DisplayName("When checking if campaign is closed, always return true")
    void testIsClosed() {
        pilotageComponent = new NoPilotageComponent(surveyUnitService, campaignService);
        boolean isClosed = pilotageComponent.isClosed("campaign-id");
        assertThat(isClosed).isTrue();
    }

    @Test
    @DisplayName("When retrieving survey units by campaign, return all survey units from campaign")
    void testGetSUByCampaign() {
        pilotageComponent = new NoPilotageComponent(surveyUnitService, campaignService);
        List<SurveyUnitSummary> surveyUnits = pilotageComponent.getSurveyUnitsByCampaign("campaign-id");
        assertThat(surveyUnits).isEqualTo(surveyUnitService.surveyUnitSummaries());
    }

    @Test
    @DisplayName("When retrieving campaigns for interviewer, return all campaigns")
    void testGetInterviewerCampaigns() {
        pilotageComponent = new NoPilotageComponent(surveyUnitService, campaignService);
        List<PilotageCampaign> campaignSummaries = pilotageComponent.getInterviewerCampaigns();
        for(CampaignSummary campaign : CampaignFakeService.CAMPAIGN_SUMMARY_LIST) {
            assertThat(campaignSummaries).contains(new PilotageCampaign(campaign.id(), campaign.questionnaireIds().stream().toList()));
        }

    }
}
