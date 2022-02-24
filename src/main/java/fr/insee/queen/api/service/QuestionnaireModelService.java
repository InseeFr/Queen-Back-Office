package fr.insee.queen.api.service;

import java.util.List;
import java.util.Optional;

import fr.insee.queen.api.domain.QuestionnaireModel;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireIdDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelCreateDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelDto;

public interface QuestionnaireModelService extends BaseService<QuestionnaireModel, String> {

	Optional<QuestionnaireModel> findById(String id);

	Optional<QuestionnaireModelDto> findQuestionnaireModelDtoByCampaignId(String id);

	Optional<QuestionnaireIdDto> findQuestionnaireIdDtoByCampaignId(String id);
	
	List<String> findAllQuestionnaireIdDtoByCampaignId(String id);

	Optional<QuestionnaireModelDto> findDtoById(String id);
	
	List<QuestionnaireModel> findQuestionnaireModelByCampaignId(String id);
	
	void save(QuestionnaireModel qm);
	
	void createQuestionnaire(QuestionnaireModelCreateDto qm);    
}
