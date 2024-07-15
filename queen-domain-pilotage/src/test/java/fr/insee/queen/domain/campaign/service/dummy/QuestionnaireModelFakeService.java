package fr.insee.queen.domain.campaign.service.dummy;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.campaign.model.QuestionnaireModel;
import fr.insee.queen.domain.campaign.service.QuestionnaireModelService;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class QuestionnaireModelFakeService implements QuestionnaireModelService {
    @Getter
    private boolean updated = false;
    @Getter
    private boolean created = false;
    @Setter
    private String campaignIdNotFound;

    @Override
    public List<String> getQuestionnaireIds(String campaignId) {
        if(campaignIdNotFound != null && campaignIdNotFound.equals(campaignId)) {
            throw new EntityNotFoundException("Entity not found");
        }
        return new ArrayList<>();
    }

    @Override
    public ObjectNode getQuestionnaireData(String id) {
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
    public List<ObjectNode> getQuestionnaireDatas(String campaignId) {
        return null;
    }
}
