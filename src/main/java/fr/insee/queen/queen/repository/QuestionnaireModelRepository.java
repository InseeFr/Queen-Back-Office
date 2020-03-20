package fr.insee.queen.queen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.insee.queen.queen.domain.QuestionnaireModel;
import fr.insee.queen.queen.dto.operation.QuestionnaireModelDto;

public interface QuestionnaireModelRepository extends JpaRepository<QuestionnaireModel, String> {
	
	@Query("SELECT qm " 
			+ "FROM QuestionnaireModel qm "
			+ "INNER JOIN Operation op "
			+ "ON op.questionnaireModel = qm.id "
			+ "WHERE op.id=?1 ")
	QuestionnaireModelDto findDtoByOperationId(String id);

}
