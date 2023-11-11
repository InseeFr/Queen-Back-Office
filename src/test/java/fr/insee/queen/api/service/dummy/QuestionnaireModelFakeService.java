package fr.insee.queen.api.service.dummy;

import fr.insee.queen.api.campaign.service.QuestionnaireModelService;
import fr.insee.queen.api.campaign.service.model.QuestionnaireModel;
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
    public List<String> getQuestionnaireIds(String campaignId) {
        return new ArrayList<>();
    }

    @Override
    public String getQuestionnaireData(String id) {
        return null;
    }

    @Override
    public void createQuestionnaire(QuestionnaireModel qm) {
        created = true;
    }

    @Override
    public void updateQuestionnaire(QuestionnaireModel qm) {
        updated = true;
    }

    @Override
    public List<String> getQuestionnaireDatas(String campaignId) {
        return null;
    }
}
