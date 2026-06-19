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
     * Count valid questionnaires for a group
     * This is typically used to check if questionnaires can be associated on a group.
     * A valid questionnaire is a questionnaire already linked to the group or a questionnaire with no group linked
     *
     * @param groupId group id
     * @param questionnaireIds questionnaire ids we want to check for the group
     * @return number of valid questionnaires
     */
    Long countValidQuestionnaires(String groupId, Set<String> questionnaireIds);

    /**
     * Delete all questionnaires in a group
     * @param groupId group id
     */
    void deleteAllFromGroup(String groupId);

    /**
     * Find data structure for all questionnaire of a group
     *
     * @param groupId group id
     * @return {@link List<String>} all questionnaire values for a group
     */
    List<ObjectNode> findAllQuestionnaireDatas(String groupId);
}
