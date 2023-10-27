package fr.insee.queen.api.service.questionnaire;

import fr.insee.queen.api.dto.input.QuestionnaireModelInputDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelIdDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelValueDto;

import java.util.List;

public interface QuestionnaireModelService {
	List<String> findAllQuestionnaireIdDtoByCampaignId(String campaignId);
	QuestionnaireModelValueDto getQuestionnaireModelDto(String id);
	void createQuestionnaire(QuestionnaireModelInputDto qm);
	void createQuestionnaire(QuestionnaireModelInputDto qm, String campaignId);
	void updateQuestionnaire(QuestionnaireModelInputDto qm, String campaignId);
	List<QuestionnaireModelIdDto> getQuestionnaireIds(String campaignId);
	List<QuestionnaireModelValueDto> getQuestionnaireValues(String campaignId);
}
