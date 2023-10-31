package fr.insee.queen.api.service.dummy;

import fr.insee.queen.api.domain.QuestionnaireModelData;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelIdDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelValueDto;
import fr.insee.queen.api.service.questionnaire.QuestionnaireModelService;

import java.util.ArrayList;
import java.util.List;

public class QuestionnaireModelFakeService implements QuestionnaireModelService {
    @Override
    public List<String> findAllQuestionnaireIdDtoByCampaignId(String campaignId) {
        return new ArrayList<>();
    }

    @Override
    public QuestionnaireModelValueDto getQuestionnaireModelDto(String id) {
        return null;
    }

    @Override
    public void createQuestionnaire(QuestionnaireModelData qm) {

    }

    @Override
    public void updateQuestionnaire(QuestionnaireModelData qm) {

    }

    @Override
    public List<QuestionnaireModelIdDto> getQuestionnaireIds(String campaignId) {
        return null;
    }

    @Override
    public List<QuestionnaireModelValueDto> getQuestionnaireValues(String campaignId) {
        return null;
    }
}
