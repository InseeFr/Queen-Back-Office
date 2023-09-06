package fr.insee.queen.api.repository;

import fr.insee.queen.api.domain.QuestionnaireModel;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelCampaignDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelDto;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
* CampaignRepository is the repository using to access to Campaign table in DB
* 
* @author Claudel Benjamin
* 
*/
@Transactional
@Repository
public interface QuestionnaireModelRepository extends JpaRepository<QuestionnaireModel, String> {
	
	/**
	* This method retrieve all questionnaires Id for a specific campaign
	* 
	* @param campaignId id of the campaign
	* @return all questionnaire ids for a specific campaign
	*/
	@Query(value = "select qm.id from QuestionnaireModel qm where qm.campaign.id=:campaignId")
	List<String> findAllIdByCampaignId(String campaignId);
	
	/**
	* This method retrieve questionnaire model for a specific id
	* 
	* @param questionnaireId id of the questionnaire
	* @return {@link QuestionnaireModelDto}
	*/
	Optional<QuestionnaireModelDto> findQuestionnaireModelById(String questionnaireId);

	List<QuestionnaireModel> findByCampaignId(String questionnaireId);

	@Query("""
		select new fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelCampaignDto(
		    qm.id,
		    new fr.insee.queen.api.dto.campaign.CampaignDto(
		        qm.campaign.id,
		        qm.campaign.label
		    )
		) from QuestionnaireModel qm where qm.id=:questionnaireId""")
	Optional<QuestionnaireModelCampaignDto> findQuestionnaireModelWithCampaignById(String questionnaireId);
}
