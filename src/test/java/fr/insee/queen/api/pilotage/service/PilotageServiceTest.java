package fr.insee.queen.api.pilotage.service;

import fr.insee.queen.api.pilotage.service.exception.PilotageApiException;
import fr.insee.queen.api.pilotage.service.model.PilotageCampaign;
import fr.insee.queen.api.pilotage.repository.dummy.PilotageFakeRepository;
import fr.insee.queen.api.campaign.service.dummy.CampaignExistenceFakeService;
import fr.insee.queen.api.campaign.service.dummy.QuestionnaireModelFakeService;
import fr.insee.queen.api.surveyunit.service.dummy.SurveyUnitFakeService;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnitSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PilotageServiceTest {
    private PilotageService pilotageService;
    private CampaignExistenceFakeService campaignExistenceService;
    private PilotageFakeRepository pilotageRepository;

    @BeforeEach
    public void init() {
        SurveyUnitFakeService surveyUnitService = new SurveyUnitFakeService();
        pilotageRepository = new PilotageFakeRepository();
        campaignExistenceService = new CampaignExistenceFakeService();
        QuestionnaireModelFakeService questionnaireModelFakeService = new QuestionnaireModelFakeService();
        pilotageService = new PilotageApiService(surveyUnitService, campaignExistenceService, pilotageRepository, questionnaireModelFakeService);
    }

    @Test
    @DisplayName("On check if campaign closed, check campaign existence")
    void testCampaignIsClosed() {
        pilotageService.isClosed("11", "auth-token");
        assertThat(campaignExistenceService.checkCampaignExist()).isTrue();
        assertThat(pilotageRepository.wentThroughIsClosedCampaign()).isTrue();
    }

    @Test
    @DisplayName("On retrieving interviewer campaigns throw exception if campaigns are null")
    void testGetInterviewerCampaigns01() {
        pilotageRepository.nullInterviewerCampaigns(true);
        assertThatThrownBy(() -> pilotageService.getInterviewerCampaigns("auth-token"))
                .isInstanceOf(PilotageApiException.class);
    }

    @Test
    @DisplayName("On retrieving interviewer campaigns return campaigns")
    void testGetInterviewerCampaigns02() {
        List<PilotageCampaign> campaigns = pilotageService.getInterviewerCampaigns("auth-token");
        assertThat(campaigns).hasSize(2);
        assertThat(campaigns.get(0).id()).isEqualTo(PilotageFakeRepository.INTERVIEWER_CAMPAIGN1_ID);
    }

    @Test
    @DisplayName("On check habilitation, when role == INTERVIEWER return true")
    void testHasHabilitation_01() {
        SurveyUnitSummary su = new SurveyUnitSummary("su-id", "questionnaire-id", "campaign-id");
        boolean hasHabilitation = pilotageService.hasHabilitation(su, PilotageRole.INTERVIEWER, "idep", "auth-token");
        assertThat(hasHabilitation).isTrue();
    }

    @Test
    @DisplayName("On check habilitation, when role == REVIEWER return true")
    void testHasHabilitation_02() {
        SurveyUnitSummary su = new SurveyUnitSummary("su-id", "questionnaire-id", "campaign-id");
        boolean hasHabilitation = pilotageService.hasHabilitation(su, PilotageRole.REVIEWER, "idep", "auth-token");
        assertThat(hasHabilitation).isTrue();
    }

    @Test
    @DisplayName("On retrieving survey units by campaign, when current survey unit is null return empty collection")
    void testGetSurveyUnitsByCampaign_01() {
        pilotageRepository.nullCurrentSurveyUnit(true);
        List<SurveyUnitSummary> surveyUnits = pilotageService.getSurveyUnitsByCampaign("campaign-id", "auth-token");
        assertThat(surveyUnits).isEmpty();
    }

    @Test
    @DisplayName("On retrieving survey units by campaign check campaign existence")
    void testGetSurveyUnitsByCampaign_02() {
        pilotageRepository.nullCurrentSurveyUnit(true);
        pilotageService.getSurveyUnitsByCampaign("campaign-id", "auth-token");
        assertThat(campaignExistenceService.checkCampaignExist()).isTrue();
    }

    @Test
    @DisplayName("On retrieving survey units by campaign, when current survey unit return current user survey units for a campaign")
    void testGetSurveyUnitsByCampaign_03() {
        List<SurveyUnitSummary> surveyUnits = pilotageService.getSurveyUnitsByCampaign(PilotageFakeRepository.CURRENT_SU_CAMPAIGN1_ID, "auth-token");
        assertThat(surveyUnits).hasSize(2);
        assertThat(surveyUnits.get(0).id()).isEqualTo("survey-unit1");
        assertThat(surveyUnits.get(1).id()).isEqualTo("survey-unit3");
    }
}
