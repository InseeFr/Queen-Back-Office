package fr.insee.queen.infrastructure.db.group.repository.jpa;

import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.infrastructure.db.group.entity.QuestionnaireModelDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * JPA repositiory to handle questionnaires
 */
@Repository
public interface QuestionnaireModelJpaRepository extends JpaRepository<QuestionnaireModelDB, String> {

    /**
     * Find ids for all questionnaire linked to a group
     *
     * @param groupId group id
     * @return all questionnaire ids for a group
     */
    @Query(value = "select qm.id from QuestionnaireModelDB qm where qm.group.id=:groupId")
    List<String> findAllIdByGroupId(String groupId);

    /**
     * Find data structure for all questionnaire linked to a group
     *
     * @param groupId group id
     * @return all questionnaire values for a group
     */
    @Query(value = "select qm.value from QuestionnaireModelDB qm where qm.group.id=:groupId")
    List<ObjectNode> findAllValueByGroupId(String groupId);

    /**
     * Find data structure for a questionnaire
     *
     * @param questionnaireId questionnaire id
     * @return questionnaire data for a group
     */
    @Query(value = "select qm.value from QuestionnaireModelDB qm where qm.id=:questionnaireId")
    Optional<ObjectNode> findQuestionnaireData(String questionnaireId);

    /**
     * Count valid questionnaires for a group
     * This is typically used to check if questionnaires can be associated on a group.
     * A valid questionnaire is a questionnaire already linked to the group or a questionnaire with no group linked
     *
     * @param groupId group id
     * @param questionnaireIds questionnaire ids we want to check for the group
     * @return number of valid questionnaires
     */
    @NativeQuery("select count(*) from questionnaire_model qm where qm.id in :questionnaireIds and (qm.survey_group_id is NULL or qm.survey_group_id=:groupId)")
    Long countValidQuestionnairesByIds(String groupId, Set<String> questionnaireIds);

    /**
     * Find questionnaires by ids
     *
     * @param questionnaireIds questionnaire ids
     * @return {@link QuestionnaireModelDB}
     */
    Set<QuestionnaireModelDB> findByIdIn(Set<String> questionnaireIds);

    /**
     * Delete all questionnaire by group id
     *
     * @param groupId group id
     */
    void deleteAllByGroupId(String groupId);
}
