package fr.insee.queen.domain.campaign.infrastructure.dummy;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.campaign.gateway.QuestionnaireModelRepository;
import fr.insee.queen.domain.campaign.model.QuestionnaireModel;
import lombok.Setter;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class QuestionnaireModelFakeRepository implements QuestionnaireModelRepository {

    @Setter
    private boolean questionnaireExists = true;

    @Override
    public List<String> findAllIds(String campaignId) {
        return null;
    }

    @Override
    public Optional<ObjectNode> findQuestionnaireData(String questionnaireId) {
        return Optional.empty();
    }

    @Override
    public boolean exists(String questionnaireId) {
        return questionnaireExists;
    }

    @Override
    public void create(QuestionnaireModel questionnaireData) {
        // not used at this moment
    }

    @Override
    public void update(QuestionnaireModel questionnaireData) {
        // not used at this moment
    }

    @Override
    public Long countValidQuestionnaires(String campaignId, Set<String> questionnaireIds) {
        return null;
    }

    @Override
    public void deleteAllFromCampaign(String campaignId) {
        // not used at this moment
    }

    @Override
    public List<ObjectNode> findAllQuestionnaireDatas(String campaignId) {
        return null;
    }
}
