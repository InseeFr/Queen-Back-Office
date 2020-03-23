package fr.insee.queen.queen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.insee.queen.queen.domain.QuestionnaireModel;
import fr.insee.queen.queen.dto.questionnairemodel.QuestionnaireModelDto;

/**
* OperationRepository is the repository using to access to Operation table in DB
* 
* @author Claudel Benjamin
* 
*/
public interface QuestionnaireModelRepository extends JpaRepository<QuestionnaireModel, String> {
	/**
	* This method retrieve questionnaire model for a specific operation
	* 
	* @param id id of the operation
	* @return {@link QuestionnaireModelDto}
	*/
	@Query("SELECT qm " 
			+ "FROM QuestionnaireModel qm "
			+ "INNER JOIN Operation op "
			+ "ON op.questionnaireModel = qm.id "
			+ "WHERE op.id=?1 ")
	QuestionnaireModelDto findDtoByOperationId(String id);

}
