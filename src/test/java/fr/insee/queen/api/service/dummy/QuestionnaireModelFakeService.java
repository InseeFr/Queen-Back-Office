package fr.insee.queen.api.service.dummy;

import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelData;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelIdDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelValueDto;
import fr.insee.queen.api.service.questionnaire.QuestionnaireModelService;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class QuestionnaireModelFakeService implements QuestionnaireModelService {
    @Getter
    private boolean updated = false;
    @Getter
    private boolean created = false;

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
        created = true;
    }

    @Override
    public void updateQuestionnaire(QuestionnaireModelData qm) {
        updated = true;
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
