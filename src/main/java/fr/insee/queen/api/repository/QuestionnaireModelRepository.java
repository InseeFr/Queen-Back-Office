package fr.insee.queen.api.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.insee.queen.api.domain.QuestionnaireModel;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireIdDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelDto;

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
	* This method retrieve questionnaire model for a specific campaign
	* 
	* @param id id of the campaign
	* @return {@link QuestionnaireModelDto}
	*/
	Optional<QuestionnaireModelDto> findDtoByCampaignId(String id);

	/**
	* This method retrieve questionnaire Id for a specific campaign
	* 
	* @param id id of the campaign
	* @return {@link QuestionnaireModelDto}
	*/
	Optional<QuestionnaireIdDto> findIdByCampaignId(String id);
	
	/**
	* This method retrieve all questionnaires Id for a specific campaign
	* 
	* @param id id of the campaign
	* @return {@link QuestionnaireModelDto}
	*/
	@Query(value = "select qm.id  from questionnaire_model qm where qm.campaign_id = ?1", nativeQuery = true)
	List<String> findAllIdByCampaignId(String id);
	
	/**
	* This method retrieve questionnaire model for a specific id
	* 
	* @param id id of the campaign
	* @return {@link QuestionnaireModelDto}
	*/
	Optional<QuestionnaireModelDto> findDtoById(String id);

	List<QuestionnaireModel> findByCampaignId(String id);
	
}
