package fr.insee.queen.application.utils;

import fr.insee.queen.application.campaign.service.dummy.CampaignFakeService;
import fr.insee.queen.application.pilotage.controller.NoPilotageComponent;
import fr.insee.queen.application.surveyunit.service.dummy.SurveyUnitFakeService;
import fr.insee.queen.domain.campaign.model.CampaignSummary;
import fr.insee.queen.domain.pilotage.model.PilotageCampaign;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitSummary;
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
        assertThat(surveyUnitService.isCheckSurveyUnitExist()).isTrue();
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
        assertThat(surveyUnits).isEqualTo(surveyUnitService.getSurveyUnitSummaries());
    }

    @Test
    @DisplayName("When retrieving campaigns for interviewer, return all campaigns")
    void testGetInterviewerCampaigns() {
        pilotageComponent = new NoPilotageComponent(surveyUnitService, campaignService);
        List<PilotageCampaign> campaignSummaries = pilotageComponent.getInterviewerCampaigns();
        for(CampaignSummary campaign : CampaignFakeService.CAMPAIGN_SUMMARY_LIST) {
            assertThat(campaignSummaries).contains(new PilotageCampaign(campaign.getId(), campaign.getQuestionnaireIds().stream().toList()));
        }

    }
}
