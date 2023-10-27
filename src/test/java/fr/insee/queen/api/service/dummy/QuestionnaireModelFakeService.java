package fr.insee.queen.api.service.dummy;

import fr.insee.queen.api.dto.input.QuestionnaireModelInputDto;
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
    public void createQuestionnaire(QuestionnaireModelInputDto qm) {

    }

    @Override
    public void createQuestionnaire(QuestionnaireModelInputDto qm, String campaignId) {

    }

    @Override
    public void updateQuestionnaire(QuestionnaireModelInputDto qm, String campaignId) {

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
