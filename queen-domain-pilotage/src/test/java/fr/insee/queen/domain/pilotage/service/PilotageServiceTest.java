package fr.insee.queen.domain.pilotage.service;

import fr.insee.queen.domain.campaign.service.dummy.CampaignExistenceFakeService;
import fr.insee.queen.domain.campaign.service.dummy.QuestionnaireModelFakeService;
import fr.insee.queen.domain.pilotage.infrastructure.dummy.PilotageFakeRepository;
import fr.insee.queen.domain.pilotage.model.PilotageCampaign;
import fr.insee.queen.domain.pilotage.service.exception.PilotageApiException;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitSummary;
import fr.insee.queen.domain.surveyunit.service.dummy.SurveyUnitFakeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PilotageServiceTest {
    private PilotageApiService pilotageService;
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
        pilotageService.isClosed("11");
        assertThat(campaignExistenceService.isCheckCampaignExist()).isTrue();
        assertThat(pilotageRepository.isWentThroughIsClosedCampaign()).isTrue();
    }

    @Test
    @DisplayName("On retrieving interviewer campaigns throw exception if campaigns are null")
    void testGetInterviewerCampaigns01() {
        pilotageRepository.setNullInterviewerCampaigns(true);
        assertThatThrownBy(() -> pilotageService.getInterviewerCampaigns())
                .isInstanceOf(PilotageApiException.class);
    }

    @Test
    @DisplayName("On retrieving interviewer campaigns return campaigns")
    void testGetInterviewerCampaigns02() {
        List<PilotageCampaign> campaigns = pilotageService.getInterviewerCampaigns();
        assertThat(campaigns).hasSize(2);
        assertThat(campaigns.get(0).id()).isEqualTo(PilotageFakeRepository.INTERVIEWER_CAMPAIGN1_ID);
    }

    @Test
    @DisplayName("On check habilitation, when role == INTERVIEWER return true")
    void testHasHabilitation_01() {
        SurveyUnitSummary su = new SurveyUnitSummary("su-id", "questionnaire-id", "campaign-id");
        boolean hasHabilitation = pilotageService.hasHabilitation(su, PilotageRole.INTERVIEWER, "idep");
        assertThat(hasHabilitation).isTrue();
    }

    @Test
    @DisplayName("On check habilitation, when role == REVIEWER return true")
    void testHasHabilitation_02() {
        SurveyUnitSummary su = new SurveyUnitSummary("su-id", "questionnaire-id", "campaign-id");
        boolean hasHabilitation = pilotageService.hasHabilitation(su, PilotageRole.REVIEWER, "idep");
        assertThat(hasHabilitation).isTrue();
    }

    @Test
    @DisplayName("On retrieving survey units by campaign, when current survey unit is null return empty collection")
    void testGetSurveyUnitsByCampaign_01() {
        pilotageRepository.setNullCurrentSurveyUnit(true);
        List<SurveyUnitSummary> surveyUnits = pilotageService.getSurveyUnitsByCampaign("campaign-id");
        assertThat(surveyUnits).isEmpty();
    }

    @Test
    @DisplayName("On retrieving survey units by campaign check campaign existence")
    void testGetSurveyUnitsByCampaign_02() {
        pilotageRepository.setNullCurrentSurveyUnit(true);
        pilotageService.getSurveyUnitsByCampaign("campaign-id");
        assertThat(campaignExistenceService.isCheckCampaignExist()).isTrue();
    }

    @Test
    @DisplayName("On retrieving survey units by campaign, return survey units for a campaign")
    void testGetSurveyUnitsByCampaign_03() {
        List<SurveyUnitSummary> surveyUnits = pilotageService.getSurveyUnitsByCampaign(PilotageFakeRepository.CURRENT_SU_CAMPAIGN1_ID);
        assertThat(surveyUnits).hasSize(2);
        assertThat(surveyUnits.get(0).id()).isEqualTo(PilotageFakeRepository.SURVEY_UNIT1_ID);
        assertThat(surveyUnits.get(1).id()).isEqualTo(PilotageFakeRepository.SURVEY_UNIT2_ID);
    }
}
