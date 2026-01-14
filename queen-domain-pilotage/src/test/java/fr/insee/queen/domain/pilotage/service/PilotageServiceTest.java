package fr.insee.queen.domain.pilotage.service;

import fr.insee.queen.domain.campaign.model.CampaignSensitivity;
import fr.insee.queen.domain.campaign.model.CampaignSummary;
import fr.insee.queen.domain.campaign.service.dummy.CampaignExistenceFakeService;
import fr.insee.queen.domain.campaign.service.dummy.QuestionnaireModelFakeService;
import fr.insee.queen.domain.habilitation.HabilitationFakeService;
import fr.insee.queen.domain.pilotage.infrastructure.dummy.PilotageFakeRepository;
import fr.insee.queen.domain.pilotage.model.PilotageCampaign;
import fr.insee.queen.domain.pilotage.service.exception.PilotageApiException;
import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
import fr.insee.queen.domain.interrogation.service.dummy.InterrogationFakeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PilotageServiceTest {
    private PilotageApiService pilotageService;
    private CampaignExistenceFakeService campaignExistenceService;
    private HabilitationFakeService habilitationFakeService;
    private PilotageFakeRepository pilotageRepository;
    private QuestionnaireModelFakeService questionnaireModelFakeService;

    @BeforeEach
    void init() {
        InterrogationFakeService interrogationService = new InterrogationFakeService();
        pilotageRepository = new PilotageFakeRepository();
        campaignExistenceService = new CampaignExistenceFakeService();
        questionnaireModelFakeService = new QuestionnaireModelFakeService();
        habilitationFakeService = new HabilitationFakeService();
        pilotageService = new PilotageApiService(interrogationService, habilitationFakeService, campaignExistenceService, pilotageRepository, questionnaireModelFakeService);
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
        assertThat(campaigns.getFirst().id()).isEqualTo(PilotageFakeRepository.INTERVIEWER_CAMPAIGN1_ID);
    }

    @Test
    @DisplayName("Should not retrieve unexisting campaigns in DB")
    void testGetInterviewerCampaigns03() {
        questionnaireModelFakeService.setCampaignIdNotFound(PilotageFakeRepository.INTERVIEWER_CAMPAIGN1_ID);
        List<PilotageCampaign> campaigns = pilotageService.getInterviewerCampaigns();
        assertThat(campaigns).hasSize(1);
        assertThat(campaigns.getFirst().id()).isNotEqualTo(PilotageFakeRepository.INTERVIEWER_CAMPAIGN1_ID);
    }

    @Test
    @DisplayName("On check habilitation, when role == INTERVIEWER return true")
    void testHasHabilitation_01() {
        InterrogationSummary su = new InterrogationSummary("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa01", "su-id", "questionnaire-id", new CampaignSummary("campaign-id", "campaign-label", CampaignSensitivity.NORMAL));
        boolean hasHabilitation = pilotageService.hasHabilitation(su, PilotageRole.INTERVIEWER, "idep");
        assertThat(hasHabilitation).isTrue();
    }

    @Test
    @DisplayName("On check habilitation, when role == REVIEWER return true")
    void testHasHabilitation_02() {
        InterrogationSummary su = new InterrogationSummary("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa01", "su-id", "questionnaire-id", new CampaignSummary("campaign-id", "campaign-label", CampaignSensitivity.NORMAL));
        boolean hasHabilitation = pilotageService.hasHabilitation(su, PilotageRole.REVIEWER, "idep");
        assertThat(hasHabilitation).isTrue();
    }

    @Test
    @DisplayName("On retrieving interrogations by campaign, when current interrogation is null return empty collection")
    void testGetInterrogationsByCampaign_01() {
        pilotageRepository.setNullCurrentInterrogation(true);
        List<InterrogationSummary> interrogations = pilotageService.getInterrogationsByCampaign("campaign-id");
        assertThat(interrogations).isEmpty();
    }

    @Test
    @DisplayName("On retrieving interrogations by campaign check campaign existence")
    void testGetInterrogationsByCampaign_02() {
        pilotageRepository.setNullCurrentInterrogation(true);
        pilotageService.getInterrogationsByCampaign("campaign-id");
        assertThat(campaignExistenceService.isCheckCampaignExist()).isTrue();
    }

    @Test
    @DisplayName("On retrieving interrogations by campaign, return interrogations for a campaign")
    void testGetInterrogationsByCampaign_03() {
        List<InterrogationSummary> interrogations = pilotageService.getInterrogationsByCampaign(PilotageFakeRepository.CURRENT_SU_CAMPAIGN1_ID);
        assertThat(interrogations).hasSize(2);
        assertThat(interrogations.get(0).id()).isEqualTo(PilotageFakeRepository.INTERROGATION1_ID);
        assertThat(interrogations.get(1).id()).isEqualTo(PilotageFakeRepository.INTERROGATION3_ID);
    }

    @Test
    @DisplayName("On check habilitation, should return false when habilitation service denies access")
    void testHasHabilitation_Denied() {
        // Given
        habilitationFakeService.setHabilitationResult(false); // On force le refus
        InterrogationSummary su = new InterrogationSummary("id", "su-id", "q-id", new CampaignSummary("c-id", "label", CampaignSensitivity.NORMAL));

        // When
        boolean hasHabilitation = pilotageService.hasHabilitation(su, PilotageRole.INTERVIEWER, "idep");

        // Then
        assertThat(hasHabilitation).isFalse();
    }
}
