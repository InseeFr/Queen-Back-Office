package fr.insee.queen.application.group.integration;

import fr.insee.queen.application.configuration.ScriptConstants;
import fr.insee.queen.domain.group.gateway.GroupRepository;
import fr.insee.queen.domain.group.service.GroupService;
import fr.insee.queen.domain.interrogation.gateway.InterrogationRepository;
import fr.insee.queen.infrastructure.db.group.repository.jpa.QuestionnaireModelJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@ActiveProfiles("test")
@SpringBootTest
class GroupDeleteCascadeIT {

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private QuestionnaireModelJpaRepository questionnaireModelJpaRepository;

    @Autowired
    private InterrogationRepository interrogationRepository;

    @Test
    @DisplayName("on group delete with force, the group, its interrogations, its join entries and the orphan questionnaire model are removed")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void delete_with_force_cascades_to_interrogations_and_removes_orphan_questionnaire() {
        String groupId = "LOG2021X11Tel";
        String exclusiveQuestionnaireModelId = "LOG2021X11Tel";

        // Given: the seed group has interrogations and exclusively references LOG2021X11Tel
        assertThat(interrogationRepository.existsByGroupId(groupId)).isTrue();
        assertThat(questionnaireModelJpaRepository.existsById(exclusiveQuestionnaireModelId)).isTrue();

        // When: the group is deleted with deleteInterrogations=true
        groupService.delete(groupId, true);

        // Then: the group, the interrogations and the now-orphan questionnaire model are gone
        assertThat(groupRepository.findWithQuestionnaireIds(groupId)).isEmpty();
        assertThat(interrogationRepository.existsByGroupId(groupId)).isFalse();
        assertThat(questionnaireModelJpaRepository.existsById(exclusiveQuestionnaireModelId)).isFalse();
    }
}
