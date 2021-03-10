package fr.insee.queen.api.repository;

import java.util.List;
import java.util.Optional;

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
public interface QuestionnaireModelRepository extends JpaRepository<QuestionnaireModel, String> {
	/**
	* This method retrieve questionnaire model for a specific campaign
	* 
	* @param id id of the campaign
	* @return {@link QuestionnaireModelDto}
	*/
//	@Query("SELECT qm " 
//			+ "FROM QuestionnaireModel qm "
//			+ "INNER JOIN Campaign op "
//			+ "ON op.questionnaireModel = qm.id "
//			+ "WHERE op.id=?1 ")
//	QuestionnaireModelDto findQuestionnaireModelDtoByCampaignId(String id);
//
//	/**
//	* This method retrieve questionnaire Id for a specific campaign
//	* 
//	* @param id id of the campaign
//	* @return {@link QuestionnaireModelDto}
//	*/
//	@Query(value="SELECT qm.id as questionnaireId " 
//			+ "FROM questionnaire_model qm "
//			+ "INNER JOIN campaign op "
//			+ "ON op.questionnaire_model_id = qm.id "
//			+ "WHERE op.id=?1 ", nativeQuery=true)
//	QuestionnaireIdDto findQuestionnaireIdDtoByCampaignId(String id);
//	
	
	Optional<QuestionnaireModelDto> findDtoById(String id);

}
