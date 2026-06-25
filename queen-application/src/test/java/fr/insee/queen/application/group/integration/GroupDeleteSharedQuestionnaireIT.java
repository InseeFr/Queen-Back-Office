package fr.insee.queen.application.group.integration;

import tools.jackson.databind.node.JsonNodeFactory;
import fr.insee.queen.application.configuration.ScriptConstants;
import fr.insee.queen.domain.group.gateway.GroupRepository;
import fr.insee.queen.domain.group.model.Group;
import fr.insee.queen.domain.group.service.GroupService;
import fr.insee.queen.infrastructure.db.group.repository.jpa.QuestionnaireModelJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@ActiveProfiles("test")
@SpringBootTest
class GroupDeleteSharedQuestionnaireIT {

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private QuestionnaireModelJpaRepository questionnaireModelJpaRepository;

    @Test
    @DisplayName("on group delete, a questionnaire model shared with another group is preserved")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void delete_does_not_remove_shared_questionnaire_model() {
        // Given: a new group sharing the seed questionnaire model LOG2021X11Tel
        String sharedQmId = "LOG2021X11Tel";
        String newGroupId = "SHARED-GROUP";
        Group sharingGroup = new Group(
                newGroupId,
                "Shared group",
                Set.of(sharedQmId),
                JsonNodeFactory.instance.objectNode());
        groupService.createGroup(sharingGroup);

        // When: the new (interrogation-less) group is deleted
        groupService.delete(newGroupId, false);

        // Then: the shared questionnaire model still exists in the database
        assertThat(questionnaireModelJpaRepository.existsById(sharedQmId)).isTrue();

        // And: the seed group LOG2021X11Tel still references it
        assertThat(groupRepository.findWithQuestionnaireIds("LOG2021X11Tel").orElseThrow().getQuestionnaireIds())
                .contains(sharedQmId);

        // And: the new group is gone
        assertThat(groupRepository.findWithQuestionnaireIds(newGroupId)).isEmpty();
    }
}
