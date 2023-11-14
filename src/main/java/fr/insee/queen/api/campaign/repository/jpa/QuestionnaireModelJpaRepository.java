package fr.insee.queen.api.campaign.repository.jpa;

import fr.insee.queen.api.campaign.repository.entity.QuestionnaireModelDB;
import org.springframework.data.jpa.repository.JpaRepository;
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
     * Find ids for all questionnaire linked to a campaign
     *
     * @param campaignId campaign id
     * @return all questionnaire ids for a campaign
     */
    @Query(value = "select qm.id from QuestionnaireModelDB qm where qm.campaign.id=:campaignId")
    List<String> findAllIdByCampaignId(String campaignId);

    /**
     * Find data structure for all questionnaire linked to a campaign
     *
     * @param campaignId campaign id
     * @return all questionnaire values for a campaign
     */
    @Query(value = "select qm.value from QuestionnaireModelDB qm where qm.campaign.id=:campaignId")
    List<String> findAllValueByCampaignId(String campaignId);

    /**
     * Find data structure for a questionnaire
     *
     * @param questionnaireId questionnaire id
     * @return questionnaire data for a campaign
     */
    @Query(value = "select qm.value from QuestionnaireModelDB qm where qm.id=:questionnaireId")
    Optional<String> findQuestionnaireData(String questionnaireId);

    /**
     * Count valid questionnaires for a campaign
     * This is typically used to check if questionnaires can be associated on a campaign.
     * A valid questionnaire is a questionnaire already linked to the campaign or a questionnaire with no campaign linked
     *
     * @param campaignId campaign id
     * @param questionnaireIds questionnaire ids we want to check for the campaign
     * @return number of valid questionnaires
     */
    @Query(value = "select count(*) from questionnaire_model qm where qm.id in :questionnaireIds and (qm.campaign_id is NULL or qm.campaign_id=:campaignId)", nativeQuery = true)
    Long countValidQuestionnairesByIds(String campaignId, Set<String> questionnaireIds);

    /**
     * Find questionnaires by ids
     *
     * @param questionnaireIds questionnaire ids
     * @return {@link QuestionnaireModelDB}
     */
    Set<QuestionnaireModelDB> findByIdIn(Set<String> questionnaireIds);

    /**
     * Delete all questionnaire by campaign id
     *
     * @param campaignId campaign id
     */
    void deleteAllByCampaignId(String campaignId);
}
