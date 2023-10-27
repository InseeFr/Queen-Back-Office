package fr.insee.queen.api.service;

import fr.insee.queen.api.dto.campaign.CampaignSummaryDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitHabilitationDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitSummaryDto;
import fr.insee.queen.api.repository.PilotageFakeRepository;
import fr.insee.queen.api.service.dummy.CampaignExistenceFakeService;
import fr.insee.queen.api.service.dummy.SurveyUnitFakeService;
import fr.insee.queen.api.service.exception.PilotageApiException;
import fr.insee.queen.api.service.pilotage.PilotageApiService;
import fr.insee.queen.api.service.pilotage.PilotageRole;
import fr.insee.queen.api.service.pilotage.PilotageService;
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
    private SurveyUnitFakeService surveyUnitService;

    @BeforeEach
    public void init() {
        surveyUnitService = new SurveyUnitFakeService();
        pilotageRepository = new PilotageFakeRepository();
        campaignExistenceService = new CampaignExistenceFakeService();
        pilotageService = new PilotageApiService(surveyUnitService, campaignExistenceService, pilotageRepository);
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
        List<CampaignSummaryDto> campaigns = pilotageService.getInterviewerCampaigns("auth-token");
        assertThat(campaigns).hasSize(2);
        assertThat(campaigns.get(0).id()).isEqualTo(PilotageFakeRepository.INTERVIEWER_CAMPAIGN1_ID);
    }

    @Test
    @DisplayName("On check habilitation, when role == INTERVIEWER return true")
    void testHasHabilitation_01() {
        SurveyUnitHabilitationDto su = new SurveyUnitHabilitationDto("su-id", "campaign-id");
        boolean hasHabilitation = pilotageService.hasHabilitation(su, PilotageRole.INTERVIEWER, "idep", "auth-token") ;
        assertThat(hasHabilitation).isTrue();
    }

    @Test
    @DisplayName("On check habilitation, when role == REVIEWER return true")
    void testHasHabilitation_02() {
        SurveyUnitHabilitationDto su = new SurveyUnitHabilitationDto("su-id", "campaign-id");
        boolean hasHabilitation = pilotageService.hasHabilitation(su, PilotageRole.REVIEWER, "idep", "auth-token") ;
        assertThat(hasHabilitation).isTrue();
    }

    @Test
    @DisplayName("On retrieving survey units by campaign, when current survey unit is null return empty collection")
    void testGetSurveyUnitsByCampaign_01() {
        pilotageRepository.nullCurrentSurveyUnit(true);
        List<SurveyUnitSummaryDto> surveyUnits = pilotageService.getSurveyUnitsByCampaign("campaign-id", "auth-token");
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
        List<SurveyUnitSummaryDto> surveyUnits = pilotageService.getSurveyUnitsByCampaign(PilotageFakeRepository.CURRENT_SU_CAMPAIGN1_ID, "auth-token");
        assertThat(surveyUnits).hasSize(2);
        assertThat(surveyUnits.get(0).id()).isEqualTo("survey-unit1");
        assertThat(surveyUnits.get(1).id()).isEqualTo("survey-unit3");
    }
}
