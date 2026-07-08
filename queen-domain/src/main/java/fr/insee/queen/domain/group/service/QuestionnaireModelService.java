package fr.insee.queen.domain.group.service;

import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.group.model.QuestionnaireModel;

import java.util.List;

public interface QuestionnaireModelService {
    List<String> getQuestionnaireIds(String groupId);

    ObjectNode getQuestionnaireData(String id);

    void createQuestionnaire(QuestionnaireModel qm);

    void updateQuestionnaire(QuestionnaireModel qm);

    List<ObjectNode> getQuestionnaireDatas(String groupId);
}
