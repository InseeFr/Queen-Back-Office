package fr.insee.queen.application.pilotage.controller;

import fr.insee.queen.application.group.service.dummy.GroupFakeService;
import fr.insee.queen.application.interrogation.service.dummy.InterrogationFakeService;
import fr.insee.queen.domain.group.model.GroupSummary;
import fr.insee.queen.domain.pilotage.model.PilotageGroup;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NoPilotageComponentTest {
    private InterrogationFakeService interrogationService;
    private GroupFakeService groupService;
    private NoPilotageComponent pilotageComponent;

    @BeforeEach
    void init() {
        interrogationService = new InterrogationFakeService();
        groupService = new GroupFakeService();
    }

    @Test
    @DisplayName("On check habilitations check interrogation existence")
    void testCheckHabilitations01() {
        pilotageComponent = new NoPilotageComponent(interrogationService, groupService);
        pilotageComponent.checkHabilitations("11", PilotageRole.INTERVIEWER);
        assertThat(interrogationService.isCheckInterrogationExist()).isTrue();
    }

    @Test
    @DisplayName("When checking if group is closed, always return true")
    void testIsClosed() {
        pilotageComponent = new NoPilotageComponent(interrogationService, groupService);
        boolean isClosed = pilotageComponent.isClosed("group-id");
        assertThat(isClosed).isTrue();
    }

    @Test
    @DisplayName("When retrieving interrogations by group, return all interrogations from group")
    void testGetSUByGroup() {
        pilotageComponent = new NoPilotageComponent(interrogationService, groupService);
        List<InterrogationSummary> interrogations = pilotageComponent.getInterrogations("group-id");
        assertThat(interrogations).isEqualTo(interrogationService.getInterrogationSummaries());
    }

    @Test
    @DisplayName("When retrieving groups for interviewer, return all groups")
    void testGetInterviewerGroups() {
        pilotageComponent = new NoPilotageComponent(interrogationService, groupService);
        List<PilotageGroup> groupSummaries = pilotageComponent.getInterviewerGroups();
        for(GroupSummary group : GroupFakeService.GROUP_SUMMARY_LIST) {
            assertThat(groupSummaries).contains(new PilotageGroup(group.getId(), group.getQuestionnaireIds().stream().toList()));
        }

    }
}
