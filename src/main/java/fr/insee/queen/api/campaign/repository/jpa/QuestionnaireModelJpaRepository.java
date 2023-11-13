package fr.insee.queen.api.campaign.repository.jpa;

import fr.insee.queen.api.campaign.repository.entity.QuestionnaireModelDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * CampaignRepository is the repository using to access to Campaign table in DB
 *
 * @author Claudel Benjamin
 */
@Repository
public interface QuestionnaireModelJpaRepository extends JpaRepository<QuestionnaireModelDB, String> {

    /**
     * This method retrieve all questionnaires Id for a specific campaign
     *
     * @param campaignId id of the campaign
     * @return all questionnaire ids for a specific campaign
     */
    @Query(value = "select qm.id from QuestionnaireModelDB qm where qm.campaign.id=:campaignId")
    List<String> findAllIdByCampaignId(String campaignId);

    @Query(value = "select qm.value from QuestionnaireModelDB qm where qm.campaign.id=:campaignId")
    List<String> findAllValueByCampaignId(String campaignId);

    /**
     * This method retrieve questionnaire model for a specific id
     *
     * @param questionnaireId id of the questionnaire
     * @return {@link String}
     */
    @Query(value = "select qm.value from QuestionnaireModelDB qm where qm.id=:questionnaireId")
    Optional<String> findQuestionnaireValue(String questionnaireId);

    @Query(value = "select count(*) from questionnaire_model qm where qm.id in :questionnaireIds and (qm.campaign_id is NULL or qm.campaign_id=:campaignId)", nativeQuery = true)
    Long countValidQuestionnairesByIds(String campaignId, Set<String> questionnaireIds);

    Set<QuestionnaireModelDB> findByIdIn(Set<String> questionnaireIds);

    void deleteAllByCampaignId(String campaignId);
}
