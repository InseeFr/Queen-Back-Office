package fr.insee.queen.api.service.questionnaire;

import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelData;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelIdDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelValueDto;

import java.util.List;

public interface QuestionnaireModelService {
	List<String> findAllQuestionnaireIdDtoByCampaignId(String campaignId);
	QuestionnaireModelValueDto getQuestionnaireModelDto(String id);
	void createQuestionnaire(QuestionnaireModelData qm);
	void updateQuestionnaire(QuestionnaireModelData qm);
	List<QuestionnaireModelIdDto> getQuestionnaireIds(String campaignId);
	List<QuestionnaireModelValueDto> getQuestionnaireValues(String campaignId);
}
