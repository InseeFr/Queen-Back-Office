package fr.insee.queen.application.group.integration;

import tools.jackson.databind.node.JsonNodeFactory;
import fr.insee.queen.application.configuration.ScriptConstants;
import fr.insee.queen.domain.group.gateway.GroupRepository;
import fr.insee.queen.domain.group.model.Group;
import fr.insee.queen.domain.group.model.GroupSummary;
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
class GroupUpdateIT {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private QuestionnaireModelJpaRepository questionnaireModelJpaRepository;

    @Test
    @DisplayName("on group update, the join table is replaced with the new set and previously linked questionnaire models still exist")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void update_replaces_questionnaire_models_in_join_table() {
        // Given: SIMPSONS2020X00 is initially linked to {simpsons, simpsonsV2} via the seed
        GroupSummary initial = groupRepository.findWithQuestionnaireIds("SIMPSONS2020X00").orElseThrow();
        assertThat(initial.getQuestionnaireIds()).containsExactlyInAnyOrder("simpsons", "simpsonsV2");

        // When: we update the group so it only references QmWithoutCamp
        Group updated = new Group(
                "SIMPSONS2020X00",
                "Survey on the Simpsons tv show 2020",
                Set.of("QmWithoutCamp"),
                JsonNodeFactory.instance.objectNode());
        groupRepository.update(updated);

        // Then: the join table now exposes only the new questionnaire model
        GroupSummary reloaded = groupRepository.findWithQuestionnaireIds("SIMPSONS2020X00").orElseThrow();
        assertThat(reloaded.getQuestionnaireIds()).containsExactly("QmWithoutCamp");

        // And: the previously linked questionnaire models are not deleted, only unlinked
        assertThat(questionnaireModelJpaRepository.existsById("simpsons")).isTrue();
        assertThat(questionnaireModelJpaRepository.existsById("simpsonsV2")).isTrue();
    }
}
