package fr.insee.queen.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.insee.queen.api.domain.QuestionnaireModel;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelDto;

/**
* CampaignRepository is the repository using to access to Campaign table in DB
* 
* @author Claudel Benjamin
* 
*/
public interface QuestionnaireModelRepository extends JpaRepository<QuestionnaireModel, String> {
	/**
	* This method retrieve questionnaire model for a specific campaign
	* 
	* @param id id of the campaign
	* @return {@link QuestionnaireModelDto}
	*/
	@Query("SELECT qm " 
			+ "FROM QuestionnaireModel qm "
			+ "INNER JOIN Campaign op "
			+ "ON op.questionnaireModel = qm.id "
			+ "WHERE op.id=?1 ")
	QuestionnaireModelDto findDtoByCampaignId(String id);

}
