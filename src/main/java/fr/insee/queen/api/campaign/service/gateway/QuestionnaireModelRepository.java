package fr.insee.queen.api.campaign.service.gateway;

import fr.insee.queen.api.campaign.service.model.QuestionnaireModel;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface QuestionnaireModelRepository {
    /**
     * Find ids for all questionnaire of a campaign
     *
     * @param campaignId campaign id
     * @return {@link List<String>} all questionnaire ids for a campaign
     */
    List<String> findAllIds(String campaignId);

    /**
     * Find data structure for a questionnaire
     *
     * @param questionnaireId questionnaire id
     * @return questionnaire data for a campaign
     */
    Optional<String> findQuestionnaireData(String questionnaireId);

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
     * Count valid questionnaires for a campaign
     * This is typically used to check if questionnaires can be associated on a campaign.
     * A valid questionnaire is a questionnaire already linked to the campaign or a questionnaire with no campaign linked
     *
     * @param campaignId campaign id
     * @param questionnaireIds questionnaire ids we want to check for the campaign
     * @return number of valid questionnaires
     */
    Long countValidQuestionnaires(String campaignId, Set<String> questionnaireIds);

    /**
     * Delete all questionnaires in a campaign
     * @param campaignId campaign id
     */
    void deleteAllFromCampaign(String campaignId);

    /**
     * Find data structure for all questionnaire of a campaign
     *
     * @param campaignId campaign id
     * @return {@link List<String>} all questionnaire values for a campaign
     */
    List<String> findAllQuestionnaireDatas(String campaignId);
}
