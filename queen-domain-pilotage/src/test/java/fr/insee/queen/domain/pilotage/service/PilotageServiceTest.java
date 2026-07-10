package fr.insee.queen.domain.pilotage.service;

import fr.insee.queen.domain.group.service.dummy.GroupExistenceFakeService;
import fr.insee.queen.domain.group.service.dummy.QuestionnaireModelFakeService;
import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
import fr.insee.queen.domain.interrogation.service.dummy.InterrogationFakeService;
import fr.insee.queen.domain.pilotage.infrastructure.dummy.PilotageFakeRepository;
import fr.insee.queen.domain.pilotage.model.PilotageGroup;
import fr.insee.queen.domain.pilotage.service.exception.PilotageApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PilotageServiceTest {
    private PilotageApiService pilotageService;
    private GroupExistenceFakeService groupExistenceService;
    private PilotageFakeRepository pilotageRepository;
    private QuestionnaireModelFakeService questionnaireModelFakeService;

    @BeforeEach
    void init() {
        InterrogationFakeService interrogationService = new InterrogationFakeService();
        pilotageRepository = new PilotageFakeRepository();
        groupExistenceService = new GroupExistenceFakeService();
        questionnaireModelFakeService = new QuestionnaireModelFakeService();
        pilotageService = new PilotageApiService(interrogationService, groupExistenceService, pilotageRepository, questionnaireModelFakeService);
    }

    @Test
    @DisplayName("On check if group closed, check group existence")
    void testGroupIsClosed() {
        pilotageService.isClosed("11");
        assertThat(groupExistenceService.isCheckGroupExist()).isTrue();
        assertThat(pilotageRepository.isWentThroughIsClosedGroup()).isTrue();
    }

    @Test
    @DisplayName("On retrieving interviewer groups throw exception if groups are null")
    void testGetInterviewerGroups01() {
        pilotageRepository.setNullInterviewerGroups(true);
        assertThatThrownBy(() -> pilotageService.getInterviewerGroups())
                .isInstanceOf(PilotageApiException.class);
    }

    @Test
    @DisplayName("On retrieving interviewer groups return groups")
    void testGetInterviewerGroups02() {
        List<PilotageGroup> groups = pilotageService.getInterviewerGroups();
        assertThat(groups).hasSize(2);
        assertThat(groups.getFirst().id()).isEqualTo(PilotageFakeRepository.INTERVIEWER_GROUP1_ID);
    }

    @Test
    @DisplayName("Should not retrieve unexisting groups in DB")
    void testGetInterviewerGroups03() {
        questionnaireModelFakeService.setGroupIdNotFound(PilotageFakeRepository.INTERVIEWER_GROUP1_ID);
        List<PilotageGroup> groups = pilotageService.getInterviewerGroups();
        assertThat(groups).hasSize(1);
        assertThat(groups.getFirst().id()).isNotEqualTo(PilotageFakeRepository.INTERVIEWER_GROUP1_ID);
    }

    @Test
    @DisplayName("On retrieving interrogations by group, when current interrogation is null return empty collection")
    void testGetInterrogations_01() {
        pilotageRepository.setNullCurrentInterrogation(true);
        List<InterrogationSummary> interrogations = pilotageService.getInterrogations("group-id");
        assertThat(interrogations).isEmpty();
    }

    @Test
    @DisplayName("On retrieving interrogations by group check group existence")
    void testGetInterrogations_02() {
        pilotageRepository.setNullCurrentInterrogation(true);
        pilotageService.getInterrogations("group-id");
        assertThat(groupExistenceService.isCheckGroupExist()).isTrue();
    }

    @Test
    @DisplayName("On retrieving interrogations by group, return interrogations for a group")
    void testGetInterrogations_03() {
        List<InterrogationSummary> interrogations = pilotageService.getInterrogations(PilotageFakeRepository.CURRENT_SU_GROUP1_ID);
        assertThat(interrogations).hasSize(2);
        assertThat(interrogations.get(0).id()).isEqualTo(PilotageFakeRepository.INTERROGATION1_ID);
        assertThat(interrogations.get(1).id()).isEqualTo(PilotageFakeRepository.INTERROGATION3_ID);
    }
}
