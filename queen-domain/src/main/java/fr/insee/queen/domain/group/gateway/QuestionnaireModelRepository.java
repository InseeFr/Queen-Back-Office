package fr.insee.queen.domain.group.gateway;

import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.group.model.QuestionnaireModel;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface QuestionnaireModelRepository {
    /**
     * Find ids for all questionnaire of a group
     *
     * @param groupId group id
     * @return {@link List<String>} all questionnaire ids for a group
     */
    List<String> findAllIds(String groupId);

    /**
     * Find data structure for a questionnaire
     *
     * @param questionnaireId questionnaire id
     * @return questionnaire data for a group
     */
    Optional<ObjectNode> findQuestionnaireData(String questionnaireId);

    /**
     * Check if questionnaire exists
     *
     * @param questionnaireId questionnaire id
     * @return true if exists, false otherwise
     */
    boolean exists(String questionnaireId);

    /**
     * Create a questionnaire
     *
     * @param questionnaireData questionnaire to create
     */
    void create(QuestionnaireModel questionnaireData);

    /**
     * Update a questionnaire
     *
     * @param questionnaireData questionnaire to update
     */
    void update(QuestionnaireModel questionnaireData);

    /**
     * Count how many of the given questionnaire ids exist in database
     *
     * @param questionnaireIds questionnaire ids to check
     * @return number of existing questionnaires
     */
    Long countExistingQuestionnaires(Set<String> questionnaireIds);

    /**
     * Delete questionnaires from the given set that are no longer linked to any group
     *
     * @param questionnaireIds candidate questionnaire ids
     */
    void deleteOrphanedQuestionnaires(Set<String> questionnaireIds);

    /**
     * Find data structure for all questionnaire of a group
     *
     * @param groupId group id
     * @return {@link List<String>} all questionnaire values for a group
     */
    List<ObjectNode> findAllQuestionnaireDatas(String groupId);
}
