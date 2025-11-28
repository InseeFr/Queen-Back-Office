package fr.insee.queen.application.pilotage.controller;

import fr.insee.queen.application.campaign.dto.output.CampaignSummaryDto;
import fr.insee.queen.application.pilotage.controller.dummy.PilotageFakeComponent;
import fr.insee.queen.application.interrogation.dto.output.InterrogationByCampaignDto;
import fr.insee.queen.application.interrogation.dto.output.InterrogationDto;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InterviewerControllerTest {
    private PilotageFakeComponent pilotageComponent;
    private InterviewerController interviewerController;

    @BeforeEach
    void init() {
        pilotageComponent = new PilotageFakeComponent();
        interviewerController = new InterviewerController(pilotageComponent);
    }

    @Test
    @DisplayName("On retrieving interviewer campaigns, all interviewer campaigns are retrieved")
    void testGetInterviewerCampaigns01() {
        List<CampaignSummaryDto> campaigns = interviewerController.getInterviewerCampaignList("userId");
        assertThat(pilotageComponent.isWentThroughInterviewerCampaigns()).isTrue();
        assertThat(campaigns).hasSize(2);
        assertThat(campaigns.getFirst().getId()).isEqualTo(PilotageFakeComponent.CAMPAIGN1_ID);
    }

    @Test
    @DisplayName("On retrieving interrogations for a campaign, then return interrogations from pilotage service")
    void testGetInterrogationsCampaign01() {
        List<InterrogationByCampaignDto> interrogations = interviewerController.getListInterrogationByCampaign("campaign-id");
        assertThat(interrogations).hasSize(2);
        assertThat(interrogations.getFirst().id()).isEqualTo(PilotageFakeComponent.INTERROGATION1_ID);
    }

    @Test
    @DisplayName("On retrieving interrogations for a campaign, when interrogations are empty")
    void testGetInterrogationsCampaign02() {
        // Given
        pilotageComponent.setHasEmptyInterrogations(true);
        // When
        List<InterrogationByCampaignDto> result = interviewerController.getListInterrogationByCampaign("campaign-id");
        // Then
        assertThat(result)
                .isNotNull()
                .isEmpty();
   }

    @Test
    @DisplayName("On retrieving interrogations for an interviewer, return interrogations found")
    void testGetInterrogationsForInterviewers03() {
        List<InterrogationDto> interrogations =  interviewerController.getInterviewerInterrogations();
        assertThat(interrogations).size().isEqualTo(2);
        assertThat(interrogations.get(0).id()).isEqualTo(PilotageFakeComponent.INTERROGATION1_ID);
        assertThat(interrogations.get(1).id()).isEqualTo(PilotageFakeComponent.INTERROGATION2_ID);
    }
}
