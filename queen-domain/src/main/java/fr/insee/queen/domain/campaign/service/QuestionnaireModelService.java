package fr.insee.queen.domain.campaign.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.campaign.model.QuestionnaireModel;

import java.util.List;

public interface QuestionnaireModelService {
    List<String> getQuestionnaireIds(String campaignId);

    ObjectNode getQuestionnaireData(String id);

    void createQuestionnaire(QuestionnaireModel qm);

    void updateQuestionnaire(QuestionnaireModel qm);

    List<ObjectNode> getQuestionnaireDatas(String campaignId);
}
