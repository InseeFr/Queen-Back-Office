package fr.insee.queen.application.pilotage.controller;

import fr.insee.queen.application.group.dto.output.GroupSummaryResponse;
import fr.insee.queen.application.interrogation.dto.output.InterrogationByGroupResponse;
import fr.insee.queen.application.interrogation.dto.output.InterrogationDto;
import fr.insee.queen.application.pilotage.controller.dummy.PilotageFakeComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InterviewerControllerTest {
    private PilotageFakeComponent pilotageComponent;
    private InterviewerController interviewerController;

    @BeforeEach
    void init() {
        pilotageComponent = new PilotageFakeComponent();
        interviewerController = new InterviewerController(pilotageComponent);
    }

    @Test
    @DisplayName("On retrieving interviewer groups, all interviewer groups are retrieved")
    void testGetInterviewerGroups01() {
        List<GroupSummaryResponse> groups = interviewerController.getInterviewerGroupList("userId");
        assertThat(pilotageComponent.isWentThroughInterviewerGroups()).isTrue();
        assertThat(groups).hasSize(2);
        assertThat(groups.getFirst().getId()).isEqualTo(PilotageFakeComponent.GROUP1_ID);
    }

    @Test
    @DisplayName("On retrieving interrogations for a group, then return interrogations from pilotage service")
    void testGetInterrogationsGroup01() {
        List<InterrogationByGroupResponse> interrogations = interviewerController.getListInterrogationByGroup("group-id");
        assertThat(interrogations).hasSize(2);
        assertThat(interrogations.getFirst().id()).isEqualTo(PilotageFakeComponent.INTERROGATION1_ID);
    }

    @Test
    @DisplayName("On retrieving interrogations for a group, when interrogations are empty")
    void testGetInterrogationsGroup02() {
        // Given
        pilotageComponent.setHasEmptyInterrogations(true);
        // When
        List<InterrogationByGroupResponse> result = interviewerController.getListInterrogationByGroup("group-id");
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
