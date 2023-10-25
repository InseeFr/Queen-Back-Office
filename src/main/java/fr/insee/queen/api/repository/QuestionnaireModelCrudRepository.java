package fr.insee.queen.api.repository;

import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelCampaignDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelValueDto;
import fr.insee.queen.api.entity.QuestionnaireModelDB;
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
* 
*/
@Repository
public interface QuestionnaireModelCrudRepository extends JpaRepository<QuestionnaireModelDB, String> {
	
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
	* @return {@link QuestionnaireModelValueDto}
	*/
	Optional<QuestionnaireModelValueDto> findQuestionnaireModelById(String questionnaireId);

	List<QuestionnaireModelDB> findByCampaignId(String questionnaireId);

	@Query(value = "select count(*) from questionnaire_model qm where qm.id in :questionnaireIds and (qm.campaign_id is NULL or qm.campaign_id=:campaignId)", nativeQuery = true)
	Long countValidQuestionnairesByIds(String campaignId, List<String> questionnaireIds);

	@Query("""
		select new fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelCampaignDto(
		    qm.id,
		    new fr.insee.queen.api.dto.campaign.CampaignDto(
		        qm.campaign.id,
		        qm.campaign.label
		    )
		) from QuestionnaireModelDB qm where qm.id=:questionnaireId""")
	Optional<QuestionnaireModelCampaignDto> findQuestionnaireModelWithCampaignById(String questionnaireId);

	Set<QuestionnaireModelDB> findByIdIn(List<String> questionnaireIds);

	void deleteAllByCampaignId(String campaignId);
}
