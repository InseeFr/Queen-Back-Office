package fr.insee.queen.domain.group.service.dummy;

import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.group.service.QuestionnaireModelService;
import fr.insee.queen.domain.group.model.QuestionnaireModel;
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
    public List<String> getQuestionnaireIds(String groupId) {
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
    public List<ObjectNode> getQuestionnaireDatas(String groupId) {
        return null;
    }
}
